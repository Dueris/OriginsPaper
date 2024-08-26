package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import org.jetbrains.annotations.NotNull;

public class EnderChestAction {

	private static final Component TITLE = Component.translatable("container.enderchest");

	public static void action(SerializableData.Instance data, Entity entity) {
		if (!(entity instanceof Player player)) return;

		PlayerEnderChestContainer enderChestContainer = player.getEnderChestInventory();

		player.openMenu(
			new SimpleMenuProvider((i, inventory, _player) ->
				ChestMenu.threeRows(i, inventory, enderChestContainer),
				TITLE
			)
		);

		player.awardStat(Stats.OPEN_ENDERCHEST);
	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(OriginsPaper.apoliIdentifier("ender_chest"),
			SerializableData.serializableData(),
			EnderChestAction::action
		);
	}
}
