package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionOnItemUse extends CraftPower implements Listener {
    Player p;

    public ActionOnItemUse(){
        this.p = p;
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void entityRightClickEntity(PlayerInteractEvent e) {
        Player actor = e.getPlayer();

        for (OriginContainer origin : OriginPlayer.getOrigin(actor).values()) {
            PowerContainer power = origin.getPowerFileFromType(getPowerFile());
            if (power == null) continue;

            if (!getPowerArray().contains(e.getPlayer())) return;
            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
            ActionTypes.EntityActionType(actor, power.getEntityAction());
            ActionTypes.ItemActionType(actor.getActiveItem(), power.getItemAction());
            GenesisMC.getOriginScheduler().runTaskLater(new BukkitRunnable() {
                @Override
                public void run() {
                    if (!getPowerArray().contains(e.getPlayer())) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                }
            }, 2L);
        }

//        if (e.getHand() == EquipmentSlot.HAND) System.out.println("main");
//        if (e.getHand() == EquipmentSlot.OFF_HAND) System.out.println("off");
    }

    @Override
    public String getPowerFile() {
        return "origins:action_on_item_use";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_item_use;
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }
}
