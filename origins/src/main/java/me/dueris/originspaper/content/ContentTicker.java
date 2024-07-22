package me.dueris.originspaper.content;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustTransition;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ContentTicker extends BukkitRunnable implements Listener {
	DustTransition whitedust = new DustTransition(Color.fromRGB(139, 243, 252), Color.fromRGB(224, 253, 255), 0.7F);

	public static @NotNull Location getRightSide(@NotNull Location location, double distance) {
		float angle = location.getYaw() / 60.0F;
		return location.clone().subtract(new Vector(Math.cos(angle), 0.0, Math.sin(angle)).normalize().multiply(distance));
	}

	public static @NotNull Location getLeftSide(@NotNull Location location, double distance) {
		float angle = location.getYaw() / 60.0F;
		return location.clone().add(new Vector(Math.cos(angle), 0.0, Math.sin(angle)).normalize().multiply(distance));
	}

	public void run() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getInventory().getItemInMainHand().equals(OrbOfOrigins.orb)) {
				Location rightHand = getRightSide(p.getEyeLocation(), 0.45).subtract(0.0, 0.6, 0.0);
				p.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, rightHand, 1, 0.25, -0.5, 0.25, this.whitedust);
			}

			if (p.getInventory().getItemInOffHand().equals(OrbOfOrigins.orb)) {
				Location leftHand = getLeftSide(p.getEyeLocation(), 0.45).subtract(0.0, 0.6, 0.0);
				p.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, leftHand, 1, 0.25, -0.5, 0.25, this.whitedust);
			}
		}
	}
}
