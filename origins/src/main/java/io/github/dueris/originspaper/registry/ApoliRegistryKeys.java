package io.github.dueris.originspaper.registry;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.types.modifier.IModifierOperation;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.screen.ChoosingPage;
import io.github.dueris.originspaper.util.LangFile;
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
import org.jetbrains.annotations.NotNull;

public class ApoliRegistryKeys {

	public static final ResourceKey<Registry<ConditionTypeFactory<Entity>>> ENTITY_CONDITION = create("entity_condition");
	public static final ResourceKey<Registry<ConditionTypeFactory<Tuple<Entity, Entity>>>> BIENTITY_CONDITION = create("bientity_condition");
	public static final ResourceKey<Registry<ConditionTypeFactory<Tuple<Level, ItemStack>>>> ITEM_CONDITION = create("item_condition");
	public static final ResourceKey<Registry<ConditionTypeFactory<BlockInWorld>>> BLOCK_CONDITION = create("block_condition");
	public static final ResourceKey<Registry<ConditionTypeFactory<Tuple<DamageSource, Float>>>> DAMAGE_CONDITION = create("damage_condition");
	public static final ResourceKey<Registry<ConditionTypeFactory<FluidState>>> FLUID_CONDITION = create("fluid_condition");
	public static final ResourceKey<Registry<ConditionTypeFactory<Tuple<BlockPos, Holder<Biome>>>>> BIOME_CONDITION = create("biome_condition");

	public static final ResourceKey<Registry<ActionTypeFactory<Entity>>> ENTITY_ACTION = create("entity_action");
	public static final ResourceKey<Registry<ActionTypeFactory<Tuple<Level, SlotAccess>>>> ITEM_ACTION = create("item_action");
	public static final ResourceKey<Registry<ActionTypeFactory<Triple<Level, BlockPos, Direction>>>> BLOCK_ACTION = create("block_action");
	public static final ResourceKey<Registry<ActionTypeFactory<Tuple<Entity, Entity>>>> BIENTITY_ACTION = create("bientity_action");

	public static final ResourceKey<Registry<IModifierOperation>> MODIFIER_OPERATION = create("modifier_operation");
	public static final ResourceKey<Registry<ChoosingPage>> CHOOSING_PAGE = create("choosing_page");
	public static final ResourceKey<Registry<LangFile>> LANG = create("lang");

	public static final ResourceKey<Registry<Origin>> ORIGIN = create("origin");
	public static final ResourceKey<Registry<OriginLayer>> ORIGIN_LAYER = create("origin_layer");
	public static final ResourceKey<Registry<PowerType>> POWER = create("power");

	private static <T> @NotNull ResourceKey<Registry<T>> create(String path) {
		return ResourceKey.createRegistryKey(OriginsPaper.apoliIdentifier(path));
	}
}
