package io.github.dueris.calio.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.dueris.calio.CraftCalio;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.world.flag.FeatureFlagSet;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {

	@Inject(method = "lambda$loadResources$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/SimpleReloadInstance;create(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Z)Lnet/minecraft/server/packs/resources/ReloadInstance;"))
	private static void calio$cacheDynamicRegistries(FeatureFlagSet featureSet, Commands.CommandSelection registrationEnvironment, int i, ResourceManager resourceManager, Executor executor, Executor executor2, @NotNull LayeredRegistryAccess<?> reloadedDynamicRegistries, CallbackInfoReturnable<CompletionStage<?>> cir, @Local ReloadableServerResources dataPackContents) {
		CraftCalio.DYNAMIC_REGISTRIES.put(Unit.INSTANCE, reloadedDynamicRegistries.compositeAccess());
		CraftCalio.DATA_PACK_CONTENTS.put(Unit.INSTANCE, dataPackContents);
	}
}
