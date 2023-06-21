package me.dueris.genesismc.core.utils;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.ender_particles;

public class ParticleHandler extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getGameMode() != GameMode.SPECTATOR && !p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                if (ender_particles.contains(p)) {
                    Particle.DustTransition ender_particle = new Particle.DustTransition(Color.fromRGB(252, 0, 255), Color.fromRGB(209, 0, 255), 0.5F);
                    p.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, p.getLocation(), 2, 0.6F, -0.5, 0.6F, ender_particle);
                    p.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, p.getEyeLocation(), 2, 0.6F, -0.5, 0.6F, ender_particle);
                }
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    if (origin.getTag().equals("origins:starborne")) {
                        Location eyelocation = new Location(p.getWorld(), p.getEyeLocation().getX(), p.getEyeLocation().getY() - 0.3, p.getEyeLocation().getZ());
                        p.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, p.getLocation(), 2, 0.25F, -0.5, 0.25F, 0);
                        Particle.DustTransition dustColorTransition = new Particle.DustTransition(Color.fromRGB(151, 0, 227), Color.fromRGB(53, 0, 212), 1F);
                        p.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, p.getLocation(), 2, 0.25F, -0.5, 0.25F, dustColorTransition);
                        p.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, p.getEyeLocation(), 2, 0.25F, -0.5, 0.25F, 0);
                        p.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, eyelocation, 2, 0.25F, -0.5, 0.25F, dustColorTransition);

                    }
                    if (origin.getTag().equals("origins:allay")) {
                        Particle.DustTransition dustColorTransition = new Particle.DustTransition(Color.fromRGB(97, 255, 250), Color.fromRGB(163, 255, 244), 0.275F);
                        p.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, p.getLocation(), 3, 0.4F, -0.5, 0.4F, dustColorTransition);
                        p.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, p.getEyeLocation(), 3, 0.4F, -0.5, 0.4F, dustColorTransition);
                    }
                }
//                if (flame_particles.contains(p)) {
//                    p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 3, 0.4F, -0.5, 0.4F);
//                    p.getWorld().spawnParticle(Particle.FLAME, p.getEyeLocation(), 3, 0.4F, -0.5, 0.4F);
//                }
            }
        }
    }
}
