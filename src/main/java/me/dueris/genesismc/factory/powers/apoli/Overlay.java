package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;

public class Overlay extends CraftPower implements Listener {

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    @EventHandler
    public void remove(PowerUpdateEvent e){
        if(e.isRemoved() && e.getPower().getType().equals(getPowerFile())){
            Phasing.deactivatePhantomOverlay(e.getPlayer());
        }
    }

    @Override
    public void run(Player player) {
        if (getPowerArray().contains(player)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                    if (conditionExecutor.check("condition", "conditions", player, power, "apoli:overlay", player, null, player.getLocation().getBlock(), null, player.getInventory().getItemInMainHand(), null)) {
                        setActive(player, power.getTag(), true);
                        Phasing.initializePhantomOverlay(player);
                    } else {
                        setActive(player, power.getTag(), false);
                        Phasing.deactivatePhantomOverlay(player);
                    }
                }
            }
        } else {
            Phasing.deactivatePhantomOverlay(player);
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:overlay";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return overlay;
    }
}
