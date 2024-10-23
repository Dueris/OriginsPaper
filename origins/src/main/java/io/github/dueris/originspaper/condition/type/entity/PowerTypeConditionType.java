package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.power.type.PowerType;
import net.minecraft.world.entity.Entity;

public class PowerTypeConditionType {

	public static boolean condition(Entity entity, PowerTypeFactory<?> powerTypeFactory) {
		return PowerHolderComponent.KEY.maybeGet(entity)
			.stream()
			.flatMap(pc -> pc.getPowerTypes().stream())
			.map(PowerType::getPower)
			.map(Power::getFactoryInstance)
			.map(PowerTypeFactory.Instance::getFactory)
			.anyMatch(powerTypeFactory::equals);
	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("power_type"),
			new SerializableData()
				.add("power_type", ApoliDataTypes.POWER_TYPE_FACTORY),
			(data, entity) -> condition(entity,
				data.get("power_type")
			)
		);
	}

}
