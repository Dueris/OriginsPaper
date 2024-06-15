package me.dueris.genesismc.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.factory.FactoryNumber;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.registry.registries.Origin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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

public class Util {
	public static Registry<DamageType> DAMAGE_REGISTRY = CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.DAMAGE_TYPE);
	public static MinecraftServer server = GenesisMC.server;
	public static CraftServer bukkitServer = server.server;
	public static HashMap<String, Material> KNOWN_MATERIALS = new HashMap<>();
	public static org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger("GenesisMC");

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
		boolean showParticles = false;
		boolean showIcon = true;

		if (effect.isPresent("effect")) potionEffect = effect.getString("effect");
		if (effect.isPresent("duration")) duration = effect.getNumber("duration").getInt();
		if (effect.isPresent("amplifier")) amplifier = effect.getNumber("amplifier").getInt();
		if (effect.isPresent("is_ambient")) isAmbient = effect.getBooleanOrDefault("is_ambient", true);
		if (effect.isPresent("show_particles")) showParticles = effect.getBooleanOrDefault("show_particles", false);
		if (effect.isPresent("show_icon")) showIcon = effect.getBooleanOrDefault("show_icon", false);

		return new PotionEffect(PotionEffectType.getByKey(NamespacedKey.fromString(potionEffect)), duration, amplifier, isAmbient, showParticles, showIcon);
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

	public static String getNameOrTag(PowerType power) {
		String name = power.getName();
		String tag = power.getTag();
		return !name.equals("craftapoli.name.not_found") ? name : tag;
	}

	public static String getNameOrTag(Origin power) {
		String name = power.getName();
		String tag = power.getTag();
		return !name.equals("craftapoli.name.not_found") ? name : tag;
	}

	public static List<MobEffectInstance> toMobEffectList(List<PotionEffect> effects) {
		List<MobEffectInstance> ret = new ArrayList<>();
		effects.forEach(effect -> ret.add(CraftPotionUtil.fromBukkit(effect)));
		return ret;
	}

	public static CraftFoodComponent parseProperties(FactoryJsonObject jsonObject) {
		FoodProperties.Builder builder = new FoodProperties.Builder();
		Util.computeIfObjectPresent("hunger", jsonObject, value -> builder.nutrition(value.getNumber().getInt()));
		Util.computeIfObjectPresent("saturation", jsonObject, value -> builder.saturationModifier(value.getNumber().getFloat()));
		// Value removed in 1.20.5
        /* Utils.computeIfObjectPresent("meat", jsonObject, value -> {
            if (value.isBoolean() && value.getBoolean()) builder.meat();
        }); */
		Util.computeIfObjectPresent("always_edible", jsonObject, value -> {
			if (value.isBoolean() && value.getBoolean()) builder.alwaysEdible();
		});
		Util.computeIfObjectPresent("snack", jsonObject, value -> {
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

	@SuppressWarnings("unchecked")
	protected static Optional<Object2DoubleMap<TagKey<Fluid>>> getFluidHeightMap(Entity entity) {
		try {
			return Optional.of(Reflector.accessField("fluidHeight", Entity.class, entity, Object2DoubleMap.class));
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	@SuppressWarnings("unchecked")
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

	public static EquipmentSlot getEquipmentSlotForItem(net.minecraft.world.item.ItemStack stack) {
		Equipable equipable = Equipable.get(stack);

		return equipable != null ? equipable.getEquipmentSlot() : EquipmentSlot.MAINHAND;
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

	public static String makeDescriptionId(String type, @Nullable ResourceLocation id) {
		return id == null ? type + ".unregistered_sadface" : type + "." + id.getNamespace() + "." + id.getPath().replace('/', '.');
	}

	public static <T> List<T> collectValues(Collection<List<T>> collection) {
		List<T> lC = new ArrayList<>();
		collection.forEach(lC::addAll);
		return lC;
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

	public static <T> Optional<T> ifElse(Optional<T> optional, Consumer<T> presentAction, Runnable elseAction) {
		if (optional.isPresent()) {
			presentAction.accept(optional.get());
		} else {
			elseAction.run();
		}

		return optional;
	}

	public static JsonArray toJsonStringArray(List<String> strings) {
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
			CodeSource src = Util.class.getProtectionDomain().getCodeSource();
			URL jar = src.getLocation();
			ZipInputStream zip = new ZipInputStream(jar.openStream());
			while (true) {
				ZipEntry entry = zip.getNextEntry();
				if (entry == null)
					break;
				String name = entry.getName();

				if (!name.startsWith("minecraft/")) continue;
				if (FilenameUtils.getExtension(name).equals("zip")) continue;
				if (name.equals("minecraft/")) continue;

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

	private static <T extends Number> Map<String, BinaryOperator<T>> createOperationMappings(
		BinaryOperator<T> addition,
		BinaryOperator<T> subtraction,
		BinaryOperator<T> multiplication,
		BinaryOperator<T> division,
		BinaryOperator<T> multiplyBase,
		BinaryOperator<T> multiplyTotal,
		BinaryOperator<T> multiplyTotalAddictive,
		BinaryOperator<T> minBase,
		BinaryOperator<T> maxBase) {

		Map<String, BinaryOperator<T>> operationMap = new HashMap<>();
		operationMap.put("addition", addition);
		operationMap.put("add", addition);
		operationMap.put("subtraction", subtraction);
		operationMap.put("subtract", subtraction);
		operationMap.put("multiplication", multiplication);
		operationMap.put("multiply", multiplication);
		operationMap.put("division", division);
		operationMap.put("divide", division);
		operationMap.put("multiply_base", multiplyBase);
		operationMap.put("multiply_total", multiplyTotal);
		operationMap.put("set_total", (a, b) -> b);
		operationMap.put("set", (a, b) -> b);
		operationMap.put("add_base_early", addition);
		operationMap.put("multiply_base_additive", multiplyBase);
		operationMap.put("multiply_base_multiplicative", multiplyTotal);
		operationMap.put("add_base_late", addition);
		operationMap.put("multiply_total_additive", multiplyTotalAddictive);
		operationMap.put("multiply_total_multiplicative", multiplyTotal);
		operationMap.put("min_base", minBase);
		operationMap.put("max_base", maxBase);
		operationMap.put("min_total", minBase);
		operationMap.put("max_total", maxBase);

		return operationMap;
	}

	public static Map<String, BinaryOperator<Double>> getOperationMappingsDouble() {
		return createOperationMappings(
			Double::sum,
			(a, b) -> a - b,
			(a, b) -> a * b,
			(a, b) -> a / b,
			(a, b) -> a + (a * b),
			(a, b) -> a * (1 + b),
			(a, b) -> a * (a * b),
			(a, b) -> (a > b) ? a : b,
			(a, b) -> (a < b) ? a : b
		);
	}

	public static Map<String, BinaryOperator<Integer>> getOperationMappingsInteger() {
		return createOperationMappings(
			Integer::sum,
			(a, b) -> a - b,
			(a, b) -> a * b,
			(a, b) -> a / b,
			(a, b) -> a + (a * b),
			(a, b) -> a * (1 + b),
			(a, b) -> a * (a * b),
			(a, b) -> (a > b) ? a : b,
			(a, b) -> (a < b) ? a : b
		);
	}

	public static Map<String, BinaryOperator<Float>> getOperationMappingsFloat() {
		return createOperationMappings(
			Float::sum,
			(a, b) -> a - b,
			(a, b) -> a * b,
			(a, b) -> a / b,
			(a, b) -> a + (a * b),
			(a, b) -> a * (1 + b),
			(a, b) -> a * (a * b),
			(a, b) -> (a > b) ? a : b,
			(a, b) -> (a < b) ? a : b
		);
	}

	public static List<Integer> fillMissingNumbers(List<Integer> numbers, int min, int max) {
		Set<Integer> numberSet = new HashSet<>(numbers);
		for (int i = min; i <= max; i++) {
			numberSet.add(i);
		}

		List<Integer> filledNumbers = new ArrayList<>(numberSet);
		Collections.sort(filledNumbers);
		return filledNumbers;
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

	public static double slope(double[] p1, double[] p2) {
		if (p2[0] - p1[0] == 0) throw new ArithmeticException("Line is vertical");
		return (p2[1] - p1[1]) / (p2[0] - p1[0]);
	}

	public static double[] rotatePoint(double[] point, double angle) {
		double cosA = Math.cos(angle);
		double sinA = Math.sin(angle);
		return new double[]{
			point[0] * cosA - point[1] * sinA,
			point[0] * sinA + point[1] * cosA
		};
	}

	public static double lerp(double start, double end, double t) {
		return start + t * (end - start);
	}

	public static int lcm(int a, int b) {
		return Math.abs(a * b) / gcd(a, b);
	}

	public static int gcd(int a, int b) {
		while (b != 0) {
			int t = b;
			b = a % b;
			a = t;
		}
		return a;
	}

	public static long factorial(int n) {
		if (n < 0) throw new IllegalArgumentException("n must be non-negative");
		return (n == 0) ? 1 : n * factorial(n - 1);
	}

	public static int[] convertToIntArray(Collection<Integer> integers) {
		return integers.stream()
			.mapToInt(Integer::intValue)
			.toArray();
	}

	public static int getArmorValue(ItemStack armorItem) {
		net.minecraft.world.item.Item stack = CraftItemStack.asNMSCopy(armorItem).getItem();
		return stack instanceof ArmorItem item ? item.getDefense() : 0;
	}

	public static void consumeItem(ItemStack item) {
		item.setAmount(item.getAmount() - 1);
	}

	public static List<Integer> getSlots(FactoryJsonObject data) {
		FactoryJsonArray slots = null;
		if (data.isPresent("slots")) {
			slots = data.getJsonArray("slots");
		}
		if (!data.isPresent("slot")) {
			return new ArrayList<>();
		}
		return slots == null ? List.of(data.getNumber("slot").getInt()) : slots.asList().stream().map(FactoryElement::getNumber).map(FactoryNumber::getInt).toList();
	}

	public static int checkInventory(FactoryJsonObject data, Entity entity, @Nullable me.dueris.genesismc.factory.powers.apoli.Inventory inventoryPower, Function<net.minecraft.world.item.ItemStack, Integer> processor) {
		FactoryJsonObject itemCondition = data.getJsonObject("item_condition");
		List<Integer> slots = getSlots(data);
		if (slots.isEmpty()) {
			slots = fillMissingNumbers(slots, 0, 40);
		}

		int matches = 0;
		slots.removeIf(slot -> slotNotWithinBounds(entity, inventoryPower, slot));

		for (int slot : slots) {

			SlotAccess stackReference = getStackReference(entity, inventoryPower, slot);
			net.minecraft.world.item.ItemStack stack = stackReference.get();

			if ((itemCondition == null && !stack.isEmpty()) || (itemCondition == null || ConditionExecutor.testItem(itemCondition, stack.getBukkitStack())) && !stack.getBukkitStack().getType().isAir()) {
				matches += processor.apply(stack);
			}

		}

		return matches;

	}

	public static boolean slotNotWithinBounds(Entity entity, @Nullable me.dueris.genesismc.factory.powers.apoli.Inventory inventoryPower, int slot) {
		return entity.getSlot(slot) == SlotAccess.NULL;
	}

	public static SlotAccess getStackReference(Entity entity, @Nullable me.dueris.genesismc.factory.powers.apoli.Inventory inventoryPower, int slot) {
		return entity.getSlot(slot);
	}

	public enum ProcessMode {
		STACKS(stack -> 1),
		ITEMS(net.minecraft.world.item.ItemStack::getCount);

		private final Function<net.minecraft.world.item.ItemStack, Integer> processor;

		ProcessMode(Function<net.minecraft.world.item.ItemStack, Integer> processor) {
			this.processor = processor;
		}

		public Function<net.minecraft.world.item.ItemStack, Integer> getProcessor() {
			return processor;
		}
	}

	public enum OS {
		LINUX("linux"),
		SOLARIS("solaris"),
		WINDOWS("windows") {
			@Override
			protected String[] getOpenUrlArguments(URL url) {
				return new String[]{"rundll32", "url.dll,FileProtocolHandler", url.toString()};
			}
		},
		OSX("mac") {
			@Override
			protected String[] getOpenUrlArguments(URL url) {
				return new String[]{"open", url.toString()};
			}
		},
		UNKNOWN("unknown");

		private final String telemetryName;

		OS(final String name) {
			this.telemetryName = name;
		}

		public void openUrl(URL url) {
			throw new IllegalStateException("This method is not useful on dedicated servers."); // Paper - Fix warnings on build by removing client-only code
		}

		public void openUri(URI uri) {
			try {
				this.openUrl(uri.toURL());
			} catch (MalformedURLException var3) {
				Util.LOGGER.error("Couldn't open uri '{}'", uri, var3);
			}
		}

		public void openFile(File file) {
			try {
				this.openUrl(file.toURI().toURL());
			} catch (MalformedURLException var3) {
				Util.LOGGER.error("Couldn't open file '{}'", file, var3);
			}
		}

		protected String[] getOpenUrlArguments(URL url) {
			String string = url.toString();
			if ("file".equals(url.getProtocol())) {
				string = string.replace("file:", "file://");
			}

			return new String[]{"xdg-open", string};
		}

		public void openUri(String uri) {
			try {
				this.openUrl(new URI(uri).toURL());
			} catch (MalformedURLException | IllegalArgumentException | URISyntaxException var3) {
				Util.LOGGER.error("Couldn't open uri '{}'", uri, var3);
			}
		}

		public String telemetryName() {
			return this.telemetryName;
		}
	}

	public static class ParserUtils {
		private static final Field JSON_READER_POS = net.minecraft.Util.make(() -> {
			try {
				Field field = JsonReader.class.getDeclaredField("pos");
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException var1) {
				throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", var1);
			}
		});
		private static final Field JSON_READER_LINESTART = net.minecraft.Util.make(() -> {
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

			T var4;
			try {
				JsonElement jsonElement = Streams.parse(jsonReader);
				var4 = getOrThrow(codec.parse(JsonOps.INSTANCE, jsonElement), JsonParseException::new);
			} catch (StackOverflowError var8) {
				throw new JsonParseException(var8);
			} finally {
				stringReader.setCursor(stringReader.getCursor() + getPos(jsonReader));
			}

			return var4;
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
