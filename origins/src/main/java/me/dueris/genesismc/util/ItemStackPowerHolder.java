package me.dueris.genesismc.util;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.util.entity.PowerUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ItemStackPowerHolder implements Listener {
	private static final NamespacedKey key = GenesisMC.apoliIdentifier("stored_powers");
	private static final Map<String, Set<Player>> playersWithTags = new HashMap<>();

	public static void addPower(PowerType type, ItemStack stack) {
		if (stack.getItemMeta() == null) return;
		ItemMeta meta = stack.getItemMeta();
		PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
		List<String> tags = getTags(stack);
		tags.add(type.getTag());
		saveTags(dataContainer, tags);
		stack.setItemMeta(meta);
	}

	public static void removePower(PowerType type, ItemStack stack) {
		if (stack.getItemMeta() == null) return;
		ItemMeta meta = stack.getItemMeta();
		PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
		List<String> tags = getTags(stack);
		tags.remove(type.getTag());
		saveTags(dataContainer, tags);
		stack.setItemMeta(meta);
	}

	private static List<String> getTags(ItemStack itemStack) {
		if (itemStack == null || itemStack.getItemMeta() == null) return new ArrayList<>();
		PersistentDataContainer container = itemStack.getItemMeta().getPersistentDataContainer();
		String serializedTags = container.get(key, PersistentDataType.STRING);
		if (serializedTags == null || serializedTags.isEmpty()) {
			return new ArrayList<>();
		}
		String[] tagsArray = serializedTags.split(",");
		return new ArrayList<>(List.of(tagsArray));
	}

	public static int getPowerCount(ItemStack stack) {
		return getPowers(stack).size();
	}

	public static List<PowerType> getPowers(ItemStack stack) {
		return getTags(stack).stream().map(CraftApoli::getPowerFromTag).toList();
	}

	private static void saveTags(PersistentDataContainer container, List<String> tags) {
		String serializedTags = String.join(",", tags);
		container.set(key, PersistentDataType.STRING, serializedTags);
	}

	public void addPlayerToTagCheck(Player player, String tag) {
		playersWithTags.computeIfAbsent(tag, k -> new HashSet<>()).add(player);
		updatePlayerTagStatus(player, tag);
	}

	public void removePlayerFromTagCheck(Player player, String tag) {
		Set<Player> players = playersWithTags.get(tag);
		if (players != null) {
			players.remove(player);
		}
	}

	/**
	 * Run this when updating powers on the ItemStack
	 */
	private void updatePlayerTagStatus(Player player, String tag) {
		boolean hasTag = checkEquippedItems(player, tag);
		Set<Player> players = playersWithTags.computeIfAbsent(tag, k -> new HashSet<>());

		if (hasTag) {
			players.add(player);
			if (!CraftApoli.getPowerFromTag(tag).getPlayers().contains(player)) {
				try {
					PowerUtils.grantPower(Bukkit.getConsoleSender(), CraftApoli.getPowerFromTag(tag), player, CraftApoli.getLayerFromTag("apoli:command"), true);
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		} else {
			players.remove(player);
			try {
				PowerUtils.removePower(Bukkit.getConsoleSender(), CraftApoli.getPowerFromTag(tag), player, CraftApoli.getLayerFromTag("apoli:command"), true);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public ItemStackPowerHolder startTicking() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					for (String tag : playersWithTags.keySet()) {
						updatePlayerTagStatus(player, tag);
					}
				}
			}
		}.runTaskTimer(GenesisMC.getPlugin(), 0L, 40L);
		return this;
	}

	private boolean checkEquippedItems(Player player, String tag) {
		ItemStack[] equipment = new ItemStack[]{
			player.getInventory().getItemInMainHand(),
			player.getInventory().getItemInOffHand(),
			player.getInventory().getHelmet(),
			player.getInventory().getChestplate(),
			player.getInventory().getLeggings(),
			player.getInventory().getBoots()
		};

		for (ItemStack item : equipment) {
			if (item != null && hasTag(item, tag)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasTag(ItemStack itemStack, String tag) {
		List<String> tags = ItemStackPowerHolder.getTags(itemStack);
		return tags.contains(tag);
	}
}
