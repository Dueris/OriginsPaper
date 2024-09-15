package io.github.dueris.originspaper.util;

import com.google.common.base.Strings;
import com.google.gson.*;
import com.google.gson.internal.LazilyParsedNumber;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class JsonTextFormatter {

	private static final ChatFormatting NAME_COLOR = ChatFormatting.AQUA;
	private static final ChatFormatting STRING_COLOR = ChatFormatting.GREEN;
	private static final ChatFormatting NUMBER_COLOR = ChatFormatting.GOLD;
	private static final ChatFormatting BOOLEAN_COLOR = ChatFormatting.BLUE;
	private static final ChatFormatting TYPE_SUFFIX_COLOR = ChatFormatting.RED;

	private final String indent;

	private final boolean root;
	private final int offset;

	protected JsonTextFormatter(String indent, int offset, boolean root) {
		this.indent = indent;
		this.offset = Math.max(0, offset);
		this.root = root;
	}

	public JsonTextFormatter(char indent, int size) {
		this(Strings.repeat(String.valueOf(indent), size), 1, true);
	}

	public JsonTextFormatter(int size) {
		this(' ', size);
	}

	public Component apply(JsonElement jsonElement) {
		return applyInternal(jsonElement).orElse(Component.empty());
	}

	protected Optional<Component> applyInternal(JsonElement jsonElement) {

		Component result = switch (jsonElement) {
			case JsonArray jsonArray -> visitArray(jsonArray);
			case JsonObject jsonObject -> visitObject(jsonObject);
			case JsonPrimitive jsonPrimitive -> visitPrimitive(jsonPrimitive);
			case JsonNull ignored -> null;
			case null -> throw new JsonSyntaxException("JSON element cannot be null!");
			default -> throw new JsonParseException("The format of JSON element " + jsonElement + " is not supported!");
		};

		return Optional.ofNullable(result);

	}

	public Component visitArray(@NotNull JsonArray jsonArray) {

		if (jsonArray.isEmpty()) {
			return Component.literal("[]");
		}

		MutableComponent result = Component.literal("[");
		if (!indent.isEmpty()) {
			result.append("\n");
		}

		Iterator<JsonElement> iterator = jsonArray.iterator();
		while (iterator.hasNext()) {

			JsonElement jsonElement = iterator.next();
			Optional<Component> jsonText = new JsonTextFormatter(indent, offset + 1, false).applyInternal(jsonElement);

			jsonText.ifPresent(text -> result
				.append(Strings.repeat(indent, offset))
				.append(text));

			if (iterator.hasNext() && jsonText.isPresent()) {
				result.append(!indent.isEmpty() ? ",\n" : ", ");
			}

		}

		if (!indent.isEmpty()) {
			result.append("\n");
		}

		if (!root) {
			result.append(Strings.repeat(indent, offset - 1));
		}

		return result.append("]");

	}

	public Component visitObject(@NotNull JsonObject jsonObject) {

		if (jsonObject.isEmpty()) {
			return Component.literal("{}");
		}

		MutableComponent result = Component.literal("{");
		if (!indent.isEmpty()) {
			result.append("\n");
		}

		Iterator<Map.Entry<String, JsonElement>> iterator = jsonObject.entrySet().iterator();
		while (iterator.hasNext()) {

			Map.Entry<String, JsonElement> entry = iterator.next();

			Component name = Component.literal(entry.getKey()).withStyle(NAME_COLOR);
			Optional<Component> jsonText = new JsonTextFormatter(indent, offset + 1, false).applyInternal(entry.getValue());

			jsonText.ifPresent(text -> result
				.append(Strings.repeat(indent, offset))
				.append(name).append(": ")
				.append(text));

			if (iterator.hasNext() && jsonText.isPresent()) {
				result.append(!indent.isEmpty() ? ",\n" : ", ");
			}

		}

		if (!indent.isEmpty()) {
			result.append("\n");
		}

		if (!root) {
			result.append(Strings.repeat(indent, offset - 1));
		}

		return result.append("}");

	}

	public Component visitPrimitive(@NotNull JsonPrimitive jsonPrimitive) {

		if (jsonPrimitive.isBoolean()) {
			return Component.literal(String.valueOf(jsonPrimitive.getAsBoolean())).withStyle(BOOLEAN_COLOR);
		} else if (jsonPrimitive.isString()) {
			return Component.literal("\"" + jsonPrimitive.getAsString() + "\"").withStyle(STRING_COLOR);
		} else if (jsonPrimitive.isNumber()) {

			Number number = jsonPrimitive.getAsNumber();

			return switch (number) {
				case Integer i -> Component.literal(i.toString()).withStyle(NUMBER_COLOR);
				case Long l -> Component.literal(l.toString()).withStyle(NUMBER_COLOR)
					.append(Component.literal("L").withStyle(TYPE_SUFFIX_COLOR));
				case Float f -> Component.literal(f.toString()).withStyle(NUMBER_COLOR)
					.append(Component.literal("F").withStyle(TYPE_SUFFIX_COLOR));
				case Double d -> Component.literal(d.toString()).withStyle(NUMBER_COLOR)
					.append(Component.literal("D").withStyle(TYPE_SUFFIX_COLOR));
				case Byte b -> Component.literal(b.toString()).withStyle(NUMBER_COLOR)
					.append(Component.literal("B")).withStyle(TYPE_SUFFIX_COLOR);
				case Short s -> Component.literal(s.toString()).withStyle(NUMBER_COLOR)
					.append(Component.literal("S")).withStyle(TYPE_SUFFIX_COLOR);
				case null -> throw new JsonSyntaxException("Number cannot be null!");
				default -> {
					if (!(number instanceof LazilyParsedNumber l)) {
						throw new JsonParseException("The type of number " + number + " is not supported!");
					}

					yield Component.literal(String.valueOf(l.floatValue())).withStyle(NUMBER_COLOR);
				}
			};

		} else {
			throw new JsonParseException("The format of JSON primitive " + jsonPrimitive + " is not supported!");
		}

	}

}