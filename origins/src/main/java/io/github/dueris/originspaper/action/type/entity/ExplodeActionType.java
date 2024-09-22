package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ExplodeActionType {

	public static void action(@NotNull Entity entity, float power, Explosion.BlockInteraction destructionType, @Nullable Predicate<BlockInWorld> indestructibleCondition, float indestructibleResistance, @Nullable Predicate<BlockInWorld> destructibleCondition, boolean damageSelf, boolean createFire) {

		Level world = entity.level();
		if (world.isClientSide) {
			return;
		}

		if (destructibleCondition != null) {
			indestructibleCondition = Util.combineOr(destructibleCondition.negate(), indestructibleCondition);
		}

		Vec3 pos = entity.position();
		Util.createExplosion(
			world,
			damageSelf ? null : entity,
			Explosion.getDefaultDamageSource(world, entity),
			pos.x(),
			pos.y(),
			pos.z(),
			power,
			createFire,
			destructionType == null ? Explosion.BlockInteraction.DESTROY : destructionType,
			Util.getExplosionBehavior(world, indestructibleResistance, indestructibleCondition)
		);

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("explode"),
			new SerializableData()
				.add("power", SerializableDataTypes.FLOAT)
				.add("destruction_type", ApoliDataTypes.DESTRUCTION_TYPE, Explosion.BlockInteraction.DESTROY)
				.add("indestructible", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("indestructible_resistance", SerializableDataTypes.FLOAT, 10.0f)
				.add("destructible", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("damage_self", SerializableDataTypes.BOOLEAN, true)
				.add("create_fire", SerializableDataTypes.BOOLEAN, false),
			(data, entity) -> action(entity,
				data.get("power"),
				data.get("destruction_type"),
				data.get("indestructible"),
				data.get("indestructible_resistance"),
				data.get("destructible"),
				data.get("damage_self"),
				data.get("create_fire")
			)
		);
	}
}
