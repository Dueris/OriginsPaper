package me.dueris.genesismc.util;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import me.dueris.genesismc.GenesisMC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ConcurrentHashMap;

public class EntityLinkedItemStack implements Listener {
	private static EntityLinkedItemStack instance;
	private final ConcurrentHashMap<ItemStack, Player> itemHolderMap;

	public EntityLinkedItemStack() {
		itemHolderMap = new ConcurrentHashMap<>();
	}

	public static EntityLinkedItemStack getInstance() {
		if (instance == null) {
			instance = new EntityLinkedItemStack();
		}
		return instance;
	}

	public void updateHolder(ItemStack itemStack, Player player) {
		if (player == null) {
			itemHolderMap.remove(itemStack);
		} else {
			itemHolderMap.put(itemStack, player);
		}
	}

	public Player getHolder(ItemStack itemStack) {
		return itemHolderMap.get(itemStack);
	}

	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent e) {
		Player player = e.getPlayer();
		ItemStack itemStack = e.getItem().getItemStack();
		updateHolder(itemStack, player);
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		ItemStack itemStack = e.getItemDrop().getItemStack();
		updateHolder(itemStack, e.getPlayer());
	}

	@EventHandler
	public void onItemSwitch(PlayerInventorySlotChangeEvent e) {
		ItemStack itemStack = e.getNewItemStack();
		updateHolder(itemStack, e.getPlayer());
	}

}
