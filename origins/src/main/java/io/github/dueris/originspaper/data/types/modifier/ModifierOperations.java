package io.github.dueris.originspaper.data.types.modifier;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;

import java.util.Locale;

public final class ModifierOperations {

	public static void registerAll() {
		for (ModifierOperation operation : ModifierOperation.values()) {
			Registry.register(ApoliRegistries.MODIFIER_OPERATION, OriginsPaper.apoliIdentifier(operation.toString().toLowerCase(Locale.ROOT)), operation);
		}
	}
}
