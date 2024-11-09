package io.github.dueris.originspaper.action.type.block;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Optional;
import java.util.function.Consumer;

public class SpawnEntityActionType {

	public static void action(Level world, BlockPos pos, EntityType<?> entityType, Consumer<Entity> entityAction, CompoundTag entityNbt) {

		if (!(world instanceof ServerLevel serverWorld)) {
			return;
		}

		Entity entityToSpawn = Util.getEntityWithPassengersSafe(
			serverWorld,
			entityType,
			entityNbt,
			pos.getCenter(),
			Optional.empty(),
			Optional.empty()
		).orElse(null);

		if (entityToSpawn == null) {
			return;
		}

		serverWorld.tryAddFreshEntityWithPassengers(entityToSpawn);
		entityAction.accept(entityToSpawn);

	}

	public static ActionTypeFactory<Triple<Level, BlockPos, Direction>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("spawn_entity"),
			new SerializableData()
				.add("entity_type", SerializableDataTypes.ENTITY_TYPE)
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("tag", SerializableDataTypes.NBT_COMPOUND, new CompoundTag()),
			(data, block) -> action(block.getLeft(), block.getMiddle(),
				data.get("entity_type"),
				data.getOrElse("entity_action", e -> {
				}),
				data.get("tag")
			)
		);
	}

}
