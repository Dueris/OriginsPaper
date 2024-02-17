package me.dueris.genesismc.util;

import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.Inventory;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.storage.GenesisConfigs;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static me.dueris.genesismc.util.entity.OriginPlayerAccessor.powersAppliedList;

public class PowerUtils {

    public static void grant(CommandSender executor, PowerContainer power, Player p, LayerContainer layer) throws InstantiationException, IllegalAccessException {
        ArrayList<String> powerAppliedTypes = new ArrayList<>();
        ArrayList<Class<? extends CraftPower>> powerAppliedClasses = new ArrayList<>();
        if (!OriginPlayerAccessor.playerPowerMapping.get(p).get(layer).contains(power)) {
            OriginPlayerAccessor.playerPowerMapping.get(p).get(layer).add(power);
            for (Class<? extends CraftPower> c : CraftPower.getRegistry()) {
                CraftPower craftPower = null;

                try {
                    craftPower = c.newInstance();
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                if (power.getType().equals(craftPower.getPowerFile())) {
                    craftPower.getPowerArray().add(p);
                    if (!powersAppliedList.containsKey(p)) {
                        ArrayList lst = new ArrayList<>();
                        lst.add(c);
                        powerAppliedTypes.add(c.newInstance().getPowerFile());
                        powerAppliedClasses.add(c);
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

    public static void remove(CommandSender executor, PowerContainer power, Player p, LayerContainer layer) throws InstantiationException, IllegalAccessException {
        if (OriginPlayerAccessor.playerPowerMapping.get(p).get(layer).contains(power)) {
            OriginPlayerAccessor.playerPowerMapping.get(p).get(layer).remove(power);
            ArrayList<String> powerRemovedTypes = new ArrayList<>();
            ArrayList<Class<? extends CraftPower>> powerRemovedClasses = new ArrayList<>();
            Class<? extends CraftPower> c = Inventory.class;
            CraftPower craftPower = null;
            try {
                craftPower = c.newInstance();
            } catch (InstantiationException | IllegalAccessException ee) {
                throw new RuntimeException(ee);
            }
            if (power.getType().equals(craftPower.getPowerFile())) {
                craftPower.getPowerArray().remove(p);
                powerRemovedTypes.add(c.newInstance().getPowerFile());
                powerRemovedClasses.add(c);
                powersAppliedList.get(p).remove(c);
                if (GenesisConfigs.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Removed power[" + power.getTag() + "] to player " + p.getName());
                }
            }
            executor.sendMessage("Entity %name% had the power %power% removed"
                    .replace("%power%", power.getName())
                    .replace("%name%", p.getName())
            );
            new PowerUpdateEvent(p, power, true).callEvent();
        } else {

        }
    }
}
