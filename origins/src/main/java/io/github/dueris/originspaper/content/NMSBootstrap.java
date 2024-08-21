package io.github.dueris.originspaper.content;

import io.github.dueris.originspaper.registry.nms.OriginLootCondition;
import io.github.dueris.originspaper.registry.nms.PowerLootCondition;
import io.github.dueris.originspaper.util.WrappedBootstrapContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class NMSBootstrap {
//	public static EntityType<?> ENDERIAN_PEARL_ENTITYTYPE = EntityType.Builder.of(ThrownEnderpearl::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("enderian_pearl");

	public static void bootstrap(@NotNull WrappedBootstrapContext context) {
		context.registerBuiltin(BuiltInRegistries.LOOT_CONDITION_TYPE, ResourceLocation.fromNamespaceAndPath("apoli", "power"), PowerLootCondition.TYPE);
		context.registerBuiltin(BuiltInRegistries.LOOT_CONDITION_TYPE, ResourceLocation.fromNamespaceAndPath("origins", "origin"), OriginLootCondition.TYPE);
		//context.registerBuiltin(BuiltInRegistries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("origins", "enderian_pearl"), ENDERIAN_PEARL_ENTITYTYPE);
	}
}
