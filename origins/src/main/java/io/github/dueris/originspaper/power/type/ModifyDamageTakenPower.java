package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// TODO - apply armor condition / damage armor condition
public class ModifyDamageTakenPower extends ModifierPower {
	private final ActionTypeFactory<Entity> selfAction;
	private final ActionTypeFactory<Entity> attackerAction;
	private final ActionTypeFactory<Tuple<Entity, Entity>> biEntityAction;
	private final ConditionTypeFactory<Tuple<Entity, Entity>> biEntityCondition;
	private final ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition;
	private final ConditionTypeFactory<Entity> applyArmorCondition;
	private final ConditionTypeFactory<Entity> damageArmorCondition;

	public ModifyDamageTakenPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								  @Nullable Modifier modifier, @Nullable List<Modifier> modifiers, ActionTypeFactory<Entity> selfAction, ActionTypeFactory<Entity> attackerAction, ActionTypeFactory<Tuple<Entity, Entity>> biEntityAction,
								  ConditionTypeFactory<Tuple<Entity, Entity>> biEntityCondition, ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition, ConditionTypeFactory<Entity> applyArmorCondition, ConditionTypeFactory<Entity> damageArmorCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority, modifier, modifiers);
		this.selfAction = selfAction;
		this.attackerAction = attackerAction;
		this.biEntityAction = biEntityAction;
		this.biEntityCondition = biEntityCondition;
		this.damageCondition = damageCondition;
		this.applyArmorCondition = applyArmorCondition;
		this.damageArmorCondition = damageArmorCondition;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("modify_damage_taken"), ModifierPower.getFactory().getSerializableData()
			.add("self_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("attacker_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
			.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
			.add("apply_armor_condition", ApoliDataTypes.ENTITY_CONDITION, null)
			.add("damage_armor_condition", ApoliDataTypes.ENTITY_CONDITION, null));
	}

	public boolean modifiesArmorApplicance() {
		return this.applyArmorCondition != null;
	}

	public boolean shouldApplyArmor(Entity entity) {
		return applyArmorCondition != null && applyArmorCondition.test(entity);
	}

	public boolean modifiesArmorDamaging() {
		return this.damageArmorCondition != null;
	}

	public boolean shouldDamageArmor(Entity entity) {
		return damageArmorCondition != null && damageArmorCondition.test(entity);
	}

	public boolean doesApply(@NotNull DamageSource source, Entity entity, float damageAmount) {

		Entity attacker = source.getEntity();
		Tuple<DamageSource, Float> damageAndAmount = new Tuple<>(source, damageAmount);

		return attacker == null
			? (damageCondition == null || damageCondition.test(damageAndAmount)) && biEntityCondition == null
			: (damageCondition == null || damageCondition.test(damageAndAmount)) && (biEntityCondition == null || biEntityCondition.test(new Tuple<>(attacker, entity)));

	}

	public void executeActions(Entity attacker, Entity entity) {

		if (selfAction != null) {
			selfAction.accept(entity);
		}

		if (attackerAction != null) {
			attackerAction.accept(attacker);
		}

		if (biEntityAction != null) {
			biEntityAction.accept(new Tuple<>(attacker, entity));
		}

	}
}
