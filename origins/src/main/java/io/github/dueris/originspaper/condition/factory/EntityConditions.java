package io.github.dueris.originspaper.condition.factory;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.type.entity.*;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import io.github.dueris.originspaper.power.type.ClimbingPower;
import io.github.dueris.originspaper.power.type.ElytraFlightPower;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

public class EntityConditions {
	private static final Location[] prevLoca = new Location[100000];

	public static void register() {
		MetaConditions.register(ApoliDataTypes.ENTITY_CONDITION, EntityConditions::register);
		register(BlockCollisionConditionType.getFactory());
		register(BrightnessConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("daytime"), (entity) -> {
			return TimeOfDayConditionType.condition(entity.level(), Comparison.LESS_THAN, 13000);
		}));
		register(TimeOfDayConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("fall_flying"), (entity) -> {
			if (entity instanceof LivingEntity living) {
				return living.isFallFlying() || PowerHolderComponent.doesHaveConditionedPower(living.getBukkitEntity(), ElytraFlightPower.class, p -> p.getGlidingPlayers().contains(living.getBukkitEntity().getUniqueId()));
			}
			return false;
		}));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("exposed_to_sun"), ExposedToSunConditionType::condition));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("in_rain"), (entity) -> {
			return entity.getBukkitEntity().isInRain();
		}));
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
		DistanceFromCoordinatesConditionRegistry.registerEntityCondition(EntityConditions::register);
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
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("collided_horizontally"), (entity) -> {
			return entity.horizontalCollision;
		}));
		register(InBlockAnywhereConditionType.getFactory());
		register(InTagConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("climbing"), (entity) -> {
			if (entity instanceof LivingEntity living) {
				return living.onClimbable() || PowerHolderComponent.doesHaveConditionedPower(living.getBukkitEntity(), ClimbingPower.class, p -> p.isActive(living));
			}
			return false;
		}));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("tamed"), (entity) -> {
			if (entity instanceof OwnableEntity tameable) {
				return tameable.getOwnerUUID() != null;
			}
			return false;
		}));
		register(UsingItemConditionType.getFactory());
		register(MovingConditionType.getFactory());
		register(EnchantmentConditionType.getFactory());
		register(RidingConditionType.getFactory());
		register(RidingRootConditionType.getFactory());
		register(RidingRecursiveConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("living"), (entity) -> {
			return entity instanceof LivingEntity;
		}));
		register(PassengerConditionType.getFactory());
		register(PassengerRecursiveConditionType.getFactory());
		register(NbtConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("exists"), Objects::nonNull));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("creative_flying"), (entity) -> {
			if (entity instanceof Player player) {
				return player.getAbilities().flying;
			}
			return false;
		}));
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
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("glowing"), Entity::isCurrentlyGlowing));
		register(EntityInRadiusConditionType.getFactory());
		register(HasCommandTagConditionType.getFactory());
	}

	public static @NotNull ConditionTypeFactory<Entity> createSimpleFactory(ResourceLocation id, Predicate<Entity> condition) {
		return new ConditionTypeFactory<>(id, new SerializableData(), (data, entity) -> {
			return condition.test(entity);
		});
	}

	public static @NotNull ConditionTypeFactory<Entity> register(ConditionTypeFactory<Entity> conditionFactory) {
		return Registry.register(ApoliRegistries.ENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
	}

	public static boolean isEntityMovingHorizontal(@NotNull Entity entity) {
		int entID = entity.getBukkitEntity().getEntityId();
		Location prevLocat = prevLoca[entID];
		Location cuLo = entity.getBukkitEntity().getLocation().clone();
		prevLoca[entID] = cuLo;
		if (prevLocat == null) return true;

		return cuLo.getX() != prevLocat.getX() || prevLocat.getZ() != cuLo.getZ();
	}

	public static boolean isEntityMovingVertical(@NotNull Entity entity) {
		int entID = entity.getBukkitEntity().getEntityId();
		Location prevLocat = prevLoca[entID];
		Location cuLo = entity.getBukkitEntity().getLocation().clone();
		prevLoca[entID] = cuLo;
		if (prevLocat == null) return true;

		return cuLo.getY() != prevLocat.getY();
	}
}
