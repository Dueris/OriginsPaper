package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.actions.Actions;
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

public class ActionOnEntityUse extends CraftPower implements Listener {
    Player p;

    public ActionOnEntityUse() {
        this.p = p;
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void entityRightClickEntity(PlayerInteractEntityEvent e) {
        Player actor = e.getPlayer();
        Entity target = e.getRightClicked();

        if (!(target instanceof Player player)) return;
        if (!getPowerArray().contains(target)) return;

        for (OriginContainer origin : OriginPlayer.getOrigin(player).values()) {
            for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                if (power == null) continue;

                if (!getPowerArray().contains(e.getPlayer())) return;
                setActive(power.getTag(), true);
                Actions.biEntityActionType(actor, target, power.getBiEntityAction());
                Actions.ItemActionType(actor.getActiveItem(), power.getAction("held_item_action"));
                Actions.ItemActionType(actor.getActiveItem(), power.getAction("result_item_action"));
                //todo:add conditions for it see https://origins.readthedocs.io/en/latest/types/power_types/action_on_entity_use/
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!getPowerArray().contains(e.getPlayer())) return;
                        setActive(power.getTag(), false);
                    }
                }.runTaskLater(GenesisMC.getPlugin(), 2L);
            }
        }

//        if (e.getHand() == EquipmentSlot.HAND) System.out.println("main");
//        if (e.getHand() == EquipmentSlot.OFF_HAND) System.out.println("off");
    }

    @Override
    public String getPowerFile() {
        return "origins:action_on_entity_use";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_entity_use;
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
