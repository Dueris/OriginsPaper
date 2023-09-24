package me.dueris.genesismc.mixin.mixins;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.mixin.GenesisMixin;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

@Mixin(Main.class)
public class StartupHookTestMixin {
    @Inject(method = "main", at = @At("RETURN"))
    private static void injectT(String[] args, CallbackInfo ci){
        //start copying the jar

        Path cdir = Path.of(Bukkit.getServer().getPluginsFolder().toPath() + File.separator + ".." + File.separator + "mods");

        try (Stream<Path> files = Files.list(cdir)) {
            Path jarFile = files
                    .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".jar"))
                    .findFirst()
                    .orElse(null);

            if (jarFile != null) {
                JarFile jar = new JarFile(jarFile.toFile());

                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().startsWith("standalone") && entry.getName().endsWith(".jar")) {
                        Path pluginsDirectory = Bukkit.getServer().getPluginsFolder().toPath();

                        if (!Files.exists(pluginsDirectory)) {
                            Files.createDirectories(pluginsDirectory);
                        }

                        Path destinationPath = pluginsDirectory.resolve("genesis-standalone-{version}.jar".replace("{version}", GenesisMixin.version));
                        Files.copy(jar.getInputStream(entry), destinationPath, StandardCopyOption.REPLACE_EXISTING);

                        System.out.println("JAR file copied successfully to: " + destinationPath.toString());
                        break; // Stop after the first matching entry is found
                    }
                }

                jar.close();
            } else {
                System.err.println("No JAR file found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //run the rest of the mc code...
    }
}
