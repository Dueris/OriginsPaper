package me.dueris.calio.parse.reader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DirectoryReader implements FileReader {
	private final Path directory;

	public DirectoryReader(Path directory) {
		this.directory = directory;
	}

	@Override
	public List<String> listFiles() throws IOException {
		List<String> fileList = new ArrayList<>();
		Files.walk(directory).forEach(path -> {
			if (Files.isRegularFile(path)) {
				fileList.add(directory.relativize(path).toString());
			}
		});
		return fileList;
	}

	@Override
	public InputStream getFileStream(String name) throws IOException {
		Path filePath = directory.resolve(name);
		return Files.newInputStream(filePath);
	}
}
