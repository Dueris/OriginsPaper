package me.dueris.genesismc.core;

import me.dueris.genesismc.core.utils.ShulkUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;

public class DataContainer implements Listener {

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e){

    Player p = e.getPlayer();

    PersistentDataContainer data = p.getPersistentDataContainer();

    if (!data.has(new NamespacedKey(GenesisMC.getPlugin(), "shulker"), PersistentDataType.STRING)){
      data.set(new NamespacedKey(GenesisMC.getPlugin(), "shulker"), PersistentDataType.STRING, "");
    }

  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent e){

    Player p = (Player) e.getPlayer();

    if (e.getView().getTitle().equalsIgnoreCase("Shulker box")){

      ArrayList<ItemStack> prunedItems = new ArrayList<>();

      Arrays.stream(e.getInventory().getContents())
              .filter(itemStack -> {
                if (itemStack == null){
                  return false;
                }
                return true;
              })
              .forEach(itemStack -> prunedItems.add(itemStack));

      ShulkUtils.storeItems(prunedItems, p);

    }

  }

}