package me.dueris.genesismc.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler;
import me.dueris.genesismc.utils.apoli.Space;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_20_R3.CraftRegistry;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

public class Utils {
    public static Registry<DamageType> DAMAGE_REGISTRY = CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.DAMAGE_TYPE);
    public MinecraftServer server = MinecraftServer.getServer();
    public CraftServer bukkitServer = server.server;

    public static DamageSource getDamageSource(DamageType type) {
        DamageSource source = null;
        for (ResourceKey<DamageType> dkey : DAMAGE_REGISTRY.registryKeySet()) {
            if (DAMAGE_REGISTRY.get(dkey).equals(type)) {
                source = new DamageSource(DAMAGE_REGISTRY.getHolderOrThrow(dkey));
                break;
            }
        }
        return source;
    }

    public static Registry<?> getRegistry(ResourceKey<Registry<?>> registry) {
        return CraftRegistry.getMinecraftRegistry().registryOrThrow(registry);
    }

    public static String[] readJSONFileAsString(File file) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines.toArray(new String[0]);
    }

    public static String getNameOrTag(String name, String tag) {
        return name != "No Name" ? name : tag;
    }

    public static boolean hasChangedBlockCoordinates(final Location fromLoc, final Location toLoc) {
        return !(fromLoc.getWorld().equals(toLoc.getWorld())
                && fromLoc.getBlockX() == toLoc.getBlockX()
                && fromLoc.getBlockY() == toLoc.getBlockY()
                && fromLoc.getBlockZ() == toLoc.getBlockZ());
    }

    public static Space getSpaceFromString(String space){
        switch (space) {
            case "world" -> {
                return Space.WORLD;
            }
            case "local" -> {
                return Space.LOCAL;
            }
            case "local_horizontal" -> {
                return Space.LOCAL_HORIZONTAL;
            }
            case "local_horizontal_normalized" -> {
                return Space.LOCAL_HORIZONTAL_NORMALIZED;
            }
            case "velocity" -> {
                return Space.VELOCITY;
            }
            case "velocity_normalized" -> {
                return Space.VELOCITY_NORMALIZED;
            }
            case "velocity_horizontal" -> {
                return Space.VELOCITY_HORIZONTAL;
            }
            case "velocity_horizontal_normalized" -> {
                return Space.VELOCITY_HORIZONTAL_NORMALIZED;
            }
            default -> {
                return Space.WORLD;
            }
        }
    }

    // Math
    public static Map<String, BinaryOperator<Double>> getOperationMappingsDouble() {
        Map<String, BinaryOperator<Double>> operationMap = new HashMap<>();
        operationMap.put("addition", (a, b) -> a + b);
        operationMap.put("subtraction", (a, b) -> a - b);
        operationMap.put("multiplication", (a, b) -> a * b);
        operationMap.put("division", (a, b) -> a / b);
        operationMap.put("multiply_base", (a, b) -> a * (b + 1));
        operationMap.put("multiply_total", (a, b) -> a * (1 + b));
        operationMap.put("set_total", (a, b) -> b);
        operationMap.put("add_base_early", (a, b) -> a + b);
        operationMap.put("multiply_base_additive", (a, b) -> a + (a * b));
        operationMap.put("multiply_base_multiplicative", (a, b) -> a * (1 + b));
        operationMap.put("add_base_late", (a, b) -> a + b);

        Random random = new Random();

        operationMap.put("add_random_max", (a, b) -> a + random.nextDouble(b));
        operationMap.put("subtract_random_max", (a, b) -> a - random.nextDouble(b));
        operationMap.put("multiply_random_max", (a, b) -> a * random.nextDouble(b));
        operationMap.put("divide_random_max", (a, b) -> a / random.nextDouble(b));

        return operationMap;
    }

    public static Map<String, BinaryOperator<Long>> getOperationMappingsLong() {
        Map<String, BinaryOperator<Long>> operationMap = new HashMap<>();
        operationMap.put("addition", (a, b) -> a + b);
        operationMap.put("subtraction", (a, b) -> a - b);
        operationMap.put("multiplication", (a, b) -> a * b);
        operationMap.put("division", (a, b) -> a / b);
        operationMap.put("multiply_base", (a, b) -> a * (b + 1));
        operationMap.put("multiply_total", (a, b) -> a * (1 + b));
        operationMap.put("set_total", (a, b) -> b);
        operationMap.put("add_base_early", (a, b) -> a + b);
        operationMap.put("multiply_base_additive", (a, b) -> a + (a * b));
        operationMap.put("multiply_base_multiplicative", (a, b) -> a * (1 + b));
        operationMap.put("add_base_late", (a, b) -> a + b);

        Random random = new Random();

        operationMap.put("add_random_max", (a, b) -> a + random.nextLong(b));
        operationMap.put("subtract_random_max", (a, b) -> a - random.nextLong(b));
        operationMap.put("multiply_random_max", (a, b) -> a * random.nextLong(b));
        operationMap.put("divide_random_max", (a, b) -> a / random.nextLong(b));

        return operationMap;
    }

    public static Map<String, BinaryOperator<Integer>> getOperationMappingsInteger() {
        Map<String, BinaryOperator<Integer>> operationMap = new HashMap<>();
        operationMap.put("addition", (a, b) -> a + b);
        operationMap.put("subtraction", (a, b) -> a - b);
        operationMap.put("multiplication", (a, b) -> a * b);
        operationMap.put("division", (a, b) -> a / b);
        operationMap.put("multiply_base", (a, b) -> a * (b + 1));
        operationMap.put("multiply_total", (a, b) -> a * (1 + b));
        operationMap.put("set_total", (a, b) -> b);
        operationMap.put("add_base_early", (a, b) -> a + b);
        operationMap.put("multiply_base_additive", (a, b) -> a + (a * b));
        operationMap.put("multiply_base_multiplicative", (a, b) -> a * (1 + b));
        operationMap.put("add_base_late", (a, b) -> a + b);

        Random random = new Random();

        operationMap.put("add_random_max", (a, b) -> a + random.nextInt(b));
        operationMap.put("subtract_random_max", (a, b) -> a - random.nextInt(b));
        operationMap.put("multiply_random_max", (a, b) -> a * random.nextInt(b));
        operationMap.put("divide_random_max", (a, b) -> a / random.nextInt(b));

        return operationMap;
    }


    public static Map<String, BinaryOperator<Float>> getOperationMappingsFloat() {
        Map<String, BinaryOperator<Float>> operationMap = new HashMap<>();
        operationMap.put("addition", (a, b) -> a + b);
        operationMap.put("subtraction", (a, b) -> a - b);
        operationMap.put("multiplication", (a, b) -> a * b);
        operationMap.put("division", (a, b) -> a / b);
        operationMap.put("multiply_base", (a, b) -> a * (b + 1));
        operationMap.put("multiply_total", (a, b) -> a * (1 + b));
        operationMap.put("set_total", (a, b) -> b);
        operationMap.put("add_base_early", (a, b) -> a + b);
        operationMap.put("multiply_base_additive", (a, b) -> a + (a * b));
        operationMap.put("multiply_base_multiplicative", (a, b) -> a * (1 + b));
        operationMap.put("add_base_late", (a, b) -> a + b);

        Random random = new Random();

        operationMap.put("add_random_max", (a, b) -> a + random.nextFloat(b));
        operationMap.put("subtract_random_max", (a, b) -> a - random.nextFloat(b));
        operationMap.put("multiply_random_max", (a, b) -> a * random.nextFloat(b));
        operationMap.put("divide_random_max", (a, b) -> a / random.nextFloat(b));

        return operationMap;
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
