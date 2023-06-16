package me.dueris.genesismc.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class PlayerAddScoreboard extends BukkitRunnable implements Listener {
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    Scoreboard scoreboard = manager.getNewScoreboard();
    Team team = scoreboard.registerNewTeam("origin-players");

    @EventHandler
    public void OnNewOriginPlayerJoin(PlayerJoinEvent e) {
//        Player p = e.getPlayer();
//        team.addEntities(p);
//        team.setCanSeeFriendlyInvisibles(true);
//        team.setDisplayName("Origin Player");
//        p.setScoreboard(scoreboard);
    }

    @Override
    public void run() {
//        for (Player p : Bukkit.getOnlinePlayers()) {
//            Team team = scoreboard.getTeam("origin-players");
//            if (!p.getScoreboard().equals(team) && team != null) {
//                team.addPlayer(p);
//            } else {
//                scoreboard.registerNewTeam("origin-players");
//            }
//        }
    }
}
