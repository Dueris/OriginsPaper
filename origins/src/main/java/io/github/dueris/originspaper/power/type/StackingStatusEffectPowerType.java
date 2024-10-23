package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class StackingStatusEffectPowerType extends StatusEffectPowerType {

	private final int minStack;
	private final int maxStack;
	private final int durationPerStack;
	private final int tickRate;

	private int currentStack;

	public StackingStatusEffectPowerType(Power power, LivingEntity entity, int minStack, int maxStack, int durationPerStack, int tickRate) {
		super(power, entity);
		this.minStack = minStack;
		this.maxStack = maxStack;
		this.durationPerStack = durationPerStack;
		this.tickRate = tickRate;
		this.setTicking(true);
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("stacking_status_effect"),
			new SerializableData()
				.add("min_stacks", SerializableDataTypes.INT)
				.add("max_stacks", SerializableDataTypes.INT)
				.add("duration_per_stack", SerializableDataTypes.INT)
				.add("tick_rate", SerializableDataTypes.POSITIVE_INT, 10)
				.add("effect", SerializableDataTypes.STATUS_EFFECT_INSTANCE, null)
				.add("effects", SerializableDataTypes.STATUS_EFFECT_INSTANCES, null),
			data -> (power, entity) -> {

				StackingStatusEffectPowerType powerType = new StackingStatusEffectPowerType(
					power,
					entity,
					data.getInt("min_stacks"),
					data.getInt("max_stacks"),
					data.getInt("duration_per_stack"),
					data.getInt("tick_rate")
				);

				data.<MobEffectInstance>ifPresent("effect", powerType::addEffect);
				data.<List<MobEffectInstance>>ifPresent("effects", effects -> effects.forEach(powerType::addEffect));

				return powerType;

			}
		).allowCondition();
	}

	public void tick() {
		if (entity.tickCount % tickRate == 0) {
			if (isActive()) {
				currentStack += 1;
				if (currentStack > maxStack) {
					currentStack = maxStack;
				}
				if (currentStack > 0) {
					applyEffects();
				}
			} else {
				currentStack -= 1;
				if (currentStack < minStack) {
					currentStack = minStack;
				}
			}
		}
	}

	@Override
	public void applyEffects() {
		effects.forEach(sei -> {
			int duration = durationPerStack * currentStack;
			if (duration > 0) {
				MobEffectInstance applySei = new MobEffectInstance(sei.getEffect(), duration, sei.getAmplifier(), sei.isAmbient(), sei.isVisible(), sei.showIcon());
				entity.addEffect(applySei);
			}
		});
	}

	@Override
	public Tag toTag() {
		return IntTag.valueOf(currentStack);
	}

	@Override
	public void fromTag(Tag tag) {
		currentStack = ((IntTag) tag).getAsInt();
	}

}
