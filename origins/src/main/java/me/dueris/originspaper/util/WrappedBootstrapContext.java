package me.dueris.originspaper.util;

import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import me.dueris.calio.registry.IRegistry;
import me.dueris.calio.registry.Registrar;
import me.dueris.calio.registry.RegistryKey;
import me.dueris.calio.registry.impl.CalioRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	private final BootstrapContext context;
	private final IRegistry registry;
	private final List<String> registryPointers = new CopyOnWriteArrayList<>();
	private final ConcurrentHashMap<Pair<ResourceKey, ResourceLocation>, JsonObject> registered = new ConcurrentHashMap<>();
	private final Logger LOGGER = LogManager.getLogger("ApoliBootstrapContext");

	public WrappedBootstrapContext(BootstrapContext context) {
		this.context = context;
		this.registry = CalioRegistry.INSTANCE;
	}

	public void createRegistry(RegistryKey key) {
		registry.create(key, new Registrar<>(key.type()));
	}

	public void createRegistries(RegistryKey... keys) {
		for (RegistryKey key : keys) {
			createRegistry(key);
		}
	}

	public void addDataDrivenPointer(ResourceKey<?> key) {
		registryPointers.add(key.location().getPath());
	}

	public void registerData(ResourceKey<?> key, JsonObject data, ResourceLocation location) {
		if (!registryPointers.contains(key.location().getPath())) {
			registryPointers.add(key.location().getPath());
		}

		LOGGER.log(Level.INFO, "Registered new data for location: {}", location.getPath());
		registered.put(
			new Pair<>(key, location),
			data
		);
	}

	public void initRegistries(Path datapackPath) {
		LOGGER.log(Level.INFO, "Creating data-driven registries...");
		File data = Arrays.stream(Arrays.stream(datapackPath.toFile().listFiles())
				.filter(Objects::nonNull)
				.filter(f -> {
					return f.getName().equalsIgnoreCase("datapack");
				}).filter(File::isDirectory)
				.findFirst().orElseThrow().listFiles()).filter(f -> {
				return f.getName().equalsIgnoreCase("data");
			}).filter(File::isDirectory)
			.findFirst().orElseThrow();

		for (Pair<ResourceKey, ResourceLocation> key : registered.keySet()) {
			String namespace = key.getSecond().getNamespace();
			String registryLocation = key.getFirst().location().getPath();
			File namespaceFile = new File(data, namespace);
			if (!namespaceFile.exists()) {
				namespaceFile.mkdirs();
			}

			File registryLocationFile = new File(namespaceFile, registryLocation);
			if (!registryLocationFile.exists()) {
				registryLocationFile.mkdirs();
			}

			File registryFile = new File(registryLocationFile, key.getSecond().getPath() + ".json");
			if (registryFile.exists()) {
				try {
					List<String> lines = Files.readLines(registryFile, StandardCharsets.UTF_8);
					String finishedLines = compileStrings(lines);
					if (registered.get(key).toString().equalsIgnoreCase(finishedLines)) {
						continue;
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			try {
				Files.write(registered.get(key).toString(), registryFile, StandardCharsets.UTF_8);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				LOGGER.log(Level.INFO, "Created registry entry ({}) for registry \"{}\"", key.getSecond().toString(), registryLocation);
			}
		}

		registered.clear();

	}

	public <T> void registerBuiltin(Registry<T> registry, ResourceLocation location, T type) {
		Registry.register(registry, location, type);
	}

	public IRegistry registry() {
		return registry;
	}

	public BootstrapContext context() {
		return context;
	}

	private String compileStrings(List<String> strings) {
		StringBuilder builder = new StringBuilder();
		strings.forEach(builder::append);
		return builder.toString();
	}
}
