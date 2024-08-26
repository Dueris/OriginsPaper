package io.github.dueris.originspaper.power;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ActionOnDeathPower extends PowerType {
	private final ActionTypeFactory<Tuple<Entity, Entity>> bientityAction;
	private final ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition;
	private final ConditionTypeFactory<Tuple<Entity, Entity>> bientityCondition;

	public ActionOnDeathPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
							  ActionTypeFactory<Tuple<Entity, Entity>> bientityAction, ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition, ConditionTypeFactory<Tuple<Entity, Entity>> bientityCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.bientityAction = bientityAction;
		this.damageCondition = damageCondition;
		this.bientityCondition = bientityCondition;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("action_on_death"))
			.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION)
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
			.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null);
	}

	public boolean doesApply(Entity actor, DamageSource damageSource, float damageAmount, Entity entity) {
		return (bientityCondition == null || bientityCondition.test(new Tuple<>(actor, entity)))
			&& (damageCondition == null || damageCondition.test(new Tuple<>(damageSource, damageAmount))) && isActive(entity);
	}

	public void onDeath(Entity actor, Entity entity) {
		bientityAction.accept(new Tuple<>(actor, entity));
	}

}
