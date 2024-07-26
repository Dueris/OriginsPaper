package io.github.dueris.calio.parser.reader;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileSystemReader {

	public static String accessFileContent(Path filePath) throws IOException {
		return Files.readString(filePath, StandardCharsets.UTF_8);
	}

	public static @NotNull String readZipContent(Path zipPath, ZipEntry entry) throws IOException {
		try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
			if (entry != null) {
				try (InputStream inputStream = zipFile.getInputStream(entry)) {
					return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
				}
			}
		}
		return "{}";
	}

	public static Set<Path> collectFiles(Path directory) throws IOException {
		Set<Path> paths = new HashSet<>();
		try (Stream<Path> filePaths = Files.walk(directory)) {
			paths = filePaths.filter(Files::isRegularFile).collect(Collectors.toUnmodifiableSet());
		}
		return paths;
	}

	public static void processDatapacks(Path directory, BiConsumer<String, String> fileContentConsumer) throws IOException {
		Set<Path> paths = collectFiles(directory);

		for (Path filePath : paths) {
			boolean isZipBased = filePath.toString().endsWith(".zip") || filePath.toString().endsWith(".jar");
			if (!isZipBased && filePath.toString().endsWith(".json")) {
				fileContentConsumer.accept(filePath.toString(), accessFileContent(filePath));
			} else if (isZipBased) {
				try (ZipFile file = new ZipFile(filePath.toFile())) {
					file.stream().toList().forEach(entry -> {
						if (!entry.isDirectory() && entry.getName().endsWith(".json")) {
							try {
								fileContentConsumer.accept(entry.getName(), readZipContent(filePath, entry));
							} catch (IOException e) {
								throw new RuntimeException("Unable to read entry contents from zip file : " + filePath, e);
							}
						}
					});
				}
			}
		}
	}
}
