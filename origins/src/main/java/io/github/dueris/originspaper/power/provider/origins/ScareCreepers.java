package io.github.dueris.originspaper.power.provider.origins;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.provider.PowerProvider;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class ScareCreepers implements Listener, PowerProvider {
	protected static ResourceLocation powerReference = OriginsPaper.originIdentifier("scare_creepers");
	private final ResourceLocation hitByPlayerKey = OriginsPaper.identifier("hit-by-player");

	@EventHandler
	public void load(@NotNull EntitySpawnEvent event) {
		if (event.getEntity() instanceof Creeper creeper) {
			applyPatch(creeper);
		}
	}

	@EventHandler
	public void load(@NotNull EntitiesLoadEvent event) {
		for (Entity entity : event.getEntities()) {
			if (entity instanceof Creeper creeper) {
				applyPatch(creeper);
			}
		}
	}

	public void applyPatch(Creeper creeper) {
		Bukkit.getMobGoals().addGoal(creeper, 0, new AvoidEntityGoal<>(
			(PathfinderMob) ((CraftEntity) creeper).getHandle(), net.minecraft.world.entity.player.Player.class, 6, 1, 1.2,
			livingEntity -> {
				if (livingEntity.getBukkitEntity() instanceof Player player) {
					if (PowerHolderComponent.hasPower(player, powerReference.toString())) {
						String data = creeper.getPersistentDataContainer().get(CraftNamespacedKey.fromMinecraft(hitByPlayerKey), PersistentDataType.STRING);
						if (data == null) {
							return true;
						}
						return !data.equals(player.getName());
					}
				}
				return false;
			}
		).asPaperVanillaGoal());
	}

	@EventHandler
	public void onEntityDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
		if (event.getEntity().getType().equals(EntityType.CREEPER)) {
			Player player;
			if (event.getDamager() instanceof Projectile projectile) {
				if (projectile.getShooter() instanceof org.bukkit.entity.Player shooter) player = shooter;
				else return;
			} else if (event.getDamager() instanceof org.bukkit.entity.Player damager) {
				player = damager;
			} else {
				return;
			}
			event.getEntity().getPersistentDataContainer().set(CraftNamespacedKey.fromMinecraft(hitByPlayerKey), PersistentDataType.STRING, player.getName());
		}
	}

	@EventHandler
	public void onEntityTargetLivingEntity(@NotNull EntityTargetLivingEntityEvent event) {
		if (event.getEntity().getType().equals(EntityType.CREEPER)) {
			if (event.getTarget() instanceof Player player) {
				new BukkitRunnable() {
					@Override
					public void run() {
						String data = event.getEntity().getPersistentDataContainer().get(CraftNamespacedKey.fromMinecraft(hitByPlayerKey), PersistentDataType.STRING);
						if (data == null) {
							event.setCancelled(true);
							return;
						}
						if (!data.equals(player.getName())) {
							event.setCancelled(true);
						}
					}
				}.runTask(OriginsPaper.getPlugin());
			}
		}
	}
}
