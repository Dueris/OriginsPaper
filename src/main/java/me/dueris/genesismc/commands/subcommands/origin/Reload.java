package me.dueris.genesismc.commands.subcommands.origin;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.OriginDataContainer;
import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.TagRegistry;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import com.mojang.logging.LogUtils;

public class Reload extends SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "reloads/reparses origins";
    }

    @Override
    public String getSyntax() {
        return "/origin reload";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.RED + "This action is unsupported and could cause major issues with origins.");
        sender.sendMessage(ChatColor.RED + "Use this command with caution. This could result in data loss, errors, and possibly a crash of the server.");
        try {
            CraftApoli.unloadData();
            CraftPower.getRegistered().clear();
            OriginDataContainer.unloadAllData();
            CraftApoli.loadOrigins();
            try {
                for (Class<? extends CraftPower> c : CraftPower.findCraftPowerClasses()) {
                    if (CraftPower.class.isAssignableFrom(c)) {
                        CraftPower instance = c.newInstance();
                        CraftPower.getRegistered().add(instance.getClass());
                        if (instance instanceof Listener || Listener.class.isAssignableFrom(instance.getClass())) {
                            Bukkit.getServer().getPluginManager().registerEvents((Listener) instance, GenesisMC.getPlugin());
                        }
                    }
                }
                for (OriginContainer origin : CraftApoli.getOrigins()) {
                    for (PowerContainer powerContainer : origin.getPowerContainers()) {
                        CraftApoli.getPowers().add(powerContainer);
                    }
                }
            } catch (IOException | ReflectiveOperationException e) {
                e.printStackTrace();
            }
            OriginDataContainer.loadData();
            sender.sendMessage(ChatColor.GREEN + "Origins reloaded successfully.");
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while attempting to reload origins", e);
        }

    }
}
