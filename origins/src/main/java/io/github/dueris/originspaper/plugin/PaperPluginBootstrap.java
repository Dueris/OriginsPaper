package io.github.dueris.originspaper.plugin;

import io.github.dueris.originspaper.OriginsPaper;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class PaperPluginBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(@NotNull BootstrapContext bootstrapContext) {
		// Register commands via lifecycle event system.
		OriginsPaper.registerCommands(bootstrapContext);
	}
}
