package me.dueris.genesismc.factory.powers.effects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.factory.powers.Power.jump_increased;

public class JumpIncreased extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (jump_increased.contains(p)) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 15, 1, false, false, false));
            }
        }
    }
}
