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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ModifyDamageDealtPowerType extends ValueModifyingPowerType {

	private final Consumer<Entity> selfAction;
	private final Consumer<Entity> targetAction;
	private final Consumer<Tuple<Entity, Entity>> biEntityAction;

	private final Predicate<Entity> targetCondition;
	private final Predicate<Tuple<Entity, Entity>> biEntityCondition;
	private final Predicate<Tuple<DamageSource, Float>> damageCondition;


	public ModifyDamageDealtPowerType(Power power, LivingEntity entity, Consumer<Entity> selfAction, Consumer<Entity> targetAction, Consumer<Tuple<Entity, Entity>> biEntityAction, Predicate<Entity> targetCondition, Predicate<Tuple<Entity, Entity>> biEntityCondition, Predicate<Tuple<DamageSource, Float>> damageCondition, Modifier modifier, List<Modifier> modifiers) {
		super(power, entity);

		this.selfAction = selfAction;
		this.targetAction = targetAction;
		this.biEntityAction = biEntityAction;

		this.targetCondition = targetCondition;
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
			OriginsPaper.apoliIdentifier("modify_damage_dealt"),
			new SerializableData()
				.add("self_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("target_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
				.add("target_condition", ApoliDataTypes.ENTITY_CONDITION, null)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
				.add("modifier", Modifier.DATA_TYPE, null)
				.add("modifiers", Modifier.LIST_TYPE, null),
			data -> (power, entity) -> new ModifyDamageDealtPowerType(power, entity,
				data.get("self_action"),
				data.get("target_action"),
				data.get("bientity_action"),
				data.get("target_condition"),
				data.get("bientity_condition"),
				data.get("damage_condition"),
				data.get("modifier"),
				data.get("modifiers")
			)
		).allowCondition();
	}

	public boolean doesApply(DamageSource source, float damageAmount, @Nullable LivingEntity target) {
		return (damageCondition == null || damageCondition.test(new Tuple<>(source, damageAmount)))
			&& (target == null || targetCondition == null || targetCondition.test(target))
			&& (target == null || biEntityCondition == null || biEntityCondition.test(new Tuple<>(entity, target)));
	}

	public void executeActions(Entity target) {

		if (selfAction != null) {
			selfAction.accept(entity);
		}

		if (targetAction != null) {
			targetAction.accept(target);
		}

		if (biEntityAction != null) {
			biEntityAction.accept(new Tuple<>(entity, target));
		}

	}

}
