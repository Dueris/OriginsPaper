package me.dueris.genesismc.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.registry.registries.Power;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_20_R3.CraftRegistry;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;

public class Utils {
    public static Registry<DamageType> DAMAGE_REGISTRY = CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.DAMAGE_TYPE);
    public MinecraftServer server = GenesisMC.server;
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

    public static JsonElement readJSONFileAsString(File jsonObj) throws IOException {
        FileReader reader = new FileReader(jsonObj);
        JsonElement jsonElement = JsonParser.parseReader(reader);
        reader.close();
        return jsonElement;
    }

    public static String prettyPrintUsingGson(String uglyJson) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = JsonParser.parseString(uglyJson);
        String prettyJsonString = gson.toJson(jsonElement);
        return prettyJsonString;
    }

    public static Pair<String, String> getNameOrTag(Power power) {
        String name = power.getName();
        String tag = power.getTag();
        return new Pair<String, String>() {
            @Override
            public String left() {
                return name != "No Name" ? name : power.getPowerParent() != null ? getNameOrTag(power.getPowerParent()).first() : tag;
            }

            @Override
            public String right() {
                return power.getTag();
            }
        };
    }

    public static boolean inSnow(Level world, BlockPos... blockPositions) {
        return Arrays.stream(blockPositions)
            .anyMatch(blockPos -> {
                Biome biome = world.getBiome(blockPos).value();
                return biome.getPrecipitationAt(blockPos) == Biome.Precipitation.SNOW
                    && isRainingAndExposed(world, blockPos);
            });
    }

    public static double apoli$getFluidHeightLoosely(Entity entity, TagKey<Fluid> tag) {
        if (tag == null) return 0;
        Optional<Object2DoubleMap<TagKey<Fluid>>> fluidHeightMap = getFluidHeightMap(entity);
        if (fluidHeightMap.isPresent() && fluidHeightMap.get() != null) {
            Object2DoubleMap<TagKey<Fluid>> fluidHeight = fluidHeightMap.get();
            if (fluidHeight.containsKey(tag)) {
                return fluidHeight.getDouble(tag);
            }

            for (TagKey<Fluid> ft : fluidHeight.keySet()) {
                if (areTagsEqual(ft, tag)) {
                    return fluidHeight.getDouble(ft);
                }
            }
        }
        return 0;
    }

    public static <T> boolean areTagsEqual(TagKey<T> tag1, TagKey<T> tag2) {
        if (tag1 == tag2) {
            return true;
        }
        if (tag1 == null || tag2 == null) {
            return false;
        }
        if (!tag1.registry().equals(tag2.registry())) {
            return false;
        }
        return tag1.location().equals(tag2.location());
    }

    protected static Optional<Object2DoubleMap<TagKey<Fluid>>> getFluidHeightMap(Entity entity) {
        try {
            Field field = Entity.class.getDeclaredField(getFluidHeightFromReobf(1204));
            if (!field.isAccessible()) field.setAccessible(true);
            return Optional.of((Object2DoubleMap<TagKey<Fluid>>) field.get(entity));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private static String getFluidHeightFromReobf(int vNumber) {
        switch (vNumber) {
            case 1204:
                return "aj"; // 1.20.4
        }
        return "fluidHeight";
    }

    public static boolean inThunderstorm(Level world, BlockPos... blockPositions) {
        return Arrays.stream(blockPositions)
            .anyMatch(blockPos -> world.isThundering() && isRainingAndExposed(world, blockPos));
    }

    private static boolean isRainingAndExposed(Level world, BlockPos blockPos) {
        return world.isRaining()
            && world.canSeeSky(blockPos)
            && world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos).getY() < blockPos.getY();
    }

    public static boolean hasChangedBlockCoordinates(final Location fromLoc, final Location toLoc) {
        return !(fromLoc.getWorld().equals(toLoc.getWorld())
            && fromLoc.getBlockX() == toLoc.getBlockX()
            && fromLoc.getBlockY() == toLoc.getBlockY()
            && fromLoc.getBlockZ() == toLoc.getBlockZ());
    }

    public static void downloadFileFromURL(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        try (BufferedInputStream in = new BufferedInputStream(url.openStream())) {
            Path savePath = Path.of(System.getProperty("user.home"), "Downloads");
            Files.createDirectories(savePath);

            String fileName = url.getFile().substring(url.getFile().lastIndexOf('/') + 1);
            Path filePath = savePath.resolve(fileName);
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void downloadFileFromURL(String fileUrl, String saveDirectory) throws IOException {
        URL url = new URL(fileUrl);
        try (BufferedInputStream in = new BufferedInputStream(url.openStream())) {
            Path savePath = Path.of(saveDirectory);
            Files.createDirectories(savePath);

            Path filePath = savePath.resolve(getFileNameFromUrl(fileUrl));
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void downloadFileFromURL(String fileUrl, String saveDirectory, String fileName) throws IOException {
        URL url = new URL(fileUrl);
        try (BufferedInputStream in = new BufferedInputStream(url.openStream())) {
            Path savePath = Path.of(saveDirectory);
            Files.createDirectories(savePath);

            Path filePath = savePath.resolve(fileName);
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static String getFileNameFromUrl(String fileUrl) {
        String[] segments = fileUrl.split("/");
        return segments[segments.length - 1];
    }

    public static void printValues(ConfigurationSection section, String indent) {
        StringBuilder values = new StringBuilder();

        for (String key : section.getKeys(false)) {
            String path = section.getCurrentPath() + "|" + key;
            Object value = section.get(key);

            if (value instanceof ConfigurationSection subsection) {
                // If the value is another section, recursively print its values
                printValues(subsection, indent + "  ");
            } else {
                // Append the key and value to the StringBuilder
                values.append(indent).append(path).append(": ").append(value).append("  ");
            }
        }

        // Print the concatenated values
        Bukkit.getLogger().info(values.toString());
    }

    public static void computeIfObjectPresent(String key, JSONObject object, Consumer<Object> function) {
        if (object.containsKey(key)) {
            function.accept(object.get(key));
        }
    }

    public static <T> T getOrAbsent(Optional<T> optional, T absent) {
        return optional.isPresent() ? optional.get() : absent;
    }

    public static <T> Optional<T> createIfPresent(T instance) {
        if (instance != null) return Optional.of(instance);
        return Optional.empty();
    }

    public static int getArmorValue(ItemStack armorItem) {
        net.minecraft.world.item.Item stack = CraftItemStack.asNMSCopy(armorItem).getItem();
        return stack instanceof ArmorItem item ? item.getDefense() : 0;
    }

    public static void consumeItem(ItemStack item) {
        item.setAmount(item.getAmount() - 1);
    }

    // Math
    public static Map<String, BinaryOperator<Double>> getOperationMappingsDouble() {
        Map<String, BinaryOperator<Double>> operationMap = new HashMap<>();
        operationMap.put("addition", (a, b) -> a + b);
        operationMap.put("add", (a, b) -> a + b);
        operationMap.put("subtraction", (a, b) -> a - b);
        operationMap.put("subtract", (a, b) -> a - b);
        operationMap.put("multiplication", (a, b) -> a * b);
        operationMap.put("multiply", (a, b) -> a * b);
        operationMap.put("division", (a, b) -> a / b);
        operationMap.put("divide", (a, b) -> a / b);
        operationMap.put("multiply_base", (a, b) -> a * (b + 1));
        operationMap.put("multiply_total", (a, b) -> a * (1 + b));
        operationMap.put("set_total", (a, b) -> b);
        operationMap.put("set", (a, b) -> b);
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
        operationMap.put("add", (a, b) -> a + b);
        operationMap.put("subtraction", (a, b) -> a - b);
        operationMap.put("subtract", (a, b) -> a - b);
        operationMap.put("multiplication", (a, b) -> a * b);
        operationMap.put("multiply", (a, b) -> a * b);
        operationMap.put("division", (a, b) -> a / b);
        operationMap.put("divide", (a, b) -> a / b);
        operationMap.put("multiply_base", (a, b) -> a * (b + 1));
        operationMap.put("multiply_total", (a, b) -> a * (1 + b));
        operationMap.put("set_total", (a, b) -> b);
        operationMap.put("set", (a, b) -> b);
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
        operationMap.put("add", (a, b) -> a + b);
        operationMap.put("subtraction", (a, b) -> a - b);
        operationMap.put("subtract", (a, b) -> a - b);
        operationMap.put("multiplication", (a, b) -> a * b);
        operationMap.put("multiply", (a, b) -> a * b);
        operationMap.put("division", (a, b) -> a / b);
        operationMap.put("divide", (a, b) -> a / b);
        operationMap.put("multiply_base", (a, b) -> a * (b + 1));
        operationMap.put("multiply_total", (a, b) -> a * (1 + b));
        operationMap.put("set_total", (a, b) -> b);
        operationMap.put("set", (a, b) -> b);
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
        operationMap.put("add", (a, b) -> a + b);
        operationMap.put("subtraction", (a, b) -> a - b);
        operationMap.put("subtract", (a, b) -> a - b);
        operationMap.put("multiplication", (a, b) -> a * b);
        operationMap.put("multiply", (a, b) -> a * b);
        operationMap.put("division", (a, b) -> a / b);
        operationMap.put("divide", (a, b) -> a / b);
        operationMap.put("multiply_base", (a, b) -> a * (b + 1));
        operationMap.put("multiply_total", (a, b) -> a * (1 + b));
        operationMap.put("set_total", (a, b) -> b);
        operationMap.put("set", (a, b) -> b);
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
}
