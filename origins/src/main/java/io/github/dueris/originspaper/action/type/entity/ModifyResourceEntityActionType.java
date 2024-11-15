package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.originspaper.util.PowerUtil;
import io.github.dueris.originspaper.util.modifier.Modifier;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModifyResourceEntityActionType extends EntityActionType {

	public static final TypedDataObjectFactory<ModifyResourceEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("resource", ApoliDataTypes.RESOURCE_REFERENCE)
			.add("modifier", Modifier.DATA_TYPE),
		data -> new ModifyResourceEntityActionType(
			data.get("resource"),
			data.get("modifier")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("resource", actionType.resource)
			.set("modifier", actionType.modifier)
	);

	private final PowerReference resource;
	private final Modifier modifier;

	public ModifyResourceEntityActionType(PowerReference resource, Modifier modifier) {
		this.resource = resource;
		this.modifier = modifier;
	}

	@Override
	protected void execute(Entity entity) {

		if (PowerUtil.modifyResourceValue(resource.getPowerTypeFrom(entity), List.of(modifier))) {
			PowerHolderComponent.syncPower(entity, resource);
		}

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.MODIFY_RESOURCE;
	}

}
