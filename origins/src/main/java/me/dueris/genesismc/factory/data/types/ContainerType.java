package me.dueris.genesismc.factory.data.types;

import me.dueris.genesismc.util.Utils;
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

    private int size;
    private InventoryType bukkit;

    private ContainerType(int size, InventoryType type) {
        this.size = size == -1 ? type.getDefaultSize() : size;
        this.bukkit = type;
    }

    public static ContainerType getContainerType(String raw) {
        switch (raw.toLowerCase()) {
            case "hopper" -> {
                return HOPPER;
            }
            case "dropper" -> {
                return DROPPER;
            }
            case "dispenser" -> {
                return DISPENSER;
            }
            case "double_chest" -> {
                return DOUBLE_CHEST;
            }
            default -> {
                return CHEST;
            }
        }
    }

    public InventoryType getBukkit() {
        return this.bukkit;
    }

    public int getSize() {
        return this.size;
    }

    public Inventory createInventory(InventoryHolder holder, Optional<String> string) {
        Component name = Component.text(Utils.getOrAbsent(string, "container.inventory"));
        return this == DOUBLE_CHEST ? Bukkit.createInventory(holder, this.getSize(), name) : this == CHEST ? Bukkit.createInventory(holder, this.getSize(), name) : Bukkit.createInventory(holder, this.bukkit, name);
    }
}
