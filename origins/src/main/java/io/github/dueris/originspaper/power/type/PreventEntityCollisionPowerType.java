package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PreventEntityCollisionPowerType extends PowerType {

	public static final TypedDataObjectFactory<PreventEntityCollisionPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("bientity_condition", BiEntityCondition.DATA_TYPE.optional(), Optional.empty()),
		(data, condition) -> new PreventEntityCollisionPowerType(
			data.get("bientity_condition"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("bientity_condition", powerType.biEntityCondition)
	);

	private final Optional<BiEntityCondition> biEntityCondition;

	public PreventEntityCollisionPowerType(Optional<BiEntityCondition> biEntityCondition, Optional<EntityCondition> condition) {
		super(condition);
		this.biEntityCondition = biEntityCondition;
	}

	public static boolean doesApply(Entity fromEntity, Entity collidingEntity) {
		return PowerHolderComponent.hasPowerType(fromEntity, PreventEntityCollisionPowerType.class, p -> p.doesApply(collidingEntity))
			|| PowerHolderComponent.hasPowerType(collidingEntity, PreventEntityCollisionPowerType.class, p -> p.doesApply(fromEntity));
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.PREVENT_ENTITY_COLLISION;
	}

	public boolean doesApply(Entity target) {
		return biEntityCondition
			.map(condition -> condition.test(getHolder(), target))
			.orElse(true);
	}

}
