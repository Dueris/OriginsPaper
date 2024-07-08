package me.dueris.originspaper.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.powers.holder.PowerType;
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
	private final Float velocity;
	private final boolean takeFallDamage;

	public ModifyFallingPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, float velocity, boolean takeFallDamage) {
		super(name, description, hidden, condition, loading_priority);
		this.velocity = velocity;
		this.takeFallDamage = takeFallDamage;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("modify_falling"))
			.add("velocity", float.class, 0F)
			.add("take_fall_damage", boolean.class, true);
	}

	@EventHandler
	public void runE(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (getPlayers().contains(p)) {
			if (e.getTo().getY() == e.getFrom().getY()) return;
			@NotNull Vector velocityVal = p.getVelocity();
			if (isActive(p)) {
				if (velocityVal.getY() > 0D) return;
				if (velocity != null && velocity != 0F) {
					if (velocity < 0.08) {
						// This way is a lot smoother and also updates the client preventing weird glitches
						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 5, 1, false, false, false));
					} else {
						Vector veloc = e.getPlayer().getVelocity();
						veloc.setY(veloc.getY() * (1 + velocity));
						e.getPlayer().setVelocity(veloc);
					}
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
