package me.dueris.originspaper.content;

import me.dueris.originspaper.registry.nms.OriginLootCondition;
import me.dueris.originspaper.registry.nms.PowerLootCondition;
import me.dueris.originspaper.util.WrappedBootstrapContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class NMSBootstrap {
	public static void bootstrap(@NotNull WrappedBootstrapContext context) {
		context.registerBuiltin(BuiltInRegistries.LOOT_CONDITION_TYPE, ResourceLocation.fromNamespaceAndPath("apoli", "power"), PowerLootCondition.TYPE);
		context.registerBuiltin(BuiltInRegistries.LOOT_CONDITION_TYPE, ResourceLocation.fromNamespaceAndPath("origins", "origin"), OriginLootCondition.TYPE);
	}
}
