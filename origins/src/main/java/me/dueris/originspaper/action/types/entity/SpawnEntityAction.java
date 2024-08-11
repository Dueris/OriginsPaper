package me.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.action.ActionFactory;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public class SpawnEntityAction {

	public static void action(DeserializedFactoryJson data, @NotNull Entity entity) {

		if (entity.level().isClientSide) return;

		ServerLevel serverWorld = (ServerLevel) entity.level();
		EntityType<?> entityType = data.get("entity_type");
		CompoundTag entityNbt = data.get("tag");

		Optional<Entity> opt$entityToSpawn = Util.getEntityWithPassengers(
			serverWorld,
			entityType,
			entityNbt,
			entity.position(),
			entity.getYRot(),
			entity.getXRot()
		);

		if (opt$entityToSpawn.isEmpty()) return;
		Entity entityToSpawn = opt$entityToSpawn.get();

		serverWorld.tryAddFreshEntityWithPassengers(entityToSpawn);
		data.<Consumer<Entity>>ifPresent("entity_action", entityAction -> entityAction.accept(entityToSpawn));
		data.<Consumer<Tuple<Entity, Entity>>>ifPresent("bientity_action", biEntityAction -> biEntityAction.accept(new Tuple<>(entity, entityToSpawn)));

	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("spawn_entity"),
			InstanceDefiner.instanceDefiner()
				.add("entity_type", SerializableDataTypes.ENTITY_TYPE)
				.add("tag", SerializableDataTypes.NBT, null)
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null),
			SpawnEntityAction::action
		);
	}
}
