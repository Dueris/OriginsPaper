package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.power.type.meta.DummyPowerType;
import io.github.dueris.originspaper.power.type.meta.MultiplePowerType;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;

public class PowerTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();
	public static final SerializableDataType<PowerConfiguration<PowerType>> DATA_TYPE = SerializableDataType.registry(ApoliRegistries.POWER_TYPE, "apoli", ALIASES, (configurations, id) -> "Power type \"" + id + "\" is undefined!");

	public static final PowerConfiguration<DummyPowerType> DUMMY = register(PowerConfiguration.conditionedSimple(OriginsPaper.apoliIdentifier("dummy"), DummyPowerType::new));
	public static final PowerConfiguration<MultiplePowerType> MULTIPLE = register(PowerConfiguration.simple(OriginsPaper.apoliIdentifier("multiple"), MultiplePowerType::new));

	public static final PowerConfiguration<ActionOnBeingUsedPowerType> ACTION_ON_BEING_USED = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("action_on_being_used"), ActionOnBeingUsedPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ActionOnBlockBreakPowerType> ACTION_ON_BLOCK_BREAK = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("action_on_block_break"), ActionOnBlockBreakPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ActionOnBlockPlacePowerType> ACTION_ON_BLOCK_PLACE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("action_on_block_place"), ActionOnBlockPlacePowerType.DATA_FACTORY));
	public static final PowerConfiguration<ActionOnBlockUsePowerType> ACTION_ON_BLOCK_USE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("action_on_block_use"), ActionOnBlockUsePowerType.DATA_FACTORY));
	public static final PowerConfiguration<ActionOnCallbackPowerType> ACTION_ON_CALLBACK = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("action_on_callback"), ActionOnCallbackPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ActionOnDeathPowerType> ACTION_ON_DEATH = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("action_on_death"), ActionOnDeathPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ActionOnEntityUsePowerType> ACTION_ON_ENTITY_USE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("action_on_entity_use"), ActionOnEntityUsePowerType.DATA_FACTORY));
	public static final PowerConfiguration<ActionOnHitPowerType> ACTION_ON_HIT = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("action_on_hit"), ActionOnHitPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ActionOnItemPickupPowerType> ACTION_ON_ITEM_PICKUP = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("action_on_item_pickup"), ActionOnItemPickupPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ActionOnItemUsePowerType> ACTION_ON_ITEM_USE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("action_on_item_use"), ActionOnItemUsePowerType.DATA_FACTORY));
	public static final PowerConfiguration<ActionOnLandPowerType> ACTION_ON_LAND = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("action_on_land"), ActionOnLandPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ActionOnWakeUpPowerType> ACTION_ON_WAKE_UP = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("action_on_wake_up"), ActionOnWakeUpPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ActionOverTimePowerType> ACTION_OVER_TIME = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("action_over_time"), ActionOverTimePowerType.DATA_FACTORY));
	public static final PowerConfiguration<ActionWhenDamageTakenPowerType> ACTION_WHEN_DAMAGE_TAKEN = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("action_when_damage_taken"), ActionWhenDamageTakenPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ActionWhenHitPowerType> ACTION_WHEN_HIT = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("action_when_hit"), ActionWhenHitPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ActionOnKeyPressPowerType> ACTION_ON_KEY_PRESS = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("active_self"), ActionOnKeyPressPowerType.DATA_FACTORY));
	public static final PowerConfiguration<AttackerActionWhenHitPowerType> ATTACKER_ACTION_WHEN_HIT = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("attacker_action_when_hit"), AttackerActionWhenHitPowerType.DATA_FACTORY));
	public static final PowerConfiguration<AttributeModifyTransferPowerType> ATTRIBUTE_MODIFY_TRANSFER = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("attribute_modify_transfer"), AttributeModifyTransferPowerType.DATA_FACTORY));
	public static final PowerConfiguration<AttributePowerType> ATTRIBUTE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("attribute"), AttributePowerType.DATA_FACTORY));
	public static final PowerConfiguration<BurnPowerType> BURN = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("burn"), BurnPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ClimbingPowerType> CLIMBING = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("climbing"), ClimbingPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ConditionedAttributePowerType> CONDITIONED_ATTRIBUTE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("conditioned_attribute"), ConditionedAttributePowerType.DATA_FACTORY));
	public static final PowerConfiguration<ConditionedRestrictArmorPowerType> CONDITIONED_RESTRICT_ARMOR = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("conditioned_restrict_armor"), ConditionedRestrictArmorPowerType.DATA_FACTORY));
	public static final PowerConfiguration<CooldownPowerType> COOLDOWN = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("cooldown"), CooldownPowerType.DATA_FACTORY));
	public static final PowerConfiguration<CreativeFlightPowerType> CREATIVE_FLIGHT = register(PowerConfiguration.conditionedSimple(OriginsPaper.apoliIdentifier("creative_flight"), CreativeFlightPowerType::new));
	public static final PowerConfiguration<DamageOverTimePowerType> DAMAGE_OVER_TIME = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("damage_over_time"), DamageOverTimePowerType.DATA_FACTORY));
	public static final PowerConfiguration<DisableRegenPowerType> DISABLE_REGEN = register(PowerConfiguration.conditionedSimple(OriginsPaper.apoliIdentifier("disable_regen"), DisableRegenPowerType::new));
	public static final PowerConfiguration<EdibleItemPowerType> EDIBLE_ITEM = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("edible_item"), EdibleItemPowerType.DATA_FACTORY));
	public static final PowerConfiguration<EffectImmunityPowerType> EFFECT_IMMUNITY = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("effect_immunity"), EffectImmunityPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ElytraFlightPowerType> ELYTRA_FLIGHT = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("elytra_flight"), ElytraFlightPowerType.DATA_FACTORY));
	public static final PowerConfiguration<EntityGlowPowerType> ENTITY_GLOW = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("entity_glow"), EntityGlowPowerType.DATA_FACTORY));
	public static final PowerConfiguration<EntitySetPowerType> ENTITY_SET = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("entity_set"), EntitySetPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ExhaustOverTimePowerType> EXHAUST = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("exhaust"), ExhaustOverTimePowerType.DATA_FACTORY));
	public static final PowerConfiguration<FireImmunityPowerType> FIRE_IMMUNITY = register(PowerConfiguration.conditionedSimple(OriginsPaper.apoliIdentifier("fire_immunity"), FireImmunityPowerType::new));
	public static final PowerConfiguration<FireProjectilePowerType> FIRE_PROJECTILE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("fire_projectile"), FireProjectilePowerType.DATA_FACTORY));
	public static final PowerConfiguration<FreezePowerType> FREEZE = register(PowerConfiguration.conditionedSimple(OriginsPaper.apoliIdentifier("freeze"), FreezePowerType::new));
	public static final PowerConfiguration<GameEventListenerPowerType> GAME_EVENT_LISTENER = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("game_event_listener"), GameEventListenerPowerType.DATA_FACTORY));
	public static final PowerConfiguration<GroundedPowerType> GROUNDED = register(PowerConfiguration.conditionedSimple(OriginsPaper.apoliIdentifier("grounded"), GroundedPowerType::new));
	//	public static final PowerConfiguration<IgnoreWaterPowerType> IGNORE_WATER = register(PowerConfiguration.conditionedSimple(OriginsPaper.apoliIdentifier("ignore_water"), IgnoreWaterPowerType::new));
	public static final PowerConfiguration<InventoryPowerType> INVENTORY = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("inventory"), InventoryPowerType.DATA_FACTORY));
	public static final PowerConfiguration<InvisibilityPowerType> INVISIBILITY = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("invisibility"), InvisibilityPowerType.DATA_FACTORY));
	public static final PowerConfiguration<InvulnerabilityPowerType> INVULNERABILITY = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("invulnerability"), InvulnerabilityPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ItemOnItemPowerType> ITEM_ON_ITEM = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("item_on_item"), ItemOnItemPowerType.DATA_FACTORY));
	public static final PowerConfiguration<KeepInventoryPowerType> KEEP_INVENTORY = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("keep_inventory"), KeepInventoryPowerType.DATA_FACTORY));
	public static final PowerConfiguration<LaunchPowerType> LAUNCH = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("launch"), LaunchPowerType.DATA_FACTORY));
	//	public static final PowerConfiguration<LavaVisionPowerType> LAVA_VISION = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("lava_vision"), LavaVisionPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModelColorPowerType> MODEL_COLOR = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("model_color"), ModelColorPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModifyAirSpeedPowerType> MODIFY_AIR_SPEED = register(ValueModifyingPowerType.createModifyingConfiguration(OriginsPaper.apoliIdentifier("modify_air_speed"), ModifyAirSpeedPowerType::new));
	public static final PowerConfiguration<ModifyAttributePowerType> MODIFY_ATTRIBUTE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_attribute"), ModifyAttributePowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModifyBlockRenderPowerType> MODIFY_BLOCK_RENDER = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_block_render"), ModifyBlockRenderPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModifyBreakSpeedPowerType> MODIFY_BREAK_SPEED = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_break_speed"), ModifyBreakSpeedPowerType.DATA_FACTORY));
	//	public static final PowerConfiguration<ModifyCameraSubmersionTypePowerType> MODIFY_CAMERA_SUBMERSION = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_camera_submersion"), ModifyCameraSubmersionTypePowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModifyCraftingPowerType> MODIFY_CRAFTING = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_crafting"), ModifyCraftingPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModifyDamageDealtPowerType> MODIFY_DAMAGE_DEALT = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_damage_dealt"), ModifyDamageDealtPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModifyDamageTakenPowerType> MODIFY_DAMAGE_TAKEN = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_damage_taken"), ModifyDamageTakenPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModifyEnchantmentLevelPowerType> MODIFY_ENCHANTMENT_LEVEL = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_enchantment_level"), ModifyEnchantmentLevelPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModifyExhaustionPowerType> MODIFY_EXHAUSTION = register(ValueModifyingPowerType.createModifyingConfiguration(OriginsPaper.apoliIdentifier("modify_exhaustion"), ModifyExhaustionPowerType::new));
	public static final PowerConfiguration<ModifyExperiencePowerType> MODIFY_EXPERIENCE = register(ValueModifyingPowerType.createModifyingConfiguration(OriginsPaper.apoliIdentifier("modify_xp_gain"), ModifyExperiencePowerType::new));
	public static final PowerConfiguration<ModifyFallingPowerType> MODIFY_FALLING = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_falling"), ModifyFallingPowerType.DATA_FACTORY));
	//	public static final PowerConfiguration<ModifyFluidRenderPowerType> MODIFY_FLUID_RENDER = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_fluid_render"), ModifyFluidRenderPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModifyFoodPowerType> MODIFY_FOOD = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_food"), ModifyFoodPowerType.DATA_FACTORY));
	//	public static final PowerConfiguration<ModifyFovPowerType> MODIFY_FOV = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_fov"), ModifyFovPowerType.DATA_FACTORY));
