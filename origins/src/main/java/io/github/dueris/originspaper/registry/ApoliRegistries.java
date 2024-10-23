package io.github.dueris.originspaper.registry;

import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.registry.fabric.FabricRegistryBuilder;
import io.github.dueris.originspaper.util.modifier.IModifierOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.FluidState;
import org.apache.commons.lang3.tuple.Triple;

public class ApoliRegistries {

	public static final Registry<ConditionTypeFactory<Entity>> ENTITY_CONDITION = create(ApoliRegistryKeys.ENTITY_CONDITION);
	public static final Registry<ConditionTypeFactory<Tuple<Entity, Entity>>> BIENTITY_CONDITION = create(ApoliRegistryKeys.BIENTITY_CONDITION);
	public static final Registry<ConditionTypeFactory<Tuple<Level, ItemStack>>> ITEM_CONDITION = create(ApoliRegistryKeys.ITEM_CONDITION);
	public static final Registry<ConditionTypeFactory<BlockInWorld>> BLOCK_CONDITION = create(ApoliRegistryKeys.BLOCK_CONDITION);
	public static final Registry<ConditionTypeFactory<Tuple<DamageSource, Float>>> DAMAGE_CONDITION = create(ApoliRegistryKeys.DAMAGE_CONDITION);
	public static final Registry<ConditionTypeFactory<FluidState>> FLUID_CONDITION = create(ApoliRegistryKeys.FLUID_CONDITION);
	public static final Registry<ConditionTypeFactory<Tuple<BlockPos, Holder<Biome>>>> BIOME_CONDITION = create(ApoliRegistryKeys.BIOME_CONDITION);

	public static final Registry<ActionTypeFactory<Entity>> ENTITY_ACTION = create(ApoliRegistryKeys.ENTITY_ACTION);
	public static final Registry<ActionTypeFactory<Tuple<Level, SlotAccess>>> ITEM_ACTION = create(ApoliRegistryKeys.ITEM_ACTION);
	public static final Registry<ActionTypeFactory<Triple<Level, BlockPos, Direction>>> BLOCK_ACTION = create(ApoliRegistryKeys.BLOCK_ACTION);
	public static final Registry<ActionTypeFactory<Tuple<Entity, Entity>>> BIENTITY_ACTION = create(ApoliRegistryKeys.BIENTITY_ACTION);

	public static final Registry<IModifierOperation> MODIFIER_OPERATION = create(ApoliRegistryKeys.MODIFIER_OPERATION);
	public static final Registry<PowerTypeFactory<? extends PowerType>> POWER_FACTORY = create(ApoliRegistryKeys.POWER_FACTORY);

	private static <T> Registry<T> create(ResourceKey<Registry<T>> registryKey) {
		return FabricRegistryBuilder.createSimple(registryKey).buildAndRegister();
	}

}
