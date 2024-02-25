package me.dueris.genesismc.factory.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.AddToSetEvent;
import me.dueris.genesismc.event.RemoveFromSetEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.apoli.AttributeHandler;
import me.dueris.genesismc.factory.powers.apoli.Resource;
import me.dueris.genesismc.factory.powers.apoli.StackingStatusEffect;
import me.dueris.genesismc.factory.powers.apoli.Toggle;
import me.dueris.genesismc.registry.Registrar;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.apoli.RaycastUtils;
import me.dueris.genesismc.util.apoli.Space;
import me.dueris.genesismc.util.console.OriginConsoleSender;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.joml.Vector3f;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

import static me.dueris.genesismc.util.KeybindingUtils.addItems;

public class Actions {

    public static HashMap<Entity, Boolean> resourceChangeTimeout = new HashMap<>();

    public static void BiEntityActionType(Entity actor, Entity target, JSONObject action) {
        if(!action.containsKey("type")) return;
        String type = action.get("type").toString();

        if (type.equals("apoli:invert")) {
            BiEntityActionType(target, actor, (JSONObject) action.get("action"));
        } else if (type.equals("apoli:and")) {
            JSONArray andActions = (JSONArray) action.get("actions");
            for (Object actionObj : andActions) {
                JSONObject actionn = (JSONObject) actionObj;
                BiEntityActionType(actor, target, actionn);
            }
        } else if (type.equals("apoli:chance")) {
            double chance = Double.parseDouble(action.get("chance").toString());
            double randomValue = new Random().nextDouble(1);

            if (randomValue <= chance) {
                JSONObject actionn = (JSONObject) action.get("action");
                BiEntityActionType(actor, target, actionn);
            }
        } else if (type.equals("apoli:choice")) {
            JSONArray actionsArray = (JSONArray) action.get("actions");
            List<JSONObject> actionsList = new ArrayList<>();

            for (Object actionObj : actionsArray) {
                JSONObject actionn = (JSONObject) actionObj;
                JSONObject element = (JSONObject) actionn.get("element");
                int weight = Integer.parseInt(actionn.get("weight").toString());
                for (int i = 0; i < weight; i++) {
                    actionsList.add(element);
                }
            }

            if (!actionsList.isEmpty()) {
                int randomIndex = (int) (Math.random() * actionsList.size());
                JSONObject chosenAction = actionsList.get(randomIndex);
                BiEntityActionType(actor, target, chosenAction);
            }
        } else if (type.equals("apoli:delay")) {
            int ticks = Integer.parseInt(action.get("ticks").toString());
            JSONObject delayedAction = (JSONObject) action.get("action");

            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                BiEntityActionType(actor, target, delayedAction);
            }, ticks);
        } else if (type.equals("apoli:nothing")) {
            // Literally does nothing
        } else if (type.equals("apoli:side")) {
            if(action.get("side").toString().toLowerCase().equals("server")){
                JSONObject actionn = (JSONObject) action.get("action");
                BiEntityActionType(actor, target, actionn);
            }
        } else if (type.equals("apoli:if_else")) {
            boolean bool = ConditionExecutor.testBiEntity((JSONObject) action.get("condition"), (CraftEntity) actor, (CraftEntity) target);
            if (bool) {
                BiEntityActionType(actor, target, (JSONObject) action.get("if_action"));
            } else {
                BiEntityActionType(actor, target, (JSONObject) action.get("else_action"));
            }
        } else {
            runbiEntity(actor, target, action);
        }
    }

    public static void ItemActionType(ItemStack item, JSONObject power) {
        if (power == null || power.isEmpty()) return;
        String type = power.get("type").toString();

        if (type.equals("apoli:and")) {
            JSONArray andActions = (JSONArray) power.get("actions");
            for (Object actionObj : andActions) {
                JSONObject action = (JSONObject) actionObj;
                runItem(item, action);
            }
        } else if (type.equals("apoli:chance")) {
            double chance = Double.parseDouble(power.get("chance").toString());
            double randomValue = new Random().nextDouble(1);

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
            Optional<Boolean> bool = Optional.of(ConditionExecutor.testItem((JSONObject) power.get("item_condition"), item));
            if (bool.isPresent()) {
                if (bool.get()) {
                    ItemActionType(item, (JSONObject) power.get("if_action"));
                } else {
                    ItemActionType(item, (JSONObject) power.get("else_action"));
                }
            } else {
                ItemActionType(item, (JSONObject) power.get("else_action"));
            }
        } else if (type.equals("apoli:side")) {
            if(power.get("side").toString().toLowerCase().equals("server")){
                JSONObject action = (JSONObject) power.get("action");
                runItem(item, action);
            }
        } else {
            runItem(item, power);
        }
    }

    public static void EntityActionType(Entity entity, JSONObject action) {
        if (action == null || action.isEmpty()) return;
        String type = action.get("type").toString();

        if (type.equals("apoli:and")) {
            JSONArray andActions = (JSONArray) action.get("actions");
            for (Object actionObj : andActions) {
                JSONObject actionn = (JSONObject) actionObj;
                EntityActionType(entity, actionn);
            }
        } else if (type.equals("apoli:chance")) {
            double chance = Double.parseDouble(action.get("chance").toString());
            double randomValue = new Random().nextDouble(1);

            if (randomValue <= chance) {
                JSONObject actionn = (JSONObject) action.get("action");
                EntityActionType(entity, actionn);
            } else if (action.containsKey("fail_action")) {
                JSONObject failAction = (JSONObject) action.get("fail_action");
                EntityActionType(entity, failAction);
            }
        } else if (type.equals("apoli:choice")) {
            JSONArray actionsArray = (JSONArray) action.get("actions");
            List<JSONObject> actionsList = new ArrayList<>();

            for (Object actionObj : actionsArray) {
                JSONObject actionn = (JSONObject) actionObj;
                JSONObject element = (JSONObject) actionn.get("element");
                int weight = Integer.parseInt(actionn.get("weight").toString());
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
            int ticks = Integer.parseInt(action.get("ticks").toString());
            JSONObject delayedAction = (JSONObject) action.get("action");

            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                EntityActionType(entity, delayedAction);
            }, ticks);
        } else if (type.equals("apoli:nothing")) {
            //literally does nothin
        } else if (type.equals("apoli:if_else")) {
            boolean bool = ConditionExecutor.testEntity((JSONObject) action.get("condition"), (CraftEntity) entity);
            if (bool) {
                EntityActionType(entity, (JSONObject) action.get("if_action"));
            } else {
                EntityActionType(entity, (JSONObject) action.get("else_action"));
            }
        } else if (type.equals("apoli:side")) {
            if(action.get("side").toString().toLowerCase().equals("server")){
                EntityActionType(entity, (JSONObject) action.get("action"));
            }
        } else {
            runEntity(entity, action);
        }
    }

    public static void BlockActionType(Location location, JSONObject action) {
        if (action == null || action.isEmpty() || !action.containsKey("type")) return;
        String type = action.get("type").toString();

        if (type.equals("apoli:and")) {
            JSONArray andActions = (JSONArray) action.get("actions");
            for (Object actionObj : andActions) {
                JSONObject actionn = (JSONObject) actionObj;
                runBlock(location, actionn);
            }
        } else if (type.equals("apoli:chance")) {
            double chance = Double.parseDouble(action.get("chance").toString());
            double randomValue = new Random().nextDouble(1);

            if (randomValue <= chance) {
                JSONObject actionn = (JSONObject) action.get("action");
                runBlock(location, actionn);
            } else if (action.containsKey("fail_action")) {
                JSONObject failAction = (JSONObject) action.get("fail_action");
                runBlock(location, failAction);
            }
        } else if (type.equals("apoli:choice")) {
            JSONArray actionsArray = (JSONArray) action.get("actions");
            List<JSONObject> actionsList = new ArrayList<>();

            for (Object actionObj : actionsArray) {
                JSONObject actionn = (JSONObject) actionObj;
                JSONObject element = (JSONObject) actionn.get("element");
                int weight = Integer.parseInt(actionn.get("weight").toString());
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
            int ticks = Integer.parseInt(action.get("ticks").toString());
            JSONObject delayedAction = (JSONObject) action.get("action");

            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                runBlock(location, delayedAction);
            }, ticks);
        } else if (type.equals("apoli:nothing")) {
            // Literally does nothing
        } else if (type.equals("apoli:if_else")) {
            Optional<Boolean> bool = Optional.of(ConditionExecutor.testBlock((JSONObject) action.get("block_condition"), (CraftBlock) location.getBlock()));
            if (bool.isPresent()) {
                if (bool.get()) {
                    BlockActionType(location, (JSONObject) action.get("if_action"));
                } else {
                    BlockActionType(location, (JSONObject) action.get("else_action"));
                }
            } else {
                BlockActionType(location, (JSONObject) action.get("else_action"));
            }
        } else if (type.equals("apoli:side")) {
            if(action.get("side").toString().toLowerCase().equals("server")){
                JSONObject actionn = (JSONObject) action.get("action");
                runBlock(location, actionn);
            }
        } else {
            runBlock(location, action);
        }
    }

    private static void runBlock(Location location, JSONObject action) {
        if(action == null || action.isEmpty()) return;
        String type = action.get("type").toString();

        if (type.equals("apoli:add_block")) {
            if (action.containsKey("block")) {
                Material block;
                block = Material.getMaterial(action.get("block").toString().split(":")[1].toUpperCase());
                if (block == null) return;

                //i experimented with it, and it seemed that it just set it one block above?
                //still unsure about this one tho
                location.add(0d, 1d, 0d);
                location.getWorld().getBlockAt(location).setType(block);
            }
        }
        if (type.equals("apoli:offset")) {
            BlockActionType(location.add(Double.valueOf(action.getOrDefault("x", "0").toString()), Double.valueOf(action.getOrDefault("y", "0").toString()), Double.valueOf(action.getOrDefault("z", "0").toString())), (JSONObject) action.get("action"));
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

            if (action.containsKey("action"))
                explosionPower = Float.parseFloat(action.get("action").toString());
            if (action.containsKey("destruction_type"))
                destruction_type = action.get("destruction_type").toString();
            if (action.containsKey("indestructible"))
                indestructible = (JSONObject) action.get("indestructible");
            if (action.containsKey("destructible")) destructible = (JSONObject) action.get("destructible");
            if (action.containsKey("create_fire"))
                create_fire = Boolean.parseBoolean(action.get("create_fire").toString());

            location.createExplosion(explosionPower, create_fire);
        }
        if (type.equals("apoli:set_block")) {
            location.getBlock().setType(Material.valueOf(action.get("block").toString().split(":")[1].toUpperCase()));
        }
    }

    private static void runItem(ItemStack item, JSONObject action) {
        if(action == null || action.isEmpty()) return;
        String type = action.get("type").toString();
        if (type.equals("apoli:damage")) {
            item.setDurability((short) (item.getDurability() + Short.parseShort(action.get("amount").toString())));
        }
        if (type.equals("apoli:consume")) {
            item.setAmount(item.getAmount() - 1);
        }
        if (type.equals("apoli:remove_enchantment")) {
            Enchantment enchantment = Enchantment.getByKey(new NamespacedKey(action.get("enchantment").toString().split(":")[0], action.get("enchantment").toString().split(":")[1]));
            if (item.containsEnchantment(enchantment)) {
                item.removeEnchantment(enchantment);
            }
        }
    }

    public static void runbiEntity(Entity actor, Entity target, JSONObject action) {
        if(action == null || action.isEmpty()) return;
        String type = action.get("type").toString();
        if (type.equals("apoli:add_velocity")) {
            float x = 0.0f;
            float y = 0.0f;
            float z = 0.0f;
            boolean set = false;

            if (action.containsKey("x")) x = Float.parseFloat(action.get("x").toString());
            if (action.containsKey("y")) y = Float.parseFloat(action.get("y").toString());
            if (action.containsKey("z")) z = Float.parseFloat(action.get("z").toString());
            if (action.containsKey("set")) set = Boolean.parseBoolean(action.get("set").toString());

            if (set) target.setVelocity(new Vector(x, y, z));
            else target.setVelocity(target.getVelocity().add(new Vector(x, y, z)));
        }
        if (type.equals("apoli:remove_from_set")) {
            RemoveFromSetEvent ev = new RemoveFromSetEvent(target, action.get("set").toString());
            ev.callEvent();
        }
        if (type.equals("apoli:add_to_set")) {
            AddToSetEvent ev = new AddToSetEvent(target, action.get("set").toString());
            ev.callEvent();
        }
        if (type.equals("apoli:damage")) {
            if (target.isDead() || !(target instanceof LivingEntity)) return;
            float amount = 0.0f;

            if (action.containsKey("amount"))
                amount = Float.parseFloat(action.get("amount").toString());

            String namespace;
            String key;
            if (action.get("damage_type") != null) {
                if (action.get("damage_type").toString().contains(":")) {
                    namespace = action.get("damage_type").toString().split(":")[0];
                    key = action.get("damage_type").toString().split(":")[1];
                } else {
                    namespace = "minecraft";
                    key = action.get("damage_type").toString();
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
            EntityActionType(actor, (JSONObject) action.get("action"));
        }
        if (type.equals("apoli:target_action")) {
            EntityActionType(target, (JSONObject) action.get("action"));
        }
    }

    private static void runEntity(Entity entity, JSONObject action) {
        if(action == null || action.isEmpty()) return;
        String type = action.get("type").toString();
        if (entity == null) return;

        if (type.equals("apoli:modify_inventory")) {
            if (entity instanceof Player player) {
                if (action.containsKey("slot")) {
                    try {
                        if (player.getInventory().getItem(getSlotFromString(action.get("slot").toString())) == null)
                            return;
                        ItemActionType(player.getInventory().getItem(getSlotFromString(action.get("slot").toString())), action);
                    } catch (Exception e) {
                        //silently fail bc idk whats going on and yeah it wokrs lol
                    }
                }
            }
        }
        if (type.equals("apoli:change_resource")) {
            if (resourceChangeTimeout.containsKey(entity)) return;
            String resource = action.get("resource").toString();
            if (Resource.getResource(entity, resource) == null) return;
            if (Resource.getResource(entity, resource).right() == null) return;
            if (Resource.getResource(entity, resource).left() == null) return;
            int change = Integer.parseInt(action.get("change").toString());
            double finalChange = 1.0 / Resource.getResource(entity, resource).right();
            BossBar bossBar = Resource.getResource(entity, resource).left();
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
            entity.setFireTicks(Integer.parseInt(action.get("duration").toString()));
        }
        if (type.equals("apoli:spawn_entity")) {
            OriginConsoleSender originConsoleSender = new OriginConsoleSender();
            originConsoleSender.setOp(true);
            RaycastUtils.executeCommandAtHit(((CraftEntity)entity).getHandle(), CraftLocation.toVec3D(entity.getLocation()), "summon $1 %1 %2 %3 $2"
                    .replace("$1", action.get("entity_type").toString())
                    .replace("$2", action.getOrDefault("tag", "{}").toString())
                    .replace("%1", String.valueOf(entity.getLocation().getX()))
                    .replace("%2", String.valueOf(entity.getLocation().getY()))
                    .replace("%3", String.valueOf(entity.getLocation().getZ()))
            );
        }
        if (type.equals("apoli:spawn_particles")) {
            Particle particle = Particle.valueOf(((JSONObject)action.get("particle")).getOrDefault("type", null).toString().split(":")[1].toUpperCase());
            int count = Integer.parseInt(String.valueOf(action.getOrDefault("count", 1)));
            float offset_y_no_vector = Float.parseFloat(String.valueOf(action.getOrDefault("offset_y", 1.0)));
            float offset_x = 0.25f;
            float offset_y = 0.50f;
            float offset_z = 0.25f;
            if(action.get("spread") != null){
                JSONObject spread = (JSONObject) action.get("spread");
                if (spread.get("y") != null) {
                    offset_y = Float.parseFloat(String.valueOf(spread.get("y")));
                }

                if (spread.get("x") != null) {
                    offset_x = Float.parseFloat(String.valueOf(spread.get("x")));
                }

                if (spread.get("z") != null) {
                    offset_z = Float.parseFloat(String.valueOf(spread.get("z")));
                }
            }
            entity.getWorld().spawnParticle(particle, new Location(entity.getWorld(), entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ()), count, offset_x, offset_y, offset_z, 0);
        }
        if (type.equals("apoli:random_teleport")) {
            int spreadDistance = Math.round(Float.valueOf(action.getOrDefault("max_width", "8.0").toString()));
            int attempts = Integer.valueOf(action.getOrDefault("attempts", "1").toString());
            for (int i = 0; i < attempts; i++) {
                String cmd = "spreadplayers {xloc} {zloc} 1 {spreadDist} false {name}"
                        .replace("{xloc}", String.valueOf(entity.getLocation().getX()))
                        .replace("{zloc}", String.valueOf(entity.getLocation().getZ()))
                        .replace("{spreadDist}", String.valueOf(spreadDistance))
                        .replace("{name}", "@e[{data}]"
                                .replace("{data}", "x=" + entity.getLocation().getX() + ",y=" + entity.getLocation().getY() + ",z=" + entity.getLocation().getZ() + ",type=" + entity.getType().toString().toLowerCase() + ",x_rotation=" + entity.getLocation().getDirection().getX() + ",y_rotation=" + entity.getLocation().getDirection().getY())
                        );
                RaycastUtils.executeCommandAtHit(((CraftEntity)entity).getHandle(), CraftLocation.toVec3D(entity.getLocation()), cmd);
            }
        }
        if (type.equals("apoli:remove_power")) {
            if (entity instanceof Player p) {
                Power powerContainer = ((Registrar<Power>)GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(NamespacedKey.fromString(action.get("power").toString()));
                if (powerContainer != null) {
                    RaycastUtils.executeCommandAtHit(((CraftEntity)p).getHandle(), CraftLocation.toVec3D(p.getLocation()), "power remove {name} {identifier}".replace("{name}", p.getName()).replace("{identifier}", action.get("action").toString()));
                }
            }
        }
        if (type.equals("apoli:spawn_effect_cloud")) {
            spawnEffectCloud(entity, Float.valueOf(action.getOrDefault("radius", 3.0).toString()), Integer.valueOf(action.getOrDefault("wait_time", 10).toString()), new PotionEffect(StackingStatusEffect.getPotionEffectType(action.get("effect").toString()), 1, 1));
        }
        if (type.equals("apoli:replace_inventory")) {
            if (entity instanceof Player player) {
                if (action.containsKey("slot")) {
                    try {
                        if (player.getInventory().getItem(getSlotFromString(action.get("slot").toString())) == null)
                            return;
                        JSONObject jsonObject = (JSONObject) action.get("stack");
                        player.getInventory().getItem(getSlotFromString(action.get("slot").toString())).setType(Material.valueOf(jsonObject.get("item").toString().split(":")[1].toUpperCase()));
                    } catch (Exception e) {
                        //silently fail
                    }
                }
            }
        }
        if (type.equals("apoli:heal")) {
            if (entity instanceof LivingEntity li) {
                double healthFinal = li.getHealth() + Double.parseDouble(action.get("amount").toString());
                if (li.getHealth() >= 20) return;
                if (healthFinal > 20) {
                    li.setHealth(20);
                } else {
                    li.setHealth(healthFinal);
                }
            }
        }
        if (type.equals("apoli:clear_effect")) {
            PotionEffectType potionEffectType = StackingStatusEffect.getPotionEffectType(action.get("effect").toString());
            if (entity instanceof Player player) {
                if (player.hasPotionEffect(potionEffectType)) {
                    player.removePotionEffect(potionEffectType);
                }
            }
        }
        if (type.equals("apoli:exhaust")) {
            if (entity instanceof Player player) {
                player.setFoodLevel(player.getFoodLevel() - Math.round(Float.valueOf(action.get("amount").toString())));
            }
        }
        if (type.equals("apoli:explode")) {
            float explosionPower = 1f;
            String destruction_type = "break";
            JSONObject indestructible = new JSONObject();
            JSONObject destructible = new JSONObject();
            boolean create_fire = false;

            if (action.containsKey("action"))
                explosionPower = Float.parseFloat(action.get("action").toString());
            if (action.containsKey("destruction_type"))
                destruction_type = action.get("destruction_type").toString();
            if (action.containsKey("indestructible"))
                indestructible = (JSONObject) action.get("indestructible");
            if (action.containsKey("destructible")) destructible = (JSONObject) action.get("destructible");
            if (action.containsKey("create_fire"))
                create_fire = Boolean.parseBoolean(action.get("create_fire").toString());

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
                if (action.containsKey("equipment_slot")) {
                    try {
                        if (player.getInventory().getItem(getSlotFromString(action.get("equipment_slot").toString())) == null)
                            return;
                        ItemActionType(player.getInventory().getItem(getSlotFromString(action.get("equipment_slot").toString())), action);
                    } catch (Exception e) {
                        //silently fail
                    }
                }
            }
        }
        if (type.equals("apoli:dismount")) {
            entity.getVehicle().removePassenger(entity);
        }
        if (type.equals("apoli:feed")) {
            if (entity instanceof Player player) {
                player.setFoodLevel(player.getFoodLevel() + Integer.parseInt(action.get("food").toString()));
                player.setSaturation(player.getSaturation() + Float.parseFloat(action.get("saturation").toString()));
            }
        }
        if (type.equals("apoli:fire_projectile")) {
            if (entity instanceof ProjectileSource) {
                float finalDivergence1 = Float.parseFloat(action.getOrDefault("divergence", 1.0).toString());
                float speed = Float.parseFloat(action.getOrDefault("speed", 1).toString());
                EntityType typeE;
                if (action.getOrDefault("entity_type", null).toString().equalsIgnoreCase("origins:enderian_pearl")) {
                    typeE = EntityType.ENDER_PEARL;
                } else {
                    typeE = EntityType.valueOf(action.getOrDefault("entity_type", null).toString().split(":")[1].toUpperCase());
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
            if(entity.getPassengers() == null || entity.getPassengers().isEmpty()) return;
            EntityActionType(entity.getPassenger(), (JSONObject) action.get("action"));
            BiEntityActionType(entity.getPassenger(), entity, (JSONObject) action.get("bientity_action"));
        }
        if (type.equals("apoli:riding_action")) {
            if(entity.getVehicle() == null) return;
            if(action.containsKey("action")) {
                EntityActionType(entity.getVehicle(), (JSONObject) action.get("action"));
            }
            if(action.containsKey("bientity_action")) {
                BiEntityActionType(entity.getVehicle(), entity, (JSONObject) action.get("bientity_action"));
            }
        }
        if (type.equals("apoli:raycast")) {
            RaycastUtils.action(action, ((CraftEntity)entity).getHandle());
        }
        if (type.equals("apoli:extinguish")) {
            entity.setFireTicks(0);
        }
        if (type.equals("apoli:play_sound")) {
            entity.getWorld().playSound(entity, Sound.valueOf(action.get("sound").toString().toUpperCase().split(":")[1].replace(".", "_")), 8, 1);
        }
        if (type.equals("apoli:gain_air")) {
            long amt = (long) action.get("value");
            if (entity instanceof Player p) {
                p.setRemainingAir(p.getRemainingAir() + Math.toIntExact(amt));
            }
        }
        if (type.equals("apoli:drop_inventory")) {
            if (entity instanceof Player player) {
                if (action.containsKey("slot")) {
                    try {
                        if (player.getInventory().getItem(getSlotFromString(action.get("slot").toString())) == null)
                            return;
                        ItemActionType(player.getInventory().getItem(getSlotFromString(action.get("slot").toString())), action);
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
            RaycastUtils.executeCommandAtHit(((CraftEntity)entity).getHandle(), CraftLocation.toVec3D(entity.getLocation()), "advancement grant $1 $2".replace("$1", entity.getName()).replace("$2", action.get("advacnement").toString()));
        }
        if (type.equals("apoli:revoke_advancement")) {
            RaycastUtils.executeCommandAtHit(((CraftEntity)entity).getHandle(), CraftLocation.toVec3D(entity.getLocation()), "advancement revoke $1 $2".replace("$1", entity.getName()).replace("$2", action.get("advacnement").toString()));
        }
        if (type.equals("apoli:selector_action")) {
            if (action.get("bientity_condition") != null) {
                if (entity instanceof Player player) {
                    runbiEntity(entity, player.getTargetEntity(AttributeHandler.Reach.getDefaultReach(player), false), (JSONObject) action.get("bientity_condition"));
                }
            }
        }
        if (type.equals("apoli:give")) {
            int amt = 1;
            if (action.containsKey("amount")) {
                amt = Integer.parseInt(action.get("amount").toString());
            }

            if (action.containsKey("stack")) {
                JSONObject stackObject = (JSONObject) action.get("stack");
                String item = stackObject.get("item").toString();
                int amount = Integer.parseInt(String.valueOf(stackObject.getOrDefault("amount", 1)));

                ItemStack itemStack = new ItemStack(Material.valueOf(item.toUpperCase().split(":")[1]), amount);

                if (action.containsKey("item_action")) {
                    ItemActionType(itemStack, action);
                }
                if (entity instanceof Player player) {
                    player.getInventory().addItem(itemStack);
                }
            }

        }
        if (type.equals("apoli:damage")) {
            if (entity instanceof Player P) {
                P.damage(Double.valueOf(action.get("amount").toString()));
            }
        }
        if (type.equals("apoli:add_velocity")) {
            float y = 0f;
            float x = 0f;
            float z = 0f;
            Space space = Utils.getSpaceFromString(action.getOrDefault("space", "world").toString());
            if (action.containsKey("y")) y = Float.parseFloat(action.get("y").toString());
            if (action.containsKey("x")) x = Float.parseFloat(action.get("x").toString());
            if (action.containsKey("z")) z = Float.parseFloat(action.get("z").toString());

            Vector3f vec = new Vector3f(x, y, z);
            net.minecraft.world.entity.Entity en = ((CraftLivingEntity) entity).getHandle();
            space.toGlobal(vec, en);
            if (Boolean.parseBoolean(action.getOrDefault("set", "false").toString())) {
                en.getBukkitEntity().getVelocity().add(new Vector(vec.x, vec.y, vec.z));
            } else {
                en.getBukkitEntity().setVelocity(new Vector(vec.x, vec.y, vec.z));
            }
        }
        if (type.equals("apoli:execute_command")) {
            String cmd = null;
            if (action.get("command").toString().startsWith("action") || action.get("command").toString().startsWith("/action"))
                return;
            if (action.get("command").toString().startsWith("/")) {
                cmd = action.get("command").toString().split("/")[1];
            } else {
                cmd = action.get("command").toString();
            }
            RaycastUtils.executeCommandAtHit(((CraftEntity)entity).getHandle(), CraftLocation.toVec3D(entity.getLocation()), cmd);
        }
        if (type.equals("apoli:add_xp")) {
            int points = 0;
            int levels = 0;

            if (action.containsKey("points")) points = Integer.parseInt(action.get("points").toString());
            if (action.containsKey("levels")) levels = Integer.parseInt(action.get("levels").toString());

            if (entity instanceof Player player) {
                player.giveExp(points);
                player.setLevel(player.getLevel() + levels);
            }
        }
        if (type.equals("apoli:apply_effect")) {
            if (entity instanceof LivingEntity le) {
                Utils.statusEffectInstance(le, action);
            }
        }
        if (type.equals("apoli:area_of_effect")) {
            float radius = 15f;
            JSONObject bientity_action = new JSONObject();
            boolean include_target = false;

            if (action.containsKey("radius")) radius = Float.parseFloat(action.get("radius").toString());
            if (action.containsKey("bientity_action"))
                bientity_action = (JSONObject) action.get("bientity_action");
            if (action.containsKey("include_target"))
                include_target = Boolean.parseBoolean(action.get("include_target").toString());

            for (Entity nearbyEntity : entity.getNearbyEntities(radius, radius, radius)) {
                boolean run = ConditionExecutor.testBiEntity((JSONObject) action.get("bientity_condition"), (CraftEntity) entity, (CraftEntity) nearbyEntity);
                if (run) {
                    BiEntityActionType(entity, nearbyEntity, bientity_action);
                }
            }
            if (include_target) BiEntityActionType(entity, entity, bientity_action);
        }
        if (type.equals("apoli:block_action_at")) {
            BlockActionType(entity.getLocation(), (JSONObject) action.get("block_action"));
        }
        if (type.equals("apoli:toggle")) {
            if (entity instanceof Player) {
                for (Origin origin : OriginPlayerAccessor.getOrigin((Player) entity).values()) {
                    if (origin.getPowers().contains(action.get("action"))) {
                        for (Power powerContainer : origin.getPowerContainers()) {
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
            entity.setFallDistance(Float.parseFloat(action.get("fall_distance").toString()));
        }
        if (type.equals("apoli:trigger_cooldown")) {
            if (entity instanceof Player player) {
                for (Origin origin : OriginPlayerAccessor.getOrigin((Player) entity).values()) {
                    if (origin.getPowers().contains(action.get("action"))) {
                        for (Power powerContainer : origin.getPowerContainers()) {
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
                                CooldownUtils.addCooldown(player, Utils.getNameOrTag(powerContainer), powerContainer.getType(), powerContainer.getInt("cooldown"), key);
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
