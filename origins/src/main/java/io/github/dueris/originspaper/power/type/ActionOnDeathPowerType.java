package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ActionOnDeathPowerType extends PowerType {

	private final Predicate<Tuple<DamageSource, Float>> damageCondition;
	private final Predicate<Tuple<Entity, Entity>> bientityCondition;
	private final Consumer<Tuple<Entity, Entity>> bientityAction;

	public ActionOnDeathPowerType(Power power, LivingEntity entity, Consumer<Tuple<Entity, Entity>> bientityAction, Predicate<Tuple<Entity, Entity>> bientityCondition, Predicate<Tuple<DamageSource, Float>> damageCondition) {
		super(power, entity);
		this.damageCondition = damageCondition;
		this.bientityAction = bientityAction;
		this.bientityCondition = bientityCondition;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("action_on_death"),
			new SerializableData()
				.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null),
			data -> (power, entity) -> new ActionOnDeathPowerType(power, entity,
				data.get("bientity_action"),
				data.get("bientity_condition"),
				data.get("damage_condition")
			)
		).allowCondition();
	}

	public boolean doesApply(Entity actor, DamageSource damageSource, float damageAmount) {
		return (bientityCondition == null || bientityCondition.test(new Tuple<>(actor, entity)))
			&& (damageCondition == null || damageCondition.test(new Tuple<>(damageSource, damageAmount)));
	}

	public void onDeath(Entity actor) {
		bientityAction.accept(new Tuple<>(actor, entity));
	}
}
