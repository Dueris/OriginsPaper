package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.event.PowerUpdateEvent;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.papermc.paper.event.player.PlayerFailMoveEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.GameEvent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.world.GenericGameEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.UUID;

public class ElytraFlightPower extends PowerType {
	private final LinkedList<LivingEntity> glidingPlayers = new LinkedList<>();
	private final LinkedList<Player> renderingChanged = new LinkedList<>();
	private static ItemStack renderStack;

	public ElytraFlightPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		renderStack = new ItemStack(Material.ELYTRA);
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("elytra_flight"), PowerType.getFactory().getSerializableData());
	}

	public LinkedList<LivingEntity> getGlidingPlayers() {
		return glidingPlayers;
	}

	@EventHandler
	public void fixChangeConstantFlight(@NotNull PowerUpdateEvent e) {
		if (!(e.getPower().getType().equalsIgnoreCase(getType()) && e.isRemoved())) return;
		if (glidingPlayers.contains(e.getPlayer())) {
			glidingPlayers.remove(e.getPlayer());
			e.getPlayer().setGliding(false);
		}
	}

	@EventHandler
	public void executeFlight(@NotNull PlayerToggleFlightEvent e) {
		Player p = e.getPlayer();
		if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR)) return;
		if (getPlayers().contains(((CraftPlayer) e.getPlayer()).getHandle())) {
			e.setCancelled(true);
			p.setFlying(false);
			if (isActive(((CraftPlayer) p).getHandle()) && !PowerHolderComponent.doesHaveConditionedPower(p, PreventElytraFlightPower.class, (preventElytraFlight) -> preventElytraFlight.isActive(((CraftPlayer) p).getHandle()))) {
				if (!p.isGliding() && !p.getLocation().add(0, 1, 0).getBlock().isCollidable()) {
					if (p.getGameMode() == GameMode.SPECTATOR) return;
					glidingPlayers.add(p);
					new BukkitRunnable() {
						@Override
						public void run() {
							if (!p.getInventory().getItem(EquipmentSlot.CHEST).getType().equals(Material.ELYTRA)) {
								p.sendEquipmentChange(p, EquipmentSlot.CHEST, renderStack);
								renderingChanged.add(p);
							}

							if (p.isOnGround() || p.isFlying() || p.isInsideVehicle()) {
								this.cancel();
								if (renderingChanged.contains(p)) {
									renderingChanged.remove(p);
									p.updateInventory();
								}
								glidingPlayers.remove(p);
							}

							p.setFallDistance(0.0F);
							glidingPlayers.add(p);
							p.setGliding(true);
						}
					}.runTaskTimer(OriginsPaper.getPlugin(), 0L, 1L);
				}
			}
		}
	}

	@EventHandler
	public void onBoost(@NotNull PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (event.getItem().getType().equals(Material.FIREWORK_ROCKET)) {
				if (getPlayers().contains(((CraftPlayer) event.getPlayer()).getHandle()) && glidingPlayers.contains(event.getPlayer())) {
					event.getPlayer().fireworkBoost(event.getItem());
					if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
						event.getItem().setAmount(event.getItem().getAmount() - 1);
				}
			}
		}
	}

	@EventHandler
	public void fixBlockGlitch(@NotNull PlayerFailMoveEvent e) {
		if (glidingPlayers.contains(e.getPlayer())) {
			e.setAllowed(true);
			e.setLogWarning(false);
		}
	}

}
