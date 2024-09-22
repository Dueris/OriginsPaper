package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.dueris.originspaper.OriginsPaper;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin<T> {

	@WrapOperation(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerResources;loadResources(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/LayeredRegistryAccess;Lnet/minecraft/world/flag/FeatureFlagSet;Lnet/minecraft/commands/Commands$CommandSelection;ILjava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
	private static CompletableFuture<ReloadableServerResources> prepareRegistryAccess(ResourceManager manager, LayeredRegistryAccess<RegistryLayer> dynamicRegistries, FeatureFlagSet enabledFeatures, Commands.CommandSelection environment, int functionPermissionLevel, Executor prepareExecutor, Executor applyExecutor, @NotNull Operation<CompletableFuture<ReloadableServerResources>> original) {
		CompletableFuture<ReloadableServerResources> future = original.call(
			manager,
			dynamicRegistries,
			enabledFeatures,
			environment,
			functionPermissionLevel,
			prepareExecutor,
			applyExecutor
		);

		OriginsPaper.REGISTRY_ACCESS.set(dynamicRegistries.compositeAccess());
		return future;
	}
}
