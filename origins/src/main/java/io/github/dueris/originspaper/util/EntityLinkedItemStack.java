package io.github.dueris.originspaper.util;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

public class EntityLinkedItemStack implements Listener {
	private static EntityLinkedItemStack instance;
	private final ConcurrentHashMap<ItemStack, Player> itemHolderMap = new ConcurrentHashMap<>();

	public static EntityLinkedItemStack getInstance() {
		if (instance == null) {
			instance = new EntityLinkedItemStack();
		}

		return instance;
	}

	public void updateHolder(ItemStack itemStack, Player player) {
		if (player == null) {
			this.itemHolderMap.remove(itemStack);
		} else {
			this.itemHolderMap.put(itemStack, player);
		}
	}

	public Player getHolder(ItemStack itemStack) {
		return this.itemHolderMap.get(itemStack);
	}

	@EventHandler
	public void onItemPickup(@NotNull PlayerPickupItemEvent e) {
		Player player = e.getPlayer();
		ItemStack itemStack = e.getItem().getItemStack();
		this.updateHolder(itemStack, player);
	}

	@EventHandler
	public void onItemDrop(@NotNull PlayerDropItemEvent e) {
		ItemStack itemStack = e.getItemDrop().getItemStack();
		this.updateHolder(itemStack, e.getPlayer());
	}

	@EventHandler
	public void onItemSwitch(@NotNull PlayerInventorySlotChangeEvent e) {
		ItemStack itemStack = e.getNewItemStack();
		this.updateHolder(itemStack, e.getPlayer());
	}
}
