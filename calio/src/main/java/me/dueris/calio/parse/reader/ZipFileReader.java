package me.dueris.calio.parse.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileReader implements FileReader {
	private final ZipFile zipFile;

	public ZipFileReader(ZipFile zipFile) {
		this.zipFile = zipFile;
	}

	@Override
	public List<String> listFiles() {
		List<String> fileList = new ArrayList<>();
		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		boolean hasPackMcmeta = false;
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if (!entry.isDirectory() && "pack.mcmeta".equals(entry.getName())) {
				hasPackMcmeta = true;
				break;
			}
		}

		if (!hasPackMcmeta) {
			return new ArrayList<>();
		}

		entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if (!entry.isDirectory()) {
				fileList.add(entry.getName());
			}
		}

		return fileList;
	}

	@Override
	public InputStream getFileStream(String name) throws IOException {
		ZipEntry entry = zipFile.getEntry(name);
		if (entry == null) {
			throw new IOException("File not found in ZIP: " + name);
		}
		return zipFile.getInputStream(entry);
	}
}
