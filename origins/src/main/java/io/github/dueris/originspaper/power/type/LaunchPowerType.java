package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.HudRender;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class LaunchPowerType extends ActiveCooldownPowerType {

	public static final TypedDataObjectFactory<LaunchPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("sound", SerializableDataTypes.SOUND_EVENT.optional(), Optional.empty())
			.add("speed", SerializableDataTypes.FLOAT)
			.add("hud_render", HudRender.DATA_TYPE, HudRender.DONT_RENDER)
			.add("cooldown", SerializableDataTypes.INT, 1)
			.add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Key()),
		(data, condition) -> new LaunchPowerType(
			data.get("sound"),
			data.get("speed"),
			data.get("hud_render"),
			data.get("cooldown"),
			data.get("key"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("sound", powerType.sound)
			.set("speed", powerType.speed)
			.set("hud_render", powerType.getRenderSettings())
			.set("cooldown", powerType.getCooldown())
			.set("key", powerType.getKey())
	);

	private final Optional<SoundEvent> sound;
	private final float speed;

	public LaunchPowerType(Optional<SoundEvent> sound, float speed, HudRender hudRender, int cooldownDuration, Key key, Optional<EntityCondition> condition) {
		super(hudRender, cooldownDuration, key, condition);
		this.sound = sound;
		this.speed = speed;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.LAUNCH;
	}

	@Override
	public void onUse() {

		LivingEntity holder = getHolder();
		if (!(holder.level() instanceof ServerLevel serverWorld)) {
			return;
		}

		super.onUse();

		holder.push(0, speed, 0);
		holder.hurtMarked = true;

		sound.ifPresent(soundEvent -> serverWorld.playSound(null, holder.getX(), holder.getY(), holder.getZ(), soundEvent, SoundSource.NEUTRAL, 0.5F, 0.4F / holder.getRandom().nextFloat()));

		for (int i = 0; i < 4; i++) {
			serverWorld.sendParticles(ParticleTypes.CLOUD, holder.getX(), holder.getRandomY(), holder.getZ(), 8, holder.getRandom().nextGaussian(), 0.0D, holder.getRandom().nextGaussian(), 0.5);
		}

	}

}
