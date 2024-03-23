package me.dueris.genesismc.factory.actions.types;

import me.dueris.genesismc.event.AddToSetEvent;
import me.dueris.genesismc.event.RemoveFromSetEvent;
import me.dueris.genesismc.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;

import static me.dueris.genesismc.factory.actions.Actions.EntityActionType;

public class BiEntityActions {

	public static void runbiEntity(Entity actor, Entity target, JSONObject action) {
		if (action == null || action.isEmpty()) return;
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
}
