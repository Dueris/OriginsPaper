package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.modifier.Modifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.List;
import java.util.function.Predicate;

public class ModifySlipperinessPowerType extends ValueModifyingPowerType {

	private final Predicate<BlockInWorld> blockCondition;

	public ModifySlipperinessPowerType(Power power, LivingEntity entity, Predicate<BlockInWorld> blockCondition, Modifier modifier, List<Modifier> modifiers) {
		super(power, entity);
		this.blockCondition = blockCondition;

		if (modifier != null) {
			this.addModifier(modifier);
		}

		if (modifiers != null) {
			modifiers.forEach(this::addModifier);
		}

	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("modify_slipperiness"),
			new SerializableData()
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("modifier", Modifier.DATA_TYPE, null)
				.add("modifiers", Modifier.LIST_TYPE, null),
			data -> (power, entity) -> new ModifySlipperinessPowerType(power, entity,
				data.get("block_condition"),
				data.get("modifier"),
				data.get("modifiers")
			)
		).allowCondition();
	}

	public boolean doesApply(LevelReader world, BlockPos pos) {
		return blockCondition == null || blockCondition.test(new BlockInWorld(world, pos, true));
	}

}
