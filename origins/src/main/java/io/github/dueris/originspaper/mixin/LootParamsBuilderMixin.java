package io.github.dueris.originspaper.mixin;

import io.github.dueris.originspaper.access.ReplacingLootContextParameterSet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootParams.Builder.class)
public class LootParamsBuilderMixin {

	@Inject(method = "create", at = @At("RETURN"))
	private void setLootContextType(LootContextParamSet type, @NotNull CallbackInfoReturnable<LootContext> cir) {
		ReplacingLootContextParameterSet rlc = (ReplacingLootContextParameterSet) cir.getReturnValue();
		rlc.apoli$setType(type);
	}
}
