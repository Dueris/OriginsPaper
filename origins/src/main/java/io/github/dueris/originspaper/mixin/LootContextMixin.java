package io.github.dueris.originspaper.mixin;

import io.github.dueris.originspaper.access.ReplacingLootContext;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashSet;
import java.util.Set;

@Mixin(LootContext.class)
public class LootContextMixin implements ReplacingLootContext {

	@Unique
	private final Set<LootTable> apoli$replacedTables = new HashSet<>();
	@Unique
	private LootContextParamSet apoli$lootContextType;

	@Override
	public void apoli$setType(LootContextParamSet type) {
		apoli$lootContextType = type;
	}

	@Override
	public LootContextParamSet apoli$getType() {
		return apoli$lootContextType;
	}

	@Override
	public void apoli$setReplaced(LootTable table) {
		apoli$replacedTables.add(table);
	}

	@Override
	public boolean apoli$isReplaced(LootTable table) {
		return apoli$replacedTables.contains(table);
	}
}
