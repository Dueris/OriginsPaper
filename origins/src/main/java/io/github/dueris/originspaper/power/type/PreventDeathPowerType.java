package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.condition.DamageCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PreventDeathPowerType extends PowerType {

	public static final TypedDataObjectFactory<PreventDeathPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("damage_condition", DamageCondition.DATA_TYPE.optional(), Optional.empty()),
		(data, condition) -> new PreventDeathPowerType(
			data.get("entity_action"),
			data.get("damage_condition"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("entity_action", powerType.entityAction)
			.set("damage_condition", powerType.damageCondition)
	);

	private final Optional<EntityAction> entityAction;
	private final Optional<DamageCondition> damageCondition;

	public PreventDeathPowerType(Optional<EntityAction> entityAction, Optional<DamageCondition> damageCondition, Optional<EntityCondition> condition) {
		super(condition);
		this.entityAction = entityAction;
		this.damageCondition = damageCondition;
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

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.PREVENT_DEATH;
	}

	public boolean doesApply(DamageSource source, float amount) {
		return damageCondition
			.map(condition -> condition.test(source, amount))
			.orElse(true);
	}

	public void executeAction() {
		entityAction.ifPresent(action -> action.execute(getHolder()));
	}

}
