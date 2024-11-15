package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class RevokeAllPowersEntityActionType extends EntityActionType {

	public static final TypedDataObjectFactory<RevokeAllPowersEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("source", SerializableDataTypes.IDENTIFIER),
		data -> new RevokeAllPowersEntityActionType(
			data.get("source")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("source", actionType.source)
	);

	private final ResourceLocation source;

	public RevokeAllPowersEntityActionType(ResourceLocation source) {
		this.source = source;
	}

	@Override
	protected void execute(Entity entity) {
		PowerHolderComponent.revokeAllPowersFromSource(entity, source, true);
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.REVOKE_ALL_POWERS;
	}

}
