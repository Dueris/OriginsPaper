package io.github.dueris.originspaper.data;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.inventory.MenuType;

public class ApoliContainerTypes {

	public static final ContainerType DOUBLE_CHEST = register("double_chest", ContainerType.preset(9, 6, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new ChestMenu(MenuType.GENERIC_9x3, syncId, playerInventory, inventory, rows)));
	public static final ContainerType CHEST = register("chest", ContainerType.preset(9, 3, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new ChestMenu(MenuType.GENERIC_9x6, syncId, playerInventory, inventory, rows)));
	public static final ContainerType HOPPER = register("hopper", ContainerType.preset(5, 1, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new HopperMenu(syncId, playerInventory, inventory)));
	public static final ContainerType DROPPER = register("dropper", ContainerType.preset(3, 3, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new DispenserMenu(syncId, playerInventory, inventory)));
	public static final ContainerType DISPENSER = register("dispenser", ContainerType.preset(3, 3, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new DispenserMenu(syncId, playerInventory, inventory)));

	public static void register() {

	}

	public static ContainerType register(String name, ContainerType containerType) {
		return Registry.register(ApoliRegistries.CONTAINER_TYPE, OriginsPaper.apoliIdentifier(name), containerType);
	}

}
