package io.github.dueris.originspaper.mixin;

import com.google.common.collect.ImmutableSet;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.access.AvailablePackSourceRetriever;
import io.github.dueris.originspaper.data.pack.OriginPackRepositorySource;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.*;
import net.minecraft.world.level.validation.DirectoryValidator;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Mixin(PackRepository.class)
public class PackRepositoryMixin implements AvailablePackSourceRetriever {

	@Shadow
	private Map<String, Pack> available;

	@Unique
	private static @NotNull LinkedHashSet<RepositorySource> originspaper$fixupSourcesOrder(RepositorySource pluginSource, @NotNull Set<RepositorySource> original) {
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

	@ModifyExpressionValue(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableSet;copyOf([Ljava/lang/Object;)Lcom/google/common/collect/ImmutableSet;"))
	public ImmutableSet<RepositorySource> injectOriginsPack(@NotNull ImmutableSet<RepositorySource> original) {
		RepositorySource toAdd = null;

		for (RepositorySource source : original) {
			if (source instanceof FolderRepositorySource folderRepositorySource) {
				try {
					Field folder = FolderRepositorySource.class.getDeclaredField("folder");
					Field validator = FolderRepositorySource.class.getDeclaredField("validator");
					folder.setAccessible(true);
					validator.setAccessible(true);

					Path datapackFolder = (Path) folder.get(folderRepositorySource);
					toAdd = new OriginPackRepositorySource(Paths.get("plugins/"), PackType.SERVER_DATA, PackSource.WORLD, (DirectoryValidator) validator.get(folderRepositorySource), OriginsPaper.jarFile.getFileName().toString());
				} catch (IllegalAccessException | NoSuchFieldException e) {
					throw new RuntimeException(e);
				}
			}
		}

		Set<RepositorySource> sources = new LinkedHashSet<>(original);
		if (!sources.stream().map(Object::getClass).toList().contains(OriginPackRepositorySource.class)) {
			sources = originspaper$fixupSourcesOrder(toAdd, sources);
		}

		return ImmutableSet.copyOf(sources);
	}

	@Override
	public LinkedHashMap<String, Pack> originspaper$getAvailable() {
		Map<String, Pack> current = this.available;

		LinkedHashMap<String, Pack> orderedMap = new LinkedHashMap<>();

		current.entrySet().stream()
			.filter(entry -> entry.getKey().startsWith("origins/"))
			.findFirst()
			.ifPresent(entry -> orderedMap.put(entry.getKey(), entry.getValue()));

		current.entrySet().stream()
			.filter(entry -> !entry.getKey().startsWith("origins/"))
			.forEach(entry -> orderedMap.put(entry.getKey(), entry.getValue()));

		return orderedMap;
	}
}
