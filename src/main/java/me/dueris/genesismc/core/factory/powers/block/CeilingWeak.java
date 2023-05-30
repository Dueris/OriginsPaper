package me.dueris.genesismc.core.factory.powers.block;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.claustrophobia;

public class CeilingWeak extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (claustrophobia.contains(OriginPlayer.getOriginTag(p)) && !p.getGameMode().equals(GameMode.SPECTATOR)) {
                if (p.getEyeLocation().add(0, 2, 0).getBlock().isSolid() || p.getEyeLocation().add(0, 1, 0).getBlock().isSolid()) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3, 2, false, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 3, 1, false, false, false));
                }
            }
        }
    }
}
