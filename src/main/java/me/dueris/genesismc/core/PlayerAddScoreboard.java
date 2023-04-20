package me.dueris.genesismc.core;

import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;

public class PlayerAddScoreboard implements Listener {

    ScoreboardManager manager = Bukkit.getScoreboardManager();
    Scoreboard scoreboard = manager.getNewScoreboard();
    Team team = scoreboard.registerNewTeam("origin-players");

    @EventHandler
    public void OnNewOriginPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        team.addEntities(p);
        team.setCanSeeFriendlyInvisibles(true);
        team.setDisplayName("Origin Player");
        p.setScoreboard(scoreboard);
    }

}
