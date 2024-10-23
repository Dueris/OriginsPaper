package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class PreventDeathPowerType extends PowerType {

	private final Consumer<Entity> entityAction;
	private final Predicate<Tuple<DamageSource, Float>> condition;

	public PreventDeathPowerType(Power power, LivingEntity entity, Consumer<Entity> entityAction, Predicate<Tuple<DamageSource, Float>> condition) {
		super(power, entity);
		this.entityAction = entityAction;
		this.condition = condition;
	}

	public static boolean doesPrevent(Entity entity, DamageSource source, float amount) {

		boolean prevented = false;
		for (PreventDeathPowerType preventDeathPower : PowerHolderComponent.getPowerTypes(entity, PreventDeathPowerType.class)) {

			if (!preventDeathPower.doesApply(source, amount)) {
				continue;
			}

			preventDeathPower.executeAction();
			prevented = true;

		}

		return prevented;

	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("prevent_death"),
			new SerializableData()
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("damage_condition", ApoliDataTypes.DAMAGE_CONDITION, null),
			data -> (power, entity) -> new PreventDeathPowerType(power, entity,
				data.get("entity_action"),
				data.get("damage_condition")
			)
		).allowCondition();
	}

	public boolean doesApply(DamageSource source, float amount) {
		return condition == null || condition.test(new Tuple<>(source, amount));
	}

	public void executeAction() {
		if (entityAction != null) {
			entityAction.accept(entity);
		}
	}

}
