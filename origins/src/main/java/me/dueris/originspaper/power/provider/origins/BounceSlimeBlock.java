package me.dueris.originspaper.power.provider.origins;

import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.power.provider.PowerProvider;
import me.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.GameEvent;
import org.bukkit.Location;
import org.bukkit.craftbukkit.block.CraftBiome;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.GenericGameEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class BounceSlimeBlock implements Listener, PowerProvider {
	public static HashMap<Player, Location> lastLoc = new HashMap<>();
	protected static ResourceLocation powerReference = OriginsPaper.originIdentifier("slime_block_bounce");

	@EventHandler
	public void gameEvent(@NotNull GenericGameEvent event) {
		if (event.getEvent().equals(GameEvent.HIT_GROUND)) {
			if (event.getEntity() instanceof Player player) {
				me.dueris.originspaper.event.PlayerHitGroundEvent playerHitGroundEvent = new me.dueris.originspaper.event.PlayerHitGroundEvent(player);
				Bukkit.getPluginManager().callEvent(playerHitGroundEvent);
				if (player.isSneaking()) return;
				if (!PowerHolderComponent.hasPower(player, powerReference.toString())) return;
				if (!lastLoc.containsKey(player)) return;
				if (CraftBiome.bukkitToMinecraft(player.getLocation().getBlock().getBiome()).getTemperature(CraftLocation.toBlockPosition(player.getLocation())) < 0.2)
					return;
				Location lastLocation = lastLoc.get(player);

				if (lastLocation.getY() > player.getY()) {
					double coefficientOfRestitution = 0.45;
					double reboundVelocity = -coefficientOfRestitution * -(lastLocation.getY() - player.getY());
					if (reboundVelocity <= 0.27) return;

					if (!player.isOnGround() || player.isJumping() || player.isSprinting()) return;
					player.setVelocity(new Vector(player.getVelocity().getX(), reboundVelocity, player.getVelocity().getZ()));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void move(@NotNull PlayerMoveEvent e) {
		if (!e.isCancelled()) {
			if (e.getPlayer().isOnGround()) return;
			lastLoc.put(e.getPlayer(), e.getFrom());
		}
	}

}
