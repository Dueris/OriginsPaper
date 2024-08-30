package io.github.dueris.originspaper.util;

import io.github.dueris.calio.registry.IRegistry;
import io.github.dueris.calio.registry.Registrar;
import io.github.dueris.calio.registry.RegistryKey;
import io.github.dueris.calio.registry.impl.CalioRegistry;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class WrappedBootstrapContext {
	public final Logger LOGGER = LogManager.getLogger("ApoliBootstrapContext");
	private final BootstrapContext context;
	private final IRegistry registry;

	public WrappedBootstrapContext(BootstrapContext context) {
		this.context = context;
		this.registry = CalioRegistry.INSTANCE;
	}

	public <T> void createRegistry(RegistryKey<T> key) {
		this.registry.create(key, new Registrar<T>(key.type()));
	}

	public void createRegistries(RegistryKey<?> @NotNull ... keys) {
		for (RegistryKey<?> key : keys) {
			this.createRegistry(key);
		}
	}

	public <T> void registerBuiltin(Registry<T> registry, ResourceLocation location, T type) {
		Registry.register(registry, location, type);
	}

	public IRegistry registry() {
		return this.registry;
	}

	public BootstrapContext context() {
		return this.context;
	}
}
