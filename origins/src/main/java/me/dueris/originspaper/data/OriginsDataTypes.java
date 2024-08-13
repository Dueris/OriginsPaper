package me.dueris.originspaper.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.data.types.GuiTitle;
import me.dueris.originspaper.data.types.Impact;
import me.dueris.originspaper.data.types.OriginUpgrade;
import me.dueris.originspaper.origin.OriginLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

@SuppressWarnings("unused")
public class OriginsDataTypes {

	public static final SerializableDataBuilder<ItemStack> ITEM_OR_ITEM_STACK = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (!(jsonElement instanceof JsonPrimitive jsonPrimitive) || !jsonPrimitive.isString()) {
				return SerializableDataTypes.ITEM_STACK.deserialize(jsonElement);
			}

			Item item = SerializableDataTypes.ITEM.deserialize(jsonPrimitive);
			return new ItemStack(item);
		}, ItemStack.class
	);
	public static final SerializableDataBuilder<Impact> IMPACT = SerializableDataTypes.enumValue(Impact.class);

	public static final SerializableDataBuilder<GuiTitle> GUI_TITLE = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (!(jsonElement instanceof JsonObject jo)) throw new JsonSyntaxException("Expected JsonObject for GuiTitle!");
			DeserializedFactoryJson data = SerializableDataBuilder.compound(GuiTitle.DATA, jo, GuiTitle.class);

			Component viewOriginTitle = SerializableDataTypes.TEXT.deserialize(jo.get("view_origin"));
			Component chooseOriginTitle = SerializableDataTypes.TEXT.deserialize(jo.get("choose_origin"));

			return new GuiTitle(data.get("view_origin"), data.get("choose_origin"));
		}, GuiTitle.class
	);

	public static final SerializableDataBuilder<OriginLayer.ConditionedOrigin> CONDITIONED_ORIGIN = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (jsonElement instanceof JsonObject jsonObject && !jsonObject.isEmpty()) {
				DeserializedFactoryJson factoryJson = DeserializedFactoryJson.decompileJsonObject(jsonObject, OriginLayer.ConditionedOrigin.DATA, "Origin/ConditionedOrigin", "null", Optional.of(OriginLayer.ConditionedOrigin.class));
				return new OriginLayer.ConditionedOrigin(factoryJson.get("condition"), factoryJson.get("origins"));
			} else throw new JsonSyntaxException("Expected JsonObject for ConditionedOrigin!");
		}, OriginLayer.ConditionedOrigin.class
	);

	public static final SerializableDataBuilder<OriginLayer.ConditionedOrigin> ORIGIN_OR_CONDITIONED_ORIGIN = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (jsonElement instanceof JsonObject jsonObject) {
				return CONDITIONED_ORIGIN.deserialize(jsonObject);
			}

			if (!(jsonElement instanceof JsonPrimitive jsonPrimitive) || !jsonPrimitive.isString()) {
				throw new JsonSyntaxException("Expected a JSON object or string when parsing Origin/Conditioned Origin");
			}

			ResourceLocation originId = SerializableDataTypes.IDENTIFIER.deserialize(jsonPrimitive);
			return new OriginLayer.ConditionedOrigin(null, Lists.newArrayList(originId));
		}, OriginLayer.ConditionedOrigin.class
	);

	public static final SerializableDataBuilder<OriginUpgrade> ORIGIN_UPGRADE = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (!jsonElement.isJsonObject()) throw new JsonSyntaxException("Expected JsonObject for Origin Upgrade!");
			JsonObject jo = jsonElement.getAsJsonObject();
			return new OriginUpgrade(
				SerializableDataTypes.IDENTIFIER.deserialize(jo.get("condition")),
				SerializableDataTypes.IDENTIFIER.deserialize(jo.get("origin")),
				jo.has("advancement") ? SerializableDataTypes.STRING.deserialize(jo.get("announcement")) : null
			);
		}, OriginUpgrade.class
	);

	public static void init() {
	}
}
