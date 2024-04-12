package me.dueris.genesismc.factory.conditions.types;

import com.mojang.brigadier.StringReader;
import me.dueris.calio.registry.Registerable;
import me.dueris.calio.registry.Registrar;
import me.dueris.calio.util.MiscUtils;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Comparison;
import me.dueris.genesismc.factory.data.types.Shape;
import me.dueris.genesismc.factory.data.types.VectorGetter;
import me.dueris.genesismc.factory.powers.ApoliPower;
import me.dueris.genesismc.factory.powers.apoli.*;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.RaycastUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataType;
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
import org.bukkit.craftbukkit.v1_20_R3.CraftRegistry;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntityType;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

public class EntityConditions {
    public static HashMap<String, ArrayList<EntityType>> entityTagMappings = new HashMap<>();
    private final Location[] prevLoca = new Location[100000];

    public void prep() {
        register(new ConditionFactory(GenesisMC.apoliIdentifier("ability"), (condition, entity) -> {
            if (entity instanceof Player p) {
                String ability = condition.get("ability").toString().toLowerCase();

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
                if (c.getPowerFile().equals(condition.get("power_type").toString())) {
                    return c.getPowerArray().contains(entity);
                }
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("origin"), (condition, entity) -> entity instanceof Player p && OriginPlayerAccessor.hasOrigin(p, condition.get("origin").toString())));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("power_active"), (condition, entity) -> {
            if (!ApoliPower.powers_active.containsKey(entity)) return false;
            String power = condition.get("power").toString();
            return ApoliPower.powers_active.get(entity).getOrDefault(power, false);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("advancement"), (condition, entity) -> {
            MinecraftServer server = GenesisMC.server;
            if (entity instanceof CraftPlayer p) {
                NamespacedKey namespacedKey = NamespacedKey.fromString(condition.get("advancement").toString());

                AdvancementHolder advancementHolder = server.getAdvancements().get(CraftNamespacedKey.toMinecraft(namespacedKey));
                if(advancementHolder == null){
                    GenesisMC.getPlugin().getLogger().severe("Advancement \"{}\" did not exist but was referenced in the apoli:advancement entity condition!".replace("{}", namespacedKey.asString()));
                    return false;
                }

                return p.getHandle().getAdvancements().getOrStartProgress(advancementHolder).isDone();
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("sprinting"), (condition, entity) -> (entity instanceof CraftPlayer player && player.isSprinting()) || OriginPlayerAccessor.currentSprintingPlayersFallback.contains(entity)));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("food_level"), (condition, entity) -> {
            String comparison = condition.get("comparison").toString();
            int compare_to = Integer.parseInt(condition.get("compare_to").toString());
            return entity instanceof Player p && Comparison.getFromString(comparison).compare(p.getFoodLevel(), compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("air"), (condition, entity) -> {
            if (entity instanceof Player p) {
                return Comparison.getFromString(condition.get("comparison").toString()).compare(p.getRemainingAir(), Integer.parseInt(condition.get("compare_to").toString()));
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("block_collision"), (condition, entity) -> {
            net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
            AABB boundingBox = nmsEntity.getBoundingBox();
            double offsetX = Utils.getToDouble(condition.getOrDefault("offset_x", 0));
            double offsetY = Utils.getToDouble(condition.getOrDefault("offset_y", 0));
            double offsetZ = Utils.getToDouble(condition.getOrDefault("offset_z", 0));
            AABB offsetBox = boundingBox.move(offsetX, offsetY, offsetZ);

            ServerLevel level = (ServerLevel) nmsEntity.level();
            BlockCollisions<BlockPos> spliterator = new BlockCollisions<>(level, nmsEntity, offsetBox, false, (pos, shape) -> pos);

            while (spliterator.hasNext()) {
                BlockPos pos = spliterator.next();
                boolean pass = true;

                if (condition.containsKey("block_condition"))
                    pass = ConditionExecutor.testBlock((JSONObject) condition.get("block_condition"), CraftBlock.at(level, pos));

                if (pass) {
                    return true;
                }
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("block_in_radius"), (condition, entity) -> {
            int radius = Math.toIntExact((Long) condition.getOrDefault("radius", 15L));
            Shape shape = Shape.getShape(condition.getOrDefault("shape", "cube"));
            String comparison = condition.getOrDefault("comparison", ">=").toString();
            int compare_to = Integer.parseInt(condition.getOrDefault("compare_to", 1).toString());

            boolean hasCondition = condition.containsKey("block_condition");
            int stopAt = -1;
            Comparison fixedComparison = Comparison.getFromString(comparison);
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
                    if (!ConditionExecutor.testBlock((JSONObject) condition.get("block_condition"), CraftBlock.at(((CraftWorld) entity.getWorld()).getHandle(), pos))) {
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
            String tag = condition.get("set").toString();
            ArrayList<Entity> entities = EntitySetPower.entity_sets.get(tag);
            if (entities.contains(entity)) {
                String comparison = condition.get("comparison").toString();
                int compare_to = Integer.parseInt(condition.get("compare_to").toString());
                return Comparison.getFromString(comparison).compare(entities.size(), compare_to);
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("scoreboard"), (condition, entity) -> {
            String name = condition.get("name").toString();
            if (name == null) {
                if (entity instanceof Player player) name = player.getName();
                else name = entity.getUniqueId().toString();
            }

            Scoreboard scoreboard = entity.getHandle().level().getScoreboard();
            Objective value = scoreboard.getObjective(condition.get("objective").toString());

            if (value != null && scoreboard.getPlayerScoreInfo(entity.getHandle(), value) != null) {
                int score = scoreboard.getPlayerScoreInfo(entity.getHandle(), value).value();
                String comparison = condition.get("comparison").toString();
                int compare_to = Integer.parseInt(condition.get("compare_to").toString());
                return Comparison.getFromString(comparison).compare(score, compare_to);
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("weather_check"), (condition, entity) -> {
            boolean thunder = (boolean) condition.getOrDefault("thundering", false);
            boolean rain = (boolean) condition.getOrDefault("raining", false);
            boolean clear = (boolean) condition.getOrDefault("clear", false);
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
            String comparison = condition.get("comparison").toString();
            double compare_to = Double.parseDouble(condition.get("compare_to").toString());
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
            return Comparison.getFromString(comparison).compare(brightness, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("light_level"), (condition, entity) -> {
            String comparison = condition.get("comparison").toString();
            double compare_to = Double.parseDouble(condition.get("compare_to").toString());
            int lightLevel = entity.getLocation().getBlock().getLightLevel();
            return Comparison.getFromString(comparison).compare(lightLevel, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("climbing"), (condition, entity) -> {
            if (entity instanceof Player player) {
                Climbing climbing = new Climbing();
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
        register(new ConditionFactory(GenesisMC.apoliIdentifier("dimension"), (condition, entity) -> {
            String dim = condition.get("dimension").toString();
            if (!dim.contains(":")) {
                dim = "minecraft:" + dim;
            }
            return entity.getWorld().getKey().equals(NamespacedKey.fromString(dim));
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("fluid_height"), (condition, entity) -> {
            String comparison = condition.get("comparison").toString();
            double compare_to = Double.parseDouble(condition.get("compare_to").toString());

            NamespacedKey tag = NamespacedKey.fromString(condition.get("fluid").toString());
            TagKey key = TagKey.create(net.minecraft.core.registries.Registries.FLUID, CraftNamespacedKey.toMinecraft(tag));

            return Comparison.getFromString(comparison).compare(Utils.apoli$getFluidHeightLoosely(entity.getHandle(), key), compare_to);
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
        register(new ConditionFactory(GenesisMC.apoliIdentifier("nbt"), (condition, entity) -> NbtUtils.compareNbt(MiscUtils.ParserUtils.parseJson(new StringReader(condition.get("nbt").toString()), CompoundTag.CODEC), entity.getHandle().saveWithoutId(new CompoundTag()), true)));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("sneaking"), (condition, entity) -> entity.isSneaking()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("resource"), (condition, entity) -> {
            if (CooldownUtils.cooldownMap.containsKey(entity) && CooldownUtils.cooldownMap.get(entity).contains(condition.get("resource").toString())) {
                return !CooldownUtils.isPlayerInCooldownFromTag((Player) entity, condition.get("resource").toString());
            } else {
                if (Resource.registeredBars.containsKey(entity) && Resource.registeredBars.get(entity).containsKey(condition.get("resource").toString())) {
                    String comparison = condition.get("comparison").toString();
                    double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                    return Comparison.getFromString(comparison).compare(Resource.getResource(entity, condition.get("resource").toString()).left().getProgress(), compare_to);
                } else {
                    return false;
                }
            }
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("fall_flying"), (condition, entity) -> entity instanceof LivingEntity le && (((CraftLivingEntity) le).getHandle().isFallFlying() || FlightElytra.getGlidingPlayers().contains(le))));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("submerged_in"), (condition, entity) -> {
            String formatted = NamespacedKey.fromString(condition.get("fluid").toString()).asString();
            if (formatted.equalsIgnoreCase("minecraft:water")) {
                return entity.isInWaterOrBubbleColumn();
            } else if (formatted.equalsIgnoreCase("minecraft:lava")) {
                return entity.isInLava();
            } else {
                return false;
            }
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("enchantment"), (condition, entity) -> {
            if (entity instanceof Player player) {
                NamespacedKey enchantmentNamespace = NamespacedKey.fromString(condition.get("enchantment").toString());
                String comparison = condition.get("comparison").toString();
                Object compareTo = condition.get("compare_to");
                int value = 1;

                Enchantment enchantment = CraftRegistry.ENCHANTMENT.get(enchantmentNamespace);
                switch(condition.get("calculation").toString()){
                    case "sum":
                        for(net.minecraft.world.item.ItemStack stack : CraftEnchantment.bukkitToMinecraft(enchantment).getSlotItems(((CraftPlayer)player).getHandle()).values()){
                            value += EnchantmentHelper.getItemEnchantmentLevel(CraftEnchantment.bukkitToMinecraft(enchantment), stack);
                        }
                        break;
                    case "max":
                        int equippedEnchantmentLevel = 0;

                        for (net.minecraft.world.item.ItemStack stack : CraftEnchantment.bukkitToMinecraft(enchantment).getSlotItems(((CraftPlayer)player).getHandle()).values()) {

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
                return Comparison.getFromString(comparison).compare(value, Utils.getToInt(compareTo));
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("on_fire"), (condition, entity) -> entity.getFireTicks() > 0));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("entity_type"), (condition, entity) -> entity.getType().equals(EntityType.valueOf(condition.get("entity_type").toString().toUpperCase().split(":")[1]))));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("equipped_item"), (condition, entity) -> {
            if (entity instanceof InventoryHolder invH) {
                if (invH instanceof LivingEntity LeInvH) {
                    EquipmentSlot eSlot = Actions.getSlotFromString(condition.get("equipment_slot").toString());
                    if (eSlot != null) {
                        if (LeInvH.getEquipment().getItem(eSlot) != null) {
                            if (condition.get("item_condition") != null) {
                                return ConditionExecutor.testItem((JSONObject) condition.get("item_condition"), LeInvH.getEquipment().getItem(eSlot));
                            } else {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("exists"), (condition, entity) -> entity != null));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("distance_from_coordinates"), (condition, entity) -> {
            CraftEntity craftEntity = (CraftEntity) entity;
            boolean scaleReferenceToDimension = (boolean) condition.getOrDefault("scale_reference_to_dimension", true);
            boolean setResultOnWrongDimension = condition.containsKey("result_on_wrong_dimension"), resultOnWrongDimension = setResultOnWrongDimension && (boolean) condition.get("result_on_wrong_dimension");
            double x = 0, y = 0, z = 0;
            Vec3 pos = entity.getHandle().position();
            ServerLevel level = (ServerLevel) entity.getHandle().level();

            double currentDimensionCoordinateScale = level.dimensionType().coordinateScale();
            switch (condition.getOrDefault("reference", "world_origin").toString()) {
                case "player_natural_spawn", "world_spawn", "player_spawn" :
                    if(setResultOnWrongDimension && level.dimension() != Level.OVERWORLD)
                        return resultOnWrongDimension;
                    BlockPos spawnPos = level.getSharedSpawnPos();
                    x = spawnPos.getX();
                    y = spawnPos.getY();
                    z = spawnPos.getZ();
                    break;
                case "world_origin":
                    break;
            }

            Vec3 coords = VectorGetter.getNMSVector((JSONObject) condition.getOrDefault("coordinates", new JSONObject(Map.of("x", 0, "y", 0, "z", 0))));
            Vec3 offset = VectorGetter.getNMSVector((JSONObject) condition.getOrDefault("offset", new JSONObject(Map.of("x", 0, "y", 0, "z", 0))));
            x += coords.x + offset.x;
            y += coords.y + offset.y;
            z += coords.z + offset.z;
            if(scaleReferenceToDimension && (x != 0 || z != 0)){
                Comparison comparison = Comparison.getFromString(condition.get("comparison").toString());
                if(currentDimensionCoordinateScale == 0) return comparison == Comparison.NOT_EQUAL || comparison == Comparison.GREATER_THAN || comparison == Comparison.GREATER_THAN_OR_EQUAL;

                x /= currentDimensionCoordinateScale;
                z /= currentDimensionCoordinateScale;
            }

            double distance,
                xDistance = (boolean) condition.getOrDefault("ignore_x", false) ? 0 : Math.abs(pos.x() - x),
                yDistance = (boolean) condition.getOrDefault("ignore_y", false) ? 0 : Math.abs(pos.y() - y),
                zDistance = (boolean) condition.getOrDefault("ignore_z", false) ? 0 : Math.abs(pos.z() - z);
            if((boolean) condition.getOrDefault("scale_distance_to_dimension", false)){
                xDistance *= currentDimensionCoordinateScale;
                zDistance *= currentDimensionCoordinateScale;
            }

            distance = Shape.getDistance(Shape.getShape(condition.getOrDefault("shape", "cube")), xDistance, yDistance, zDistance);

            if(condition.containsKey("round_to_digit")){
                distance = new BigDecimal(distance).setScale((int) condition.get("round_to_digit"), RoundingMode.HALF_UP).doubleValue();
            }

            return Comparison.getFromString(condition.get("comparison").toString()).compare(distance, Utils.getToDouble(condition.get("compare_to")));
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("elytra_flight_possible"), (condition, entity) -> {
            boolean hasElytraPower = FlightElytra.elytra.contains(entity);
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
        register(new ConditionFactory(GenesisMC.apoliIdentifier("fall_distance"), (condition, entity) -> Comparison.getFromString(condition.get("comparison").toString()).compare(entity.getFallDistance(), Double.parseDouble(condition.get("compare_to").toString()))));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("gamemode"), (condition, entity) -> {
            if (entity instanceof Player player) {
                return player.getGameMode().equals(GameMode.valueOf(condition.get("gamemode").toString().toUpperCase()));
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("glowing"), (condition, entity) -> entity.isGlowing()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("health"), (condition, entity) -> {
            if (entity instanceof LivingEntity le) {
                return Comparison.getFromString(condition.get("comparison").toString()).compare(le.getHealth(), Double.parseDouble(condition.get("compare_to").toString()));
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("in_block"), (condition, entity) -> {
            if (entity.getLocation().getBlock().getType().isCollidable()) {
                return !condition.containsKey("block_condition") || ConditionExecutor.testBlock((JSONObject) condition.get("block_condition"), (CraftBlock) entity.getLocation().getBlock());
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("in_block_anywhere"), (condition, entity) -> {
            int stopAt = -1;
            Comparison comparison = Comparison.getFromString(condition.get("comparison").toString());
            int compareTo = Utils.getToInt(condition.get("compare_to"));
            switch(comparison) {
                case EQUAL: case LESS_THAN_OR_EQUAL: case GREATER_THAN: case NOT_EQUAL:
                    stopAt = compareTo + 1;
                    break;
                case LESS_THAN: case GREATER_THAN_OR_EQUAL:
                    stopAt = compareTo;
                    break;
            }
            int count = 0;
            AABB box = entity.getHandle().getBoundingBox();
            BlockPos blockPos = BlockPos.containing(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D);
            BlockPos blockPos2 = BlockPos.containing(box.maxX - 0.001D, box.maxY - 0.001D, box.maxZ - 0.001D);
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            for(int i = blockPos.getX(); i <= blockPos2.getX() && count < stopAt; ++i) {
                for(int j = blockPos.getY(); j <= blockPos2.getY() && count < stopAt; ++j) {
                    for(int k = blockPos.getZ(); k <= blockPos2.getZ() && count < stopAt; ++k) {
                        mutable.set(i, j, k);
                        boolean pass = true;
                        if(condition.containsKey("block_condition")){
                            pass = ConditionExecutor.testBlock((JSONObject) condition.get("block_condition"), CraftBlock.at(entity.getHandle().level(), mutable.immutable()));
                        }

                        if(pass) count++;
                    }
                }
            }
            return comparison.compare(count, compareTo);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("in_tag"), (condition, entity) -> {
            NamespacedKey tag = NamespacedKey.fromString(condition.get("tag").toString());
            TagKey key = TagKey.create(net.minecraft.core.registries.Registries.ENTITY_TYPE, CraftNamespacedKey.toMinecraft(tag));
            return CraftEntityType.bukkitToMinecraft(entity.getType()).is(key);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("living"), (condition, entity) -> !entity.isDead()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("moving"), (condition, entity) -> isEntityMoving(entity)));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("on_block"), (condition, entity) -> {
            if (condition.get("block_condition") == null) {
                return entity.isOnGround();
            } else {
                return ConditionExecutor.testBlock((JSONObject) condition.get("block_condition"), (CraftBlock) entity.getLocation().add(0, -1, 0).getBlock());
            }
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("biome"), (condition, entity) -> {
            if (condition.containsKey("condition")) {
                return ConditionExecutor.testBiome((JSONObject) condition.get("condition"), entity.getLocation().getBlock().getBiome(), entity.getLocation());
            } else { // Assumed to be trying to get biome type
                String key = condition.get("biome").toString();
                if (key.contains(":")) {
                    key = key.split(":")[1];
                }
                return entity.getLocation().getBlock().getBiome().equals(Biome.valueOf(key.toUpperCase()));
            }
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("raycast"), (condition, entity) -> RaycastUtils.condition(condition, entity.getHandle())));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("relative_health"), (condition, entity) -> {
            if (entity instanceof LivingEntity le) {
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                double fin = le.getHealth() / le.getMaxHealth();
                return Comparison.getFromString(comparison).compare(fin, compare_to);
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("riding"), (condition, entity) -> {
            if (entity.getVehicle() != null) {
                if (condition.containsKey("bientity_condition")) {
                    return ConditionExecutor.testBiEntity((JSONObject) condition.get("bientity_condition"), entity, (CraftEntity) entity.getVehicle());
                }
                return true;
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("riding_root"), (condition, entity) -> {
            if (entity.getVehicle() != null) {
                if (condition.containsKey("bientity_condition")) {
                    return ConditionExecutor.testBiEntity((JSONObject) condition.get("bientity_condition"), entity, (CraftEntity) entity.getVehicle());
                }
                return true;
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("riding_recursive"), (condition, entity) -> {
            int count = 0;
            if (entity.getVehicle() != null) {
                Entity vehicle = entity.getVehicle();
                boolean pass = ConditionExecutor.testBiEntity((JSONObject) condition.get("bientity_condition"), entity, (CraftEntity) vehicle);
                while (vehicle != null) {
                    if (pass) {
                        count++;
                    }
                    vehicle = vehicle.getVehicle();
                }
            }
            String comparison = condition.get("comparison").toString();
            double compare_to = Double.parseDouble(condition.get("compare_to").toString());
            return Comparison.getFromString(comparison).compare(count, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("passenger_recursive"), (condition, entity) -> {
            int count = 0;
            if (entity.getPassengers() != null && !entity.getPassengers().isEmpty()) {
                if (condition.containsKey("bientity_condition")) {
                    count = (int) entity.getPassengers().stream().filter(ent -> {
                        return ConditionExecutor.testBiEntity((JSONObject) condition.get("bientity_condition"), (CraftEntity) ent, entity);
                    }).count();
                } else {
                    count = entity.getPassengers().size();
                }
            }
            String comparison = condition.getOrDefault("comparison", ">=").toString();
            int compare_to = Integer.parseInt(condition.getOrDefault("compare_to", 1).toString());
            return Comparison.getFromString(comparison).compare(count, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("passenger"), (condition, entity) -> {
            int count = 0;
            if (entity.getPassengers() != null && !entity.getPassengers().isEmpty()) {
                if (condition.containsKey("bientity_condition")) {
                    count = (int) entity.getPassengers().stream().filter(ent -> {
                        return ConditionExecutor.testBiEntity((JSONObject) condition.get("bientity_condition"), (CraftEntity) ent, entity);
                    }).count();
                } else {
                    count = entity.getPassengers().size();
                }
            }
            String comparison = condition.getOrDefault("comparison", ">=").toString();
            int compare_to = Integer.parseInt(condition.getOrDefault("compare_to", 1).toString());
            return Comparison.getFromString(comparison).compare(count, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("saturation_level"), (condition, entity) -> {
            if (entity instanceof Player le) {
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                double fin = le.getSaturation();
                return Comparison.getFromString(comparison).compare(fin, compare_to);
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("status_effect"), (condition, entity) -> {
            if (entity instanceof LivingEntity le) {
                if (entity != null && StackingStatusEffect.getPotionEffectType(condition.get("effect").toString()) != null) {
                    for (PotionEffect effect : le.getActivePotionEffects()) {
                        return effect.getType().equals(StackingStatusEffect.getPotionEffectType(condition.get("effect").toString()))
                            && effect.getAmplifier() >= Integer.parseInt(condition.getOrDefault("min_amplifier", 0).toString())
                            && effect.getAmplifier() <= Integer.parseInt(condition.getOrDefault("max_amplifier", Integer.MAX_VALUE).toString())
                            && effect.getDuration() >= Integer.parseInt(condition.getOrDefault("min_duration", 0).toString())
                            && effect.getDuration() <= Integer.parseInt(condition.getOrDefault("max_duration", Integer.MAX_VALUE).toString());
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
            String comparison = condition.get("comparison").toString();
            double compare_to = Double.parseDouble(condition.get("compare_to").toString());
            return Comparison.getFromString(comparison).compare(entity.getWorld().getTime(), compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("set_size"), (condition, entity) -> {
            NamespacedKey key = NamespacedKey.fromString(condition.get("set").toString());
            String comparison = condition.get("comparison").toString();
            int compare_to = Integer.parseInt(condition.get("compare_to").toString());
            return Comparison.getFromString(comparison).compare(EntitySetPower.entity_sets.getOrDefault(key.toString(), new ArrayList<>()).size(), compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("predicate"), (condition, entity) -> {
            MinecraftServer server = GenesisMC.server;
            ServerLevel level = (ServerLevel) entity.getHandle().level();

            LootItemCondition predicate = server.getLootData().getElement(LootDataType.PREDICATE, CraftNamespacedKey.toMinecraft(
                NamespacedKey.fromString(condition.get("predicate").toString())
            ));

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
                if(condition.containsKey("item_condition")){
                    pass = ConditionExecutor.testItem((JSONObject) condition.get("item_condition"), stack.getBukkitStack());
                }
                return pass;
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("xp_levels"), (condition, entity) -> {
            if (entity instanceof Player p) {
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                return Comparison.getFromString(comparison).compare(p.getExpToLevel(), compare_to);
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("xp_points"), (condition, entity) -> {
            if (entity instanceof Player p) {
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                return Comparison.getFromString(comparison).compare(p.getTotalExperience(), compare_to);
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

    public class ConditionFactory implements Registerable {
        NamespacedKey key;
        BiPredicate<JSONObject, CraftEntity> test;

        public ConditionFactory(NamespacedKey key, BiPredicate<JSONObject, CraftEntity> test) {
            this.key = key;
            this.test = test;
        }

        public boolean test(JSONObject condition, CraftEntity tester) {
            return test.test(condition, tester);
        }

        @Override
        public NamespacedKey getKey() {
            return key;
        }
    }
}
