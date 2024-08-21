package io.github.dueris.originspaper.power;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class PreventDeathPower extends PowerType {
	private final Consumer<Entity> entityAction;
	private final Predicate<Tuple<DamageSource, Float>> damageCondition;

	public PreventDeathPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
							 ActionFactory<Entity> entityAction, ConditionFactory<Tuple<DamageSource, Float>> damageCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityAction = entityAction;
		this.damageCondition = damageCondition;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("prevent_death"))
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null);
	}

	public boolean doesApply(DamageSource source, float amount) {
		return damageCondition == null || damageCondition.test(new Tuple<>(source, amount));
	}

	public void executeAction(Entity entity) {
		if (entityAction != null) {
			entityAction.accept(entity);
		}
	}
}
