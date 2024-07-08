package me.dueris.originspaper.factory.data.types;

import me.dueris.originspaper.util.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Optional;

public enum ContainerType {
	CHEST(27, InventoryType.CHEST),
	HOPPER(-1, InventoryType.HOPPER),
	DROPPER(-1, InventoryType.DROPPER),
	DISPENSER(-1, InventoryType.DISPENSER),
	DOUBLE_CHEST(54, InventoryType.CHEST);

	private final int size;
	private final InventoryType bukkit;

	ContainerType(int size, InventoryType type) {
		this.size = size == -1 ? type.getDefaultSize() : size;
		this.bukkit = type;
	}

	public InventoryType getBukkit() {
		return this.bukkit;
	}

	public int getSize() {
		return this.size;
	}

	public Inventory createInventory(InventoryHolder holder, Optional<String> string) {
		Component name = Component.text(Util.getOrAbsent(string, "container.inventory"));
		return this == DOUBLE_CHEST ? Bukkit.createInventory(holder, this.getSize(), name) : this == CHEST ? Bukkit.createInventory(holder, this.getSize(), name) : Bukkit.createInventory(holder, this.bukkit, name);
	}
}
