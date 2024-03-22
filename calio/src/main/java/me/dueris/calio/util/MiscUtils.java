package me.dueris.calio.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_20_R3.CraftRegistry;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class MiscUtils {

	@Nullable
	public static PotionEffect parseAndApplyStatusEffectInstance(JSONObject power) {
		JSONObject singleEffect = (JSONObject) power.get("effect");
		JSONArray effects = (JSONArray) power.getOrDefault("effects", new JSONArray());

		if (singleEffect != null) {
			effects.add(singleEffect);
		}

		for (Object obj : effects) {
			JSONObject effect = (JSONObject) obj;
			String potionEffect = "minecraft:luck";
			int duration = 100;
			int amplifier = 0;
			boolean isAmbient = false;
			boolean showParticles = true;
			boolean showIcon = true;

			if (effect.containsKey("effect")) potionEffect = effect.get("effect").toString();
			if (effect.containsKey("duration")) duration = Integer.parseInt(effect.get("duration").toString());
			if (effect.containsKey("amplifier")) amplifier = Integer.parseInt(effect.get("amplifier").toString());
			if (effect.containsKey("is_ambient")) isAmbient = Boolean.parseBoolean(effect.get("is_ambient").toString());
			if (effect.containsKey("show_particles"))
				showParticles = Boolean.parseBoolean(effect.get("show_particles").toString());
			if (effect.containsKey("show_icon")) showIcon = Boolean.parseBoolean(effect.get("show_icon").toString());

			return new PotionEffect(PotionEffectType.getByKey(new NamespacedKey(potionEffect.split(":")[0], potionEffect.split(":")[1])), duration, amplifier, isAmbient, showParticles, showIcon);
		}
		return null;
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

		public static <T> T parseJson(StringReader stringReader, Codec<T> codec) {
			JsonReader jsonReader = new JsonReader(new java.io.StringReader(stringReader.getRemaining()));
			jsonReader.setLenient(true);

			Object var4;
			try {
				JsonElement jsonElement = Streams.parse(jsonReader);
				var4 = Util.getOrThrow(codec.parse(JsonOps.INSTANCE, jsonElement), JsonParseException::new);
			} catch (StackOverflowError var8) {
				throw new JsonParseException(var8);
			} finally {
				stringReader.setCursor(stringReader.getCursor() + getPos(jsonReader));
			}

			return (T) var4;
		}
	}

	public static Sound parseSound(String sound) {
		return CraftRegistry.SOUNDS.get(NamespacedKey.fromString(sound));
	}
}
