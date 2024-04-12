package me.dueris.calio.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import me.dueris.calio.builder.inst.factory.FactoryJsonArray;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import net.minecraft.Util;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_20_R3.CraftRegistry;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MiscUtils {

    @Nullable
    public static List<PotionEffect> parseAndReturnPotionEffects(FactoryJsonObject power) {
        List<PotionEffect> effectList = new ArrayList<>();
        FactoryJsonObject singleEffect = power.isPresent("effect") ? power.getJsonObject("effect") : new FactoryJsonObject(new JsonObject());
        List<FactoryJsonObject> effects = (power.isPresent("effects") ? power.getJsonArray("effects") : new FactoryJsonArray(new JsonArray())).asJsonObjectList();

        if (singleEffect != null && !singleEffect.isEmpty()) {
            effects.add(singleEffect);
        }

        for (FactoryJsonObject effect : effects) {
            String potionEffect = "minecraft:luck";
            int duration = 100;
            int amplifier = 0;
            boolean isAmbient = false;
            boolean showParticles = true;
            boolean showIcon = true;

            if (effect.isPresent("effect")) potionEffect = effect.getString("effect");
            if (effect.isPresent("duration")) duration = effect.getNumber("duration").getInt();
            if (effect.isPresent("amplifier")) amplifier = effect.getNumber("amplifier").getInt();
            if (effect.isPresent("is_ambient")) isAmbient = effect.getBooleanOrDefault("is_ambient", true);
            if (effect.isPresent("show_particles")) effect.getBooleanOrDefault("show_particles", false);
            if (effect.isPresent("show_icon")) showIcon = effect.getBooleanOrDefault("show_icon", false);

            effectList.add(new PotionEffect(PotionEffectType.getByKey(new NamespacedKey(potionEffect.split(":")[0], potionEffect.split(":")[1])), duration, amplifier, isAmbient, showParticles, showIcon));
        }
        return effectList;
    }

    public static Sound parseSound(String sound) {
        return CraftRegistry.SOUNDS.get(NamespacedKey.fromString(sound));
    }

    public static Material getBukkitMaterial(String string) {
        NamespacedKey key = NamespacedKey.fromString(string);
        return Material.matchMaterial(key.asString());
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
}
