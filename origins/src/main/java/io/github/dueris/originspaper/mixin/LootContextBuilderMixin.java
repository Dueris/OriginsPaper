package io.github.dueris.originspaper.mixin;

import io.github.dueris.originspaper.access.ReplacingLootContext;
import io.github.dueris.originspaper.access.ReplacingLootContextParameterSet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootContext.Builder.class)
public class LootContextBuilderMixin {

	@Shadow
	@Final
	private LootParams params;

	@Inject(method = "create", at = @At("RETURN"))
	private void setLootContextType(@NotNull CallbackInfoReturnable<LootContext> cir) {
		ReplacingLootContextParameterSet rlcps = (ReplacingLootContextParameterSet) params;

		ReplacingLootContext rlc = (ReplacingLootContext) cir.getReturnValue();
		rlc.apoli$setType(rlcps.apoli$getType());
	}
}
