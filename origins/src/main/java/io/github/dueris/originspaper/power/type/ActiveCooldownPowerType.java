package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.HudRender;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class ActiveCooldownPowerType extends CooldownPowerType implements Active {

	private final Key key;

	public ActiveCooldownPowerType(HudRender hudRender, int cooldownDuration, Key key, Optional<EntityCondition> condition) {
		super(cooldownDuration, hudRender, condition);
		this.key = key;
	}

	public ActiveCooldownPowerType(HudRender hudRender, int cooldownDuration, Key key) {
		this(hudRender, cooldownDuration, key, Optional.empty());
	}

	@Override
	public abstract @NotNull PowerConfiguration<?> getConfig();

	@Override
	public void onUse() {
		use();
	}

	@Override
	public Key getKey() {
		return key;
	}

}
