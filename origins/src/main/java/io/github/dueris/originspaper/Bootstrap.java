package io.github.dueris.originspaper;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class Bootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(@NotNull BootstrapContext bootContext) {
		try {
			OriginsPaper.init(bootContext);
		} catch (Throwable e) {
			throw new RuntimeException("An error occurred when loading OriginsPaper!", e);
		}
	}

}
