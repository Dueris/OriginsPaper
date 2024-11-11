package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TogglePowerType extends PowerType implements Active {

	public static final TypedDataObjectFactory<TogglePowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Key())
			.add("retain_state", SerializableDataTypes.BOOLEAN, true)
			.add("active_by_default", SerializableDataTypes.BOOLEAN, true),
		(data, condition) -> new TogglePowerType(
			data.get("key"),
			data.get("retain_state"),
			data.get("active_by_default"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("key", powerType.getKey())
			.set("retain_state", powerType.shouldRetainState)
			.set("active_by_default", powerType.activeByDefault)
	);

	private final Key key;

	private final boolean activeByDefault;
	private final boolean shouldRetainState;

	private boolean toggled;

	public TogglePowerType(Key key, boolean retainState, boolean activeByDefault, Optional<EntityCondition> condition) {
		super(condition);
		this.key = key;
		this.activeByDefault = activeByDefault;
		this.shouldRetainState = retainState;
		this.toggled = activeByDefault;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.TOGGLE;
	}

	@Override
	public boolean shouldTick() {
		return !shouldRetainState && this.condition.isPresent();
	}

	@Override
	public boolean shouldTickWhenInactive() {
		return true;
	}

	@Override
	public void serverTick() {

		if (!super.isActive() && this.toggled) {
			this.toggled = false;
			PowerHolderComponent.syncPower(getHolder(), getPower());
		}

	}

	@Override
	public void onUse() {
		this.toggled = !this.toggled;
		PowerHolderComponent.syncPower(getHolder(), getPower());
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
