package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class CraftingTableAction {

	public static void action(DeserializedFactoryJson data, Entity entity) {

		if (!(entity instanceof Player playerEntity)) {
			return;
		}

		playerEntity.getBukkitEntity().openWorkbench(null, true);
		playerEntity.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);

	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("crafting_table"),
			InstanceDefiner.instanceDefiner(),
			CraftingTableAction::action
		);
	}
}
