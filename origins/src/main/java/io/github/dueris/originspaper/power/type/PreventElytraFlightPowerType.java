package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public class PreventElytraFlightPowerType extends PowerType {

	public static final TypedDataObjectFactory<PreventElytraFlightPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty()),
		(data, condition) -> new PreventElytraFlightPowerType(
			data.get("entity_action"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("entity_action", powerType.entityAction)
	);

	private final Optional<EntityAction> entityAction;

	public PreventElytraFlightPowerType(Optional<EntityAction> entityAction, Optional<EntityCondition> condition) {
		super(condition);
		this.entityAction = entityAction;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.PREVENT_ELYTRA_FLIGHT;
	}

	public void executeAction() {
		entityAction.ifPresent(action -> action.execute(getHolder()));
	}

	@Override
	public void serverTick() {
		this.getHolder().setSharedFlag(7, false);
	}
}
