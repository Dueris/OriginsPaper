package me.dueris.originspaper.factory;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.dueris.calio.CalioDataTypes;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.registry.RegistryKey;
import io.github.dueris.calio.registry.impl.CalioRegistry;
import io.github.dueris.calio.util.holder.Pair;
import me.dueris.originspaper.factory.actions.ActionFactory;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class CraftApoliDataTypes {
	public static final SerializableDataBuilder<ActionFactory<Entity>> ENTITY_ACTION = action(Registries.ENTITY_ACTION);
	public static final SerializableDataBuilder<ActionFactory<Pair<Entity, Entity>>> BIENTITY_ACTION = action(Registries.BIENTITY_ACTION);
	public static final SerializableDataBuilder<ActionFactory<Location>> BLOCK_ACTION = action(Registries.BLOCK_ACTION);
	public static final SerializableDataBuilder<ActionFactory<Pair<ServerLevel, ItemStack>>> ITEM_ACTION = action(Registries.ITEM_ACTION);

	@Contract(value = "_ -> new", pure = true)
	public static <T> @NotNull SerializableDataBuilder<ActionFactory<T>> action(RegistryKey<ActionFactory<T>> registry) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				if (!(jsonElement instanceof JsonObject jsonObject)) {
					throw new JsonSyntaxException("Expected a JSON object.");
				}

				ResourceLocation factoryID = CalioDataTypes.IDENTIFIER.deserialize(jsonObject.get("type"));
				return CalioRegistry.INSTANCE.retrieve(registry).get(factoryID).copy().decompile(jsonObject);
			}, ActionFactory.class
		);
	}

	private static void test() {
	}
}
