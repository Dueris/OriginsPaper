package io.github.dueris.originspaper.power.type.origins;

import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.power.type.PowerType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ConduitPowerOnLandPowerType extends PowerType {

	public ConduitPowerOnLandPowerType() {
		super(Optional.empty());
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return OriginsPowerTypes.CONDUIT_POWER_ON_LAND;
	}

}
