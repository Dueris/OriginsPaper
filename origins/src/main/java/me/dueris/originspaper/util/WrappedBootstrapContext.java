package me.dueris.originspaper.util;

import com.google.common.io.Files;
import com.google.gson.JsonObject;
import io.github.dueris.calio.registry.IRegistry;
import io.github.dueris.calio.registry.Registrar;
import io.github.dueris.calio.registry.RegistryKey;
import io.github.dueris.calio.registry.impl.CalioRegistry;
import io.github.dueris.calio.util.holder.Pair;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class WrappedBootstrapContext {
	public final Logger LOGGER = LogManager.getLogger("ApoliBootstrapContext");
	private final BootstrapContext context;
	private final IRegistry registry;
	private final List<String> registryPointers = new CopyOnWriteArrayList<>();
	private final ConcurrentHashMap<Pair<ResourceKey<?>, ResourceLocation>, JsonObject> registered = new ConcurrentHashMap<>();

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

	public void addDataDrivenPointer(@NotNull ResourceKey<?> key) {
		this.registryPointers.add(key.location().getPath());
	}

	public void registerData(@NotNull ResourceKey<?> key, JsonObject data, ResourceLocation location) {
		if (!this.registryPointers.contains(key.location().getPath())) {
			this.registryPointers.add(key.location().getPath());
		}

		this.LOGGER.log(Level.INFO, "Registered new data for location: {}", location.getPath());
		this.registered.put(new Pair<>(key, location), data);
	}

	public void initRegistries(@NotNull Path datapackPath) {
		LOGGER.log(Level.INFO, "Creating data-driven registries...");
		File data = Arrays.stream(Objects.requireNonNull(Arrays.stream(datapackPath.toFile().listFiles())
				.filter(Objects::nonNull)
				.filter(f -> {
					return f.getName().equalsIgnoreCase("datapack");
				}).filter(File::isDirectory)
				.findFirst().orElseThrow().listFiles())).filter(f -> {
				return f.getName().equalsIgnoreCase("data");
			}).filter(File::isDirectory)
			.findFirst().orElseThrow();

		for (Pair<ResourceKey<?>, ResourceLocation> key : this.registered.keySet()) {
			String namespace = key.second().getNamespace();
			String registryLocation = key.first().location().getPath();
			File namespaceFile = new File(data, namespace);
			if (!namespaceFile.exists()) {
				namespaceFile.mkdirs();
			}

			File registryLocationFile = new File(namespaceFile, registryLocation);
			if (!registryLocationFile.exists()) {
				registryLocationFile.mkdirs();
			}

			File registryFile = new File(registryLocationFile, key.second().getPath() + ".json");
			if (registryFile.exists()) {
				try {
					List<String> lines = Files.readLines(registryFile, StandardCharsets.UTF_8);
					String finishedLines = this.compileStrings(lines);
					if (this.registered.get(key).toString().equalsIgnoreCase(finishedLines)) {
						continue;
					}
				} catch (IOException var18) {
					throw new RuntimeException(var18);
				}
			}

			try {
				Files.write(this.registered.get(key).toString(), registryFile, StandardCharsets.UTF_8);
			} catch (IOException var16) {
				throw new RuntimeException(var16);
			} finally {
				this.LOGGER.log(Level.INFO, "Created registry entry ({}) for registry \"{}\"", key.second().toString(), registryLocation);
			}
		}

		this.registered.clear();
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

	private @NotNull String compileStrings(@NotNull List<String> strings) {
		StringBuilder builder = new StringBuilder();
		strings.forEach(builder::append);
		return builder.toString();
	}
}
