package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ClimbingPower extends CraftPower implements Listener {

	public ArrayList<Player> active_climbing = new ArrayList<>();
	public ArrayList<Player> holdingPlayers = new ArrayList<>();
	public ArrayList<Player> allowedToClimb = new ArrayList<>();

	public boolean isActiveClimbing(Player player) {
		return active_climbing.contains(player);
	}

	public ArrayList<Player> getActiveClimbingMap() {
		return active_climbing;
	}

	@Override
	public void run(Player p, Power power) {
		if (!p.isSneaking()) holdingPlayers.remove(p);
		if (!((CraftWorld) p.getWorld()).getHandle().getBlockStates(((CraftPlayer) p).getHandle().getBoundingBox().inflate(0.1, 0, 0.1)).filter(state -> state.getBukkitMaterial().isCollidable()).toList().isEmpty()) {
			if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p) && allowedToClimb.contains(p)) {
				setActive(p, power.getTag(), true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 4, 2, false, false, false));
				getActiveClimbingMap().add(p);
				new BukkitRunnable() {
					@Override
					public void run() {
						getActiveClimbingMap().remove(p);
					}
				}.runTaskLater(GenesisMC.getPlugin(), 2L);
			} else {
				setActive(p, power.getTag(), false);
			}
		}
	}

	@EventHandler
	public void jump(PlayerJumpEvent e) {
		if (climbing.contains(e.getPlayer())) {
			Player p = e.getPlayer();
			allowedToClimb.add(p);
			new BukkitRunnable() {
				@Override
				public void run() {
					if (p.isOnGround()) {
						allowedToClimb.remove(p);
						cancel();
					}
				}
			}.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
		}
	}

	@EventHandler
	public void latch(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		if (climbing.contains(p)) {
			for (Power power : OriginPlayerAccessor.getPowers(p, getType())) {
				if (power.getBooleanOrDefault("allow_holding", true)) {
					final Location[] location = {p.getLocation()};
					if (e.isSneaking() && getActiveClimbingMap().contains(p)) {
						new BukkitRunnable() {
							@Override
							public void run() {
								if (p.isSneaking()) {
									if (location[0].getPitch() != p.getPitch() || location[0].getYaw() != p.getYaw()) {
										float pitch = p.getPitch();
										float yaw = p.getYaw();
										Location updatedLocation = new Location(location[0].getWorld(), location[0].getX(), location[0].getY(), location[0].getZ(), yaw, pitch);
										location[0] = updatedLocation;
									}
									p.teleportAsync(location[0]);
									holdingPlayers.add(p);
								} else {
									holdingPlayers.remove(p);
									cancel();
								}
							}
						}.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
					}
				}
			}
		}
	}

	@Override
	public String getType() {
		return "apoli:climbing";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return climbing;
	}
}
