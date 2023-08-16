package me.dueris.genesismc.core.factory.powers.OriginsMod.prevent;

import me.dueris.genesismc.core.GenesisMC;
import net.skinsrestorer.api.serverinfo.Platform;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PreventSuperClass {
    public void runTasks(){
        Bukkit.getServer().getPluginManager().registerEvents(new PreventBeingUsed(), GenesisMC.getPlugin());
        Bukkit.getServer().getPluginManager().registerEvents(new PreventBlockSelection(), GenesisMC.getPlugin());
        Bukkit.getServer().getPluginManager().registerEvents(new PreventBlockUse(), GenesisMC.getPlugin());
        Bukkit.getServer().getPluginManager().registerEvents(new PreventDeath(), GenesisMC.getPlugin());
    }

    public static ArrayList<Player> prevent_being_used = new ArrayList<>();
    public static ArrayList<Player> prevent_block_selection = new ArrayList<>();
    public static ArrayList<Player> prevent_block_use = new ArrayList<>();
    public static ArrayList<Player> prevent_death = new ArrayList<>();
    public static ArrayList<Player> prevent_elytra_flight = new ArrayList<>();
    public static ArrayList<Player> prevent_entity_collision = new ArrayList<>();
    public static ArrayList<Player> prevent_entity_render = new ArrayList<>();
    public static ArrayList<Player> prevent_entity_use = new ArrayList<>();
    public static ArrayList<Player> prevent_item_use = new ArrayList<>();
    public static ArrayList<Player> prevent_sleep = new ArrayList<>();
    public static ArrayList<Player> prevent_sprinting = new ArrayList<>();
}
