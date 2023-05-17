package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.api.entity.OriginPlayer;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.factory.powers.world.WorldSpawnHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

import static me.dueris.genesismc.core.choosing.ChoosingCORE.*;
import static org.bukkit.ChatColor.GRAY;

public class Set extends SubCommand {
    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "sets origin of given player";
    }

    @Override
    public String getSyntax() {
        return "/origin set <player> <origin>";
    }

    @Override
    public void perform(Player p, String[] args) {
        if(args.length > 2){
            Player given = Bukkit.getPlayer(args[1]);
            String origintag = args[2];

            OriginPlayer.setOrigin(p, origintag);




        }else{
            p.sendMessage(ChatColor.RED + "Invalid Args!!!");
        }
    }
}
