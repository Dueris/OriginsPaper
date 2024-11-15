package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BrightnessEntityConditionType extends EntityConditionType {

	public static final TypedDataObjectFactory<BrightnessEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("comparison", ApoliDataTypes.COMPARISON)
			.add("compare_to", SerializableDataTypes.FLOAT),
		data -> new BrightnessEntityConditionType(
			data.get("comparison"),
			data.get("compare_to")
		),
		(conditionType, serializableData) -> serializableData.instance()
			.set("comparison", conditionType.comparison)
			.set("compare_to", conditionType.compareTo)
	);

	private final Comparison comparison;
	private final float compareTo;

	public BrightnessEntityConditionType(Comparison comparison, float compareTo) {
		this.comparison = comparison;
		this.compareTo = compareTo;
	}

	@Override
	public boolean test(Entity entity) {
		Level world = entity.level();
		return comparison.compare(world.getLightLevelDependentMagicValue(BlockPos.containing(entity.getEyePosition())), compareTo)
			|| comparison.compare(world.getLightLevelDependentMagicValue(entity.blockPosition()), compareTo);
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.BRIGHTNESS;
	}

}
