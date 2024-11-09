package io.github.dueris.originspaper.condition.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.condition.factory.DistanceFromCoordinatesConditionRegistry;
import io.github.dueris.originspaper.condition.type.entity.*;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import io.github.dueris.originspaper.util.Comparison;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;
import java.util.function.Predicate;

public class EntityConditionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static void register() {
		MetaConditionTypes.register(ApoliDataTypes.ENTITY_CONDITION, EntityConditionTypes::register);
		register(BlockCollisionConditionType.getFactory());
		register(BrightnessConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("daytime"), entity -> TimeOfDayConditionType.condition(entity.level(), Comparison.LESS_THAN, 13000)));
		register(TimeOfDayConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("fall_flying"), entity -> entity instanceof LivingEntity living && living.isFallFlying()));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("exposed_to_sun"), ExposedToSunConditionType::condition));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("in_rain"), InRainConditionType::condition));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("invisible"), Entity::isInvisible));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("on_fire"), Entity::isOnFire));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("exposed_to_sky"), ExposedToSkyConditionType::condition));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("sneaking"), Entity::isShiftKeyDown));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("sprinting"), Entity::isSprinting));
		register(PowerActiveConditionType.getFactory());
		register(SubmergedInConditionType.getFactory());
		register(FluidHeightConditionType.getFactory());
		register(PowerConditionType.getFactory());
		register(FoodLevelConditionType.getFactory());
		register(SaturationLevelConditionType.getFactory());
		register(OnBlockConditionType.getFactory());
		register(EquippedConditionType.getFactory());
		register(AttributeConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("swimming"), Entity::isSwimming));
		register(ResourceConditionType.getFactory());
		register(AirConditionType.getFactory());
		register(InBlockConditionType.getFactory());
		register(BlockInRadiusConditionType.getFactory());
		DistanceFromCoordinatesConditionRegistry.registerEntityCondition(EntityConditionTypes::register);
		register(DimensionConditionType.getFactory());
		register(XpLevelsConditionType.getFactory());
		register(XpPointsConditionType.getFactory());
		register(HealthConditionType.getFactory());
		register(RelativeHealthConditionType.getFactory());
		register(BiomeConditionType.getFactory());
		register(EntityTypeConditionType.getFactory());
		register(ScoreboardConditionType.getFactory());
		register(StatusEffectConditionType.getFactory());
		register(CommandConditionType.getFactory());
		register(PredicateConditionType.getFactory());
		register(FallDistanceConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("collided_horizontally"), entity -> entity.horizontalCollision));
		register(InBlockAnywhereConditionType.getFactory());
		register(InTagConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("climbing"), entity -> entity instanceof LivingEntity living && living.onClimbable()));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("tamed"), entity -> entity instanceof OwnableEntity tameable && tameable.getOwnerUUID() != null));
		register(UsingItemConditionType.getFactory());
		register(MovingConditionType.getFactory());
		register(EnchantmentConditionType.getFactory());
		register(RidingConditionType.getFactory());
		register(RidingRootConditionType.getFactory());
		register(RidingRecursiveConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("living"), entity -> entity instanceof LivingEntity));
		register(PassengerConditionType.getFactory());
		register(PassengerRecursiveConditionType.getFactory());
		register(NbtConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("exists"), Objects::nonNull));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("creative_flying"), entity -> entity instanceof Player player && player.getAbilities().flying));
		register(PowerTypeConditionType.getFactory());
		register(AbilityConditionType.getFactory());
		register(RaycastConditionType.getFactory());
		register(ElytraFlightPossibleConditionType.getFactory());
		register(InventoryConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("in_snow"), InSnowConditionType::condition));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("in_thunderstorm"), InThunderstormConditionType::condition));
		register(AdvancementConditionType.getFactory());
		register(EntitySetSizeConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("using_effective_tool"), UsingEffectiveToolConditionType::condition));
		register(GameModeConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("glowing"), entity -> entity.isCurrentlyGlowing()));
		register(EntityInRadiusConditionType.getFactory());
		register(HasCommandTagConditionType.getFactory());
		// Origins-Fabric
		register(OriginConditionType.getFactory());
	}

	public static ConditionTypeFactory<Entity> createSimpleFactory(ResourceLocation id, Predicate<Entity> condition) {
		return new ConditionTypeFactory<>(id, new SerializableData(), (data, entity) -> condition.test(entity));
	}

	public static <F extends ConditionTypeFactory<Entity>> F register(F conditionFactory) {
		return Registry.register(ApoliRegistries.ENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
	}

}
