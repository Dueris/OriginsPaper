package io.github.dueris.originspaper.util;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.type.Active;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.LinkedList;

public class KeybindUtil implements Listener {
	public static HashMap<Player, LinkedList<String>> activeKeys = new HashMap<>();

	private static void clearOldData(Player player) {
		for (ItemStack itemStack : player.getInventory().getContents().clone()) {
			if (itemStack == null) continue;
			if (itemStack.getPersistentDataContainer().getKeys().contains(new NamespacedKey("genesismc", "origin_item_data"))) {
				player.getInventory().remove(itemStack);
			}
		}
	}

	public static String translateOriginRawKey(String string) {
		if (string.contains("key.origins.primary_active"))
			return string.replace("key.origins.primary_active", "Primary");
		if (string.contains("key.origins.secondary_active"))
			return string.replace("key.origins.secondary_active", "Secondary");
		return string;
	}

	public static ItemStack getPrimaryTrigger(Player player) {
		for (ItemStack item : player.getInventory().getContents()) {
			if (item == null) continue;
			if (item.getItemMeta() == null) continue;
			if (item.getItemMeta().getPersistentDataContainer().has(identifier("origin_item_data"))) {
				if (item.getItemMeta().getPersistentDataContainer().get(identifier("origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase("key.origins.primary_active")) {
					return item;
				}
			}
		}
		return null;
	}

	public static ItemStack getSecondaryTrigger(Player player) {
		for (ItemStack item : player.getInventory().getContents()) {
			if (item == null) continue;
			if (item.getItemMeta().getPersistentDataContainer().has(identifier("origin_item_data"))) {
				if (item.getItemMeta().getPersistentDataContainer().get(identifier("origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase("key.origins.secondary_active")) {
					return item;
				}
			}
		}
		return null;
	}

	public static void triggerExecution(String key, Player player) {
		triggerActiveKey(player, key);
		net.minecraft.world.entity.player.Player nms = ((CraftPlayer) player).getHandle();
		if (!PowerHolderComponent.KEY.isProvidedBy(nms)) return;
		for (Power power : PowerHolderComponent.KEY.get(nms).getPowers(true)) {
			if (power.asReference().getNullablePowerType(nms) instanceof Active active) {
				if (active.getKey().key.equalsIgnoreCase(key) && active.canTrigger()) {
					active.onUse();
				}
			}
		}
	}

	private static void triggerActiveKey(Player player, String key) {
		activeKeys.putIfAbsent(player, new LinkedList<>());
		activeKeys.get(player).add(key);
		new BukkitRunnable() {
			@Override
			public void run() {
				activeKeys.get(player).remove(key);
			}
		}.runTaskLater(OriginsPaper.getPlugin(), 1);
	}

	// Keybind triggers end

	public static boolean isKeyActive(String key, Player player) {
		activeKeys.putIfAbsent(player, new LinkedList<>());
		return activeKeys.get(player).contains(key);
	}

	public static ItemStack createKeybindItem(String keybindType, String valueKEY, int id) {
		ItemStack itemStack = new ItemStack(Material.GRAY_DYE);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itemMeta.addEnchant(Enchantment.INFINITY, 1, true);
		itemMeta.setCustomModelData(id);
		itemMeta.setDisplayName(ChatColor.GRAY + "Keybind : " + keybindType);
		itemMeta.getPersistentDataContainer().set(identifier("origin_item_data"), PersistentDataType.STRING, valueKEY);
		itemMeta.getPersistentDataContainer().set(new NamespacedKey(OriginsPaper.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	public static void addPrimaryItem(HumanEntity p) {
		p.getInventory().addItem(createKeybindItem("Primary", "key.origins.primary_active", 0001));
	}

	public static void addSecondaryItem(HumanEntity p) {
		p.getInventory().addItem(createKeybindItem("Secondary", "key.origins.secondary_active", 0002));
	}

	public static void addItems(HumanEntity p) {
		if (!hasOriginDataTriggerPrimary(p.getInventory()))
			addPrimaryItem(p);
		if (!hasOriginDataTriggerSecondary(p.getInventory()))
			addSecondaryItem(p);
	}

	public static boolean hasOriginDataTriggerPrimary(Inventory inventory) {
		for (ItemStack item : inventory.getContents()) {
			if (item != null) {
				if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(OriginsPaper.getPlugin(), "origin_item_data"))) {
					if (item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(OriginsPaper.getPlugin(), "origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase("key.origins.primary_active")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean hasOriginDataTriggerSecondary(Inventory inventory) {
		for (ItemStack item : inventory.getContents()) {
			if (item != null) {
				if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(OriginsPaper.getPlugin(), "origin_item_data"))) {
					if (item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(OriginsPaper.getPlugin(), "origin_item_data"), PersistentDataType.STRING).equalsIgnoreCase("key.origins.secondary_active")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static NamespacedKey identifier(String id) {
		return new NamespacedKey(OriginsPaper.getPlugin(), id);
	}

	@EventHandler
	public void jump(PlayerJumpEvent e) {
		triggerExecution("key.jump", e.getPlayer());
	}

	@EventHandler
	public void click(PlayerInteractEvent e) {
		if (e.getAction().isRightClick()) {
			triggerExecution("key.use", e.getPlayer());
			if (e.getItem() != null) {
				if (e.getItem().getItemMeta() == null || !e.getHand().equals(EquipmentSlot.HAND)) return;
				if (e.getItem().getItemMeta().getPersistentDataContainer().has(identifier("origin_item_data"))) { // Is keybind
					triggerExecution(e.getItem().getItemMeta().getPersistentDataContainer().get(identifier("origin_item_data"), PersistentDataType.STRING), e.getPlayer());
				}
			}
		} else {
			triggerExecution("key.attack", e.getPlayer());
		}
	}

	@EventHandler
	public void interactEvent(PlayerInteractEntityEvent e) {
		ItemStack stack = e.getPlayer().getInventory().getItem(e.getHand());
		if (stack != null && stack.getItemMeta() != null && stack.getItemMeta().getPersistentDataContainer().has(identifier("origin_item_data")))
			e.setCancelled(true);
	}

	private void resetKeybinds(Player p) {
		if (activeKeys.containsKey(p)) {
			activeKeys.get(p).clear();
		}
		for (ItemStack item : p.getInventory()) {
			if (item == null) continue;
			if (item.equals(getPrimaryTrigger(p))) {
				item.setType(Material.GRAY_DYE);
			}
			if (item.equals(getSecondaryTrigger(p))) {
				item.setType(Material.GRAY_DYE);
			}
		}
	}

	@EventHandler
	public void shift(PlayerToggleSneakEvent e) {
		triggerExecution("key.sneak", e.getPlayer());
	}

	@EventHandler
	public void swap(PlayerSwapHandItemsEvent e) {
		triggerExecution("key.swapOffhand", e.getPlayer());
	}

	@EventHandler
	public void drop(PlayerDropItemEvent e) {
		triggerExecution("key.drop", e.getPlayer());
	}

	@EventHandler
	@SuppressWarnings("Index out of bounds")
	public void onTransfer(InventoryClickEvent e) {
		if (e.getClick().isKeyboardClick()) {
			if (e.getView().getTopInventory().getType() == InventoryType.CRAFTING) return;
			try {
				if (e.getView().getBottomInventory().getItem(e.getHotbarButton()) != null) {
					ItemStack transferred = e.getView().getBottomInventory().getItem(e.getHotbarButton());
					if (transferred == null) return;
					if (transferred.isSimilar(getPrimaryTrigger((Player) e.getWhoClicked()))) {
						e.setCancelled(true);
					}
					if (transferred.isSimilar(getSecondaryTrigger((Player) e.getWhoClicked()))) {
						e.setCancelled(true);
					}
				}
			} catch (Throwable t) {
				// Silence
			}

			return;
		}
		if (e.getView().getTopInventory().getHolder() != null && e.getView().getTopInventory().getHolder().equals(e.getWhoClicked()))
			return;
		if (e.getCurrentItem() == null) return;
		if (e.getCurrentItem().isSimilar(getPrimaryTrigger((Player) e.getWhoClicked()))) {
			e.setCancelled(true);
		}
		if (e.getCurrentItem().isSimilar(getSecondaryTrigger((Player) e.getWhoClicked()))) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void resetKeybinding2(PlayerRespawnEvent e) {
		resetKeybinds(e.getPlayer());
	}

	@EventHandler
	public void OnCraftAttempt(PrepareItemCraftEvent e) {
		for (ItemStack ingredient : e.getInventory().getMatrix()) {
			if (ingredient == null) return;
			if (ingredient.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(OriginsPaper.getPlugin(), "origins_item_data"))) {
				e.getInventory().setResult(null);
			}
		}
	}

	@EventHandler
	public void jointhing(PlayerJoinEvent e) {
		clearOldData(e.getPlayer());
		if (!hasOriginDataTriggerPrimary(e.getPlayer().getInventory())) {
			addPrimaryItem(e.getPlayer());
		}
		if (!hasOriginDataTriggerSecondary(e.getPlayer().getInventory())) {
			addSecondaryItem(e.getPlayer());
		}
	}

	@EventHandler
	public void deathDropCancel(PlayerDeathEvent e) {
		e.getDrops().removeIf(itemStack -> itemStack.getItemMeta() != null && itemStack.getItemMeta().getPersistentDataContainer().has(identifier("origin_item_data")));
	}

	@EventHandler
	public void respawnGIVE(PlayerRespawnEvent e) {
		if (!hasOriginDataTriggerPrimary(e.getPlayer().getInventory())) {
			addPrimaryItem(e.getPlayer());
		}
		if (!hasOriginDataTriggerSecondary(e.getPlayer().getInventory())) {
			addSecondaryItem(e.getPlayer());
		}
	}

	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent event) {
		if (!event.getSource().equals(event.getDestination()) && event.getItem().getItemMeta().getPersistentDataContainer().has(identifier("origin_item_data"))) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void preventDrop(PlayerDropItemEvent e) {
		if (e.getItemDrop().getItemStack().getItemMeta().getPersistentDataContainer().has(identifier("origin_item_data"))) {
			e.setCancelled(true);
			new BukkitRunnable() {
				@Override
				public void run() {
					if (!e.getPlayer().getInventory().contains(e.getItemDrop().getItemStack())) {
						e.getPlayer().getInventory().addItem(e.getItemDrop().getItemStack());
					}
				}
			}.runTaskLater(OriginsPaper.getPlugin(), 1);
		}
	}
}
