package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.content.NMSBootstrap;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.HudRender;
import io.github.dueris.originspaper.data.types.Keybind;
import io.github.dueris.originspaper.event.KeybindTriggerEvent;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentLinkedQueue;

public class FireProjectilePower extends PowerType implements CooldownInterface {
	private final int cooldown;
	private final int projectileCount;
	private final int interval;
	private final int startDelay;
	private final float speed;
	private final float divergence;
	private final SoundEvent soundEvent;
	private final EntityType<?> entityType;
	private final HudRender hudRender;
	private final CompoundTag tag;
	private final Keybind keybind;
	private final ActionFactory<Entity> projectileAction;
	private final ActionFactory<Entity> shooterAction;
	private final ConcurrentLinkedQueue<org.bukkit.entity.Entity> enderian_pearl = new ConcurrentLinkedQueue<>();
	protected long lastUseTime;
	private boolean isFiringProjectiles;
	private boolean finishedStartDelay;
	private int shotProjectiles;

	public FireProjectilePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
							   int cooldown, int count, int interval, int startDelay, float speed, float divergence, SoundEvent sound, EntityType<?> entityType, HudRender hudRender, CompoundTag tag,
							   Keybind keybind, ActionFactory<Entity> projectileAction, ActionFactory<Entity> shooterAction) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.cooldown = cooldown;
		this.projectileCount = count;
		this.interval = interval;
		this.startDelay = startDelay;
		this.speed = speed;
		this.divergence = divergence;
		this.soundEvent = sound;
		this.entityType = entityType;
		this.hudRender = hudRender;
		this.tag = tag;
		this.keybind = keybind;
		this.projectileAction = projectileAction;
		this.shooterAction = shooterAction;
	}

	public static InstanceDefiner buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("fire_projectile"))
			.add("cooldown", SerializableDataTypes.INT, 1)
			.add("count", SerializableDataTypes.INT, 1)
			.add("interval", SerializableDataTypes.POSITIVE_INT, 0)
			.add("start_delay", SerializableDataTypes.POSITIVE_INT, 0)
			.add("speed", SerializableDataTypes.FLOAT, 1.5F)
			.add("divergence", SerializableDataTypes.FLOAT, 1F)
			.add("sound", SerializableDataTypes.SOUND_EVENT, null)
			.add("entity_type", SerializableDataTypes.ENTITY_TYPE)
			.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
			.add("tag", SerializableDataTypes.NBT, new CompoundTag())
			.add("key", ApoliDataTypes.KEYBIND, Keybind.DEFAULT_KEYBIND)
			.add("projectile_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("shooter_action", ApoliDataTypes.ENTITY_ACTION, null);
	}

	@EventHandler
	public void enderianPearl(@NotNull PlayerTeleportEvent e) {
		if (enderian_pearl.contains(e.getPlayer()) && e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
			e.setCancelled(true);
			e.getPlayer().teleportAsync(e.getTo());
			e.getPlayer().setFallDistance(0);
			enderian_pearl.remove(e.getPlayer());
		}
	}

	@EventHandler
	public void onKeybind(@NotNull KeybindTriggerEvent e) {
		Player nms = ((CraftPlayer) e.getPlayer()).getHandle();
		if (e.getKey().equalsIgnoreCase(keybind.key()) && getPlayers().contains(nms) && !CooldownPower.isInCooldown(e.getPlayer(), this)) {
			this.lastUseTime = nms.level().getGameTime();
			this.isFiringProjectiles = true;
			CooldownPower.addCooldown(e.getPlayer(), this);
		}
	}

	@Override
	public void tick(Player entity) {
		if (isFiringProjectiles) {
			if (!finishedStartDelay && startDelay == 0) {
				finishedStartDelay = true;
			}
			if (!finishedStartDelay && (entity.getCommandSenderWorld().getGameTime() - lastUseTime) % startDelay == 0) {
				finishedStartDelay = true;
				shotProjectiles += 1;
				if (shotProjectiles <= projectileCount) {
					if (soundEvent != null) {
						entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), soundEvent, SoundSource.NEUTRAL, 0.5F, 0.4F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
					}
					if (!entity.level().isClientSide) {
						fireProjectile(entity);
					}
				} else {
					shotProjectiles = 0;
					finishedStartDelay = false;
					isFiringProjectiles = false;
				}
			} else if (interval == 0 && finishedStartDelay) {
				if (soundEvent != null) {
					entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), soundEvent, SoundSource.NEUTRAL, 0.5F, 0.4F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
				}
				if (!entity.level().isClientSide) {
					for (; shotProjectiles < projectileCount; shotProjectiles++) {
						fireProjectile(entity);
					}
				}
				shotProjectiles = 0;
				finishedStartDelay = false;
				isFiringProjectiles = false;
			} else if (finishedStartDelay && (entity.getCommandSenderWorld().getGameTime() - lastUseTime) % interval == 0) {
				shotProjectiles += 1;
				if (shotProjectiles <= projectileCount) {
					if (soundEvent != null) {
						entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), soundEvent, SoundSource.NEUTRAL, 0.5F, 0.4F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
					}
					if (!entity.level().isClientSide) {
						fireProjectile(entity);
					}
				} else {
					shotProjectiles = 0;
					finishedStartDelay = false;
					isFiringProjectiles = false;
				}
			}
		}
	}

	private void fireProjectile(Entity entity) {

		if (entityType == null || entity.level().isClientSide) return;

		ServerLevel serverWorld = (ServerLevel) entity.level();
		float yaw = entity.getYRot();
		float pitch = entity.getXRot();

		Entity entityToSpawn = Util
			.getEntityWithPassengers(serverWorld, entityType, tag, entity.position().add(0, entity.getEyeHeight(entity.getPose()), 0), yaw, pitch)
			.orElse(null);

		if (entityToSpawn == null) {
			return;
		}

		Vec3 rotationVector = entity.getLookAngle();
		Vec3 velocity = entity.getDeltaMovement();
		RandomSource random = serverWorld.getRandom();

		if (entityToSpawn instanceof Projectile projectileToSpawn) {

			if (projectileToSpawn instanceof AbstractHurtingProjectile explosiveProjectileToSpawn) {
				explosiveProjectileToSpawn.accelerationPower = speed;
			}

			projectileToSpawn.setOwner(entity);
			projectileToSpawn.shootFromRotation(entity, pitch, yaw, 0F, speed, divergence);

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
			entityToSpawn.push(velocity.x, entity.onGround() ? 0.0D : velocity.y, velocity.z);

		}

		if (tag.isEmpty()) {

			CompoundTag mergedTag = entityToSpawn.saveWithoutId(new CompoundTag());
			mergedTag.merge(tag);

			entityToSpawn.load(mergedTag);

		}

		serverWorld.tryAddFreshEntityWithPassengers(entityToSpawn);

		// OriginsPaper - Paper makes spawning projectiles resets the Owner upon adding the entity, so we need to reload the owner.
		if (entityToSpawn instanceof Projectile projectileToSpawn) {
			projectileToSpawn.setOwner(entity);

			if (entityType.equals(NMSBootstrap.ENDERIAN_PEARL_ENTITYTYPE)) {
				enderian_pearl.add(entity.getBukkitEntity());
			}
		}

		if (projectileAction != null) {
			projectileAction.accept(entityToSpawn);
		}

		if (shooterAction != null) {
			shooterAction.accept(entity);
		}

	}

	@Override
	public int getCooldown() {
		return cooldown;
	}

	@Override
	public HudRender getHudRender() {
		return hudRender;
	}
}
