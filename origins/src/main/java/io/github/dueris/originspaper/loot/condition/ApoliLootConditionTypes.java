package io.github.dueris.originspaper.loot.condition;

import com.mojang.serialization.MapCodec;
import io.github.dueris.originspaper.OriginsPaper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public class ApoliLootConditionTypes {

	public static final LootItemConditionType POWER = register("power", PowerLootCondition.MAP_CODEC);

	public static void register() {

	}

	public static <C extends LootItemCondition> @NotNull LootItemConditionType register(String path, MapCodec<C> mapCodec) {
		return Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, OriginsPaper.apoliIdentifier(path), new LootItemConditionType(mapCodec));
	}

}
