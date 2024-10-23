package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.PowerReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class PowerConditionType {

	public static boolean condition(Entity entity, PowerReference power, @Nullable ResourceLocation source) {
		return PowerHolderComponent.KEY.maybeGet(entity)
			.map(component -> source != null ? component.hasPower(power, source) : component.hasPower(power))
			.orElse(false);
	}

	public static ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("power"),
			new SerializableData()
				.add("power", ApoliDataTypes.POWER_REFERENCE)
				.add("source", SerializableDataTypes.IDENTIFIER, null),
			(data, entity) -> condition(entity,
				data.get("power"),
				data.get("source")
			)
		);
	}

}
