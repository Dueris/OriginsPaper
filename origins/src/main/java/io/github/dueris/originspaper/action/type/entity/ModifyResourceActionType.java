package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.originspaper.util.PowerUtil;
import io.github.dueris.originspaper.util.modifier.Modifier;
import net.minecraft.world.entity.Entity;

import java.util.List;

public class ModifyResourceActionType {

	public static void action(Entity entity, PowerReference power, Modifier modifier) {

		if (PowerUtil.modifyResourceValue(power.getType(entity), List.of(modifier))) {
			PowerHolderComponent.syncPower(entity, power);
		}

	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("modify_resource"),
			new SerializableData()
				.add("resource", ApoliDataTypes.RESOURCE_REFERENCE)
				.add("modifier", Modifier.DATA_TYPE),
			(data, entity) -> action(entity,
				data.get("resource"),
				data.get("modifier")
			)
		);
	}
}
