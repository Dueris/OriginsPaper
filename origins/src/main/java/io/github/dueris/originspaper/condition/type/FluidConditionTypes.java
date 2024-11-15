package io.github.dueris.originspaper.condition.type;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.FluidCondition;
import io.github.dueris.originspaper.condition.type.fluid.FluidFluidConditionType;
import io.github.dueris.originspaper.condition.type.fluid.InTagFluidConditionType;
import io.github.dueris.originspaper.condition.type.fluid.meta.AllOfFluidConditionType;
import io.github.dueris.originspaper.condition.type.fluid.meta.AnyOfFluidConditionType;
import io.github.dueris.originspaper.condition.type.fluid.meta.ConstantFluidConditionType;
import io.github.dueris.originspaper.condition.type.fluid.meta.RandomChanceFluidConditionType;
import io.github.dueris.originspaper.condition.type.meta.AllOfMetaConditionType;
import io.github.dueris.originspaper.condition.type.meta.AnyOfMetaConditionType;
import io.github.dueris.originspaper.condition.type.meta.ConstantMetaConditionType;
import io.github.dueris.originspaper.condition.type.meta.RandomChanceMetaConditionType;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;

public class FluidConditionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();
	public static final SerializableDataType<ConditionConfiguration<FluidConditionType>> DATA_TYPE = SerializableDataType.registry(ApoliRegistries.FLUID_CONDITION_TYPE, "apoli", ALIASES, (configurations, id) -> "Fluid condition type \"" + id + "\" is undefined!");

	public static final ConditionConfiguration<AllOfFluidConditionType> ALL_OF = register(AllOfMetaConditionType.createConfiguration(FluidCondition.DATA_TYPE, AllOfFluidConditionType::new));
	public static final ConditionConfiguration<AnyOfFluidConditionType> ANY_OF = register(AnyOfMetaConditionType.createConfiguration(FluidCondition.DATA_TYPE, AnyOfFluidConditionType::new));
	public static final ConditionConfiguration<ConstantFluidConditionType> CONSTANT = register(ConstantMetaConditionType.createConfiguration(ConstantFluidConditionType::new));
	public static final ConditionConfiguration<RandomChanceFluidConditionType> RANDOM_CHANCE = register(RandomChanceMetaConditionType.createConfiguration(RandomChanceFluidConditionType::new));

	public static final ConditionConfiguration<FluidFluidConditionType> FLUID = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("fluid"), FluidFluidConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<InTagFluidConditionType> IN_TAG = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("in_tag"), InTagFluidConditionType.DATA_FACTORY));

	public static void register() {

	}

	@SuppressWarnings("unchecked")
	public static <CT extends FluidConditionType> ConditionConfiguration<CT> register(ConditionConfiguration<CT> configuration) {

		ConditionConfiguration<FluidConditionType> casted = (ConditionConfiguration<FluidConditionType>) configuration;
		Registry.register(ApoliRegistries.FLUID_CONDITION_TYPE, casted.id(), casted);

		return configuration;

	}

}
