package me.dueris.apoli;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class CraftApoli extends JavaPlugin {
    @Override
    public void onDisable() {
        super.onDisable();
        getLogger().info("CraftApoli shutting down...");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        getLogger().info("CraftApoli is ready to power up your game!");
    }

    @Override
    public @NotNull Logger getLogger() {
        return Logger.getLogger("CraftApoli");
    }
}
