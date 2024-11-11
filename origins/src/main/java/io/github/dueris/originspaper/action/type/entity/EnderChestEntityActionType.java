package io.github.dueris.originspaper.action.type.entity;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.EntityActionTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import org.jetbrains.annotations.NotNull;

public class EnderChestEntityActionType extends EntityActionType {

    @Override
    protected void execute(Entity entity) {

        if (!(entity instanceof Player player)) {
            return;
        }

        PlayerEnderChestContainer enderChestContainer = player.getEnderChestInventory();
        MenuConstructor handlerFactory = (syncId, playerInventory, _player) -> ChestMenu.threeRows(syncId, playerInventory, enderChestContainer);

        player.openMenu(new SimpleMenuProvider(handlerFactory, Component.translatable("container.enderchest")));
        player.awardStat(Stats.OPEN_ENDERCHEST);

    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return EntityActionTypes.ENDER_CHEST;
    }

}
