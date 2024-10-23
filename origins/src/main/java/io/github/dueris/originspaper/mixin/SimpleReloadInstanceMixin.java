package io.github.dueris.originspaper.mixin;

import io.github.dueris.originspaper.util.fabric.resource.FabricResourceManagerImpl;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ProfiledReloadInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(SimpleReloadInstance.class)
public class SimpleReloadInstanceMixin {

	@Unique
	private static final ThreadLocal<PackType> fabric_resourceType = new ThreadLocal<>();

	@ModifyArg(method = "create", index = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/SimpleReloadInstance;of(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;)Lnet/minecraft/server/packs/resources/SimpleReloadInstance;"))
	private static List<PreparableReloadListener> sortSimple(List<PreparableReloadListener> reloaders) {
		List<PreparableReloadListener> sorted = FabricResourceManagerImpl.sort(fabric_resourceType.get(), reloaders);
		fabric_resourceType.remove();
		return sorted;
	}

	@Redirect(method = "create", at = @At(value = "NEW", target = "(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;)Lnet/minecraft/server/packs/resources/ProfiledReloadInstance;"))
	private static @NotNull ProfiledReloadInstance sortProfiled(ResourceManager manager, List<PreparableReloadListener> reloaders, Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage) {
		List<PreparableReloadListener> sorted = FabricResourceManagerImpl.sort(fabric_resourceType.get(), reloaders);
		fabric_resourceType.remove();
		return new ProfiledReloadInstance(manager, sorted, prepareExecutor, applyExecutor, initialStage);
	}
}
