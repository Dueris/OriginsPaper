package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.util.Validatable;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

public abstract class PowerType implements Validatable {

	protected final Optional<EntityCondition> condition;

	private LivingEntity holder;
	private Power power;

	private boolean ticking;
	private boolean tickingWhenInActive;

	public PowerType() {
		this(Optional.empty());
	}

	public PowerType(Optional<EntityCondition> condition) {
		this.condition = condition;
	}

	public static <T extends PowerType> TypedDataObjectFactory<T> createConditionedDataFactory(SerializableData serializableData, BiFunction<SerializableData.Instance, Optional<EntityCondition>, T> fromData, BiFunction<T, SerializableData, SerializableData.Instance> toData) {
		return TypedDataObjectFactory.simple(
			serializableData
				.add("condition", EntityCondition.DATA_TYPE.optional(), Optional.empty()),
			data -> fromData.apply(
				data,
				data.get("condition")
			),
			(t, _serializableData) -> toData.apply(t, _serializableData)
				.set("condition", t.condition)
		);
	}

	@Override
	public void validate() throws Exception {
		this.toData().validate();
	}

	@NotNull
	public abstract PowerConfiguration<?> getConfig();

	@ApiStatus.Internal
	public final void setPower(@NotNull final Power power) {

		if (this.power == null && !this.getConfig().equals(power.getPowerType().getConfig())) {
			throw new IllegalArgumentException("Couldn't initialize power type \"" + this.getConfig().id() + "\" with a power that has a mismatching power type! (power type data: " + this.toData() + ")");
		}

		else if (!Objects.equals(this.power, power)) {
			this.power = power;
		}

	}

	@ApiStatus.Internal
	public final void setHolder(@NotNull final LivingEntity holder) {
		this.holder = holder;
	}

	public SerializableData.Instance toData() {

		//noinspection unchecked
		PowerConfiguration<PowerType> config = (PowerConfiguration<PowerType>) this.getConfig();
		TypedDataObjectFactory<PowerType> dataFactory = config.dataFactory();

		return dataFactory.toData(this);

	}

	public final Power getPower() {
		return Objects.requireNonNull(power, "Couldn't get power of power type \"" + getConfig().id() + "\" as it wasn't initialized yet! (power type data: " + this.toData() + ")");
	}

	public final LivingEntity getHolder() {
		return Objects.requireNonNull(holder, "Couldn't get holder of power type \"" + getConfig().id() + " as it wasn't initialized yet! (power type data: " + this.toData() + ")");
	}

	public void serverTick() {

	}

	public void clientTick() {

	}

	public void commonTick() {

	}

	public void onInit() {

	}

	public void onGained() {

	}

	public void onLost() {

	}

	public void onAdded() {

	}

	public void onRemoved() {

	}

	public void onRespawn() {

	}

	public void onLeave() {

	}

	public boolean isActive() {
		return isInitialized() && condition
			.map(condition -> condition.test(getHolder()))
			.orElse(true);
	}

	public Tag toTag() {
		return new CompoundTag();
	}

	public void fromTag(Tag tag) {

	}

	public boolean isInitialized() {
		return holder != null
			&& power != null;
	}

	public boolean shouldTick() {
		return ticking;
	}

	public boolean shouldTickWhenInactive() {
		return tickingWhenInActive;
	}

	public final void setTicking() {
		setTicking(false);
	}

	public final void setTicking(boolean whenInActive) {
		this.ticking = true;
		this.tickingWhenInActive = whenInActive;
	}

}
