package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.builder.inst.FactoryObjectInstance;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.GameEvent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.world.GenericGameEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FlightElytra extends CraftPower implements Listener {
	public static ArrayList<UUID> glidingPlayers = new ArrayList<>();

	public static ArrayList<UUID> getGlidingPlayers() {
		return glidingPlayers;
	}

	@Override
	public void run(Player p) {

	}

	@EventHandler
	public void fixChangeConstantFlight(OriginChangeEvent e) {
		if (glidingPlayers.contains(e.getPlayer())) {
			glidingPlayers.remove(e.getPlayer());
			e.getPlayer().setGliding(false);
		}
	}

	@EventHandler
	@SuppressWarnings({"unchecked", "Not scheduled yet"})
	public void ExecuteFlight(PlayerToggleFlightEvent e) {
		Player p = e.getPlayer();
		if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR)) return;
		if (elytra.contains(e.getPlayer())) {
			e.setCancelled(true);
			p.setFlying(false);
			for (Layer layer : CraftApoli.getLayersFromRegistry()) {
				for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
					if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
						setActive(p, power.getTag(), true);
						if (!p.isGliding() && !p.getLocation().add(0, 1, 0).getBlock().isCollidable()) {
							if (p.getGameMode() == GameMode.SPECTATOR) return;
							glidingPlayers.add(p.getUniqueId());
							new BukkitRunnable() {
								@Override
								public void run() {
									if (p.isOnGround() || p.isFlying() || p.isInsideVehicle()) {
										this.cancel();
										glidingPlayers.remove(p.getUniqueId());
									}
									glidingPlayers.add(p.getUniqueId());
									p.setGliding(true);
									p.setFallDistance(0);
								}
							}.runTaskTimer(GenesisMC.getPlugin(), 0L, 1L);
						}
					} else {
						setActive(p, power.getTag(), false);
					}
				}
			}
		}
	}

	@EventHandler
	public void onBoost(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (event.getItem().getType().equals(Material.FIREWORK_ROCKET)) {
				if (elytra.contains(event.getPlayer()) && glidingPlayers.contains(event.getPlayer().getUniqueId())) {
					event.getPlayer().fireworkBoost(event.getItem());
					if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
						event.getItem().setAmount(event.getItem().getAmount() - 1);
				}
			}
		}
	}

	@EventHandler
	public void recreateFallDamage(GenericGameEvent e) {
		if (e.getEvent().equals(GameEvent.HIT_GROUND)) {
			if (e.getEntity() instanceof Player p) {
				if (p.getGameMode().equals(GameMode.CREATIVE)) return;
				if (elytra.contains(p)) {
					Utils.DamageUtils.applyFallDamage(p);
				}
			}
		}
	}

	@Override
	public String getPowerFile() {
		return "apoli:elytra_flight";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return elytra;
	}

	@Override
	public List<FactoryObjectInstance> getValidObjectFactory() {
		return super.getDefaultObjectFactory(List.of());
	}
}
