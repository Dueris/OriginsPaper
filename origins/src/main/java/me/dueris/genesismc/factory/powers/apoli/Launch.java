package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.KeybindTriggerEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.KeybindingUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.core.particles.ParticleTypes;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.apoli.FireProjectile.in_continuous;

public class Launch extends CraftPower implements Listener {

	@EventHandler
	public void inContinuousFix(KeybindTriggerEvent e) {
		Player p = e.getPlayer();
		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			if (getPlayersWithPower().contains(p)) {
				for (Power power : OriginPlayerAccessor.getPowers(p, getType(), layer)) {
					if (KeybindingUtils.isKeyActive(power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active"), p)) {
						in_continuous.putIfAbsent(p, new ArrayList<>());
					}
				}
			}
		}
	}

	@EventHandler
	public void keybindToggle(KeybindTriggerEvent e) {
		Player p = e.getPlayer();
		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			for (Power power : OriginPlayerAccessor.getPowers(p, getType(), layer)) {
				if (getPlayersWithPower().contains(p)) {
					if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
						if (!Cooldown.isInCooldown(p, power)) {
							if (KeybindingUtils.isKeyActive(power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active"), p)) {
								String key = power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active");
								final int[] times = {-1};
								new BukkitRunnable() {
									@Override
									public void run() {
										int cooldown = power.getNumberOrDefault("cooldown", 1).getInt();
										if (times[0] >= 0) {
											/* Launch power doesnt execute continuously */
											if (!in_continuous.get(p).contains(key)) {
												Cooldown.addCooldown(p, cooldown, power);
												setActive(p, power.getTag(), false);
												this.cancel();
												return;
											}
										}
										int speed = Integer.parseInt(power.getStringOrDefault("speed", null)); // used as string so that upon parsing the int it throws if not found
										Cooldown.addCooldown(p, cooldown, power);
										setActive(p, power.getTag(), true);
										p.setVelocity(p.getVelocity().setY(p.getVelocity().getY() + speed));
										((CraftWorld) p.getWorld()).getHandle().sendParticles(ParticleTypes.CLOUD, p.getX(), p.getY(), p.getZ(), 8, ((CraftPlayer) p).getHandle().getRandom().nextGaussian(), 0.0D, ((CraftPlayer) p).getHandle().getRandom().nextGaussian(), 0.5);
										if (power.isPresent("sound")) {
											p.getWorld().playSound(p, Utils.parseSound(power.getString("sound")), 0.5F, 0.4F / (((CraftPlayer) p).getHandle().getRandom().nextFloat() * 0.4F + 0.8F));
										}
										setActive(p, power.getTag(), true);
										times[0]++;
									}
								}.runTaskTimer(GenesisMC.getPlugin(), 1L, 1L);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public String getType() {
		return "apoli:launch";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return launch_into_air;
	}
}
