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
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModifyProjectileDamagePower extends ModifierPower {
	private final ActionTypeFactory<Entity> selfAction;
	private final ActionTypeFactory<Entity> targetAction;
	private final ConditionTypeFactory<Entity> targetCondition;
	private final ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition;

	public ModifyProjectileDamagePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
									   @Nullable Modifier modifier, @Nullable List<Modifier> modifiers, ActionTypeFactory<Entity> selfAction, ActionTypeFactory<Entity> targetAction, ConditionTypeFactory<Entity> targetCondition,
									   ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority, modifier, modifiers);
		this.selfAction = selfAction;
		this.targetAction = targetAction;
		this.targetCondition = targetCondition;
		this.damageCondition = damageCondition;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("modify_projectile_damage"), ModifierPower.getFactory().getSerializableData()
			.add("self_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("target_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("target_condition", ApoliDataTypes.ENTITY_CONDITION, null)
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null));
	}

	public boolean doesApply(DamageSource source, float damageAmount, LivingEntity target, Entity entity) {
		return damageCondition.test(new Tuple<>(source, damageAmount)) && (target == null || targetCondition == null || targetCondition.test(target)) && isActive(entity);
	}

	public void executeActions(Entity target, Entity entity) {

		if (selfAction != null) {
			selfAction.accept(entity);
		}

		if (targetAction != null) {
			targetAction.accept(target);
		}

	}
}
