package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.ArrayList;
import java.util.Random;

public class WaterBreathe extends CraftPower implements Listener {
	private static final ArrayList<Player> genesisExecuting = new ArrayList<>();
	public static ArrayList<Player> outofAIR = new ArrayList<>();

	public static boolean isInBreathableWater(Player player) {
		Block block = player.getEyeLocation().getBlock();
		Material material = block.getType();
		if (block.getType().equals(Material.WATER)) {
			return true;
		} else return player.isInWater() && !material.equals(Material.AIR);
	}

	public static void start() {
		WaterBreathe waterBreathe = new WaterBreathe();
		Bukkit.getScheduler().runTaskTimer(GenesisMC.getPlugin(), () -> {
			waterBreathe.run();
		}, 0, 1);
	}


	@EventHandler
	public void interuptMinecraft(EntityAirChangeEvent e) {
		if (e.getEntity() instanceof Player player) {
			if (water_breathing.contains(player)) {
				if (!genesisExecuting.contains(player)) {
					e.setCancelled(true);
					e.setAmount(0);
				}
			}
		}
	}

	@EventHandler
	public void drinkWater(PlayerItemConsumeEvent e) {
		if (water_breathing.contains(e.getPlayer()) && e.getItem().getType().equals(Material.POTION)) {
			genesisExecuting.add(e.getPlayer());
			e.getPlayer().setRemainingAir(e.getPlayer().getRemainingAir() + 60);
			genesisExecuting.remove(e.getPlayer());
		}
	}

	@Override
	public void run(Player p) {
		if (!getPowerArray().contains(p)) return;
		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
			for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
				if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
					setActive(p, power.getTag(), true);
					if (water_breathing.contains(p)) {
						genesisExecuting.add(p);
						int addonAir = 4;
						int lowestAir = -10;
						int tickDownAir = 1;
						boolean shouldDamage = true;
						if (((CraftPlayer) (p)).getHandle().hasEffect(MobEffects.WATER_BREATHING)
							|| p.isInRain()
							|| ((CraftPlayer) p).getHandle().hasEffect(MobEffects.CONDUIT_POWER)
							|| p.getGameMode().equals(GameMode.SPECTATOR)
							|| p.getGameMode().equals(GameMode.CREATIVE)
						) {
							addonAir = 0;
							tickDownAir = 0;
							shouldDamage = false;
						}
						if (isInBreathableWater(p)) {
							if (p.getRemainingAir() < 290) {
								p.setRemainingAir(p.getRemainingAir() + addonAir);
							} else {
								p.setRemainingAir(310);
							}
							outofAIR.remove(p);
						} else {
							if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR))
								return;
							int remainingAir = p.getRemainingAir();
							if (remainingAir <= 5) {
								p.setRemainingAir(lowestAir);
								outofAIR.add(p);
							} else {
								p.setRemainingAir(remainingAir - tickDownAir);
								outofAIR.remove(p);
							}
						}
						if (!shouldDamage) {
							outofAIR.remove(p);
						} else if (outofAIR.contains(p)) {
							if (p.getRemainingAir() > 20) {
								outofAIR.remove(p);
							}
						}
						genesisExecuting.remove(p);
					}
				} else {
					setActive(p, power.getTag(), false);
				}
			}

		}
	}

	private void spawnBubbleLooseParticle(Location location) {
		Random r = new Random();
		location.getWorld().spawnParticle(Particle.WATER_BUBBLE, location, r.nextInt(7));
	}

	@Override
	public String getPowerFile() {
		return "apoli:water_breathing";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return water_breathing;
	}

	public void run() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (outofAIR.contains(p)) {
				int remainingAir = p.getRemainingAir();
				if (remainingAir <= 5) {
					int finalDmg = 3;
					if (p.getInventory().getHelmet() != null) {
						if (p.getInventory().getHelmet().getType() == Material.TURTLE_HELMET) {
							finalDmg = 2;
						} else if (p.getInventory().getHelmet().containsEnchantment(Enchantment.OXYGEN)) {
							finalDmg = 2;
						}
					}
					DamageType dmgType = Utils.DAMAGE_REGISTRY.get(new ResourceLocation("origins", "no_water_for_gills"));
					((CraftPlayer) p).getHandle().hurt(Utils.getDamageSource(dmgType), finalDmg);
				}
			}
		}
	}
}
