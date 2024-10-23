package io.github.dueris.originspaper.util;

import com.mojang.serialization.DataResult;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.SubPower;
import io.github.dueris.originspaper.power.type.CooldownPowerType;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.power.type.VariableIntPowerType;
import io.github.dueris.originspaper.util.modifier.Modifier;
import io.github.dueris.originspaper.util.modifier.ModifierUtil;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PowerUtil {

	public static DataResult<PowerType> validateResource(@Nullable PowerType powerType) {
		return switch (powerType) {
			case VariableIntPowerType varInt -> DataResult.success(varInt);
			case CooldownPowerType cooldown -> DataResult.success(cooldown);
			case null -> DataResult.error(() -> "Power type cannot be null!");
			default -> {

				Power power = powerType.getPower();

				ResourceLocation powerTypeId = power.getFactoryInstance().getSerializerId();
				StringBuilder powerString = new StringBuilder();

				if (power instanceof SubPower subPower) {
					powerString
						.append("Sub-power \"").append(subPower.getSubName()).append("\"")
						.append(" of power \"").append(subPower.getSuperPowerId()).append("\"");
				} else {
					powerString.append("Power \"").append(power.getId()).append("\"");
				}

				DataResult<PowerType> result = DataResult.error(() -> powerString
					.append(" is using power type \"").append(powerTypeId).append("\"")
					.append(", which is not considered a resource!")
					.toString());

				yield result.setPartial(powerType);

			}
		};
	}

	public static float getResourceValue(PowerType powerType) {
		return switch (powerType) {
			case VariableIntPowerType varInt -> varInt.getValue();
			case CooldownPowerType cooldown -> cooldown.getRemainingTicks();
			case null, default -> 0;
		};
	}

	public static int getResourceValueInt(PowerType type) {
		return (int) getResourceValue(type);
	}

	public static boolean modifyResourceValue(PowerType powerType, Collection<Modifier> modifiers) {

		int oldValue = Math.round(getResourceValue(powerType));
		int newValue = 0;

		switch (powerType) {
			case VariableIntPowerType varInt -> {
				varInt.setValue((int) ModifierUtil.applyModifiers(powerType.getHolder(), modifiers, oldValue));
				newValue = varInt.getValue();
			}
			case CooldownPowerType cooldown -> {

				int modified = Math.max((int) ModifierUtil.applyModifiers(powerType.getHolder(), modifiers, oldValue), 0);
				cooldown.modify(modified - oldValue);

				newValue = cooldown.getRemainingTicks();

			}
			case null, default -> {

			}
		}

		return oldValue != newValue;

	}

	public static boolean changeResourceValue(PowerType powerType, int value) {

		int oldValue = Math.round(getResourceValue(powerType));
		int newValue = 0;

		switch (powerType) {
			case VariableIntPowerType varInt -> {
				varInt.setValue(oldValue + value);
				newValue = varInt.getValue();
			}
			case CooldownPowerType cooldown -> {
				cooldown.modify(value);
				newValue = cooldown.getRemainingTicks();
			}
			case null, default -> {

			}
		}

		return oldValue != newValue;

	}

	public static boolean setResourceValue(PowerType powerType, int value) {

		int oldValue = Math.round(getResourceValue(powerType));
		int newValue = 0;

		switch (powerType) {
			case VariableIntPowerType varInt -> {
				varInt.setValue(value);
				newValue = varInt.getValue();
			}
			case CooldownPowerType cooldown -> {
				cooldown.setCooldown(value);
				newValue = cooldown.getRemainingTicks();
			}
			case null, default -> {

			}
		}

		return oldValue != newValue;

	}

}

