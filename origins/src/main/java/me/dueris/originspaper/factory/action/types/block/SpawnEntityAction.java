package me.dueris.originspaper.factory.action.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.ActionFactory;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import me.dueris.originspaper.util.Util;
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

	public static void action(DeserializedFactoryJson data, @NotNull Triple<Level, BlockPos, Direction> worldPosAndDirection) {

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

	public static @NotNull ActionFactory<Triple<Level, BlockPos, Direction>> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("spawn_entity"),
			InstanceDefiner.instanceDefiner()
				.add("entity_type", SerializableDataTypes.ENTITY_TYPE)
				.add("tag", SerializableDataTypes.NBT, new CompoundTag())
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null),
			SpawnEntityAction::action
		);
	}
}
