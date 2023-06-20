package me.dueris.genesismc.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class ScoreboardRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard = manager.getNewScoreboard();
            Team team = scoreboard.getTeam("origin-players");
            if (!p.getScoreboard().equals(team) && team != null) {
                team.addPlayer(p);
                team.setCanSeeFriendlyInvisibles(true);
                team.setDisplayName("Origin Player");
            }

        }
    }
}
