package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionOnBeingUsed extends CraftPower implements Listener {

    @EventHandler
    public void entityRightClickEntity(PlayerInteractEntityEvent e) {
        Entity actor = e.getPlayer();
        Entity target = e.getRightClicked();

        if (!(target instanceof Player player)) return;
        if (!getPowerArray().contains(target)) return;

        for (OriginContainer origin : OriginPlayer.getOrigin(player).values()) {
            PowerContainer power = origin.getPowerFileFromType("origins:action_on_being_used");
            if (power == null) continue;

            if (!getPowerArray().contains(e.getPlayer())) return;
            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
            ActionTypes.biEntityActionType(actor, target, power.getBiEntityAction());
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

    Player p;

    public ActionOnBeingUsed(){
        this.p = p;
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "origins:action_on_being_used";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_being_used;
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
