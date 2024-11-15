package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SpawnEntityEntityActionType extends EntityActionType {

	public static final TypedDataObjectFactory<SpawnEntityEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("entity_type", SerializableDataTypes.ENTITY_TYPE)
			.add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("bientity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("tag", SerializableDataTypes.NBT_COMPOUND, new CompoundTag()),
		data -> new SpawnEntityEntityActionType(
			data.get("entity_type"),
			data.get("entity_action"),
			data.get("bientity_action"),
			data.get("tag")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("entity_type", actionType.entityType)
			.set("entity_action", actionType.entityAction)
			.set("bientity_action", actionType.biEntityAction)
			.set("tag", actionType.tag)
	);

	private final EntityType<?> entityType;

	private final Optional<EntityAction> entityAction;
	private final Optional<BiEntityAction> biEntityAction;

	private final CompoundTag tag;

	public SpawnEntityEntityActionType(EntityType<?> entityType, Optional<EntityAction> entityAction, Optional<BiEntityAction> biEntityAction, CompoundTag tag) {
		this.entityType = entityType;
		this.entityAction = entityAction;
		this.biEntityAction = biEntityAction;
		this.tag = tag;
	}

	@Override
	protected void execute(Entity entity) {

		if (!(entity.level() instanceof ServerLevel serverWorld)) {
			return;
		}

		Optional<Entity> entityToSpawn = Util.getEntityWithPassengersSafe(
			serverWorld,
			entityType,
			tag,
			entity.position(),
			entity.getYRot(),
			entity.getXRot()
		);

		if (entityToSpawn.isEmpty()) {
			return;
		}

		Entity actualEntityToSpawn = entityToSpawn.get();
		serverWorld.tryAddFreshEntityWithPassengers(actualEntityToSpawn);

		entityAction.ifPresent(action -> action.execute(actualEntityToSpawn));
		biEntityAction.ifPresent(action -> action.execute(entity, actualEntityToSpawn));

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.SPAWN_ENTITY;
	}

}
