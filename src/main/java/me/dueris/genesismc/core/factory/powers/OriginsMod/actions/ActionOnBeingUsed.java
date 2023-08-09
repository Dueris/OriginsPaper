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

            ActionTypes.biEntityActionType(actor, target, power.getBiEntityAction());
        }

//        if (e.getHand() == EquipmentSlot.HAND) System.out.println("main");
//        if (e.getHand() == EquipmentSlot.OFF_HAND) System.out.println("off");
    }

}
