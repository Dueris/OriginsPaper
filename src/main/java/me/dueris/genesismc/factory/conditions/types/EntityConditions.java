package me.dueris.genesismc.factory.conditions.types;

import com.mojang.brigadier.StringReader;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.TagRegistryParser;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.ApoliPower;
import me.dueris.genesismc.factory.powers.apoli.*;
import me.dueris.genesismc.registry.Registerable;
import me.dueris.genesismc.registry.Registrar;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.apoli.RaycastApoli;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class EntityConditions {
    public static HashMap<String, ArrayList<EntityType>> entityTagMappings = new HashMap<>();
    private final Location[] prevLoca = new Location[100000];

    public static Enchantment getEnchantmentByNamespace(String namespaceString) {
        return Enchantment.getByName(namespaceString);
    }

    private static int countBlocksInCube(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, World world, JSONObject condition, Entity entity) {
        int blockCount = 0;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(world, x, y, z);
                    Block blockw = location.getBlock();
                    if (blockw.getType() != Material.AIR) {
                        boolean p = true;
                        if(condition.containsKey("block_condition")){
                            p = ConditionExecutor.testBlock((JSONObject) condition.get("block_condition"), (CraftBlock) blockw);
                        }
                        if(p){
                            blockCount++;
                        }
                    }
                }
            }
        }

        return blockCount;
    }

    private static int countBlocksInStar(int centerX, int centerY, int centerZ, int radius, World world, JSONObject condition, Entity entity) {
        int blockCount = 0;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                    if (distance <= radius && distance >= radius / 2) {
                        Location location = new Location(world, x, y, z);
                        Block block2 = location.getBlock();

                        if (block2.getType() != Material.AIR) {
                            boolean p = true;
                            if(condition.containsKey("block_condition")){
                                p = ConditionExecutor.testBlock((JSONObject) condition.get("block_condition"), (CraftBlock) block2);
                            }
                            if(p){
                                blockCount++;
                            }
                        }
                    }
                }
            }
        }

        return blockCount;
    }

    public static int countBlocksInSphere(int centerX, int centerY, int centerZ, int radius, World world, JSONObject condition, Entity entity) {
        int blockCount = 0;
        int squaredRadius = radius * radius;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    if ((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY) + (z - centerZ) * (z - centerZ) <= squaredRadius) {
                        Location location = new Location(world, x, y, z);
                        if (location.getBlock().getType() != Material.AIR) {
                            boolean p = true;
                            if(condition.containsKey("block_condition")){
                                p = ConditionExecutor.testBlock((JSONObject) condition.get("block_condition"), (CraftBlock) location.getBlock());
                            }
                            if(p){
                                blockCount++;
                            }
                        }
                    }
                }
            }
        }

        return blockCount;
    }

    public void prep(){
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
        register(new ConditionFactory(GenesisMC.apoliIdentifier("origin"), (condition, entity) -> {
            return entity instanceof Player p && OriginPlayerAccessor.hasOrigin(p, condition.get("origin").toString());
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("power_active"), (condition, entity) -> {
            if (!ApoliPower.powers_active.containsKey(entity)) return false;
            if (condition.get("power").toString().contains("*")) {
                String[] powerK = condition.get("power").toString().split("\\*");
                for (String string : ApoliPower.powers_active.get(entity).keySet()) {
                    if (string.startsWith(powerK[0]) && string.endsWith(powerK[1])) {
                        return ApoliPower.powers_active.get(entity).get(string);
                    }
                }
            } else {
                String power = condition.get("power").toString();
                return ApoliPower.powers_active.get(entity).getOrDefault(power, false);
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("advancement"), (condition, entity) -> {
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
                            Objects.requireNonNullElse(done, false);
                        }
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("sprinting"), (condition, entity) -> {
            return entity instanceof Player p && p.isSprinting();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("food_level"), (condition, entity) -> {
            String comparison = condition.get("comparison").toString();
            int compare_to = Integer.parseInt(condition.get("compare_to").toString());
            return entity instanceof Player p && Utils.compareValues(p.getFoodLevel(), comparison, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("air"), (condition, entity) -> {
            if (entity instanceof Player p) {
                return Utils.compareValues(p.getRemainingAir(), condition.get("comparison").toString(), Integer.parseInt(condition.get("compare_to").toString()));
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("block_collision"), (condition, entity) -> {
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
                return blockAt.getType().isSolid();
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("block_in_radius"), (condition, entity) -> {
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
                blockCount = countBlocksInSphere(centerX, centerY, centerZ, radius, world, ingredientMap, entity);
            } else if (shape.equalsIgnoreCase("star")) {
                blockCount = countBlocksInStar(centerX, centerY, centerZ, radius, world, ingredientMap, entity);
            } else if (shape.equalsIgnoreCase("cube")) {
                blockCount = countBlocksInCube(minX, minY, minZ, maxX, maxY, maxZ, world, ingredientMap, entity);
            } else {
                return false;
            }
            return Utils.compareValues(blockCount, comparison, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("set_size"), (condition, entity) -> {
            String tag = condition.get("set").toString();
            ArrayList<Entity> entities = EntitySetPower.entity_sets.get(tag);
            if(entities.contains(entity)){
                String comparison = condition.get("comparison").toString();
                int compare_to = Integer.parseInt(condition.get("compare_to").toString());
                return Utils.compareValues(entities.size(), comparison, compare_to);
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("scoreboard"), (condition, entity) -> {
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
                return Utils.compareValues(score, comparison, compare_to);
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
            return Utils.compareValues(brightness, comparison, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("light_level"), (condition, entity) -> {
            String comparison = condition.get("comparison").toString();
            double compare_to = Double.parseDouble(condition.get("compare_to").toString());
            int lightLevel = entity.getLocation().getBlock().getLightLevel();
            return Utils.compareValues(lightLevel, comparison, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("climbing"), (condition, entity) -> {
            if (entity instanceof Player player) {
                Climbing climbing = new Climbing();
                if (player.isClimbing()) {
                    return true;
                } else if (climbing.isActiveClimbing(player)) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("collided_horizontally"), (condition, entity) -> {
            return ((CraftEntity)entity).getHandle().horizontalCollision;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("creative_flying"), (condition, entity) -> {
            if (entity instanceof Player player) {
                return player.isFlying();
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("daytime"), (condition, entity) -> {
            return entity.getWorld().isDayTime();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("dimension"), (condition, entity) -> {
            String dim = condition.get("dimension").toString();
            if (!dim.contains(":")) {
                dim = "minecraft:" + dim;
            }
            return entity.getWorld().getKey().equals(NamespacedKey.fromString(dim));
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("fluid_height"), (condition, entity) -> {
            String fluidD = condition.get("fluid").toString();

            if (fluidD.equalsIgnoreCase("lava") || fluidD.equalsIgnoreCase("minecraft:lava")) {
                return entity.isInLava();
            } else if (fluidD.equalsIgnoreCase("water") || fluidD.equalsIgnoreCase("minecraft:water")) {
                return entity.isInWaterOrBubbleColumn();
            } else {
                return false;
            }
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("invisible"), (condition, entity) -> {
            if (entity instanceof LivingEntity le) {
                return le.isInvisible() || le.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY);
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("in_rain"), (condition, entity) -> {
            return entity.isInRain();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("exposed_to_sun"), (condition, entity) -> {
            ServerLevel level = ((CraftWorld) entity.getWorld()).getHandle();
            BlockPos blockPos = BlockPos.containing(entity.getX(), entity.getY() + ((CraftEntity) entity).getHandle().getEyeHeight(((CraftEntity) entity).getHandle().getPose()), entity.getZ());

            return level.canSeeSky(blockPos) && entity.getWorld().isDayTime();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("exposed_to_sky"), (condition, entity) -> {
            ServerLevel level = ((CraftWorld) entity.getWorld()).getHandle();
            BlockPos blockPos = BlockPos.containing(entity.getX(), entity.getY() + ((CraftEntity) entity).getHandle().getEyeHeight(((CraftEntity) entity).getHandle().getPose()), entity.getZ());

            return level.canSeeSky(blockPos);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("nbt"), (condition, entity) -> {
            return NbtUtils.compareNbt(Utils.ParserUtils.parseJson(new StringReader(condition.get("nbt").toString()), CompoundTag.CODEC), ((CraftEntity) entity).getHandle().saveWithoutId(new CompoundTag()), true);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("sneaking"), (condition, entity) -> {
            return entity.isSneaking();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("resource"), (condition, entity) -> {
            if (CooldownUtils.cooldowns.containsKey(entity) && CooldownUtils.cooldowns.get(entity).contains(condition.get("resource").toString()) && CooldownUtils.cooldowns.containsKey(entity)) {
                return !CooldownUtils.isPlayerInCooldownFromTag((Player) entity, condition.get("resource").toString());
            } else {
                if (Resource.registeredBars.containsKey(entity) && Resource.registeredBars.get(entity).containsKey(condition.get("resource").toString())) {
                    String comparison = condition.get("comparison").toString();
                    double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                    return Utils.compareValues(Resource.getResource(entity, condition.get("resource").toString()).getLeft().getProgress(), comparison, compare_to);
                } else {
                    return false;
                }
            }
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("fall_flying"), (condition, entity) -> {
            return entity instanceof LivingEntity le && (((CraftLivingEntity)le).getHandle().isFallFlying() || FlightElytra.getGlidingPlayers().contains(le));
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("submerged_in"), (condition, entity) -> {
            if (condition.get("fluid").equals("minecraft:water")) {
                return entity.isInWaterOrBubbleColumn();
            } else if (condition.get("fluid").equals("minecraft:lava")) {
                return entity.isInLava();
            } else {
                return false;
            }
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("enchantment"), (condition, entity) -> {
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

                            return Utils.compareValues(enchantmentLevel, comparison, compareTo);
                        } else {
                            if (Double.compare(compareTo, 0.0) == 0 && comparison.equals("==")) {
                                return !item.containsEnchantment(enchantment);
                            }
                        }
                    } else {
                        return compareTo == 0 && comparison == "==";
                        // p.sendMessage("Enchantment not found"); // Spams logs with weird things lol
                    }
                }
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("on_fire"), (condition, entity) -> {
            return entity.getFireTicks() > 0;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("entity_type"), (condition, entity) -> {
            return entity.getType().equals(EntityType.valueOf(condition.get("entity_type").toString().toUpperCase().split(":")[1]));
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("equipped_item"), (condition, entity) -> {
            if (entity instanceof InventoryHolder invH) {
                if (invH instanceof LivingEntity LeInvH) {
                    EquipmentSlot eSlot = Actions.getSlotFromString(condition.get("equipment_slot").toString());
                    if (eSlot != null) {
                        if (LeInvH.getEquipment().getItem(eSlot) != null) {
                            if (condition.get("item_condition") != null) {
                                return ConditionExecutor.testItem((JSONObject) condition.get("item_condition"), (CraftItemStack) LeInvH.getEquipment().getItem(eSlot));
                            } else {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("exists"), (condition, entity) -> {
            return entity != null;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("distance_from_spawn"), (condition, entity) -> {
            @NotNull Vector actorVector = entity.getLocation().toVector();
            @NotNull Vector targetVector = entity.getWorld().getSpawnLocation().toVector();
            String comparison = condition.get("comparison").toString();
            double compare_to = Double.parseDouble(condition.get("compare_to").toString());
            return Utils.compareValues(actorVector.distance(targetVector), comparison, compare_to);
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
        register(new ConditionFactory(GenesisMC.apoliIdentifier("fall_distance"), (condition, entity) -> {
            return Utils.compareValues(entity.getFallDistance(), condition.get("comparison").toString(), Double.parseDouble(condition.get("compare_to").toString()));
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("gamemode"), (condition, entity) -> {
            if (entity instanceof Player player) {
                return player.getGameMode().equals(GameMode.valueOf(condition.get("gamemode").toString().toUpperCase()));
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("glowing"), (condition, entity) -> {
            return entity.isGlowing();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("health"), (condition, entity) -> {
            if (entity instanceof LivingEntity le) {
                return Utils.compareValues(le.getHealth(), condition.get("comparison").toString(), Double.parseDouble(condition.get("compare_to").toString()));
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("in_block"), (condition, entity) -> {
            if(entity.getLocation().getBlock().getType().isCollidable()){
                return condition.containsKey("block_condition") ? ConditionExecutor.testBlock((JSONObject) condition.get("block_condition"), (CraftBlock) entity.getLocation().getBlock()) : true;
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("in_tag"), (condition, entity) -> {
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
                        return entityTagMappings.get(condition.get("tag")).contains(entity.getType());
                    }
                }
            } catch (IllegalArgumentException e) {
                // yeah imma just ignore this one ty
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("living"), (condition, entity) -> {
            return !entity.isDead();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("moving"), (condition, entity) -> {
            return isEntityMoving(entity);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("on_block"), (condition, entity) -> {
            BlockConditions blockCondition = ConditionExecutor.blockCondition;
            if (condition.get("block_condition") == null) {
                return entity.isOnGround();
            } else {
                return ConditionExecutor.testBlock((JSONObject) condition.get("block_condition"), (CraftBlock) entity.getLocation().add(0, -1, 0).getBlock()) && entity.isOnGround();
            }
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("biome"), (condition, entity) -> {
            if(condition.containsKey("condition")){
                Optional<Boolean> bool = ConditionExecutor.biomeCondition.check((JSONObject) condition.get("condition"), entity, target, entity.getLocation().getBlock(), fluid, itemStack, entityDamageEvent);
                return bool.isPresent() && bool.get();
            }else{ // Assumed to be trying to get biome type
                String key = condition.get("biome").toString();
                if(key.contains(":")){
                    key = key.split(":")[1];
                }
                return entity.getLocation().getBlock().getBiome().equals(Biome.valueOf(key.toUpperCase()));
            }
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("raycast"), (condition, entity) -> {
            return RaycastApoli.condition(condition, ((CraftEntity)entity).getHandle());
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("relative_health"), (condition, entity) -> {
            if (entity instanceof LivingEntity le) {
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                double fin = le.getHealth() / le.getMaxHealth();
                return Utils.compareValues(fin, comparison, compare_to);
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("riding"), (condition, entity) -> {
            if(entity.getVehicle() != null){
                if(condition.containsKey("bientity_condition")){
                    Optional<Boolean> bool = ConditionExecutor.biEntityCondition.check((JSONObject) condition.get("bientity_condition"), entity, entity.getVehicle(), null, null, null, null);
                    return bool.isPresent() && bool.get();
                }
                return true;
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("apoli:riding_root"), (condition, entity) -> {
            if(entity.getVehicle() != null){
                if(condition.containsKey("bientity_condition")){
                    Optional<Boolean> bool = ConditionExecutor.biEntityCondition.check((JSONObject) condition.get("bientity_condition"), entity, entity.getVehicle(), null, null, null, null);
                    return bool.isPresent() && bool.get();
                }
                return true;
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("riding_recursive"), (condition, entity) -> {
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
            return Utils.compareValues(count, comparison, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("passenger_recursive"), (condition, entity) -> {
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
            return Utils.compareValues(count, comparison, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("apoli:passenger"), (condition, entity) -> {
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
            return Utils.compareValues(count, comparison, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("saturation_level"), (condition, entity) -> {
            if (entity instanceof Player le) {
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                double fin = le.getSaturation();
                return Utils.compareValues(fin, comparison, compare_to);
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
            return Utils.compareValues(entity.getWorld().getTime(), comparison, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("using_effective_tool"), (condition, entity) -> {
            if (entity instanceof Player player) {
                Predicate<Entity> filter = (entityy) -> !entityy.equals(player);
                RayTraceResult result = entity.getWorld().rayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), AttributeHandler.Reach.getFinalReach(player), FluidCollisionMode.NEVER, false, 0, filter);
                if (result != null && result.getHitBlock() != null) {
                    return result.getHitBlock().getBlockData().isPreferredTool(player.getInventory().getItemInMainHand());
                }
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("using_item"), (condition, entity) -> {
            if (entity instanceof LivingEntity le) {
                if (le.getActiveItem() != null) {
                    if (condition.get("item_condition") != null) {
                        ItemConditions itemCondition = ConditionExecutor.itemCondition;
                        Optional boolI = itemCondition.check((JSONObject) condition.get("item_condition"), le, target, block, fluid, itemStack, entityDamageEvent);
                        if (boolI.isPresent()) {
                            if (boolI.get().equals(true)) {
                                return true;
                            }
                        }
                    } else {
                        return true;
                    }
                }
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("xp_levels"), (condition, entity) -> {
            if (entity instanceof Player p) {
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                return Utils.compareValues(p.getExpToLevel(), comparison, compare_to);
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("xp_points"), (condition, entity) -> {
            if (entity instanceof Player p) {
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                return Utils.compareValues(p.getTotalExperience(), comparison, compare_to);
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("in_snow"), (condition, entity) -> {
            return entity.isInPowderedSnow();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("in_thunderstorm"), (condition, entity) -> {
            return entity.isInRain() && entity.getWorld().isThundering();
        }));
    }

    private void register(EntityConditions.ConditionFactory factory){
        GenesisMC.getPlugin().registry.retrieve(Registries.ENTITY_CONDITION).register(factory);
    }

    public class ConditionFactory implements Registerable {
        NamespacedKey key;
        BiPredicate<JSONObject, CraftEntity> test;

        public ConditionFactory(NamespacedKey key, BiPredicate<JSONObject, CraftEntity> test){
            this.key = key;
            this.test = test;
        }

        public boolean test(JSONObject condition, CraftEntity tester){
            return test.test(condition, tester);
        }

        @Override
        public NamespacedKey getKey() {
            return key;
        }
    }

    public boolean isEntityMoving(Entity entity) {
        int entID = entity.getEntityId();
        Location prevLocat = prevLoca[entID];
        Location cuLo = entity.getLocation();
        prevLoca[entID] = cuLo;

        return !cuLo.equals(prevLocat);
    }
}
