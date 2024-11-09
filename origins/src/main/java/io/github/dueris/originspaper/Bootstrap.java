package io.github.dueris.originspaper;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import me.dueris.eclipse.ignite.IgniteBootstrap;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class Bootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(@NotNull BootstrapContext bootContext) {
		try {
			if (!IgniteBootstrap.BOOTED.get()) return;
			OriginsPaper.bootstrap(bootContext);
		} catch (Throwable e) {
			throw new RuntimeException("An error occurred when loading OriginsPaper!", e);
		}
	}

}
