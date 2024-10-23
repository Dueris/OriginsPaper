package io.github.dueris.calio.data;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public abstract class SimplePreparableReloadListener<T> implements PreparableReloadListener {
	@Override
	public CompletableFuture<Void> reload(
		PreparableReloadListener.@NotNull PreparationBarrier synchronizer,
		ResourceManager manager,
		ProfilerFiller prepareProfiler,
		ProfilerFiller applyProfiler,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		return CompletableFuture.<T>supplyAsync(() -> this.prepare(manager, prepareProfiler), prepareExecutor)
			.thenCompose(synchronizer::wait)
			.thenAcceptAsync(prepared -> this.apply((T) prepared, manager, applyProfiler), applyExecutor);
	}

	protected abstract T prepare(ResourceManager manager, ProfilerFiller profiler);

	protected abstract void apply(T prepared, ResourceManager manager, ProfilerFiller profiler);
}

