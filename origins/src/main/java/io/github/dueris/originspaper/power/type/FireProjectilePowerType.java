package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.action.EntityAction;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.HudRender;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Deprecated
public class FireProjectilePowerType extends ActiveCooldownPowerType {

	public static final TypedDataObjectFactory<FireProjectilePowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("projectile_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("shooter_action", EntityAction.DATA_TYPE.optional(), Optional.empty())
			.add("entity_type", SerializableDataTypes.ENTITY_TYPE)
			.add("tag", SerializableDataTypes.NBT_COMPOUND, new CompoundTag())
			.add("sound", SerializableDataTypes.SOUND_EVENT.optional(), Optional.empty())
			.add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Key())
			.add("hud_render", HudRender.DATA_TYPE, HudRender.DONT_RENDER)
			.add("cooldown", SerializableDataTypes.INT, 1)
			.add("count", SerializableDataTypes.INT, 1)
			.add("interval", SerializableDataTypes.NON_NEGATIVE_INT, 0)
			.add("start_delay", SerializableDataTypes.NON_NEGATIVE_INT, 0)
			.add("speed", SerializableDataTypes.FLOAT, 1.5F)
			.add("divergence", SerializableDataTypes.FLOAT, 1.0F),
		(data, condition) -> new FireProjectilePowerType(
			data.get("projectile_action"),
			data.get("shooter_action"),
			data.get("entity_type"),
			data.get("tag"),
			data.get("sound"),
			data.get("key"),
			data.get("hud_render"),
			data.get("cooldown"),
			data.get("count"),
			data.get("interval"),
			data.get("start_delay"),
			data.get("speed"),
			data.get("divergence"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("projectile_action", powerType.projectileAction)
			.set("shooter_action", powerType.shooterAction)
			.set("entity_type", powerType.entityType)
			.set("tag", powerType.tag)
			.set("sound", powerType.soundEvent)
			.set("key", powerType.getKey())
			.set("hud_render", powerType.getRenderSettings())
			.set("cooldown", powerType.getCooldown())
			.set("count", powerType.projectileCount)
			.set("interval", powerType.interval)
			.set("start_delay", powerType.startDelay)
			.set("speed", powerType.speed)
			.set("divergence", powerType.divergence)
	);

	private final Optional<EntityAction> projectileAction;
	private final Optional<EntityAction> shooterAction;

	private final EntityType<?> entityType;
	private final CompoundTag tag;

	private final Optional<SoundEvent> soundEvent;

	private final int projectileCount;
	private final int interval;
	private final int startDelay;

	private final float speed;
	private final float divergence;

	private boolean isFiringProjectiles;
	private boolean finishedStartDelay;
	private int shotProjectiles;

	public FireProjectilePowerType(Optional<EntityAction> projectileAction, Optional<EntityAction> shooterAction, EntityType<?> entityType, CompoundTag tag, Optional<SoundEvent> soundEvent, Key key, HudRender hudRender, int cooldownDuration, int projectileCount, int interval, int startDelay, float speed, float divergence, Optional<EntityCondition> condition) {
		super(hudRender, cooldownDuration, key, condition);
		this.projectileAction = projectileAction;
		this.shooterAction = shooterAction;
		this.entityType = entityType;
		this.tag = tag;
		this.soundEvent = soundEvent;
		this.projectileCount = projectileCount;
		this.interval = interval;
		this.startDelay = startDelay;
		this.speed = speed;
		this.divergence = divergence;
		this.setTicking(true);
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.FIRE_PROJECTILE;
	}

	@Override
	public void onUse() {
		if (canUse()) {
			isFiringProjectiles = true;
			use();
		}
	}

	@Override
	public Tag toTag() {

		CompoundTag nbt = new CompoundTag();

		nbt.putLong("LastUseTime", lastUseTime);
		nbt.putInt("ShotProjectiles", shotProjectiles);
		nbt.putBoolean("FinishedStartDelay", finishedStartDelay);
		nbt.putBoolean("IsFiringProjectiles", isFiringProjectiles);

		return nbt;

	}

	@Override
	public void fromTag(Tag tag) {

		if (tag instanceof LongTag nbtLong) {
			this.lastUseTime = nbtLong.getAsLong();
		} else if (tag instanceof CompoundTag nbtCompound) {
			this.lastUseTime = nbtCompound.getLong("LastUseTime");
			this.shotProjectiles = nbtCompound.getInt("ShotProjectiles");
			this.finishedStartDelay = nbtCompound.getBoolean("FinishedStartDelay");
			this.isFiringProjectiles = nbtCompound.getBoolean("IsFiringProjectiles");
		}

	}

	public void serverTick() {

		LivingEntity holder = getHolder();

		if (isFiringProjectiles) {

			if (!finishedStartDelay && startDelay == 0) {
				finishedStartDelay = true;
			}

			if (!finishedStartDelay && (holder.getCommandSenderWorld().getGameTime() - lastUseTime) % startDelay == 0) {

				this.finishedStartDelay = true;
				this.shotProjectiles++;

				if (shotProjectiles <= projectileCount) {

					soundEvent.ifPresent(event -> holder.level().playSound(null, holder.getX(), holder.getY(), holder.getZ(), event, SoundSource.NEUTRAL, 0.5F, 0.4F / (holder.getRandom().nextFloat() * 0.4F + 0.8F)));

					if (!holder.level().isClientSide()) {
						fireProjectile();
					}

				} else {
					shotProjectiles = 0;
					finishedStartDelay = false;
					isFiringProjectiles = false;
				}

			} else if (interval == 0 && finishedStartDelay) {

				soundEvent.ifPresent(event -> holder.level().playSound(null, holder.getX(), holder.getY(), holder.getZ(), event, SoundSource.NEUTRAL, 0.5F, 0.4F / (holder.getRandom().nextFloat() * 0.4F + 0.8F)));

				if (!holder.level().isClientSide()) {

					for (; shotProjectiles < projectileCount; shotProjectiles++) {
						fireProjectile();
					}

				}

				this.shotProjectiles = 0;
				this.finishedStartDelay = false;
				this.isFiringProjectiles = false;

			} else if (finishedStartDelay && (holder.getCommandSenderWorld().getGameTime() - lastUseTime) % interval == 0) {

				this.shotProjectiles++;

				if (shotProjectiles <= projectileCount) {

					soundEvent.ifPresent(event -> holder.level().playSound(null, holder.getX(), holder.getY(), holder.getZ(), event, SoundSource.NEUTRAL, 0.5F, 0.4F / (holder.getRandom().nextFloat() * 0.4F + 0.8F)));

					if (!holder.level().isClientSide) {
						fireProjectile();
					}

				} else {
					shotProjectiles = 0;
					finishedStartDelay = false;
					isFiringProjectiles = false;
				}

			}

		}

	}

	private void fireProjectile() {

		LivingEntity holder = getHolder();
		if (!(holder.level() instanceof ServerLevel serverWorld)) {
			return;
		}

		RandomSource random = serverWorld.getRandom();

		Vec3 velocity = holder.getDeltaMovement();
		Vec3 verticalOffset = holder.position().add(0, holder.getEyeHeight(holder.getPose()), 0);

		float pitch = holder.getXRot();
		float yaw = holder.getYRot();

		Entity entityToSpawn = Util
			.getEntityWithPassengersSafe(serverWorld, entityType, tag, verticalOffset, yaw, pitch)
			.orElse(null);

		if (entityToSpawn == null) {
			return;
		}

		if (entityToSpawn instanceof Projectile projectileToSpawn) {

			if (projectileToSpawn instanceof AbstractHurtingProjectile explosiveProjectileToSpawn) {
				explosiveProjectileToSpawn.accelerationPower = speed;
			}

			projectileToSpawn.setOwner(holder);
			projectileToSpawn.shootFromRotation(holder, pitch, yaw, 0F, speed, divergence);

		} else {

			float j = 0.017453292F;
			double k = 0.007499999832361937D;

			float l = -Mth.sin(yaw * j) * Mth.cos(pitch * j);
			float m = -Mth.sin(pitch * j);
			float n = Mth.cos(yaw * j) * Mth.cos(pitch * j);

			Vec3 velocityToApply = new Vec3(l, m, n)
				.normalize()
				.add(random.nextGaussian() * k * divergence, random.nextGaussian() * k * divergence, random.nextGaussian() * k * divergence)
				.scale(speed);

			entityToSpawn.setDeltaMovement(velocityToApply);
			entityToSpawn.push(velocity.x, holder.onGround() ? 0.0D : velocity.y, velocity.z);

		}

		if (!tag.isEmpty()) {

			CompoundTag mergedTag = entityToSpawn.saveWithoutId(new CompoundTag());
			mergedTag.merge(tag);

			entityToSpawn.load(mergedTag);

		}

		serverWorld.tryAddFreshEntityWithPassengers(entityToSpawn);

		projectileAction.ifPresent(action -> action.execute(entityToSpawn));
		shooterAction.ifPresent(action -> action.execute(holder));

	}

}
