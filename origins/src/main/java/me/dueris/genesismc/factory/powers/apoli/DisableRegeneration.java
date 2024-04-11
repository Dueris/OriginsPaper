package me.dueris.genesismc.factory.powers.apoli;

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
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.ArrayList;

public class DisableRegeneration extends CraftPower implements Listener {


    @EventHandler
    public void disable(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (disable_regen.contains(p)) {
                for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                    ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                    for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (ConditionExecutor.testEntity(power, power.get("condition"), (CraftEntity) p)) {
                            setActive(p, power.getTag(), true);
                            if (e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
                                e.setAmount(0);
                                e.setCancelled(true);
                            }
                        } else {
                            setActive(p, power.getTag(), false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "apoli:disable_regen";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return disable_regen;
    }
}
