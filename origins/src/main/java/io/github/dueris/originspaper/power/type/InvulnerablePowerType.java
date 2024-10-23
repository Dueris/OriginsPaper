package io.github.dueris.originspaper.power.type;

import com.mojang.serialization.DataResult;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.condition.type.DamageConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Predicate;

public class InvulnerablePowerType extends PowerType {

	private final Predicate<Tuple<DamageSource, Float>> damageCondition;

	public InvulnerablePowerType(Power power, LivingEntity entity, Predicate<Tuple<DamageSource, Float>> damageCondition) {
		super(power, entity);
		this.damageCondition = damageCondition;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("invulnerability"),
			new SerializableData()
				.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION)
				.validate(data -> {

					ConditionTypeFactory<Tuple<DamageSource, Float>>.Instance damageCondition = data.get("damage_condition");

					if (damageCondition.getFactory() == DamageConditionTypes.AMOUNT) {
						return DataResult.error(() -> "Using the 'amount' damage condition type in a power that uses the 'invulnerability' power type is not allowed!");
					} else {
						return DataResult.success(data);
					}

				}),
			data -> (power, entity) -> new InvulnerablePowerType(power, entity,
				data.get("damage_condition")
			)
		).allowCondition();
	}

	public boolean doesApply(DamageSource source) {
		return damageCondition.test(new Tuple<>(source, 0.0F));
	}
}

