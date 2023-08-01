package me.dueris.genesismc.core.factory.powers.OriginsMod.actions;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.powers.Powers;
import me.dueris.genesismc.core.utils.LayerContainer;
import me.dueris.genesismc.core.utils.PowerContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class ActionOnBeingUsed implements Listener {

    @EventHandler
    public void entityRightClickEntity(PlayerInteractEntityEvent e) {
        Entity actor = e.getPlayer();
        Entity target = e.getRightClicked();

        if (!(target instanceof Player player)) return;
        if (!Powers.action_on_being_used.contains(target)) return;

        for (LayerContainer layer : OriginPlayer.getOrigin(player).keySet()) {
            PowerContainer power = OriginPlayer.getOrigin(player, layer).getPowerFileFromType("origins:action_on_being_used");
            if (power == null) continue;

            HashMap<String, Object> biEntityAction = power.getBiEntityAction();
            String type = biEntityAction.get("type").toString();
            System.out.println(type);
            if (type.equals("origins:add_velocity")) {
                float x;
                float y;
                float z;
                boolean set;

                if (biEntityAction.containsKey("x")) x = Float.parseFloat(biEntityAction.get("x").toString());
                else x = 0.0f;
                if (biEntityAction.containsKey("y")) y = Float.parseFloat(biEntityAction.get("y").toString());
                else y = 0.0f;
                if (biEntityAction.containsKey("z")) z = Float.parseFloat(biEntityAction.get("z").toString());
                else z = 0.0f;
                if (biEntityAction.containsKey("set")) set = Boolean.parseBoolean(biEntityAction.get("set").toString());
                else set = false;

                if (set) target.setVelocity(new Vector(x, y, z));
                else target.setVelocity(target.getVelocity().add(new Vector(x, y, z)));
            }
            if (type.equals("origins:damage")) {
                //haven't been able to find a way to change the damage type
                float amount;
//                String damageType;

                if (biEntityAction.containsKey("amount")) amount = Float.parseFloat(biEntityAction.get("amount").toString());
                else amount = 0.0f;
//                if (biEntityAction.containsKey("damage_type")) damageType = biEntityAction.get("damage_type").toString();
//                else damageType = "minecraft:kill";

                //target.setLastDamageCause(new EntityDamageEvent(actor, EntityDamageEvent.DamageCause.valueOf(damageType.split(":")[1].toUpperCase()), ((Player) target).getLastDamage()));
                ((Player) target).damage(amount);
            }
        }

        if (e.getHand() == EquipmentSlot.HAND) System.out.println("main");
        if (e.getHand() == EquipmentSlot.OFF_HAND) System.out.println("off");
    }

}
