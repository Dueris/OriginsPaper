package me.dueris.genesismc.util;

import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.components.CraftFoodComponent;
import org.bukkit.craftbukkit.potion.CraftPotionUtil;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class Utils extends Util { // Extend MC Utils for easy access to them
	public static Registry<DamageType> DAMAGE_REGISTRY = CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.DAMAGE_TYPE);
	public static MinecraftServer server = GenesisMC.server;
	public static CraftServer bukkitServer = server.server;
	public static HashMap<String, Material> KNOWN_MATERIALS = new HashMap<>();

	static {
		BuiltInRegistries.BLOCK.forEach(block -> {
			String k = CraftBlockType.minecraftToBukkit(block).getKey().asString();
			if (k.contains(":")) {
				KNOWN_MATERIALS.put(k, block.defaultBlockState().getBukkitMaterial()); // With minecraft: namespace
				k = k.split(":")[1];
			}
			KNOWN_MATERIALS.put(k, block.defaultBlockState().getBukkitMaterial()); // Without minecraft: namespace
		});
	}

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

	public static PotionEffect parsePotionEffect(FactoryJsonObject effect) {
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

		return new PotionEffect(PotionEffectType.getByKey(new NamespacedKey(potionEffect.split(":")[0], potionEffect.split(":")[1])), duration, amplifier, isAmbient, showParticles, showIcon);
	}

	public static List<PotionEffect> parseAndReturnPotionEffects(FactoryJsonObject power) {
		List<PotionEffect> effectList = new ArrayList<>();
		FactoryJsonObject singleEffect = power.isPresent("effect") ? power.getJsonObject("effect") : new FactoryJsonObject(new JsonObject());
		List<FactoryJsonObject> effects = (power.isPresent("effects") ? power.getJsonArray("effects") : new FactoryJsonArray(new JsonArray())).asJsonObjectList();

		if (singleEffect != null && !singleEffect.isEmpty()) {
			effects.add(singleEffect);
		}

		for (FactoryJsonObject effect : effects) {
			effectList.add(parsePotionEffect(effect));
		}
		return effectList;
	}

	public static void addPositionedItemStack(Inventory inventory, ItemStack stack, int slot) {
		int maxSlots = inventory.getSize();
		if (slot < 0 || slot > maxSlots) {
			GenesisMC.getPlugin().getLogger().warning("Invalid slot number provided!");
			return;
		}

		if (inventory.getItem(slot) == null) {
			inventory.setItem(slot, stack);
			return;
		}

		int originalSlot = slot;
		slot = (slot + 1) % maxSlots;
		while (slot != originalSlot) {
			if (inventory.getItem(slot) == null) {
				inventory.setItem(slot, stack);
				return;
			}
			slot = (slot + 1) % maxSlots;
		}

		GenesisMC.getPlugin().getLogger().warning("Inventory is full!");
	}

	public static Sound parseSound(String sound) {
		return CraftRegistry.SOUNDS.get(NamespacedKey.fromString(sound));
	}

	public static Registry<?> getRegistry(ResourceKey<Registry<?>> registry) {
		return CraftRegistry.getMinecraftRegistry().registryOrThrow(registry);
	}

	public static Pair<String, String> getNameOrTag(PowerType power) {
		String name = power.getName();
		String tag = power.getTag();
		return new Pair<String, String>() {
			@Override
			public String left() {
				return !name.equals("craftapoli.name.not_found") ? name : tag;
			}

			@Override
			public String right() {
				return power.getTag();
			}
		};
	}

	public static List<MobEffectInstance> toMobEffectList(List<PotionEffect> effects) {
		List<MobEffectInstance> ret = new ArrayList<>();
		effects.forEach(effect -> ret.add(CraftPotionUtil.fromBukkit(effect)));
		return ret;
	}

	public static CraftFoodComponent parseProperties(FactoryJsonObject jsonObject) {
		FoodProperties.Builder builder = new FoodProperties.Builder();
		Utils.computeIfObjectPresent("hunger", jsonObject, value -> builder.nutrition(value.getNumber().getInt()));
		Utils.computeIfObjectPresent("saturation", jsonObject, value -> builder.saturationModifier(value.getNumber().getFloat()));
		// Value removed in 1.20.5
        /* Utils.computeIfObjectPresent("meat", jsonObject, value -> {
            if (value.isBoolean() && value.getBoolean()) builder.meat();
        }); */
		Utils.computeIfObjectPresent("always_edible", jsonObject, value -> {
			if (value.isBoolean() && value.getBoolean()) builder.alwaysEdible();
		});
		Utils.computeIfObjectPresent("snack", jsonObject, value -> {
			if (value.isBoolean() && value.getBoolean()) builder.fast();
		});
		List<PotionEffect> effects = parseAndReturnPotionEffects(jsonObject);
		effects.forEach(potionEffect -> {
			MobEffectInstance instance = CraftPotionUtil.fromBukkit(potionEffect);
			builder.effect(instance, 1.0F);
		});
		return new CraftFoodComponent(builder.build());
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

	public static boolean apoli$isSubmergedInLoosely(Entity entity, TagKey<Fluid> tag) {
		if (tag == null) return false;
		Optional<Set<TagKey<Fluid>>> submergedSet = getSubmergedSet(entity);
		if (submergedSet.isPresent() && submergedSet.get() != null) {
			return submergedSet.get().contains(tag);
		}
		return false;
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
			return Optional.of(Reflector.accessField("fluidHeight", Entity.class, entity, Object2DoubleMap.class));
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	protected static Optional<Set<TagKey<Fluid>>> getSubmergedSet(Entity entity) {
		try {
			return Optional.of(Reflector.accessField("fluidOnEyes", Entity.class, entity, Set.class));
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
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

	public static PotionEffectType getPotionEffectType(String key) {
		if (key == null) {
			return null;
		}
		return org.bukkit.Registry.EFFECT.get(NamespacedKey.fromString(key));
	}

	public static PotionEffectType getPotionEffectType(NamespacedKey key) {
		if (key == null) {
			return null;
		}
		return org.bukkit.Registry.EFFECT.get(key);
	}

	public static <T> List<T> collectValues(Collection<List<T>> collection) {
		List<T> lC = new ArrayList<>();
		collection.forEach(lC::addAll);
		return lC;
	}

	public static int[] missingNumbers(Integer[] array, int minRange, int maxRange) {
		boolean[] found = new boolean[maxRange - minRange + 1];
		int missingCount = 0;
		for (int num : array) {
			int adjustedIndex = num - minRange;
			if (adjustedIndex >= 0 && adjustedIndex < found.length) {
				found[adjustedIndex] = true;
			}
		}

		for (boolean val : found) {
			if (!val) {
				missingCount++;
			}
		}

		int[] missingNumbers = new int[missingCount];
		int index = 0;
		for (int i = minRange; i <= maxRange; i++) {
			int adjustedIndex = i - minRange;
			if (adjustedIndex >= 0 && adjustedIndex < found.length && !found[adjustedIndex]) {
				missingNumbers[index++] = i;
			}
		}

		return missingNumbers;
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

	public static void computeIfObjectPresent(String key, FactoryJsonObject object, Consumer<FactoryElement> function) {
		if (object.isPresent(key)) {
			function.accept(object.getElement(key));
		}
	}

	public static <T> T getOrAbsent(Optional<T> optional, T absent) {
		return optional.orElse(absent);
	}

	public static <T> Optional<T> createIfPresent(T instance) {
		if (instance != null) return Optional.of(instance);
		return Optional.empty();
	}

	public static JsonArray toJsonStringArray(List<String> strings) {
		Gson gson = new Gson();
		JsonArray array = new JsonArray();
		for (String s : strings) {
			array.add(s);
		}
		return array;
	}

	public static Optional<Entity> getEntityWithPassengers(Level world, EntityType<?> entityType, @Nullable CompoundTag entityNbt, Vec3 pos, float yaw, float pitch) {
		return getEntityWithPassengers(world, entityType, entityNbt, pos, Optional.of(yaw), Optional.of(pitch));
	}

	public static Optional<Entity> getEntityWithPassengers(Level world, EntityType<?> entityType, @Nullable CompoundTag entityNbt, Vec3 pos, Optional<Float> yaw, Optional<Float> pitch) {
		if (!(world instanceof ServerLevel serverWorld)) {
			return Optional.empty();
		}

		CompoundTag entityToSpawnNbt = new CompoundTag();
		if (entityNbt != null && !entityNbt.isEmpty()) {
			entityToSpawnNbt.merge(entityNbt);
		}

		entityToSpawnNbt.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString());
		Entity entityToSpawn = EntityType.loadEntityRecursive(
			entityToSpawnNbt,
			serverWorld,
			entity -> {
				entity.moveTo(pos.x, pos.y, pos.z, yaw.orElse(entity.getYRot()), pitch.orElse(entity.getXRot()));
				return entity;
			}
		);

		if (entityToSpawn == null) {
			return Optional.empty();
		}

		if ((entityNbt == null || entityNbt.isEmpty()) && entityToSpawn instanceof Mob mobToSpawn) {
			mobToSpawn.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(BlockPos.containing(pos)), MobSpawnType.COMMAND, null);
		}

		return Optional.of(entityToSpawn);

	}

	public static void unzip(String zipFilePath, String destDirectory) {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}

		String zipFileName = new File(zipFilePath).getName();
		String zipDirName = zipFileName.substring(0, zipFileName.lastIndexOf('.'));
		String destDirForZip = destDirectory + File.separator + zipDirName;

		try {
			ZipFile zipFile = new ZipFile(zipFilePath);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
				String entryName = zipEntry.getName();
				File entryFile = new File(destDirForZip + File.separator + entryName);
				if (zipEntry.isDirectory()) {
					entryFile.mkdirs();
				} else {
					File parent = entryFile.getParentFile();
					if (parent != null && !parent.exists()) {
						parent.mkdirs();
					}
					InputStream inputStream = zipFile.getInputStream(zipEntry);
					FileOutputStream outputStream = new FileOutputStream(entryFile);
					byte[] buffer = new byte[1024];
					int length;
					while ((length = inputStream.read(buffer)) > 0) {
						outputStream.write(buffer, 0, length);
					}
					outputStream.close();
					inputStream.close();
				}
			}
			zipFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void unpackOriginPack() {
		try {
			CodeSource src = Utils.class.getProtectionDomain().getCodeSource();
			URL jar = src.getLocation();
			ZipInputStream zip = new ZipInputStream(jar.openStream());
			while (true) {
				ZipEntry entry = zip.getNextEntry();
				if (entry == null)
					break;
				String name = entry.getName();

				if (!name.startsWith("datapack/")) continue;
				if (!name.startsWith("datapack/builtin")) continue;
				if (FilenameUtils.getExtension(name).equals("zip")) continue;
				if (name.equals("datapack/")) continue;

				name = name.substring(9);
				File file = new File(GenesisMC.getTmpFolder().getAbsolutePath().replace(".\\", "") + File.separator + name);
				if (!file.getName().contains(".")) {
					Files.createDirectory(Path.of(file.getAbsolutePath()));
					continue;
				}

				File parentDir = file.getParentFile();
				if (!parentDir.exists()) {
					parentDir.mkdirs();
				}

				Files.writeString(Path.of(file.getAbsolutePath()), new String(zip.readAllBytes()));
			}
			zip.close();
		} catch (Exception e) {
			// Say nothing, no need to print.
		}
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
		operationMap.put("addition", Double::sum);
		operationMap.put("add", Double::sum);
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
		operationMap.put("add_base_early", Double::sum);
		operationMap.put("multiply_base_additive", (a, b) -> a + (a * b));
		operationMap.put("multiply_base_multiplicative", (a, b) -> a * (1 + b));
		operationMap.put("add_base_late", Double::sum);

		Random random = new Random();

		operationMap.put("add_random_max", (a, b) -> a + random.nextDouble(b));
		operationMap.put("subtract_random_max", (a, b) -> a - random.nextDouble(b));
		operationMap.put("multiply_random_max", (a, b) -> a * random.nextDouble(b));
		operationMap.put("divide_random_max", (a, b) -> a / random.nextDouble(b));

		return operationMap;
	}

	public static Map<String, BinaryOperator<Integer>> getOperationMappingsInteger() {
		Map<String, BinaryOperator<Integer>> operationMap = new HashMap<>();
		operationMap.put("addition", Integer::sum);
		operationMap.put("add", Integer::sum);
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
		operationMap.put("add_base_early", Integer::sum);
		operationMap.put("multiply_base_additive", (a, b) -> a + (a * b));
		operationMap.put("multiply_base_multiplicative", (a, b) -> a * (1 + b));
		operationMap.put("add_base_late", Integer::sum);

		Random random = new Random();

		operationMap.put("add_random_max", (a, b) -> a + random.nextInt(b));
		operationMap.put("subtract_random_max", (a, b) -> a - random.nextInt(b));
		operationMap.put("multiply_random_max", (a, b) -> a * random.nextInt(b));
		operationMap.put("divide_random_max", (a, b) -> a / random.nextInt(b));

		return operationMap;
	}


	public static Map<String, BinaryOperator<Float>> getOperationMappingsFloat() {
		Map<String, BinaryOperator<Float>> operationMap = new HashMap<>();
		operationMap.put("addition", Float::sum);
		operationMap.put("add", Float::sum);
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
		operationMap.put("add_base_early", Float::sum);
		operationMap.put("multiply_base_additive", (a, b) -> a + (a * b));
		operationMap.put("multiply_total_additive", (a, b) -> a + (a * b));
		operationMap.put("multiply_base_multiplicative", (a, b) -> a * (1 + b));
		operationMap.put("multiply_total_multiplicative", (a, b) -> a * (1 + b));
		operationMap.put("add_base_late", Float::sum);

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
