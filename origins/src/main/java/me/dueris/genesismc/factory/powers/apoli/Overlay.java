package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class Overlay extends CraftPower implements Listener {


    @EventHandler
    public void remove(PowerUpdateEvent e) {
        if (e.isRemoved() && e.getPower().getType().equals(getPowerFile())) {
            Phasing.deactivatePhantomOverlay(e.getPlayer());
        }
    }

    @Override
    public void run(Player player) {
        if (getPowerArray().contains(player)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                    if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) player)) {
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
