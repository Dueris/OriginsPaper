package io.github.dueris.originspaper.condition.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.condition.type.fluid.FluidConditionType;
import io.github.dueris.originspaper.condition.type.fluid.InTagConditionType;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FluidState;

import java.util.function.Predicate;

public class FluidConditionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static void register() {
		MetaConditionTypes.register(ApoliDataTypes.FLUID_CONDITION, FluidConditionTypes::register);
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("empty"), FluidState::isEmpty));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("still"), FluidState::isSource));
		register(InTagConditionType.getFactory());
		register(FluidConditionType.getFactory());
	}

	public static ConditionTypeFactory<FluidState> createSimpleFactory(ResourceLocation id, Predicate<FluidState> condition) {
		return new ConditionTypeFactory<>(id, new SerializableData(), (data, fluidState) -> condition.test(fluidState));
	}

	public static <F extends ConditionTypeFactory<FluidState>> F register(F conditionFactory) {
		return Registry.register(ApoliRegistries.FLUID_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
	}

}
