package io.github.dueris.originspaper.action.type;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.action.type.entity.*;
import io.github.dueris.originspaper.action.type.entity.meta.*;
import io.github.dueris.originspaper.action.type.meta.*;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;

public class EntityActionTypes {

    public static final IdentifierAlias ALIASES = new IdentifierAlias();
    public static final SerializableDataType<ActionConfiguration<EntityActionType>> DATA_TYPE = SerializableDataType.registry(ApoliRegistries.ENTITY_ACTION_TYPE, "apoli", ALIASES, (configurations, id) -> "Entity action type \"" + id + "\" is undefined!");

    public static final ActionConfiguration<AndEntityActionType> AND = register(AndMetaActionType.createConfiguration(EntityAction.DATA_TYPE, AndEntityActionType::new));
    public static final ActionConfiguration<ChanceEntityActionType> CHANCE = register(ChanceMetaActionType.createConfiguration(EntityAction.DATA_TYPE, ChanceEntityActionType::new));
    public static final ActionConfiguration<ChoiceEntityActionType> CHOICE = register(ChoiceMetaActionType.createConfiguration(EntityAction.DATA_TYPE, ChoiceEntityActionType::new));
    public static final ActionConfiguration<DelayEntityActionType> DELAY = register(DelayMetaActionType.createConfiguration(EntityAction.DATA_TYPE, DelayEntityActionType::new));
    public static final ActionConfiguration<IfElseListEntityActionType> IF_ELSE_LIST = register(IfElseListMetaActionType.createConfiguration(EntityAction.DATA_TYPE, EntityCondition.DATA_TYPE, IfElseListEntityActionType::new));
    public static final ActionConfiguration<IfElseEntityActionType> IF_ELSE = register(IfElseMetaActionType.createConfiguration(EntityAction.DATA_TYPE, EntityCondition.DATA_TYPE, IfElseEntityActionType::new));
    public static final ActionConfiguration<SideEntityActionType> SIDE = register(SideMetaActionType.createConfiguration(EntityAction.DATA_TYPE, SideEntityActionType::new));

