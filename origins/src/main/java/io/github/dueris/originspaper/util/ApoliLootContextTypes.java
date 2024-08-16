package io.github.dueris.originspaper.util;

import com.google.common.collect.BiMap;
import io.github.dueris.calio.util.ReflectionUtils;
import io.github.dueris.originspaper.OriginsPaper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class ApoliLootContextTypes {

	public static final LootContextParamSet ANY = register(
			OriginsPaper.apoliIdentifier("any"),
			LootContextParamSet.builder()
					.optional(LootContextParams.THIS_ENTITY)
					.optional(LootContextParams.LAST_DAMAGE_PLAYER)
					.optional(LootContextParams.DAMAGE_SOURCE)
					.optional(LootContextParams.ATTACKING_ENTITY)
					.optional(LootContextParams.DIRECT_ATTACKING_ENTITY)
					.optional(LootContextParams.ORIGIN)
					.optional(LootContextParams.BLOCK_STATE)
					.optional(LootContextParams.BLOCK_ENTITY)
					.optional(LootContextParams.TOOL)
					.optional(LootContextParams.EXPLOSION_RADIUS)
	);

	private ApoliLootContextTypes() {
	}

	private static LootContextParamSet register(ResourceLocation id, LootContextParamSet.Builder lootContextTypeBuilder) {

		LootContextParamSet lootContextType = lootContextTypeBuilder.build();
		BiMap<ResourceLocation, LootContextParamSet> idAndLootContextTypeMap = (BiMap<ResourceLocation, LootContextParamSet>) ReflectionUtils.getStaticFieldValue(LootContextParamSets.class, "REGISTRY");

		if (idAndLootContextTypeMap.containsKey(id)) {
			throw new IllegalStateException("Loot table parameter set \"" + id + "\" is already registered!");
		}

		idAndLootContextTypeMap.put(id, lootContextType);
		return lootContextType;

	}

}
