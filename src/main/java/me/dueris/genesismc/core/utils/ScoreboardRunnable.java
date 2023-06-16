package me.dueris.genesismc.core.utils;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

public class ScoreboardRunnable extends BukkitRunnable {
    @Override
    public void run() {
//        for (Player p : Bukkit.getOnlinePlayers()) {
//            PersistentDataContainer data = p.getPersistentDataContainer();
//            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
//            ScoreboardManager manager = Bukkit.getScoreboardManager();
//            Scoreboard scoreboard = manager.getNewScoreboard();
//            Team team = scoreboard.getTeam("origin-players");
//            if (!p.getScoreboard().equals(team) && team != null) {
//                team.addPlayer(p);
//                team.setCanSeeFriendlyInvisibles(true);
//                team.setDisplayName("Origin Player");
//                Objective objective = scoreboard.registerNewObjective("originid", "id");
//                Score score = objective.getScore(p);
//                score.setScore(originid);
//            }
//
//        }
    }
}
