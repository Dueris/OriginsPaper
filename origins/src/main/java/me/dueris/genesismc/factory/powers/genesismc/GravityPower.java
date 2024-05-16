package me.dueris.genesismc.factory.powers.genesismc;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.apoli.CreativeFlight;
import me.dueris.genesismc.factory.powers.apoli.ElytraFlightPower;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.util.entity.PowerHolderComponent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class GravityPower extends PowerType {

	public GravityPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		super(name, description, hidden, condition, loading_priority);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.identifier("no_gravity"));
	}

	@Override
	public void tickAsync(Player p) {
		if (!PowerHolderComponent.hasPower(p, "origins:like_water")) { // Let LikeWater handle its own gravity
			if (isActive(p)) {
				if (getPlayers().contains(p)) {
					p.setGravity(false);
					p.setFallDistance(0.1f);
				} else {
					p.setGravity(true);
				}
			}
		}
	}

	@EventHandler
	public void serverTickEnd(PlayerToggleFlightEvent e) {
		if (PowerHolderComponent.hasPowerType(e.getPlayer(), CreativeFlight.class) || e.getPlayer().getGameMode().equals(GameMode.CREATIVE) || e.getPlayer().getGameMode().equals(GameMode.SPECTATOR)
			|| PowerHolderComponent.hasPowerType(e.getPlayer(), ElytraFlightPower.class)) return;
		e.setCancelled(getPlayers().contains(e.getPlayer()));
	}

	@EventHandler
	public void shiftgodown(PlayerToggleSneakEvent e) {
		if (getPlayers().contains(e.getPlayer())) {
			if (e.getPlayer().isOnGround()) return;
			new BukkitRunnable() {
				@Override
				public void run() {
					if (e.getPlayer().isFlying()) return;
					if (e.getPlayer().isSneaking()) {
						if (!(e.getPlayer().getVelocity().getY() < -0.2)) {
							e.getPlayer().setVelocity(new Vector(e.getPlayer().getVelocity().getX(), e.getPlayer().getVelocity().getY() - 0.1, e.getPlayer().getVelocity().getZ()));
						}
					} else {
						this.cancel();
					}
				}
			}.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
		}

	}

	@EventHandler
	public void jumpyupy(PlayerJumpEvent e) {
		if (getPlayers().contains(e.getPlayer())) {
			e.getPlayer().setVelocity(new Vector(e.getPlayer().getVelocity().getX(), e.getPlayer().getVelocity().getY() + 1, e.getPlayer().getVelocity().getZ()));
		}
	}
}
