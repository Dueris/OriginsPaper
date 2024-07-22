package me.dueris.calio.parse.reader;

import me.dueris.calio.CraftCalio;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipFile;

public class FileReaderFactory {

	public static @Nullable FileReader createFileReader(Path path) throws IOException {
		if (Files.isDirectory(path)) {
			return new DirectoryReader(path);
		} else if (!Files.isRegularFile(path) || !path.toString().endsWith(".zip") && !path.toString().endsWith(".jar")) {
			CraftCalio.INSTANCE.getLogger().severe("Unknown file type! : " + path.toFile().getName());
			return null;
		} else {
			return new ZipFileReader(new ZipFile(path.toFile()));
		}
	}
}
