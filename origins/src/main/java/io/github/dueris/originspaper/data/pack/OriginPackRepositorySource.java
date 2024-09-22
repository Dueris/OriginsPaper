package io.github.dueris.originspaper.data.pack;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.linkfs.LinkFileSystem;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackDetector;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.level.validation.ContentValidationException;
import net.minecraft.world.level.validation.DirectoryValidator;
import net.minecraft.world.level.validation.ForbiddenSymlinkInfo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class OriginPackRepositorySource implements RepositorySource {
	static final Logger LOGGER = LogUtils.getLogger();
	private static final PackSelectionConfig DISCOVERED_PACK_SELECTION_CONFIG = new PackSelectionConfig(false, Pack.Position.TOP, false);
	private final Path folder;
	private final PackType packType;
	private final PackSource packSource;
	private final DirectoryValidator validator;
	private final String fileName;

	public OriginPackRepositorySource(Path packsDir, PackType type, PackSource source, DirectoryValidator symlinkFinder, String fileName) {
		this.folder = packsDir;
		this.packType = type;
		this.packSource = source;
		this.validator = symlinkFinder;
		this.fileName = fileName;
	}

	private static @NotNull String nameFromPath(@NotNull Path path) {
		return path.getFileName().toString();
	}

	public static void discoverPacks(Path path, DirectoryValidator symlinkFinder, BiConsumer<Path, Pack.ResourcesSupplier> callback, String fileName) throws IOException {
		OriginPackRepositorySource.FolderPackDetector folderPackDetector = new OriginPackRepositorySource.FolderPackDetector(symlinkFinder, fileName);

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
			for (Path path2 : directoryStream) {
				try {
					List<ForbiddenSymlinkInfo> list = new LinkedList<>();
					Pack.ResourcesSupplier resourcesSupplier = folderPackDetector.detectPackResources(path2, list);
					if (!list.isEmpty()) {
						LOGGER.warn("Ignoring potential pack entry: {}", ContentValidationException.getMessage(path2, list));
					} else if (resourcesSupplier != null) {
						callback.accept(path2, resourcesSupplier);
					}
				} catch (IOException var10) {
					LOGGER.warn("Failed to read properties of '{}', ignoring", path2, var10);
				}
			}
		}
	}

	@Override
	public void loadPacks(@NotNull Consumer<Pack> profileAdder) {
		try {
			discoverPacks(this.folder, this.validator, (path, packFactory) -> {
				PackLocationInfo packLocationInfo = this.createDiscoveredFilePackInfo(path);
				Pack pack = Pack.readMetaAndCreate(packLocationInfo, packFactory, this.packType, DISCOVERED_PACK_SELECTION_CONFIG);
				if (pack != null) {
					profileAdder.accept(pack);
				}
			}, this.fileName);
		} catch (IOException var3) {
			LOGGER.warn("Failed to list packs in {}", this.folder, var3);
		}
	}

	private @NotNull PackLocationInfo createDiscoveredFilePackInfo(Path path) {
		String string = nameFromPath(path);
		return new PackLocationInfo("origins/" + string, Component.literal(string), this.packSource, Optional.empty());
	}

	static class FolderPackDetector extends PackDetector<Pack.ResourcesSupplier> {
		private final DirectoryValidator validator;
		private final String fileName;

		protected FolderPackDetector(DirectoryValidator symlinkFinder, String fileName) {
			super(symlinkFinder);
			this.validator = symlinkFinder;
			this.fileName = fileName;
		}

		@Nullable
		@Override
		protected Pack.ResourcesSupplier createZipPack(@NotNull Path path) {
			FileSystem fileSystem = path.getFileSystem();
			if (fileSystem != FileSystems.getDefault() && !(fileSystem instanceof LinkFileSystem)) {
				OriginPackRepositorySource.LOGGER.info("Can't open pack archive at {}", path);
				return null;
			} else {
				return new FilePackResources.FileResourcesSupplier(path);
			}
		}

		@Override
		protected Pack.ResourcesSupplier createDirectoryPack(@NotNull Path path) {
			return new PathPackResources.PathResourcesSupplier(path);
		}

		@Override
		public Pack.ResourcesSupplier detectPackResources(@NotNull Path path, @NotNull List<ForbiddenSymlinkInfo> foundSymlinks) throws IOException {
			Path path2 = path;

			BasicFileAttributes basicFileAttributes;
			try {
				basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
			} catch (NoSuchFileException var6) {
				return null;
			}

			if (basicFileAttributes.isSymbolicLink()) {
				this.validator.validateSymlink(path, foundSymlinks);
				if (!foundSymlinks.isEmpty()) {
					return null;
				}

				path2 = Files.readSymbolicLink(path);
				basicFileAttributes = Files.readAttributes(path2, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
			}

			if (basicFileAttributes.isDirectory()) {
				this.validator.validateKnownDirectory(path2, foundSymlinks);
				if (!foundSymlinks.isEmpty()) {
					return null;
				} else {
					return !Files.isRegularFile(path2.resolve("pack.mcmeta")) ? null : this.createDirectoryPack(path2);
				}
			} else {
				return basicFileAttributes.isRegularFile() && path2.getFileName().toString().equalsIgnoreCase(this.fileName) ? this.createZipPack(path2) : null;
			}
		}
	}
}
