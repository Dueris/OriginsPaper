package io.github.dueris.originspaper.mixin;

import io.github.dueris.originspaper.access.ReplacingLootContextParameterSet;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LootParams.class)
public class LootParamsMixin implements ReplacingLootContextParameterSet {

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

}
