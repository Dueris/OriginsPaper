package io.github.dueris.originspaper.action.type;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.action.type.bientity.*;
import io.github.dueris.originspaper.action.type.bientity.meta.*;
import io.github.dueris.originspaper.action.type.meta.*;
import io.github.dueris.originspaper.condition.BiEntityCondition;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;

public class BiEntityActionTypes {

    public static final IdentifierAlias ALIASES = new IdentifierAlias();
    public static final SerializableDataType<ActionConfiguration<BiEntityActionType>> DATA_TYPE = SerializableDataType.registry(ApoliRegistries.BIENTITY_ACTION_TYPE, "apoli", ALIASES, (configurations, id) -> "Bi-entity action type \"" + id + "\" is undefined!");

    public static final ActionConfiguration<AndBiEntityActionType> AND = register(AndMetaActionType.createConfiguration(BiEntityAction.DATA_TYPE, AndBiEntityActionType::new));
    public static final ActionConfiguration<ChanceBiEntityActionType> CHANCE = register(ChanceMetaActionType.createConfiguration(BiEntityAction.DATA_TYPE, ChanceBiEntityActionType::new));
    public static final ActionConfiguration<ChoiceBiEntityActionType> CHOICE = register(ChoiceMetaActionType.createConfiguration(BiEntityAction.DATA_TYPE, ChoiceBiEntityActionType::new));
    public static final ActionConfiguration<DelayBiEntityActionType> DELAY = register(DelayMetaActionType.createConfiguration(BiEntityAction.DATA_TYPE, DelayBiEntityActionType::new));
    public static final ActionConfiguration<IfElseListBiEntityActionType> IF_ELSE_LIST = register(IfElseListMetaActionType.createConfiguration(BiEntityAction.DATA_TYPE, BiEntityCondition.DATA_TYPE, IfElseListBiEntityActionType::new));
    public static final ActionConfiguration<IfElseBiEntityActionType> IF_ELSE = register(IfElseMetaActionType.createConfiguration(BiEntityAction.DATA_TYPE, BiEntityCondition.DATA_TYPE, IfElseBiEntityActionType::new));
    public static final ActionConfiguration<NothingBiEntityActionType> NOTHING = register(NothingMetaActionType.createConfiguration(NothingBiEntityActionType::new));
    public static final ActionConfiguration<SideBiEntityActionType> SIDE = register(SideMetaActionType.createConfiguration(BiEntityAction.DATA_TYPE, SideBiEntityActionType::new));

    public static final ActionConfiguration<ActorActionBiEntityActionType> ACTOR_ACTION = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("actor_action"), ActorActionBiEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<InvertBiEntityActionType> INVERT = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("invert"), InvertBiEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<TargetActionBiEntityActionType> TARGET_ACTION = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("target_action"), TargetActionBiEntityActionType.DATA_FACTORY));

    public static final ActionConfiguration<AddToEntitySetBiEntityActionType> ADD_TO_ENTITY_SET = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("add_to_entity_set"), AddToEntitySetBiEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<AddVelocityBiEntityActionType> ADD_VELOCITY = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("add_velocity"), AddVelocityBiEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<DamageBiEntityActionType> DAMAGE = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("damage"), DamageBiEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<LeashBiEntityActionType> LEASH = register(ActionConfiguration.simple(OriginsPaper.apoliIdentifier("leash"), LeashBiEntityActionType::new));
    public static final ActionConfiguration<MountBiEntityActionType> MOUNT = register(ActionConfiguration.simple(OriginsPaper.apoliIdentifier("mount"), MountBiEntityActionType::new));
    public static final ActionConfiguration<RemoveFromEntitySetBiEntityActionType> REMOVE_FROM_ENTITY_SET = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("remove_from_entity_set"), RemoveFromEntitySetBiEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<TameBiEntityActionType> TAME = ActionConfiguration.simple(OriginsPaper.apoliIdentifier("tame"), TameBiEntityActionType::new);
    public static final ActionConfiguration<SetInLoveBiEntityActionType> SET_IN_LOVE = register(ActionConfiguration.simple(OriginsPaper.apoliIdentifier("set_in_love"), SetInLoveBiEntityActionType::new));

    public static void register() {

    }

    @SuppressWarnings("unchecked")
	public static <T extends BiEntityActionType> ActionConfiguration<T> register(ActionConfiguration<T> configuration) {

        ActionConfiguration<BiEntityActionType> casted = (ActionConfiguration<BiEntityActionType>) configuration;
        Registry.register(ApoliRegistries.BIENTITY_ACTION_TYPE, casted.id(), casted);

        return configuration;

    }

}
