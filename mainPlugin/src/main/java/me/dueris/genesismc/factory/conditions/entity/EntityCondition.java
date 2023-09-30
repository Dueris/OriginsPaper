package me.dueris.genesismc.factory.conditions.entity;

import me.dueris.genesismc.CooldownStuff;
import me.dueris.genesismc.factory.TagRegistry;
import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.conditions.block.BlockCondition;
import me.dueris.genesismc.factory.conditions.item.ItemCondition;
import me.dueris.genesismc.factory.actions.Actions;
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
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;
import static me.dueris.genesismc.factory.powers.player.RestrictArmor.compareValues;

public class EntityCondition implements Condition {

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
                        BlockCondition blockCondition = new BlockCondition();
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
                            BlockCondition blockCondition = new BlockCondition();
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
                            BlockCondition blockCondition = new BlockCondition();
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
        if (type.equalsIgnoreCase("origins:ability")) {
            String ability = condition.get("ability").toString().toLowerCase();

            switch (ability) {
                case "minecraft:flying" -> {
                    return getResult(inverted, ((CraftPlayer) p).getHandle().getAbilities().flying);
                }
                case "minecraft:instabuild" -> {
                    return getResult(inverted, ((CraftPlayer) p).getHandle().getAbilities().instabuild);
                }
                case "minecraft:invulnerable" -> {
                    return getResult(inverted, ((CraftPlayer) p).getHandle().getAbilities().invulnerable);
                }
                case "minecraft:maybuild" -> {
                    return getResult(inverted, ((CraftPlayer) p).getHandle().getAbilities().mayBuild);
                }
                case "minecraft:mayfly" -> {
                    return getResult(inverted, ((CraftPlayer) p).getHandle().getAbilities().mayfly);
                }
            }
        }

