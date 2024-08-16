package io.github.dueris.originspaper.data.types;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Optional;

public enum ContainerType {
	CHEST(27, InventoryType.CHEST),
	HOPPER(9, InventoryType.HOPPER),
	DROPPER(9, InventoryType.DROPPER),
	DISPENSER(9, InventoryType.DISPENSER),
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

	public Inventory createInventory(Optional<String> string) {
		Component name = Component.text(string.orElse("container.inventory"));
		return this == DOUBLE_CHEST ? Bukkit.createInventory(null, this.getSize(), name) :
				this == CHEST ? Bukkit.createInventory(null, this.getSize(), name) : Bukkit.createInventory(null, this.bukkit, name);
	}
}
