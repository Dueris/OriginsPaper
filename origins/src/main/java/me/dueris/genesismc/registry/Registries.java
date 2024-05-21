package me.dueris.genesismc.registry;

import me.dueris.calio.registry.RegistryKey;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.types.BiEntityActions;
import me.dueris.genesismc.factory.actions.types.BlockActions;
import me.dueris.genesismc.factory.actions.types.EntityActions;
import me.dueris.genesismc.factory.actions.types.ItemActions;
import me.dueris.genesismc.factory.conditions.types.*;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.registry.registries.DatapackRepository;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.screen.ChoosingPage;
import me.dueris.genesismc.util.TextureLocation;

public class Registries {
	public static final RegistryKey<Origin> ORIGIN = new RegistryKey<>(Origin.class, GenesisMC.apoliIdentifier("origin"));
	public static final RegistryKey<Layer> LAYER = new RegistryKey<>(Layer.class, GenesisMC.apoliIdentifier("layer"));
	public static final RegistryKey<PowerType> CRAFT_POWER = new RegistryKey<>(PowerType.class, GenesisMC.apoliIdentifier("craft_power"));

	public static final RegistryKey<FluidConditions.ConditionFactory> FLUID_CONDITION = new RegistryKey<>(FluidConditions.ConditionFactory.class, GenesisMC.apoliIdentifier("fluid_condition"));
	public static final RegistryKey<ItemConditions.ConditionFactory> ITEM_CONDITION = new RegistryKey<>(ItemConditions.ConditionFactory.class, GenesisMC.apoliIdentifier("item_condition"));
	public static final RegistryKey<EntityConditions.ConditionFactory> ENTITY_CONDITION = new RegistryKey<>(EntityConditions.ConditionFactory.class, GenesisMC.apoliIdentifier("entity_condition"));
	public static final RegistryKey<DamageConditions.ConditionFactory> DAMAGE_CONDITION = new RegistryKey<>(DamageConditions.ConditionFactory.class, GenesisMC.apoliIdentifier("damage_condition"));
	public static final RegistryKey<BiEntityConditions.ConditionFactory> BIENTITY_CONDITION = new RegistryKey<>(BiEntityConditions.ConditionFactory.class, GenesisMC.apoliIdentifier("bientity_condition"));
	public static final RegistryKey<BlockConditions.ConditionFactory> BLOCK_CONDITION = new RegistryKey<>(BlockConditions.ConditionFactory.class, GenesisMC.apoliIdentifier("block_condition"));
	public static final RegistryKey<BiomeConditions.ConditionFactory> BIOME_CONDITION = new RegistryKey<>(BiomeConditions.ConditionFactory.class, GenesisMC.apoliIdentifier("biome_condition"));

	public static final RegistryKey<ItemActions.ActionFactory> ITEM_ACTION = new RegistryKey<>(ItemActions.ActionFactory.class, GenesisMC.apoliIdentifier("item_action"));
	public static final RegistryKey<EntityActions.ActionFactory> ENTITY_ACTION = new RegistryKey<>(EntityActions.ActionFactory.class, GenesisMC.apoliIdentifier("entity_action"));
	public static final RegistryKey<BiEntityActions.ActionFactory> BIENTITY_ACTION = new RegistryKey<>(BiEntityActions.ActionFactory.class, GenesisMC.apoliIdentifier("bientity_action"));
	public static final RegistryKey<BlockActions.ActionFactory> BLOCK_ACTION = new RegistryKey<>(BlockActions.ActionFactory.class, GenesisMC.apoliIdentifier("block_action"));

	public static final RegistryKey<TextureLocation> TEXTURE_LOCATION = new RegistryKey<>(TextureLocation.class, GenesisMC.apoliIdentifier("texture_location"));
	public static final RegistryKey<DatapackRepository> PACK_SOURCE = new RegistryKey<>(DatapackRepository.class, GenesisMC.apoliIdentifier("pack_source"));
	public static final RegistryKey<ChoosingPage> CHOOSING_PAGE = new RegistryKey<>(ChoosingPage.class, GenesisMC.originIdentifier("choosing_page"));

}