package me.dueris.genesismc.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.Bukkit.createInventory;
import static org.bukkit.ChatColor.LIGHT_PURPLE;

public class ForceChooseRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if(!p.getScoreboardTags().contains("chosen")){
                p.addScoreboardTag("choosing");
            }
            if (p.getScoreboardTags().contains("choosing")) {
                if (p.getOpenInventory().getTitle().equalsIgnoreCase(LIGHT_PURPLE + "Origins Menu") || p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Human") || p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Enderian") || p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Shulk") || p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Arachnid") || p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Creep") || p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Phantom") || p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Slimeling") || p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Vexian") || p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Blazeborn") || p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Starborne") || p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Mermaid") || p.getOpenInventory().getTitle().equalsIgnoreCase("Allay") || p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Rabbit") || p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Bumblebee") || p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Elytrian") || p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Avian") || p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Piglin") || p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Dragonborne") || p.getOpenInventory().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Allay")) {
                    p.setGameMode(GameMode.SPECTATOR);
                } else {
                    p.setGameMode(GameMode.SURVIVAL);
                }

            }

        }
    }
}
