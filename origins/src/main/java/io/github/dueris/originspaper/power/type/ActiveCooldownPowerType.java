package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.HudRender;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

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

	@Override
	public boolean canUse() {
		return super.canUse();
	}

	@Override
	public boolean canTrigger() {
		return super.isActive();
	}
}
