package me.dueris.genesismc.core.factory.powers.entity;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import static me.dueris.genesismc.core.factory.powers.Powers.pumpkin_hate;
import static me.dueris.genesismc.core.factory.powers.Powers.translucent;

public class PlayerRender extends BukkitRunnable {
    @Override
    public void run() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getMainScoreboard();
        Team team = scoreboard.getTeam("origin-players");
        if (team == null) {
            team = scoreboard.registerNewTeam("origin-players");
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            boolean isInvisible = p.hasPotionEffect(PotionEffectType.INVISIBILITY);
            boolean isInTranslucentList = translucent.contains(p);
            boolean isInPhantomForm = OriginPlayer.isInPhantomForm(p);

            if (isInPhantomForm) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (!other.equals(p)) {
                        other.hidePlayer(GenesisMC.getPlugin(), p);
                    }
                }
                team.addEntry(p.getName());
            } else if (isInvisible && !isInTranslucentList) {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (!other.equals(p)) {
                        other.hidePlayer(GenesisMC.getPlugin(), p);
                    }
                }
                Location location = p.getLocation();
                location.getWorld().spawnParticle(Particle.SPELL_MOB_AMBIENT, location, 2, 0.0, 0.0, 0.0, 1.0, null);
                team.addEntry(p.getName());
            } else {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (!other.equals(p)) {
                        other.showPlayer(GenesisMC.getPlugin(), p);
                    }
                }
                team.addEntry(p.getName());
            }

            if(isInTranslucentList){
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (!other.equals(p)) {
                        other.showPlayer(GenesisMC.getPlugin(), p);
                    }
                }
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 15, 255, false, false, false));
                team.addEntry(p.getName());
            }

            // Hide player from pumpkin_hate players if wearing a pumpkin
            ItemStack helmet = p.getInventory().getHelmet();
            boolean wearingPumpkin = helmet != null && helmet.getType() == Material.CARVED_PUMPKIN;

            for (Player target : Bukkit.getOnlinePlayers()) {
                if (pumpkin_hate.contains(target)) {
                    if (wearingPumpkin) {
                        target.hidePlayer(GenesisMC.getPlugin(), p);
                    } else {
                        target.showPlayer(GenesisMC.getPlugin(), p);
                    }
                }
            }
        }
    }
}
