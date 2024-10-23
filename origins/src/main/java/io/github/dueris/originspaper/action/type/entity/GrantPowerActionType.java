package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.PowerReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class GrantPowerActionType {

	public static void action(Entity entity, PowerReference power, ResourceLocation source) {
		PowerHolderComponent.grantPower(entity, power, source, true);
	}

	public static ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("grant_power"),
			new SerializableData()
				.add("power", ApoliDataTypes.POWER_REFERENCE)
				.add("source", SerializableDataTypes.IDENTIFIER),
			(data, entity) -> action(entity,
				data.get("power"),
				data.get("source")
			)
		);
	}

}
