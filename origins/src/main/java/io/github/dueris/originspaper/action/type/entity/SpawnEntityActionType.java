package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SpawnEntityActionType {

	public static void action(Entity entity, EntityType<?> entityType, Consumer<Entity> entityAction, Consumer<Tuple<Entity, Entity>> biEntityAction, CompoundTag entityNbt) {

		if (!(entity.level() instanceof ServerLevel serverWorld)) {
			return;
		}

		Entity entityToSpawn = Util.getEntityWithPassengers(
			serverWorld,
			entityType,
			entityNbt,
			entity.position(),
			entity.getYRot(),
			entity.getXRot()
		).orElse(null);

		if (entityToSpawn == null) {
			return;
		}

		serverWorld.tryAddFreshEntityWithPassengers(entityToSpawn);

		entityAction.accept(entityToSpawn);
		biEntityAction.accept(new Tuple<>(entity, entityToSpawn));

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("spawn_entity"),
			new SerializableData()
				.add("entity_type", SerializableDataTypes.ENTITY_TYPE)
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
				.add("tag", SerializableDataTypes.NBT_COMPOUND, null),
			(data, entity) -> action(entity,
				data.get("entity_type"),
				data.getOrElse("entity_action", e -> {
				}),
				data.getOrElse("bientity_action", at -> {
				}),
				data.get("tag")
			)
		);
	}

}
