package me.dueris.genesismc.core.utils;

import me.dueris.genesismc.core.GenesisMC;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ParticleHandler extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if (p.getGameMode() != GameMode.SPECTATOR && !p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                if (originid == 0401065) {
                    p.getWorld().spawnParticle(Particle.PORTAL, p.getLocation(), 4, 0.6F, -0.5, 0.6F);
                }
                if (originid == 7303065) {
                    p.getWorld().spawnParticle(Particle.GLOW, p.getLocation(), 1, 0.35F, -0.5, 0.35F);
                    Particle.DustTransition dustColorTransition = new Particle.DustTransition(Color.fromRGB(42, 191, 232), Color.fromRGB(191, 0, 255), 1F);
                    Particle.DustTransition whitedust = new Particle.DustTransition(Color.fromRGB(255, 255, 255), Color.fromRGB(255, 255, 255), 0.7F);
                    p.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, p.getLocation(), 2, 0.35F, -0.5, 0.35F, dustColorTransition);
                    p.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, p.getLocation(), 2, 0.35F, -0.5, 0.35F, whitedust);
                    p.getWorld().spawnParticle(Particle.PORTAL, p.getLocation(), 1, 0.35F, -0.5, 0.35F);

                    p.getWorld().spawnParticle(Particle.GLOW, p.getEyeLocation(), 1, 0.35F, -0.5, 0.35F);
                    p.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, p.getEyeLocation(), 2, 0.35F, -0.5, 0.35F, dustColorTransition);
                    p.getWorld().spawnParticle(Particle.PORTAL, p.getEyeLocation(), 1, 0.35F, -0.5, 0.35F);
                    p.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, p.getEyeLocation(), 2, 0.35F, -0.5, 0.35F, whitedust);

                }
                if (originid == 1205048) {
                    Particle.DustTransition dustColorTransition = new Particle.DustTransition(Color.fromRGB(97, 255, 250), Color.fromRGB(163, 255, 244), 0.275F);
                    p.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, p.getLocation(), 3, 0.4F, -0.5, 0.4F, dustColorTransition);
                    p.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, p.getEyeLocation(), 3, 0.4F, -0.5, 0.4F, dustColorTransition);



                }

            }
        }
    }
}
