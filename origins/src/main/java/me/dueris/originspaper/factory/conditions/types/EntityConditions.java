package me.dueris.originspaper.factory.conditions.types;

import me.dueris.calio.data.CalioDataTypes;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.conditions.types.entity.RaycastCondition;
import me.dueris.originspaper.factory.data.types.Comparison;
import me.dueris.originspaper.factory.data.types.Shape;
import me.dueris.originspaper.factory.data.types.VectorGetter;
import me.dueris.originspaper.factory.powers.apoli.ElytraFlightPower;
import me.dueris.originspaper.factory.powers.apoli.Resource;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.util.Reflector;
import me.dueris.originspaper.util.Util;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;

public class EntityConditions {
	private static final ArrayList<Object> previousWarnings = new ArrayList<>();
	private final Location[] prevLoca = new Location[100000];

	public static void warnOnce(String warning, Object key) {
		if (!previousWarnings.contains(key)) {
			previousWarnings.add(key);
			OriginsPaper.getPlugin().getLog4JLogger().warn(warning);
		}
	}

	public static void warnOnce(String warning) {
		warnOnce(warning, warning);
	}

	public static boolean compareOutOfBounds(Comparison comparison) {
		return comparison == Comparison.NOT_EQUAL || comparison == Comparison.GREATER_THAN || comparison == Comparison.GREATER_THAN_OR_EQUAL;
	}

	public static <T> T warnCouldNotGetObject(String object, String from, T assumption) {
		warnOnce("Could not retrieve " + object + " from " + from + " for distance_from_spawn condition, assuming " + assumption + " for condition.");
		return assumption;
	}

