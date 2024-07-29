package me.dueris.originspaper.registry;

import io.github.dueris.calio.registry.RegistryKey;
import io.github.dueris.calio.util.Util;
import io.github.dueris.calio.util.holder.Pair;
import me.dueris.originspaper.factory.actions.ActionFactory;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.data.types.modifier.IModifierOperation;
import me.dueris.originspaper.registry.registries.Origin;
import me.dueris.originspaper.registry.registries.OriginLayer;
import me.dueris.originspaper.registry.registries.PowerType;
import me.dueris.originspaper.screen.ChoosingPage;
import me.dueris.originspaper.util.LangFile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Registries {
	public static final RegistryKey<Origin> ORIGIN = new RegistryKey<>(Origin.class, apoliIdentifier("origin"));
	public static final RegistryKey<OriginLayer> LAYER = new RegistryKey<>(OriginLayer.class, apoliIdentifier("layer"));
	public static final RegistryKey<PowerType> CRAFT_POWER = new RegistryKey<>(PowerType.class, apoliIdentifier("craft_power"));

	public static final RegistryKey<ConditionFactory<Fluid>> FLUID_CONDITION = new RegistryKey<>(Util.castClass(ConditionFactory.class), apoliIdentifier("fluid_condition"));
	public static final RegistryKey<ConditionFactory<Pair<Level, ItemStack>>> ITEM_CONDITION = new RegistryKey<>(Util.castClass(ConditionFactory.class), apoliIdentifier("item_condition"));
	public static final RegistryKey<ConditionFactory<Entity>> ENTITY_CONDITION = new RegistryKey<>(Util.castClass(ConditionFactory.class), apoliIdentifier("entity_condition"));
	public static final RegistryKey<ConditionFactory<EntityDamageEvent>> DAMAGE_CONDITION = new RegistryKey<>(Util.castClass(ConditionFactory.class), apoliIdentifier("damage_condition"));
	public static final RegistryKey<ConditionFactory<Pair<Entity, Entity>>> BIENTITY_CONDITION = new RegistryKey<>(Util.castClass(ConditionFactory.class), apoliIdentifier("bientity_condition"));
	public static final RegistryKey<ConditionFactory<BlockInWorld>> BLOCK_CONDITION = new RegistryKey<>(Util.castClass(ConditionFactory.class), apoliIdentifier("block_condition"));
	public static final RegistryKey<ConditionFactory<Holder<Biome>>> BIOME_CONDITION = new RegistryKey<>(Util.castClass(ConditionFactory.class), apoliIdentifier("biome_condition"));

	public static final RegistryKey<ActionFactory<Pair<Level, SlotAccess>>> ITEM_ACTION = new RegistryKey<>(Util.castClass(ActionFactory.class), apoliIdentifier("item_action"));
	public static final RegistryKey<ActionFactory<Entity>> ENTITY_ACTION = new RegistryKey<>(Util.castClass(ActionFactory.class), apoliIdentifier("entity_action"));
	public static final RegistryKey<ActionFactory<Pair<Entity, Entity>>> BIENTITY_ACTION = new RegistryKey<>(Util.castClass(ActionFactory.class), apoliIdentifier("bientity_action"));
	public static final RegistryKey<ActionFactory<Triple<Level, BlockPos, Direction>>> BLOCK_ACTION = new RegistryKey<>(Util.castClass(ActionFactory.class), apoliIdentifier("block_action"));

	public static final RegistryKey<LangFile> LANG = new RegistryKey<>(LangFile.class, identifier("lang_file"));
	public static final RegistryKey<ChoosingPage> CHOOSING_PAGE = new RegistryKey<>(ChoosingPage.class, originIdentifier("choosing_page"));
	public static final RegistryKey<IModifierOperation> MODIFIER_OPERATION = new RegistryKey<>(IModifierOperation.class, apoliIdentifier("modifier_operation"));

	@Contract("_ -> new")
	public static @NotNull ResourceLocation identifier(String path) {
		return ResourceLocation.fromNamespaceAndPath("originspaper", path);
	}

	@Contract("_ -> new")
	public static @NotNull ResourceLocation originIdentifier(String path) {
		return ResourceLocation.fromNamespaceAndPath("origins", path);
	}

	@Contract("_ -> new")
	public static @NotNull ResourceLocation apoliIdentifier(String path) {
		return ResourceLocation.fromNamespaceAndPath("apoli", path);
	}
}
