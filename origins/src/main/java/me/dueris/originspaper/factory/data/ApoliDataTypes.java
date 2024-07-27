package me.dueris.originspaper.factory.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.registry.RegistryKey;
import io.github.dueris.calio.registry.impl.CalioRegistry;
import io.github.dueris.calio.util.holder.Pair;
import me.dueris.originspaper.factory.actions.ActionFactory;
import me.dueris.originspaper.factory.conditions.ConditionFactory;
import me.dueris.originspaper.factory.data.types.Keybind;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;
import org.bukkit.Location;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ApoliDataTypes {
	public static final SerializableDataBuilder<ActionFactory<Entity>> ENTITY_ACTION = action(Registries.ENTITY_ACTION);
	public static final SerializableDataBuilder<ActionFactory<Pair<Entity, Entity>>> BIENTITY_ACTION = action(Registries.BIENTITY_ACTION);
	public static final SerializableDataBuilder<ActionFactory<Location>> BLOCK_ACTION = action(Registries.BLOCK_ACTION);
	public static final SerializableDataBuilder<ActionFactory<Pair<ServerLevel, ItemStack>>> ITEM_ACTION = action(Registries.ITEM_ACTION);
	public static final SerializableDataBuilder<ConditionFactory<Pair<Entity, Entity>>> BIENTITY_CONDITION = condition(Registries.BIENTITY_CONDITION);
	public static final SerializableDataBuilder<ConditionFactory<Biome>> BIOME_CONDITION = condition(Registries.BIOME_CONDITION);
	public static final SerializableDataBuilder<ConditionFactory<CraftBlock>> BLOCK_CONDITION = condition(Registries.BLOCK_CONDITION);
	public static final SerializableDataBuilder<ConditionFactory<EntityDamageEvent>> DAMAGE_CONDITION = condition(Registries.DAMAGE_CONDITION);
	public static final SerializableDataBuilder<ConditionFactory<Entity>> ENTITY_CONDITION = condition(Registries.ENTITY_CONDITION);
	public static final SerializableDataBuilder<ConditionFactory<net.minecraft.world.item.ItemStack>> ITEM_CONDITION = condition(Registries.ITEM_CONDITION);
	public static final SerializableDataBuilder<ConditionFactory<Fluid>> FLUID_CONDITION = condition(Registries.FLUID_CONDITION);
	public static final SerializableDataBuilder<Keybind> KEYBIND = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (jsonElement.isJsonObject()) {
				JsonObject jo = jsonElement.getAsJsonObject();
				String key = SerializableDataTypes.STRING.deserialize(jo.get("key"));
				boolean continuous = jo.has("continuous") ? SerializableDataTypes.BOOLEAN.deserialize(jo.get("continuous")) : false;
				return new Keybind(key, continuous);
			} else throw new JsonSyntaxException("Keybind must be an instanceof a JsonObject!");
		}, Keybind.class
	);

	public static <T> @NotNull SerializableDataBuilder<ActionFactory<T>> action(RegistryKey<ActionFactory<T>> registry) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				if (!(jsonElement instanceof JsonObject jsonObject)) {
					throw new JsonSyntaxException("Expected a JSON object.");
				}

				ResourceLocation factoryID = SerializableDataTypes.IDENTIFIER.deserialize(jsonObject.get("type"));
				return CalioRegistry.INSTANCE.retrieve(registry).get(factoryID).copy().decompile(jsonObject);
			}, ActionFactory.class
		);
	}

	public static <T> @NotNull SerializableDataBuilder<ConditionFactory<T>> condition(RegistryKey<ConditionFactory<T>> registry) {
		return SerializableDataBuilder.of(
			(jsonElement) -> {
				if (!(jsonElement instanceof JsonObject jsonObject)) {
					throw new JsonSyntaxException("Expected a JSON object.");
				}

				ResourceLocation factoryID = SerializableDataTypes.IDENTIFIER.deserialize(jsonObject.get("type"));
				return CalioRegistry.INSTANCE.retrieve(registry).get(factoryID).copy().decompile(jsonObject);
			}, ConditionFactory.class
		);
	}

}
