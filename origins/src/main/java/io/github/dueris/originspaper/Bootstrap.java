package io.github.dueris.originspaper;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import me.dueris.eclipse.ignite.IgniteBootstrap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class Bootstrap implements PluginBootstrap {

	private static final Logger log = LogManager.getLogger(Bootstrap.class);

	private static boolean igniteBooted() {
		try {
			Class.forName("me.dueris.eclipse.ignite.IgniteBootstrap");
			return IgniteBootstrap.BOOTED.get();
		} catch (ClassNotFoundException e) {
			log.error("Eclipse was not found in classpath! Please install from modrinth : {}", "https://modrinth.com/plugin/eclipse-mixin");
			return false;
		}
	}

	@Override
	public void bootstrap(@NotNull BootstrapContext bootContext) {
		try {
			if (!igniteBooted()) return;
			OriginsPaper.bootstrap(bootContext);
		} catch (Throwable e) {
			throw new RuntimeException("An error occurred when loading OriginsPaper!", e);
		}
	}

}
