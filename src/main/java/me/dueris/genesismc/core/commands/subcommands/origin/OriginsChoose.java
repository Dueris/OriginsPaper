package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.dueris.genesismc.core.choosing.contents.MainMenuContents.GenesisMainMenuContents;
import static org.bukkit.ChatColor.*;

public class OriginsChoose extends SubCommand {
    @Override
    public String getName() {
        return "choose";
    }

    @Override
    public String getDescription() {
        return "choose your origin";
    }

    @Override
    public String getSyntax() {
        return "/origins choose";
    }

    @Override
    public void perform(Player p, String[] args) {
        if (args.length == 2) {
            p.sendMessage(args);
            p.sendMessage(args[0]);
            p.sendMessage(args[1]);
            p.sendMessage(String.valueOf(args.length));
            Player foundPlayer = Bukkit.getPlayer(args[1]);
            if (foundPlayer == null) {
                p.sendMessage(RED + "No player with that name can be found.");
                return;
            } else {
                p = foundPlayer;
            }
        }
        if (p.hasPermission("genesismc.origins.cmd.choose")) {
            PersistentDataContainer data = p.getPersistentDataContainer();
            @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
            int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);
            if (!p.getScoreboardTags().contains("chosen") && origintag.equalsIgnoreCase("genesis:origin-null") && phantomid != 2) {

                @NotNull Inventory mainmenu = Bukkit.createInventory(p, 54, "Choosing Menu");
                if (origintag == "genesis:origin-null") {
                    mainmenu.setContents(GenesisMainMenuContents());
                    p.openInventory(mainmenu);
                }

            } else {
                if (phantomid == 2) {
                    p.sendMessage(RED + "You cannot rechoose your origin while in phantom form.");
                }
                p.sendMessage(RED + "You have already chosen an origin. Craft the Orb of Origins if you would like to rechoose your origin.");
            }
        }

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