        if (type.equalsIgnoreCase("origins:advancement")) {
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
                            return getResult(inverted, Objects.requireNonNullElse(done, false));
                        } else {
                            return getResult(inverted, false);
                        }
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    return getResult(inverted, false);
                }
            }
        }

        if (type.equalsIgnoreCase("origins:sprinting")) {
            return getResult(inverted, p.isSprinting());
        }

        if (type.equalsIgnoreCase("origins:food_level")) {
            String comparison = condition.get("comparison").toString();
            int compare_to = Integer.parseInt(condition.get("compare_to").toString());
            if (RestrictArmor.compareValues(p.getFoodLevel(), comparison, compare_to)) {
                return getResult(inverted, true);
            }
        }

        if (type.equalsIgnoreCase("origins:air")) {
            if (entity instanceof Player player) {
                if (compareValues(player.getRemainingAir(), condition.get("comparison").toString(), Integer.parseInt(condition.get("compare_to").toString()))) {
                    return getResult(inverted, true);
                }
            }
        }

        if (type.equalsIgnoreCase("origins:attribute")) {
            if (entity instanceof Player player) {
                String attributeString = condition.get("attribute").toString().split(":")[1].replace(".", "_").toUpperCase();
                if (compareValues(player.getAttribute(Attribute.valueOf(attributeString)).getValue(), condition.get("comparison").toString(), Integer.parseInt(condition.get("compare_to").toString()))) {
                    return getResult(inverted, true);
                }
            }
        }

        if (type.equalsIgnoreCase("origins:block_collision")) {
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
                if (!blockAt.getType().isAir()) {
                    return getResult(inverted, true);
                }
            }
        }

        if (type.equalsIgnoreCase("origins:block_in_radius")) {
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
            }
            if (compareValues(blockCount, comparison, compare_to)) {
                return getResult(inverted, true);
            }

        }

        if (type.equalsIgnoreCase("origins:weather_check")) {
            boolean thunder = (boolean) condition.getOrDefault("thundering", false);
            boolean rain = (boolean) condition.getOrDefault("raining", false);
            boolean clear = (boolean) condition.getOrDefault("clear", false);
            if (thunder) {
                return getResult(inverted, p.getWorld().isThundering());
            }
            if (rain) {
                return getResult(inverted, p.getWorld().getClearWeatherDuration() == 0);
            }
            if (clear) {
                return getResult(inverted, p.getWorld().getClearWeatherDuration() > 0);
            }
        }

        if (type.equalsIgnoreCase("origins:brightness")) {
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
            p.sendMessage(String.valueOf(lightLevel));
            if (compareValues(brightness, comparison, compare_to)) {
                return getResult(inverted, true);
            }

        }

        if (type.equalsIgnoreCase("origins:light_level")) {
            String comparison = condition.get("comparison").toString();
            double compare_to = Double.parseDouble(condition.get("compare_to").toString());
            int lightLevel = entity.getLocation().getBlock().getLightLevel();

            if (compareValues(lightLevel, comparison, compare_to)) {
                return getResult(inverted, true);
            }

        }

        if (type.equalsIgnoreCase("origins:climbing")) {
            if (entity instanceof Player player) {
                if (player.isClimbing()) {
                    return getResult(inverted, true);
                }
                Climbing climbing = new Climbing();
                if (climbing.isActiveClimbing(player)) {
                    return getResult(inverted, true);
                }

            }
        }

        if (type.equalsIgnoreCase("origins:collided_horizontally")) {
            if (entity instanceof Player player) {
                BoundingBox playerBoundingBox = player.getBoundingBox();
                BoundingBox blockBoundingBox = block.getBoundingBox();

                if (blockBoundingBox.overlaps(playerBoundingBox)) {
                    return getResult(inverted, true);
                }

            }
        }

        if (type.equalsIgnoreCase("origins:creative_flying")) {
            if (entity instanceof Player player) {
                if (player.isFlying()) {
                    return getResult(inverted, true);
                }

            }
        }

        if (type.equalsIgnoreCase("origins:daytime")) {
            if (entity.getWorld().isDayTime()) {
                return getResult(inverted, true);
            }
        }

        if (type.equalsIgnoreCase("origins:dimension")) {
            if (entity.getWorld().getEnvironment().toString().equals(condition.get("dimension").toString().split(":")[1].replace("the_", "").toUpperCase())) {
                return getResult(inverted, true);
            }
        }

        if (type.equalsIgnoreCase("origins:fluid_height")) {
            String fluidD = condition.get("fluid").toString();

            if (fluidD.equalsIgnoreCase("lava") || fluidD.equalsIgnoreCase("minecraft:lava")) {
                return getResult(inverted, entity.isInLava());
            } else if (fluidD.equalsIgnoreCase("water") || fluidD.equalsIgnoreCase("minecraft:water")) {
                return getResult(inverted, entity.isInWaterOrBubbleColumn());
            }
        }

        if (type.equalsIgnoreCase("origins:invisible")) {
            if (entity instanceof Player player) {
                return getResult(false, player.isInvisible() || p.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY));
            }
        }

        if (type.equalsIgnoreCase("origins:in_rain")) {
            return getResult(inverted, entity.isInRain());
        }

        if (type.equalsIgnoreCase("origins:health")) {
            if (RestrictArmor.compareValues(p.getHealth(), condition.get("comparison").toString(), Double.parseDouble(condition.get("compare_to").toString()))) {
                return getResult(inverted, true);
            }
        }

        if (type.equalsIgnoreCase("origins:exposed_to_sun")) {
            if ((p.getLocation().getBlockY() + 1 > p.getWorld().getHighestBlockYAt(p.getLocation()))) {
                return getResult(inverted, p.getWorld().isDayTime());
            }
        }

        if (type.equalsIgnoreCase("origins:exposed_to_sky")) {
            if ((p.getLocation().getBlockY() + 1 > p.getWorld().getHighestBlockYAt(p.getLocation()))) {
                return getResult(inverted, true);
            }
        }

        if (type.equalsIgnoreCase("origins:sneaking")) {
            return getResult(inverted, entity.isSneaking());
        }

        if (type.equalsIgnoreCase("origins:resource")) {
            return getResult(inverted, !CooldownStuff.isPlayerInCooldownFromTag(p, power.getTag()));
        }

        if (type.equalsIgnoreCase("origins:fall_flying")) {
            if (entity instanceof Player player) {
                if (player.isGliding() || FlightElytra.getGlidingPlayers().contains(player)) {
                    if (player.getVelocity().getY() < 0 && !player.isOnGround()) {
                        return getResult(inverted, true);
                    }
                }
            }
        }

        if (type.equalsIgnoreCase("origins:light_level")) {

        }

        if (type.equalsIgnoreCase("origins:submerged_in")) {
            if (condition.get("fluid").equals("minecraft:water")) {
                if (entity.isInWaterOrBubbleColumn()) return getResult(inverted, true);
            } else if (condition.get("fluid").equals("minecraft:lava")) {
                if (entity.isInLava()) return getResult(inverted, true);
            }
        }

        if (type.equalsIgnoreCase("origins:enchantment")) {
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

                            if (RestrictArmor.compareValues(enchantmentLevel, comparison, compareTo)) {
                                p.sendMessage("true enchants");
                                return getResult(inverted, true);
                            }
                        } else {
                            if (Double.compare(compareTo, 0.0) == 0 && comparison.equals("==")) {
                                return getResult(inverted, !item.containsEnchantment(enchantment));
                            }
                        }
                    } else {
                        if (compareTo == 0 && comparison == "==") return getResult(inverted, true);
                        p.sendMessage("Enchantment not found");
                    }
                }
            }
        }

        if (type.equalsIgnoreCase("origins:on_block")) {
            return getResult(inverted, entity.isOnGround());
        }

        if (type.equalsIgnoreCase("origins:on_fire")) {
            return getResult(inverted, entity.isVisualFire());
        }

        if (type.equalsIgnoreCase("origins:entity_type")){
            if(entity.getType().equals(EntityType.valueOf(condition.get("entity_type").toString().toUpperCase().split(":")[1]))){
                return getResult(inverted, true);
            }
        }

        if (type.equalsIgnoreCase("origins:equipped_item")){
            if(entity instanceof InventoryHolder invH){
                if(invH instanceof LivingEntity LeInvH){
                    EquipmentSlot eSlot = Actions.getSlotFromString(condition.get("equipment_slot").toString());
                    if(eSlot != null){
                        if(LeInvH.getEquipment().getItem(eSlot) != null){
                            if(condition.get("item_condition") != null){
                                ItemCondition itemCondition = new ItemCondition();
                                Optional boolIC = itemCondition.check((HashMap<String, Object>) condition.get("item_condition"), p, power, powerfile, entity, target, block, fluid, LeInvH.getEquipment().getItem(eSlot), entityDamageEvent);
                                if(boolIC.isPresent()){
                                    if(boolIC.get().equals(true)){
                                        return getResult(inverted, true);
                                    }
                                }
                            }else{
                                return getResult(inverted, true);
                            }
                        }
                    }
                }
            }
            //DEPRECIATED - USING CODE ABOVE
//            EquipmentSlot eSlot = Actions.getSlotFromString(condition.get("equipment_slot").toString());
//            if(eSlot != null){
//                if(condition.get("item_condition") != null){
//                    ItemCondition itemCondition = new ItemCondition();
//                    Optional boolIC = itemCondition.check((HashMap<String, Object>) condition.get("item_condition"), p, power, powerfile, entity, target, block, fluid, itemStack, entityDamageEvent);
//                    if(boolIC.isPresent()){
//                        if(boolIC.get().equals(true)){
//                            return getResult(inverted, true);
//                        }
//                    }
//                }
//            }
        }

        if (type.equalsIgnoreCase("origins:exists")){
            return getResult(inverted, entity != null);
        }

        if (type.equalsIgnoreCase("origins:fall_distance")){
            return getResult(inverted, RestrictArmor.compareValues(entity.getFallDistance(), condition.get("comparison").toString(), Double.parseDouble(condition.get("compare_to").toString())));
        }

        if (type.equalsIgnoreCase("origins:gamemode")){
            if(entity instanceof Player player){
                return getResult(inverted, player.equals(GameMode.valueOf(condition.get("gamemode").toString())));
            }
        }

        if (type.equalsIgnoreCase("origins:glowing")){
            return getResult(inverted, entity.isGlowing());
        }

        if (type.equalsIgnoreCase("origins:health")){
            if(entity instanceof LivingEntity le){
                return getResult(inverted, RestrictArmor.compareValues(le.getHealth(), condition.get("comparison").toString(), Double.parseDouble(condition.get("compare_to").toString())));
            }
        }

        if (type.equalsIgnoreCase("origins:in_block")){
            BlockCondition blockCondition = new BlockCondition();
            Optional boolB = blockCondition.check((HashMap<String, Object>) condition.get("block_condition"), p, power, powerfile, entity, target, block, fluid, itemStack, entityDamageEvent);
            if(boolB.isPresent()){
                if(boolB.get().equals(true)){
                    return getResult(inverted, true);
                }
            }
        }

        if (type.equals("origins:in_tag")){
            try{
                for(String mat : TagRegistry.getRegisteredTagFromFileKey(condition.get("tag").toString())){
                    if(entity.getType().equals(EntityType.valueOf(mat.split(":")[1].toUpperCase()))){
                        return Optional.of(true);
                    }
                }
            }catch (Exception e){
                //silence because of weird arg things with tags being parsed wrong.
            }
        }

        if (type.equalsIgnoreCase("origins:invisible")){
            if(entity instanceof LivingEntity le){
                return getResult(inverted, le.isInvisible());
            }
        }

        if (type.equalsIgnoreCase("origins:living")){
            return getResult(inverted, !entity.isDead());
        }

        if (type.equalsIgnoreCase("origins:moving")){
            return getResult(inverted, isEntityMoving(entity));
        }

        if (type.equalsIgnoreCase("origins:on_block")){
            BlockCondition blockCondition = new BlockCondition();
            if(condition.get("block_condition") == null){
                return getResult(inverted, entity.isOnGround());
            }else{
                Optional boolB = blockCondition.check((HashMap<String, Object>) condition.get("block_condition"), p, power, powerfile, entity, target, entity.getLocation().add(0, -1, 0).getBlock(), fluid, itemStack, entityDamageEvent);
                if(boolB.isPresent()){
                    if(boolB.get().equals(true)){
                        return getResult(inverted, entity.isOnGround());
                    }
                }
            }
        }

        if (type.equalsIgnoreCase("origins:passenger")){
            for(Entity entity1 : entity.getWorld().getEntities()){
                if(entity1.getPassengers().contains(entity)){
                    return getResult(inverted, true);
                }
            }
            return getResult(inverted, false);
        }

        if (type.equalsIgnoreCase("origins:raycast")){
            Predicate<Entity> filter = entity1 -> !entity1.equals(entity);

            RayTraceResult traceResult = p.getWorld().rayTrace(entity.getLocation(), entity.getLocation().getDirection(), 12, FluidCollisionMode.valueOf(condition.getOrDefault("fluid_handling", "none").toString()), false, 1, filter);
            final boolean[] booleans = new boolean[0];
            booleans[0] = true;
            booleans[1] = true;
            booleans[2] = true;
            if(traceResult != null){
                if(traceResult.getHitEntity() != null){
                    Entity entity2 = traceResult.getHitEntity();
                    if (entity2.isDead() || !(entity2 instanceof LivingEntity)) return getResult(inverted, false);
                    if (entity2.isInvulnerable()) return getResult(inverted, false);
                    if (entity2.getPassengers().contains(p)) return getResult(inverted, false);
                    if(entity2.equals(target)){
                        EntityCondition entityCondition = new EntityCondition();
                        Optional boolB = entityCondition.check((HashMap<String, Object>) condition.get("block_condition"), p, power, powerfile, entity, target, block, fluid, itemStack, entityDamageEvent);
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
                    BlockCondition blockCondition = new BlockCondition();
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
            return getResult(inverted, finalB);
        }

        if (type.equalsIgnoreCase("origins:relative_health")){
            if(entity instanceof LivingEntity le){
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                double fin = p.getHealth() / le.getMaxHealth();
                return getResult(inverted, RestrictArmor.compareValues(fin, comparison, compare_to));
            }
        }

        if (type.equalsIgnoreCase("origins:riding")){
            for(Entity entity1 : entity.getWorld().getEntities()){
                if(entity1.getPassengers().contains(entity)){
                    return getResult(inverted, true);
                }
            }
        }

        if (type.equalsIgnoreCase("origins:saturation_level")){
            String comparison = condition.get("comparison").toString();
            double compare_to = Double.parseDouble(condition.get("compare_to").toString());
            double fin = p.getSaturation();
            return getResult(inverted, RestrictArmor.compareValues(fin, comparison, compare_to));
        }

        if (type.equalsIgnoreCase("origins:sneaking")){
            return getResult(inverted, entity.isSneaking());
        }

        if (type.equalsIgnoreCase("origins:sprinting")){
            return getResult(inverted, p.isSprinting());
        }

        if (type.equalsIgnoreCase("origins:status_effect")){
            if(entity instanceof LivingEntity le){
                if (entity != null && StackingStatusEffect.getPotionEffectType(condition.get("effect").toString()) != null) {
                    for (PotionEffect effect : le.getActivePotionEffects()) {
                        if (effect.getType().equals(StackingStatusEffect.getPotionEffectType(condition.get("effect").toString()))
                                && effect.getAmplifier() >= Integer.parseInt(condition.getOrDefault("min_amplifier", 0).toString())
                                && effect.getAmplifier() <= Integer.parseInt(condition.getOrDefault("max_amplifier", Integer.MAX_VALUE).toString())
                                && effect.getDuration() >= Integer.parseInt(condition.getOrDefault("min_duration", 0).toString())
                                && effect.getDuration() <= Integer.parseInt(condition.getOrDefault("max_duration", Integer.MAX_VALUE).toString())) {
                            return getResult(inverted, true);
                        }
                    }
                }
            }
        }

        if (type.equalsIgnoreCase("origins:swimming")){
            if(entity instanceof LivingEntity le){
                return getResult(inverted, le.isSwimming());
            }
        }

        if (type.equalsIgnoreCase("origins:tamed")){
            if(entity instanceof Tameable tameable){
                return getResult(inverted, tameable.isTamed());
            }
        }

        if (type.equalsIgnoreCase("origins:time_of_day")){
            String comparison = condition.get("comparison").toString();
            double compare_to = Double.parseDouble(condition.get("compare_to").toString());
            return getResult(inverted, RestrictArmor.compareValues(entity.getWorld().getTime(), comparison, compare_to));
        }

        if (type.equalsIgnoreCase("origins:using_effective_tool")){
            if(p.getTargetBlockExact(AttributeHandler.Reach.getDefaultReach(p)) != null){
                if(p.getTargetBlockExact(AttributeHandler.Reach.getDefaultReach(p)).getBlockData().isPreferredTool(p.getInventory().getItemInMainHand())){
                    return getResult(inverted, true);
                }
            }
        }

        if (type.equalsIgnoreCase("origins:using_item")){
            if(entity instanceof LivingEntity le){
                    if(le.getActiveItem() != null){
                        if(condition.get("item_condition") != null){
                            ItemCondition itemCondition = new ItemCondition();
                            Optional boolI = itemCondition.check((HashMap<String, Object>) condition.get("item_condition"), p, power, powerfile, le, target, block, fluid, itemStack, entityDamageEvent);
                            if(boolI.isPresent()){
                                if(boolI.get().equals(true)){
                                    return getResult(inverted, true);
                                }
                            }
                        }else{
                            return getResult(inverted, true);
                        }
                    }
            }
        }

        if (type.equalsIgnoreCase("origins:xp_levels")){
            String comparison = condition.get("comparison").toString();
            double compare_to = Double.parseDouble(condition.get("compare_to").toString());
            return getResult(inverted, RestrictArmor.compareValues(p.getExpToLevel(), comparison, compare_to));
        }

        if (type.equalsIgnoreCase("origins:xp_points")){
            String comparison = condition.get("comparison").toString();
            double compare_to = Double.parseDouble(condition.get("compare_to").toString());
            return getResult(inverted, RestrictArmor.compareValues(p.getTotalExperience(), comparison, compare_to));
        }

        return getResult(inverted, false);
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
