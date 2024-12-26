package io.github.dueris.originspaper.data;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

public class ApoliContainerTypes {
	//	Presets
	public static final PresetContainerType DOUBLE_CHEST = register("double_chest", new PresetContainerType(9, 6, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new ChestMenu(MenuType.GENERIC_9x3, syncId, playerInventory, inventory, rows)));
	public static final PresetContainerType CHEST = register("chest", new PresetContainerType(9, 3, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new ChestMenu(MenuType.GENERIC_9x6, syncId, playerInventory, inventory, rows)));
	public static final PresetContainerType HOPPER = register("hopper", new PresetContainerType(5, 1, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new HopperMenu(syncId, playerInventory, inventory)));
	public static final PresetContainerType DROPPER = register("dropper", new PresetContainerType(3, 3, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new DispenserMenu(syncId, playerInventory, inventory)));
	public static final PresetContainerType DISPENSER = register("dispenser", new PresetContainerType(3, 3, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new DispenserMenu(syncId, playerInventory, inventory)));

	public static void register() {

	}

	public static <T extends ContainerType> @NotNull T register(String name, T containerType) {
		return Registry.register(ApoliRegistries.CONTAINER_TYPE, OriginsPaper.apoliIdentifier(name), containerType);
	}

}
