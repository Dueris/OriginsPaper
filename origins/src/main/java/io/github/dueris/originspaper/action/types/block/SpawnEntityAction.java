package io.github.dueris.originspaper.action.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
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
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public class SpawnEntityAction {

	public static void action(SerializableData.Instance data, @NotNull Triple<Level, BlockPos, Direction> worldPosAndDirection) {

		Level world = worldPosAndDirection.getLeft();
		BlockPos pos = worldPosAndDirection.getMiddle();

		if (!(world instanceof ServerLevel serverWorld)) {
			return;
		}

		EntityType<?> entityType = data.get("entity_type");
		CompoundTag entityNbt = data.get("tag");

		Entity entityToSpawn = Util.getEntityWithPassengers(
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
		data.<Consumer<Entity>>ifPresent("entity_action", entityAction -> entityAction.accept(entityToSpawn));

	}

	public static @NotNull ActionTypeFactory<Triple<Level, BlockPos, Direction>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("spawn_entity"),
			SerializableData.serializableData()
				.add("entity_type", SerializableDataTypes.ENTITY_TYPE)
				.add("tag", SerializableDataTypes.NBT_COMPOUND, new CompoundTag())
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null),
			SpawnEntityAction::action
		);
	}
}
