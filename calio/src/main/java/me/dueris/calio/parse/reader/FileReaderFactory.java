package me.dueris.calio.parse.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipFile;

public class FileReaderFactory {
	public static FileReader createFileReader(Path path) throws IOException {
		if (Files.isDirectory(path)) {
			return new DirectoryReader(path);
		} else if (Files.isRegularFile(path) && (path.toString().endsWith(".zip") || path.toString().endsWith(".jar"))) {
			return new ZipFileReader(new ZipFile(path.toFile()));
		} else {
			new IllegalArgumentException("Unsupported file type: " + path).printStackTrace();
		}
		return null;
	}
}
