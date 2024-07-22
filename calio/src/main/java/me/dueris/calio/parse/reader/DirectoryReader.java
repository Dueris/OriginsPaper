package me.dueris.calio.parse.reader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DirectoryReader implements FileReader {
	private final Path directory;

	public DirectoryReader(Path directory) {
		this.directory = directory;
	}

	@Override
	public List<String> listFiles() throws IOException {
		List<String> fileList = new ArrayList<>();
		if (!Files.exists(this.directory.resolve("pack.mcmeta"))) {
			return new ArrayList<>();
		} else {
			Files.walk(this.directory).forEach(path -> {
				if (Files.isRegularFile(path)) {
					fileList.add(this.directory.relativize(path).toString());
				}
			});
			return fileList;
		}
	}

	@Override
	public InputStream getFileStream(String name) throws IOException {
		Path filePath = this.directory.resolve(name);
		return Files.newInputStream(filePath);
	}
}
