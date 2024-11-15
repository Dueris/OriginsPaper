package io.github.dueris.originspaper.action.type;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BlockAction;
import io.github.dueris.originspaper.action.type.block.*;
import io.github.dueris.originspaper.action.type.block.meta.*;
import io.github.dueris.originspaper.action.type.meta.*;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;

public class BlockActionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();
	public static final SerializableDataType<ActionConfiguration<BlockActionType>> DATA_TYPE = SerializableDataType.registry(ApoliRegistries.BLOCK_ACTION_TYPE, "apoli", ALIASES, (configurations, id) -> "Block action type \"" + id + "\" is undefined!");

	public static final ActionConfiguration<AndBlockActionType> AND = register(AndMetaActionType.createConfiguration(BlockAction.DATA_TYPE, AndBlockActionType::new));
	public static final ActionConfiguration<ChanceBlockActionType> CHANCE = register(ChanceMetaActionType.createConfiguration(BlockAction.DATA_TYPE, ChanceBlockActionType::new));
	public static final ActionConfiguration<ChoiceBlockActionType> CHOICE = register(ChoiceMetaActionType.createConfiguration(BlockAction.DATA_TYPE, ChoiceBlockActionType::new));
	public static final ActionConfiguration<DelayBlockActionType> DELAY = register(DelayMetaActionType.createConfiguration(BlockAction.DATA_TYPE, DelayBlockActionType::new));
	public static final ActionConfiguration<IfElseListBlockActionType> IF_ELSE_LIST = register(IfElseListMetaActionType.createConfiguration(BlockAction.DATA_TYPE, BlockCondition.DATA_TYPE, IfElseListBlockActionType::new));
	public static final ActionConfiguration<IfElseBlockActionType> IF_ELSE = register(IfElseMetaActionType.createConfiguration(BlockAction.DATA_TYPE, BlockCondition.DATA_TYPE, IfElseBlockActionType::new));
	public static final ActionConfiguration<NothingBlockActionType> NOTHING = register(NothingMetaActionType.createConfiguration(NothingBlockActionType::new));
	public static final ActionConfiguration<SideBlockActionType> SIDE = register(SideMetaActionType.createConfiguration(BlockAction.DATA_TYPE, SideBlockActionType::new));

	public static final ActionConfiguration<OffsetBlockActionType> OFFSET = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("offset"), OffsetBlockActionType.DATA_FACTORY));

	public static final ActionConfiguration<AddBlockBlockActionType> ADD_BLOCK = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("add_block"), AddBlockBlockActionType.DATA_FACTORY));
	public static final ActionConfiguration<AreaOfEffectBlockActionType> AREA_OF_EFFECT = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("area_of_effect"), AreaOfEffectBlockActionType.DATA_FACTORY));
	public static final ActionConfiguration<BoneMealBlockActionType> BONE_MEAL = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("bonemeal"), BoneMealBlockActionType.DATA_FACTORY));
	public static final ActionConfiguration<ExecuteCommandBlockActionType> EXECUTE_COMMAND = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("execute_command"), ExecuteCommandBlockActionType.DATA_FACTORY));
	public static final ActionConfiguration<ExplodeBlockActionType> EXPLODE = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("explode"), ExplodeBlockActionType.DATA_FACTORY));
	public static final ActionConfiguration<ModifyBlockStateBlockActionType> MODIFY_BLOCK_STATE = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("modify_block_state"), ModifyBlockStateBlockActionType.DATA_FACTORY));
	public static final ActionConfiguration<SetBlockBlockActionType> SET_BLOCK = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("set_block"), SetBlockBlockActionType.DATA_FACTORY));
	public static final ActionConfiguration<SpawnEntityBlockActionType> SPAWN_ENTITY = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("spawn_entity"), SpawnEntityBlockActionType.DATA_FACTORY));

	public static void register() {

	}

	@SuppressWarnings("unchecked")
	public static <T extends BlockActionType> ActionConfiguration<T> register(ActionConfiguration<T> configuration) {

		ActionConfiguration<BlockActionType> casted = (ActionConfiguration<BlockActionType>) configuration;
		Registry.register(ApoliRegistries.BLOCK_ACTION_TYPE, casted.id(), casted);

		return configuration;

	}

}
