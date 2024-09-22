package io.github.dueris.originspaper.power.type;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class ClimbingPower extends PowerType {
	public static LinkedList<Player> active_climbing = new LinkedList<>();
	public LinkedList<org.bukkit.entity.Player> allowedToClimb = new LinkedList<>();

	public ClimbingPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("climbing"), PowerType.getFactory().getSerializableData());
	}

	@Override
	public void tick(@NotNull Player p) {
		if (!p.level().getBlockStates(p.getBoundingBox().inflate(0.1, 0, 0.1)).filter(state -> state.getBukkitMaterial().isCollidable()).toList().isEmpty()) {
			if (isActive(p) && allowedToClimb.contains((org.bukkit.entity.Player) p.getBukkitEntity())) {
				active_climbing.add(p);
				p.getBukkitEntity().addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 3, 2, false, false, false));
			} else active_climbing.remove(p);
		}
	}

	@EventHandler
	public void jump(@NotNull PlayerJumpEvent e) {
		if (getPlayers().contains(((CraftPlayer) e.getPlayer()).getHandle())) {
			org.bukkit.entity.Player p = e.getPlayer();
			allowedToClimb.add(p);
			new BukkitRunnable() {
				@Override
				public void run() {
					if (p.isOnGround()) {
						allowedToClimb.remove(p);
						cancel();
					}
				}
			}.runTaskTimer(OriginsPaper.getPlugin(), 0, 1);
		}
	}
}
