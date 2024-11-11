package io.github.dueris.originspaper.registry;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.type.BiEntityActionType;
import io.github.dueris.originspaper.action.type.BlockActionType;
import io.github.dueris.originspaper.action.type.EntityActionType;
import io.github.dueris.originspaper.action.type.ItemActionType;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.*;
import io.github.dueris.originspaper.data.ContainerType;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.registry.fabric.FabricRegistryBuilder;
import io.github.dueris.originspaper.util.modifier.IModifierOperation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class ApoliRegistries {

	public static final Registry<PowerConfiguration<PowerType>> POWER_TYPE = create(ApoliRegistryKeys.POWER_TYPE);

	public static final Registry<ConditionConfiguration<BiEntityConditionType>> BIENTITY_CONDITION_TYPE = create(ApoliRegistryKeys.BIENTITY_CONDITION_TYPE);
	public static final Registry<ConditionConfiguration<BiomeConditionType>> BIOME_CONDITION_TYPE = create(ApoliRegistryKeys.BIOME_CONDITION_TYPE);
	public static final Registry<ConditionConfiguration<BlockConditionType>> BLOCK_CONDITION_TYPE = create(ApoliRegistryKeys.BLOCK_CONDITION_TYPE);
	public static final Registry<ConditionConfiguration<DamageConditionType>> DAMAGE_CONDITION_TYPE = create(ApoliRegistryKeys.DAMAGE_CONDITION_TYPE);
	public static final Registry<ConditionConfiguration<EntityConditionType>> ENTITY_CONDITION_TYPE = create(ApoliRegistryKeys.ENTITY_CONDITION_TYPE);
	public static final Registry<ConditionConfiguration<FluidConditionType>> FLUID_CONDITION_TYPE = create(ApoliRegistryKeys.FLUID_CONDITION_TYPE);
	public static final Registry<ConditionConfiguration<ItemConditionType>> ITEM_CONDITION_TYPE = create(ApoliRegistryKeys.ITEM_CONDITION_TYPE);

	public static final Registry<ActionConfiguration<BiEntityActionType>> BIENTITY_ACTION_TYPE = create(ApoliRegistryKeys.BIENTITY_ACTION_TYPE);
	public static final Registry<ActionConfiguration<BlockActionType>> BLOCK_ACTION_TYPE = create(ApoliRegistryKeys.BLOCK_ACTION_TYPE);
	public static final Registry<ActionConfiguration<EntityActionType>> ENTITY_ACTION_TYPE = create(ApoliRegistryKeys.ENTITY_ACTION_TYPE);
	public static final Registry<ActionConfiguration<ItemActionType>> ITEM_ACTION_TYPE = create(ApoliRegistryKeys.ITEM_ACTION_TYPE);

	public static final Registry<IModifierOperation> MODIFIER_OPERATION = create(ApoliRegistryKeys.MODIFIER_OPERATION);
	public static final Registry<ContainerType> CONTAINER_TYPE = create(ApoliRegistryKeys.CONTAINER_TYPE);

	private static <T> Registry<T> create(ResourceKey<Registry<T>> registryKey) {
		return FabricRegistryBuilder.createSimple(registryKey).buildAndRegister();
	}

}
