package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;

public class ToggleNightVisionPowerType extends NightVisionPowerType implements Active {

	private final Key key;
	private boolean toggled;

	public ToggleNightVisionPowerType(Power power, LivingEntity entity, boolean activeByDefault, float strength, Key key) {
		super(power, entity, strength);
		this.toggled = activeByDefault;
		this.key = key;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("toggle_night_vision"),
			new SerializableData()
				.add("active_by_default", SerializableDataTypes.BOOLEAN, false)
				.add("strength", SerializableDataTypes.FLOAT, 1.0F)
				.add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Active.Key()),
			data -> (power, entity) -> new ToggleNightVisionPowerType(power, entity,
				data.get("active_by_default"),
				data.get("strength"),
				data.get("key")
			)
		).allowCondition();
	}

	@Override
	public void onUse() {
		this.toggled = !this.toggled;
		PowerHolderComponent.syncPower(entity, this.power);
	}

	public boolean isActive() {
		return this.toggled && super.isActive();
	}

	@Override
	public Tag toTag() {
		return ByteTag.valueOf(toggled);
	}

	@Override
	public void fromTag(Tag tag) {

		if (tag instanceof ByteTag nbtByte) {
			toggled = nbtByte.getAsByte() > 0;
		}

	}

	@Override
	public Key getKey() {
		return key;
	}
}
