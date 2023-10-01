package me.dueris.genesismc.factory.actions;

import me.dueris.genesismc.CooldownStuff;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.OriginCommandSender;
import me.dueris.genesismc.enchantments.EnchantProtEvent;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.block.BlockCondition;
import me.dueris.genesismc.factory.conditions.entity.EntityCondition;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.Resource;
import me.dueris.genesismc.factory.powers.Toggle;
import me.dueris.genesismc.factory.powers.effects.StackingStatusEffect;
import me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.function.Predicate;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;
import static me.dueris.genesismc.factory.powers.OriginMethods.statusEffectInstance;
import static me.dueris.genesismc.factory.powers.Power.conditioned_attribute;
import static me.dueris.genesismc.factory.powers.player.FireProjectile.enderian_pearl;

public class Actions {

    public static void runbiEntity(Entity actor, Entity target, JSONObject biEntityAction) {
        String type = biEntityAction.get("type").toString();
        if (type.equals("origins:add_velocity")) {
            //TODO: make this align to the actor entity
            float x = 0.0f;
            float y = 0.0f;
            float z = 0.0f;
            boolean set = false;

            if (biEntityAction.containsKey("x")) x = Float.parseFloat(biEntityAction.get("x").toString());
            if (biEntityAction.containsKey("y")) y = Float.parseFloat(biEntityAction.get("y").toString());
            if (biEntityAction.containsKey("z")) z = Float.parseFloat(biEntityAction.get("z").toString());
            if (biEntityAction.containsKey("set")) set = Boolean.parseBoolean(biEntityAction.get("set").toString());

            if (set) target.setVelocity(new Vector(x, y, z));
            else target.setVelocity(target.getVelocity().add(new Vector(x, y, z)));
        }
        if (type.equals("origins:damage")) {
            //haven't been able to find a way to change the damage type
            float amount = 0.0f;
//                String damageType;

            if (biEntityAction.containsKey("amount"))
                amount = Float.parseFloat(biEntityAction.get("amount").toString());
//                if (biEntityAction.containsKey("damage_type")) damageType = biEntityAction.get("damage_type").toString();
//                else damageType = "minecraft:kill";
//                else damageType = "minecraft:kill";

            //target.setLastDamageCause(new EntityDamageEvent(actor, EntityDamageEvent.DamageCause.valueOf(damageType.split(":")[1].toUpperCase()), ((Player) target).getLastDamage()));
            ((Player) target).damage(amount);
        }
        if (type.equals("origins:mount")) {
            target.addPassenger(actor);
        }
        if (type.equals("origins:set_in_love")) {
            if (target instanceof Animals targetAnimal) {
                targetAnimal.setLoveModeTicks(600);
            }
        }
        if (type.equals("origins:tame")) {
            if (target instanceof Tameable targetTameable && actor instanceof AnimalTamer actorTamer) {
                targetTameable.setOwner(actorTamer);
            }
        }
        if (type.equals("origins:actor_action")) {
            EntityActionType(actor, biEntityAction);
        }
        if (type.equals("origins:target_action")) {
            EntityActionType(target, biEntityAction);
        }
    }