//	public static final PowerConfiguration<ModifyGrindstonePowerType> MODIFY_GRINDSTONE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_grindstone"), ModifyGrindstonePowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModifyHarvestPowerType> MODIFY_HARVEST = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_harvest"), ModifyHarvestPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModifyHealingPowerType> MODIFY_HEALING = register(ValueModifyingPowerType.createModifyingConfiguration(OriginsPaper.apoliIdentifier("modify_healing"), ModifyHealingPowerType::new));
	public static final PowerConfiguration<ModifyInsomniaTicksPowerType> MODIFY_INSOMNIA_TICKS = register(ValueModifyingPowerType.createModifyingConfiguration(OriginsPaper.apoliIdentifier("modify_insomnia_ticks"), ModifyInsomniaTicksPowerType::new));
	public static final PowerConfiguration<ModifyJumpPowerType> MODIFY_JUMP = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_jump"), ModifyJumpPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModifyLavaSpeedPowerType> MODIFY_LAVA_SPEED = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_lava_speed"), ModifyLavaSpeedPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModifyPlayerSpawnPowerType> MODIFY_PLAYER_SPAWN = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_player_spawn"), ModifyPlayerSpawnPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModifyProjectileDamagePowerType> MODIFY_PROJECTILE_DAMAGE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_projectile_damage"), ModifyProjectileDamagePowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModifySlipperinessPowerType> MODIFY_SLIPPERINESS = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_slipperiness"), ModifySlipperinessPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModifyStatusEffectAmplifierPowerType> MODIFY_STATUS_EFFECT_AMPLIFIER = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_status_effect_amplifier"), ModifyStatusEffectAmplifierPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModifyStatusEffectDurationPowerType> MODIFY_STATUS_EFFECT_DURATION = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_status_effect_duration"), ModifyStatusEffectDurationPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModifySwimSpeedPowerType> MODIFY_SWIM_SPEED = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_swim_speed"), ModifySwimSpeedPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ModifyTypeTagPowerType> MODIFY_TYPE_TAG = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_type_tag"), ModifyTypeTagPowerType.DATA_FACTORY));
	//	public static final PowerConfiguration<ModifyVelocityPowerType> MODIFY_VELOCITY = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("modify_velocity"), ModifyVelocityPowerType.DATA_FACTORY));
	public static final PowerConfiguration<NightVisionPowerType> NIGHT_VISION = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("night_vision"), NightVisionPowerType.DATA_FACTORY));
	//	public static final PowerConfiguration<OverlayPowerType> OVERLAY = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("overlay"), OverlayPowerType.DATA_FACTORY));
