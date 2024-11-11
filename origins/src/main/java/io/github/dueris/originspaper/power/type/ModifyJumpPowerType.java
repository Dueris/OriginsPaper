package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.modifier.Modifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ModifyJumpPowerType extends ValueModifyingPowerType {

	public static final TypedDataObjectFactory<ModifyJumpPowerType> DATA_FACTORY = createConditionedModifyingDataFactory(
		new SerializableData()
			.add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty()),
		(data, modifiers, condition) -> new ModifyJumpPowerType(
			data.get("entity_action"),
			modifiers,
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("entity_action", powerType.entityAction)
	);

	private final Optional<EntityAction> entityAction;

	public ModifyJumpPowerType(Optional<EntityAction> entityAction, List<Modifier> modifiers, Optional<EntityCondition> condition) {
		super(modifiers, condition);
		this.entityAction = entityAction;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.MODIFY_JUMP;
	}

	public void executeAction() {
		entityAction.ifPresent(action -> action.execute(getHolder()));
	}

}
