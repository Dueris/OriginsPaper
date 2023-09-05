package me.dueris.genesismc.factory.powers.simple;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.events.KeybindTriggerEvent;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

import static me.dueris.genesismc.FoliaOriginScheduler.OriginSchedulerTree.mimic_warden;

public class MimicWarden extends CraftPower implements OriginSimple, Listener {

    @Override
    public void run(Player p) {

    }

    private static ArrayList<Player> mimicWardenPlayers = new ArrayList<>();

    @EventHandler
    public void event(OriginChangeEvent e) {
        boolean hasMimicWardenPower = false;

        for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
            for (String power : origin.getPowers()) {
                if (power.equals("origins:mimic_warden")) {
                    hasMimicWardenPower = true;
                    break;
                }
            }
        }

        if (hasMimicWardenPower && !mimicWardenPlayers.contains(e.getPlayer())) {
            mimicWardenPlayers.add(e.getPlayer());
        } else if (!hasMimicWardenPower && mimicWardenPlayers.contains(e.getPlayer())) {
            mimicWardenPlayers.remove(e.getPlayer());
        }
    }

    @EventHandler
    public void event(PlayerJoinEvent e) {
        boolean hasMimicWardenPower = false;

        for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
            for (PowerContainer power : origin.getPowerContainers()) {
                if (power.getTag().equals("origins:mimic_warden")) {
                    hasMimicWardenPower = true;
                    break;
                }
            }
        }

        if (hasMimicWardenPower && !mimicWardenPlayers.contains(e.getPlayer())) {
            mimicWardenPlayers.add(e.getPlayer());
        } else if (!hasMimicWardenPower && mimicWardenPlayers.contains(e.getPlayer())) {
            mimicWardenPlayers.remove(e.getPlayer());
        }
    }

    @Override
    public String getPowerFile() {
        return "genesis:simple-implementation-mimic-warden[@dueris]";
    }

    @EventHandler
    public void key(KeybindTriggerEvent e){
        if(mimicWardenPlayers.contains(e.getPlayer())){
            if(e.getKey().equals("key.origins.primary_active")){

            }
        }
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return mimic_warden;
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    @Override
    public String getSimpleTagID() {
        return "origins:mimic_warden";
    }
}
