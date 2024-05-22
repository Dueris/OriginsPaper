package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.calio.data.types.RequiredInstance;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.util.Util;
import net.minecraft.world.damagesource.DamageType;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DamageOverTime extends PowerType {
	private final int interval;
	private final float damage;
	private final float damageEasy;
	private final String damageType;

	public DamageOverTime(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, int interval, float damage, @Nullable float damageEasy, String damageType) {
		super(name, description, hidden, condition, loading_priority);
		this.interval = interval;
		this.damage = damage;
		this.damageEasy = Objects.isNull(damageEasy) ? damage : damageEasy;
		this.damageType = damageType;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("damage_over_time"))
			.add("interval", int.class, 20)
			.add("damage", float.class, new RequiredInstance())
			.add("damage_easy", float.class, new OptionalInstance())
			.add("damage_type", String.class, "apoli:damage_over_time");
	}

	@EventHandler
	public void erk(PlayerDeathEvent e) {
		if (e.getDeathMessage().equals("death.attack.hurt_by_water")) {
			if (e.getPlayer().getName().equals("Optima1")) {
				e.setDeathMessage("Optima1 got too thirsty");
			} else {
				e.setDeathMessage("{p} took a bath for too long."
					.replace("{p}", e.getPlayer().getName()));
			}
		} else if (e.getDeathMessage().equals("death.attack.no_water_for_gills")) {
			e.setDeathMessage("{p} didn't manage to keep wet"
				.replace("{p}", e.getPlayer().getName()));
		} else if (e.getDeathMessage().equals("death.attack.genericDamageOverTime")) {
			e.setDeathMessage("{p} died to a damage over time effect"
				.replace("{p}", e.getPlayer().getName()));
		} else if (e.getDeathMessage().equals("death.attack.wardenSonicBoom")) {
			e.setDeathMessage("{p} was imploded by a sonic boom"
				.replace("{p}", e.getPlayer().getName()));
		}
	}

	@Override
	public void tick(Player p) {
		if (Bukkit.getServer().getCurrentTick() % interval == 0) {
			float damageVal = p.getWorld().getDifficulty().equals(Difficulty.EASY) ? damageEasy : damage;

			if (isActive(p)) {
				if (p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)) {
					NamespacedKey key = NamespacedKey.fromString(damageType);
					DamageType dmgType = Util.DAMAGE_REGISTRY.get(CraftNamespacedKey.toMinecraft(key));
					((CraftPlayer) p).getHandle().hurt(Util.getDamageSource(dmgType), damageVal);
				}
			}
		}
	}

	public float getDamage() {
		return damage;
	}

	public float getDamageEasy() {
		return damageEasy;
	}

	public int getInterval() {
		return interval;
	}

	public String getDamageType() {
		return damageType;
	}
}