	public void registerConditions() {
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("power_type"), (condition, entity) -> {
			for (PowerType c : PowerHolderComponent.getPowers(entity)) {
				if (c.getType().equals(condition.getString("power_type").replace("origins:", "apoli:"))) { // Apoli remapping
					return true;
				}
			}
			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("power"), (condition, entity) -> {
			for (PowerType c : PowerHolderComponent.getPowers(entity)) {
				if (c.getTag().equals(condition.getString("power"))) {
					return true;
				}
			}
			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("origin"), (condition, entity) -> entity instanceof Player p && PowerHolderComponent.hasOrigin(p, condition.getString("origin"))));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("power_active"), (condition, entity) -> {
			String power = condition.getString("power");
			PowerType found = PowerHolderComponent.getPower(entity, power);
			return found != null && found.isActive((Player) entity);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("resource"), (condition, entity) -> {
			Optional<Resource.Bar> bar = Resource.getDisplayedBar(entity, condition.getString("resource"));
			if (bar.isPresent()) {
				return bar.get().meetsComparison(Comparison.fromString(condition.getString("comparison")), condition.getNumber("compare_to").getInt());
			}
			// We do a manual check of this as a backup for when people check for a non-functioning/displaying resource
			// By checking the serverloaded bars(after we define that its not displayed) and seeing if the origin wants to check
			// if its value is 0, then it would be true in apoli.
			return Resource.serverLoadedBars.containsKey(condition.getString("resource")) && condition.getString("comparison").equalsIgnoreCase("==") && condition.getNumber("compare_to").getInt() == 0;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("block_collision"), (data, entity) -> {
			AABB entityBoundingBox = entity.getHandle().getBoundingBox();
			AABB offsetEntityBoundingBox = entityBoundingBox.move(
				data.getNumberOrDefault("offset_x", 0F).getFloat() * entityBoundingBox.getXsize(),
				data.getNumberOrDefault("offset_y", 0F).getFloat() * entityBoundingBox.getYsize(),
				data.getNumberOrDefault("offset_z", 0F).getFloat() * entityBoundingBox.getZsize()
			);

			FactoryJsonObject blockCondition = data.getJsonObject("block_condition");
			Level world = entity.getHandle().level();

			BlockCollisions<BlockPos> spliterator = new BlockCollisions<>(world, entity.getHandle(), offsetEntityBoundingBox, false, (pos, shape) -> pos);

			while (spliterator.hasNext()) {

				BlockPos blockPos = spliterator.next();

				if (blockCondition == null || blockCondition.isEmpty() || ConditionExecutor.testBlock(blockCondition, world.getWorld().getBlockAt(CraftLocation.toBukkit(blockPos)))) {
					return true;
				}

			}

			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("brightness"), (data, entity) -> {
			Level world = entity.getHandle().level();
			if (world.isClientSide) {
				world.updateSkyBrightness();   //  Re-calculate the world's ambient darkness, since it's only calculated once in the client
			}

			Comparison comparison = Comparison.fromString(data.getString("comparison"));
			float compareTo = data.getNumber("compare_to").getFloat();

			BlockPos blockPos = BlockPos.containing(entity.getX(), entity.getY() + entity.getHandle().getEyeHeight(entity.getHandle().getPose()), entity.getZ());
			float brightness = world.getLightLevelDependentMagicValue(blockPos);

			return comparison.compare(brightness, compareTo);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("daytime"), (data, entity) -> {
			return entity.getHandle().level().getDayTime() % 24000L < 13000L;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("time_of_day"), (data, entity) -> {
			return Comparison.fromString(data.getString("comparison")).compare(entity.getHandle().level().getDayTime() % 24000L, data.getNumber("compare_to").getInt());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("fall_flying"), (data, entity) -> {
			return (entity.getHandle() instanceof LivingEntity && ((LivingEntity) entity.getHandle()).isFallFlying()) || ElytraFlightPower.getGlidingPlayers().contains(entity.getUniqueId());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("exposed_to_sun"), (data, entity) -> {
			Level world = entity.getHandle().level();
			if (!world.isDay() || entity.isInRain()/*we use bukkits check for if the entity is in rain*/) {
				return false;
			}

			if (world.isClientSide) {
				world.updateSkyBrightness();
			}

			BlockPos blockPos = BlockPos.containing(entity.getX(), entity.getHandle().getBoundingBox().maxY, entity.getZ());
			float brightness = world.getLightLevelDependentMagicValue(blockPos);

			return brightness > 0.5
				&& world.canSeeSky(blockPos);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("in_rain"), (data, entity) -> {
			return entity.isInRain(); // We use bukkits check for if the entity is in rain
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("invisible"), (data, entity) -> {
			return entity.getHandle().isInvisible();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("on_fire"), (data, entity) -> {
			return entity.getHandle().isOnFire();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("exposed_to_sky"), (data, entity) -> {
			BlockPos blockPos = entity.getHandle().getVehicle() instanceof Boat ?
				(BlockPos.containing(
					entity.getHandle().getX(),
					(double) Math.round(entity.getHandle().getY()),
					entity.getHandle().getZ())
				).above() : BlockPos.containing(entity.getHandle().getX(), (double) Math.round(entity.getHandle().getY()), entity.getHandle().getZ());
			return entity.getHandle().level().canSeeSky(blockPos);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("sneaking"), (data, entity) -> {
			return entity.getHandle().isShiftKeyDown();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("sprinting"), (data, entity) -> {
			return entity.getHandle().isSprinting();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("status_effect"), (data, entity) -> {
			if (!(entity.getHandle() instanceof LivingEntity livingEntity)) {
				return false;
			}

			MobEffectInstance statusEffectInstance = livingEntity.getEffect(data.registryEntry("effect", net.minecraft.core.registries.Registries.MOB_EFFECT));
			if (statusEffectInstance == null) {
				return false;
			}

			int duration = statusEffectInstance.getDuration();
			int amplifier = statusEffectInstance.getAmplifier();

			return (duration <= data.getNumberOrDefault("max_duration", Integer.MAX_VALUE).getInt() && duration >= data.getNumberOrDefault("min_duration", -1).getInt())
				&& (amplifier <= data.getNumberOrDefault("max_amplifier", Integer.MAX_VALUE).getInt() && amplifier >= data.getNumberOrDefault("min_amplifier", 0).getInt());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("submerged_in"), (data, entity) -> {
			return Util.apoli$isSubmergedInLoosely(entity.getHandle(), data.getTagKey("fluid", net.minecraft.core.registries.Registries.FLUID));
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("fluid_height"), (data, entity) -> {
			return Comparison.fromString(data.getString("comparison")).compare(Util.apoli$getFluidHeightLoosely(entity.getHandle(), data.getTagKey("fluid", net.minecraft.core.registries.Registries.FLUID)), data.getNumber("compare_to").getDouble());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("food_level"), (data, entity) -> {
			if (entity.getHandle() instanceof net.minecraft.world.entity.player.Player) {
				return (Comparison.fromString(data.getString("comparison")).compare(((net.minecraft.world.entity.player.Player) entity.getHandle()).getFoodData().getFoodLevel(), data.getNumber("compare_to").getInt()));
			}
			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("saturation_level"), (data, entity) -> {
			if (entity.getHandle() instanceof net.minecraft.world.entity.player.Player) {
				return (Comparison.fromString(data.getString("comparison")).compare(((net.minecraft.world.entity.player.Player) entity.getHandle()).getFoodData().getSaturationLevel(), data.getNumber("compare_to").getFloat()));
			}
			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("on_block"), (data, entity) -> {
			return entity.getHandle().onGround() &&
				(!data.isPresent("block_condition") || ConditionExecutor.testBlock(data.getJsonObject("block_condition"),
					entity.getWorld().getBlockAt(CraftLocation.toBukkit(BlockPos.containing(entity.getX(), entity.getHandle().getBoundingBox().minY - 0.5000001D, entity.getZ())))));
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("attribute"), (data, entity) -> {
			double attrValue = 0F;
			if (entity.getHandle() instanceof LivingEntity living) {
				AttributeInstance attributeInstance = living.getAttribute(
					living.level().registryAccess().registry(net.minecraft.core.registries.Registries.ATTRIBUTE).get().getHolder(data.getResourceLocation("attribute")).get()
				);
				if (attributeInstance != null) {
					attrValue = attributeInstance.getValue();
				}
			}
			return Comparison.fromString(data.getString("comparison")).compare(attrValue, data.getNumber("compare_to").getDouble());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("swimming"), (data, entity) -> {
			return entity.getHandle().isSwimming();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("air"), (data, entity) -> {
			return Comparison.fromString(data.getString("comparison")).compare(entity.getHandle().getAirSupply(), data.getNumber("compare_to").getInt());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("in_block"), (data, entity) -> {
			return !data.isPresent("block_condition") || ConditionExecutor.testBlock(data.getJsonObject("block_condition"), entity.getWorld().getBlockAt(CraftLocation.toBukkit(entity.getHandle().blockPosition())));
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("block_in_radius"), (data, entity) -> {
			FactoryJsonObject blockCondition = data.getJsonObject("block_condition");
			int stopAt = -1;
			Comparison comparison = Comparison.fromString(data.getString("comparison"));
			int compareTo = data.getNumber("compare_to").getInt();
			switch (comparison) {
				case EQUAL:
				case LESS_THAN_OR_EQUAL:
				case GREATER_THAN:
					stopAt = compareTo + 1;
					break;
				case LESS_THAN:
				case GREATER_THAN_OR_EQUAL:
					stopAt = compareTo;
					break;
			}
			int count = 0;
			for (BlockPos pos : Shape.getPositions(entity.getHandle().blockPosition(), data.getEnumValueOrDefault("shape", Shape.class, Shape.CUBE), data.getNumber("radius").getInt())) {
				if ((blockCondition.isEmpty() || ConditionExecutor.testBlock(blockCondition, entity.getWorld().getBlockAt(CraftLocation.toBukkit(pos))))
					&& !entity.getWorld().getBlockAt(CraftLocation.toBukkit(pos)).getType().isAir()) {
					count++;
					if (count == stopAt) {
						break;
					}
				}
			}
			return comparison.compare(count, compareTo);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("distance_from_coordinates"), (data, entity) -> {
			boolean scaleReferenceToDimension = data.getBooleanOrDefault("scale_reference_to_dimension", true),
				setResultOnWrongDimension = data.isPresent("result_on_wrong_dimension"),
				resultOnWrongDimension = setResultOnWrongDimension && data.getBoolean("result_on_wrong_dimension");
			double x = 0, y = 0, z = 0;
			Comparison comparison = Comparison.fromString(data.getString("comparison"));
			Vec3 pos;
			Level world;
			pos = entity.getHandle().position();
			world = entity.getHandle().getCommandSenderWorld();
			double currentDimensionCoordinateScale = world.dimensionType().coordinateScale();

			// Get the reference's scaled coordinates
			switch (data.getStringOrDefault("reference", "world_origin")) {
				case "player_spawn":
//                 if (entity instanceof ServerPlayerEntity) { // null instance of AnyClass is always false so the block case is covered
//
//                 }
//                 // No break on purpose (defaulting to natural spawn)
				case "player_natural_spawn": // spawn not set through commands or beds/anchors
					if (entity.getHandle() instanceof net.minecraft.world.entity.player.Player) { // && data.getBoolean("check_modified_spawn")){
						warnOnce("Used reference '" + data.getString("reference") + "' which is not implemented yet, defaulting to world spawn.");
					}
					// No break on purpose (defaulting to world spawn)
					if (entity == null)
						warnOnce("Used entity-condition-only reference point in block condition, defaulting to world spawn.");
				case "world_spawn":
					if (setResultOnWrongDimension && world.dimension() != Level.OVERWORLD)
						return resultOnWrongDimension;
					BlockPos spawnPos;
					if (world instanceof ServerLevel)
						spawnPos = world.getSharedSpawnPos();
					else
						return warnCouldNotGetObject("world with spawn position", "entity", compareOutOfBounds(comparison));
					x = spawnPos.getX();
					y = spawnPos.getY();
					z = spawnPos.getZ();
					break;
				case "world_origin":
					break;
			}
			Vec3 coords = VectorGetter.getNMSVector(data.getJsonObject("coordinates"));
			Vec3 offset = VectorGetter.getNMSVector(data.getJsonObject("offset"));
			x += coords.x + offset.x;
			y += coords.y + offset.y;
			z += coords.z + offset.z;
			if (scaleReferenceToDimension && (x != 0 || z != 0)) {
				if (currentDimensionCoordinateScale == 0) // pocket dimensions?
					// coordinate scale 0 means it takes 0 blocks to travel in the OW to travel 1 block in the dimension,
					// so the dimension is folded on 0 0, so unless the OW reference is at 0 0, it gets scaled to infinity
					return compareOutOfBounds(comparison);
				x /= currentDimensionCoordinateScale;
				z /= currentDimensionCoordinateScale;
			}

			// Get the distance to these coordinates
			double distance,
				xDistance = data.getBooleanOrDefault("ignore_x", false) ? 0 : Math.abs(pos.x() - x),
				yDistance = data.getBooleanOrDefault("ignore_y", false) ? 0 : Math.abs(pos.y() - y),
				zDistance = data.getBooleanOrDefault("ignore_z", false) ? 0 : Math.abs(pos.z() - z);
			if (data.getBooleanOrDefault("scale_distance_to_dimension", false)) {
				xDistance *= currentDimensionCoordinateScale;
				zDistance *= currentDimensionCoordinateScale;
			}

			distance = Shape.getDistance(data.getEnumValueOrDefault("shape", Shape.class, Shape.CUBE), xDistance, yDistance, zDistance);

			if (data.isPresent("round_to_digit"))
				distance = new BigDecimal(distance).setScale(data.getNumber("round_to_digit").getInt(), RoundingMode.HALF_UP).doubleValue();

			return comparison.compare(distance, data.getNumber("compare_to").getDouble());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("dimension"), (data, entity) -> {
			return entity.getHandle().level().dimension() == ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, data.getResourceLocation("dimension"));
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("xp_levels"), (data, entity) -> {
			if (entity.getHandle() instanceof net.minecraft.world.entity.player.Player) {
				return (Comparison.fromString(data.getString("comparison"))).compare(((net.minecraft.world.entity.player.Player) entity.getHandle()).experienceLevel, data.getNumber("compare_to").getInt());
			}
			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("xp_points"), (data, entity) -> {
			if (entity.getHandle() instanceof net.minecraft.world.entity.player.Player) {
				return Comparison.fromString(data.getString("comparison")).compare(((net.minecraft.world.entity.player.Player) entity.getHandle()).totalExperience, data.getNumber("compare_to").getInt());
			}
			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("health"), (data, entity) -> {
			return Comparison.fromString(data.getString("comparison")).compare(entity.getHandle() instanceof LivingEntity ? ((LivingEntity) entity.getHandle()).getHealth() : 0f, data.getNumber("compare_to").getFloat());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("relative_health"), (data, entity) -> {
			float health = 0f;
			if (entity.getHandle() instanceof LivingEntity living) {
				health = living.getHealth() / living.getMaxHealth();
			}
			return Comparison.fromString(data.getString("comparison")).compare(health, data.getNumber("compare_to").getFloat());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("biome"), (data, entity) -> {
			BlockPos blockPos = entity.getHandle().blockPosition();
			ServerLevel level = (ServerLevel) entity.getHandle().level();
			Biome biome = level.getBiome(blockPos).value();
			FactoryJsonObject condition = data.getJsonObject("condition");
			if (data.isPresent("biome") || data.isPresent("biomes")) {
				ResourceLocation biomeId = entity.getHandle().level().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME).getKey(biome);
				if (data.isPresent("biome") && biomeId.equals(data.getResourceLocation("biome"))) {
					return condition == null || condition.isEmpty() || ConditionExecutor.testBiome(condition, blockPos, level);
				}
				if (data.isPresent("biomes") && (
					data.getJsonArray("biomes").asList.stream()
						.map(FactoryElement::getString)
						.map(ResourceLocation::parse).toList()
				).contains(biomeId)) {
					return condition == null || condition.isEmpty() || ConditionExecutor.testBiome(condition, blockPos, level);
				}
				return false;
			}
			return condition == null || condition.isEmpty() || ConditionExecutor.testBiome(condition, blockPos, level);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("entity_type"), (data, entity) -> {
			return entity.getHandle().getType() == entity.getHandle().level().registryAccess().registry(net.minecraft.core.registries.Registries.ENTITY_TYPE).get().get(data.getResourceLocation("entity_type"));
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("scoreboard_condition"), (data, entity) -> {
			ScoreHolder scoreHolder = ScoreHolder.forNameOnly(entity.getHandle().getScoreboardName());
			Scoreboard scoreboard = entity.getHandle().level().getScoreboard();

			Objective scoreboardObjective = scoreboard.getObjective(data.getString("objective"));
			if (scoreboardObjective == null) {
				return false;
			}

			Comparison comparison = Comparison.fromString(data.getString("comparison"));
			int compareTo = data.getNumber("compare_to").getInt();

			ScoreAccess scoreAccess = scoreboard.getOrCreatePlayerScore(scoreHolder, scoreboardObjective);
			return comparison.compare(scoreAccess.get(), compareTo);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("command"), (data, entity) -> {
			MinecraftServer server = entity.getHandle().getServer();
			AtomicInteger result = new AtomicInteger();

			if (server == null) {
				return false;
			}

			CommandSource commandOutput = CommandSource.NULL;
			CommandSourceStack source = entity.getHandle().createCommandSourceStack()
				.withSource(commandOutput)
				.withPermission(4)
				.withCallback((successful, returnValue) -> result.set(returnValue));

			Comparison comparison = Comparison.fromString(data.getString("comparison"));
			String command = data.getString("command");

			int compareTo = data.getNumber("compare_to").getInt();
			server.getCommands().performPrefixedCommand(source, command);

			return comparison.compare(result.get(), compareTo);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("predicate"), (data, entity) -> {
			if (!(entity.getHandle().level() instanceof ServerLevel serverWorld)) {
				return false;
			}

			ResourceKey<LootItemCondition> predicateKey = ResourceKey.create(net.minecraft.core.registries.Registries.PREDICATE, data.getResourceLocation("predicate"));
			LootItemCondition predicate = serverWorld.getServer().reloadableRegistries()
				.get()
				.registryOrThrow(net.minecraft.core.registries.Registries.PREDICATE)
				.getOrThrow(predicateKey);

			LootParams lootContextParameterSet = new LootParams.Builder(serverWorld)
				.withParameter(LootContextParams.ORIGIN, entity.getHandle().position())
				.withOptionalParameter(LootContextParams.THIS_ENTITY, entity.getHandle())
				.create(LootContextParamSets.COMMAND);
			LootContext lootContext = new LootContext.Builder(lootContextParameterSet)
				.create(Optional.empty());

			return predicate.test(lootContext);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("fall_distance"), (data, entity) -> {
			return Comparison.fromString(data.getString("comparison")).compare(entity.getHandle().fallDistance, data.getNumber("compare_to").getFloat());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("collided_horrizontally"), (data, entity) -> {
			return entity.getHandle().horizontalCollision;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("in_block_anywhere"), (data, entity) -> {
			FactoryJsonObject blockCondition = data.getJsonObject("block_condition");
			int stopAt = -1;
			Comparison comparison = Comparison.fromString(data.getString("comparison"));
			int compareTo = data.getNumber("compare_to").getInt();
			switch (comparison) {
				case EQUAL:
				case LESS_THAN_OR_EQUAL:
				case GREATER_THAN:
				case NOT_EQUAL:
					stopAt = compareTo + 1;
					break;
				case LESS_THAN:
				case GREATER_THAN_OR_EQUAL:
					stopAt = compareTo;
					break;
			}
			int count = 0;
			AABB box = entity.getHandle().getBoundingBox();
			BlockPos blockPos = BlockPos.containing(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D);
			BlockPos blockPos2 = BlockPos.containing(box.maxX - 0.001D, box.maxY - 0.001D, box.maxZ - 0.001D);
			BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
			for (int i = blockPos.getX(); i <= blockPos2.getX() && count < stopAt; ++i) {
				for (int j = blockPos.getY(); j <= blockPos2.getY() && count < stopAt; ++j) {
					for (int k = blockPos.getZ(); k <= blockPos2.getZ() && count < stopAt; ++k) {
						mutable.set(i, j, k);
						if (blockCondition.isEmpty() || ConditionExecutor.testBlock(blockCondition, entity.getWorld().getBlockAt(CraftLocation.toBukkit(mutable)))) {
							count++;
						}
					}
				}
			}
			return comparison.compare(count, compareTo);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("in_tag"), (data, entity) -> {
			TagKey<EntityType<?>> entityTypeTag = data.getTagKey("tag", net.minecraft.core.registries.Registries.ENTITY_TYPE);
			return entity.getHandle().getType().is(entityTypeTag);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("climbing"), (data, entity) -> {
			return entity.getHandle() instanceof LivingEntity livingEntity && livingEntity.onClimbable();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("tamed"), (data, entity) -> {
			return entity.getHandle() instanceof OwnableEntity tameable
				&& tameable.getOwnerUUID() != null;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("using_item"), (data, entity) -> {
			if (!(entity.getHandle() instanceof LivingEntity livingEntity) || !livingEntity.isUsingItem()) {
				return false;
			}

			FactoryJsonObject itemCondition = data.getJsonObject("item_condition");
			InteractionHand activeHand = livingEntity.getUsedItemHand();

			ItemStack stackInHand = livingEntity.getItemInHand(activeHand);
			return itemCondition == null || itemCondition.isEmpty() || ConditionExecutor.testItem(itemCondition, stackInHand.getBukkitStack());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("moving"), (data, entity) -> {
			boolean horizontal = data.getBooleanOrDefault("horizontally", true);
			boolean vertical = data.getBooleanOrDefault("vertically", true);
			if (vertical && horizontal) return isEntityMoving(entity);
			if (vertical && !horizontal) return isEntityMovingVertical(entity);
			if (!vertical && horizontal) return isEntityMovingHorizontal(entity);
			return !isEntityMoving(entity);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("enchantment"), (data, entity) -> {
			int value = 0;
			if (entity.getHandle() instanceof LivingEntity le) {

				Registry<Enchantment> enchantmentRegistry = entity.getHandle().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);

				ResourceKey<Enchantment> enchantmentKey = ResourceKey.create(net.minecraft.core.registries.Registries.ENCHANTMENT, data.getResourceLocation("enchantment"));
				Enchantment enchantment = enchantmentRegistry.getOrThrow(enchantmentKey);

				Holder<Enchantment> enchantmentEntry = enchantmentRegistry.wrapAsHolder(enchantment);

				String calculation = data.getStringOrDefault("calculation", "sum");

				switch (calculation) {
					case "sum":
						for (ItemStack stack : enchantment.getSlotItems(le).values()) {
							value += EnchantmentHelper.getItemEnchantmentLevel(enchantmentEntry, stack);
						}
						break;
					case "max":
						value = EnchantmentHelper.getEnchantmentLevel(enchantmentEntry, le);
						break;
					default:
						OriginsPaper.getPlugin().getLog4JLogger().error("Error in \"enchantment\" entity condition, undefined calculation type: \"{}\".", calculation);
						break;
				}
			}
			return Comparison.fromString(data.getString("comparison")).compare(value, data.getNumber("compare_to").getInt());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("riding"), (data, entity) -> {
			if (entity.getHandle().isPassenger()) {
				if (data.isPresent("bientity_condition")) {
					FactoryJsonObject condition = data.getJsonObject("bientity_condition");
					net.minecraft.world.entity.Entity vehicle = entity.getHandle().getVehicle();
					return ConditionExecutor.testBiEntity(condition, entity, vehicle.getBukkitEntity());
				}
				return true;
			}
			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("riding_root"), (data, entity) -> {
			if (entity.getHandle().isPassenger()) {
				if (data.isPresent("bientity_condition")) {
					FactoryJsonObject condition = data.getJsonObject("bientity_condition");
					net.minecraft.world.entity.Entity vehicle = entity.getHandle().getRootVehicle();
					return ConditionExecutor.testBiEntity(condition, entity, vehicle.getBukkitEntity());
				}
				return true;
			}
			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("riding_recursive"), (data, entity) -> {
			int count = 0;
			if (entity.getHandle().isPassenger()) {
				FactoryJsonObject condition = data.getJsonObject("bientity_condition");
				net.minecraft.world.entity.Entity vehicle = entity.getHandle().getVehicle();
				while (vehicle != null) {
					if (condition == null || condition.isEmpty() || ConditionExecutor.testBiEntity(condition, entity, vehicle.getBukkitEntity())) {
						count++;
					}
					vehicle = vehicle.getVehicle();
				}
			}
			return (data.isPresent("comparison") ? Comparison.fromString(data.getString("comparison")) : Comparison.GREATER_THAN_OR_EQUAL).compare(count, data.getNumberOrDefault("compare_to", 1).getInt());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("living"), (data, entity) -> {
			return entity.getHandle() instanceof LivingEntity;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("passenger"), (data, entity) -> {
			int count = 0;
			if (entity.getHandle().isVehicle()) {
				if (data.isPresent("bientity_condition")) {
					FactoryJsonObject condition = data.getJsonObject("bientity_condition");
					count = (int) entity.getHandle().getPassengers().stream().filter(e -> ConditionExecutor.testBiEntity(condition, e.getBukkitEntity(), entity)).count();
				} else {
					count = entity.getHandle().getPassengers().size();
				}
			}
			return (data.isPresent("comparison") ? Comparison.fromString(data.getString("comparison")) : Comparison.GREATER_THAN_OR_EQUAL).compare(count, data.getNumberOrDefault("compare_to", 1).getInt());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("passenger_recursive"), (data, entity) -> {
			int count = 0;
			if (entity.getHandle().isVehicle()) {
				if (data.isPresent("bientity_condition")) {
					FactoryJsonObject condition = data.getJsonObject("bientity_condition");
					List<net.minecraft.world.entity.Entity> passengers = entity.getHandle().getPassengers();
					count = (int) passengers.stream().flatMap(net.minecraft.world.entity.Entity::getSelfAndPassengers).filter(e -> ConditionExecutor.testBiEntity(condition, e.getBukkitEntity(), entity)).count();
				} else {
					count = (int) entity.getHandle().getPassengers().stream().flatMap(net.minecraft.world.entity.Entity::getSelfAndPassengers).count();
				}
			}
			return (data.isPresent("comparison") ? Comparison.fromString(data.getString("comparison")) : Comparison.GREATER_THAN_OR_EQUAL).compare(count, data.getNumberOrDefault("compare_to", 1).getInt());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("nbt"), (data, entity) -> {
			CompoundTag nbt = new CompoundTag();
			entity.getHandle().saveWithoutId(nbt);
			return NbtUtils.compareNbt(data.transformWithCalio("nbt", CalioDataTypes::compoundTag), nbt, true);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("exists"), (data, entity) -> {
			return entity != null;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("creative_flying"), (data, entity) -> {
			return entity.getHandle() instanceof net.minecraft.world.entity.player.Player && ((net.minecraft.world.entity.player.Player) entity.getHandle()).getAbilities().flying;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("ability"), (data, entity) -> {
			boolean enabled = false;
			if (entity.getHandle() instanceof net.minecraft.world.entity.player.Player && !entity.getHandle().level().isClientSide) {
				switch (data.getResourceLocation("ability").toString()) {
					case "minecraft:flying":
						enabled = ((net.minecraft.world.entity.player.Player) entity.getHandle()).getAbilities().flying;
					case "minecraft:instabuild":
						enabled = ((net.minecraft.world.entity.player.Player) entity.getHandle()).getAbilities().instabuild;
					case "minecraft:invulnerable":
						enabled = ((net.minecraft.world.entity.player.Player) entity.getHandle()).getAbilities().invulnerable;
					case "minecraft:mayBuild":
						enabled = ((net.minecraft.world.entity.player.Player) entity.getHandle()).getAbilities().mayBuild;
					case "minecraft:mayfly":
						enabled = ((net.minecraft.world.entity.player.Player) entity.getHandle()).getAbilities().mayfly;
						break;
					default:
						throw new IllegalStateException("Unexpected value: " + data.getResourceLocation("ability").toString());
				}
			}
			return enabled;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("raycast"), (data, entity) -> {
			return RaycastCondition.condition(data, entity.getHandle());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("elytra_flight_possible"), (data, entity) -> {
			if (!(entity.getHandle() instanceof LivingEntity livingEntity)) {
				return false;
			}
			boolean ability = true;
			if (data.getBooleanOrDefault("check_ability", true)) {
				ItemStack equippedChestItem = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
				ability = equippedChestItem.is(Items.ELYTRA) && ElytraItem.isFlyEnabled(equippedChestItem);
				if (!ability && PowerHolderComponent.hasPowerType(entity, ElytraFlightPower.class) && entity instanceof Player player) {
					for (ElytraFlightPower power : PowerHolderComponent.getPowers(entity, ElytraFlightPower.class)) {
						if (power.isActive(player)) {
							ability = true;
							break;
						}
					}
				}
			}
			boolean state = true;
			if (data.getBoolean("check_state")) {
				state = !livingEntity.onGround() && !livingEntity.isFallFlying() && !livingEntity.isInWater() && !livingEntity.hasEffect(MobEffects.LEVITATION);
			}
			return ability && state;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("inventory"), (data, entity) -> {
			Util.ProcessMode processMode = data.getEnumValueOrDefault("process_mode", Util.ProcessMode.class, Util.ProcessMode.ITEMS);
			Comparison comparison = Comparison.fromString(data.getString("comparison"));

			int compareTo = data.getNumberOrDefault("compare_to", 0).getInt();
			int matches = 0;

			if (true) {
				matches += Util.checkInventory(data, entity.getHandle(), null, processMode.getProcessor());
			}

			return comparison.compare(matches, compareTo);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("in_snow"), (data, entity) -> {
			BlockPos downBlockPos = entity.getHandle().blockPosition();
			BlockPos upBlockPos = BlockPos.containing(downBlockPos.getX(), entity.getHandle().getBoundingBox().maxY, downBlockPos.getX());

			return Util.inSnow(entity.getHandle().level(), downBlockPos, upBlockPos);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("in_thunderstorm"), (data, entity) -> {
			BlockPos downBlockPos = entity.getHandle().blockPosition();
			BlockPos upBlockPos = BlockPos.containing(downBlockPos.getX(), entity.getHandle().getBoundingBox().maxY, downBlockPos.getX());

			return Util.inThunderstorm(entity.getHandle().level(), downBlockPos, upBlockPos);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("advancement"), (data, entity) -> {
			if (!(entity.getHandle() instanceof net.minecraft.world.entity.player.Player playerEntity)) {
				return false;
			}

			MinecraftServer server = playerEntity.getServer();
			ResourceLocation advancementId = data.getResourceLocation("advancement");

			if (server != null) {

				AdvancementHolder advancementEntry = server.getAdvancements().get(advancementId);
				if (advancementEntry == null) {
					OriginsPaper.getPlugin().getLog4JLogger().warn("Advancement \"{}\" did not exist, but was referenced in an \"advancement\" entity condition!", advancementId);
					return false;
				}

				return ((ServerPlayer) playerEntity)
					.getAdvancements()
					.getOrStartProgress(advancementEntry)
					.isDone();

			}

			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("using_effective_tool"), (data, entity) -> {
			if (!(entity.getHandle() instanceof net.minecraft.world.entity.player.Player playerEntity)) {
				return false;
			}

			if (playerEntity instanceof ServerPlayer serverPlayerEntity) {

				ServerPlayerGameMode interactionManager = serverPlayerEntity.gameMode;
				boolean isMining = Boolean.TRUE.equals(Reflector.accessField("isDestroyingBlock", ServerPlayerGameMode.class, interactionManager, Boolean.class));
				if (!isMining) {
					return false;
				}
				BlockState miningBlockState = entity.getHandle().level().getBlockState(Reflector.accessField("destroyPos", ServerPlayerGameMode.class, interactionManager, BlockPos.class));
				return playerEntity.hasCorrectToolForDrops(miningBlockState);

			} else {
				return false;
			}
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("gamemode"), (data, entity) -> {
			GameType type = data.getEnumValue("gamemode", GameType.class);
			return entity.getHandle() instanceof ServerPlayer && ((ServerPlayer) entity.getHandle()).gameMode.getGameModeForPlayer().equals(type);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("glowing"), (data, entity) -> {
			return entity.getHandle().isCurrentlyGlowing();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("entity_in_radius"), (data, entity) -> {
			FactoryJsonObject biEntityCondition = data.getJsonObject("bientity_condition");
			Shape shape = data.getEnumValueOrDefault("shape", Shape.class, Shape.CUBE);

			Comparison comparison = Comparison.fromString(data.getString("comparison"));
			int compareTo = data.getNumberOrDefault("compare_to", 1).getInt();

			double radius = data.getNumber("radius").getDouble();
			int countThreshold = switch (comparison) {
				case EQUAL, LESS_THAN_OR_EQUAL, GREATER_THAN -> compareTo + 1;
				case LESS_THAN, GREATER_THAN_OR_EQUAL -> compareTo;
				default -> -1;
			};

			int count = 0;
			for (net.minecraft.world.entity.Entity target : Shape.getEntities(shape, entity.getHandle().level(), entity.getHandle().getPosition(1.0F), radius)) {

				if (ConditionExecutor.testBiEntity(biEntityCondition, entity, target.getBukkitEntity())) {
					++count;
				}

				if (count == countThreshold) {
					break;
				}

			}

			return comparison.compare(count, compareTo);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("has_command_tag"), (data, entity) -> {
			Set<String> commandTags = entity.getHandle().getTags();
			Set<String> specifiedCommandTags = new HashSet<>();

			if (data.isPresent("command_tag")) {
				specifiedCommandTags.add(data.getString("command_tag"));
			}

			if (data.isPresent("command_tags")) {
				specifiedCommandTags.addAll(data.getJsonArray("command_tags").asList.stream().map(FactoryElement::getString).toList());
			}

			return specifiedCommandTags.isEmpty()
				? !commandTags.isEmpty()
				: !Collections.disjoint(commandTags, specifiedCommandTags);
		}));
	}

	public void register(ConditionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.ENTITY_CONDITION).register(factory);
	}

	public boolean isEntityMoving(Entity entity) {
		int entID = entity.getEntityId();
		Location prevLocat = prevLoca[entID];
		Location cuLo = entity.getLocation().clone();
		prevLoca[entID] = cuLo;
		if (prevLocat != null) cuLo.setDirection(prevLocat.getDirection()); // Ignore direction changes

		return !cuLo.equals(prevLocat);
	}

	public boolean isEntityMovingHorizontal(Entity entity) {
		int entID = entity.getEntityId();
		Location prevLocat = prevLoca[entID];
		Location cuLo = entity.getLocation().clone();
		prevLoca[entID] = cuLo;
		if (prevLocat == null) return true;

		return cuLo.getX() != prevLocat.getX() || cuLo.getZ() != cuLo.getZ();
	}

	public boolean isEntityMovingVertical(Entity entity) {
		int entID = entity.getEntityId();
		Location prevLocat = prevLoca[entID];
		Location cuLo = entity.getLocation().clone();
		prevLoca[entID] = cuLo;
		if (prevLocat == null) return true;

		return cuLo.getY() != prevLocat.getY();
	}

	public class ConditionFactory implements Registrable {
		ResourceLocation key;
		BiPredicate<FactoryJsonObject, CraftEntity> test;

		public ConditionFactory(ResourceLocation key, BiPredicate<FactoryJsonObject, CraftEntity> test) {
			this.key = key;
			this.test = test;
		}

		public boolean test(FactoryJsonObject condition, CraftEntity tester) {
			return test.test(condition, tester);
		}

		@Override
		public ResourceLocation key() {
			return key;
		}
	}
}
