package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.dueris.originspaper.access.IdentifiedLootTable;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ReloadableServerRegistries.Holder.class)
public abstract class ReloadableServerRegistriesHolderMixin {

	@Shadow
	public abstract RegistryAccess.Frozen get();

	@ModifyReturnValue(method = "getLootTable", at = @At("RETURN"))
	private LootTable apoli$replaceLootTableOnLookup(LootTable original, @NotNull ResourceKey<LootTable> lootTableKey) {
		if (original instanceof IdentifiedLootTable identifiedLootTable) {
			identifiedLootTable.apoli$setKey(lootTableKey, (ReloadableServerRegistries.Holder) (Object) this);
		}

		return original;
	}

}
