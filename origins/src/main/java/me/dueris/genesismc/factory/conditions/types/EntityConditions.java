package me.dueris.genesismc.factory.conditions.types;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Comparison;
import me.dueris.genesismc.factory.data.types.EntityGroup;
import me.dueris.genesismc.factory.data.types.Shape;
import me.dueris.genesismc.factory.data.types.VectorGetter;
import me.dueris.genesismc.factory.powers.ApoliPower;
import me.dueris.genesismc.factory.powers.apoli.*;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.util.RaycastUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

public class EntityConditions {
	public static HashMap<String, ArrayList<EntityType>> entityTagMappings = new HashMap<>();
	private final Location[] prevLoca = new Location[100000];

	public void prep() {
		register(new ConditionFactory(GenesisMC.apoliIdentifier("ability"), (condition, entity) -> {
			if (entity instanceof Player p) {
				String ability = condition.getString("ability").toLowerCase();

				switch (ability) {
					case "minecraft:flying" -> {
						return ((CraftPlayer) p).getHandle().getAbilities().flying;
					}
					case "minecraft:instabuild" -> {
						return ((CraftPlayer) p).getHandle().getAbilities().instabuild;
					}
					case "minecraft:invulnerable" -> {
						return ((CraftPlayer) p).getHandle().getAbilities().invulnerable;
					}
					case "minecraft:maybuild" -> {
						return ((CraftPlayer) p).getHandle().getAbilities().mayBuild;
					}
					case "minecraft:mayfly" -> {
						return ((CraftPlayer) p).getHandle().getAbilities().mayfly;
					}
				}
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("power_type"), (condition, entity) -> {
			for (ApoliPower c : ((Registrar<ApoliPower>) GenesisMC.getPlugin().registry.retrieve(Registries.CRAFT_POWER)).values()) {
				if (c.getType().equals(condition.getString("power_type"))) {
					return c.getPlayersWithPower().contains(entity);
				}
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("origin"), (condition, entity) -> entity instanceof Player p && OriginPlayerAccessor.hasOrigin(p, condition.getString("origin"))));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("power_active"), (condition, entity) -> {
			if (!ApoliPower.powers_active.containsKey(entity)) return false;
			String power = condition.getString("power");
			return ApoliPower.powers_active.get(entity).getOrDefault(power, false);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("advancement"), (condition, entity) -> {
			MinecraftServer server = GenesisMC.server;
			if (entity instanceof CraftPlayer p) {
				NamespacedKey namespacedKey = condition.getNamespacedKey("advancement");

				AdvancementHolder advancementHolder = server.getAdvancements().get(CraftNamespacedKey.toMinecraft(namespacedKey));
				if (advancementHolder == null) {
					GenesisMC.getPlugin().getLogger().severe("Advancement \"{}\" did not exist but was referenced in the apoli:advancement entity condition!".replace("{}", namespacedKey.asString()));
					return false;
				}

				return p.getHandle().getAdvancements().getOrStartProgress(advancementHolder).isDone();
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("sprinting"), (condition, entity) -> (entity instanceof CraftPlayer player && player.isSprinting()) || OriginPlayerAccessor.currentSprintingPlayersFallback.contains(entity)));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("food_level"), (condition, entity) -> {
			String comparison = condition.getString("comparison");
			float compare_to = condition.getNumber("compare_to").getFloat();
			return entity instanceof Player p && Comparison.fromString(comparison).compare(p.getFoodLevel(), compare_to);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("air"), (condition, entity) -> {
			if (entity instanceof Player p) {
				return Comparison.fromString(condition.getString("comparison")).compare(p.getRemainingAir(), condition.getNumber("compare_to").getFloat());
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("block_collision"), (condition, entity) -> {
			net.minecraft.world.entity.Entity nmsEntity = entity.getHandle();
			AABB boundingBox = nmsEntity.getBoundingBox();
			double offsetX = condition.getNumberOrDefault("offset_x", 0).getDouble();
			double offsetY = condition.getNumberOrDefault("offset_y", 0).getDouble();
			double offsetZ = condition.getNumberOrDefault("offset_z", 0).getDouble();
			AABB offsetBox = boundingBox.move(offsetX, offsetY, offsetZ);

			ServerLevel level = (ServerLevel) nmsEntity.level();
			BlockCollisions<BlockPos> spliterator = new BlockCollisions<>(level, nmsEntity, offsetBox, false, (pos, shape) -> pos);

			while (spliterator.hasNext()) {
				BlockPos pos = spliterator.next();
				boolean pass = true;

				if (condition.isPresent("block_condition"))
					pass = ConditionExecutor.testBlock(condition.getJsonObject("block_condition"), CraftBlock.at(level, pos));

				if (pass) {
					return true;
				}
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("block_in_radius"), (condition, entity) -> {
			int radius = condition.getNumberOrDefault("radius", 15L).getInt();
			Shape shape = condition.getEnumValueOrDefault("shape", Shape.class, Shape.CUBE);
			String comparison = condition.getStringOrDefault("comparison", ">=");
			float compare_to = condition.getNumber("compare_to").getFloat();

			boolean hasCondition = condition.isPresent("block_condition");
			float stopAt = -1;
			Comparison fixedComparison = Comparison.fromString(comparison);
			switch (fixedComparison) {
				case EQUAL:
				case LESS_THAN_OR_EQUAL:
				case GREATER_THAN:
					stopAt = compare_to + 1;
					break;
				case LESS_THAN:
				case GREATER_THAN_OR_EQUAL:
					stopAt = compare_to;
					break;
			}
			int count = 0;
			for (BlockPos pos : Shape.getPositions(CraftLocation.toBlockPosition(entity.getLocation()), shape, radius)) {
				boolean run = true;
				if (hasCondition) {
					if (!ConditionExecutor.testBlock(condition.getJsonObject("block_condition"), CraftBlock.at(((CraftWorld) entity.getWorld()).getHandle(), pos))) {
						run = false;
					}
				}
				if (run) {
					count++;
					if (count == stopAt) {
						break;
					}
				}
			}

			return fixedComparison.compare(count, compare_to);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("set_size"), (condition, entity) -> {
			String tag = condition.getString("set");
			ArrayList<Entity> entities = EntitySetPower.entity_sets.get(tag);
			if (entities.contains(entity)) {
				String comparison = condition.getString("comparison");
				int compare_to = condition.getNumber("compare_to").getInt();
				return Comparison.fromString(comparison).compare(entities.size(), compare_to);
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("scoreboard"), (condition, entity) -> {
			String name = condition.getString("name");
			if (name == null) {
				if (entity instanceof Player player) name = player.getName();
				else name = entity.getUniqueId().toString();
			}

			Scoreboard scoreboard = entity.getHandle().level().getScoreboard();
			Objective value = scoreboard.getObjective(condition.getString("objective"));

			if (value != null && scoreboard.getPlayerScoreInfo(entity.getHandle(), value) != null) {
				int score = scoreboard.getPlayerScoreInfo(entity.getHandle(), value).value();
				String comparison = condition.getString("comparison");
				int compare_to = condition.getNumber("compare_to").getInt();
				return Comparison.fromString(comparison).compare(score, compare_to);
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("weather_check"), (condition, entity) -> {
			boolean thunder = condition.getBooleanOrDefault("thundering", false);
			boolean rain = condition.getBooleanOrDefault("raining", false);
			boolean clear = condition.getBooleanOrDefault("clear", false);
			if (thunder) {
				return entity.getWorld().isThundering();
			} else if (rain) {
				return entity.getWorld().getClearWeatherDuration() == 0;
			} else if (clear) {
				return entity.getWorld().getClearWeatherDuration() > 0;
			} else {
				return false;
			}
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("brightness"), (condition, entity) -> {
			String comparison = condition.getString("comparison");
			double compare_to = condition.getNumber("compare_to").getFloat();
			double brightness;
			int lightLevel = entity.getLocation().getBlock().getLightLevel();
			int ambientLight = 0;

			//calculate ambient light
			if (entity.getWorld() == Bukkit.getServer().getWorlds().get(0)) {
				ambientLight = 0;
			} else if (entity.getWorld() == Bukkit.getServer().getWorlds().get(2)) {
				ambientLight = 1;
			}
			brightness = ambientLight + (1 - ambientLight) * lightLevel / (60 - 3 * lightLevel);
			return Comparison.fromString(comparison).compare(brightness, compare_to);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("light_level"), (condition, entity) -> {
			String comparison = condition.getString("comparison");
			double compare_to = condition.getNumber("compare_to").getFloat();
			int lightLevel = entity.getLocation().getBlock().getLightLevel();
			return Comparison.fromString(comparison).compare(lightLevel, compare_to);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("climbing"), (condition, entity) -> {
			if (entity instanceof Player player) {
				ClimbingPower climbing = new ClimbingPower();
				return player.isClimbing() || climbing.isActiveClimbing(player);
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("collided_horizontally"), (condition, entity) -> entity.getHandle().horizontalCollision));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("creative_flying"), (condition, entity) -> {
			if (entity instanceof Player player) {
				return player.isFlying();
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("daytime"), (condition, entity) -> entity.getWorld().isDayTime()));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("dimension"), (condition, entity) -> entity.getWorld().getKey().equals(condition.getNamespacedKey("dimension"))));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("fluid_height"), (condition, entity) -> {
			String comparison = condition.getString("comparison");
			double compare_to = condition.getNumber("compare_to").getFloat();

			NamespacedKey tag = condition.getNamespacedKey("fluid");
			TagKey<Fluid> key = TagKey.create(net.minecraft.core.registries.Registries.FLUID, CraftNamespacedKey.toMinecraft(tag));
			return Comparison.fromString(comparison).compare(Utils.apoli$getFluidHeightLoosely(entity.getHandle(), key), compare_to);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("invisible"), (condition, entity) -> {
			if (entity instanceof LivingEntity le) {
				return le.isInvisible() || le.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY);
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("in_rain"), (condition, entity) -> entity.isInRain()));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("exposed_to_sun"), (condition, entity) -> {
			ServerLevel level = ((CraftWorld) entity.getWorld()).getHandle();
			BlockPos blockPos = BlockPos.containing(entity.getX(), entity.getY() + entity.getHandle().getEyeHeight(entity.getHandle().getPose()), entity.getZ());

			return level.canSeeSky(blockPos) && entity.getWorld().isDayTime();
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("exposed_to_sky"), (condition, entity) -> {
			ServerLevel level = ((CraftWorld) entity.getWorld()).getHandle();
			BlockPos blockPos = BlockPos.containing(entity.getX(), entity.getY() + entity.getHandle().getEyeHeight(entity.getHandle().getPose()), entity.getZ());

			return level.canSeeSky(blockPos);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("nbt"), (condition, entity) -> NbtUtils.compareNbt(Utils.ParserUtils.parseJson(new StringReader(condition.getString("nbt")), CompoundTag.CODEC), entity.getHandle().saveWithoutId(new CompoundTag()), true)));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("sneaking"), (condition, entity) -> entity.isSneaking()));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("resource"), (condition, entity) -> {
			Optional<Resource.Bar> bar = Resource.getDisplayedBar(entity, condition.getString("resource"));
			if (bar.isPresent()) {
				return bar.get().meetsComparison(Comparison.fromString(condition.getString("comparison")), condition.getNumber("compare_to").getInt());
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("fall_flying"), (condition, entity) -> entity instanceof LivingEntity le && (((CraftLivingEntity) le).getHandle().isFallFlying() || ElytraFlightPower.getGlidingPlayers().contains(le))));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("submerged_in"), (condition, entity) -> {
			NamespacedKey tag = condition.getNamespacedKey("fluid");
			TagKey<Fluid> key = TagKey.create(net.minecraft.core.registries.Registries.FLUID, CraftNamespacedKey.toMinecraft(tag));
			return Utils.apoli$isSubmergedInLoosely(entity.getHandle(), key);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("enchantment"), (condition, entity) -> {
			if (entity instanceof Player player) {
				NamespacedKey enchantmentNamespace = condition.getNamespacedKey("enchantment");
				String comparison = condition.getString("comparison");
				int compareTo = condition.getNumber("compare_to").getInt();
				int value = 1;

				Enchantment enchantment = CraftRegistry.ENCHANTMENT.get(enchantmentNamespace);
				switch (condition.getStringOrDefault("calculation", "sum")) {
					case "sum":
						for (net.minecraft.world.item.ItemStack stack : CraftEnchantment.bukkitToMinecraft(enchantment).getSlotItems(((CraftPlayer) player).getHandle()).values()) {
							value += EnchantmentHelper.getItemEnchantmentLevel(CraftEnchantment.bukkitToMinecraft(enchantment), stack);
						}
						break;
					case "max":
						int equippedEnchantmentLevel = 0;

						for (net.minecraft.world.item.ItemStack stack : CraftEnchantment.bukkitToMinecraft(enchantment).getSlotItems(((CraftPlayer) player).getHandle()).values()) {
							int enchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel(CraftEnchantment.bukkitToMinecraft(enchantment), stack);

							if (enchantmentLevel > equippedEnchantmentLevel) {
								equippedEnchantmentLevel = enchantmentLevel;
							}

						}

						value = equippedEnchantmentLevel;
						break;
					default:
						break;
				}
				return Comparison.fromString(comparison).compare(value, compareTo);
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("on_fire"), (condition, entity) -> entity.getHandle().isOnFire()));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("entity_type"), (condition, entity) -> entity.getType().equals(EntityType.valueOf(condition.getString("entity_type").toUpperCase().split(":")[1]))));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("equipped_item"), (condition, entity) -> {
			EquipmentSlot slot = condition.getEnumValue("equipment_slot", EquipmentSlot.class);
			if (entity instanceof LivingEntity le && le.getEquipment().getItem(CraftEquipmentSlot.getSlot(slot)) != null) {
				return ConditionExecutor.testItem(condition.getJsonObject("item_condition"), le.getEquipment().getItem(CraftEquipmentSlot.getSlot(slot)));
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("exists"), (condition, entity) -> entity != null));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("distance_from_coordinates"), (condition, entity) -> {
			boolean scaleReferenceToDimension = condition.getBooleanOrDefault("scale_reference_to_dimension", true);
			boolean setResultOnWrongDimension = condition.isPresent("result_on_wrong_dimension"), resultOnWrongDimension = setResultOnWrongDimension && condition.getBoolean("result_on_wrong_dimension");
			double x = 0, y = 0, z = 0;
			Vec3 pos = entity.getHandle().position();
			ServerLevel level = (ServerLevel) entity.getHandle().level();

			double currentDimensionCoordinateScale = level.dimensionType().coordinateScale();
			switch (condition.getStringOrDefault("reference", "world_origin")) {
				case "player_natural_spawn", "world_spawn", "player_spawn":
					if (setResultOnWrongDimension && level.dimension() != Level.OVERWORLD)
						return resultOnWrongDimension;
					BlockPos spawnPos = level.getSharedSpawnPos();
					x = spawnPos.getX();
					y = spawnPos.getY();
					z = spawnPos.getZ();
					break;
				case "world_origin":
					break;
			}

			Gson gson = new Gson();
			Map<String, Integer> fallbackMapConstant = Map.of("x", 0, "y", 0, "z", 0);
			FactoryJsonObject jsonObjectFallback = new FactoryJsonObject(gson.fromJson(gson.toJson(fallbackMapConstant), JsonObject.class));
			Vec3 coords = VectorGetter.getNMSVector(condition.isPresent("coordinates") ? condition.getJsonObject("coordinates") : jsonObjectFallback);
			Vec3 offset = VectorGetter.getNMSVector(condition.isPresent("offset") ? condition.getJsonObject("offset") : jsonObjectFallback);
			x += coords.x + offset.x;
			y += coords.y + offset.y;
			z += coords.z + offset.z;
			if (scaleReferenceToDimension && (x != 0 || z != 0)) {
				Comparison comparison = Comparison.fromString(condition.getString("comparison"));
				if (currentDimensionCoordinateScale == 0)
					return comparison == Comparison.NOT_EQUAL || comparison == Comparison.GREATER_THAN || comparison == Comparison.GREATER_THAN_OR_EQUAL;

				x /= currentDimensionCoordinateScale;
				z /= currentDimensionCoordinateScale;
			}

			double distance,
				xDistance = condition.getBooleanOrDefault("ignore_x", false) ? 0 : Math.abs(pos.x() - x),
				yDistance = condition.getBooleanOrDefault("ignore_y", false) ? 0 : Math.abs(pos.y() - y),
				zDistance = condition.getBooleanOrDefault("ignore_z", false) ? 0 : Math.abs(pos.z() - z);
			if (condition.getBooleanOrDefault("scale_distance_to_dimension", false)) {
				xDistance *= currentDimensionCoordinateScale;
				zDistance *= currentDimensionCoordinateScale;
			}

			distance = Shape.getDistance(condition.getEnumValueOrDefault("shape", Shape.class, Shape.CUBE), xDistance, yDistance, zDistance);

			if (condition.isPresent("round_to_digit")) {
				distance = new BigDecimal(distance).setScale(condition.getNumber("round_to_digit").getInt(), RoundingMode.HALF_UP).doubleValue();
			}

			return Comparison.fromString(condition.getString("comparison")).compare(distance, condition.getNumber("compare_to").getFloat());
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("entity_group"), (condition, entity) -> (EntityGroupManager.modifiedEntityGroups.containsKey(entity) && EntityGroupManager.modifiedEntityGroups.get(entity).equals(condition.getEnumValue("group", EntityGroup.class))) || (entity.getHandle() instanceof net.minecraft.world.entity.LivingEntity le && EntityGroup.getMobType(le).equals(condition.getEnumValue("group", EntityGroup.class)))));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("elytra_flight_possible"), (condition, entity) -> {
			boolean hasElytraPower = ElytraFlightPower.elytra.contains(entity);
			boolean hasElytraEquipment = false;
			if (entity instanceof LivingEntity li) {
				for (ItemStack item : li.getEquipment().getArmorContents()) {
					if (hasElytraEquipment) break;
					if (item == null) continue;
					if (item.getType().equals(Material.ELYTRA)) {
						hasElytraEquipment = true;
					}
				}
			}
			return hasElytraPower || hasElytraEquipment;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("fall_distance"), (condition, entity) -> Comparison.fromString(condition.getString("comparison")).compare(entity.getFallDistance(), condition.getNumber("compare_to").getFloat())));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("gamemode"), (condition, entity) -> {
			if (entity instanceof Player player) {
				return player.getGameMode().equals(GameMode.valueOf(condition.getString("gamemode").toUpperCase()));
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("glowing"), (condition, entity) -> entity.isGlowing()));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("health"), (condition, entity) -> {
			if (entity instanceof LivingEntity le) {
				return Comparison.fromString(condition.getString("comparison")).compare(le.getHealth(), condition.getNumber("compare_to").getFloat());
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("in_block"), (condition, entity) -> {
			return ConditionExecutor.testBlock(condition.getJsonObject("block_condition"), CraftBlock.at(entity.getHandle().level(), CraftLocation.toBlockPosition(entity.getLocation())));
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("in_block_anywhere"), (condition, entity) -> {
			int stopAt = -1;
			Comparison comparison = Comparison.fromString(condition.getStringOrDefault("comparison", ">="));
			int compareTo = condition.getNumberOrDefault("compare_to", 1).getInt();
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
						boolean pass = true;
						if (condition.isPresent("block_condition")) {
							pass = ConditionExecutor.testBlock(condition.getJsonObject("block_condition"), CraftBlock.at(entity.getHandle().level(), mutable.immutable()));
						}

						if (pass) count++;
					}
				}
			}
			return comparison.compare(count, compareTo);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("in_tag"), (condition, entity) -> {
			NamespacedKey tag = NamespacedKey.fromString(condition.getString("tag"));
			TagKey key = TagKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, CraftNamespacedKey.toMinecraft(tag));
			return CraftEntityType.bukkitToMinecraft(entity.getType()).is(key);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("living"), (condition, entity) -> !entity.isDead()));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("moving"), (condition, entity) -> isEntityMoving(entity)));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("on_block"), (condition, entity) -> {
			if (!condition.isPresent("block_condition")) {
				return entity.isOnGround();
			} else {
				return ConditionExecutor.testBlock(condition.getJsonObject("block_condition"), (CraftBlock) entity.getLocation().add(0, -1, 0).getBlock());
			}
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("biome"), (condition, entity) -> {
			if (condition.isPresent("condition")) {
				return ConditionExecutor.testBiome(condition.getJsonObject("condition"), entity.getLocation().getBlock().getBiome(), entity.getLocation());
			} else { // Assumed to be trying to get biome type
				String key = condition.getString("biome");
				if (key.contains(":")) {
					key = key.split(":")[1];
				}
				return entity.getLocation().getBlock().getBiome().equals(Biome.valueOf(key.toUpperCase()));
			}
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("raycast"), (condition, entity) -> RaycastUtils.condition(condition, entity.getHandle())));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("relative_health"), (condition, entity) -> {
			if (entity instanceof LivingEntity le) {
				String comparison = condition.getString("comparison");
				double compare_to = condition.getNumber("compare_to").getFloat();
				double fin = le.getHealth() / le.getMaxHealth();
				return Comparison.fromString(comparison).compare(fin, compare_to);
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("riding"), (condition, entity) -> {
			if (entity.getVehicle() != null) {
				if (condition.isPresent("bientity_condition")) {
					return ConditionExecutor.testBiEntity(condition.getJsonObject("bientity_condition"), entity, (CraftEntity) entity.getVehicle());
				}
				return true;
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("riding_root"), (condition, entity) -> {
			if (entity.getVehicle() != null) {
				if (condition.isPresent("bientity_condition")) {
					return ConditionExecutor.testBiEntity(condition.getJsonObject("bientity_condition"), entity, (CraftEntity) entity.getVehicle());
				}
				return true;
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("riding_recursive"), (condition, entity) -> {
			int count = 0;
			if (entity.getVehicle() != null) {
				Entity vehicle = entity.getVehicle();
				boolean pass = ConditionExecutor.testBiEntity(condition.getJsonObject("bientity_condition"), entity, (CraftEntity) vehicle);
				while (vehicle != null) {
					if (pass) {
						count++;
					}
					vehicle = vehicle.getVehicle();
				}
			}
			String comparison = condition.getString("comparison");
			double compare_to = condition.getNumber("compare_to").getFloat();
			return Comparison.fromString(comparison).compare(count, compare_to);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("passenger_recursive"), (condition, entity) -> {
			int count = 0;
			if (entity.getPassengers() != null && !entity.getPassengers().isEmpty()) {
				if (condition.isPresent("bientity_condition")) {
					count = (int) entity.getPassengers().stream().filter(ent -> ConditionExecutor.testBiEntity(condition.getJsonObject("bientity_condition"), (CraftEntity) ent, entity)).count();
				} else {
					count = entity.getPassengers().size();
				}
			}
			String comparison = condition.getStringOrDefault("comparison", ">=");
			int compare_to = condition.getNumber("compare_to").getInt();
			return Comparison.fromString(comparison).compare(count, compare_to);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("passenger"), (condition, entity) -> {
			int count = 0;
			if (entity.getPassengers() != null && !entity.getPassengers().isEmpty()) {
				if (condition.isPresent("bientity_condition")) {
					count = (int) entity.getPassengers().stream().filter(ent -> ConditionExecutor.testBiEntity(condition.getJsonObject("bientity_condition"), (CraftEntity) ent, entity)).count();
				} else {
					count = entity.getPassengers().size();
				}
			}
			String comparison = condition.getStringOrDefault("comparison", ">=");
			int compare_to = condition.getNumber("compare_to").getInt();
			return Comparison.fromString(comparison).compare(count, compare_to);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("saturation_level"), (condition, entity) -> {
			if (entity instanceof Player le) {
				String comparison = condition.getString("comparison");
				double compare_to = condition.getNumber("compare_to").getFloat();
				double fin = le.getSaturation();
				return Comparison.fromString(comparison).compare(fin, compare_to);
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("status_effect"), (condition, entity) -> {
			if (entity instanceof LivingEntity le) {
				if (entity != null && Utils.getPotionEffectType(condition.getString("effect")) != null) {
					for (PotionEffect effect : le.getActivePotionEffects()) {
						return effect.getType().equals(Utils.getPotionEffectType(condition.getString("effect")))
							&& effect.getAmplifier() >= condition.getNumberOrDefault("min_amplifier", 0).getInt()
							&& effect.getAmplifier() <= condition.getNumberOrDefault("max_amplifier", Integer.MAX_VALUE).getInt()
							&& effect.getDuration() >= condition.getNumberOrDefault("min_duration", 0).getInt()
							&& effect.getDuration() <= condition.getNumberOrDefault("max_duration", Integer.MAX_VALUE).getInt();
					}
				}
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("swimming"), (condition, entity) -> {
			if (entity instanceof LivingEntity le) {
				return le.isSwimming();
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("tamed"), (condition, entity) -> {
			if (entity instanceof Tameable tameable) {
				return tameable.isTamed();
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("time_of_day"), (condition, entity) -> {
			String comparison = condition.getString("comparison");
			double compare_to = condition.getNumber("compare_to").getFloat();
			return Comparison.fromString(comparison).compare(entity.getWorld().getTime(), compare_to);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("set_size"), (condition, entity) -> {
			NamespacedKey key = condition.getNamespacedKey("set");
			String comparison = condition.getString("comparison");
			int compare_to = condition.getNumber("compare_to").getInt();
			return Comparison.fromString(comparison).compare(EntitySetPower.entity_sets.getOrDefault(key.toString(), new ArrayList<>()).size(), compare_to);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("predicate"), (condition, entity) -> {
			ServerLevel level = (ServerLevel) entity.getHandle().level();
			ResourceLocation location = CraftNamespacedKey.toMinecraft(
				condition.getNamespacedKey("predicate")
			);

			LootItemCondition predicate = GenesisMC.server.registryAccess().registry(net.minecraft.core.registries.Registries.PREDICATE).orElseThrow().get(location);

			LootParams params = new LootParams.Builder(level)
				.withParameter(LootContextParams.ORIGIN, entity.getHandle().position())
				.withOptionalParameter(LootContextParams.THIS_ENTITY, entity.getHandle())
				.create(LootContextParamSets.COMMAND);

			LootContext context = new LootContext.Builder(params).create(Optional.empty());

			return predicate.test(context);
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("using_effective_tool"), (condition, entity) -> {
			if (entity instanceof Player player) {
				ServerPlayer p = ((CraftPlayer) player).getHandle();
				if (ActionOnBlockBreak.playersMining.containsKey(p.getBukkitEntity()) && ActionOnBlockBreak.playersMining.get(p.getBukkitEntity())) {
					BlockState state = p.level().getBlockState(ActionOnBlockBreak.playersMiningBlockPos.get(p.getBukkitEntity()));
					return p.hasCorrectToolForDrops(state);
				}
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("using_item"), (condition, entity) -> {
			if (entity instanceof CraftLivingEntity le && le.getHandle().isUsingItem()) {
				InteractionHand hand = le.getHandle().getUsedItemHand();
				net.minecraft.world.item.ItemStack stack = le.getHandle().getItemInHand(hand);
				boolean pass = true;
				if (condition.isPresent("item_condition")) {
					pass = ConditionExecutor.testItem(condition.getJsonObject("item_condition"), stack.getBukkitStack());
				}
				return pass;
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("xp_levels"), (condition, entity) -> {
			if (entity instanceof Player p) {
				String comparison = condition.getString("comparison");
				double compare_to = condition.getNumber("compare_to").getFloat();
				return Comparison.fromString(comparison).compare(p.getExpToLevel(), compare_to);
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("xp_points"), (condition, entity) -> {
			if (entity instanceof Player p) {
				String comparison = condition.getString("comparison");
				double compare_to = condition.getNumber("compare_to").getFloat();
				return Comparison.fromString(comparison).compare(p.getTotalExperience(), compare_to);
			}
			return false;
		}));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("in_snow"), (condition, entity) -> entity.isInPowderedSnow()));
		register(new ConditionFactory(GenesisMC.apoliIdentifier("in_thunderstorm"), (condition, entity) -> entity.isInRain() && entity.getWorld().isThundering()));
	}

	private void register(EntityConditions.ConditionFactory factory) {
		GenesisMC.getPlugin().registry.retrieve(Registries.ENTITY_CONDITION).register(factory);
	}

	public boolean isEntityMoving(Entity entity) {
		int entID = entity.getEntityId();
		Location prevLocat = prevLoca[entID];
		Location cuLo = entity.getLocation();
		prevLoca[entID] = cuLo;

		return !cuLo.equals(prevLocat);
	}

	public class ConditionFactory implements Registrable {
		NamespacedKey key;
		BiPredicate<FactoryJsonObject, CraftEntity> test;

		public ConditionFactory(NamespacedKey key, BiPredicate<FactoryJsonObject, CraftEntity> test) {
			this.key = key;
			this.test = test;
		}

		public boolean test(FactoryJsonObject condition, CraftEntity tester) {
			return test.test(condition, tester);
		}

		@Override
		public NamespacedKey getKey() {
			return key;
		}
	}
}
