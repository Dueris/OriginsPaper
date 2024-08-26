package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.power.ElytraFlightPower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class FallFlyingCondition {

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("fall_flying"),
			SerializableData.serializableData(),
			(data, entity) -> {
				return entity instanceof LivingEntity && (((LivingEntity) entity).isFallFlying() ||
					PowerHolderComponent.doesHaveConditionedPower(entity.getBukkitEntity(), ElytraFlightPower.class, (p) -> p.getGlidingPlayers().contains(entity.getBukkitEntity().getUniqueId())));
			}
		);
	}
}
