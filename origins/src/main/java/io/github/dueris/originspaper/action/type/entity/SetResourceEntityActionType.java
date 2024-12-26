package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.originspaper.util.PowerUtil;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class SetResourceEntityActionType extends EntityActionType {

	public static final TypedDataObjectFactory<SetResourceEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("resource", ApoliDataTypes.RESOURCE_REFERENCE)
			.add("value", SerializableDataTypes.INT),
		data -> new SetResourceEntityActionType(
			data.get("resource"),
			data.get("value")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("resource", actionType.resource)
			.set("value", actionType.value)
	);

	private final PowerReference resource;
	private final int value;

	public SetResourceEntityActionType(PowerReference resource, int value) {
		this.resource = resource;
		this.value = value;
	}

	@Override
	protected void execute(Entity entity) {

		if (PowerUtil.setResourceValue(resource.getNullablePowerType(entity), value)) {
			PowerHolderComponent.syncPower(entity, resource);
		}

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.SET_RESOURCE;
	}

}
