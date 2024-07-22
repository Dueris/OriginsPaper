package me.dueris.calio.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.Error;
import com.mojang.serialization.JsonOps;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.ParticleEffect;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.ItemValue;
import net.minecraft.world.item.crafting.Ingredient.Value;
import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class CalioDataTypes {
	public static HashMap<Class<?>, Function<JsonElement, ?>> registries = new HashMap<>();

    @SuppressWarnings("unchecked")
	public static <T> @Nullable T test(@NotNull Class<T> ofType, JsonElement provider) {
		if (ofType.equals(ItemStack.class)) {
			return (T) itemStack(provider);
		} else if (ofType.equals(Item.class)) {
			return (T) item(provider);
		} else if (ofType.equals(NamespacedKey.class)) {
			return (T) bukkitIdentifier(provider);
		} else if (ofType.equals(ResourceLocation.class)) {
			return (T) identifier(provider);
		} else if (ofType.equals(CompoundTag.class)) {
			return (T) compoundTag(provider);
		} else if (ofType.equals(ParticleEffect.class)) {
			return (T) particleEffect(provider);
		} else if (ofType.equals(Vector.class)) {
			return (T) vector(provider);
		} else if (ofType.equals(Sound.class)) {
			return (T) sound(provider);
		} else if (ofType.equals(Material.class)) {
			return (T) material(provider);
		} else if (ofType.equals(Stat.class)) {
			return (T) stat(provider);
		} else if (ofType.isEnum()) {
			return (T) getEnumValue(provider, (Class<Enum>) ofType);
		} else {
			return (T) (registries.containsKey(ofType) ? registries.get(ofType).apply(provider) : null);
		}
	}

	public static @NotNull Stat stat(@NotNull JsonElement element) {
		ResourceLocation type = CraftNamespacedKey.toMinecraft(NamespacedKey.fromString(element.getAsJsonObject().get("type").getAsString()));
		ResourceLocation id = CraftNamespacedKey.toMinecraft(NamespacedKey.fromString(element.getAsJsonObject().get("id").getAsString()));
		StatType searchedType = BuiltInRegistries.STAT_TYPE.get(type);
		if (searchedType != null) {
			Registry statRegistry = searchedType.getRegistry();
			if (statRegistry.containsKey(id)) {
				return searchedType.get(statRegistry.get(id));
			} else {
				throw new IllegalArgumentException(
					"Desired stat \"" + id + "\" does not exist in stat type \"" + BuiltInRegistries.STAT_TYPE.getKey(searchedType) + "\""
				);
			}
		} else {
			throw new IllegalArgumentException("Provided \"type\" field was not found in the STAT_TYPE registry \"{}\"".replace("{}", type.toString()));
		}
	}

	public static Material material(JsonElement element) {
		NamespacedKey a = bukkitIdentifier(element);
		return Material.matchMaterial(a.asString());
	}

	public static Ingredient ingredient(@NotNull JsonElement element) {
		List<Value> entries = new ArrayList<>();
		if (element.isJsonObject()) {
			initValues(element.getAsJsonObject(), entries);
		} else if (element.isJsonArray()) {
			JsonArray array = element.getAsJsonArray();
			array.asList().stream().map(JsonElement::getAsJsonObject).forEach(object -> initValues(object, entries));
		}

		return fromValues(entries.stream());
	}

	private static void initValues(@NotNull JsonObject object, List<Value> entries) {
		if (object.has("item")) {
			entries.add(
				new ItemValue(
					((Item) ((Registry) MinecraftServer.getServer().registryAccess().registry(Registries.ITEM).get())
						.get(ResourceLocation.parse(object.get("item").getAsString())))
						.getDefaultInstance()
				)
			);
		}

		if (object.has("tag")) {
			try {
				Class<?> tagValueClass = Class.forName("net.minecraft.world.item.crafting.Ingredient$TagValue");
				Constructor constructor = tagValueClass.getDeclaredConstructor(TagKey.class);
				constructor.setAccessible(true);
				Object tagValueInst = constructor.newInstance(TagKey.create(Registries.ITEM, ResourceLocation.parse(object.get("tag").getAsString())));
				entries.add((Value) tagValueInst);
			} catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
					 IllegalAccessException | ClassNotFoundException var5) {
				throw new RuntimeException(var5);
			}
		}
	}

	private static Ingredient fromValues(Stream<? extends Value> entries) {
		Ingredient recipeitemstack = new Ingredient(entries);
		return recipeitemstack.isEmpty() ? Ingredient.EMPTY : recipeitemstack;
	}

	@Contract("_ -> new")
    public static @NotNull Vector vector(JsonElement element) {
		FactoryJsonObject object = new FactoryJsonObject(element.getAsJsonObject());
		float x = 0.0F;
		float y = 0.0F;
		float z = 0.0F;
		if (object.isPresent("x")) {
			x = object.getNumber("x").getFloat();
		}

		if (object.isPresent("y")) {
			y = object.getNumber("y").getFloat();
		}

		if (object.isPresent("z")) {
			z = object.getNumber("z").getFloat();
		}

		return new Vector(x, y, z);
	}

	public static @NotNull ItemStack itemStack(@NotNull JsonElement element) {
		if (element.isJsonObject()) {
			JsonObject object = element.getAsJsonObject();
			Item item = item(object.get("item"));
			int amount = object.has("amount") ? object.get("amount").getAsInt() : 1;
			net.minecraft.world.item.ItemStack stack = item.getDefaultInstance();
			stack.setCount(amount);
			return stack.asBukkitCopy();
		} else {
			return item(element).getDefaultInstance().asBukkitCopy();
		}
	}

	public static Sound sound(@NotNull JsonElement element) {
		return CraftRegistry.SOUNDS.get(NamespacedKey.fromString(element.getAsString()));
	}

	@Contract("_ -> new")
    public static @NotNull ParticleEffect particleEffect(JsonElement raw) {
		FactoryElement element = new FactoryElement(raw);
		Particle particle = null;
		Optional<DustOptions> data = Optional.empty();
		Optional<BlockData> blockData = Optional.empty();
		boolean containsParams = !element.isString() && element.isJsonObject() && element.toJsonObject().isPresent("params");
		if (element.isString()) {
			particle = Particle.valueOf(ensureCorrectNamespace(element.getString()).split(":")[1].toUpperCase());
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
				data = Optional.of(new DustOptions(Color.fromRGB(calculateParticleValue(arg1), calculateParticleValue(arg2), calculateParticleValue(arg3)), size));
			} else if (particle.equals(Particle.BLOCK)) {
				blockData = Optional.of(material(element.toJsonObject().getElement("params").handle).createBlockData());
			}
		}

		return new ParticleEffect(particle, data, blockData);
	}

	public static PotionEffectType potionEffectType(@NotNull JsonElement raw) {
		return PotionEffectType.getByKey(NamespacedKey.fromString(raw.getAsString()));
	}

	private static int calculateParticleValue(float value) {
		return Math.round(value * 255.0F) > 255 ? 254 : Math.round(value * 255.0F);
	}

	@Contract(pure = true)
    private static String ensureCorrectNamespace(@NotNull String string) {
		return string.contains(":") ? string : "minecraft:" + string;
	}

	public static @NotNull Item item(JsonElement element) {
		return BuiltInRegistries.ITEM.get(identifier(element));
	}

	public static NamespacedKey bukkitIdentifier(@NotNull JsonElement element) {
		return NamespacedKey.fromString(element.getAsString());
	}

	@Contract("_ -> new")
    public static @NotNull ResourceLocation identifier(JsonElement element) {
		return CraftNamespacedKey.toMinecraft(bukkitIdentifier(element));
	}

	public static CompoundTag compoundTag(JsonElement element) {
		return element == null
			? new CompoundTag()
			: Codec.withAlternative(CompoundTag.CODEC, TagParser.LENIENT_CODEC).parse(JsonOps.INSTANCE, element).getOrThrow();
	}

	private static <T extends Enum<T>> @NotNull T getEV(@NotNull Class<T> enumClass, String value) {
		T[] enumConstants = enumClass.getEnumConstants();

		for (T enumValue : enumConstants) {
			if (enumValue.toString().toLowerCase().equalsIgnoreCase(value)) {
				return enumValue;
			}
		}

		throw new IllegalArgumentException("Provided JsonValue from key \"{key}\" was not an instanceof enum \"{enum}\"");
	}

	public static <T extends Enum<T>> @NotNull T getEnumValue(@NotNull JsonElement provider, Class<T> enumClass) {
		String value = provider.getAsString().toLowerCase();
		return getEV(enumClass, value);
	}

	public static class ParserUtils {
		private static final Field JSON_READER_POS = Util.make(() -> {
			try {
				Field field = JsonReader.class.getDeclaredField("pos");
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException var11) {
				throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", var11);
			}
		});
		private static final Field JSON_READER_LINESTART = Util.make(() -> {
			try {
				Field field = JsonReader.class.getDeclaredField("lineStart");
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException var11) {
				throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", var11);
			}
		});

		private static int getPos(JsonReader jsonReader) {
			try {
				return JSON_READER_POS.getInt(jsonReader) - JSON_READER_LINESTART.getInt(jsonReader) + 1;
			} catch (IllegalAccessException var2) {
				throw new IllegalStateException("Couldn't read position of JsonReader", var2);
			}
		}

		public static <T> T parseJson(@NotNull StringReader stringReader, @NotNull Codec<T> codec) {
			JsonReader jsonReader = new JsonReader(new java.io.StringReader(stringReader.getRemaining()));
			jsonReader.setLenient(true);

			Object var4;
			try {
				JsonElement jsonElement = Streams.parse(jsonReader);
				var4 = getOrThrow(codec.parse(JsonOps.INSTANCE, jsonElement), JsonParseException::new);
			} catch (StackOverflowError var81) {
				throw new JsonParseException(var81);
			} finally {
				stringReader.setCursor(stringReader.getCursor() + getPos(jsonReader));
			}

			return (T) var4;
		}

		public static <T, E extends Throwable> T getOrThrow(@NotNull DataResult<T> result, Function<String, E> exceptionGetter) throws E {
			Optional<Error<T>> optional = result.error();
			if (optional.isPresent()) {
				throw exceptionGetter.apply(optional.get().message());
			} else {
				return result.result().orElseThrow();
			}
		}
	}
}
