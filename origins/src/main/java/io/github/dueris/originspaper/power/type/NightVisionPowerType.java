package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class NightVisionPowerType extends PowerType {

	public static final TypedDataObjectFactory<NightVisionPowerType> DATA_FACTORY = createConditionedDataFactory(
		new SerializableData()
			.add("strength", SerializableDataTypes.FLOAT, 1.0F),
		(data, condition) -> new NightVisionPowerType(
			data.get("strength"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("strength", powerType.strength)
	);

	private final float strength;

	public NightVisionPowerType(float strength, Optional<EntityCondition> condition) {
		super(condition);
		this.strength = strength;
	}

	@Override
	public void serverTick() {
		if (isActive()) {
			if (!getHolder().hasEffect(MobEffects.NIGHT_VISION)) {
				getHolder().addEffect(
					new MobEffectInstance(MobEffects.NIGHT_VISION, Integer.MAX_VALUE, 255, false, false, false)
				);
			}
		} else {
			if (getHolder().hasEffect(MobEffects.NIGHT_VISION)) {
				getHolder().removeEffect(MobEffects.NIGHT_VISION);
			}
		}
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.NIGHT_VISION;
	}

	public float getStrength() {
		return strength;
	}

}
