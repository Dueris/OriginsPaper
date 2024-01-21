package me.dueris.genesismc.factory.actions;

import me.dueris.genesismc.CooldownManager;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.events.AddToSetEvent;
import me.dueris.genesismc.events.RemoveFromSetEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.Resource;
import me.dueris.genesismc.factory.powers.Toggle;
import me.dueris.genesismc.factory.powers.effects.StackingStatusEffect;
import me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.Utils;
import me.dueris.genesismc.utils.apoli.Space;
import me.dueris.genesismc.utils.console.OriginConsoleSender;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
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
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.joml.Vector3f;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.function.Predicate;

import static me.dueris.genesismc.factory.powers.OriginMethods.statusEffectInstance;
import static me.dueris.genesismc.utils.KeybindUtils.addItems;

public class Actions {

    public static HashMap<Entity, Boolean> resourceChangeTimeout = new HashMap<>();

    public static void BiEntityActionType(Entity actor, Entity target, JSONObject power) {
        String type = power.get("type").toString();

        if (type.equals("apoli:invert")) {
            BiEntityActionType(target, actor, (JSONObject) power.get("action"));
        } else if (type.equals("apoli:and")) {
            JSONArray andActions = (JSONArray) power.get("actions");
            for (Object actionObj : andActions) {
                JSONObject action = (JSONObject) actionObj;
                runbiEntity(actor, target, action);
            }
        } else if (type.equals("apoli:chance")) {
            double chance = Double.parseDouble(power.get("chance").toString());
            double randomValue = Math.random();

            if (randomValue <= chance) {
                JSONObject action = (JSONObject) power.get("action");
                runbiEntity(actor, target, action);
            }
        } else if (type.equals("apoli:choice")) {
            JSONArray actionsArray = (JSONArray) power.get("actions");
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
        } else if (type.equals("apoli:delay")) {
            int ticks = Integer.parseInt(power.get("ticks").toString());
            JSONObject delayedAction = (JSONObject) power.get("action");

            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                runbiEntity(actor, target, delayedAction);
            }, ticks);
        } else if (type.equals("apoli:nothing")) {
            // Literally does nothing
        } else if (type.equals("apoli:side")) {
            JSONObject action = (JSONObject) power.get("action");
            runbiEntity(actor, target, action);
        } else if (type.equals("apoli:if_else")) {
            if(actor instanceof Player p){
                Optional<Boolean> bool = ConditionExecutor.biEntityCondition.check((JSONObject) power.get("condition"), actor, target, actor.getLocation().getBlock(), null, null, null);
                if(bool.isPresent()){
                    if(bool.get()){BiEntityActionType(actor, target, (JSONObject) power.get("if_action"));}
                    else{BiEntityActionType(actor, target, (JSONObject) power.get("else_action"));}
                }else{BiEntityActionType(actor, target, (JSONObject) power.get("else_action"));}
            }else if(target instanceof Player p){
                Optional<Boolean> bool = ConditionExecutor.biEntityCondition.check((JSONObject) power.get("condition"), actor, target, actor.getLocation().getBlock(), null, null, null);
                if(bool.isPresent()){
                    if(bool.get()){BiEntityActionType(actor, target, (JSONObject) power.get("if_action"));}
                    else{BiEntityActionType(actor, target, (JSONObject) power.get("else_action"));}
                }else{BiEntityActionType(actor, target, (JSONObject) power.get("else_action"));}
            }else{
                BiEntityActionType(actor, target, (JSONObject) power.get("else_action"));
            }
        } else {
            runbiEntity(actor, target, power);
        }
    }

    public static void ItemActionType(ItemStack item, JSONObject power) {
        if (power == null) return;
        if (power.get("type") == null) return;
        String type = power.get("type").toString();

        if (type.equals("apoli:and")) {
            JSONArray andActions = (JSONArray) power.get("actions");
            for (Object actionObj : andActions) {
                JSONObject action = (JSONObject) actionObj;
                runItem(item, action);
            }
        } else if (type.equals("apoli:chance")) {
            double chance = Double.parseDouble(power.get("chance").toString());
            double randomValue = Math.random();

            if (randomValue <= chance) {
                JSONObject action = (JSONObject) power.get("action");
                runItem(item, action);
            } else if (power.containsKey("fail_action")) {
                JSONObject failAction = (JSONObject) power.get("fail_action");
                runItem(item, failAction);
            }
        } else if (type.equals("apoli:choice")) {
            JSONArray actionsArray = (JSONArray) power.get("actions");
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
        } else if (type.equals("apoli:delay")) {
            int ticks = Integer.parseInt(power.get("ticks").toString());
            JSONObject delayedAction = (JSONObject) power.get("action");

            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                runItem(item, delayedAction);
            }, ticks);
        } else if (type.equals("apoli:nothing")) {
            // Literally does nothing
        } else if (type.equals("apoli:if_else")) {
            Optional<Boolean> bool = ConditionExecutor.itemCondition.check((JSONObject) power.get("condition"), null, null, null, null, item, null);
            if(bool.isPresent()){
                if(bool.get()){ItemActionType(item, (JSONObject) power.get("if_action"));}
                else{ItemActionType(item, (JSONObject) power.get("else_action"));}
            }else{ItemActionType(item, (JSONObject) power.get("else_action"));}
        } else if (type.equals("apoli:side")) {
            JSONObject action = (JSONObject) power.get("action");
            runItem(item, action);
        } else {
            runItem(item, power);
        }
    }

    public static void EntityActionType(Entity entity, JSONObject power) {
        if (power == null) return;
        if (power.get("type") == null) return;
        String type = power.get("type").toString();

        if (type.equals("apoli:and")) {
            JSONArray andActions = (JSONArray) power.get("actions");
            for (Object actionObj : andActions) {
                JSONObject action = (JSONObject) actionObj;
                EntityActionType(entity, action);
            }
        } else if (type.equals("apoli:chance")) {
            double chance = Double.parseDouble(power.get("chance").toString());
            double randomValue = Math.random();

            if (randomValue <= chance) {
                JSONObject action = (JSONObject) power.get("action");
                EntityActionType(entity, action);
            } else if (power.containsKey("fail_action")) {
                JSONObject failAction = (JSONObject) power.get("fail_action");
                EntityActionType(entity, failAction);
            }
        } else if (type.equals("apoli:choice")) {
            JSONArray actionsArray = (JSONArray) power.get("actions");
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
        } else if (type.equals("apoli:delay")) {
            int ticks = Integer.parseInt(power.get("ticks").toString());
            JSONObject delayedAction = (JSONObject) power.get("action");

            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                EntityActionType(entity, delayedAction);
            }, ticks);
        } else if (type.equals("apoli:nothing")) {
            //literally does nothin
        } else if (type.equals("apoli:if_else")) {
            if(entity instanceof Player p){
                Optional<Boolean> bool = ConditionExecutor.entityCondition.check((JSONObject) power.get("condition"), entity, null, entity.getLocation().getBlock(), null, null, null);
                if(bool.isPresent()){
                    if(bool.get()){EntityActionType(entity, (JSONObject) power.get("if_action"));}
                    else{EntityActionType(entity, (JSONObject) power.get("else_action"));}
                }else{EntityActionType(entity, (JSONObject) power.get("else_action"));}
            }else{EntityActionType(entity, (JSONObject) power.get("else_action"));}
        } else if (type.equals("apoli:side")) {
            JSONObject action = (JSONObject) power.get("action");
            EntityActionType(entity, action);
        } else {
            runEntity(entity, power);
        }
    }

    public static void BlockActionType(Location location, JSONObject power) {
        if (power == null) return;
        String type = power.get("type").toString();

        if (type.equals("apoli:and")) {
            JSONArray andActions = (JSONArray) power.get("actions");
            for (Object actionObj : andActions) {
                JSONObject action = (JSONObject) actionObj;
                runBlock(location, action);
            }
        } else if (type.equals("apoli:chance")) {
            double chance = Double.parseDouble(power.get("chance").toString());
            double randomValue = Math.random();

            if (randomValue <= chance) {
                JSONObject action = (JSONObject) power.get("action");
                runBlock(location, action);
            } else if (power.containsKey("fail_action")) {
                JSONObject failAction = (JSONObject) power.get("fail_action");
                runBlock(location, failAction);
            }
        } else if (type.equals("apoli:choice")) {
            JSONArray actionsArray = (JSONArray) power.get("actions");
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
        } else if (type.equals("apoli:delay")) {
            int ticks = Integer.parseInt(power.get("ticks").toString());
            JSONObject delayedAction = (JSONObject) power.get("action");

            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                runBlock(location, delayedAction);
            }, ticks);
        } else if (type.equals("apoli:nothing")) {
            // Literally does nothing
        } else if (type.equals("apoli:if_else")) {
            Optional<Boolean> bool = ConditionExecutor.blockCondition.check((JSONObject) power.get("condition"), null, null, location.getBlock(), null, null, null);
            if(bool.isPresent()){
                if(bool.get()){BlockActionType(location, (JSONObject) power.get("if_action"));}
                else{BlockActionType(location, (JSONObject) power.get("else_action"));}
            }else{BlockActionType(location, (JSONObject) power.get("else_action"));}
        } else if (type.equals("apoli:side")) {
            JSONObject action = (JSONObject) power.get("action");
            runBlock(location, action);
        } else {
            runBlock(location, power);
        }
    }

    private static void runBlock(Location location, JSONObject power) {
        String type = power.get("type").toString();

        if (type.equals("apoli:add_block")) {
            if (power.containsKey("block")) {
                Material block;
                block = Material.getMaterial(power.get("block").toString().split(":")[1].toUpperCase());
                if (block == null) return;

                //i experimented with it, and it seemed that it just set it one block above?
                //still unsure about this one tho
                location.add(0d, 1d, 0d);
                location.getWorld().getBlockAt(location).setType(block);
            }
        }
        if (type.equals("apoli:offset")) {
            BlockActionType(location.add(Double.valueOf(power.getOrDefault("x", "0").toString()), Double.valueOf(power.getOrDefault("y", "0").toString()), Double.valueOf(power.getOrDefault("z", "0").toString())), (JSONObject) power.get("action"));
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
        if (type.equals("apoli:bonemeal")) {
            Block block = location.getWorld().getBlockAt(location);
            block.applyBoneMeal(BlockFace.UP);
        }
        if (type.equals("apoli:explode")) {

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

            location.createExplosion(explosionPower, create_fire);
        }
        if (type.equals("apoli:execute_command")) {
            OriginConsoleSender originConsoleSender = new OriginConsoleSender();
            originConsoleSender.setOp(true);
            final boolean lastSendCMDFeedback = Boolean.parseBoolean(GameRule.SEND_COMMAND_FEEDBACK.toString());
            Bukkit.dispatchCommand(originConsoleSender, "gamerule sendCommandFeedback false");
            final boolean lastlogAdminCMDs = Boolean.parseBoolean(GameRule.LOG_ADMIN_COMMANDS.toString());
            Bukkit.dispatchCommand(originConsoleSender, "gamerule logAdminCommands false");
            String cmd = null;
            if (power.get("command").toString().startsWith("/")) {
                cmd = power.get("command").toString().split("/")[1];
            } else {
                cmd = power.get("command").toString();
            }
            Bukkit.dispatchCommand(originConsoleSender, cmd);
            Bukkit.dispatchCommand(originConsoleSender, "gamerule logAdminCommands {bool}".replace("{bool}", String.valueOf(lastlogAdminCMDs)));
            Bukkit.dispatchCommand(originConsoleSender, "gamerule logAdminCommands {bool}".replace("{bool}", String.valueOf(lastSendCMDFeedback)));
        }
        if (type.equals("apoli:set_block")) {
            location.getBlock().setType(Material.valueOf(power.get("block").toString().split(":")[1].toUpperCase()));
        }
    }

    private static void runItem(ItemStack item, JSONObject power) {
        JSONObject itemAction = (JSONObject) power.get("item_action");
        String type = itemAction.get("type").toString();
        if (type.equals("apoli:damage")) {
            item.setDurability((short) (item.getDurability() + Short.parseShort(itemAction.get("amount").toString())));
        }
        if (type.equals("apoli:consume")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getInventory().contains(item)) {
                    if (item.getType().isEdible()) {
                        item.setAmount(item.getAmount() - 1);
                        player.setSaturation(player.getSaturation() + 2);
                        player.setFoodLevel(player.getFoodLevel() + 3);
                    }
                }
            }
        }
        if (type.equals("apoli:remove_enchantment")) {
            Enchantment enchantment = Enchantment.getByKey(new NamespacedKey(power.get("enchantment").toString().split(":")[0], power.get("enchantment").toString().split(":")[1]));
            if (item.containsEnchantment(enchantment)) {
                item.removeEnchantment(enchantment);
            }
        }
    }

    public static void runbiEntity(Entity actor, Entity target, JSONObject biEntityAction) {
        String type = biEntityAction.get("type").toString();
        if (type.equals("apoli:add_velocity")) {
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
        if (type.equals("apoli:remove_from_set")) {
            RemoveFromSetEvent ev = new RemoveFromSetEvent(target, biEntityAction.get("set").toString());
            ev.callEvent();
        }
        if (type.equals("apoli:add_to_set")) {
            AddToSetEvent ev = new AddToSetEvent(target, biEntityAction.get("set").toString());
            ev.callEvent();
        }
        if (type.equals("apoli:damage")) {
            if (target.isDead() || !(target instanceof LivingEntity)) return;
            float amount = 0.0f;

            if (biEntityAction.containsKey("amount"))
                amount = Float.parseFloat(biEntityAction.get("amount").toString());

            String namespace;
            String key;
            if (biEntityAction.get("damage_type") != null) {
                if (biEntityAction.get("damage_type").toString().contains(":")) {
                    namespace = biEntityAction.get("damage_type").toString().split(":")[0];
                    key = biEntityAction.get("damage_type").toString().split(":")[1];
                } else {
                    namespace = "minecraft";
                    key = biEntityAction.get("damage_type").toString();
                }
            } else {
                namespace = "minecraft";
                key = "generic";
            }
            DamageType dmgType = Utils.DAMAGE_REGISTRY.get(new ResourceLocation(namespace, key));
            net.minecraft.world.entity.LivingEntity serverEn = ((CraftLivingEntity) target).getHandle();
            serverEn.hurt(Utils.getDamageSource(dmgType), amount);
        }
        if (type.equals("apoli:mount")) {
            target.addPassenger(actor);
        }
        if (type.equals("apoli:set_in_love")) {
            if (target instanceof Animals targetAnimal) {
                targetAnimal.setLoveModeTicks(600);
            }
        }
        if (type.equals("apoli:tame")) {
            if (target instanceof Tameable targetTameable && actor instanceof AnimalTamer actorTamer) {
                targetTameable.setOwner(actorTamer);
            }
        }
        if (type.equals("apoli:actor_action")) {
            EntityActionType(actor, (JSONObject) biEntityAction.get("action"));
        }
        if (type.equals("apoli:target_action")) {
            runEntity(target, (JSONObject) biEntityAction.get("action"));
        }
    }

    private static void runEntity(Entity entity, JSONObject power) {
        JSONObject entityAction;
        entityAction = power;
        String type = entityAction.get("type").toString();
        if (entity == null) return;

        if (type.equals("apoli:modify_inventory")) {
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
        if (type.equals("apoli:change_resource")) {
            if (resourceChangeTimeout.containsKey(entity)) return;
            String resource = power.get("resource").toString();
            if (Resource.getResource(entity, resource) == null) return;
            if (Resource.getResource(entity, resource).getRight() == null) return;
            if (Resource.getResource(entity, resource).getLeft() == null) return;
            int change = Integer.parseInt(power.get("change").toString());
            double finalChange = 1.0 / Resource.getResource(entity, resource).getRight();
            BossBar bossBar = Resource.getResource(entity, resource).getLeft();
            double toRemove = finalChange * change;
            double newP = bossBar.getProgress() + toRemove;
            if (newP > 1.0) {
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
            }.runTaskLater(GenesisMC.getPlugin(), 1);
        }
        if (type.equals("apoli:set_on_fire")) {
            entity.setFireTicks(Integer.parseInt(power.get("duration").toString()));
        }
        if (type.equals("apoli:spawn_entity")) {
            OriginConsoleSender originConsoleSender = new OriginConsoleSender();
            originConsoleSender.setOp(true);
            Bukkit.dispatchCommand(originConsoleSender, "summon $1 %1 %2 %3 $2"
                    .replace("$1", power.get("entity_type").toString())
                    .replace("$2", power.getOrDefault("tag", "").toString()
                            .replace("%1", String.valueOf(entity.getLocation().getX()))
                            .replace("%2", String.valueOf(entity.getLocation().getY()))
                            .replace("%3", String.valueOf(entity.getLocation().getZ()))
                    ));
        }
        if (type.equals("apoli:spawn_particles")) {
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
        if (type.equals("apoli:random_teleport")) {
            int spreadDistance = Math.round(Float.valueOf(power.getOrDefault("max_width", "8.0").toString()));
            int attempts = Integer.valueOf(power.getOrDefault("attempts", "1").toString());
            for (int i = 0; i < attempts; i++) {
                String cmd = "spreadplayers {xloc} {zloc} 1 {spreadDist} false {name}"
                        .replace("{xloc}", String.valueOf(entity.getLocation().getX()))
                        .replace("{zloc}", String.valueOf(entity.getLocation().getZ()))
                        .replace("{spreadDist}", String.valueOf(spreadDistance))
                        .replace("{name}", "@e[{data}]"
                                .replace("{data}", "x=" + entity.getLocation().getX() + ",y=" + entity.getLocation().getY() + ",z=" + entity.getLocation().getZ() + ",type=" + entity.getType().toString().toLowerCase() + ",x_rotation=" + entity.getLocation().getDirection().getX() + ",y_rotation=" + entity.getLocation().getDirection().getY())
                        );
                Bukkit.dispatchCommand(new OriginConsoleSender(), cmd);
            }
        }
        if (type.equals("apoli:remove_power")) {
            if (entity instanceof Player p) {
                PowerContainer powerContainer = CraftApoli.getPowerContainerFromTag(power.get("power").toString());
                if (powerContainer != null) {
                    Bukkit.dispatchCommand(new OriginConsoleSender(), "power remove {name} {identifier}".replace("{name}", p.getName()).replace("{identifier}", power.get("power").toString()));
                }
            }
        }
        if (type.equals("apoli:spawn_effect_cloud")) {
            spawnEffectCloud(entity, Float.valueOf(power.getOrDefault("radius", 3.0).toString()), Integer.valueOf(power.getOrDefault("wait_time", 10).toString()), new PotionEffect(StackingStatusEffect.getPotionEffectType(power.get("effect").toString()), 1, 1));
        }
        if (type.equals("apoli:replace_inventory")) {
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
        if (type.equals("apoli:heal")) {
            if (entity instanceof LivingEntity li) {
                double healthFinal = li.getHealth() + Double.parseDouble(power.get("amount").toString());
                if (li.getHealth() >= 20) return;
                if (healthFinal > 20) {
                    li.setHealth(20);
                } else {
                    li.setHealth(healthFinal);
                }
            }
        }
        if (type.equals("apoli:clear_effect")) {
            PotionEffectType potionEffectType = StackingStatusEffect.getPotionEffectType(power.get("effect").toString());
            if (entity instanceof Player player) {
                if (player.hasPotionEffect(potionEffectType)) {
                    player.removePotionEffect(potionEffectType);
                }
            }
        }
        if (type.equals("apoli:exhaust")) {
            if (entity instanceof Player player) {
                player.setFoodLevel(player.getFoodLevel() - Math.round(Float.valueOf(power.get("amount").toString())));
            }
        }
        if (type.equals("apoli:explode")) {
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
        if (type.equals("apoli:crafting_table")) {
            if (entity instanceof Player player) {
                Inventory inventory = Bukkit.createInventory(player, InventoryType.CRAFTING);
                player.openInventory(inventory);
            }
        }
        if (type.equals("apoli:ender_chest")) {
            if (entity instanceof Player player) {
                Inventory inventory = Bukkit.createInventory(player, InventoryType.ENDER_CHEST);
                player.openInventory(inventory);
            }
        }
        if (type.equals("apoli:equipped_item_action")) {
            if (entity instanceof Player player) {
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
        if (type.equals("apoli:dismount")) {
            for (Entity entity1 : entity.getVehicle().getPassengers()) {
                if (entity1 == entity) entity1.getPassengers().remove(entity);
            }
        }
        if (type.equals("apoli:feed")) {
            if (entity instanceof Player player) {
                player.setFoodLevel(player.getFoodLevel() + Integer.parseInt(power.get("food").toString()));
                player.setSaturation(player.getSaturation() + Float.parseFloat(power.get("saturation").toString()));
            }
        }
        if (type.equals("apoli:fire_projectile")) {
            if (entity instanceof ProjectileSource) {
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
        if (type.equals("apoli:passenger_action")) {
            runEntity(entity.getPassenger(), (JSONObject) power.get("action"));
            runbiEntity(entity, entity.getPassenger(), (JSONObject) power.get("action"));
        }
        if (type.equals("apoli:raycast")) {
            Predicate<Entity> filter = entity1 -> !entity1.equals(entity);
            if (power.get("before_action") != null) {
                runEntity(entity, (JSONObject) power.get("before_action"));
            }
            RayTraceResult traceResult = entity.getWorld().rayTrace(entity.getLocation(), entity.getLocation().getDirection(), 12, FluidCollisionMode.valueOf(power.getOrDefault("fluid_handling", "NEVER").toString().toUpperCase()), false, 1, filter);
            if (traceResult != null) {
                if (traceResult.getHitEntity() != null) {
                    Entity entity2 = traceResult.getHitEntity();
                    if (entity2.isDead() || !(entity2 instanceof LivingEntity)) return;
                    if (entity2.isInvulnerable()) return;
                    if (entity2.getPassengers().contains(entity)) return;
                    if (power.get("bientity_action") != null) {
                        runbiEntity(entity, entity2, (JSONObject) power.get("bientity_action"));
                    }
                }
                if (traceResult.getHitBlock() != null) {
                    if (power.get("block_action") != null) {
                        runBlock(traceResult.getHitBlock().getLocation(), (JSONObject) power.get("block_action"));
                    }
                }
                if (power.get("after_action") != null) {
                    runEntity(entity, (JSONObject) power.get("after_action"));
                }
            } else {
                if (power.get("miss_action") != null) {
                    runEntity(entity, (JSONObject) power.get("miss_action"));
                }
            }
        }
        if (type.equals("apoli:extinguish")) {
            entity.setFireTicks(0);
        }
        if (type.equals("apoli:play_sound")) {
            entity.getWorld().playSound(entity, Sound.valueOf(power.get("sound").toString().toUpperCase().split(":")[1].replace(".", "_")), 8, 1);
        }
        if (type.equals("apoli:gain_air")) {
            long amt = (long) power.get("value");
            if (entity instanceof Player p) {
                p.setRemainingAir(p.getRemainingAir() + Math.toIntExact(amt));
            }
        }
        if (type.equals("apoli:drop_inventory")) {
            if (entity instanceof Player player) {
                if (power.containsKey("slot")) {
                    try {
                        if (player.getInventory().getItem(getSlotFromString(power.get("slot").toString())) == null)
                            return;
                        ItemActionType(player.getInventory().getItem(getSlotFromString(power.get("slot").toString())), power);
                    } catch (Exception e) {
                        //fail noononooo
                    }
                } else {
                    ArrayList<String> ke = new ArrayList<>();
                    ke.add("key.origins.primary_active");
                    ke.add("key.origins.secondary_active");
                    for (ItemStack item : player.getInventory().getContents()) {
                        if (item == null) continue;
                        if (!item.containsEnchantment(Enchantment.ARROW_INFINITE) && !item.getType().equals(Material.GRAY_DYE)) {
                            player.getWorld().dropItemNaturally(player.getLocation(), item);
                        }
                    }
                    player.getInventory().clear();
                    addItems(player);
                }
            }
        }
        if (type.equals("apoli:grant_advancement")) {
            OriginConsoleSender originConsoleSender = new OriginConsoleSender();
            originConsoleSender.setOp(true);
            Bukkit.dispatchCommand(originConsoleSender, "advancement grant $1 $2".replace("$1", entity.getName()).replace("$2", power.get("advacnement").toString()));
        }
        if (type.equals("apoli:revoke_advancement")) {
            OriginConsoleSender originConsoleSender = new OriginConsoleSender();
            originConsoleSender.setOp(true);
            Bukkit.dispatchCommand(originConsoleSender, "advancement revoke $1 $2".replace("$1", entity.getName()).replace("$2", power.get("advacnement").toString()));
        }
        if (type.equals("apoli:selector_action")) {
            if (power.get("bientity_condition") != null) {
                if (entity instanceof Player player) {
                    runbiEntity(entity, player.getTargetEntity(AttributeHandler.Reach.getDefaultReach(player), false), (JSONObject) power.get("bientity_condition"));
                }
            }
        }
        if (type.equals("apoli:give")) {
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
        if (type.equals("apoli:damage")) {
            if (entity instanceof Player P) {
                P.damage(Double.valueOf(power.get("amount").toString()));
                P.setLastDamageCause(new EntityDamageEvent(P, EntityDamageEvent.DamageCause.CUSTOM, Double.valueOf(power.get("amount").toString())));
            }
        }
        if (type.equals("genesis:set_spawn")) {
            if (entity instanceof Player p) {
                p.sendMessage("Bed location set.");
                p.setBedSpawnLocation(p.getTargetBlockExact(Math.toIntExact(Math.round(AttributeHandler.Reach.getFinalReach(p)))).getLocation());
            }
        }
        if (type.equals("apoli:add_velocity")) {
            float y = 0f;
            float x = 0f;
            float z = 0f;
            Space space = Utils.getSpaceFromString(entityAction.getOrDefault("space", "world").toString());
            if (entityAction.containsKey("y")) y = Float.parseFloat(entityAction.get("y").toString());
            if (entityAction.containsKey("x")) x = Float.parseFloat(entityAction.get("x").toString());
            if (entityAction.containsKey("z")) z = Float.parseFloat(entityAction.get("z").toString());

            Vector3f vec = new Vector3f(x, y, z);
            net.minecraft.world.entity.Entity en = ((CraftLivingEntity)entity).getHandle();
            space.toGlobal(vec, en);
            if(Boolean.parseBoolean(entityAction.getOrDefault("set", "false").toString())){
                en.getBukkitEntity().getVelocity().add(new Vector(vec.x, vec.y, vec.z));
            }else{
                en.getBukkitEntity().setVelocity(new Vector(vec.x, vec.y, vec.z));
            }
        }
        if (type.equals("apoli:execute_command")) {
            String cmd = null;
            if (power.get("command").toString().startsWith("power") || power.get("command").toString().startsWith("/power"))
                return;
            if (power.get("command").toString().startsWith("/")) {
                cmd = power.get("command").toString().split("/")[1];
            } else {
                cmd = power.get("command").toString();
            }
            final boolean returnToNormal = entity.getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK);
            entity.getWorld().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
            Bukkit.dispatchCommand(new OriginConsoleSender(), "execute as {entity} at {name} run ".replace("{name}", entity.getUniqueId().toString()).replace("{entity}", entity.getUniqueId().toString()) + cmd);
            new BukkitRunnable() {
                @Override
                public void run() {
                    entity.getWorld().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, returnToNormal);
                }
            }.runTaskLater(GenesisMC.getPlugin(), 1);
        }
        if (type.equals("apoli:add_xp")) {
            int points = 0;
            int levels = 0;

            if (entityAction.containsKey("points")) points = Integer.parseInt(entityAction.get("points").toString());
            if (entityAction.containsKey("levels")) levels = Integer.parseInt(entityAction.get("levels").toString());

            if (entity instanceof Player player) {
                player.giveExp(points);
                player.setLevel(player.getLevel() + levels);
            }
        }
        if (type.equals("apoli:apply_effect")) {
            if (entity instanceof LivingEntity player) {
                statusEffectInstance(player, entityAction);
            }
        }
        if (type.equals("apoli:area_of_effect")) {
            float radius = 15f;
            JSONObject bientity_action = new JSONObject();
            boolean include_target = false;

            if (entityAction.containsKey("radius")) radius = Float.parseFloat(entityAction.get("radius").toString());
            if (entityAction.containsKey("bientity_action"))
                bientity_action = (JSONObject) entityAction.get("bientity_action");
            if (entityAction.containsKey("include_target"))
                include_target = Boolean.parseBoolean(entityAction.get("include_target").toString());

            for (Entity nearbyEntity : entity.getNearbyEntities(radius, radius, radius)) {
                boolean run = true;
                if(entityAction.containsKey("bientity_condition")) {
                    Optional<Boolean> bool = ConditionExecutor.biEntityCondition.check((JSONObject) bientity_action.get("bientity_condition"), entity, nearbyEntity, entity.getLocation().getBlock(), null, null, null);
                    if (bool.isPresent()) {
                        if (bool.get()) {
                            run = true;
                        } else {
                            run = false;
                        }
                    } else {
                        run = false;
                    }
                }

                if(run){
                    BiEntityActionType(entity, nearbyEntity, bientity_action);
                }
            }
            if (include_target) BiEntityActionType(entity, entity, bientity_action);
        }
        if (type.equals("apoli:block_action_at")) {
            BlockActionType(entity.getLocation(), (JSONObject) entityAction.get("block_action"));
        }
        if (type.equals("apoli:toggle")) {
            if (entity instanceof Player) {
                for (OriginContainer origin : OriginPlayerUtils.getOrigin((Player) entity).values()) {
                    if (origin.getPowers().contains(power.get("power"))) {
                        for (PowerContainer powerContainer : origin.getPowerContainers()) {
                            if (powerContainer.getType().equals("apoli:toggle")) {
                                Toggle toggle = new Toggle();
                                toggle.execute((Player) entity, powerContainer);
                            }
                        }
                    }
                }
            }
        }
        if (type.equals("apoli:set_fall_distance")) {
            entity.setFallDistance(Float.parseFloat(power.get("fall_distance").toString()));
        }
        if (type.equals("apoli:trigger_cooldown")) {
            if (entity instanceof Player player) {
                for (OriginContainer origin : OriginPlayerUtils.getOrigin((Player) entity).values()) {
                    if (origin.getPowers().contains(power.get("power"))) {
                        for (PowerContainer powerContainer : origin.getPowerContainers()) {
                            if (powerContainer.get("cooldown") != null) {
                                String key = "*";
                                if (powerContainer.get("key").getOrDefault("key", "key.origins.primary_active") != null) {
                                    key = powerContainer.get("key").getOrDefault("key", "key.origins.primary_active").toString();
                                    if (powerContainer.getType().equals("apoli:action_on_hit")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("apoli:action_when_damage_taken")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("apoli:action_when_hit")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("apoli:action_self")) {
                                        key = "key.use";
                                    } else if (powerContainer.getType().equals("apoli:attacker_action_when_hit")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("apoli:self_action_on_hit")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("apoli:self_action_on_kill")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("apoli:self_action_when_hit")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("apoli:target_action_on_hit")) {
                                        key = "key.attack";
                                    }
                                }
                                CooldownManager.addCooldown(player, Utils.getNameOrTag(powerContainer), powerContainer.getType(), powerContainer.getInt("cooldown"), key);
                            }
                        }
                    }
                }
            }
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
}
