package io.github.dueris.originspaper.power;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FireImmunityPower extends PowerType {
	private static final List<EntityDamageEvent.DamageCause> POSSIBLE_CAUSES = List.of(EntityDamageEvent.DamageCause.FIRE, EntityDamageEvent.DamageCause.FIRE_TICK, EntityDamageEvent.DamageCause.LAVA, EntityDamageEvent.DamageCause.HOT_FLOOR);

	public FireImmunityPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("fire_immunity"));
	}

	@EventHandler
	public void onDamage(@NotNull EntityDamageEvent e) {
		if (e.getEntity().isDead()) return;
		if (e.getEntity() instanceof Player player && POSSIBLE_CAUSES.contains(e.getCause())) {
			net.minecraft.world.entity.player.Player nms = ((CraftPlayer) player).getHandle();
			if (getPlayers().contains(nms) && isActive(nms)) {
				e.setCancelled(true);
				e.setDamage(0);
			}
		}
	}
}
