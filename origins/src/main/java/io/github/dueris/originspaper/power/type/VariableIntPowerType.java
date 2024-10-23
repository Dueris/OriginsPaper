package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.power.Power;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class VariableIntPowerType extends PowerType {

	protected final int min, max;
	protected int currentValue;

	public VariableIntPowerType(Power power, LivingEntity entity, int startValue, int min, int max) {
		super(power, entity);
		this.currentValue = startValue;
		this.min = min;
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public int getValue() {
		return currentValue;
	}

	public int setValue(int newValue) {
		return currentValue = Mth.clamp(newValue, min, max);
	}

	public int increment() {
		return setValue(getValue() + 1);
	}

	public int decrement() {
		return setValue(getValue() - 1);
	}

	@Override
	public Tag toTag() {
		return IntTag.valueOf(currentValue);
	}

	@Override
	public void fromTag(Tag tag) {
		currentValue = Mth.clamp(((IntTag) tag).getAsInt(), min, max);
	}

}
