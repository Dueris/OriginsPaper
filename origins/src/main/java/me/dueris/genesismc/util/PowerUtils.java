package me.dueris.genesismc.util;

import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.ApoliPower;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.storage.GenesisConfigs;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static me.dueris.genesismc.util.entity.OriginPlayerAccessor.powersAppliedList;

public class PowerUtils {

    public static void grant(CommandSender executor, Power power, Player p, Layer layer) throws InstantiationException, IllegalAccessException {
        if (!OriginPlayerAccessor.playerPowerMapping.get(p).get(layer).contains(power)) {
            OriginPlayerAccessor.playerPowerMapping.get(p).get(layer).add(power);
            for (ApoliPower c : ((Registrar<ApoliPower>)GenesisMC.getPlugin().registry.retrieve(Registries.CRAFT_POWER)).values()) {
                if (power.getType().equals(c.getPowerFile())) {
                    c.getPowerArray().add(p);
                    if (!powersAppliedList.containsKey(p)) {
                        ArrayList lst = new ArrayList<>();
                        lst.add(c);
                        powersAppliedList.put(p, lst);
                    } else {
                        powersAppliedList.get(p).add(c);
                    }
                    if (GenesisConfigs.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
                        Bukkit.getConsoleSender().sendMessage(net.md_5.bungee.api.ChatColor.GREEN + "Assigned power[" + power.getTag() + "] to player " + p.getName());
                    }
                }
            }
            executor.sendMessage("Entity %name% was granted the power %power%"
                    .replace("%power%", power.getName())
                    .replace("%name%", p.getName())
            );
            new PowerUpdateEvent(p, power, false).callEvent();
        } else {

        }
    }

    public static void remove(CommandSender executor, Power poweR, Player p, Layer layer) throws InstantiationException, IllegalAccessException {
        if(OriginPlayerAccessor.playerPowerMapping.get(p) != null){
            ArrayList<Power> powersToEdit = new ArrayList<>();
            powersToEdit.add(poweR);
            powersToEdit.addAll(CraftApoli.getNestedPowers(poweR));
            for (Power power : powersToEdit) {
                try {
                    if (OriginPlayerAccessor.playerPowerMapping.get(p).get(layer).contains(power)) {
                        OriginPlayerAccessor.playerPowerMapping.get(p).get(layer).remove(power);
                        for (ApoliPower c : ((Registrar<ApoliPower>)GenesisMC.getPlugin().registry.retrieve(Registries.CRAFT_POWER)).values()) {
                            if (power.getType().equals(c.getPowerFile())) {
                                c.getPowerArray().remove(p);
                                powersAppliedList.get(p).remove(c);
                                if (GenesisConfigs.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
                                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Removed power[" + power.getTag() + "] to player " + p.getName());
                                }
                                executor.sendMessage("Entity %name% had the power %power% removed"
                                        .replace("%power%", power.getName())
                                        .replace("%name%", p.getName())
                                );
                            }
                        }
                        new PowerUpdateEvent(p, power, true).callEvent();
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
