package io.github.dueris.originspaper.condition.type;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.type.entity.*;
import io.github.dueris.originspaper.condition.type.entity.meta.AllOfEntityConditionType;
import io.github.dueris.originspaper.condition.type.entity.meta.AnyOfEntityConditionType;
import io.github.dueris.originspaper.condition.type.entity.meta.ConstantEntityConditionType;
import io.github.dueris.originspaper.condition.type.entity.meta.RandomChanceEntityConditionType;
import io.github.dueris.originspaper.condition.type.meta.*;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;

public class EntityConditionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();
	public static final SerializableDataType<ConditionConfiguration<EntityConditionType>> DATA_TYPE = SerializableDataType.registry(ApoliRegistries.ENTITY_CONDITION_TYPE, "apoli", ALIASES, (configurations, id) -> "Entity condition type \"" + id + "\" is not undefined!");

	public static final ConditionConfiguration<AllOfEntityConditionType> ALL_OF = register(AllOfMetaConditionType.createConfiguration(EntityCondition.DATA_TYPE, AllOfEntityConditionType::new));
	public static final ConditionConfiguration<AnyOfEntityConditionType> ANY_OF = register(AnyOfMetaConditionType.createConfiguration(EntityCondition.DATA_TYPE, AnyOfEntityConditionType::new));
	public static final ConditionConfiguration<ConstantEntityConditionType> CONSTANT = register(ConstantMetaConditionType.createConfiguration(ConstantEntityConditionType::new));
	public static final ConditionConfiguration<RandomChanceEntityConditionType> RANDOM_CHANCE = register(RandomChanceMetaConditionType.createConfiguration(RandomChanceEntityConditionType::new));

	public static final ConditionConfiguration<AbilityEntityConditionType> ABILITY = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("ability"), AbilityEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<AdvancementEntityConditionType> ADVANCEMENT = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("advancement"), AdvancementEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<AirEntityConditionType> AIR = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("air"), AirEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<AttributeEntityConditionType> ATTRIBUTE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("attribute"), AttributeEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<BiomeEntityConditionType> BIOME = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("biome"), BiomeEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<BlockCollisionEntityConditionType> BLOCK_COLLISION = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("block_collision"), BlockCollisionEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<BlockInRadiusEntityConditionType> BLOCK_IN_RADIUS = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("block_in_radius"), BlockInRadiusEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<BrightnessEntityConditionType> BRIGHTNESS = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("brightness"), BrightnessEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<ClimbingEntityConditionType> CLIMBING = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("climbing"), ClimbingEntityConditionType::new));
	public static final ConditionConfiguration<CollidedHorizontallyEntityConditionType> COLLIDED_HORIZONTALLY = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("collided_horizontally"), CollidedHorizontallyEntityConditionType::new));
	public static final ConditionConfiguration<CommandEntityConditionType> COMMAND = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("command"), CommandEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<CreativeFlyingEntityConditionType> CREATIVE_FLYING = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("creative_flying"), CreativeFlyingEntityConditionType::new));
	public static final ConditionConfiguration<DayTimeEntityConditionType> DAY_TIME = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("daytime"), DayTimeEntityConditionType::new));
	public static final ConditionConfiguration<DimensionEntityConditionType> DIMENSION = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("dimension"), DimensionEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<DistanceFromCoordinatesEntityConditionType> DISTANCE_FROM_COORDINATES = register(DistanceFromCoordinatesMetaConditionType.createConfiguration(DistanceFromCoordinatesEntityConditionType::new));
	public static final ConditionConfiguration<ElytraFlightPossibleEntityConditionType> ELYTRA_FLIGHT_POSSIBLE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("elytra_flight_possible"), ElytraFlightPossibleEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<EnchantmentEntityConditionType> ENCHANTMENT = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("enchantment"), EnchantmentEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<EntityInRadiusEntityConditionType> ENTITY_IN_RADIUS = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("entity_in_radius"), EntityInRadiusEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<EntitySetSizeEntityConditionType> ENTITY_SET_SIZE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("entity_set_size"), EntitySetSizeEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<EntityTypeEntityConditionType> ENTITY_TYPE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("entity_type"), EntityTypeEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<EquippedItemEntityConditionType> EQUIPPED_ITEM = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("equipped_item"), EquippedItemEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<ExistsEntityConditionType> EXISTS = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("exists"), ExistsEntityConditionType::new));
	public static final ConditionConfiguration<ExposedToSkyEntityConditionType> EXPOSED_TO_SKY = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("exposed_to_sky"), ExposedToSkyEntityConditionType::new));
	public static final ConditionConfiguration<ExposedToSunEntityConditionType> EXPOSED_TO_SUN = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("exposed_to_sun"), ExposedToSunEntityConditionType::new));
	public static final ConditionConfiguration<FallDistanceEntityConditionType> FALL_DISTANCE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("fall_distance"), FallDistanceEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<FallFlyingEntityConditionType> FALL_FLYING = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("fall_flying"), FallFlyingEntityConditionType::new));
	public static final ConditionConfiguration<FluidHeightEntityConditionType> FLUID_HEIGHT = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("fluid_height"), FluidHeightEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<FoodLevelEntityConditionType> FOOD_LEVEL = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("food_level"), FoodLevelEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<GameModeEntityConditionType> GAME_MODE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("gamemode"), GameModeEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<GlowingEntityConditionType> GLOWING = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("glowing"), GlowingEntityConditionType::new));
	public static final ConditionConfiguration<HasCommandTagEntityConditionType> HAS_COMMAND_TAG = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("has_command_tag"), HasCommandTagEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<HealthEntityConditionType> HEALTH = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("health"), HealthEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<InBlockAnywhereEntityConditionType> IN_BLOCK_ANYWHERE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("in_block_anywhere"), InBlockAnywhereEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<InBlockEntityConditionType> IN_BLOCK = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("in_block"), InBlockEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<InRainEntityConditionType> IN_RAIN = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("in_rain"), InRainEntityConditionType::new));
	public static final ConditionConfiguration<InSnowEntityConditionType> IN_SNOW = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("in_snow"), InSnowEntityConditionType::new));
	public static final ConditionConfiguration<InTagEntityConditionType> IN_TAG = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("in_tag"), InTagEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<InThunderstormEntityConditionType> IN_THUNDERSTORM = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("in_thunderstorm"), InThunderstormEntityConditionType::new));
	public static final ConditionConfiguration<InventoryEntityConditionType> INVENTORY = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("inventory"), InventoryEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<InvisibleEntityConditionType> INVISIBLE = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("invisible"), InvisibleEntityConditionType::new));
	public static final ConditionConfiguration<LivingEntityConditionType> LIVING = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("living"), LivingEntityConditionType::new));
	public static final ConditionConfiguration<MovingEntityConditionType> MOVING = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("moving"), MovingEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<NbtEntityConditionType> NBT = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("nbt"), NbtEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<OnBlockEntityConditionType> ON_BLOCK = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("on_block"), OnBlockEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<OnFireEntityConditionType> ON_FIRE = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("on_fire"), OnFireEntityConditionType::new));
	public static final ConditionConfiguration<PassengerEntityConditionType> PASSENGER = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("passenger"), PassengerEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<PassengerRecursiveEntityConditionType> PASSENGER_RECURSIVE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("passenger_recursive"), PassengerRecursiveEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<PowerActiveEntityConditionType> POWER_ACTIVE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("power_active"), PowerActiveEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<PowerEntityConditionType> POWER = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("power"), PowerEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<PowerTypeEntityConditionType> POWER_TYPE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("power_type"), PowerTypeEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<PredicateEntityConditionType> PREDICATE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("predicate"), PredicateEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<RaycastEntityConditionType> RAYCAST = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("raycast"), RaycastEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<RelativeHealthEntityConditionType> RELATIVE_HEALTH = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("relative_health"), RelativeHealthEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<ResourceEntityConditionType> RESOURCE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("resource"), ResourceEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<RidingEntityConditionType> RIDING = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("riding"), RidingEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<RidingRecursiveEntityConditionType> RIDING_RECURSIVE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("riding_recursive"), RidingRecursiveEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<RidingRootEntityConditionType> RIDING_ROOT = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("riding_root"), RidingRootEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<SaturationLevelEntityConditionType> SATURATION_LEVEL = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("saturation_level"), SaturationLevelEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<ScoreboardEntityConditionType> SCOREBOARD = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("scoreboard"), ScoreboardEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<SneakingEntityConditionType> SNEAKING = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("sneaking"), SneakingEntityConditionType::new));
	public static final ConditionConfiguration<SprintingEntityConditionType> SPRINTING = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("sprinting"), SprintingEntityConditionType::new));
	public static final ConditionConfiguration<StatusEffectEntityConditionType> STATUS_EFFECT = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("status_effect"), StatusEffectEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<SubmergedInEntityConditionType> SUBMERGED_IN = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("submerged_in"), SubmergedInEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<SwimmingEntityConditionType> SWIMMING = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("swimming"), SwimmingEntityConditionType::new));
	public static final ConditionConfiguration<TamedEntityConditionType> TAMED = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("tamed"), TamedEntityConditionType::new));
	public static final ConditionConfiguration<TimeOfDayEntityConditionType> TIME_OF_DAY = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("time_of_day"), TimeOfDayEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<UsingEffectiveToolEntityConditionType> USING_EFFECTIVE_TOOL = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("using_effective_tool"), UsingEffectiveToolEntityConditionType::new));
	public static final ConditionConfiguration<UsingItemEntityConditionType> USING_ITEM = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("using_item"), UsingItemEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<XpLevelsEntityConditionType> XP_LEVELS = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("xp_levels"), XpLevelsEntityConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<XpPointsEntityConditionType> XP_POINTS = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("xp_points"), XpPointsEntityConditionType.DATA_FACTORY));

	public static void register() {

	}

	@SuppressWarnings("unchecked")
	public static <CT extends EntityConditionType> ConditionConfiguration<CT> register(ConditionConfiguration<CT> configuration) {

		ConditionConfiguration<EntityConditionType> casted = (ConditionConfiguration<EntityConditionType>) configuration;
		Registry.register(ApoliRegistries.ENTITY_CONDITION_TYPE, casted.id(), casted);

		return configuration;

	}

}