    public static final ActionConfiguration<ActionOnEntitySetEntityActionType> ACTION_ON_ENTITY_SET = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("action_on_entity_set"), ActionOnEntitySetEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<AddVelocityEntityActionType> ADD_VELOCITY = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("add_velocity"), AddVelocityEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<AddXpEntityActionType> ADD_XP = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("add_xp"), AddXpEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<ApplyEffectEntityActionType> APPLY_EFFECT = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("apply_effect"), ApplyEffectEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<AreaOfEffectEntityActionType> AREA_OF_EFFECT = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("area_of_effect"), AreaOfEffectEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<BlockActionAtEntityActionType> BLOCK_ACTION_AT = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("block_action_at"), BlockActionAtEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<ChangeResourceEntityActionType> CHANGE_RESOURCE = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("change_resource"), ChangeResourceEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<ClearEffectEntityActionType> CLEAR_EFFECT = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("clear_effect"), ClearEffectEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<CraftingTableEntityActionType> CRAFTING_TABLE = register(ActionConfiguration.simple(OriginsPaper.apoliIdentifier("crafting_table"), CraftingTableEntityActionType::new));
    public static final ActionConfiguration<DamageEntityActionType> DAMAGE = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("damage"), DamageEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<DismountEntityActionType> DISMOUNT = register(ActionConfiguration.simple(OriginsPaper.apoliIdentifier("dismount"), DismountEntityActionType::new));
    public static final ActionConfiguration<DropInventoryEntityActionType> DROP_INVENTORY = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("drop_inventory"), DropInventoryEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<EmitGameEventEntityActionType> EMIT_GAME_EVENT = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("emit_game_event"), EmitGameEventEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<EnderChestEntityActionType> ENDER_CHEST = register(ActionConfiguration.simple(OriginsPaper.apoliIdentifier("ender_chest"), EnderChestEntityActionType::new));
    public static final ActionConfiguration<EquippedItemActionEntityActionType> EQUIPPED_ITEM_ACTION = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("equipped_item_action"), EquippedItemActionEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<ExecuteCommandEntityActionType> EXECUTE_COMMAND = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("execute_command"), ExecuteCommandEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<ExhaustEntityActionType> EXHAUST = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("exhaust"), ExhaustEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<ExplodeEntityActionType> EXPLODE = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("explode"), ExplodeEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<ExtinguishEntityActionType> EXTINGUISH = register(ActionConfiguration.simple(OriginsPaper.apoliIdentifier("extinguish"), ExtinguishEntityActionType::new));
    public static final ActionConfiguration<FeedEntityActionType> FEED = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("feed"), FeedEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<FireProjectileEntityActionType> FIRE_PROJECTILE = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("fire_projectile"), FireProjectileEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<GainAirEntityActionType> GAIN_AIR = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("gain_air"), GainAirEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<GiveEntityActionType> GIVE = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("give"), GiveEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<GrantAdvancementEntityActionType> GRANT_ADVANCEMENT = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("grant_advancement"), GrantAdvancementEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<GrantPowerEntityActionType> GRANT_POWER = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("grant_power"), GrantPowerEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<HealEntityActionType> HEAL = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("heal"), HealEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<ModifyDeathTicksEntityActionType> MODIFY_DEATH_TICKS = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("modify_death_ticks"), ModifyDeathTicksEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<ModifyInventoryEntityActionType> MODIFY_INVENTORY = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("modify_inventory"), ModifyInventoryEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<ModifyResourceEntityActionType> MODIFY_RESOURCE = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("modify_resource"), ModifyResourceEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<ModifyStatEntityActionType> MODIFY_STAT = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("modify_stat"), ModifyStatEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<PassengerActionEntityActionType> PASSENGER = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("passenger_action"), PassengerActionEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<PlaySoundEntityActionType> PLAY_SOUND = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("play_sound"), PlaySoundEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<RandomTeleportEntityActionType> RANDOM_TELEPORT = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("random_teleport"), RandomTeleportEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<RaycastEntityActionType> RAYCAST = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("raycast"), RaycastEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<RemovePowerEntityActionType> REMOVE_POWER = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("remove_power"), RemovePowerEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<ReplaceInventoryEntityActionType> REPLACE_INVENTORY = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("replace_inventory"), ReplaceInventoryEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<RevokeAdvancementEntityActionType> REVOKE_ADVANCEMENT = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("revoke_advancement"), RevokeAdvancementEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<RevokeAllPowersEntityActionType> REVOKE_ALL_POWERS = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("revoke_all_powers"), RevokeAllPowersEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<RevokePowerEntityActionType> REVOKE_POWER = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("revoke_power"), RevokePowerEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<RidingActionEntityActionType> RIDING_ACTION = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("riding_action"), RidingActionEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<SelectorActionEntityActionType> SELECTOR_ACTION = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("selector_action"), SelectorActionEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<SetFallDistanceEntityActionType> SET_FALL_DISTANCE = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("set_fall_distance"), SetFallDistanceEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<SetOnFireEntityActionType> SET_ON_FIRE = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("set_on_fire"), SetOnFireEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<SetResourceEntityActionType> SET_RESOURCE = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("set_resource"), SetResourceEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<ShowToastEntityActionType> SHOW_TOAST = register(ActionConfiguration.simple(OriginsPaper.apoliIdentifier("show_toast"), ShowToastEntityActionType::new)); // OriginsPaper
    public static final ActionConfiguration<SpawnEffectCloudEntityActionType> SPAWN_EFFECT_CLOUD = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("spawn_effect_cloud"), SpawnEffectCloudEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<SpawnEntityEntityActionType> SPAWN_ENTITY = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("spawn_entity"), SpawnEntityEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<SpawnParticlesEntityActionType> SPAWN_PARTICLES = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("spawn_particles"), SpawnParticlesEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<SwingHandEntityActionType> SWING_HAND = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("swing_hand"), SwingHandEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<ToggleEntityActionType> TOGGLE = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("toggle"), ToggleEntityActionType.DATA_FACTORY));
    public static final ActionConfiguration<TriggerCooldownEntityActionType> TRIGGER_COOLDOWN = register(ActionConfiguration.of(OriginsPaper.apoliIdentifier("trigger_cooldown"), TriggerCooldownEntityActionType.DATA_FACTORY));

    public static void register() {

    }

    @SuppressWarnings("unchecked")
	public static <T extends EntityActionType> ActionConfiguration<T> register(ActionConfiguration<T> configuration) {

        ActionConfiguration<EntityActionType> casted = (ActionConfiguration<EntityActionType>) configuration;
        Registry.register(ApoliRegistries.ENTITY_ACTION_TYPE, casted.id(), casted);

        return configuration;

    }

}
