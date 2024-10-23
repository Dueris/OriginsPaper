package io.github.dueris.originspaper.action.type.entity;

import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class CraftingTableActionType {

	public static void action(Entity entity) {

		if (!(entity instanceof Player player)) {
			return;
		}

		player.getBukkitEntity().openWorkbench(null, true);
		player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);

	}

}
