package me.dueris.originspaper.factory.data;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataBuilder;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.factory.data.types.GuiTitle;
import me.dueris.originspaper.factory.data.types.Impact;
import me.dueris.originspaper.factory.data.types.OriginUpgrade;
import me.dueris.originspaper.registry.registries.OriginLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
			if (!jsonElement.isJsonObject()) throw new JsonSyntaxException("Expected JsonObject for GuiTitle!");
			JsonObject jo = jsonElement.getAsJsonObject();

			Component viewOriginTitle = SerializableDataTypes.TEXT.deserialize(jo.get("view_origin"));
			Component chooseOriginTitle = SerializableDataTypes.TEXT.deserialize(jo.get("choose_origin"));
			net.kyori.adventure.text.Component kyoriViewOrigin = net.kyori.adventure.text.Component.text(viewOriginTitle.getString());
			net.kyori.adventure.text.Component kyoriChooseOrigin = net.kyori.adventure.text.Component.text(chooseOriginTitle.getString());

			return new GuiTitle(kyoriViewOrigin, kyoriChooseOrigin);
		}, GuiTitle.class
	);

	public static final SerializableDataBuilder<OriginLayer.ConditionedOrigin> CONDITIONED_ORIGIN = SerializableDataBuilder.of(
		(jsonElement) -> {
			if (jsonElement instanceof JsonObject jsonObject && !jsonObject.isEmpty()) {
				DeserializedFactoryJson factoryJson = DeserializedFactoryJson.decompileJsonObject(jsonObject, OriginLayer.ConditionedOrigin.DATA, "Origin/ConditionedOrigin", "null");
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
}
