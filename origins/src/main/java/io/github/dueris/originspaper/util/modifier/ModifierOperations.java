package io.github.dueris.originspaper.util.modifier;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;

import java.util.Locale;

public final class ModifierOperations {

	public static void register() {
		for (ModifierOperation operation : ModifierOperation.values()) {
			Registry.register(ApoliRegistries.MODIFIER_OPERATION, OriginsPaper.apoliIdentifier(operation.toString().toLowerCase(Locale.ROOT)), operation);
		}
	}
}