    public static void biEntityActionType(Entity actor, Entity target, JSONObject biEntityAction) {
        JSONObject entityAction = biEntityAction;
        String type = entityAction.get("type").toString();

        if (type.equals("origins:and")) {
            JSONArray andActions = (JSONArray) entityAction.get("actions");
            for (Object actionObj : andActions) {
                JSONObject action = (JSONObject) actionObj;
                runbiEntity(actor, target, action);
            }
        } else if (type.equals("origins:chance")) {
            double chance = Double.parseDouble(entityAction.get("chance").toString());
            double randomValue = Math.random();

            if (randomValue <= chance) {
                JSONObject action = (JSONObject) entityAction.get("action");
                runbiEntity(actor, target, action);
            }
        } else if (type.equals("origins:choice")) {
            JSONArray actionsArray = (JSONArray) entityAction.get("actions");
            List<JSONObject> actionsList = new ArrayList<>();

            for (Object actionObj : actionsArray) {
                JSONObject action = (JSONObject) actionObj;
                JSONObject element = (JSONObject) action.get("element");
                int weight = Integer.parseInt(action.get("weight").toString());
                for (int i = 0; i < weight; i++) {
                    actionsList.add(element);
                }
            }

            if (!actionsList.isEmpty()) {
                int randomIndex = (int) (Math.random() * actionsList.size());
                JSONObject chosenAction = actionsList.get(randomIndex);
                runbiEntity(actor, target, chosenAction);
            }
        } else if (type.equals("origins:delay")) {
            int ticks = Integer.parseInt(entityAction.get("ticks").toString());
            JSONObject delayedAction = (JSONObject) entityAction.get("action");

            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                runbiEntity(actor, target, delayedAction);
            }, ticks);
        } else if (type.equals("origins:nothing")) {
            // Literally does nothing
        } else if (type.equals("origins:side")) {
            String side = entityAction.get("side").toString();
            JSONObject action = (JSONObject) entityAction.get("action");
            runbiEntity(actor, target, action);

        } else {
            runbiEntity(actor, target, biEntityAction);
        }
    }

    public static EquipmentSlot getSlotFromString(String slotName) {
        switch (slotName.toLowerCase()) {
            case "armor.helmet":
                return EquipmentSlot.HEAD;
            case "armor.chest":
                return EquipmentSlot.CHEST;
            case "armor.legs":
                return EquipmentSlot.LEGS;
            case "armor.feet":
                return EquipmentSlot.FEET;
            case "hand":
                return EquipmentSlot.HAND;
            case "offhand":
                return EquipmentSlot.OFF_HAND;
            case "head":
                return EquipmentSlot.HEAD;
            case "chest":
                return EquipmentSlot.CHEST;
            case "legs":
                return EquipmentSlot.LEGS;
            case "feet":
                return EquipmentSlot.FEET;
            default:
                return null;
        }
    }

    public static void spawnEffectCloud(Entity entity, float radius, int waitTime, PotionEffect effect) {
        if (entity != null) {
            Location entityLocation = entity.getLocation();

            org.bukkit.entity.AreaEffectCloud effectCloud = entityLocation.getWorld()
                    .spawn(entityLocation, org.bukkit.entity.AreaEffectCloud.class);

            effectCloud.setRadius(radius);
            effectCloud.setDuration(waitTime);

            if (effect != null) {
                effectCloud.addCustomEffect(effect, true);
            }
        }
    }

    public static HashMap<Entity, Boolean> resourceChangeTimeout = new HashMap<>();

    private static void runEntity(Entity entity, JSONObject power) {
        JSONObject entityAction;
        entityAction = power;
        String type = entityAction.get("type").toString();

        if (type.equals("origins:modify_inventory")) {
            if (entity instanceof Player player) {
                if (power.containsKey("slot")) {
                    try {
                        if (player.getInventory().getItem(getSlotFromString(power.get("slot").toString())) == null)
                            return;
                        ItemActionType(player.getInventory().getItem(getSlotFromString(power.get("slot").toString())), power);
                    } catch (Exception e) {
                        //silently fail bc idk whats going on and yeah it wokrs lol
                    }
                }
            }
        }
        if (type.equals("origins:change_resource")){
            if(resourceChangeTimeout.containsKey(entity)) return;
            String resource = power.get("resource").toString();
            int change = Integer.parseInt(power.get("change").toString());
            double finalChange = 1.0 / Resource.getResource(resource).getRight();
            BossBar bossBar = Resource.getResource(resource).getLeft();
            double toRemove = finalChange * change;
            double newP = bossBar.getProgress() + toRemove;
            if(newP > 1.0){
                newP = 1.0;
            } else if (newP < 0) {
                newP = 0.0;
            }
            bossBar.setProgress(newP);
            bossBar.addPlayer((Player) entity);
            bossBar.setVisible(true);
            resourceChangeTimeout.put(entity, true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    resourceChangeTimeout.remove(entity);
                }
            }.runTaskLater(GenesisMC.getPlugin(), 2);
            System.out.println(bossBar.getProgress());
        }
        if (type.equals("origins:set_on_fire")){
            entity.setFireTicks(Integer.parseInt(power.get("duration").toString()));
        }
        if (type.equals("origins:spawn_entity")){
            OriginCommandSender originCommandSender = new OriginCommandSender();
            originCommandSender.setOp(true);
            Bukkit.dispatchCommand(originCommandSender, "summon $1 %1 %2 %3 $2"
                            .replace("$1", power.get("entity_type").toString())
                            .replace("$2", power.getOrDefault("tag", "").toString()
                            .replace("%1", String.valueOf(entity.getLocation().getX()))
                            .replace("%2", String.valueOf(entity.getLocation().getY()))
                            .replace("%3", String.valueOf(entity.getLocation().getZ()))
                            ));
        }
        if (type.equals("origins:spawn_particles")){
            Particle particle = Particle.valueOf(power.getOrDefault("particle", null).toString().split(":")[1].toUpperCase());
            int count = Integer.parseInt(String.valueOf(power.getOrDefault("count", 1)));
            float offset_y_no_vector = Float.parseFloat(String.valueOf(power.getOrDefault("offset_y", 1.0)));
            float offset_x = 0.25f;
            float offset_y = 0.50f;
            float offset_z = 0.25f;
            JSONObject spread = (JSONObject) power.get("spread");
            if (spread.get("y") != null) {
                offset_y = Float.parseFloat(String.valueOf(spread.get("y")));
            }

            if (spread.get("x") != null) {
                offset_x = Float.parseFloat(String.valueOf(spread.get("x")));
            }

            if (spread.get("z") != null) {
                offset_z = Float.parseFloat(String.valueOf(spread.get("z")));
            }
            entity.getWorld().spawnParticle(particle, new Location(entity.getWorld(), entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ()), count, offset_x, offset_y, offset_z, 0);
        }
        if (type.equals("origins:spawn_effect_cloud")){
            spawnEffectCloud(entity, Float.valueOf(power.getOrDefault("radius", 3.0).toString()), Integer.valueOf(power.getOrDefault("wait_time", 10).toString()), new PotionEffect(StackingStatusEffect.getPotionEffectType(power.get("effect").toString()), 1, 1));
        }
        if (type.equals("origins:replace_inventory")) {
            if (entity instanceof Player player) {
                if (power.containsKey("slot")) {
                    try {
                        if (player.getInventory().getItem(getSlotFromString(power.get("slot").toString())) == null)
                            return;
                        JSONObject jsonObject = (JSONObject) power.get("stack");
                        player.getInventory().getItem(getSlotFromString(power.get("slot").toString())).setType(Material.valueOf(jsonObject.get("item").toString().split(":")[1].toUpperCase()));
                    } catch (Exception e) {
                        //silently fail
                    }
                }
            }
        }
        if (type.equals("origins:heal")){
            if(entity instanceof Player player){
                player.setHealth(Double.parseDouble(player.getHealth() + power.get("amount").toString()));
            }
        }
        if (type.equals("origins:clear_effect")){
            PotionEffectType potionEffectType = StackingStatusEffect.getPotionEffectType(power.get("effect").toString());
            if(entity instanceof Player player){
                if(player.hasPotionEffect(potionEffectType)){
                    player.removePotionEffect(potionEffectType);
                }
            }
        }
        if (type.equals("origins:exhaust")){
            if (entity instanceof Player player){
                player.setFoodLevel(player.getFoodLevel() - Math.round(Float.valueOf(power.get("amount").toString())));
            }
        }
        if (type.equals("origins:explode")){
            float explosionPower = 1f;
            String destruction_type = "break";
            JSONObject indestructible = new JSONObject();
            JSONObject destructible = new JSONObject();
            boolean create_fire = false;

            if (power.containsKey("power"))
                explosionPower = Float.parseFloat(power.get("power").toString());
            if (power.containsKey("destruction_type"))
                destruction_type = power.get("destruction_type").toString();
            if (power.containsKey("indestructible"))
                indestructible = (JSONObject) power.get("indestructible");
            if (power.containsKey("destructible")) destructible = (JSONObject) power.get("destructible");
            if (power.containsKey("create_fire"))
                create_fire = Boolean.parseBoolean(power.get("create_fire").toString());

            entity.getLocation().createExplosion(explosionPower, create_fire);
        }
        if (type.equals("origins:crafting_table")){
            if(entity instanceof Player player){
                Inventory inventory = Bukkit.createInventory(player, InventoryType.CRAFTING);
                player.openInventory(inventory);
            }
        }
        if (type.equals("origins:ender_chest")){
            if(entity instanceof Player player){
                Inventory inventory = Bukkit.createInventory(player, InventoryType.ENDER_CHEST);
                player.openInventory(inventory);
            }
        }
        if (type.equals("origins:equipped_item_action")){
            if(entity instanceof Player player){
                if (power.containsKey("equipment_slot")) {
                    try {
                        if (player.getInventory().getItem(getSlotFromString(power.get("equipment_slot").toString())) == null)
                            return;
                        ItemActionType(player.getInventory().getItem(getSlotFromString(power.get("equipment_slot").toString())), power);
                    } catch (Exception e) {
                        //silently fail
                    }
                }
            }
        }
        if (type.equals("origins:dismount")){
            for(Entity entity1 : Bukkit.getWorld(entity.getWorld().getUID()).getEntities()){
                entity1.getPassengers().remove(entity);
            }
        }
        if (type.equals("origins:feed")){
            if (entity instanceof Player player){
                player.setFoodLevel(player.getFoodLevel() + Integer.parseInt(power.get("food").toString()));
                player.setSaturation(player.getSaturation() + Float.parseFloat(power.get("saturation").toString()));
            }
        }
        if (type.equals("origins:fire_projectile")){
            if(entity instanceof ProjectileSource){
                float finalDivergence1 = Float.parseFloat(power.getOrDefault("divergence", 1.0).toString());
                float speed = Float.parseFloat(power.getOrDefault("speed", 1).toString());
                EntityType typeE;
                if (power.getOrDefault("entity_type", null).toString().equalsIgnoreCase("origins:enderian_pearl")) {
                    typeE = EntityType.ENDER_PEARL;
                } else {
                    typeE = EntityType.valueOf(power.getOrDefault("entity_type", null).toString().split(":")[1].toUpperCase());
                }
                Projectile projectile = (Projectile) entity.getWorld().spawnEntity(entity.getLocation(), typeE);
                projectile.setShooter((ProjectileSource) entity);

                Vector direction = entity.getLocation().getDirection();

                double yawRadians = Math.toRadians(entity.getLocation().getYaw() + finalDivergence1);

                double x = -Math.sin(yawRadians) * Math.cos(Math.toRadians(entity.getLocation().getPitch()));
                double y = -Math.sin(Math.toRadians(entity.getLocation().getPitch()));
                double z = Math.cos(yawRadians) * Math.cos(Math.toRadians(entity.getLocation().getPitch()));

                direction.setX(x);
                direction.setY(y);
                direction.setZ(z);

                projectile.setVelocity(direction.normalize().multiply(speed));
                projectile.setGlowing(true);
            }
        }
        if (type.equals("origins:passenger_action")){
            runEntity(entity.getPassenger(), (JSONObject) power.get("action"));
            runbiEntity(entity, entity.getPassenger(), (JSONObject) power.get("bientity_action"));
        }
        if (type.equals("origins:raycast")){
            Predicate<Entity> filter = entity1 -> !entity1.equals(entity);
            if(power.get("before_action") != null){
                runEntity(entity, (JSONObject) power.get("before_action"));
            }
            RayTraceResult traceResult = entity.getWorld().rayTrace(entity.getLocation(), entity.getLocation().getDirection(), 12, FluidCollisionMode.valueOf(power.getOrDefault("fluid_handling", "NEVER").toString().toUpperCase()), false, 1, filter);
            if(traceResult != null){
                if(traceResult.getHitEntity() != null){
                    Entity entity2 = traceResult.getHitEntity();
                    if (entity2.isDead() || !(entity2 instanceof LivingEntity)) return ;
                    if (entity2.isInvulnerable()) return;
                    if (entity2.getPassengers().contains(entity)) return;
                    if(power.get("bientity_action") != null){
                        runbiEntity(entity, entity2, (JSONObject) power.get("bientity_action"));
                    }
                }
                if(traceResult.getHitBlock() != null){
                    if(power.get("block_action") != null){
                        runBlock(traceResult.getHitBlock().getLocation(), (JSONObject) power.get("block_action"));
                    }
                }
                if(power.get("after_action") != null){
                    runEntity(entity, (JSONObject) power.get("after_action"));
                }
            }else{
                if(power.get("miss_action") != null){
                    runEntity(entity, (JSONObject) power.get("miss_action"));
                }
            }
        }
        if (type.equals("origins:extinguish")) {
            entity.setFireTicks(0);
        }
        if (type.equals("origins:play_sound")) {
            entity.getWorld().playSound(entity, Sound.valueOf(power.get("sound").toString().toUpperCase().split(":")[1].replace(".", "_")), 8, 1);
        }
        if (type.equals("origins:gain_air")) {
            long amt = (long) power.get("value");
            if (entity instanceof Player p) {
                p.setRemainingAir(p.getRemainingAir() + Math.toIntExact(amt));
            }
        }
        if (type.equals("origins:drop_inventory")){
            if (entity instanceof Player player) {
                if (power.containsKey("slot")) {
                    try {
                        if (player.getInventory().getItem(getSlotFromString(power.get("slot").toString())) == null)
                            return;
                        ItemActionType(player.getInventory().getItem(getSlotFromString(power.get("slot").toString())), power);
                    } catch (Exception e) {
                        //fail noononooo
                    }
                }
            }
        }
        if (type.equals("origins:grant_advancement")){
            OriginCommandSender originCommandSender = new OriginCommandSender();
            originCommandSender.setOp(true);
            Bukkit.dispatchCommand(originCommandSender, "advancement grant $1 $2".replace("$1", entity.getName()).replace("$2", power.get("advacnement").toString()));
        }
        if (type.equals("origins:revoke_advancement")){
            OriginCommandSender originCommandSender = new OriginCommandSender();
            originCommandSender.setOp(true);
            Bukkit.dispatchCommand(originCommandSender, "advancement revoke $1 $2".replace("$1", entity.getName()).replace("$2", power.get("advacnement").toString()));
        }
        if (type.equals("origins:selector_action")){
            if(power.get("bientity_condition") != null){
                if(entity instanceof Player player){
                    runbiEntity(entity, player.getTargetEntity(AttributeHandler.Reach.getDefaultReach(player), false), (JSONObject) power.get("bientity_condition"));
                }
            }
        }
        if (type.equals("origins:give")) {
            int amt = 1;
            if (power.containsKey("amount")) {
                amt = Integer.parseInt(power.get("amount").toString());
            }

            if (entityAction.containsKey("stack")) {
                JSONObject stackObject = (JSONObject) entityAction.get("stack");
                String item = stackObject.get("item").toString();
                int amount = Integer.parseInt(String.valueOf(stackObject.getOrDefault("amount", 1)));

                ItemStack itemStack = new ItemStack(Material.valueOf(item.toUpperCase().split(":")[1]), amount);

                if (entityAction.containsKey("item_action")) {
                    ItemActionType(itemStack, power);
                }
                if (entity instanceof Player player) {
                    player.getInventory().addItem(itemStack);
                }
            }

        }
        if (type.equals("origins:damage")) {
            if (entity instanceof Player P) {
                P.damage(Double.valueOf(power.get("amount").toString()));
                P.setLastDamageCause(new EntityDamageEvent(P, EntityDamageEvent.DamageCause.CUSTOM, Double.valueOf(power.get("amount").toString())));
            }
        }
        if (type.equals("origins:add_velocity")) {
            float y = 0.0f;
            boolean set = false;

            if (entityAction.containsKey("y")) y = Float.parseFloat(entityAction.get("y").toString());

            if (entity instanceof Player player) {
                Location location = player.getEyeLocation();
                @NotNull Vector direction = location.getDirection().normalize();
                Vector velocity = direction.multiply(y + 1.8);
                player.setVelocity(velocity);
            }
        }
        if (type.equals("origins:execute_command")) {
            OriginCommandSender originCommandSender = new OriginCommandSender();
            originCommandSender.setOp(true);
            final boolean lastSendCMDFeedback = Boolean.parseBoolean(GameRule.SEND_COMMAND_FEEDBACK.toString());
            Bukkit.dispatchCommand(originCommandSender, "gamerule sendCommandFeedback false");
            final boolean lastlogAdminCMDs = Boolean.parseBoolean(GameRule.LOG_ADMIN_COMMANDS.toString());
            Bukkit.dispatchCommand(originCommandSender, "gamerule logAdminCommands false");
            final boolean isOp = entity.isOp();
            entity.setOp(true);
            String cmd = null;
            if(power.get("command").toString().startsWith("/")){
                cmd = power.get("command").toString().split("/")[1];
            }else{
                cmd = power.get("command").toString();
            }
            Bukkit.dispatchCommand(entity, cmd);
            entity.setOp(isOp);
            Bukkit.dispatchCommand(originCommandSender, "gamerule logAdminCommands {bool}".replace("{bool}", String.valueOf(lastlogAdminCMDs)));
            Bukkit.dispatchCommand(originCommandSender, "gamerule logAdminCommands {bool}".replace("{bool}", String.valueOf(lastSendCMDFeedback)));
        }
        if (type.equals("origins:add_xp")) {
            int points = 0;
            int levels = 0;

            if (entityAction.containsKey("points")) points = Integer.parseInt(entityAction.get("points").toString());
            if (entityAction.containsKey("levels")) levels = Integer.parseInt(entityAction.get("levels").toString());

            if (entity instanceof Player player) {
                player.giveExp(points);
                player.setLevel(player.getLevel() + levels);
            }
        }
        if (type.equals("origins:apply_effect")) {
            if (entity instanceof LivingEntity player) {
                statusEffectInstance(player, entityAction);
            }
        }
        if (type.equals("origins:area_of_effect")) {
            float radius = 15f;
            JSONObject bientity_action = new JSONObject();
            JSONObject bientity_condition = new JSONObject();
            boolean include_target = false;

            if (entityAction.containsKey("radius")) radius = Float.parseFloat(entityAction.get("radius").toString());
            if (entityAction.containsKey("bientity_action"))
                bientity_action = (JSONObject) entityAction.get("bientity_action");
            if (entityAction.containsKey("bientity_condition"))
                bientity_condition = (JSONObject) entityAction.get("bientity_condition");
            if (entityAction.containsKey("include_target"))
                include_target = Boolean.parseBoolean(entityAction.get("include_target").toString());

            for (Entity nearbyEntity : entity.getNearbyEntities(radius, radius, radius)) {
                biEntityActionType(entity, nearbyEntity, bientity_action);
            }
            if (include_target) biEntityActionType(entity, entity, bientity_action);
        }
        if (type.equals("origins:block_action_at")) {
            BlockActionType(entity.getLocation(), entityAction);
        }
        if (type.equals("origins:toggle")) {
            if (entity instanceof Player) {
                for (OriginContainer origin : OriginPlayer.getOrigin((Player) entity).values()) {
                    if (origin.getPowers().contains(power.get("power"))) {
                        for (PowerContainer powerContainer : origin.getPowerContainers()) {
                            if (powerContainer.getType().equals("origins:toggle")) {
                                Toggle toggle = new Toggle();
                                toggle.execute((Player) entity, powerContainer);
                            }
                        }
                    }
                }
            }
        }
        if (type.equals("origins:set_fall_distance")){
            entity.setFallDistance(Float.parseFloat(power.get("fall_distance").toString()));
        }
        if (type.equals("origins:trigger_cooldown")) {
            if (entity instanceof Player player) {
                for (OriginContainer origin : OriginPlayer.getOrigin((Player) entity).values()) {
                    if (origin.getPowers().contains(power.get("power"))) {
                        for (PowerContainer powerContainer : origin.getPowerContainers()) {
                            if (powerContainer.get("cooldown") != null) {
                                String key = "*";
                                if (powerContainer.getKey().getOrDefault("key", "key.origins.primary_active") != null) {
                                    key = powerContainer.getKey().getOrDefault("key", "key.origins.primary_active").toString();
                                    if (powerContainer.getType().equals("origins:action_on_hit")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("origins:action_when_damage_taken")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("origins:action_when_hit")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("origins:action_self")) {
                                        key = "key.use";
                                    } else if (powerContainer.getType().equals("origins:attacker_action_when_hit")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("origins:self_action_on_hit")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("origins:self_action_on_kill")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("origins:self_action_when_hit")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("origins:target_action_on_hit")) {
                                        key = "key.attack";
                                    }
                                }
                                CooldownStuff.addCooldown(player, origin, powerContainer.getTag(), powerContainer.getType(), Integer.parseInt(powerContainer.get("cooldown")), key);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void EntityActionType(Entity entity, JSONObject power) {
        JSONObject entityAction;
        entityAction = power;
        if(entityAction == null) return;
        if(entityAction.get("type") == null) return;
        String type = entityAction.get("type").toString();

        if (type.equals("origins:and")) {
            JSONArray andActions = (JSONArray) entityAction.get("actions");
            for (Object actionObj : andActions) {
                JSONObject action = (JSONObject) actionObj;
                EntityActionType(entity, action);
            }
        } else if (type.equals("origins:chance")) {
            double chance = Double.parseDouble(entityAction.get("chance").toString());
            double randomValue = Math.random();

            if (randomValue <= chance) {
                JSONObject action = (JSONObject) entityAction.get("action");
                EntityActionType(entity, action);
            } else if (entityAction.containsKey("fail_action")) {
                JSONObject failAction = (JSONObject) entityAction.get("fail_action");
                EntityActionType(entity, failAction);
            }
        } else if (type.equals("origins:choice")) {
            JSONArray actionsArray = (JSONArray) entityAction.get("actions");
            List<JSONObject> actionsList = new ArrayList<>();

            for (Object actionObj : actionsArray) {
                JSONObject action = (JSONObject) actionObj;
                JSONObject element = (JSONObject) action.get("element");
                int weight = Integer.parseInt(action.get("weight").toString());
                for (int i = 0; i < weight; i++) {
                    actionsList.add(element);
                }
            }

            if (!actionsList.isEmpty()) {
                int randomIndex = (int) (Math.random() * actionsList.size());
                JSONObject chosenAction = actionsList.get(randomIndex);
                EntityActionType(entity, chosenAction);
            }
        } else if (type.equals("origins:delay")) {
            int ticks = Integer.parseInt(entityAction.get("ticks").toString());
            JSONObject delayedAction = (JSONObject) entityAction.get("action");

            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                EntityActionType(entity, delayedAction);
            }, ticks);
        } else if (type.equals("origins:nothing")) {
            //literally does nothin
        } else if (type.equals("origins:side")) {
            JSONObject action = (JSONObject) entityAction.get("action");
            EntityActionType(entity, action);
        } else {
            runEntity(entity, power);
        }
    }

    public static void BlockActionType(Location location, JSONObject power) {
        if (power == null) return;
        JSONObject entityAction = (JSONObject) power.get("action");
        if (entityAction == null) {
            entityAction = (JSONObject) power.get("block_action");
        }
        if (entityAction == null) return;
        String type = entityAction.get("type").toString();

        if (type.equals("origins:and")) {
            JSONArray andActions = (JSONArray) entityAction.get("actions");
            for (Object actionObj : andActions) {
                JSONObject action = (JSONObject) actionObj;
                runBlock(location, action);
            }
        } else if (type.equals("origins:chance")) {
            double chance = Double.parseDouble(entityAction.get("chance").toString());
            double randomValue = Math.random();

            if (randomValue <= chance) {
                JSONObject action = (JSONObject) entityAction.get("action");
                runBlock(location, action);
            } else if (entityAction.containsKey("fail_action")) {
                JSONObject failAction = (JSONObject) entityAction.get("fail_action");
                runBlock(location, failAction);
            }
        } else if (type.equals("origins:choice")) {
            JSONArray actionsArray = (JSONArray) entityAction.get("actions");
            List<JSONObject> actionsList = new ArrayList<>();

            for (Object actionObj : actionsArray) {
                JSONObject action = (JSONObject) actionObj;
                JSONObject element = (JSONObject) action.get("element");
                int weight = Integer.parseInt(action.get("weight").toString());
                for (int i = 0; i < weight; i++) {
                    actionsList.add(element);
                }
            }

            if (!actionsList.isEmpty()) {
                int randomIndex = (int) (Math.random() * actionsList.size());
                JSONObject chosenAction = actionsList.get(randomIndex);
                runBlock(location, chosenAction);
            }
        } else if (type.equals("origins:delay")) {
            int ticks = Integer.parseInt(entityAction.get("ticks").toString());
            JSONObject delayedAction = (JSONObject) entityAction.get("action");

            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                runBlock(location, delayedAction);
            }, ticks);
        } else if (type.equals("origins:nothing")) {
            // Literally does nothing
        } else if (type.equals("origins:side")) {
            JSONObject action = (JSONObject) entityAction.get("action");
            runBlock(location, action);
        } else {
            runBlock(location, power);
        }
    }

    public static void iterateAndChangeBlocks(World world, int centerX, int centerY, int centerZ,
                                              Material targetMaterial1, float initialChance, float chanceDecrease,
                                              Material targetMaterial2, float thresholdPercentage) {
        Random random = new Random();

        for (int radius = 0; radius < 20; radius++) {
            for (int x = centerX - radius; x <= centerX + radius; x++) {
                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                    Block block = world.getHighestBlockAt(x, z);
                    if (block.getType().isSolid()) {
                        float chance = initialChance - radius * chanceDecrease;
                        if (chance <= 0.0f) {
                            break;
                        }
                        if (random.nextFloat() <= chance) {
                            block.setType(targetMaterial1);
                        }
                    }
                }
            }

            int totalBlocks = (2 * radius + 1) * (2 * radius + 1);
            int changedBlocks = totalBlocks - world.getHighestBlockYAt(centerX, centerZ);

            float percentage = (float) changedBlocks / totalBlocks;

            if (percentage < thresholdPercentage) {
                for (int x = centerX - radius; x <= centerX + radius; x++) {
                    for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                        Block block = world.getHighestBlockAt(x, z);
                        block.setType(targetMaterial2);
                    }
                }
                for (int x = centerX - radius; x <= centerX + radius; x++) {
                    for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                        Block block = world.getBlockAt(x, centerY + 1, z);
                        block.setType(targetMaterial2);
                    }
                }
                break;
            }
        }
    }

    private static void runBlock(Location location, JSONObject power) {
        JSONObject blockAction = (JSONObject) power.get("block_action");
        String type = blockAction.get("type").toString();

        if (type.equals("origins:add_block")) {
            if (blockAction.containsKey("block")) {
                Material block;
                block = Material.getMaterial(blockAction.get("block").toString().split(":")[1].toUpperCase());
                if (block == null) return;

                //i experimented with it, and it seemed that it just set it one block above?
                //still unsure about this one tho
                location.add(0d, 1d, 0d);
                location.getWorld().getBlockAt(location).setType(block);
            }
        }
        if (type.equals("genesis:grow_sculk")) {
            location.getBlock().setType(Material.SCULK_CATALYST);
            new BukkitRunnable() {
                @Override
                public void run() {
                    int centerX = location.getBlockX();
                    int centerY = location.getBlockY();
                    int centerZ = location.getBlockZ();
                    Material sculkStage1 = Material.SCULK;
                    float initialChance = 0.8f;
                    float chanceDecrease = 0.05f;
                    Material sculkStage2 = Material.SCULK_VEIN;
                    float thresholdPercentage = 0.2f;

                    World world = location.getWorld();

                    iterateAndChangeBlocks(world, centerX, centerY, centerX, sculkStage1, initialChance, chanceDecrease, sculkStage2, thresholdPercentage);
                }
            }.runTaskLater(GenesisMC.getPlugin(), 1);

        }
        if (type.equals("origins:bonemeal")) {
            Block block = location.getWorld().getBlockAt(location);
            block.applyBoneMeal(BlockFace.SELF);
        }
        if (type.equals("origins:explode")) {

            float explosionPower = 1f;
            String destruction_type = "break";
            JSONObject indestructible = new JSONObject();
            JSONObject destructible = new JSONObject();
            boolean create_fire = false;

            if (blockAction.containsKey("power"))
                explosionPower = Float.parseFloat(blockAction.get("power").toString());
            if (blockAction.containsKey("destruction_type"))
                destruction_type = blockAction.get("destruction_type").toString();
            if (blockAction.containsKey("indestructible"))
                indestructible = (JSONObject) blockAction.get("indestructible");
            if (blockAction.containsKey("destructible")) destructible = (JSONObject) blockAction.get("destructible");
            if (blockAction.containsKey("create_fire"))
                create_fire = Boolean.parseBoolean(blockAction.get("create_fire").toString());

            location.createExplosion(explosionPower, create_fire);
        }
        if (type.equals("origins:execute_command")){
            OriginCommandSender originCommandSender = new OriginCommandSender();
            originCommandSender.setOp(true);
            final boolean lastSendCMDFeedback = Boolean.parseBoolean(GameRule.SEND_COMMAND_FEEDBACK.toString());
            Bukkit.dispatchCommand(originCommandSender, "gamerule sendCommandFeedback false");
            final boolean lastlogAdminCMDs = Boolean.parseBoolean(GameRule.LOG_ADMIN_COMMANDS.toString());
            Bukkit.dispatchCommand(originCommandSender, "gamerule logAdminCommands false");
            String cmd = null;
            if(power.get("command").toString().startsWith("/")){
                cmd = power.get("command").toString().split("/")[1];
            }else{
                cmd = power.get("command").toString();
            }
            Bukkit.dispatchCommand(originCommandSender, cmd);
            Bukkit.dispatchCommand(originCommandSender, "gamerule logAdminCommands {bool}".replace("{bool}", String.valueOf(lastlogAdminCMDs)));
            Bukkit.dispatchCommand(originCommandSender, "gamerule logAdminCommands {bool}".replace("{bool}", String.valueOf(lastSendCMDFeedback)));
        }
        if (type.equals("origins:set_block")){
            location.getBlock().setType(Material.valueOf(blockAction.get("block").toString().split(":")[1].toUpperCase()));
        }
    }

    private static void runBlockEntity(Entity entity, Location location, JSONObject power) {
        JSONObject blockAction = (JSONObject) power.get("block_entity_action");
        String type = blockAction.get("type").toString();
        if (type.equals("genesis:set_spawn")) {
            if (entity instanceof Player p) {
                p.setBedSpawnLocation(location);
            }
        }
    }

    public static void BlockEntityType(Entity entity, Location location, JSONObject power) {
        JSONObject entityAction;
        entityAction = (JSONObject) power.get("action");
        if (entityAction == null) entityAction = (JSONObject) power.get("entity_action");
        String type = entityAction.get("type").toString();

        if (type.equals("origins:and")) {
            JSONArray andActions = (JSONArray) entityAction.get("actions");
            for (Object actionObj : andActions) {
                JSONObject action = (JSONObject) actionObj;
                runBlockEntity(entity, location, action);
            }
        } else if (type.equals("origins:chance")) {
            double chance = Double.parseDouble(entityAction.get("chance").toString());
            double randomValue = Math.random();

            if (randomValue <= chance) {
                JSONObject action = (JSONObject) entityAction.get("action");
                runBlockEntity(entity, location, action);
            } else if (entityAction.containsKey("fail_action")) {
                JSONObject failAction = (JSONObject) entityAction.get("fail_action");
                runBlockEntity(entity, location, failAction);
            }
        } else if (type.equals("origins:choice")) {
            JSONArray actionsArray = (JSONArray) entityAction.get("actions");
            List<JSONObject> actionsList = new ArrayList<>();

            for (Object actionObj : actionsArray) {
                JSONObject action = (JSONObject) actionObj;
                JSONObject element = (JSONObject) action.get("element");
                int weight = Integer.parseInt(action.get("weight").toString());
                for (int i = 0; i < weight; i++) {
                    actionsList.add(element);
                }
            }

            if (!actionsList.isEmpty()) {
                int randomIndex = (int) (Math.random() * actionsList.size());
                JSONObject chosenAction = actionsList.get(randomIndex);
                runBlockEntity(entity, location, chosenAction);
            }
        } else if (type.equals("origins:delay")) {
            int ticks = Integer.parseInt(entityAction.get("ticks").toString());
            JSONObject delayedAction = (JSONObject) entityAction.get("action");

            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                runBlockEntity(entity, location, delayedAction);
            }, ticks);
        } else if (type.equals("origins:nothing")) {
            //literally does nothin
        } else if (type.equals("origins:side")) {
            JSONObject action = (JSONObject) entityAction.get("action");
            runBlockEntity(entity, location, action);
        } else {
            runBlockEntity(entity, location, power);
        }
    }

    public static void ItemActionType(ItemStack item, JSONObject power) {
        if (power == null) return;
        JSONObject entityAction = power;
        if (entityAction == null) {
            entityAction = (JSONObject) power.get("item_action");
        }
        if (entityAction.get("type") == null) return;
        String type = entityAction.get("type").toString();

        if (type.equals("origins:and")) {
            JSONArray andActions = (JSONArray) entityAction.get("actions");
            for (Object actionObj : andActions) {
                JSONObject action = (JSONObject) actionObj;
                runItem(item, action);
            }
        } else if (type.equals("origins:chance")) {
            double chance = Double.parseDouble(entityAction.get("chance").toString());
            double randomValue = Math.random();

            if (randomValue <= chance) {
                JSONObject action = (JSONObject) entityAction.get("action");
                runItem(item, action);
            } else if (entityAction.containsKey("fail_action")) {
                JSONObject failAction = (JSONObject) entityAction.get("fail_action");
                runItem(item, failAction);
            }
        } else if (type.equals("origins:choice")) {
            JSONArray actionsArray = (JSONArray) entityAction.get("actions");
            List<JSONObject> actionsList = new ArrayList<>();

            for (Object actionObj : actionsArray) {
                JSONObject action = (JSONObject) actionObj;
                JSONObject element = (JSONObject) action.get("element");
                int weight = Integer.parseInt(action.get("weight").toString());
                for (int i = 0; i < weight; i++) {
                    actionsList.add(element);
                }
            }

            if (!actionsList.isEmpty()) {
                int randomIndex = (int) (Math.random() * actionsList.size());
                JSONObject chosenAction = actionsList.get(randomIndex);
                runItem(item, chosenAction);
            }
        } else if (type.equals("origins:delay")) {
            int ticks = Integer.parseInt(entityAction.get("ticks").toString());
            JSONObject delayedAction = (JSONObject) entityAction.get("action");

            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                runItem(item, delayedAction);
            }, ticks);
        } else if (type.equals("origins:nothing")) {
            // Literally does nothing
        } else if (type.equals("origins:side")) {
            JSONObject action = (JSONObject) entityAction.get("action");
            runItem(item, action);
        } else {
            runItem(item, power);
        }
    }

    private static void runItem(ItemStack item, JSONObject power) {
        JSONObject itemAction = (JSONObject) power.get("item_action");
        String type = itemAction.get("type").toString();
        if (type.equals("origins:damage")) {
            item.setDurability((short) (item.getDurability() + Short.parseShort(itemAction.get("amount").toString())));
        }
        if (type.equals("origins:consume")){
            for(Player player : Bukkit.getOnlinePlayers()){
                if(player.getInventory().contains(item)){
                    if(item.getType().isEdible()){
                        item.setAmount(item.getAmount() - 1);
                        player.setSaturation(player.getSaturation() + 2);
                        player.setFoodLevel(player.getFoodLevel() + 3);
                    }
                }
            }
        }
        if (type.equals("origins:remove_enchantment")){
            Enchantment enchantment = Enchantment.getByKey(new NamespacedKey(power.get("enchantment").toString().split(":")[0], power.get("enchantment").toString().split(":")[1]));
            if(item.containsEnchantment(enchantment)){
                item.removeEnchantment(enchantment);
            }
        }
    }

}
