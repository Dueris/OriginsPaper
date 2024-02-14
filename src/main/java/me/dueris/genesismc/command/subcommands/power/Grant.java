package me.dueris.genesismc.command.subcommands.power;

import me.dueris.genesismc.command.PlayerSelector;
import me.dueris.genesismc.command.subcommands.SubCommand;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.storage.GenesisDataFiles;
import me.dueris.genesismc.util.entity.OriginPlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static me.dueris.genesismc.util.entity.OriginPlayerUtils.powersAppliedList;

public class Grant extends SubCommand {
    @Override
    public String getName() {
        return "grant";
    }

    @Override
    public String getDescription() {
        return "grants a power";
    }

    @Override
    public String getSyntax() {
        return "/power grant <args>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Please provide a player arg.");
        } else if (args.length == 1) {
            sender.sendMessage(ChatColor.RED + "Please provide a power arg.");
        } else if (args.length >= 2) {
            String layerTag = "origins:origin";
            try {
                if (args.length >= 3 && args[3] != null) {
                    layerTag = args[3];
                }
            } catch (Exception e) {
            }
            ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[1]);
            for (Player p : players) {
                if (players.size() == 0) return;
                if (p == null) continue;
                if (OriginPlayerUtils.playerPowerMapping.get(p) == null) continue;
                PowerContainer poweR = CraftApoli.keyedPowerContainers.get(args[2]);
                ArrayList<PowerContainer> powersToEdit = new ArrayList<>();
                powersToEdit.add(poweR);
                powersToEdit.addAll(CraftApoli.getNestedPowers(poweR));
                for (PowerContainer power : powersToEdit) {
                    try {
                        ArrayList<String> powerAppliedTypes = new ArrayList<>();
                        ArrayList<Class<? extends CraftPower>> powerAppliedClasses = new ArrayList<>();
                        if (!OriginPlayerUtils.playerPowerMapping.get(p).get(CraftApoli.getLayerFromTag(layerTag)).contains(power)) {
                            OriginPlayerUtils.playerPowerMapping.get(p).get(CraftApoli.getLayerFromTag(layerTag)).add(power);
                            if (power == null) continue;
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
                                    if (GenesisDataFiles.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
                                        Bukkit.getConsoleSender().sendMessage(net.md_5.bungee.api.ChatColor.GREEN + "Assigned power[" + power.getTag() + "] to player " + p.getName());
                                    }
                                }
                            }
                            sender.sendMessage("Entity %name% was granted the power %power%"
                                    .replace("%power%", power.getName())
                                    .replace("%name%", p.getName())
                            );
                            new PowerUpdateEvent(p, power, false).callEvent();
                        } else {

                        }
                    } catch (InstantiationException ex) {
                        throw new RuntimeException(ex);
                    } catch (IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }
}
