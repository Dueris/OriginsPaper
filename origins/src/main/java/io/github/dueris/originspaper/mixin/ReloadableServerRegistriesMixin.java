package io.github.dueris.originspaper.mixin;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.DynamicOps;
import io.github.dueris.originspaper.registry.ModLoot;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.util.List;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

@Mixin(ReloadableServerRegistries.class)
public class ReloadableServerRegistriesMixin {
	@Unique
	private static final WeakHashMap<RegistryOps<JsonElement>, HolderLookup.Provider> WRAPPERS = new WeakHashMap<>();

	@WrapOperation(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerRegistries$EmptyTagLookupWrapper;createSerializationContext(Lcom/mojang/serialization/DynamicOps;)Lnet/minecraft/resources/RegistryOps;"))
	private static RegistryOps<JsonElement> storeOps(@Coerce HolderLookup.Provider registries, DynamicOps<JsonElement> ops, @NotNull Operation<RegistryOps<JsonElement>> original) {
		RegistryOps<JsonElement> created = original.call(registries, ops);
		WRAPPERS.put(created, registries);
		return created;
	}

	@WrapOperation(method = "reload", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;thenApplyAsync(Ljava/util/function/Function;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
	private static CompletableFuture<LayeredRegistryAccess<RegistryLayer>> removeOps(CompletableFuture<List<WritableRegistry<?>>> future, Function<? super List<WritableRegistry<?>>, ? extends LayeredRegistryAccess<RegistryLayer>> fn, Executor executor, Operation<CompletableFuture<LayeredRegistryAccess<RegistryLayer>>> original, @Local RegistryOps<JsonElement> ops) {
		return original.call(future.thenApply(v -> {
			WRAPPERS.remove(ops);
			return v;
		}), fn, executor);
	}

	@WrapOperation(method = "lambda$scheduleElementParse$3", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"))
	private static <T> void modifyLootTable(Optional<T> optionalTable, Consumer<? super T> action, Operation<Void> original, @Local(argsOnly = true) ResourceLocation id, @Local(argsOnly = true) RegistryOps<JsonElement> ops) {
		original.call(optionalTable.map(table -> fabric$modifyLootTable(table, id, ops)), action);
	}

	@Unique
	private static <T> T fabric$modifyLootTable(T value, ResourceLocation id, RegistryOps<JsonElement> ops) {
		if (!(value instanceof LootTable table)) return value;

		if (table == LootTable.EMPTY) {
			return value;
		}

		ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE, id);

		LootTable.Builder builder = fabric$copyOf(table);
		HolderLookup.Provider registries = WRAPPERS.get(ops);
		ModLoot.modify(key, builder, registries);

		return (T) builder.build();
	}

	@Unique
	private static LootTable.Builder fabric$copyOf(LootTable table) {
		LootTable.Builder builder = LootTable.lootTable();
		LootTableAccessor accessor = (LootTableAccessor) table;

		builder.setParamSet(table.getParamSet());
		ImmutableList.Builder<LootPool> poolBuilder = new ImmutableList.Builder<>();
		builder.pools = poolBuilder.addAll(accessor.fabric_getPools());
		ImmutableList.Builder<LootItemFunction> functionBuilder = new ImmutableList.Builder<>();
		builder.functions = functionBuilder.addAll(accessor.fabric_getFunctions());
		accessor.fabric_getRandomSequenceId().ifPresent(builder::setRandomSequence);

		return builder;
	}
}
