package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.calio.data.types.RequiredInstance;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.KeybindTriggerEvent;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.data.types.HudRender;
import me.dueris.originspaper.factory.data.types.JsonKeybind;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.util.KeybindUtil;
import me.dueris.originspaper.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class FireProjectile extends PowerType implements KeyedPower, CooldownPower {
	private static final ArrayList<Player> doubleFirePatch = new ArrayList<>();
	public static HashMap<Player, ArrayList<String>> in_continuous = new HashMap<>();
	public static ArrayList<Player> enderian_pearl = new ArrayList<>();
	public static ArrayList<Player> in_cooldown_patch = new ArrayList<>();
	private final NamespacedKey entityType;
	private final int cooldown;
	private final HudRender hudRender;
	private final int count;
	private final int interval;
	private final int startDelay;
	private final float speed;
	private final float providedDivergence;
	private final Sound sound;
	private final CompoundTag tag;
	private final JsonKeybind keybind;
	private final FactoryJsonObject projectileAction;
	private final FactoryJsonObject shooterAction;

	public FireProjectile(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, NamespacedKey entityType, int cooldown, FactoryJsonObject hudRender, int count, int interval, int startDelay, float speed, float divergence, Sound sound, CompoundTag tag, FactoryElement key, FactoryJsonObject projectileAction, FactoryJsonObject shooterAction) {
		super(name, description, hidden, condition, loading_priority);
		this.entityType = entityType;
		this.cooldown = cooldown;
		this.hudRender = HudRender.createHudRender(hudRender);
		this.count = count;
		this.interval = interval;
		this.startDelay = startDelay;
		this.speed = speed;
		this.providedDivergence = divergence;
		this.sound = sound;
		this.tag = tag;
		this.keybind = JsonKeybind.createJsonKeybind(key);
		this.projectileAction = projectileAction;
		this.shooterAction = shooterAction;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("fire_projectile"))
			.add("entity_type", NamespacedKey.class, new RequiredInstance())
			.add("cooldown", int.class, 1)
			.add("hud_render", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("count", int.class, 1)
			.add("interval", int.class, 0)
			.add("start_delay", int.class, 0)
			.add("speed", float.class, 1.5F)
			.add("divergence", float.class, 1.0F)
			.add("sound", Sound.class, new OptionalInstance())
			.add("tag", CompoundTag.class, new OptionalInstance())
			.add("key", FactoryElement.class, new FactoryElement(new Gson().fromJson("{\"key\": \"key.origins.primary_active\"}", JsonElement.class)))
			.add("projectile_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("shooter_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	public static void addCooldownPatch(Player p) {
		in_cooldown_patch.add(p);
		new BukkitRunnable() {
			@Override
			public void run() {
				in_cooldown_patch.remove(p);
			}
		}.runTaskLater(OriginsPaper.getPlugin(), 5);
	}

	@EventHandler
	public void inContinuousFix(KeybindTriggerEvent e) {
		Player p = e.getPlayer();
		if (getPlayers().contains(p)) {
			if (KeybindUtil.isKeyActive(getJsonKey().key(), p)) {
				in_continuous.putIfAbsent(p, new ArrayList<>());
				if (getJsonKey().continuous()) {
					if (in_continuous.get(p).contains(getJsonKey().key())) {
						in_continuous.get(p).remove(getJsonKey().key());
					} else {
						in_continuous.get(p).add(getJsonKey().key());
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
		if (getPlayers().contains(p)) {
			if (isActive(p)) {
				if (!Cooldown.isInCooldown(p, this)) {
					if (KeybindUtil.isKeyActive(getJsonKey().key(), p)) {
						// Slight random divergence
						float divergence = providedDivergence + (float) ((Math.random() - 0.5) * 0.05);

						EntityType type;
						if (entityType.asString().equalsIgnoreCase("origins:enderian_pearl")) {
							type = EntityType.ENDER_PEARL;
							enderian_pearl.add(p);
						} else {
							type = EntityType.valueOf(entityType.asString().split(":")[1].toUpperCase());
							enderian_pearl.remove(p);
						}
						String key = getJsonKey().key();

						boolean cont = !getJsonKey().continuous();
						ServerPlayer player = ((CraftPlayer) p).getHandle();
						new BukkitRunnable() {
							int shotsLeft = (-count) + 1;

							@Override
							public void run() {
								if (shotsLeft > 0) {
									if ((!cont || !KeybindUtil.activeKeys.get(p).contains(key)) && !in_continuous.get(p).contains(key)) {
										Cooldown.addCooldown(p, cooldown, getSelf());
										this.cancel();
									}
									return;
								}
								addCooldownPatch(p);

								if (type.getEntityClass() != null) {
									if (doubleFirePatch.contains(p)) return;

									ServerLevel serverWorld = (ServerLevel) player.level();
									float yaw = player.getYRot();
									float pitch = player.getXRot();

									Entity entityToSpawn = Util
										.getEntityWithPassengers(serverWorld, CraftEntityType.bukkitToMinecraft(type), tag, player.position().add(0, player.getEyeHeight(player.getPose()), 0), yaw, pitch)
										.orElse(null);

									if (entityToSpawn == null) {
										return;
									}

									Vec3 rotationVector = player.getLookAngle();
									Vec3 velocity = player.getDeltaMovement();
									RandomSource random = serverWorld.getRandom();

									if (entityToSpawn instanceof Projectile projectileToSpawn) {
										if (projectileToSpawn instanceof AbstractHurtingProjectile explosiveProjectileToSpawn) {
											Vec3 vector = new Vec3(rotationVector.x * speed, rotationVector.y * speed, rotationVector.z * speed);
											explosiveProjectileToSpawn.assignDirectionalMovement(
												vector, vector.length()
											);
										}

										projectileToSpawn.setOwner(player);
										projectileToSpawn.shootFromRotation(player, pitch, yaw, 0F, speed, divergence);

									} else {
										float f = 0.017453292F;
										double g = 0.007499999832361937D;

										float h = -Mth.sin(yaw * f) * Mth.cos(pitch * f);
										float i = -Mth.sin(pitch * f);
										float j = Mth.cos(yaw * f) * Mth.cos(pitch * f);

										Vec3 vec3d = new Vec3(h, i, j)
											.normalize()
											.add(random.nextGaussian() * g * divergence, random.nextGaussian() * g * divergence, random.nextGaussian() * g * divergence)
											.scale(speed);

										entityToSpawn.setDeltaMovement(vec3d);
										entityToSpawn.setDeltaMovement(velocity.x, player.onGround() ? 0.0D : velocity.y, velocity.z);

									}

									if (tag != null) {
										CompoundTag mergedTag = entityToSpawn.saveWithoutId(new CompoundTag());
										mergedTag.merge(tag);

										entityToSpawn.load(mergedTag);
									}

									if (entityToSpawn.getBukkitEntity() instanceof org.bukkit.entity.Projectile proj) {
										proj.setShooter(p);
									}

									serverWorld.tryAddFreshEntityWithPassengers(entityToSpawn);
									org.bukkit.entity.Entity bukkit = entityToSpawn.getBukkitEntity();
									Actions.executeEntity(bukkit, projectileAction);
									Actions.executeEntity(p, shooterAction);

									shotsLeft++;
								}
							}
						}.runTaskTimer(OriginsPaper.getPlugin(), startDelay, interval);
					}
				}
			}
		}
	}

	private FireProjectile getSelf() {
		return this;
	}

	@Override
	public int getCooldown() {
		return cooldown;
	}

	@Override
	public JsonKeybind getJsonKey() {
		return keybind;
	}

	@Override
	public HudRender getHudRender() {
		return hudRender;
	}

	public NamespacedKey getEntityType() {
		return entityType;
	}

	public Sound getSound() {
		return sound;
	}

	public int getInterval() {
		return interval;
	}

	public FactoryJsonObject getProjectileAction() {
		return projectileAction;
	}

	public FactoryJsonObject getShooterAction() {
		return shooterAction;
	}

	public float getDivergence() {
		return providedDivergence;
	}

	public float getSpeed() {
		return speed;
	}

	public int getCount() {
		return count;
	}

	public int getStartDelay() {
		return startDelay;
	}
}
