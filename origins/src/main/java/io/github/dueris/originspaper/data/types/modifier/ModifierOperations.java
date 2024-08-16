package io.github.dueris.originspaper.data.types.modifier;

import io.github.dueris.calio.registry.impl.CalioRegistry;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.registry.Registries;

import java.util.Locale;

public final class ModifierOperations {

	public static void registerAll() {
		for (ModifierOperation operation : ModifierOperation.values()) {
			CalioRegistry.INSTANCE.retrieve(Registries.MODIFIER_OPERATION).register(
					operation, OriginsPaper.apoliIdentifier(operation.toString().toLowerCase(Locale.ROOT))
			);
		}
	}
}
