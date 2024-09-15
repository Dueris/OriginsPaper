package io.github.dueris.originspaper.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.data.types.GuiTitle;
import io.github.dueris.originspaper.data.types.Impact;
import io.github.dueris.originspaper.data.types.OriginUpgrade;
import io.github.dueris.originspaper.origin.OriginLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused")
public class OriginsDataTypes {

	public static final SerializableDataType<ItemStack> ITEM_OR_ITEM_STACK = SerializableDataType.of(
		(jsonElement) -> {
			if (!(jsonElement instanceof JsonPrimitive jsonPrimitive) || !jsonPrimitive.isString()) {
				return SerializableDataTypes.ITEM_STACK.deserialize(jsonElement);
			}

			Item item = SerializableDataTypes.ITEM.deserialize(jsonPrimitive);
			return new ItemStack(item);
		}, ItemStack.class
	);
	public static final SerializableDataType<Impact> IMPACT = SerializableDataTypes.enumValue(Impact.class);

	public static final SerializableDataType<GuiTitle> GUI_TITLE = SerializableDataType.of(
		(jsonElement) -> {
			if (!(jsonElement instanceof JsonObject jo))
				throw new JsonSyntaxException("Expected JsonObject for GuiTitle!");
			SerializableData.Instance data = SerializableDataType.compound(GuiTitle.DATA, jo, GuiTitle.class);

			Component viewOriginTitle = SerializableDataTypes.TEXT.deserialize(jo.get("view_origin"));
			Component chooseOriginTitle = SerializableDataTypes.TEXT.deserialize(jo.get("choose_origin"));

			return new GuiTitle(data.get("view_origin"), data.get("choose_origin"));
		}, GuiTitle.class
	);

	public static final SerializableDataType<OriginLayer.ConditionedOrigin> CONDITIONED_ORIGIN = SerializableDataType.of(
		(jsonElement) -> {
			if (jsonElement instanceof JsonObject jsonObject && !jsonObject.isEmpty()) {
				SerializableData.Instance factoryJson = SerializableDataType.compound(OriginLayer.ConditionedOrigin.DATA, jsonObject, OriginLayer.ConditionedOrigin.class);
				return new OriginLayer.ConditionedOrigin(factoryJson.get("condition"), factoryJson.get("origins"));
			} else throw new JsonSyntaxException("Expected JsonObject for ConditionedOrigin!");
		}, OriginLayer.ConditionedOrigin.class
	);

	public static final SerializableDataType<OriginLayer.ConditionedOrigin> ORIGIN_OR_CONDITIONED_ORIGIN = SerializableDataType.of(
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

	public static final SerializableDataType<OriginUpgrade> ORIGIN_UPGRADE = SerializableDataType.of(
		(jsonElement) -> {
			if (!jsonElement.isJsonObject())
				throw new JsonSyntaxException("Expected JsonObject for Origin Upgrade!");
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
