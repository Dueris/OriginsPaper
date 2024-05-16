package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ClimbingPower extends PowerType {
	public static ArrayList<Player> active_climbing = new ArrayList<>();
	private final boolean allowHolding;
	public ArrayList<Player> holdingPlayers = new ArrayList<>();
	public ArrayList<Player> allowedToClimb = new ArrayList<>();

	public ClimbingPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, boolean allowHolding) {
		super(name, description, hidden, condition, loading_priority);
		this.allowHolding = allowHolding;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("climbing"))
			.add("allow_holding", boolean.class, true);
	}

	public static boolean isActiveClimbing(Player player) {
		return active_climbing.contains(player);
	}

	public static ArrayList<Player> getActiveClimbingMap() {
		return active_climbing;
	}

	@Override
	public void tick(Player p) {
		if (!p.isSneaking()) holdingPlayers.remove(p);
		if (!((CraftWorld) p.getWorld()).getHandle().getBlockStates(((CraftPlayer) p).getHandle().getBoundingBox().inflate(0.1, 0, 0.1)).filter(state -> state.getBukkitMaterial().isCollidable()).toList().isEmpty()) {
			if (isActive(p) && allowedToClimb.contains(p)) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 4, 2, false, false, false));
				getActiveClimbingMap().add(p);
				new BukkitRunnable() {
					@Override
					public void run() {
						getActiveClimbingMap().remove(p);
					}
				}.runTaskLater(GenesisMC.getPlugin(), 2L);
			}
		}
	}

	@EventHandler
	public void jump(PlayerJumpEvent e) {
		if (getPlayers().contains(e.getPlayer())) {
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
		if (getPlayers().contains(p)) {
			if (this.isAllowHolding()) {
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

	public boolean isAllowHolding() {
		return allowHolding;
	}
}
