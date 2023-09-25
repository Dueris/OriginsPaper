package me.dueris.genesismc.utils;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class BukkitUtils {

    public static void deleteDirectory(Path directory, boolean ignoreErrors) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                    .sorted((a, b) -> b.compareTo(a)) // Sort in reverse order for correct deletion
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                            Files.delete(path);
                        } catch (IOException e) {
                            if(!ignoreErrors){
                                System.err.println("Error deleting: " + path + e);
                            }
                        }
                    });
        }
    }
    public static ArrayList<String> oldDV = new ArrayList<>();
    static {
        oldDV.add("OriginsGenesis");
        oldDV.add("Origins-Genesis");
        oldDV.add("Origins-GenesisMC");
    }

    public static void CopyOriginDatapack() {
        for(String string : oldDV){
            if (Files.exists(Path.of(Bukkit.getWorlds().get(0).getName() + File.separator + "datapacks" + File.separator + string))) {
                String path = Path.of(Bukkit.getWorlds().get(0).getName() + File.separator + "datapacks" + File.separator + string).toAbsolutePath().toString();
                try {
                    deleteDirectory(Path.of(path), false);
                } catch (IOException e) {
                    //SDGFLKSDJFGO
                }
            }
        }
        try {
            CodeSource src = BukkitUtils.class.getProtectionDomain().getCodeSource();
            URL jar = src.getLocation();
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            while (true) {
                ZipEntry e = zip.getNextEntry();
                if (e == null)
                    break;
                String name = e.getName();

                if (!name.startsWith("datapacks/")) continue;
                if (FilenameUtils.getExtension(name).equals("zip")) continue;
                if (name.equals("datapacks/")) continue;

                name = name.substring(10);
                File file = new File(Path.of(Bukkit.getWorlds().get(0).getName() + File.separator + "datapacks" + File.separator + name).toAbsolutePath().toString());
                if (!file.getName().contains(".")) {
                    Files.createDirectory(Path.of(file.getAbsolutePath()));
                    continue;
                }
                Files.writeString(Path.of(file.getAbsolutePath()), new String(zip.readAllBytes()));
            }
            zip.close();
        } catch (Exception e) {
            //rip thing still there
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

            if (value instanceof ConfigurationSection subsection) {
                // If the value is another section, recursively print its values
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
