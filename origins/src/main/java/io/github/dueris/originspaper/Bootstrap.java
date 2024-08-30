package io.github.dueris.originspaper;

import io.github.dueris.originspaper.registry.nms.OriginLootCondition;
import io.github.dueris.originspaper.registry.nms.PowerLootCondition;
import io.github.dueris.originspaper.util.WrappedBootstrapContext;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class Bootstrap implements PluginBootstrap {

	@Override
	@SuppressWarnings("unchecked")
	public void bootstrap(@Nullable BootstrapContext bootContext) {
		WrappedBootstrapContext context = new WrappedBootstrapContext(bootContext);
		context.registerBuiltin(BuiltInRegistries.LOOT_CONDITION_TYPE, ResourceLocation.fromNamespaceAndPath("apoli", "power"), PowerLootCondition.TYPE);
		context.registerBuiltin(BuiltInRegistries.LOOT_CONDITION_TYPE, ResourceLocation.fromNamespaceAndPath("origins", "origin"), OriginLootCondition.TYPE);

		try {
//			((PluginProvider)((LinkedList)LaunchEntryPointHandler.INSTANCE.get(Entrypoint.BOOTSTRAPPER).getRegisteredProviders()).getFirst()).createInstance();
			OriginsPaper.init(context);
		} catch (Throwable e) {
			throw new RuntimeException("An error occurred when loading OriginsPaper!", e);
		}
	}

}
