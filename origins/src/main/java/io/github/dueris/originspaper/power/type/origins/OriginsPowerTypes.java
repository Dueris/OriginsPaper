package io.github.dueris.originspaper.power.type.origins;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;

public class OriginsPowerTypes {
	public static final PowerConfiguration<ConduitPowerOnLandPowerType> CONDUIT_POWER_ON_LAND = register(PowerConfiguration.simple(OriginsPaper.identifier("conduit_power_on_land"), ConduitPowerOnLandPowerType::new));
	public static final PowerConfiguration<LikeWaterPowerType> LIKE_WATER = register(PowerConfiguration.simple(OriginsPaper.identifier("like_water"), LikeWaterPowerType::new));
	public static final PowerConfiguration<OriginsCallbackPowerType> ACTION_ON_CALLBACK = register(PowerConfiguration.dataFactory(OriginsPaper.identifier("action_on_callback"), OriginsCallbackPowerType.DATA_FACTORY));
	public static final PowerConfiguration<ScareCreepersPowerType> SCARE_CREEPERS = register(PowerConfiguration.simple(OriginsPaper.identifier("scare_creepers"), ScareCreepersPowerType::new));
	public static final PowerConfiguration<WaterBreathingPowerType> WATER_BREATHING = register(PowerConfiguration.simple(OriginsPaper.identifier("water_breathing"), WaterBreathingPowerType::new));

	public static void register() {
	}

	public static <T extends PowerType> PowerConfiguration<T> register(PowerConfiguration<T> configuration) {

		PowerConfiguration<PowerType> casted = (PowerConfiguration<PowerType>) configuration;
		Registry.register(ApoliRegistries.POWER_TYPE, casted.id(), casted);

		return configuration;

	}
}
