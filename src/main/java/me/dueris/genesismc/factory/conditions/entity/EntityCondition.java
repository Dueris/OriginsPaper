package me.dueris.genesismc.factory.conditions.entity;

import me.dueris.genesismc.CooldownManager;
import me.dueris.genesismc.factory.TagRegistry;
import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.conditions.block.BlockCondition;
import me.dueris.genesismc.factory.conditions.item.ItemCondition;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.powers.Resource;
import me.dueris.genesismc.factory.powers.effects.StackingStatusEffect;
import me.dueris.genesismc.factory.powers.player.Climbing;
import me.dueris.genesismc.factory.powers.player.FlightElytra;
import me.dueris.genesismc.factory.powers.player.RestrictArmor;
import me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler;
import me.dueris.genesismc.utils.PowerContainer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversable;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;
import static me.dueris.genesismc.factory.powers.Power.climbing;
import static me.dueris.genesismc.factory.powers.player.RestrictArmor.compareValues;

public class EntityCondition implements Condition {
    public static HashMap<PowerContainer, ArrayList<String>> inTagValues = new HashMap<>();
    public static HashMap<String, ArrayList<EntityType>> entityTagMappings = new HashMap<>();

    public static Enchantment getEnchantmentByNamespace(String namespaceString) {
        return Enchantment.getByName(namespaceString);
    }

