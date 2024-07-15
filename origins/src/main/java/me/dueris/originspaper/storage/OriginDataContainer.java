package me.dueris.originspaper.storage;

import me.dueris.originspaper.OriginsPaper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;

public class OriginDataContainer {
	private static final HashMap<Player, String> dataContainer = new HashMap<>();

	public static void loadData() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			OriginDataContainer.loadData(p);
		}
	}

	public static void loadData(Player player) {
		dataContainer.put(
			player,
			player.getPersistentDataContainer().get(
				new NamespacedKey(OriginsPaper.getPlugin(), "originLayer"),
				PersistentDataType.STRING
			)
		);
	}

	public static String getLayer(Player player) {
		return dataContainer.get(player);
	}

	public static void unloadAllData() {
		dataContainer.clear();
	}

	public static void unloadData(Player player) {
		dataContainer.remove(player);
	}

	public static HashMap<Player, String> getDataMap() {
		return dataContainer;
	}
}
