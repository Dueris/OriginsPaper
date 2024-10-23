package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.originspaper.util.PowerUtil;
import net.minecraft.world.entity.Entity;

public class SetResourceActionType {

	public static void action(Entity entity, PowerReference power, int value) {

		if (PowerUtil.setResourceValue(power.getType(entity), value)) {
			PowerHolderComponent.syncPower(entity, power);
		}

	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("set_resource"),
			new SerializableData()
				.add("resource", ApoliDataTypes.RESOURCE_REFERENCE)
				.add("value", SerializableDataTypes.INT),
			(data, entity) -> action(entity,
				data.get("resource"),
				data.get("value")
			)
		);
	}

}
