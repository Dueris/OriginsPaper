package me.purplewolfmc.genesismc.core.origins.human;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class HumanMain implements Listener {
    @EventHandler
    public void onSpawn(PlayerMoveEvent e){
        Player p = (Player) e.getPlayer();
        if (!p.getScoreboardTags().contains("chosen") || p.getScoreboardTags().contains("human") && !p.getScoreboardTags().contains("enderian") && !p.getScoreboardTags().contains("shulker") && !p.getScoreboardTags().contains("arachnid") && !p.getScoreboardTags().contains("creep") && !p.getScoreboardTags().contains("phantom") && !p.getScoreboardTags().contains("slimeling") && !p.getScoreboardTags().contains("vexian") && !p.getScoreboardTags().contains("blazeborn") && !p.getScoreboardTags().contains("starborne") && !p.getScoreboardTags().contains("mermaid") && !p.getScoreboardTags().contains("witch") && !p.getScoreboardTags().contains("rabbit") && !p.getScoreboardTags().contains("bumblebee") && !p.getScoreboardTags().contains("elytrian") && !p.getScoreboardTags().contains("avian") && !p.getScoreboardTags().contains("rabbit") && !p.getScoreboardTags().contains("piglin") && !p.getScoreboardTags().contains("dragonborne")){
                p.setHealthScale(20);
                p.setMaximumAir(20);
                p.setInvisible(false);
                p.setInvulnerable(false);
                p.setArrowsInBody(0);
                p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(0);
                }
            }
        }
