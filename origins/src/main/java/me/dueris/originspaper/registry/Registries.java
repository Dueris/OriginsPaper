package me.dueris.originspaper.registry;

import me.dueris.calio.registry.RegistryKey;
import me.dueris.originspaper.factory.actions.types.BiEntityActions;
import me.dueris.originspaper.factory.actions.types.BlockActions;
import me.dueris.originspaper.factory.actions.types.EntityActions;
import me.dueris.originspaper.factory.actions.types.ItemActions;
import me.dueris.originspaper.factory.conditions.types.*;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.registry.registries.DatapackRepository;
import me.dueris.originspaper.registry.registries.Layer;
import me.dueris.originspaper.registry.registries.Origin;
import me.dueris.originspaper.screen.ChoosingPage;
import me.dueris.originspaper.util.LangFile;
import me.dueris.originspaper.util.TextureLocation;
import net.minecraft.resources.ResourceLocation;


public class Registries {
	public static final RegistryKey<Origin> ORIGIN = new RegistryKey<>(Origin.class, apoliIdentifier("origin"));
	public static final RegistryKey<Layer> LAYER = new RegistryKey<>(Layer.class, apoliIdentifier("layer"));
	public static final RegistryKey<PowerType> CRAFT_POWER = new RegistryKey<>(PowerType.class, apoliIdentifier("craft_power"));

	public static final RegistryKey<FluidConditions.ConditionFactory> FLUID_CONDITION = new RegistryKey<>(FluidConditions.ConditionFactory.class, apoliIdentifier("fluid_condition"));
	public static final RegistryKey<ItemConditions.ConditionFactory> ITEM_CONDITION = new RegistryKey<>(ItemConditions.ConditionFactory.class, apoliIdentifier("item_condition"));
	public static final RegistryKey<EntityConditions.ConditionFactory> ENTITY_CONDITION = new RegistryKey<>(EntityConditions.ConditionFactory.class, apoliIdentifier("entity_condition"));
	public static final RegistryKey<DamageConditions.ConditionFactory> DAMAGE_CONDITION = new RegistryKey<>(DamageConditions.ConditionFactory.class, apoliIdentifier("damage_condition"));
	public static final RegistryKey<BiEntityConditions.ConditionFactory> BIENTITY_CONDITION = new RegistryKey<>(BiEntityConditions.ConditionFactory.class, apoliIdentifier("bientity_condition"));
	public static final RegistryKey<BlockConditions.ConditionFactory> BLOCK_CONDITION = new RegistryKey<>(BlockConditions.ConditionFactory.class, apoliIdentifier("block_condition"));
	public static final RegistryKey<BiomeConditions.ConditionFactory> BIOME_CONDITION = new RegistryKey<>(BiomeConditions.ConditionFactory.class, apoliIdentifier("biome_condition"));

	public static final RegistryKey<ItemActions.ActionFactory> ITEM_ACTION = new RegistryKey<>(ItemActions.ActionFactory.class, apoliIdentifier("item_action"));
	public static final RegistryKey<EntityActions.ActionFactory> ENTITY_ACTION = new RegistryKey<>(EntityActions.ActionFactory.class, apoliIdentifier("entity_action"));
	public static final RegistryKey<BiEntityActions.ActionFactory> BIENTITY_ACTION = new RegistryKey<>(BiEntityActions.ActionFactory.class, apoliIdentifier("bientity_action"));
	public static final RegistryKey<BlockActions.ActionFactory> BLOCK_ACTION = new RegistryKey<>(BlockActions.ActionFactory.class, apoliIdentifier("block_action"));

	public static final RegistryKey<TextureLocation> TEXTURE_LOCATION = new RegistryKey<>(TextureLocation.class, apoliIdentifier("texture_location"));
	public static final RegistryKey<LangFile> LANG = new RegistryKey<>(LangFile.class, identifier("lang_file"));
	public static final RegistryKey<DatapackRepository> PACK_SOURCE = new RegistryKey<>(DatapackRepository.class, apoliIdentifier("pack_source"));
	public static final RegistryKey<ChoosingPage> CHOOSING_PAGE = new RegistryKey<>(ChoosingPage.class, originIdentifier("choosing_page"));

	public static ResourceLocation identifier(String path) {
		return ResourceLocation.fromNamespaceAndPath("originspaper", path);
	}

	public static ResourceLocation originIdentifier(String path) {
		return ResourceLocation.fromNamespaceAndPath("origins", path);
	}

	public static ResourceLocation apoliIdentifier(String path) {
		return ResourceLocation.fromNamespaceAndPath("apoli", path);
	}
}