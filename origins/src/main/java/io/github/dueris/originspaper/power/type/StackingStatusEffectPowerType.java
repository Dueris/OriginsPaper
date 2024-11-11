package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class StackingStatusEffectPowerType extends StatusEffectPowerType {

	public static final TypedDataObjectFactory<StackingStatusEffectPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("effect", SerializableDataTypes.STATUS_EFFECT_INSTANCE, null)
			.addFunctionedDefault("effects", SerializableDataTypes.STATUS_EFFECT_INSTANCES, data -> Util.singletonListOrNull(data.get("effect")))
			.add("min_stacks", SerializableDataTypes.INT)
			.add("max_stacks", SerializableDataTypes.INT)
			.add("duration_per_stack", SerializableDataTypes.INT)
			.add("tick_rate", SerializableDataTypes.POSITIVE_INT, 10)
			.validate(Util.validateAnyFieldsPresent("effect", "effects")),
		(data, condition) -> new StackingStatusEffectPowerType(
			data.get("effects"),
			data.get("min_stacks"),
			data.get("max_stacks"),
			data.get("duration_per_stack"),
			data.get("tick_rate"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("effects", powerType.effects)
			.set("min_stacks", powerType.minStacks)
			.set("max_stacks", powerType.maxStacks)
			.set("duration_per_stack", powerType.durationPerStack)
			.set("tick_rate", powerType.tickRate)
	);

	private final int minStacks;
	private final int maxStacks;

	private final int durationPerStack;
	private final int tickRate;

	private Integer startTicks = null;
	private Integer endTicks = null;

	private boolean wasActive = false;
	private int currentStack = 0;

	public StackingStatusEffectPowerType(List<MobEffectInstance> effects, int minStacks, int maxStacks, int durationPerStack, int tickRate, Optional<EntityCondition> condition) {
		super(effects, condition);
		this.minStacks = minStacks;
		this.maxStacks = maxStacks;
		this.durationPerStack = durationPerStack;
		this.tickRate = tickRate;
		this.setTicking(true);
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.STACKING_STATUS_EFFECT;
	}

	@Override
	public void serverTick() {

		LivingEntity holder = getHolder();

		if (isActive()) {

			if (startTicks == null) {
				this.startTicks = holder.tickCount % tickRate;
				this.endTicks = null;
			}

			else if (holder.tickCount % tickRate == startTicks) {

				this.currentStack = Math.clamp(++currentStack, minStacks, maxStacks);
				this.wasActive = true;

				if (currentStack > 0) {
					this.applyEffects();
				}

			}

		}

		else if (wasActive) {

			if (endTicks == null) {
				this.startTicks = null;
				this.endTicks = holder.tickCount % tickRate;
			}

			else if (holder.tickCount % tickRate == endTicks) {
				this.currentStack = Math.clamp(--currentStack, minStacks, maxStacks);
				this.wasActive = currentStack <= minStacks;
			}

		}

	}

	@Override
	public void applyEffects() {

		for (MobEffectInstance effectInstance : effects) {

			int duration = Math.max(0, durationPerStack * currentStack);
			effectInstance = new MobEffectInstance(effectInstance.getEffect(), duration, effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.isVisible(), effectInstance.showIcon());

			getHolder().addEffect(effectInstance);

		}

	}

	@Override
	public Tag toTag() {
		return IntTag.valueOf(currentStack);
	}

	@Override
	public void fromTag(Tag tag) {

		if (tag instanceof IntTag nbtInt) {
			this.currentStack = nbtInt.getAsInt();
		}

	}

}
