package me.dueris.calio.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Function;

public class CalioDataTypes {
	public static <T> T test(Class<T> ofType, JsonElement provider){
		if (ofType.equals(ItemStack.class)) return (T) itemStack(provider);
		if (ofType.equals(Item.class)) return (T) item(provider);
		if (ofType.equals(NamespacedKey.class)) return (T) bukkitIdentifier(provider);
		if (ofType.equals(ResourceLocation.class)) return (T) identifier(provider);
		if (ofType.equals(CompoundTag.class)) return (T) compoundTag(provider);
		return null;
	}

	public static ItemStack itemStack(JsonElement element) {
		if (element.isJsonObject()) {
			JsonObject object = element.getAsJsonObject();
			Item item = item(object.get("item"));
			int amount = object.has("amount") ? object.get("amount").getAsInt() : 1;

			net.minecraft.world.item.ItemStack stack = item.getDefaultInstance();
			stack.setCount(amount);
			return stack.asBukkitCopy();
		} else {
			// Assume string
			return item(element).getDefaultInstance().asBukkitCopy();
		}
	}

	public static Item item(JsonElement element) {
		return BuiltInRegistries.ITEM.get(identifier(element));
	}

	public static NamespacedKey bukkitIdentifier(JsonElement element) {
		return NamespacedKey.fromString(element.getAsString());
	}

	public static ResourceLocation identifier(JsonElement element) {
		return CraftNamespacedKey.toMinecraft(bukkitIdentifier(element));
	}

	public static CompoundTag compoundTag(JsonElement element) {
		return ParserUtils.parseJson(new StringReader(element.getAsString()), CompoundTag.CODEC);
	}

	public static class ParserUtils {
		private static final Field JSON_READER_POS = Util.make(() -> {
			try {
				Field field = JsonReader.class.getDeclaredField("pos");
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException var1) {
				throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", var1);
			}
		});
		private static final Field JSON_READER_LINESTART = Util.make(() -> {
			try {
				Field field = JsonReader.class.getDeclaredField("lineStart");
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException var1) {
				throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", var1);
			}
		});

		private static int getPos(JsonReader jsonReader) {
			try {
				return JSON_READER_POS.getInt(jsonReader) - JSON_READER_LINESTART.getInt(jsonReader) + 1;
			} catch (IllegalAccessException var2) {
				throw new IllegalStateException("Couldn't read position of JsonReader", var2);
			}
		}

		public static <T> T parseJson(com.mojang.brigadier.StringReader stringReader, Codec<T> codec) {
			JsonReader jsonReader = new JsonReader(new java.io.StringReader(stringReader.getRemaining()));
			jsonReader.setLenient(true);

			Object var4;
			try {
				JsonElement jsonElement = Streams.parse(jsonReader);
				var4 = getOrThrow(codec.parse(JsonOps.INSTANCE, jsonElement), JsonParseException::new);
			} catch (StackOverflowError var8) {
				throw new JsonParseException(var8);
			} finally {
				stringReader.setCursor(stringReader.getCursor() + getPos(jsonReader));
			}

			return (T) var4;
		}

		public static <T, E extends Throwable> T getOrThrow(DataResult<T> result, Function<String, E> exceptionGetter) throws E {
			Optional<DataResult.Error<T>> optional = result.error();
			if (optional.isPresent()) {
				throw exceptionGetter.apply(optional.get().message());
			} else {
				return result.result().orElseThrow();
			}
		}
	}
}
