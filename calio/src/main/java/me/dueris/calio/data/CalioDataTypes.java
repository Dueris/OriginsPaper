package me.dueris.calio.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.ParticleEffect;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.world.item.Item;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

public class CalioDataTypes {
	public static HashMap<Class<?> /*ofType*/, Function<JsonElement, ?>> registries = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static <T> T test(Class<T> ofType, JsonElement provider) {
		if (ofType.equals(ItemStack.class)) return (T) itemStack(provider);
		if (ofType.equals(Item.class)) return (T) item(provider);
		if (ofType.equals(NamespacedKey.class)) return (T) bukkitIdentifier(provider);
		if (ofType.equals(ResourceLocation.class)) return (T) identifier(provider);
		if (ofType.equals(CompoundTag.class)) return (T) compoundTag(provider);
		if (ofType.equals(ParticleEffect.class)) return (T) particleEffect(provider);
		if (ofType.equals(Vector.class)) return (T) vector(provider);
		if (ofType.equals(Sound.class)) return (T) sound(provider);
		if (ofType.equals(Material.class)) return (T) material(provider);
		if (ofType.equals(Stat.class)) return (T) stat(provider);
		if (ofType.isEnum()) {
			return (T) getEnumValue(provider, (Class<Enum>) ofType);
		}
		if (registries.containsKey(ofType)) {
			return (T) registries.get(ofType).apply(provider);
		}
		return null;
	}

	public static Stat stat(JsonElement element) {
		ResourceLocation type = CraftNamespacedKey.toMinecraft(NamespacedKey.fromString(element.getAsJsonObject().get("type").getAsString()));
		ResourceLocation id = CraftNamespacedKey.toMinecraft(NamespacedKey.fromString(element.getAsJsonObject().get("id").getAsString()));

		StatType searchedType = BuiltInRegistries.STAT_TYPE.get(type);
		if (searchedType != null) {
			Registry statRegistry = searchedType.getRegistry();
			if (statRegistry.containsKey(id)) {
				return searchedType.get(statRegistry.get(id));
			} else
				throw new IllegalArgumentException("Desired stat \"" + id + "\" does not exist in stat type \"" + BuiltInRegistries.STAT_TYPE.getKey(searchedType) + "\"");
		} else {
			throw new IllegalArgumentException("Provided \"type\" field was not found in the STAT_TYPE registry \"{}\"".replace("{}", type.toString()));
		}
	}

	public static Material material(JsonElement element) {
		NamespacedKey a = bukkitIdentifier(element);
		return Material.matchMaterial(a.asString());
	}

	public static Vector vector(JsonElement element) {
		FactoryJsonObject object = new FactoryJsonObject(element.getAsJsonObject());
		float x = 0.0f;
		float y = 0.0f;
		float z = 0.0f;

		if (object.isPresent("x"))
			x = object.getNumber("x").getFloat();
		if (object.isPresent("y"))
			y = object.getNumber("y").getFloat();
		if (object.isPresent("z"))
			z = object.getNumber("z").getFloat();

		return new Vector(x, y, z);
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

	public static Sound sound(JsonElement element) {
		return CraftRegistry.SOUNDS.get(NamespacedKey.fromString(element.getAsString()));
	}

	public static ParticleEffect particleEffect(JsonElement raw) {
		FactoryElement element = new FactoryElement(raw);
		Particle particle = null;
		Optional<Particle.DustOptions> data = Optional.empty();
		Optional<BlockData> blockData = Optional.empty();
		boolean containsParams = !(element.isString()) && element.isJsonObject() && element.toJsonObject().isPresent("params");
		if (element.isString()) {
			particle = Particle.valueOf(ensureCorrectNamespace(element.getString()).split(":")[1].toUpperCase()); // Directly parse it
		} else if (element.isJsonObject()) {
			FactoryJsonObject particleObject = element.toJsonObject();
			particle = particleObject.getEnumValue("type", Particle.class, true);
		}
		if (containsParams) {
			String provided = element.toJsonObject().getStringOrDefault("params", "");
			if (provided.contains(" ") && !particle.equals(Particle.BLOCK)) {
				String[] splitArgs = provided.split(" ");
				float arg1 = Float.parseFloat(splitArgs[0]);
				float arg2 = Float.parseFloat(splitArgs[1]);
				float arg3 = Float.parseFloat(splitArgs[2]);
				float size = Float.parseFloat(splitArgs[3]);
				data = Optional.of(new Particle.DustOptions(Color.fromRGB(calculateParticleValue(arg1), calculateParticleValue(arg2), calculateParticleValue(arg3)), size));
			} else if (particle.equals(Particle.BLOCK)) {
				blockData = Optional.of(material(element.toJsonObject().getElement("params").handle).createBlockData());
			}
		}

		return new ParticleEffect(particle, data, blockData);
	}

	public static PotionEffectType potionEffectType(JsonElement raw) {
		return PotionEffectType.getByKey(NamespacedKey.fromString(raw.getAsString()));
	}

	private static int calculateParticleValue(float value) {
		if (Math.round(value * 255) > 255) {
			return 254;
		} else {
			return Math.round(value * 255);
		}
	}

	private static String ensureCorrectNamespace(String string) {
		return string.contains(":") ? string : "minecraft:" + string;
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
		if (element == null) return new CompoundTag();
		return Codec.withAlternative(CompoundTag.CODEC, TagParser.LENIENT_CODEC)
			.parse(JsonOps.INSTANCE, element)
			.getOrThrow();
	}

	private static <T extends Enum<T>> T getEV(Class<T> enumClass, String value) {
		T[] enumConstants = enumClass.getEnumConstants();
		for (T enumValue : enumConstants) {
			if (enumValue.toString().toLowerCase().equalsIgnoreCase(value)) {
				return enumValue;
			}
		}
		throw new IllegalArgumentException("Provided JsonValue from key \"{key}\" was not an instanceof enum \"{enum}\"");
	}

	public static <T extends Enum<T>> T getEnumValue(JsonElement provider, Class<T> enumClass) {
		String value = provider.getAsString().toLowerCase();
		return getEV(enumClass, value);
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
