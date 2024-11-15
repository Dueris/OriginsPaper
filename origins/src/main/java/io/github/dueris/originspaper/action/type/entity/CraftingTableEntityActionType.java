package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.access.ScreenHandlerUsabilityOverride;
import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuConstructor;
import org.jetbrains.annotations.NotNull;

public class CraftingTableEntityActionType extends EntityActionType {

	@Override
	protected void execute(Entity entity) {

		if (!(entity instanceof Player player)) {
			return;
		}

		MenuConstructor handlerFactory = (syncId, playerInventory, _player) -> {

			CraftingMenu craftingScreenHandler = new CraftingMenu(syncId, playerInventory, ContainerLevelAccess.create(player.level(), player.blockPosition()));
			((ScreenHandlerUsabilityOverride) craftingScreenHandler).apoli$canUse(true);

			return craftingScreenHandler;

		};

		player.openMenu(new SimpleMenuProvider(handlerFactory, Component.translatable("container.crafting")));
		player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);

	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return EntityActionTypes.CRAFTING_TABLE;
	}

}
