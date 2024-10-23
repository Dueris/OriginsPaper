package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.modifier.Modifier;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ModifyDamageTakenPowerType extends ValueModifyingPowerType {

	private final Consumer<Entity> selfAction;
	private final Consumer<Entity> attackerAction;
	private final Consumer<Tuple<Entity, Entity>> biEntityAction;

	private final Predicate<Entity> applyArmorCondition;
	private final Predicate<Entity> damageArmorCondition;
	private final Predicate<Tuple<Entity, Entity>> biEntityCondition;
	private final Predicate<Tuple<DamageSource, Float>> damageCondition;

	public ModifyDamageTakenPowerType(Power power, LivingEntity entity, Consumer<Entity> selfAction, Consumer<Entity> attackerAction, Consumer<Tuple<Entity, Entity>> biEntityAction, Predicate<Entity> applyArmorCondition, Predicate<Entity> damageArmorCondition, Predicate<Tuple<Entity, Entity>> biEntityCondition, Predicate<Tuple<DamageSource, Float>> damageCondition, Modifier modifier, List<Modifier> modifiers) {
		super(power, entity);

		this.selfAction = selfAction;
		this.attackerAction = attackerAction;
		this.biEntityAction = biEntityAction;
		this.applyArmorCondition = applyArmorCondition;
		this.damageArmorCondition = damageArmorCondition;
		this.biEntityCondition = biEntityCondition;
		this.damageCondition = damageCondition;

		if (modifier != null) {
			this.addModifier(modifier);
		}

		if (modifiers != null) {
			modifiers.forEach(this::addModifier);
		}

	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("modify_damage_taken"),
			new SerializableData()
				.add("self_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("attacker_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
				.add("apply_armor_condition", ApoliDataTypes.ENTITY_CONDITION, null)
				.add("damage_armor_condition", ApoliDataTypes.ENTITY_CONDITION, null)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
				.add("modifier", Modifier.DATA_TYPE, null)
				.add("modifiers", Modifier.LIST_TYPE, null),
			data -> (power, entity) -> new ModifyDamageTakenPowerType(power, entity,
				data.get("self_action"),
				data.get("attacker_action"),
				data.get("bientity_action"),
				data.get("apply_armor_condition"),
				data.get("damage_armor_condition"),
				data.get("bientity_condition"),
				data.get("damage_condition"),
				data.get("modifier"),
				data.get("modifiers")
			)
		).allowCondition();
	}

	public boolean modifiesArmorApplicance() {
		return this.applyArmorCondition != null;
	}

	public boolean shouldApplyArmor() {
		return applyArmorCondition != null && applyArmorCondition.test(entity);
	}

	public boolean modifiesArmorDamaging() {
		return this.damageArmorCondition != null;
	}

	public boolean shouldDamageArmor() {
		return damageArmorCondition != null && damageArmorCondition.test(entity);
	}

	public boolean doesApply(DamageSource source, float damageAmount) {

		Entity attacker = source.getEntity();
		Tuple<DamageSource, Float> damageAndAmount = new Tuple<>(source, damageAmount);

		return attacker == null
			? (damageCondition == null || damageCondition.test(damageAndAmount)) && biEntityCondition == null
			: (damageCondition == null || damageCondition.test(damageAndAmount)) && (biEntityCondition == null || biEntityCondition.test(new Tuple<>(attacker, entity)));

	}

	public void executeActions(Entity attacker) {

		if (selfAction != null) {
			selfAction.accept(entity);
		}

		if (attackerAction != null && attacker != null) {
			attackerAction.accept(attacker);
		}

		if (biEntityAction != null) {
			biEntityAction.accept(new Tuple<>(attacker, entity));
		}

	}

}
