package me.dueris.genesismc;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import me.dueris.genesismc.content.enchantment.WaterProtectionEnchantment;
import me.dueris.genesismc.registry.nms.OriginLootCondition;
import me.dueris.genesismc.registry.nms.PowerLootCondition;
import me.dueris.genesismc.util.Utils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Bootstrap implements PluginBootstrap {
    public static WaterProtectionEnchantment waterProtection;
    public static ArrayList<String> oldDV = new ArrayList<>();

    static {
        oldDV.add("OriginsGenesis");
        oldDV.add("Origins-Genesis");
        oldDV.add("Origins-GenesisMC");
        oldDV.add("Origins-GenesisMC[0_2_2]");
        oldDV.add("Origins-GenesisMC[0_2_4]");
        oldDV.add("Origins-GenesisMC[0_2_6]");
    }

    public static Enchantment registerEnchantment(String name, Enchantment enchantment) {
        return Registry.register(BuiltInRegistries.ENCHANTMENT, new ResourceLocation("origins", name), enchantment);
    }

    public static void deleteDirectory(Path directory, boolean ignoreErrors) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                    .sorted((a, b) -> b.compareTo(a)) // Sort in reverse order for correct deletion
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                            Files.delete(path);
                        } catch (IOException e) {
                            if (!ignoreErrors) {
                                System.err.println("Error deleting: " + path + e);
                            }
                        }
                    });
        }
    }

    public static void copyOriginDatapack(Path datapackPath) {
        for (String string : oldDV) {
            if (Files.exists(datapackPath)) {
                String path = Path.of(datapackPath + File.separator + string).toAbsolutePath().toString();
                try {
                    deleteDirectory(Path.of(path), true);
                } catch (IOException e) {

                }
            } else {
                File file = new File(datapackPath.toAbsolutePath().toString());
                file.mkdirs();
                copyOriginDatapack(datapackPath);
            }
        }
        try {
            CodeSource src = Utils.class.getProtectionDomain().getCodeSource();
            URL jar = src.getLocation();
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            while (true) {
                ZipEntry entry = zip.getNextEntry();
                if (entry == null)
                    break;
                String name = entry.getName();

                if (!name.startsWith("datapack/")) continue;
                if (name.startsWith("datapack/builtin")) continue;
                if (FilenameUtils.getExtension(name).equals("zip")) continue;
                if (name.equals("datapack/")) continue;

                name = name.substring(9);
                File file = new File(datapackPath.toAbsolutePath().toString().replace(".\\", "") + File.separator + name);
                if (!file.getName().contains(".")) {
                    Files.createDirectory(Path.of(file.getAbsolutePath()));
                    continue;
                }

                // Ensure parent directory exists
                File parentDir = file.getParentFile();
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                }

                // Copy PNG files
                if (FilenameUtils.getExtension(name).equalsIgnoreCase("png")) {
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zip.read(buffer)) > 0) {
                            bos.write(buffer, 0, len);
                        }
                    }
                } else { // Copy non-PNG files as text
                    Files.writeString(Path.of(file.getAbsolutePath()), new String(zip.readAllBytes()));
                }
            }
            zip.close();
        } catch (Exception e) {
            // e.printStackTrace(); // Print stack trace for debugging // I changed my mind
        }

    }

    public static String levelNameProp() {
        Path propPath = Paths.get("server.properties");
        if (propPath.toFile().exists()) {
            Properties properties = new Properties();
            try (FileInputStream input = new FileInputStream(propPath.toFile())) {
                properties.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(properties.keySet());
            return properties.getProperty("level-name", "world");
        } else {
            return "world";
        }
    }

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        try {
            File datapackDir = new File(this.parseDatapackPath(context));
            copyOriginDatapack(datapackDir.toPath());
        } catch (Exception e) {
            // ignore
        }
        // hurt by water damage type
        EquipmentSlot[] slots = {EquipmentSlot.CHEST, EquipmentSlot.FEET, EquipmentSlot.HEAD, EquipmentSlot.LEGS};
        WaterProtectionEnchantment waterProtection = new WaterProtectionEnchantment(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR, slots);
        registerEnchantment("water_protection", waterProtection);
        Bootstrap.waterProtection = waterProtection;
        Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, new ResourceLocation("apoli", "power"), PowerLootCondition.TYPE);
        Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, new ResourceLocation("origins", "origin"), OriginLootCondition.TYPE);
    }

    public String parseDatapackPath(BootstrapContext context) {
        try {
            org.bukkit.configuration.file.YamlConfiguration bukkitConfiguration = YamlConfiguration.loadConfiguration(Paths.get("bukkit.yml").toFile());
            File container;
            container = new File(bukkitConfiguration.getString("settings.world-container", "."));
            String s = Optional.ofNullable(
                    levelNameProp()
            ).orElseGet(() -> {
                return "world";
            });
            Path datapackFolder = Paths.get(container.getAbsolutePath() + File.separator + s + File.separator + "datapacks");
            return datapackFolder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