    private static int countBlocksInCube(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, World world, HashMap<String, Object> condition, Player p, PowerContainer power, String powerfile, Entity entity, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent) {
        int blockCount = 0;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(world, x, y, z);
                    Block blockw = location.getBlock();
                    if (blockw.getType() != Material.AIR) {
                        BlockCondition blockCondition = ConditionExecutor.blockCondition;
                        if (blockCondition.check(condition, p, power, powerfile, entity, target, blockw, fluid, itemStack, dmgevent).isPresent()) {
                            if (blockCondition.check(condition, p, power, powerfile, entity, target, blockw, fluid, itemStack, dmgevent).get()) {
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

    private static int countBlocksInStar(int centerX, int centerY, int centerZ, int radius, World world, HashMap<String, Object> condition, Player p, PowerContainer power, String powerfile, Entity entity, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent) {
        int blockCount = 0;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) + Math.pow(z - centerZ, 2));

                    if (distance <= radius && distance >= radius / 2) {
                        Location location = new Location(world, x, y, z);
                        Block block2 = location.getBlock();

                        if (block2.getType() != Material.AIR) {
                            BlockCondition blockCondition = ConditionExecutor.blockCondition;
                            if (blockCondition.check(condition, p, power, powerfile, entity, target, block2, fluid, itemStack, dmgevent).isPresent()) {
                                if (blockCondition.check(condition, p, power, powerfile, entity, target, block2, fluid, itemStack, dmgevent).get()) {
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

    public static int countBlocksInSphere(int centerX, int centerY, int centerZ, int radius, World world, HashMap<String, Object> condition, Player p, PowerContainer power, String powerfile, Entity entity, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent) {
        int blockCount = 0;
        int squaredRadius = radius * radius;

        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    if ((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY) + (z - centerZ) * (z - centerZ) <= squaredRadius) {
                        Location location = new Location(world, x, y, z);
                        if (location.getBlock().getType() != Material.AIR) {
                            BlockCondition blockCondition = ConditionExecutor.blockCondition;
                            if (blockCondition.check(condition, p, power, powerfile, entity, target, location.getBlock(), fluid, itemStack, dmgevent).isPresent()) {
                                if (blockCondition.check(condition, p, power, powerfile, entity, target, location.getBlock(), fluid, itemStack, dmgevent).get()) {
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
    public Optional<Boolean> check(HashMap<String, Object> condition, Player p, PowerContainer power, String powerfile, Entity entity, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent entityDamageEvent) {
        if (condition.isEmpty()) return Optional.empty();
        boolean inverted = (boolean) condition.getOrDefault("inverted", false);
        if (condition.get("type") == null) return Optional.empty();
        String type = condition.get("type").toString();
        switch(type){
            case "origins:ability" -> {
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
            case "origins:advancement" -> {
                String advancementString = condition.get("advancement").toString();

                if (entity instanceof Player player) {
                    File advancementsFolder = new File(MinecraftServer.getServer().getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).toAbsolutePath().toString());
                    File playerAdvancementFile = new File(advancementsFolder, player.getUniqueId() + ".json");
    
                    if (playerAdvancementFile.exists()) {
                        try {
                            JSONParser parser = new JSONParser();
                            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(playerAdvancementFile));
                            JSONObject advancementJson = (JSONObject) jsonObject.get(advancementString);
    
                            if (advancementJson != null) {
                                Boolean done = (Boolean) advancementJson.get("done");
                                return getResult(inverted, Optional.of(Objects.requireNonNullElse(done, false)));
                            } else {
                                return getResult(inverted, Optional.of(false));
                            }
                        } catch (IOException | ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        return getResult(inverted, Optional.of(false));
                    }
                }
            }
            case "origins:sprinting" -> {
                return getResult(inverted, Optional.of(p.isSprinting()));
            }
            case "origins:food_level" -> {
                String comparison = condition.get("comparison").toString();
                int compare_to = Integer.parseInt(condition.get("compare_to").toString());
                    return getResult(inverted, Optional.of(RestrictArmor.compareValues(p.getFoodLevel(), comparison, compare_to)));
            }
            case "origins:air" -> {
                if (entity instanceof Player player) {
                    return getResult(inverted, Optional.of(compareValues(player.getRemainingAir(), condition.get("comparison").toString(), Integer.parseInt(condition.get("compare_to").toString()))));
                }
            }
            case "origins:attribute" -> {
                if (entity instanceof Player player) {
                    String attributeString = condition.get("attribute").toString().split(":")[1].replace(".", "_").toUpperCase();
                    return getResult(inverted, Optional.of(compareValues(player.getAttribute(Attribute.valueOf(attributeString)).getValue(), condition.get("comparison").toString(), Integer.parseInt(condition.get("compare_to").toString()))));
                }
            }
            case "origins:block_collision" -> {
                String offsetX = condition.get("offset_x").toString();
                String offsetY = condition.get("offset_y").toString();
                String offsetZ = condition.get("offset_z").toString();
                System.out.println("lfjksd");
                if (entity instanceof Player player) {
                    System.out.println("lksdhfj");
                    Location playerLocation = player.getEyeLocation();
                    World world = player.getWorld();
    
                    int blockX = playerLocation.getBlockX() + Integer.parseInt(offsetX);
                    int blockY = playerLocation.getBlockY() + Integer.parseInt(offsetY);
                    int blockZ = playerLocation.getBlockZ() + Integer.parseInt(offsetZ);
    
                    Block blockAt = world.getBlockAt(blockX, blockY, blockZ);
                    System.out.println(blockAt.getType());
                    return getResult(inverted, Optional.of(blockAt.getType().isSolid()));
                }
            }
            case "origins:block_in_radius" -> {
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
                HashMap<String, Object> ingredientMap = (HashMap<String, java.lang.Object>) condition.get("block_condition");
                if (shape.equalsIgnoreCase("sphere")) {
                    blockCount = countBlocksInSphere(centerX, centerY, centerZ, radius, world, ingredientMap, p, power, powerfile, entity, target, block, fluid, itemStack, entityDamageEvent);
                } else if (shape.equalsIgnoreCase("star")) {
                    blockCount = countBlocksInStar(centerX, centerY, centerZ, radius, world, ingredientMap, p, power, powerfile, entity, target, block, fluid, itemStack, entityDamageEvent);
                } else if (shape.equalsIgnoreCase("cube")) {
                    blockCount = countBlocksInCube(minX, minY, minZ, maxX, maxY, maxZ, world, ingredientMap, p, power, powerfile, entity, target, block, fluid, itemStack, entityDamageEvent);
                }else{
                    return getResult(inverted, Optional.of(false));
                }
                return getResult(inverted, Optional.of(compareValues(blockCount, comparison, compare_to)));
    
            }
            case "origins:weather_check" -> {
                boolean thunder = (boolean) condition.getOrDefault("thundering", false);
                boolean rain = (boolean) condition.getOrDefault("raining", false);
                boolean clear = (boolean) condition.getOrDefault("clear", false);
                if (thunder) {
                    return getResult(inverted, Optional.of(p.getWorld().isThundering()));
                } else if (rain) {
                    return getResult(inverted, Optional.of(p.getWorld().getClearWeatherDuration() == 0));
                } else if (clear) {
                    return getResult(inverted, Optional.of(p.getWorld().getClearWeatherDuration() > 0));
                } else {
                    return getResult(inverted, Optional.of(false));
                }
            }
            case "origins:brightness" -> {
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
                    return getResult(inverted, Optional.of(compareValues(brightness, comparison, compare_to)));
    
            }
            case "origins:light_level" -> {
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                int lightLevel = entity.getLocation().getBlock().getLightLevel();
                    return getResult(inverted, Optional.of(compareValues(lightLevel, comparison, compare_to)));
            }
            case "origins:climbing" -> {
                if (entity instanceof Player player) {
                    Climbing climbing = new Climbing();
                    if(player.isClimbing()) {
                        return getResult(inverted, Optional.of(true));
                    } else if(climbing.isActiveClimbing(player)){
                        return getResult(inverted, Optional.of(true));
                    } else {
                        return getResult(inverted, Optional.of(false));
                    }
    
                }
            }
            case "origins:collided_horizontally" -> {
                if (entity instanceof LivingEntity le) {
                    BoundingBox playerBoundingBox = le.getBoundingBox();
                    BoundingBox blockBoundingBox = block.getBoundingBox();
                    return getResult(inverted, Optional.of(blockBoundingBox.overlaps(playerBoundingBox)));
    
                }
            }
            case "origins:creative_flying" -> {
                if (entity instanceof Player player) {
                    return getResult(inverted, Optional.of(player.isFlying()));
                }
            }
            case "origins:daytime" -> {
                return getResult(inverted, Optional.of(entity.getWorld().isDayTime()));
            }
            case "origins:dimension" -> {
                String dim = condition.get("dimension").toString();
                if(!dim.contains(":")){
                    dim = "minecraft:" + dim;
                }
                return getResult(inverted, Optional.of(entity.getWorld().getKey().equals(NamespacedKey.fromString(dim))));
            }
            case "origins:fluid_height" -> {
                String fluidD = condition.get("fluid").toString();
    
                if (fluidD.equalsIgnoreCase("lava") || fluidD.equalsIgnoreCase("minecraft:lava")) {
                    return getResult(inverted, Optional.of(entity.isInLava()));
                } else if (fluidD.equalsIgnoreCase("water") || fluidD.equalsIgnoreCase("minecraft:water")) {
                    return getResult(inverted, Optional.of(entity.isInWaterOrBubbleColumn()));
                }else{
                    return getResult(inverted, Optional.of(false));
                }
            }
            case "origins:invisible" -> {
                if (entity instanceof LivingEntity le) {
                    return getResult(inverted, Optional.of(le.isInvisible() || le.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY)));
                }
            }
            case "origins:in_rain" -> {
                return getResult(inverted, Optional.of(entity.isInRain()));
            }
            case "origins:exposed_to_sun" -> {
                return getResult(inverted, Optional.of((p.getLocation().getBlockY() + 1 > p.getWorld().getHighestBlockYAt(p.getLocation())) && p.getWorld().isDayTime()));
            }
            case "origins:exposed_to_sky" -> {
                return getResult(inverted, Optional.of((entity.getLocation().getBlockY() + 1 > entity.getWorld().getHighestBlockYAt(p.getLocation()))));
            }
            case "origins:sneaking" -> {
                return getResult(inverted, Optional.of(entity.isSneaking()));
            }
            case "origins:resource" -> {
                if(CooldownManager.cooldowns.get(p).contains(condition.get("resource").toString()) && CooldownManager.cooldowns.containsKey(p)){
                    return getResult(inverted, Optional.of(!CooldownManager.isPlayerInCooldownFromTag(p, condition.get("resource").toString())));
                }else{
                    if(Resource.registeredBars.containsKey(condition.get("resource").toString())){
                        String comparison = condition.get("comparison").toString();
                        double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                        return getResult(inverted, Optional.of(RestrictArmor.compareValues(Resource.getResource(condition.get("resource").toString()).getLeft().getProgress(), comparison, compare_to)));
                    }else{
                        return getResult(inverted, Optional.of(false));
                    }
                }
            }
            case "origins:fall_flying" -> {
                if (entity instanceof Player player) {
                    if (player.isGliding() || FlightElytra.getGlidingPlayers().contains(player)) {
                            return getResult(inverted, Optional.of(player.getVelocity().getY() < 0 && !player.isOnGround()));
                    }else{
                        return getResult(inverted, Optional.of(false));
                    }
                }
            }
            case "origins:submerged_in" -> {
                if (condition.get("fluid").equals("minecraft:water")) {
                    return getResult(inverted, Optional.of(entity.isInWaterOrBubbleColumn()));
                } else if (condition.get("fluid").equals("minecraft:lava")) {
                    return getResult(inverted, Optional.of(entity.isInLava()));
                }else{
                    return getResult(inverted, Optional.of(false));
                }
            }
            case "origins:enchantment" -> {
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

                                return getResult(inverted, Optional.of(RestrictArmor.compareValues(enchantmentLevel, comparison, compareTo)));
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
            }
            case "origins:on_fire" -> {
                return getResult(inverted, Optional.of(entity.isVisualFire()));
            }
            case "origins:entity_type" -> {
                return getResult(inverted, Optional.of(entity.getType().equals(EntityType.valueOf(condition.get("entity_type").toString().toUpperCase().split(":")[1]))));
            }
            case "origins:equipped_item" -> {
                if(entity instanceof InventoryHolder invH){
                    if(invH instanceof LivingEntity LeInvH){
                        EquipmentSlot eSlot = Actions.getSlotFromString(condition.get("equipment_slot").toString());
                        if(eSlot != null){
                            if(LeInvH.getEquipment().getItem(eSlot) != null){
                                if(condition.get("item_condition") != null){
                                    ItemCondition itemCondition = ConditionExecutor.itemCondition;
                                    Optional boolIC = itemCondition.check((HashMap<String, Object>) condition.get("item_condition"), p, power, powerfile, entity, target, block, fluid, LeInvH.getEquipment().getItem(eSlot), entityDamageEvent);
                                    if(boolIC.isPresent()){
                                        return getResult(inverted, Optional.of((Boolean) boolIC.get()));
                                    }
                                }else{
                                    return getResult(inverted, Optional.of(true));
                                }
                            }
                        }
                    }
                }
            }
            case "origins:exists" -> {
                return getResult(inverted, Optional.of(entity != null));
            }
            case "origins:distance_from_spawn" -> {
                @NotNull Vector actorVector = entity.getLocation().toVector();
                @NotNull Vector targetVector = entity.getWorld().getSpawnLocation().toVector();
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(actorVector.distance(targetVector), comparison, compare_to)));
            }
            case "origins:elytra_flight_possible" -> {
                boolean hasElytraPower = FlightElytra.elytra.contains(entity);
                boolean hasElytraEquipment = false;
                if(entity instanceof LivingEntity li){
                    for(ItemStack item : li.getEquipment().getArmorContents()){
                        if(hasElytraEquipment) break;
                        if(item == null) continue;
                        if(item.getType().equals(Material.ELYTRA)){
                            hasElytraEquipment = true;
                        }
                    }
                }
                return getResult(inverted, Optional.of(hasElytraPower || hasElytraEquipment));
            }
            case "origins:fall_distance" -> {
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(entity.getFallDistance(), condition.get("comparison").toString(), Double.parseDouble(condition.get("compare_to").toString()))));
            }
            case "origins:gamemode" -> {
                if(entity instanceof Player player){
                    return getResult(inverted, Optional.of(player.equals(GameMode.valueOf(condition.get("gamemode").toString().toUpperCase()))));
                }
            }
            case "origins:glowing" -> {
                return getResult(inverted, Optional.of(entity.isGlowing()));
            }
            case "origins:health" -> {
                if(entity instanceof LivingEntity le){
                    return getResult(inverted, Optional.of(RestrictArmor.compareValues(le.getHealth(), condition.get("comparison").toString(), Double.parseDouble(condition.get("compare_to").toString()))));
                }
            }
            case "origins:in_block" -> {
                BlockCondition blockCondition = ConditionExecutor.blockCondition;
                Optional boolB = blockCondition.check((HashMap<String, Object>) condition.get("block_condition"), p, power, powerfile, entity, target, block, fluid, itemStack, entityDamageEvent);
                if(boolB.isPresent()){
                    return getResult(inverted, Optional.of((Boolean) boolB.get()));
                }else{
                    return getResult(inverted, Optional.of(false));
                }
            }
            case "origins:in_tag" -> {
                // Use block in_tag optimization
                try {
                    if(TagRegistry.getRegisteredTagFromFileKey(condition.get("tag").toString()) != null){
                        if(!entityTagMappings.containsKey(condition.get("tag"))){
                            entityTagMappings.put(condition.get("tag").toString(), new ArrayList<>());
                            for(String mat : TagRegistry.getRegisteredTagFromFileKey(condition.get("tag").toString())){
                                entityTagMappings.get(condition.get("tag")).add(EntityType.valueOf(mat.split(":")[1].toUpperCase()));
                            }
                        }else{
                            // mappings exist, now we can start stuff
                            return getResult(inverted, Optional.of(entityTagMappings.get(condition.get("tag")).contains(entity.getType())));
                        }
                    }
                } catch (IllegalArgumentException e) {
                    // yeah imma just ignore this one ty
                }
            }
            case "origins:living" -> {
                return getResult(inverted, Optional.of(!entity.isDead()));
            }
            case "origins:moving" -> {
                return getResult(inverted, Optional.of(isEntityMoving(entity)));
            }
            case "origins:on_block" -> {
                BlockCondition blockCondition = ConditionExecutor.blockCondition;
                if(condition.get("block_condition") == null){
                    return getResult(inverted, Optional.of(entity.isOnGround()));
                }else{
                    Optional<Boolean> boolB = blockCondition.check((HashMap<String, Object>) condition.get("block_condition"), p, power, powerfile, entity, target, entity.getLocation().add(0, -1, 0).getBlock(), fluid, itemStack, entityDamageEvent);
                    if(boolB.isPresent()){
                        return getResult(inverted, Optional.of(boolB.get() && entity.isOnGround()));
                    }
                }
                return getResult(inverted, Optional.of(false));
            }
            case "origins:passenger" -> {
                for(Entity entity1 : entity.getWorld().getEntities()){
                    if(entity1.getPassengers().contains(entity)){
                        return getResult(inverted, Optional.of(true));
                    }
                }
                return getResult(inverted, Optional.of(false));
            }
            case "origins:raycast" -> {
                Predicate<Entity> filter = entity1 -> !entity1.equals(entity);
    
                RayTraceResult traceResult = p.getWorld().rayTrace(entity.getLocation(), entity.getLocation().getDirection(), 12, FluidCollisionMode.valueOf(condition.getOrDefault("fluid_handling", "none").toString()), false, 1, filter);
                final boolean[] booleans = new boolean[0];
                booleans[0] = true;
                booleans[1] = true;
                booleans[2] = true;
                if(traceResult != null){
                    if(traceResult.getHitEntity() != null){
                        Entity entity2 = traceResult.getHitEntity();
                        if (entity2.isDead() || !(entity2 instanceof LivingEntity)) return getResult(inverted, Optional.of(false));
                        if (entity2.isInvulnerable()) return getResult(inverted, Optional.of(false));
                        if (entity2.getPassengers().contains(p)) return getResult(inverted, Optional.of(false));
                        if(entity2.equals(target)){
                            Optional boolB = this.check((HashMap<String, Object>) condition.get("block_condition"), p, power, powerfile, entity, target, block, fluid, itemStack, entityDamageEvent);
                            if(boolB.isPresent()){
                                if(boolB.get().equals(true)){
                                    booleans[2] = true;
                                }else{
                                    booleans[2] = true;
                                }
                            }else{
                                booleans[2] = true;
                            }
                        }else{
                            booleans[2] = false;
                        }
                    }
                    if(traceResult.getHitBlock() != null){
                        BlockCondition blockCondition = ConditionExecutor.blockCondition;
                        if(condition.get("block_condition") != null){
                            Optional boolB = blockCondition.check((HashMap<String, Object>) condition.get("block_condition"), p, power, powerfile, entity, target, block, fluid, itemStack, entityDamageEvent);
                            if(boolB.isPresent()){
                                booleans[1] = boolB.get().equals(true);
                            }else{
                                booleans[1] = true;
                            }
                        }else{
                            booleans[1] = true;
                        }
                    }
                }
                boolean finalB = true;
                for(int i = 0; i <= booleans.length; i++){
                    if (!booleans[i]) {
                        finalB = false;
                        break;
                    }
                }
                return getResult(inverted, Optional.of(finalB));
            }
            case "origins:relative_health" -> {
                if(entity instanceof LivingEntity le){
                    String comparison = condition.get("comparison").toString();
                    double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                    double fin = p.getHealth() / le.getMaxHealth();
                    return getResult(inverted, Optional.of(RestrictArmor.compareValues(fin, comparison, compare_to)));
                }
            }
            case "origins:riding" -> {
                for(Entity entity1 : entity.getWorld().getEntities()){
                    return getResult(inverted, Optional.of(entity1.getPassengers().contains(entity)));
                }
            }
            case "origins:saturation_level" -> {
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                double fin = p.getSaturation();
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(fin, comparison, compare_to)));
            }
            case "origins:status_effect" -> {
                if(entity instanceof LivingEntity le){
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
            }
            case "origins:swimming" -> {
                if(entity instanceof LivingEntity le){
                    return getResult(inverted, Optional.of(le.isSwimming()));
                }
            }
            case "origins:tamed" -> {
                if(entity instanceof Tameable tameable){
                    return getResult(inverted, Optional.of(tameable.isTamed()));
                }
            }
            case "origins:time_of_day" -> {
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(entity.getWorld().getTime(), comparison, compare_to)));
            }
            case "origins:using_effective_tool" -> {
                if(p.getTargetBlockExact(AttributeHandler.Reach.getDefaultReach(p)) != null){
                    return getResult(inverted, Optional.of(p.getTargetBlockExact(AttributeHandler.Reach.getDefaultReach(p)).getBlockData().isPreferredTool(p.getInventory().getItemInMainHand())));
                }else{
                    return getResult(inverted, Optional.of(false));
                }
            }
            case "origins:using_item" -> {
                if(entity instanceof LivingEntity le){
                        if(le.getActiveItem() != null){
                            if(condition.get("item_condition") != null){
                                ItemCondition itemCondition = ConditionExecutor.itemCondition;
                                Optional boolI = itemCondition.check((HashMap<String, Object>) condition.get("item_condition"), p, power, powerfile, le, target, block, fluid, itemStack, entityDamageEvent);
                                if(boolI.isPresent()){
                                    if(boolI.get().equals(true)){
                                        return getResult(inverted, Optional.of(true));
                                    }
                                }
                            }else{
                                return getResult(inverted, Optional.of(true));
                            }
                        }
                }
            }
            case "origins:xp_levels" -> {
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(p.getExpToLevel(), comparison, compare_to)));
            }
            case "origins:xp_points" -> {
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(p.getTotalExperience(), comparison, compare_to)));
            }
            case "origins:in_snow" -> {
                return getResult(inverted, Optional.of(entity.isInPowderedSnow()));
            }
            case "origins:in_thunderstorm" -> {
                return getResult(inverted, Optional.of(entity.isInRain() && entity.getWorld().isThundering()));
            }
            default -> {
                return getResult(inverted, Optional.empty());
            }
        }

        return getResult(inverted, Optional.empty());
    }

    private final Location[] prevLoca = new Location[100000];

    public boolean isEntityMoving(Entity entity){
        int entID = entity.getEntityId();
        Location prevLocat = prevLoca[entID];
        Location cuLo = entity.getLocation();
        prevLoca[entID] = cuLo;

        return !cuLo.equals(prevLocat);
    }
}
