package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.power.factory.PowerReference;
import io.github.dueris.originspaper.power.type.CooldownPower;
import io.github.dueris.originspaper.power.type.ResourcePower;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ModifyResourceActionType {

	public static void action(Entity entity, PowerReference power, Modifier modifier) {

		int oldValue;
		int newValue;

		switch (power.getType()) {
			case ResourcePower varInt -> {

				oldValue = varInt.getValue(entity);
				newValue = (int) modifier.apply(entity, oldValue);

				varInt.setValue(entity, newValue);

			}
			case CooldownPower cooldown -> {

				oldValue = cooldown.getRemainingTicks(entity);
				newValue = Math.max((int) modifier.apply(entity, oldValue), 0);

				cooldown.setCooldown(entity, newValue);

			}
			case null, default -> {

			}
		}

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(OriginsPaper.apoliIdentifier("modify_resource"),
			new SerializableData()
				.add("resource", ApoliDataTypes.POWER_REFERENCE)
				.add("modifier", Modifier.DATA_TYPE),
			(data, entity) -> action(entity,
				data.get("resource"),
				data.get("modifier")
			)
		);
	}
}
