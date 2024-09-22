package io.github.dueris.originspaper;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import me.dueris.eclipse.BootstrapEntrypoint;
import org.jetbrains.annotations.Nullable;

public class Bootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(@Nullable BootstrapContext bootContext) {
		try {
			bootContext.getLogger().warn("OriginsPaper bundles version '{}' of the Eclipse MIXIN plugin. If this pluign is on the server as an external plugin, please remove it to avoid errors :)", "v1.1.0");
			BootstrapEntrypoint entrypoint = new BootstrapEntrypoint();
			entrypoint.bootstrap(bootContext);
			OriginsPaper.init(bootContext);
		} catch (Throwable e) {
			throw new RuntimeException("An error occurred when loading OriginsPaper!", e);
		}
	}

}
