package io.github.dueris.originspaper;

import io.github.dueris.eclipse.api.entrypoint.BootstrapInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap implements BootstrapInitializer {

	private static final Logger log = LogManager.getLogger(Bootstrap.class);

	@Override
	public void onInitializeBootstrap(BootstrapContext bootstrapContext) {
		// Eclipse entrypoint.
		OriginsPaper.initialize(bootstrapContext);
	}
}
