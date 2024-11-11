package io.github.dueris.originspaper.power.type.origins;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;

public class OriginsPowerTypes {

	public static void register() {
		register(OriginsCallbackPowerType.getFactory());
		register(LikeWaterPowerType.createSimpleFactory(OriginsPaper.identifier("like_water"), LikeWaterPowerType::new));
		register(WaterBreathingPowerType.createSimpleFactory(OriginsPaper.identifier("water_breathing"), WaterBreathingPowerType::new));
		register(ScareCreepersPowerType.createSimpleFactory(OriginsPaper.identifier("scare_creepers"), ScareCreepersPowerType::new));
//		register(WaterVisionPowerType.createSimpleFactory(OriginsPaper.originIdentifier("water_vision"), WaterVisionPowerType::new));
		register(ConduitPowerOnLandPowerType.createSimpleFactory(OriginsPaper.identifier("conduit_power_on_land"), ConduitPowerOnLandPowerType::new));
	}

	private static void register(PowerTypeFactory<?> serializer) {
		Registry.register(ApoliRegistries.POWER_FACTORY, serializer.getSerializerId(), serializer);
	}

}
