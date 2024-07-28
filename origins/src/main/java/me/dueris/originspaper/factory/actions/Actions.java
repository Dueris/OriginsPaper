package me.dueris.originspaper.factory.actions;

import me.dueris.originspaper.factory.actions.types.BiEntityActions;
import me.dueris.originspaper.factory.actions.types.BlockActions;
import me.dueris.originspaper.factory.actions.types.EntityActions;
import me.dueris.originspaper.factory.actions.types.ItemActions;

public class Actions {

	public static void registerAll() {
		BiEntityActions.registerAll();
		BlockActions.registerAll();
		EntityActions.registerAll();
		ItemActions.registerAll();
	}
}
