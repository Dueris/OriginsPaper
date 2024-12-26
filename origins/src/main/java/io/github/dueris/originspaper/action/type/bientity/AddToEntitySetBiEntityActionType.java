package io.github.dueris.originspaper.action.type.bientity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.BiEntityActionType;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerReference;
import io.github.dueris.originspaper.power.type.EntitySetPowerType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AddToEntitySetBiEntityActionType extends BiEntityActionType {

	public static final TypedDataObjectFactory<AddToEntitySetBiEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("set", ApoliDataTypes.POWER_REFERENCE)
			.add("time_limit", SerializableDataTypes.POSITIVE_INT.optional(), Optional.empty()),
		data -> new AddToEntitySetBiEntityActionType(
			data.get("set"),
			data.get("time_limit")
		),
		(actionType, serializableData) -> serializableData.instance()
			.set("set", actionType.set)
			.set("time_limit", actionType.timeLimit)
	);

	private final PowerReference set;
	private final Optional<Integer> timeLimit;

	public AddToEntitySetBiEntityActionType(PowerReference set, Optional<Integer> timeLimit) {
		this.set = set;
		this.timeLimit = timeLimit;
	}

	@Override
	protected void execute(Entity actor, Entity target) {

		if (set.getNullablePowerType(actor) instanceof EntitySetPowerType entitySet && entitySet.add(target, timeLimit)) {
			PowerHolderComponent.syncPower(actor, set);
		}

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BiEntityActionTypes.ADD_TO_ENTITY_SET;
	}

}
