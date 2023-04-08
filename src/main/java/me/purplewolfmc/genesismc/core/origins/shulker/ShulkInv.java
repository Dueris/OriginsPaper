package me.purplewolfmc.genesismc.core.origins.shulker;

import me.purplewolfmc.genesismc.core.GenesisMC;
import me.purplewolfmc.genesismc.core.commands.subcommands.*;
import me.purplewolfmc.genesismc.core.utils.ShulkUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ShulkInv implements CommandExecutor {

  private ArrayList<SubCommand> subCommands = new ArrayList<>();
  public ShulkInv(){
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    if (sender instanceof Player){

      Player p = (Player) sender;
      PersistentDataContainer data = p.getPersistentDataContainer();
      int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
      if (originid == 6503044) {
      if (args.length > 0) {
        if (args[0].equalsIgnoreCase ( "open" )) {

          ArrayList<ItemStack> vaultItems = ShulkUtils.getItems ( p );

          Inventory vault = Bukkit.createInventory ( p , 9 , "Shulker Box" );

          vaultItems.stream ( )
                  .forEach ( itemStack -> vault.addItem ( itemStack ) );

          p.openInventory ( vault );

        }
      }
    }else{
      p.sendMessage(ChatColor.RED + "You must be a Shulk to access this command");
    }
    }



    return true;
  }
}

