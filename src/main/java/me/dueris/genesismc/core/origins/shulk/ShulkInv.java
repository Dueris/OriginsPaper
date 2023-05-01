package me.dueris.genesismc.core.origins.shulk;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.utils.ShulkUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

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
      @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
      if (origintag.equalsIgnoreCase("genesis:origin-shulk")) {
      if (args.length > 0) {
        if (args[0].equalsIgnoreCase ( "open" )) {

          ArrayList<ItemStack> vaultItems = ShulkUtils.getItems ( p );

          Inventory vault = Bukkit.createInventory (p, InventoryType.DROPPER, "Shulker Inventory" );

          vaultItems.stream ( )
                  .forEach ( itemStack -> vault.addItem ( itemStack ) );

          p.openInventory ( vault );

        }
      }else if(args.length == 0){
        ArrayList<ItemStack> vaultItems = ShulkUtils.getItems ( p );

        Inventory vault = Bukkit.createInventory ( p , 9 , "Shulker Box" );

        vaultItems.stream ( )
                .forEach ( itemStack -> vault.addItem ( itemStack ) );

        p.openInventory ( vault );

      }
    }else{
      p.sendMessage(ChatColor.RED + "You must be a Shulk to access this command");
    }
    }



    return true;
  }
}

