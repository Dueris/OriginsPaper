package me.dueris.calio.parse.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FileReader {
	List<String> listFiles() throws IOException;
	InputStream getFileStream(String name) throws IOException;
}
