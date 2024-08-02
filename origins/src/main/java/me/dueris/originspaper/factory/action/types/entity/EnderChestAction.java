package me.dueris.originspaper.factory.action.types.entity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.ActionFactory;
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

	public static void action(DeserializedFactoryJson data, Entity entity) {
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

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("ender_chest"),
			InstanceDefiner.instanceDefiner(),
			EnderChestAction::action
		);
	}
}
