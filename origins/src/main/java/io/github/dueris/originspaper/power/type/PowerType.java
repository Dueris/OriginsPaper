package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class PowerType {

	protected final LivingEntity entity;
	protected final Power power;
	protected List<Predicate<Entity>> conditions;
	private boolean shouldTick = false;
	private boolean shouldTickWhenInactive = false;

	public PowerType(Power power, LivingEntity entity) {
		this.power = power;
		this.entity = entity;
		this.conditions = new LinkedList<>();
	}

	public static <T extends PowerType> PowerTypeFactory<T> createSimpleFactory(ResourceLocation id, BiFunction<Power, LivingEntity, T> powerConstructor) {
		return new PowerTypeFactory<>(id, new SerializableData(), data -> powerConstructor).allowCondition();
	}

	public PowerType addCondition(Predicate<Entity> condition) {
		this.conditions.add(condition);
		return this;
	}

	protected void setTicking() {
		this.setTicking(false);
	}

	protected void setTicking(boolean evenWhenInactive) {
		this.shouldTick = true;
		this.shouldTickWhenInactive = evenWhenInactive;
	}

	public boolean shouldTick() {
		return shouldTick;
	}

	public boolean shouldTickWhenInactive() {
		return shouldTickWhenInactive;
	}

	public void tick() {

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
		return conditions.stream().allMatch(condition -> condition.test(entity));
	}

	public Tag toTag() {
		return new CompoundTag();
	}

	public void fromTag(Tag tag) {

	}

	public Power getPower() {
		return power;
	}

	public LivingEntity getHolder() {
		return entity;
	}

	public ResourceLocation getPowerId() {
		return this.getPower().getId();
	}

}
