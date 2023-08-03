package me.dueris.genesismc.core.factory.powers.OriginsMod.actions;

import me.dueris.genesismc.core.factory.powers.OriginsMod.OriginMethods;
import me.dueris.genesismc.core.utils.PowerContainer;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;

import java.util.HashMap;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.OriginMethods.statusEffectInstance;

public class ActionTypes {

    public static void biEntityActionType(Entity actor, Entity target, PowerContainer power) {
        JSONObject biEntityAction = power.getBiEntityAction();
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
    }

}
