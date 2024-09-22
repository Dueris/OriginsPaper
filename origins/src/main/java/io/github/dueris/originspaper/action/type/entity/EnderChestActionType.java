package io.github.dueris.originspaper.action.type.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.PlayerEnderChestContainer;

public class EnderChestActionType {

	public static void action(Entity entity) {

		if (!(entity instanceof Player player)) {
			return;
		}

		PlayerEnderChestContainer enderChestContainer = player.getEnderChestInventory();
		MenuConstructor handlerFactory = (syncId, playerInventory, _player) -> ChestMenu.threeRows(syncId, playerInventory, enderChestContainer);

		player.openMenu(new SimpleMenuProvider(handlerFactory, Component.translatable("container.enderchest")));
		player.awardStat(Stats.OPEN_ENDERCHEST);

	}

}

