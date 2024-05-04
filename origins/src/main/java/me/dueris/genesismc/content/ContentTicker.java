package me.dueris.genesismc.content;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static me.dueris.genesismc.content.OrbOfOrigins.orb;


public class ContentTicker extends BukkitRunnable implements Listener {

    Particle.DustTransition whitedust = new Particle.DustTransition(Color.fromRGB(139, 243, 252), Color.fromRGB(224, 253, 255), 0.7F);

    public static Location getRightSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().subtract(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

    public static Location getLeftSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().add(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getInventory().getItemInMainHand().equals(orb)) {
                Location rightHand = getRightSide(p.getEyeLocation(), 0.45).subtract(0, .6, 0);
                p.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, rightHand, 1, 0.25F, -0.5, 0.25F, whitedust);
            }
            if (p.getInventory().getItemInOffHand().equals(orb)) {
                Location leftHand = getLeftSide(p.getEyeLocation(), 0.45).subtract(0, .6, 0);
                p.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, leftHand, 1, 0.25F, -0.5, 0.25F, whitedust);
            }
        }
    }

}
