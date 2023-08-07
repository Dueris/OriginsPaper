package me.dueris.genesismc.core.factory.powers.OriginsMod.actions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;

import java.util.HashMap;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.OriginMethods.statusEffectInstance;

public class ActionTypes {

    public static void biEntityActionType(Entity actor, Entity target, JSONObject biEntityAction) {
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

            if (biEntityAction.containsKey("amount")) amount = Float.parseFloat(biEntityAction.get("amount").toString());
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

    public static void EntityActionType(Entity entity, JSONObject power) {
        JSONObject entityAction;
        System.out.println(power);
        entityAction = (JSONObject) power.get("action");
        if (entityAction == null) entityAction = (JSONObject) power.get("entity_action");
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
                player.setLevel(player.getLevel()+levels);
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
            if (entityAction.containsKey("bientity_action")) bientity_action = (JSONObject) entityAction.get("bientity_action");
            if (entityAction.containsKey("bientity_condition")) bientity_condition = (JSONObject) entityAction.get("bientity_condition");
            if (entityAction.containsKey("include_target")) include_target = Boolean.parseBoolean(entityAction.get("include_target").toString());

            for (Entity nearbyEntity : entity.getNearbyEntities(radius, radius, radius)) {
                biEntityActionType(entity, nearbyEntity, bientity_action);
            }
            if (include_target) biEntityActionType(entity, entity, bientity_action);
        }
        if (type.equals("origins:block_action_at")) {
            BlockActionType(entity.getLocation(), entityAction);
        }
    }


    public static void BlockActionType(Location location, JSONObject power) {
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

             if (blockAction.containsKey("power")) explosionPower = Float.parseFloat(blockAction.get("power").toString());
             if (blockAction.containsKey("destruction_type")) destruction_type = blockAction.get("destruction_type").toString();
             if (blockAction.containsKey("indestructible")) indestructible = (JSONObject) blockAction.get("indestructible");
             if (blockAction.containsKey("destructible")) destructible = (JSONObject) blockAction.get("destructible");
             if (blockAction.containsKey("create_fire")) create_fire = Boolean.parseBoolean(blockAction.get("create_fire").toString());

             location.createExplosion(explosionPower, create_fire);
         }
    }

}
