package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.RequiredInstance;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ModifyFallingPower extends PowerType implements Listener {
	private final float velocity;
	private final boolean takeFallDamage;

	public ModifyFallingPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, float velocity, boolean takeFallDamage) {
		super(name, description, hidden, condition, loading_priority);
		this.velocity = velocity;
		this.takeFallDamage = takeFallDamage;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("modify_falling"))
			.add("velocity", float.class, new RequiredInstance())
			.add("take_fall_damage", boolean.class, true);
	}

	@EventHandler
	public void runE(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (getPlayers().contains(p)) {
			if (e.getTo().getY() == e.getFrom().getY()) return;
			@NotNull Vector velocityVal = p.getVelocity();
			if (isActive(p)) {
				if (velocity < 0) {
					velocityVal.setY(velocity);
					p.setVelocity(velocityVal);
				} else {
					p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 5, 1, false, false, false));
				}
			}
		}
	}

	@EventHandler
	public void runR(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player p) {
			if (getPlayers().contains(p)) {
				if (isActive(p)) {
					if (!takeFallDamage) {
						if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
							e.setDamage(0);
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}

	public float getVelocity() {
		return velocity;
	}

	public boolean takeFallDamage() {
		return takeFallDamage;
	}
}