//	public static final PowerConfiguration<OverrideHudTexturePowerType> STATUS_BAR_TEXTURE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("status_bar_texture"), OverrideHudTexturePowerType.DATA_FACTORY));
	public static final PowerConfiguration<ParticlePowerType> PARTICLE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("particle"), ParticlePowerType.DATA_FACTORY));
	public static final PowerConfiguration<PhasingPowerType> PHASING = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("phasing"), PhasingPowerType.DATA_FACTORY));
	//	public static final PowerConfiguration<PosePowerType> POSE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("pose"), PosePowerType.DATA_FACTORY));
	public static final PowerConfiguration<PreventBeingUsedPowerType> PREVENT_BEING_USED = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("prevent_being_used"), PreventBeingUsedPowerType.DATA_FACTORY));
	public static final PowerConfiguration<PreventBlockPlacePowerType> PREVENT_BLOCK_PLACE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("prevent_block_place"), PreventBlockPlacePowerType.DATA_FACTORY));
	//	public static final PowerConfiguration<PreventBlockSelectionPowerType> PREVENT_BLOCK_SELECTION = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("prevent_block_selection"), PreventBlockSelectionPowerType.DATA_FACTORY));
	public static final PowerConfiguration<PreventBlockUsePowerType> PREVENT_BLOCK_USE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("prevent_block_use"), PreventBlockUsePowerType.DATA_FACTORY));
	public static final PowerConfiguration<PreventDeathPowerType> PREVENT_DEATH = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("prevent_death"), PreventDeathPowerType.DATA_FACTORY));
	public static final PowerConfiguration<PreventElytraFlightPowerType> PREVENT_ELYTRA_FLIGHT = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("prevent_elytra_flight"), PreventElytraFlightPowerType.DATA_FACTORY));
	public static final PowerConfiguration<PreventEntityCollisionPowerType> PREVENT_ENTITY_COLLISION = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("prevent_entity_collision"), PreventEntityCollisionPowerType.DATA_FACTORY));
	public static final PowerConfiguration<PreventEntityRenderPowerType> PREVENT_ENTITY_RENDER = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("prevent_entity_render"), PreventEntityRenderPowerType.DATA_FACTORY));
	//	public static final PowerConfiguration<PreventEntitySelectionPowerType> PREVENT_ENTITY_SELECTION = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("prevent_entity_selection"), PreventEntitySelectionPowerType.DATA_FACTORY));
	public static final PowerConfiguration<PreventEntityUsePowerType> PREVENT_ENTITY_USE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("prevent_entity_use"), PreventEntityUsePowerType.DATA_FACTORY));
	//	public static final PowerConfiguration<PreventFeatureRenderPowerType> PREVENT_FEATURE_RENDER = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("prevent_feature_render"), PreventFeatureRenderPowerType.DATA_FACTORY));
	public static final PowerConfiguration<PreventGameEventPowerType> PREVENT_GAME_EVENT = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("prevent_game_event"), PreventGameEventPowerType.DATA_FACTORY));
	public static final PowerConfiguration<PreventItemPickupPowerType> PREVENT_ITEM_PICKUP = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("prevent_item_pickup"), PreventItemPickupPowerType.DATA_FACTORY));
	public static final PowerConfiguration<PreventItemUsePowerType> PREVENT_ITEM_USE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("prevent_item_use"), PreventItemUsePowerType.DATA_FACTORY));
	public static final PowerConfiguration<PreventSleepPowerType> PREVENT_SLEEP = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("prevent_sleep"), PreventSleepPowerType.DATA_FACTORY));
	public static final PowerConfiguration<PreventSprintingPowerType> PREVENT_SPRINTING = register(PowerConfiguration.conditionedSimple(OriginsPaper.apoliIdentifier("prevent_sprinting"), PreventSprintingPowerType::new));
	public static final PowerConfiguration<RecipePowerType> RECIPE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("recipe"), RecipePowerType.DATA_FACTORY));
	public static final PowerConfiguration<ReplaceLootTablePowerType> REPLACE_LOOT_TABLE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("replace_loot_table"), ReplaceLootTablePowerType.DATA_FACTORY));
	public static final PowerConfiguration<ResourcePowerType> RESOURCE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("resource"), ResourcePowerType.DATA_FACTORY));
	public static final PowerConfiguration<RestrictArmorPowerType> RESTRICT_ARMOR = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("restrict_armor"), RestrictArmorPowerType.DATA_FACTORY));
	public static final PowerConfiguration<SelfActionOnHitPowerType> SELF_ACTION_ON_HIT = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("self_action_on_hit"), SelfActionOnHitPowerType.DATA_FACTORY));
	public static final PowerConfiguration<SelfActionOnKillPowerType> SELF_ACTION_ON_KILL = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("self_action_on_kill"), SelfActionOnKillPowerType.DATA_FACTORY));
	public static final PowerConfiguration<SelfGlowPowerType> SELF_GLOW = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("self_glow"), SelfGlowPowerType.DATA_FACTORY));
	//	public static final PowerConfiguration<ShaderPowerType> SHADER = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("shader"), ShaderPowerType.DATA_FACTORY));
