package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModifySlipperinessPower extends ModifierPower {
	private final ConditionTypeFactory<BlockInWorld> blockCondition;

	public ModifySlipperinessPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								   @Nullable Modifier modifier, @Nullable List<Modifier> modifiers, ConditionTypeFactory<BlockInWorld> blockCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority, modifier, modifiers);
		this.blockCondition = blockCondition;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("modify_slipperiness"), ModifierPower.getFactory().getSerializableData()
			.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null));
	}

	public boolean doesApply(LevelReader world, BlockPos pos, Entity entity) {
		return isActive(entity) && (blockCondition == null || blockCondition.test(new BlockInWorld(world, pos, true)));
	}
}
