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

public class TogglePowerType extends PowerType implements Active {

	private final Key key;
	private final boolean shouldRetainState;

	private boolean toggled;

	public TogglePowerType(Power power, LivingEntity entity, boolean activeByDefault, boolean shouldRetainState, Key key) {
		super(power, entity);
		this.key = key;
		this.shouldRetainState = shouldRetainState;
		this.toggled = activeByDefault;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("toggle"),
			new SerializableData()
				.add("active_by_default", SerializableDataTypes.BOOLEAN, true)
				.add("retain_state", SerializableDataTypes.BOOLEAN, true)
				.add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Active.Key()),
			data -> (power, entity) -> new TogglePowerType(power, entity,
				data.get("active_by_default"),
				data.get("retain_state"),
				data.get("key")
			)
		).allowCondition();
	}

	@Override
	public boolean shouldTick() {
		return !shouldRetainState && !this.conditions.isEmpty();
	}

	@Override
	public boolean shouldTickWhenInactive() {
		return true;
	}

	@Override
	public void tick() {

		if (!super.isActive() && this.toggled) {
			this.toggled = false;
			PowerHolderComponent.syncPower(entity, this.power);
		}

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
			this.toggled = nbtByte.getAsByte() > 0;
		}

	}

	@Override
	public Key getKey() {
		return key;
	}

}

