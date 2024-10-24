package io.github.dueris.originspaper.power.type;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.papermc.paper.event.player.PlayerFailMoveEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

// TODO - fix fall damage not applying - Dueris
public class ElytraFlightPowerType extends PowerType implements Listener {

	private final boolean renderElytra;
	private final ResourceLocation textureLocation;
	private boolean renderChanged = false;
	private boolean overwritingFlight = false;

	public ElytraFlightPowerType(Power power, LivingEntity entity, boolean renderElytra, ResourceLocation textureLocation) {
		super(power, entity);
		this.renderElytra = renderElytra;
		this.textureLocation = textureLocation;
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(OriginsPaper.apoliIdentifier("elytra_flight"),
			new SerializableData()
				.add("render_elytra", SerializableDataTypes.BOOLEAN)
				.add("texture_location", SerializableDataTypes.IDENTIFIER, null),
			data -> (power, entity) -> new ElytraFlightPowerType(power, entity,
				data.getBoolean("render_elytra"),
				data.getId("texture_location")
			)
		).allowCondition();

	}

	@EventHandler
	public void executeFlight(@NotNull PlayerToggleFlightEvent e) {
		CraftPlayer p = (CraftPlayer) e.getPlayer();
		if (p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR)) return;
		if (entity == p.getHandle()) {
			e.setCancelled(true);
			p.setFlying(false);
			if (isActive() && !PowerHolderComponent.hasPowerType(this.entity, PreventElytraFlightPowerType.class)) {
				if (!p.isGliding() && !p.getLocation().add(0, 1, 0).getBlock().isCollidable()) {
					if (p.getGameMode() == GameMode.SPECTATOR) return;
					new BukkitRunnable() {
						@Override
						public void run() {
							if (!p.getInventory().getItem(EquipmentSlot.CHEST).getType().equals(Material.ELYTRA)) {
								p.sendEquipmentChange(p, EquipmentSlot.CHEST, new ItemStack(Material.ELYTRA));
								renderChanged = true;
							}

							if (p.isOnGround() || p.isFlying() || p.isInsideVehicle()) {
								this.cancel();
								if (renderChanged) {
									renderChanged = false;
									p.updateInventory();
								}
								overwritingFlight = false;
							}

							overwritingFlight = true;
							p.setFallDistance(0.0F);
							p.setGliding(true);
						}
					}.runTaskTimer(OriginsPaper.getPlugin(), 0L, 1L);
				}
			}
		}
	}

	public boolean shouldRenderElytra() {
		return renderElytra;
	}

	public ResourceLocation getTextureLocation() {
		return textureLocation;
	}

	@EventHandler
	public void fireworkRocketImpl(@NotNull PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (event.getItem().getType().equals(Material.FIREWORK_ROCKET)) {
				if (entity == ((CraftPlayer) event.getPlayer()).getHandle() && overwritingFlight) {
					FireworkRocketEntity rocketEntity = new FireworkRocketEntity(entity.level(), CraftItemStack.asNMSCopy(event.getItem()), entity);
					PlayerLaunchProjectileEvent projectileEvent = new PlayerLaunchProjectileEvent(event.getPlayer(), event.getItem(), (Projectile) rocketEntity.getBukkitEntity());
					if (projectileEvent.callEvent()) {
						entity.level().addFreshEntity(rocketEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
						if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
							event.getItem().setAmount(event.getItem().getAmount() - 1);
					}
				}
			}
		}
	}

	@EventHandler
	public void fixBlockGlitch(@NotNull PlayerFailMoveEvent e) {
		if (overwritingFlight && ((CraftPlayer) e.getPlayer()).getHandle() == entity) {
			e.setAllowed(true);
			e.setLogWarning(false);
		}
	}

}