//	public static final PowerConfiguration<ShakingPowerType> SHAKING = register(PowerConfiguration.conditionedSimple(OriginsPaper.apoliIdentifier("shaking"), ShakingPowerType::new));
//	public static final PowerConfiguration<SprintingPowerType> SPRINTING = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("sprinting"), SprintingPowerType.DATA_FACTORY));
	public static final PowerConfiguration<StackingStatusEffectPowerType> STACKING_STATUS_EFFECT = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("stacking_status_effect"), StackingStatusEffectPowerType.DATA_FACTORY));
	public static final PowerConfiguration<StartingEquipmentPowerType> STARTING_EQUIPMENT = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("starting_equipment"), StartingEquipmentPowerType.DATA_FACTORY));
	//	public static final PowerConfiguration<SwimmingPowerType> SWIMMING = register(PowerConfiguration.conditionedSimple(OriginsPaper.apoliIdentifier("swimming"), SwimmingPowerType::new));
	public static final PowerConfiguration<TargetActionOnHitPowerType> TARGET_ACTION_ON_HIT = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("target_action_on_hit"), TargetActionOnHitPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ToggleNightVisionPowerType> TOGGLE_NIGHT_VISION = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("toggle_night_vision"), ToggleNightVisionPowerType.DATA_FACTORY));
	public static final PowerConfiguration<TogglePowerType> TOGGLE = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("toggle"), TogglePowerType.DATA_FACTORY));
//	public static final PowerConfiguration<TooltipPowerType> TOOLTIP = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("tooltip"), TooltipPowerType.DATA_FACTORY));
//	public static final PowerConfiguration<WalkOnFluidPowerType> WALK_ON_FLUID = register(PowerConfiguration.dataFactory(OriginsPaper.apoliIdentifier("walk_on_fluid"), WalkOnFluidPowerType.DATA_FACTORY));

	public static void register() {
		ALIASES.addPathAlias("simple", DUMMY.id().getPath());
		ALIASES.addPathAlias("self_action_when_hit", ACTION_WHEN_DAMAGE_TAKEN.id().getPath());
		ALIASES.addPathAlias("active_self", ACTION_ON_KEY_PRESS.id().getPath());
	}

	@SuppressWarnings("unchecked")
	public static <T extends PowerType> PowerConfiguration<T> register(PowerConfiguration<T> configuration) {

		PowerConfiguration<PowerType> casted = (PowerConfiguration<PowerType>) configuration;
		Registry.register(ApoliRegistries.POWER_TYPE, casted.id(), casted);

		return configuration;

	}

}
