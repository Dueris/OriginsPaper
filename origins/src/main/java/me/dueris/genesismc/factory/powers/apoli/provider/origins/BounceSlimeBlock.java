package me.dueris.genesismc.factory.powers.apoli.provider.origins;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.provider.PowerProvider;
import org.bukkit.Bukkit;
import org.bukkit.GameEvent;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.GenericGameEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class BounceSlimeBlock extends CraftPower implements Listener, PowerProvider {
	public static ArrayList<Player> bouncePlayers = new ArrayList<>();
	public static HashMap<Player, Location> lastLoc = new HashMap<>();
	protected static NamespacedKey powerReference = GenesisMC.originIdentifier("slime_block_bounce");

	@Override
	public void run(Player p) {

	}

	@EventHandler
	public void gameEvent(GenericGameEvent event) {
		if (event.getEvent().equals(GameEvent.HIT_GROUND)) {
			if (event.getEntity() instanceof Player player) {
				me.dueris.genesismc.event.PlayerHitGroundEvent playerHitGroundEvent = new me.dueris.genesismc.event.PlayerHitGroundEvent(player);
				Bukkit.getPluginManager().callEvent(playerHitGroundEvent);
				if (player.isSneaking()) return;
				if (!bouncePlayers.contains(player) && !lastLoc.containsKey(player)) return;
				Location lastLocation = lastLoc.get(player);

				if (lastLocation.getY() > player.getY()) {
					double coefficientOfRestitution = 0.45;
					double reboundVelocity = -coefficientOfRestitution * -(lastLocation.getY() - player.getY());
					if (reboundVelocity <= 0.2) return;

					if (!player.isOnGround() || player.isJumping() || player.isSprinting()) return;
					player.setVelocity(new Vector(player.getVelocity().getX(), reboundVelocity, player.getVelocity().getZ()));
				}
			}
		}
	}

	@EventHandler
	public void move(PlayerMoveEvent e) {
		if (!e.isCancelled()) {
			if (!bouncePlayers.contains(e.getPlayer())) return;
			if (e.getPlayer().isOnGround()) return;
			lastLoc.put(e.getPlayer(), e.getFrom());
		}
	}

	@Override
	public String getPowerFile() {
		return null;
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return bouncePlayers;
	}

	@Override
	public void setActive(Player p, String tag, Boolean bool) {
		if (powers_active.containsKey(p)) {
			if (powers_active.get(p).containsKey(tag)) {
				powers_active.get(p).replace(tag, bool);
			} else {
				powers_active.get(p).put(tag, bool);
			}
		} else {
			powers_active.put(p, new HashMap());
			setActive(p, tag, bool);
		}
	}
}
