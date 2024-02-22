package me.dueris.genesismc.factory.conditions;

import com.mojang.brigadier.StringReader;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.TagRegistryParser;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.Power;
import me.dueris.genesismc.factory.powers.apoli.*;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.apoli.RaycastApoli;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.loot.LootDataType;

import net.minecraft.world.level.storage.loot.providers.number.ScoreboardValue;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.tag.CraftTag;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class EntityConditions implements Condition {
    public static HashMap<PowerContainer, ArrayList<String>> inTagValues = new HashMap<>();
    public static HashMap<String, ArrayList<EntityType>> entityTagMappings = new HashMap<>();
    private final Location[] prevLoca = new Location[100000];

    public static Enchantment getEnchantmentByNamespace(String namespaceString) {
        return Enchantment.getByName(namespaceString);
    }

    private static int countBlocksInCube(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, World world, JSONObject condition, Entity entity, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent) {
        int blockCount = 0;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(world, x, y, z);
                    Block blockw = location.getBlock();
                    if (blockw.getType() != Material.AIR) {
                        Optional<Boolean> blockCondition = ConditionExecutor.blockCondition.check(condition, entity, target, blockw, fluid, itemStack, dmgevent);
                        if (blockCondition.isPresent()) {
                            if (blockCondition.get()) {
                                blockCount++;
                            }
                        } else {
                            blockCount++;
                        }
                    }
                }
            }
        }

        return blockCount;
    }

    private static int countBlocksInStar(int centerX, int centerY, int centerZ, int radius, World world, JSONObject condition, Entity entity, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent) {
        int blockCount = 0;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                    if (distance <= radius && distance >= radius / 2) {
                        Location location = new Location(world, x, y, z);
                        Block block2 = location.getBlock();

                        if (block2.getType() != Material.AIR) {
                            Optional<Boolean> blockCondition = ConditionExecutor.blockCondition.check(condition, entity, target, block, fluid, itemStack, dmgevent);
                            if (blockCondition.isPresent()) {
                                if (blockCondition.get()) {
                                    blockCount++;
                                }
                            } else {
                                blockCount++;
                            }
                        }
                    }
                }
            }
        }

        return blockCount;
    }

    public static int countBlocksInSphere(int centerX, int centerY, int centerZ, int radius, World world, JSONObject condition, Entity entity, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent) {
        int blockCount = 0;
        int squaredRadius = radius * radius;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    if ((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY) + (z - centerZ) * (z - centerZ) <= squaredRadius) {
                        Location location = new Location(world, x, y, z);
                        if (location.getBlock().getType() != Material.AIR) {
                            Optional<Boolean> blockCondition = ConditionExecutor.blockCondition.check(condition, entity, target, block, fluid, itemStack, dmgevent);
                            if (blockCondition.isPresent()) {
                                if (blockCondition.get()) {
                                    blockCount++;
                                }
                            } else {
                                blockCount++;
                            }
                        }
                    }
                }
            }
        }

        return blockCount;
    }

    @Override
    public String condition_type() {
        return "ENTITY_CONDITION";
    }

    @Override
    public Optional<Boolean> check(JSONObject condition, Entity entity, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent entityDamageEvent) {
        if (condition.isEmpty()) return Optional.empty();
        boolean inverted = (boolean) condition.getOrDefault("inverted", false);
        if (condition.get("type") == null) return Optional.empty();
        String type = condition.get("type").toString();
        switch (type) {
            case "apoli:ability" -> {
                if (entity instanceof Player p) {
                    String ability = condition.get("ability").toString().toLowerCase();

                    switch (ability) {
                        case "minecraft:flying" -> {
                            return getResult(inverted, Optional.of(((CraftPlayer) p).getHandle().getAbilities().flying));
                        }
                        case "minecraft:instabuild" -> {
                            return getResult(inverted, Optional.of(((CraftPlayer) p).getHandle().getAbilities().instabuild));
                        }
                        case "minecraft:invulnerable" -> {
                            return getResult(inverted, Optional.of(((CraftPlayer) p).getHandle().getAbilities().invulnerable));
                        }
                        case "minecraft:maybuild" -> {
                            return getResult(inverted, Optional.of(((CraftPlayer) p).getHandle().getAbilities().mayBuild));
                        }
                        case "minecraft:mayfly" -> {
                            return getResult(inverted, Optional.of(((CraftPlayer) p).getHandle().getAbilities().mayfly));
                        }
                    }
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:power_type" -> {
                List<Class<? extends CraftPower>> craftPowerClasses = CraftPower.getRegistry();
                for (Class<? extends CraftPower> c : craftPowerClasses) {
                    String pt = condition.get("power_type").toString();
                    try {
                        if (c.newInstance().getPowerFile().equals(pt)) {
                            return getResult(inverted, Optional.of(c.newInstance().getPowerArray().contains(entity)));
                        } else {
                            return getResult(inverted, Optional.of(false));
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            case "apoli:origin" -> {
                return getResult(inverted, Optional.of(entity instanceof Player p && OriginPlayerAccessor.hasOrigin(p, condition.get("origin").toString())));
            }
            case "apoli:power" -> {
                return getResult(inverted, Optional.of(entity instanceof Player p && OriginPlayerAccessor.hasPower(p, condition.get("power").toString())));
            }
            case "apoli:power_active" -> {
                if (!Power.powers_active.containsKey(entity)) return getResult(inverted, Optional.of(false));
                if (condition.get("power").toString().contains("*")) {
                    String[] powerK = condition.get("power").toString().split("\\*");
                    for (String string : Power.powers_active.get(entity).keySet()) {
                        if (string.startsWith(powerK[0]) && string.endsWith(powerK[1])) {
                            return getResult(inverted, Optional.of(Power.powers_active.get(entity).get(string)));
                        }
                    }
                } else {
                    String power = condition.get("power").toString();
                    boolean invert = Boolean.parseBoolean(condition.getOrDefault("inverted", "false").toString());
                    return getResult(invert, Optional.of(Power.powers_active.get(entity).getOrDefault(power, false)));
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:advancement" -> {
                String advancementString = condition.get("advancement").toString();

                if (entity instanceof Player player) {
                    File advancementsFolder = new File(GenesisMC.server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).toAbsolutePath().toString());
                    File playerAdvancementFile = new File(advancementsFolder, player.getUniqueId() + ".json");

                    if (playerAdvancementFile.exists()) {
                        try {
                            JSONParser parser = new JSONParser();
                            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(playerAdvancementFile));
                            JSONObject advancementJson = (JSONObject) jsonObject.get(advancementString);

                            if (advancementJson != null) {
                                Boolean done = (Boolean) advancementJson.get("done");
                                return getResult(inverted, Optional.of(Objects.requireNonNullElse(done, false)));
                            }
                        } catch (IOException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:sprinting" -> {
                return getResult(inverted, Optional.of(entity instanceof Player p && p.isSprinting()));
            }
            case "apoli:food_level" -> {
                String comparison = condition.get("comparison").toString();
                int compare_to = Integer.parseInt(condition.get("compare_to").toString());
                return getResult(inverted, Optional.of(entity instanceof Player p && Utils.compareValues(p.getFoodLevel(), comparison, compare_to)));
            }
            case "apoli:air" -> {
                if (entity instanceof Player p) {
                    return getResult(inverted, Optional.of(Utils.compareValues(p.getRemainingAir(), condition.get("comparison").toString(), Integer.parseInt(condition.get("compare_to").toString()))));
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:attribute" -> {
                if (entity instanceof Player player) {
                    String attributeString = condition.get("attribute").toString().split(":")[1].replace(".", "_").toUpperCase();
                    return getResult(inverted, Optional.of(Utils.compareValues(player.getAttribute(Attribute.valueOf(attributeString)).getValue(), condition.get("comparison").toString(), Integer.parseInt(condition.get("compare_to").toString()))));
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:block_collision" -> {
                String offsetX = condition.get("offset_x").toString();
                String offsetY = condition.get("offset_y").toString();
                String offsetZ = condition.get("offset_z").toString();
                if (entity instanceof Player player) {
                    Location playerLocation = player.getEyeLocation();
                    World world = player.getWorld();

                    int blockX = playerLocation.getBlockX() + Integer.parseInt(offsetX);
                    int blockY = playerLocation.getBlockY() + Integer.parseInt(offsetY);
                    int blockZ = playerLocation.getBlockZ() + Integer.parseInt(offsetZ);

                    Block blockAt = world.getBlockAt(blockX, blockY, blockZ);
                    return getResult(inverted, Optional.of(blockAt.getType().isSolid()));
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:block_in_radius" -> {
                int radius = Math.toIntExact((Long) condition.get("radius"));
                String shape = condition.get("shape").toString();
                String comparison = condition.get("comparison").toString();
                int compare_to = Integer.parseInt(condition.get("compare_to").toString());

                Location center = entity.getLocation();
                int centerX = center.getBlockX();
                int centerY = center.getBlockY();
                int centerZ = center.getBlockZ();
                World world = center.getWorld();

                int minX = center.getBlockX() - radius;
                int minY = center.getBlockY() - radius;
                int minZ = center.getBlockZ() - radius;
                int maxX = center.getBlockX() + radius;
                int maxY = center.getBlockY() + radius;
                int maxZ = center.getBlockZ() + radius;

                int blockCount = 0;
                JSONObject ingredientMap = (JSONObject) condition.get("block_condition");
                if (shape.equalsIgnoreCase("sphere")) {
                    blockCount = countBlocksInSphere(centerX, centerY, centerZ, radius, world, ingredientMap, entity, target, block, fluid, itemStack, entityDamageEvent);
                } else if (shape.equalsIgnoreCase("star")) {
                    blockCount = countBlocksInStar(centerX, centerY, centerZ, radius, world, ingredientMap, entity, target, block, fluid, itemStack, entityDamageEvent);
                } else if (shape.equalsIgnoreCase("cube")) {
                    blockCount = countBlocksInCube(minX, minY, minZ, maxX, maxY, maxZ, world, ingredientMap, entity, target, block, fluid, itemStack, entityDamageEvent);
                } else {
                    return getResult(inverted, Optional.of(false));
                }
                return getResult(inverted, Optional.of(Utils.compareValues(blockCount, comparison, compare_to)));
            }
            case "apoli:set_size" -> {
                String tag = condition.get("set").toString();
                ArrayList<Entity> entities = EntitySetPower.entity_sets.get(tag);
                if(entities.contains(entity)){
                    String comparison = condition.get("comparison").toString();
                    int compare_to = Integer.parseInt(condition.get("compare_to").toString());
                    return getResult(inverted, Optional.of(Utils.compareValues(entities.size(), comparison, compare_to)));
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:scoreboard" -> {
                String name = condition.get("name").toString();
                if(name == null){
                    if(entity instanceof Player player) name = player.getName();
                    else name = entity.getUniqueId().toString();
                }

                Scoreboard scoreboard = ((CraftEntity)entity).getHandle().level().getScoreboard();
                Objective value = scoreboard.getObjective(condition.get("objective").toString());

                if(value != null && scoreboard.getPlayerScoreInfo(((CraftEntity)entity).getHandle(), value) != null){
                    int score = scoreboard.getPlayerScoreInfo(((CraftEntity)entity).getHandle(), value).value();
                    String comparison = condition.get("comparison").toString();
                    int compare_to = Integer.parseInt(condition.get("compare_to").toString());
                    return getResult(inverted, Optional.of(Utils.compareValues(score, comparison, compare_to)));
                }
            }
            case "apoli:weather_check" -> {
                boolean thunder = (boolean) condition.getOrDefault("thundering", false);
                boolean rain = (boolean) condition.getOrDefault("raining", false);
                boolean clear = (boolean) condition.getOrDefault("clear", false);
                if (thunder) {
                    return getResult(inverted, Optional.of(entity.getWorld().isThundering()));
                } else if (rain) {
                    return getResult(inverted, Optional.of(entity.getWorld().getClearWeatherDuration() == 0));
                } else if (clear) {
                    return getResult(inverted, Optional.of(entity.getWorld().getClearWeatherDuration() > 0));
                } else {
                    return getResult(inverted, Optional.of(false));
                }
            }
            case "apoli:brightness" -> {
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
                return getResult(inverted, Optional.of(Utils.compareValues(brightness, comparison, compare_to)));
            }
            case "apoli:light_level" -> {
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                int lightLevel = entity.getLocation().getBlock().getLightLevel();
                return getResult(inverted, Optional.of(Utils.compareValues(lightLevel, comparison, compare_to)));
            }
            case "apoli:climbing" -> {
                if (entity instanceof Player player) {
                    Climbing climbing = new Climbing();
                    if (player.isClimbing()) {
                        return getResult(inverted, Optional.of(true));
                    } else if (climbing.isActiveClimbing(player)) {
                        return getResult(inverted, Optional.of(true));
                    } else {
                        return getResult(inverted, Optional.of(false));
                    }
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:collided_horizontally" -> {
                return getResult(inverted, Optional.of(((CraftEntity)entity).getHandle().horizontalCollision));
            }
            case "apoli:creative_flying" -> {
                if (entity instanceof Player player) {
                    return getResult(inverted, Optional.of(player.isFlying()));
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:daytime" -> {
                return getResult(inverted, Optional.of(entity.getWorld().isDayTime()));
            }
            case "apoli:dimension" -> {
                String dim = condition.get("dimension").toString();
                if (!dim.contains(":")) {
                    dim = "minecraft:" + dim;
                }
                return getResult(inverted, Optional.of(entity.getWorld().getKey().equals(NamespacedKey.fromString(dim))));
            }
            case "apoli:fluid_height" -> {
                String fluidD = condition.get("fluid").toString();

                if (fluidD.equalsIgnoreCase("lava") || fluidD.equalsIgnoreCase("minecraft:lava")) {
                    return getResult(inverted, Optional.of(entity.isInLava()));
                } else if (fluidD.equalsIgnoreCase("water") || fluidD.equalsIgnoreCase("minecraft:water")) {
                    return getResult(inverted, Optional.of(entity.isInWaterOrBubbleColumn()));
                } else {
                    return getResult(inverted, Optional.of(false));
                }
            }
            case "apoli:invisible" -> {
                if (entity instanceof LivingEntity le) {
                    return getResult(inverted, Optional.of(le.isInvisible() || le.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY)));
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:in_rain" -> {
                return getResult(inverted, Optional.of(entity.isInRain()));
            }
            case "apoli:exposed_to_sun" -> {
                ServerLevel level = ((CraftWorld) entity.getWorld()).getHandle();
                BlockPos blockPos = BlockPos.containing(entity.getX(), entity.getY() + ((CraftEntity) entity).getHandle().getEyeHeight(((CraftEntity) entity).getHandle().getPose()), entity.getZ());

                return getResult(inverted, Optional.of(level.canSeeSky(blockPos) && entity.getWorld().isDayTime()));
            }
            case "apoli:exposed_to_sky" -> {
                ServerLevel level = ((CraftWorld) entity.getWorld()).getHandle();
                BlockPos blockPos = BlockPos.containing(entity.getX(), entity.getY() + ((CraftEntity) entity).getHandle().getEyeHeight(((CraftEntity) entity).getHandle().getPose()), entity.getZ());

                return getResult(inverted, Optional.of(level.canSeeSky(blockPos)));
            }
            case "apoli:nbt" -> {
                return getResult(inverted, Optional.of(NbtUtils.compareNbt(Utils.ParserUtils.parseJson(new StringReader(condition.get("nbt").toString()), CompoundTag.CODEC), ((CraftEntity) entity).getHandle().saveWithoutId(new CompoundTag()), true)));
            }
            case "apoli:sneaking" -> {
                return getResult(inverted, Optional.of(entity.isSneaking()));
            }
            case "apoli:resource" -> {
                if (CooldownUtils.cooldowns.containsKey(entity) && CooldownUtils.cooldowns.get(entity).contains(condition.get("resource").toString()) && CooldownUtils.cooldowns.containsKey(entity)) {
                    return getResult(inverted, Optional.of(!CooldownUtils.isPlayerInCooldownFromTag((Player) entity, condition.get("resource").toString())));
                } else {
                    if (Resource.registeredBars.containsKey(entity) && Resource.registeredBars.get(entity).containsKey(condition.get("resource").toString())) {
                        String comparison = condition.get("comparison").toString();
                        double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                        return getResult(inverted, Optional.of(Utils.compareValues(Resource.getResource(entity, condition.get("resource").toString()).getLeft().getProgress(), comparison, compare_to)));
                    } else {
                        return getResult(inverted, Optional.of(false));
                    }
                }
            }
            case "apoli:fall_flying" -> {
                return getResult(inverted, Optional.of(entity instanceof LivingEntity le && (((CraftLivingEntity)le).getHandle().isFallFlying() || FlightElytra.getGlidingPlayers().contains(le))));
            }
            case "apoli:submerged_in" -> {
                if (condition.get("fluid").equals("minecraft:water")) {
                    return getResult(inverted, Optional.of(entity.isInWaterOrBubbleColumn()));
                } else if (condition.get("fluid").equals("minecraft:lava")) {
                    return getResult(inverted, Optional.of(entity.isInLava()));
                } else {
                    return getResult(inverted, Optional.of(false));
                }
            }
            case "apoli:enchantment" -> {
                if (entity instanceof Player player) {
                    String enchantmentNamespace = condition.get("enchantment").toString();
                    String comparison = condition.get("comparison").toString();
                    double compareTo = Double.parseDouble(condition.get("compare_to").toString());

                    for (ItemStack item : player.getInventory().getArmorContents()) {
                        if (item == null) continue;

                        Enchantment enchantment = getEnchantmentByNamespace(enchantmentNamespace);

                        if (enchantment != null) {
                            if (item.containsEnchantment(enchantment)) {
                                int enchantmentLevel = item.getEnchantmentLevel(enchantment);

                                return getResult(inverted, Optional.of(Utils.compareValues(enchantmentLevel, comparison, compareTo)));
                            } else {
                                if (Double.compare(compareTo, 0.0) == 0 && comparison.equals("==")) {
                                    return getResult(inverted, Optional.of(!item.containsEnchantment(enchantment)));
                                }
                            }
                        } else {
                            return getResult(inverted, Optional.of(compareTo == 0 && comparison == "=="));
                            // p.sendMessage("Enchantment not found"); // Spams logs with weird things lol
                        }
                    }
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:on_fire" -> {
                return getResult(inverted, Optional.of(entity.getFireTicks() > 0));
            }
            case "apoli:entity_type" -> {
                return getResult(inverted, Optional.of(entity.getType().equals(EntityType.valueOf(condition.get("entity_type").toString().toUpperCase().split(":")[1]))));
            }
            case "apoli:equipped_item" -> {
                if (entity instanceof InventoryHolder invH) {
                    if (invH instanceof LivingEntity LeInvH) {
                        EquipmentSlot eSlot = Actions.getSlotFromString(condition.get("equipment_slot").toString());
                        if (eSlot != null) {
                            if (LeInvH.getEquipment().getItem(eSlot) != null) {
                                if (condition.get("item_condition") != null) {
                                    ItemConditions itemCondition = ConditionExecutor.itemCondition;
                                    Optional boolIC = itemCondition.check((JSONObject) condition.get("item_condition"), entity, target, block, fluid, LeInvH.getEquipment().getItem(eSlot), entityDamageEvent);
                                    if (boolIC.isPresent()) {
                                        return getResult(inverted, Optional.of((Boolean) boolIC.get()));
                                    }
                                } else {
                                    return getResult(inverted, Optional.of(true));
                                }
                            }
                        }
                    }
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:exists" -> {
                return getResult(inverted, Optional.of(entity != null));
            }
            case "apoli:distance_from_spawn" -> {
                @NotNull Vector actorVector = entity.getLocation().toVector();
                @NotNull Vector targetVector = entity.getWorld().getSpawnLocation().toVector();
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                return getResult(inverted, Optional.of(Utils.compareValues(actorVector.distance(targetVector), comparison, compare_to)));
            }
            case "apoli:elytra_flight_possible" -> {
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
                return getResult(inverted, Optional.of(hasElytraPower || hasElytraEquipment));
            }
            case "apoli:fall_distance" -> {
                return getResult(inverted, Optional.of(Utils.compareValues(entity.getFallDistance(), condition.get("comparison").toString(), Double.parseDouble(condition.get("compare_to").toString()))));
            }
            case "apoli:gamemode" -> {
                if (entity instanceof Player player) {
                    return getResult(inverted, Optional.of(player.getGameMode().equals(GameMode.valueOf(condition.get("gamemode").toString().toUpperCase()))));
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:glowing" -> {
                return getResult(inverted, Optional.of(entity.isGlowing()));
            }
            case "apoli:health" -> {
                if (entity instanceof LivingEntity le) {
                    return getResult(inverted, Optional.of(Utils.compareValues(le.getHealth(), condition.get("comparison").toString(), Double.parseDouble(condition.get("compare_to").toString()))));
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:in_block" -> {
                BlockConditions blockCondition = ConditionExecutor.blockCondition;
                Optional boolB = blockCondition.check((JSONObject) condition.get("block_condition"), entity, target, block, fluid, itemStack, entityDamageEvent);
                if (boolB.isPresent()) {
                    return getResult(inverted, Optional.of((Boolean) boolB.get()));
                } else {
                    return getResult(inverted, Optional.of(false));
                }
            }
            case "apoli:in_tag" -> {
                // Use block in_tag optimization
                try {
                    if (TagRegistryParser.getRegisteredTagFromFileKey(condition.get("tag").toString()) != null) {
                        if (!entityTagMappings.containsKey(condition.get("tag"))) {
                            entityTagMappings.put(condition.get("tag").toString(), new ArrayList<>());
                            for (String mat : TagRegistryParser.getRegisteredTagFromFileKey(condition.get("tag").toString())) {
                                entityTagMappings.get(condition.get("tag")).add(EntityType.valueOf(mat.split(":")[1].toUpperCase()));
                            }
                        } else {
                            // mappings exist, now we can start stuff
                            return getResult(inverted, Optional.of(entityTagMappings.get(condition.get("tag")).contains(entity.getType())));
                        }
                    }
                } catch (IllegalArgumentException e) {
                    // yeah imma just ignore this one ty
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:living" -> {
                return getResult(inverted, Optional.of(!entity.isDead()));
            }
            case "apoli:moving" -> {
                return getResult(inverted, Optional.of(isEntityMoving(entity)));
            }
            case "apoli:on_block" -> {
                BlockConditions blockCondition = ConditionExecutor.blockCondition;
                if (condition.get("block_condition") == null) {
                    return getResult(inverted, Optional.of(entity.isOnGround()));
                } else {
                    Optional<Boolean> boolB = blockCondition.check((JSONObject) condition.get("block_condition"), entity, target, entity.getLocation().add(0, -1, 0).getBlock(), fluid, itemStack, entityDamageEvent);
                    if (boolB.isPresent()) {
                        return getResult(inverted, Optional.of(boolB.get() && entity.isOnGround()));
                    }
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:biome" -> {
                if(condition.containsKey("condition")){
                    Optional<Boolean> bool = ConditionExecutor.biomeCondition.check((JSONObject) condition.get("condition"), entity, target, entity.getLocation().getBlock(), fluid, itemStack, entityDamageEvent);
                    return getResult(inverted, Optional.of(bool.isPresent() && bool.get()));
                }else{ // Assumed to be trying to get biome type
                    String key = condition.get("biome").toString();
                    if(key.contains(":")){
                        key = key.split(":")[1];
                    }
                    return getResult(inverted, Optional.of(entity.getLocation().getBlock().getBiome().equals(Biome.valueOf(key.toUpperCase()))));
                }
            }
            case "apoli:raycast" -> {
                return getResult(inverted, Optional.of(RaycastApoli.condition(condition, ((CraftEntity)entity).getHandle())));
            }
            case "apoli:relative_health" -> {
                if (entity instanceof LivingEntity le) {
                    String comparison = condition.get("comparison").toString();
                    double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                    double fin = le.getHealth() / le.getMaxHealth();
                    return getResult(inverted, Optional.of(Utils.compareValues(fin, comparison, compare_to)));
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:riding", "apoli:riding_root" -> {
                if(entity.getVehicle() != null){
                    if(condition.containsKey("bientity_condition")){
                        Optional<Boolean> bool = ConditionExecutor.biEntityCondition.check((JSONObject) condition.get("bientity_condition"), entity, entity.getVehicle(), null, null, null, null);
                        return getResult(inverted, Optional.of(bool.isPresent() && bool.get()));
                    }
                    return getResult(inverted, Optional.of(true));
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:riding_recursive" -> {
                int count = 0;
                if(entity.getVehicle() != null){
                    Optional<Boolean> bool = ConditionExecutor.biEntityCondition.check((JSONObject) condition.get("bientity_condition"), entity, entity.getVehicle(), null, null, null, null);
                    Entity vehicle = entity.getVehicle();
                    while(vehicle != null){
                        if(bool.isEmpty() || (bool.isPresent() && bool.get())){
                            count++;
                        }
                        vehicle = vehicle.getVehicle();
                    }
                }
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                return getResult(inverted, Optional.of(Utils.compareValues(count, comparison, compare_to)));
            }
            case "apoli:passenger_recursive", "apoli:passenger" -> {
                int count = 0;
                if(entity.getPassengers() != null && !entity.getPassengers().isEmpty()){
                    if(condition.containsKey("bientity_condition")){
                        count = (int) entity.getPassengers().stream().filter(ent -> {
                            Optional<Boolean> bool = ConditionExecutor.biEntityCondition.check((JSONObject) condition.get("bientity_condition"), ent, entity, null, null, null, null);
                            return bool.isPresent() && bool.get();
                        }).count();
                    }else{
                        count = entity.getPassengers().size();
                    }
                }
                String comparison = condition.getOrDefault("comparison", ">=").toString();
                int compare_to = Integer.parseInt(condition.getOrDefault("compare_to", 1).toString());
                return getResult(inverted, Optional.of(Utils.compareValues(count, comparison, compare_to)));
            }
            case "apoli:saturation_level" -> {
                if (entity instanceof Player le) {
                    String comparison = condition.get("comparison").toString();
                    double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                    double fin = le.getSaturation();
                    return getResult(inverted, Optional.of(Utils.compareValues(fin, comparison, compare_to)));
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:status_effect" -> {
                if (entity instanceof LivingEntity le) {
                    if (entity != null && StackingStatusEffect.getPotionEffectType(condition.get("effect").toString()) != null) {
                        for (PotionEffect effect : le.getActivePotionEffects()) {
                            return getResult(inverted, Optional.of(effect.getType().equals(StackingStatusEffect.getPotionEffectType(condition.get("effect").toString()))
                                    && effect.getAmplifier() >= Integer.parseInt(condition.getOrDefault("min_amplifier", 0).toString())
                                    && effect.getAmplifier() <= Integer.parseInt(condition.getOrDefault("max_amplifier", Integer.MAX_VALUE).toString())
                                    && effect.getDuration() >= Integer.parseInt(condition.getOrDefault("min_duration", 0).toString())
                                    && effect.getDuration() <= Integer.parseInt(condition.getOrDefault("max_duration", Integer.MAX_VALUE).toString())));
                        }
                    }
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:swimming" -> {
                if (entity instanceof LivingEntity le) {
                    return getResult(inverted, Optional.of(le.isSwimming()));
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:tamed" -> {
                if (entity instanceof Tameable tameable) {
                    return getResult(inverted, Optional.of(tameable.isTamed()));
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:time_of_day" -> {
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                return getResult(inverted, Optional.of(Utils.compareValues(entity.getWorld().getTime(), comparison, compare_to)));
            }
            case "apoli:using_effective_tool" -> {
                if (entity instanceof Player player) {
                    Predicate<Entity> filter = (entityy) -> !entityy.equals(player);
                    RayTraceResult result = entity.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), AttributeHandler.Reach.getFinalReach(player), FluidCollisionMode.NEVER, false, 0, filter);
                    if (result != null && result.getHitBlock() != null) {
                        return getResult(inverted, Optional.of(result.getHitBlock().getBlockData().isPreferredTool(player.getInventory().getItemInMainHand())));
                    }
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:using_item" -> {
                if (entity instanceof LivingEntity le) {
                    if (le.getActiveItem() != null) {
                        if (condition.get("item_condition") != null) {
                            ItemConditions itemCondition = ConditionExecutor.itemCondition;
                            Optional boolI = itemCondition.check((JSONObject) condition.get("item_condition"), le, target, block, fluid, itemStack, entityDamageEvent);
                            if (boolI.isPresent()) {
                                if (boolI.get().equals(true)) {
                                    return getResult(inverted, Optional.of(true));
                                }
                            }
                        } else {
                            return getResult(inverted, Optional.of(true));
                        }
                    }
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:xp_levels" -> {
                if (entity instanceof Player p) {
                    String comparison = condition.get("comparison").toString();
                    double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                    return getResult(inverted, Optional.of(Utils.compareValues(p.getExpToLevel(), comparison, compare_to)));
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:xp_points" -> {
                if (entity instanceof Player p) {
                    String comparison = condition.get("comparison").toString();
                    double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                    return getResult(inverted, Optional.of(Utils.compareValues(p.getTotalExperience(), comparison, compare_to)));
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:in_snow" -> {
                return getResult(inverted, Optional.of(entity.isInPowderedSnow()));
            }
            case "apoli:in_thunderstorm" -> {
                return getResult(inverted, Optional.of(entity.isInRain() && entity.getWorld().isThundering()));
            }
            default -> {
                return getResult(inverted, Optional.empty());
            }
        }

        return getResult(inverted, Optional.empty());
    }

    public boolean isEntityMoving(Entity entity) {
        int entID = entity.getEntityId();
        Location prevLocat = prevLoca[entID];
        Location cuLo = entity.getLocation();
        prevLoca[entID] = cuLo;

        return !cuLo.equals(prevLocat);
    }
}
