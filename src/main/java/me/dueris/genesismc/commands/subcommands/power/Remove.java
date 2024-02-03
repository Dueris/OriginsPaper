package me.dueris.genesismc.commands.subcommands.power;

import me.dueris.genesismc.commands.PlayerSelector;
import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.player.inventory.Inventory;
import me.dueris.genesismc.files.GenesisDataFiles;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static me.dueris.genesismc.entity.OriginPlayerUtils.powersAppliedList;

public class Remove extends SubCommand {
    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "removes a power";
    }

    @Override
    public String getSyntax() {
        return "/power remove <args>";
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
                if (OriginPlayerUtils.playerPowerMapping.get(p) == null) continue;
                PowerContainer poweR = CraftApoli.keyedPowerContainers.get(args[2]);
                ArrayList<PowerContainer> powersToEdit = new ArrayList<>();
                powersToEdit.add(poweR);
                powersToEdit.addAll(CraftApoli.getNestedPowers(poweR));
                for (PowerContainer power : powersToEdit) {
                    try {
                        if (OriginPlayerUtils.playerPowerMapping.get(p).get(CraftApoli.getLayerFromTag(layerTag)).contains(power)) {
                            OriginPlayerUtils.playerPowerMapping.get(p).get(CraftApoli.getLayerFromTag(layerTag)).remove(power);
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
                                try {
                                    powerRemovedTypes.add(c.newInstance().getPowerFile());
                                } catch (InstantiationException | IllegalAccessException ee) {
                                    throw new RuntimeException(ee);
                                }
                                powerRemovedClasses.add(c);
                                powersAppliedList.get(p).remove(c);
                                if (GenesisDataFiles.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
                                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Removed power[" + power.getTag() + "] to player " + p.getName());
                                }
                            }
                            sender.sendMessage("Entity %name% had the power %power% removed"
                                    .replace("%power%", power.getName())
                                    .replace("%name%", p.getName())
                            );
                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
