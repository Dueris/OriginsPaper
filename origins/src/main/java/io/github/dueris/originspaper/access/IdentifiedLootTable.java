package io.github.dueris.originspaper.access;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.level.storage.loot.LootTable;

public interface IdentifiedLootTable {

	void apoli$setKey(ResourceKey<LootTable> lootTableKey, ReloadableServerRegistries.Holder registryLookup);

	ResourceKey<LootTable> apoli$getLootTableKey();

}
