package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.pack.PluginRepositorySource;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.*;
import net.minecraft.world.level.validation.DirectoryValidator;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unchecked")
@Mixin(PackRepository.class)
public class PackRepositoryMixin {
	static final AtomicReference<Path> DATAPACK_PATH = new AtomicReference<>();

	@Inject(method = "reload", locator = At.Value.HEAD)
	public static void origins$loadPack(PackRepository instance, CallbackInfo info) {
		RepositorySource toAdd = null;

		for (RepositorySource source : getSources(instance)) {
			if (source instanceof FolderRepositorySource folderRepositorySource) {
				try {
					Field folder = FolderRepositorySource.class.getDeclaredField("folder");
					Field validator = FolderRepositorySource.class.getDeclaredField("validator");
					folder.setAccessible(true);
					validator.setAccessible(true);

					Path datapackFolder = (Path) folder.get(folderRepositorySource);
					toAdd = new PluginRepositorySource(Paths.get("plugins/"), PackType.SERVER_DATA, PackSource.WORLD, (DirectoryValidator) validator.get(folderRepositorySource), OriginsPaper.jarFile.getFileName().toString());

					DATAPACK_PATH.set(datapackFolder);
				} catch (IllegalAccessException | NoSuchFieldException e) {
					throw new RuntimeException(e);
				}
			}
		}

		Set<RepositorySource> sources = new LinkedHashSet<>(getSources(instance));
		if (!sources.stream().map(Object::getClass).toList().contains(PluginRepositorySource.class)) {
			sources = fixupSourcesOrder(toAdd, sources);
			try {
				Field sourceField = PackRepository.class.getDeclaredField("sources");
				sourceField.setAccessible(true);
				sourceField.set(instance, sources);
				OriginsPaper.LOGGER.info("Loaded PluginPackRepository in repository sources.");
			} catch (IllegalAccessException | NoSuchFieldException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static @NotNull LinkedHashSet<RepositorySource> fixupSourcesOrder(RepositorySource pluginSource, @NotNull Set<RepositorySource> original) {
		LinkedHashSet<RepositorySource> newSource = new LinkedHashSet<>();

		original.stream()
			.filter(source -> source instanceof ServerPacksSource)
			.findFirst()
			.ifPresent(newSource::add);

		newSource.add(pluginSource);

		original.stream()
			.filter(source -> source instanceof FolderRepositorySource)
			.findFirst()
			.ifPresent(newSource::add);

		return newSource;
	}

	public static Set<RepositorySource> getSources(PackRepository instance) {
		try {
			Field sources = PackRepository.class.getDeclaredField("sources");
			sources.setAccessible(true);
			return (Set<RepositorySource>) sources.get(instance);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	public static @NotNull LinkedHashMap<String, Pack> getAvailable(PackRepository instance) {
		try {
			Field available = PackRepository.class.getDeclaredField("available");
			available.setAccessible(true);

			Map<String, Pack> originalMap = (Map<String, Pack>) available.get(instance);

			LinkedHashMap<String, Pack> orderedMap = new LinkedHashMap<>();

			originalMap.entrySet().stream()
				.filter(entry -> entry.getKey().startsWith("origins/"))
				.findFirst()
				.ifPresent(entry -> orderedMap.put(entry.getKey(), entry.getValue()));

			originalMap.entrySet().stream()
				.filter(entry -> !entry.getKey().startsWith("origins/"))
				.forEach(entry -> orderedMap.put(entry.getKey(), entry.getValue()));

			return orderedMap;
		} catch (IllegalAccessException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
}
