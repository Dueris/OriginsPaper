package me.dueris.genesismc.factory.powers.OriginsMod.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static me.dueris.genesismc.factory.powers.OriginsMod.OriginMethods.statusEffectInstance;

public class ActionTypes {

    public static void runbiEntity(Entity actor, Entity target, JSONObject biEntityAction){
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
    }

    public static void biEntityActionType(Entity actor, Entity target, JSONObject biEntityAction) {
        JSONObject entityAction = (JSONObject) biEntityAction.get("action");
        if (entityAction == null) {
            entityAction = (JSONObject) biEntityAction.get("bientity_action");
        }
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

    private static void runEntity(Entity entity, JSONObject power){
        JSONObject entityAction;
        System.out.println(power);
        entityAction = power;
        String type = entityAction.get("type").toString();

        if (type.equals("origins:add_velocity")) {
            float x = 0.0f;
            float y = 0.0f;
            float z = 0.0f;
            boolean set = false;

            if (entityAction.containsKey("x")) x = Float.parseFloat(entityAction.get("x").toString());
            if (entityAction.containsKey("y")) y = Float.parseFloat(entityAction.get("y").toString());
            if (entityAction.containsKey("z")) z = Float.parseFloat(entityAction.get("z").toString());
            if (entityAction.containsKey("set")) set = Boolean.parseBoolean(entityAction.get("set").toString());

            if (set) entity.setVelocity(new Vector(x, y, z));
            else entity.setVelocity(entity.getVelocity().add(new Vector(x, y, z)));
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
            if (entity instanceof Player player) {
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
    }

    public static void EntityActionType(Entity entity, JSONObject power) {
        JSONObject entityAction;
        entityAction = (JSONObject) power.get("action");
        if (entityAction == null) entityAction = (JSONObject) power.get("entity_action");
        String type = entityAction.get("type").toString();

        if (type.equals("origins:and")) {
            JSONArray andActions = (JSONArray) entityAction.get("actions");
            for (Object actionObj : andActions) {
                JSONObject action = (JSONObject) actionObj;
                runEntity(entity, action);
            }
        } else if (type.equals("origins:chance")) {
            double chance = Double.parseDouble(entityAction.get("chance").toString());
            double randomValue = Math.random();

            if (randomValue <= chance) {
                JSONObject action = (JSONObject) entityAction.get("action");
                runEntity(entity, action);
            } else if (entityAction.containsKey("fail_action")) {
                JSONObject failAction = (JSONObject) entityAction.get("fail_action");
                runEntity(entity, failAction);
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
                runEntity(entity, chosenAction);
            }
        } else if (type.equals("origins:delay")) {
            int ticks = Integer.parseInt(entityAction.get("ticks").toString());
            JSONObject delayedAction = (JSONObject) entityAction.get("action");

            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                runEntity(entity, delayedAction);
            }, ticks);
        } else if (type.equals("origins:nothing")) {
            //literally does nothin
        } else if (type.equals("origins:side")) {
            JSONObject action = (JSONObject) entityAction.get("action");
            runEntity(entity, action);
        } else {
            runEntity(entity, power);
        }
    }


    public static void BlockActionType(Location location, JSONObject power) {
        JSONObject entityAction = (JSONObject) power.get("action");
        if (entityAction == null) {
            entityAction = (JSONObject) power.get("block_action");
        }
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

    private static void runBlock(Location location, JSONObject power){
        JSONObject blockAction = (JSONObject) power.get("block_action");
        String type = blockAction.get("type").toString();

        if (type.equals("origins:add_block")) {
            if (blockAction.containsKey("block")) {
                Material block;
                System.out.println(blockAction.get("block").toString().split(":")[1].toUpperCase());
                block = Material.getMaterial(blockAction.get("block").toString().split(":")[1].toUpperCase());
                if (block == null) return;

                //i experimented with it, and it seemed that it just set it one block above?
                //still unsure about this one tho
                location.add(0d, 1d, 0d);
                location.getWorld().getBlockAt(location).setType(block);
            }
        }
        if (type.equals("origins:bonemeal")) {
            Block block = location.getWorld().getBlockAt(location);
            block.applyBoneMeal(BlockFace.SELF);
        }
        if (type.equals("origins:explode")) {

            //TODO make custom explosion code for block conditions
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
    }

    public static void ItemActionType(ItemStack item, JSONObject power) {
        JSONObject entityAction = (JSONObject) power.get("action");
        if (entityAction == null) {
            entityAction = (JSONObject) power.get("item_action");
        }
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

    private static void runItem(ItemStack item, JSONObject power){

    }

}
