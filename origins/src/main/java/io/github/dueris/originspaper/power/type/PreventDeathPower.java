package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
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

	public PreventDeathPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
							 ActionTypeFactory<Entity> entityAction, ConditionTypeFactory<Tuple<DamageSource, Float>> damageCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityAction = entityAction;
		this.damageCondition = damageCondition;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("prevent_death"), PowerType.getFactory().getSerializableData()
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null));
	}

	public static boolean doesPrevent(@NotNull Entity entity, DamageSource source, float amount) {

		boolean prevented = false;
		for (PreventDeathPower preventDeathPower : PowerHolderComponent.getPowers(entity.getBukkitEntity(), PreventDeathPower.class)) {

			if (!preventDeathPower.doesApply(source, amount, entity)) {
				continue;
			}

			preventDeathPower.executeAction(entity);
			prevented = true;

		}

		return prevented;

	}

	public boolean doesApply(DamageSource source, float amount, Entity entity) {
		return isActive(entity) && (damageCondition == null || damageCondition.test(new Tuple<>(source, amount)));
	}

	public void executeAction(Entity entity) {
		if (entityAction != null) {
			entityAction.accept(entity);
		}
	}
}
