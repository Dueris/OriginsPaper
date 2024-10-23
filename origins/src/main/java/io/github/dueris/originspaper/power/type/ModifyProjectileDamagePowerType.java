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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ModifyProjectileDamagePowerType extends ValueModifyingPowerType {

	private final Consumer<Entity> selfAction;
	private final Consumer<Entity> targetAction;

	private final Predicate<Entity> targetCondition;
	private final Predicate<Tuple<DamageSource, Float>> damageCondition;

	public ModifyProjectileDamagePowerType(Power power, LivingEntity entity, Consumer<Entity> selfAction, Consumer<Entity> targetAction, Predicate<Entity> targetCondition, Predicate<Tuple<DamageSource, Float>> damageCondition, @NotNull Optional<Modifier> modifier, @NotNull Optional<List<Modifier>> modifiers) {
		super(power, entity);

		this.selfAction = selfAction;
		this.targetAction = targetAction;
		this.targetCondition = targetCondition;
		this.damageCondition = damageCondition;

		modifier.ifPresent(this::addModifier);
		modifiers.ifPresent(mods -> mods.forEach(this::addModifier));

	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("modify_projectile_damage"),
			new SerializableData()
				.add("self_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("target_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("target_condition", ApoliDataTypes.ENTITY_CONDITION, null)
				.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null)
				.add("modifier", Modifier.DATA_TYPE.optional(), Optional.empty())
				.add("modifiers", Modifier.LIST_TYPE.optional(), Optional.empty()),
			data -> (power, entity) -> new ModifyProjectileDamagePowerType(power, entity,
				data.getOrElse("self_action", e -> {
				}),
				data.getOrElse("target_action", e -> {
				}),
				data.getOrElse("target_condition", e -> true),
				data.getOrElse("damage_condition", dmg -> true),
				data.get("modifier"),
				data.get("modifiers")
			)
		).allowCondition();
	}

	public boolean doesApply(DamageSource source, float damageAmount, LivingEntity target) {
		return damageCondition.test(new Tuple<>(source, damageAmount))
			&& (target == null || targetCondition.test(target));
	}

	public void executeActions(Entity target) {
		selfAction.accept(entity);
		targetAction.accept(target);
	}

}
