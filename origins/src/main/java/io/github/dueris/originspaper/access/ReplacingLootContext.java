package io.github.dueris.originspaper.access;

import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

public interface ReplacingLootContext {

	void apoli$setType(LootContextParamSet type);

	LootContextParamSet apoli$getType();

	void apoli$setReplaced(LootTable table);

	boolean apoli$isReplaced(LootTable table);
}
