package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.util.PowerUtil;
import io.github.dueris.originspaper.util.ResourceOperation;
import net.minecraft.world.entity.Entity;

public class ChangeResourceActionType {

	public static void action(Entity entity, PowerReference power, ResourceOperation operation, int change) {

		PowerType powerType = power.getType(entity);
		boolean modified = switch (operation) {
			case ADD -> PowerUtil.changeResourceValue(powerType, change);
			case SET -> PowerUtil.setResourceValue(powerType, change);
		};

		if (modified) {
			PowerHolderComponent.syncPower(entity, power);
		}

	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("change_resource"),
			new SerializableData()
				.add("resource", ApoliDataTypes.RESOURCE_REFERENCE)
				.add("operation", ApoliDataTypes.RESOURCE_OPERATION, ResourceOperation.ADD)
				.add("change", SerializableDataTypes.INT),
			(data, entity) -> action(entity,
				data.get("resource"),
				data.get("operation"),
				data.get("change")
			)
		);
	}

}
