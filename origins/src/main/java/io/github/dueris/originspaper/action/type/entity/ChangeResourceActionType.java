package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.ResourceOperation;
import io.github.dueris.originspaper.power.type.CooldownPower;
import io.github.dueris.originspaper.power.type.ResourcePower;
import io.github.dueris.originspaper.power.factory.PowerReference;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ChangeResourceActionType {

	public static void action(Entity entity, PowerReference power, ResourceOperation operation, int change) {

		int oldValue;
		int newValue;

		switch (power.getType()) {
			case ResourcePower varInt -> {

				oldValue = varInt.getValue(entity);
				newValue = processValue(operation, oldValue, change);

				varInt.setValue(entity, newValue);

			}
			case CooldownPower cooldown -> {

				oldValue = cooldown.getRemainingTicks(entity);
				newValue = processValue(operation, oldValue, change);

				cooldown.setCooldown(entity, newValue);

			}
			case null, default -> {

			}
		}

	}

	private static int processValue(@NotNull ResourceOperation operation, int oldValue, int newValue) {
		return switch (operation) {
			case ADD -> oldValue + newValue;
			case SET -> newValue;
		};
	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("change_resource"),
			new SerializableData()
				.add("resource", ApoliDataTypes.POWER_REFERENCE)
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
