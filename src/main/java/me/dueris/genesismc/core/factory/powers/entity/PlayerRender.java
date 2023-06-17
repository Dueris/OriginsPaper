package me.dueris.genesismc.core.factory.powers.entity;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

import static me.dueris.genesismc.core.factory.powers.Powers.pumpkin_hate;
import static me.dueris.genesismc.core.factory.powers.Powers.translucent;

public class PlayerRender extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard scoreboard = manager.getNewScoreboard();
            Team team = scoreboard.getTeam("origin-players");
            if (translucent.contains(p)) {
                 if (team == null) {
                    team = scoreboard.registerNewTeam("origin-players");
                 }
                 team.addEntity(p);
                if (OriginPlayer.isInPhantomForm(p)) {
                    for (Player other : Bukkit.getOnlinePlayers()) {
                        other.hidePlayer(GenesisMC.getPlugin(), p);
                    }
                } else {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 15, 255, false, false, false));
                    for (Player other : Bukkit.getOnlinePlayers()) {
                        team.addEntity(other);
                        other.showPlayer(GenesisMC.getPlugin(), p);
                    }
                }
            }

            //hide player pumpkin_hate render

            ItemStack helmet = p.getInventory().getHelmet();
            boolean wearingPumpkin = helmet != null && helmet.getType() == Material.CARVED_PUMPKIN;

            for (Player target : Bukkit.getOnlinePlayers()){
                if(pumpkin_hate.contains(target)){
                    if(wearingPumpkin){
                        target.hidePlayer(GenesisMC.getPlugin(), p);
                    }else{
                        target.showPlayer(GenesisMC.getPlugin(), p);
                    }
                }
            }

        }

    }
}
