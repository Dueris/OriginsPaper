package me.dueris.genesismc.commands;

import me.dueris.genesismc.GenesisMC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import static me.dueris.genesismc.factory.powers.OriginsMod.genesismc.BigLeap.leapToggle;
import static me.dueris.genesismc.factory.powers.Power.big_leap_tick;
import static me.dueris.genesismc.utils.BukkitColour.RED;


public class ToggleCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player p) {
            PersistentDataContainer data = p.getPersistentDataContainer();
            if (p.hasPermission("genesismc.origins.cmd.toggle")) {

                int toggleState = data.get(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER);
                if (toggleState == 1)
                    data.set(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER, 2);
                else data.set(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER, 1);

                if (big_leap_tick.contains(p)) {
                    leapToggle(p);
                } else if (false) {
                    //add other origin toggles here like this
                } else {
                    sender.sendMessage(Component.text("Your origin does not have an ability that can be toggled").color(TextColor.fromHexString(RED)));
                }

            } else {
                sender.sendMessage(Component.text("You do not have permission to use this command.").color(TextColor.fromHexString(RED)));
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.3f, 1);
            }
        } else if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(Component.text("This is a player only command.").color(TextColor.fromHexString(RED)));
        }
        return true;
    }
}
