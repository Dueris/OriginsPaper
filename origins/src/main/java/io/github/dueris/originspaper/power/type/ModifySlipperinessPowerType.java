package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.modifier.Modifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ModifySlipperinessPowerType extends ValueModifyingPowerType {

	public static final TypedDataObjectFactory<ModifySlipperinessPowerType> DATA_FACTORY = createConditionedModifyingDataFactory(
		new SerializableData()
			.add("block_condition", BlockCondition.DATA_TYPE.optional(), Optional.empty()),
		(data, modifiers, condition) -> new ModifySlipperinessPowerType(
			data.get("block_condition"),
			modifiers,
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("block_condition", powerType.blockCondition)
	);

	private final Optional<BlockCondition> blockCondition;

	public ModifySlipperinessPowerType(Optional<BlockCondition> blockCondition, List<Modifier> modifiers, Optional<EntityCondition> condition) {
		super(modifiers, condition);
		this.blockCondition = blockCondition;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.MODIFY_SLIPPERINESS;
	}

	public boolean doesApply(LevelReader worldView, BlockPos pos) {
		return worldView instanceof Level world && blockCondition
			.map(condition -> condition.test(world, pos))
			.orElse(true);
	}

}
