package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.util.MiscUtils;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.KeybindTriggerEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.KeybindingUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class FireProjectile extends CraftPower implements Listener {

    private static final ArrayList<Player> doubleFirePatch = new ArrayList<>();
    public static HashMap<Player, ArrayList<String>> in_continuous = new HashMap<>();
    public static ArrayList<Player> enderian_pearl = new ArrayList<>();
    public static ArrayList<Player> in_cooldown_patch = new ArrayList<>();

    public static void addCooldownPatch(Player p) {
	in_cooldown_patch.add(p);
	new BukkitRunnable() {
	    @Override
	    public void run() {
		in_cooldown_patch.remove(p);
	    }
	}.runTaskLater(GenesisMC.getPlugin(), 5);
    }

    @EventHandler
    public void inContinuousFix(KeybindTriggerEvent e) {
	Player p = e.getPlayer();
	for (Layer layer : CraftApoli.getLayersFromRegistry()) {
	    if (fire_projectile.contains(p)) {
		for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType(), layer)) {
		    if (KeybindingUtils.isKeyActive(power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active"), p)) {
			in_continuous.putIfAbsent(p, new ArrayList<>());
			if (power.getJsonObject("key").getBooleanOrDefault("continuous", false)) {
			    if (in_continuous.get(p).contains(power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active"))) {
				in_continuous.get(p).remove(power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active"));
			    } else {
				in_continuous.get(p).add(power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active"));
			    }
			}
		    }
		}
	    }
	}
    }

    @EventHandler
    public void teleportDamgeOff(PlayerTeleportEvent e) {
	Player p = e.getPlayer();
	if (enderian_pearl.contains(e.getPlayer()) && e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
	    e.setCancelled(true);
	    p.teleportAsync(e.getTo());
	    p.setFallDistance(0);
	}

    }

    @EventHandler
    public void keybindPress(KeybindTriggerEvent e) {
	Player p = e.getPlayer();
	if (doubleFirePatch.contains(p)) return;
	for (Layer layer : CraftApoli.getLayersFromRegistry()) {
	    for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType(), layer)) {
		if (fire_projectile.contains(p)) {
		    if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
			if (!Cooldown.isInCooldown(p, power)) {
			    if (KeybindingUtils.isKeyActive(power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active"), p)) {
				int cooldown = power.getNumberOrDefault("cooldown", 1).getInt();
				String tag = power.getStringOrDefault("tag", "{}");
				float divergence = power.getNumberOrDefault("divergence", 1.0f).getFloat();
				float speed = power.getNumberOrDefault("speed", 1.5F).getFloat();
				int amt = power.getNumberOrDefault("count", 1).getInt();
				int start_delay = power.getNumberOrDefault("start_delay", 0).getInt();
				int interval = power.getNumberOrDefault("interval", 1).getInt();

				// Introduce a slight random divergence
				divergence += (float) ((Math.random() - 0.5) * 0.05); // Adjust the 0.05 value to control the randomness level

				EntityType type;
				if (power.getString("entity_type").equalsIgnoreCase("origins:enderian_pearl")) {
				    type = EntityType.ENDER_PEARL;
				    enderian_pearl.add(p);
				} else {
				    type = EntityType.valueOf(power.getNamespacedKey("entity_type").asString().split(":")[1].toUpperCase());
				    enderian_pearl.remove(p);
				}
				String key = power.getJsonObject("key").getStringOrDefault("key", "key.origins.primary_active");

				float finalDivergence = divergence;
				boolean cont = !power.getJsonObject("key").getBooleanOrDefault("continuous", false);
				ServerPlayer player = ((CraftPlayer) p).getHandle();
				new BukkitRunnable() {
				    int shotsLeft = -amt;

				    @Override
				    public void run() {
					if (shotsLeft >= 0) {
					    if ((!cont || !KeybindingUtils.activeKeys.get(p).contains(key)) && !in_continuous.get(p).contains(key)) {
						Cooldown.addCooldown(p, cooldown, power);
						setActive(p, power.getTag(), false);
						this.cancel();
						return;
					    }
					}
					addCooldownPatch(p);

					if (type.getEntityClass() != null) {
					    if (doubleFirePatch.contains(p)) return;

					    ServerLevel serverWorld = (ServerLevel) player.level();
					    float yaw = player.getYRot();
					    float pitch = player.getXRot();

					    Entity entityToSpawn = Utils
						.getEntityWithPassengers(serverWorld, CraftEntityType.bukkitToMinecraft(type), MiscUtils.ParserUtils.parseJson(new com.mojang.brigadier.StringReader(tag), CompoundTag.CODEC), player.position().add(0, player.getEyeHeight(player.getPose()), 0), yaw, pitch)
						.orElse(null);

					    if (entityToSpawn == null) {
						return;
					    }

					    Vec3 rotationVector = player.getLookAngle();
					    Vec3 velocity = player.getDeltaMovement();
					    RandomSource random = serverWorld.getRandom();

					    if (entityToSpawn instanceof Projectile projectileToSpawn) {
						if (projectileToSpawn instanceof AbstractHurtingProjectile explosiveProjectileToSpawn) {
						    explosiveProjectileToSpawn.xPower = rotationVector.x * speed;
						    explosiveProjectileToSpawn.yPower = rotationVector.y * speed;
						    explosiveProjectileToSpawn.zPower = rotationVector.z * speed;
						}

						projectileToSpawn.setOwner(player);
						projectileToSpawn.shootFromRotation(player, pitch, yaw, 0F, speed, finalDivergence);

					    } else {

						float f = 0.017453292F;
						double g = 0.007499999832361937D;

						float h = -Mth.sin(yaw * f) * Mth.cos(pitch * f);
						float i = -Mth.sin(pitch * f);
						float j = Mth.cos(yaw * f) * Mth.cos(pitch * f);

						Vec3 vec3d = new Vec3(h, i, j)
						    .normalize()
						    .add(random.nextGaussian() * g * finalDivergence, random.nextGaussian() * g * finalDivergence, random.nextGaussian() * g * finalDivergence)
						    .scale(speed);

						entityToSpawn.setDeltaMovement(vec3d);
						entityToSpawn.setDeltaMovement(velocity.x, player.onGround() ? 0.0D : velocity.y, velocity.z);

					    }

					    if (MiscUtils.ParserUtils.parseJson(new com.mojang.brigadier.StringReader(tag), CompoundTag.CODEC).isEmpty()) {
						CompoundTag mergedTag = entityToSpawn.saveWithoutId(new CompoundTag());
						mergedTag.merge(MiscUtils.ParserUtils.parseJson(new com.mojang.brigadier.StringReader(tag), CompoundTag.CODEC));

						entityToSpawn.load(mergedTag);

					    }

					    if (entityToSpawn.getBukkitEntity() instanceof org.bukkit.entity.Projectile proj) {
						proj.setShooter(p);
					    }

					    serverWorld.tryAddFreshEntityWithPassengers(entityToSpawn);
					    org.bukkit.entity.Entity bukkit = entityToSpawn.getBukkitEntity();
					    Actions.executeEntity(bukkit, power.getJsonObject("projectile_action"));
					    Actions.executeEntity(p, power.getJsonObject("shooter_action"));

					    shotsLeft++;
					}
				    }
				}.runTaskTimer(GenesisMC.getPlugin(), start_delay, interval);
			    }
			}
		    }
		}
	    }
	}
    }


    @Override
    public String getType() {
	return "apoli:fire_projectile";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
	return fire_projectile;
    }
}
