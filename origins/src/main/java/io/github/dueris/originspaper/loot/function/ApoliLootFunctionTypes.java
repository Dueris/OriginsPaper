package io.github.dueris.originspaper.loot.function;

import com.mojang.serialization.MapCodec;
import io.github.dueris.originspaper.OriginsPaper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import org.jetbrains.annotations.NotNull;

public class ApoliLootFunctionTypes {

	public static final LootItemFunctionType<AddPowerLootFunction> ADD_POWER = register("add_power", AddPowerLootFunction.MAP_CODEC);
	public static final LootItemFunctionType<RemovePowerLootFunction> REMOVE_POWER = register("remove_power", RemovePowerLootFunction.MAP_CODEC);

	public static void register() {

	}

	public static <F extends LootItemFunction> @NotNull LootItemFunctionType<F> register(String path, MapCodec<F> mapCodec) {
		return Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, OriginsPaper.apoliIdentifier(path), new LootItemFunctionType<>(mapCodec));
	}

}
