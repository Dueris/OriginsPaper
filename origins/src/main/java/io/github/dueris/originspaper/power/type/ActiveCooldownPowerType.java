package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.HudRender;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public class ActiveCooldownPowerType extends CooldownPowerType implements Active {

	private final Consumer<Entity> entityAction;
	private final Key key;

	public ActiveCooldownPowerType(Power power, LivingEntity entity, Consumer<Entity> entityAction, HudRender hudRender, int cooldownDuration, Key key) {
		super(power, entity, cooldownDuration, hudRender);
		this.entityAction = entityAction;
		this.key = key;
	}

	public static PowerTypeFactory<?> getActiveSelfFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("active_self"),
			new SerializableData()
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION)
				.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
				.add("cooldown", SerializableDataTypes.INT, 1)
				.add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Key()),
			data -> (power, entity) -> new ActiveCooldownPowerType(power, entity,
				data.get("entity_action"),
				data.get("hud_render"),
				data.get("cooldown"),
				data.get("key")
			)
		).allowCondition();
	}

	public static PowerTypeFactory<?> getLaunchFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("launch"),
			new SerializableData()
				.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
				.add("cooldown", SerializableDataTypes.INT, 1)
				.add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Key())
				.add("sound", SerializableDataTypes.SOUND_EVENT, null)
				.add("speed", SerializableDataTypes.FLOAT),
			data -> (power, entity) -> {

				Consumer<Entity> entityAction = e -> {

					if (!(entity.level() instanceof ServerLevel serverWorld)) {
						return;
					}

					entity.push(0, data.getFloat("speed"), 0);
					entity.hurtMarked = true;

					if (data.isPresent("sound")) {
						entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent) data.get("sound"), SoundSource.NEUTRAL, 0.5F, 0.4F / (entity.getRandom().nextFloat()));
					}

					for (int i = 0; i < 4; i++) {
						serverWorld.sendParticles(ParticleTypes.CLOUD, entity.getX(), entity.getRandomY(), entity.getZ(), 8, entity.getRandom().nextGaussian(), 0.0D, entity.getRandom().nextGaussian(), 0.5);
					}

				};

				return new ActiveCooldownPowerType(power, entity,
					entityAction,
					data.get("hud_render"),
					data.get("cooldown"),
					data.get("key")
				);

			}
		).allowCondition();
	}

	@Override
	public void onUse() {

		if (canUse()) {
			this.entityAction.accept(this.entity);
			use();
		}

	}

	@Override
	public Key getKey() {
		return key;
	}

}

