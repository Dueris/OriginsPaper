package me.dueris.genesismc.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static me.dueris.genesismc.core.GenesisMC.getPlugin;

public class BukkitUtils {

    public static void downloadFileToDirFromResource(String childPathFromOverworld, String resourceLocation){
        File datapackFile = new File(Bukkit.getWorlds().get(0).getName(), childPathFromOverworld);
        if (!datapackFile.exists()) {
            InputStream resource = getPlugin().getResource(resourceLocation);
            if (resource != null) {
                try (OutputStream outputStream = new FileOutputStream(datapackFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = resource.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public static void downloadFileFromURL(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        try (BufferedInputStream in = new BufferedInputStream(url.openStream())) {
            Path savePath = Path.of(System.getProperty("user.home"), "Downloads");
            Files.createDirectories(savePath);

            String fileName = url.getFile().substring(url.getFile().lastIndexOf('/') + 1);
            Path filePath = savePath.resolve(fileName);
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void downloadFileFromURL(String fileUrl, String saveDirectory) throws IOException {
        URL url = new URL(fileUrl);
        try (BufferedInputStream in = new BufferedInputStream(url.openStream())) {
            Path savePath = Path.of(saveDirectory);
            Files.createDirectories(savePath);

            Path filePath = savePath.resolve(getFileNameFromUrl(fileUrl));
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void downloadFileFromURL(String fileUrl, String saveDirectory, String fileName) throws IOException {
        URL url = new URL(fileUrl);
        try (BufferedInputStream in = new BufferedInputStream(url.openStream())) {
            Path savePath = Path.of(saveDirectory);
            Files.createDirectories(savePath);

            Path filePath = savePath.resolve(fileName);
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static String getFileNameFromUrl(String fileUrl) {
        String[] segments = fileUrl.split("/");
        return segments[segments.length - 1];
    }

    public static void printValues(ConfigurationSection section, String indent) {
        StringBuilder values = new StringBuilder();

        for (String key : section.getKeys(false)) {
            String path = section.getCurrentPath() + "|" + key;
            Object value = section.get(key);

            if (value instanceof ConfigurationSection) {
                // If the value is another section, recursively print its values
                ConfigurationSection subsection = (ConfigurationSection) value;
                printValues(subsection, indent + "  ");
            } else {
                // Append the key and value to the StringBuilder
                values.append(indent).append(path).append(": ").append(value).append("  ");
            }
        }

        // Print the concatenated values
        Bukkit.getLogger().info(values.toString());
    }

}
