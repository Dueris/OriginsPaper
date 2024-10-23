package io.github.dueris.calio.codec;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CalioMapCodecs {

	public static final MapCodec<Recipe<?>> RECIPE = BuiltInRegistries.RECIPE_SERIALIZER
		.byNameCodec()
		.dispatchMap(Recipe::getSerializer, RecipeSerializer::codec);

}
