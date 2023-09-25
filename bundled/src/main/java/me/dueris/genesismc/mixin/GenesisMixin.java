package me.dueris.genesismc.mixin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class GenesisMixin extends JavaPlugin {
    public void main(String[] args){
        System.out.println("This is not meant to be run as a jar. Please place this in the mods folder and run the ignite jar");
    }
    public static String version = "mc1.20-v0.2.2";

    @Override
    public void onEnable() {
        System.err.println("This is not a plugin, please run this in the \"mods\" folder of your server if using Ignite. If not, please use the standalone jar.");
        Bukkit.getPluginManager().disablePlugin(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
