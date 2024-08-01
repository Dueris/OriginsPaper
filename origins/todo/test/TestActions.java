package me.dueris.originspaper.factory.powers.test;

import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.ActionFactory;
import me.dueris.originspaper.registry.Registries;

public class TestActions {

	public static void registerTest() {
		OriginsPaper.getPlugin().registry.retrieve(Registries.ENTITY_ACTION).register(
			new ActionFactory<>(
				OriginsPaper.apoliIdentifier("test"),
				InstanceDefiner.instanceDefiner(),
				(data, entity) -> {

				}), OriginsPaper.apoliIdentifier("fs")
		);
	}
}
