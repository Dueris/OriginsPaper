package me.dueris.originspaper.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.Error;
import com.mojang.serialization.JsonOps;
import io.github.dueris.calio.util.ArgumentWrapper;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.registry.registries.Origin;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
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
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class Util {
	public static Registry<DamageType> DAMAGE_REGISTRY = CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.DAMAGE_TYPE);
	public static MinecraftServer server = OriginsPaper.server;
	public static CraftServer bukkitServer = server.server;
	public static HashMap<String, Material> KNOWN_MATERIALS = new HashMap<>();
	public static Logger LOGGER = LogManager.getLogger("OriginsPaper");

	static {
		BuiltInRegistries.BLOCK.forEach(block -> {
			String k = CraftBlockType.minecraftToBukkit(block).getKey().asString();
			if (k.contains(":")) {
				KNOWN_MATERIALS.put(k, block.defaultBlockState().getBukkitMaterial());
				k = k.split(":")[1];
			}

			KNOWN_MATERIALS.put(k, block.defaultBlockState().getBukkitMaterial());
		});
	}

	public static DamageSource getDamageSource(DamageType type) {
		return getDamageSource(type, null);
	}

	public static DamageSource getDamageSource(DamageType type, Entity attacker) {
		DamageSource source = null;

		for (ResourceKey<DamageType> dkey : DAMAGE_REGISTRY.registryKeySet()) {
			if (DAMAGE_REGISTRY.get(dkey).equals(type)) {
				source = new DamageSource(DAMAGE_REGISTRY.getHolderOrThrow(dkey), attacker, attacker, attacker == null ? null : attacker.position());
				break;
			}
		}

		return source;
	}

	public static void addPositionedItemStack(@NotNull Inventory inventory, org.bukkit.inventory.ItemStack stack, int slot) {
		int maxSlots = inventory.getSize();
		if (slot < 0 || slot > maxSlots) {
			OriginsPaper.getPlugin().getLogger().warning("Invalid slot number provided!");
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

		OriginsPaper.getPlugin().getLogger().warning("Inventory is full!");
	}

	public static @NotNull Registry<?> getRegistry(ResourceKey<Registry<?>> registry) {
		return CraftRegistry.getMinecraftRegistry().registryOrThrow(registry);
	}

	public static String getNameOrTag(@NotNull PowerType power) {
		String name = power.getName();
		String tag = power.getTag();
		return !name.equals("craftapoli.name.not_found") ? name : tag;
	}

	public static String getNameOrTag(@NotNull Origin power) {
		String name = power.getName();
		String tag = power.getTag();
		return !name.equals("craftapoli.name.not_found") ? name : tag;
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
		if (tag == null) {
			return false;
		} else {
			Optional<Set<TagKey<Fluid>>> submergedSet = getSubmergedSet(entity);
			return submergedSet.isPresent() && submergedSet.get() != null && submergedSet.get().contains(tag);
		}
	}

	public static <T> boolean areTagsEqual(TagKey<T> tag1, TagKey<T> tag2) {
		if (tag1 == tag2) {
			return true;
		} else if (tag1 != null && tag2 != null) {
			return tag1.registry().equals(tag2.registry()) && tag1.location().equals(tag2.location());
		} else {
			return false;
		}
	}

	protected static Optional<Object2DoubleMap<TagKey<Fluid>>> getFluidHeightMap(Entity entity) {
		try {
			return Optional.of(Reflector.accessField("fluidHeight", Entity.class, entity, Object2DoubleMap.class));
		} catch (Exception var2) {
			var2.printStackTrace();
			return Optional.empty();
		}
	}

	protected static Optional<Set<TagKey<Fluid>>> getSubmergedSet(Entity entity) {
		try {
			return Optional.of(Reflector.accessField("fluidOnEyes", Entity.class, entity, Set.class));
		} catch (Exception var2) {
			var2.printStackTrace();
			return Optional.empty();
		}
	}

	public static boolean inThunderstorm(Level world, BlockPos... blockPositions) {
		return Arrays.stream(blockPositions).anyMatch(blockPos -> world.isThundering() && isRainingAndExposed(world, blockPos));
	}

	private static boolean isRainingAndExposed(@NotNull Level world, BlockPos blockPos) {
		return world.isRaining() && world.canSeeSky(blockPos) && world.getHeightmapPos(Types.MOTION_BLOCKING, blockPos).getY() < blockPos.getY();
	}

	public static EquipmentSlot getEquipmentSlotForItem(ItemStack stack) {
		Equipable equipable = Equipable.get(stack);
		return equipable != null ? equipable.getEquipmentSlot() : EquipmentSlot.MAINHAND;
	}

	public static boolean hasChangedBlockCoordinates(@NotNull Location fromLoc, @NotNull Location toLoc) {
		return !fromLoc.getWorld().equals(toLoc.getWorld())
			|| fromLoc.getBlockX() != toLoc.getBlockX()
			|| fromLoc.getBlockY() != toLoc.getBlockY()
			|| fromLoc.getBlockZ() != toLoc.getBlockZ();
	}

	public static @NotNull String compileStrings(@NotNull List<String> strings) {
		StringBuilder builder = new StringBuilder();
		strings.forEach(builder::append);
		return builder.toString();
	}

	public static void downloadFileFromURL(String fileUrl) throws IOException {
		URL url = new URL(fileUrl);

		try (BufferedInputStream in = new BufferedInputStream(url.openStream())) {
			Path savePath = Path.of(System.getProperty("user.home"), "Downloads");
			Files.createDirectories(savePath);
			String fileName = url.getFile().substring(url.getFile().lastIndexOf(47) + 1);
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
		return key == null ? null : org.bukkit.Registry.EFFECT.get(NamespacedKey.fromString(key));
	}

	public static PotionEffectType getPotionEffectType(NamespacedKey key) {
		return key == null ? null : org.bukkit.Registry.EFFECT.get(key);
	}

	public static @NotNull String makeDescriptionId(String type, @Nullable ResourceLocation id) {
		return id == null ? type + ".unregistered_sadface" : type + "." + id.getNamespace() + "." + id.getPath().replace('/', '.');
	}

	public static <T> @NotNull List<T> collectValues(@NotNull Collection<List<T>> collection) {
		List<T> lC = new ArrayList<>();
		collection.forEach(lC::addAll);
		return lC;
	}

	@Contract(pure = true)
	private static String getFileNameFromUrl(@NotNull String fileUrl) {
		String[] segments = fileUrl.split("/");
		return segments[segments.length - 1];
	}

	public static void printValues(@NotNull ConfigurationSection section, String indent) {
		StringBuilder values = new StringBuilder();

		for (String key : section.getKeys(false)) {
			String path = section.getCurrentPath() + "|" + key;
			Object value = section.get(key);
			if (value instanceof ConfigurationSection subsection) {
				printValues(subsection, indent + "  ");
			} else {
				values.append(indent).append(path).append(": ").append(value).append("  ");
			}
		}

		Bukkit.getLogger().info(values.toString());
	}

	@Contract(value = "_, !null -> !null", pure = true)
	public static <T> T getOrAbsent(@NotNull Optional<T> optional, T absent) {
		return optional.orElse(absent);
	}

	public static <T> Optional<T> createIfPresent(T instance) {
		return instance != null ? Optional.of(instance) : Optional.empty();
	}

	@Contract("_, _, _ -> param1")
	public static <T> @NotNull Optional<T> ifElse(@NotNull Optional<T> optional, Consumer<T> presentAction, Runnable elseAction) {
		if (optional.isPresent()) {
			presentAction.accept(optional.get());
		} else {
			elseAction.run();
		}

		return optional;
	}

	public static @NotNull JsonArray toJsonStringArray(@NotNull List<String> strings) {
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
				File file = new File(OriginsPaper.getTmpFolder().getAbsolutePath().replace(".\\", "") + File.separator + name);
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

	private static <T extends Number> @NotNull Map<String, BinaryOperator<T>> createOperationMappings(
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
		operationMap.put("add_value", addition);
		operationMap.put("subtract_value", addition);
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

	public static @NotNull Map<String, BinaryOperator<Double>> getOperationMappingsDouble() {
		return createOperationMappings(
			Double::sum,
			(a, b) -> a - b,
			(a, b) -> a * b,
			(a, b) -> a / b,
			(a, b) -> a + a * b,
			(a, b) -> a * (1.0 + b),
			(a, b) -> a * a * b,
			(a, b) -> a > b ? a : b,
			(a, b) -> a < b ? a : b
		);
	}

	public static @NotNull Map<String, BinaryOperator<Integer>> getOperationMappingsInteger() {
		return createOperationMappings(
			Integer::sum,
			(a, b) -> a - b,
			(a, b) -> a * b,
			(a, b) -> a / b,
			(a, b) -> a + a * b,
			(a, b) -> a * (1 + b),
			(a, b) -> a * a * b,
			(a, b) -> a > b ? a : b,
			(a, b) -> a < b ? a : b
		);
	}

	public static @NotNull Map<String, BinaryOperator<Float>> getOperationMappingsFloat() {
		return createOperationMappings(
			Float::sum,
			(a, b) -> a - b,
			(a, b) -> a * b,
			(a, b) -> a / b,
			(a, b) -> a + a * b,
			(a, b) -> a * (1.0F + b),
			(a, b) -> a * a * b,
			(a, b) -> a > b ? a : b,
			(a, b) -> a < b ? a : b
		);
	}

	public static void fillMissingNumbers(List<Integer> numbers, int min, int max) {
		Set<Integer> numberSet = new HashSet<>(numbers);

		for (int i = min; i <= max; i++) {
			if (!numberSet.contains(i)) {
				numbers.add(i);
			}
		}
	}

	@Contract(pure = true)
	public static int @NotNull [] missingNumbers(Integer @NotNull [] array, int minRange, int maxRange) {
		boolean[] found = new boolean[maxRange - minRange + 1];
		int missingCount = 0;
		Integer[] missingNumbers = array;
		int index = array.length;

		for (int i = 0; i < index; i++) {
			int num = missingNumbers[i];
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

		int[] missingNumbersx = new int[missingCount];
		index = 0;

		for (int ix = minRange; ix <= maxRange; ix++) {
			int adjustedIndex = ix - minRange;
			if (adjustedIndex >= 0 && adjustedIndex < found.length && !found[adjustedIndex]) {
				missingNumbersx[index++] = ix;
			}
		}

		return missingNumbersx;
	}

	public static BarColor convertToBarColor(java.awt.@NotNull Color color) {
		int rgb = color.getRGB();
		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue = rgb & 0xFF;

		if (red > green && red > blue) {
			if (red - green < 30) return BarColor.YELLOW;
			return BarColor.RED;
		} else if (green > red && green > blue) {
			return BarColor.GREEN;
		} else if (blue > red && blue > green) {
			return BarColor.BLUE;
		} else if (red == green && red == blue) {
			return BarColor.WHITE;
		} else if (red == green) {
			return BarColor.YELLOW;
		} else if (red == blue) {
			return BarColor.PURPLE;
		} else {
			return BarColor.GREEN;
		}
	}

	@Contract(pure = true)
	public static double slope(double @NotNull [] p1, double @NotNull [] p2) {
		if (p2[0] - p1[0] == 0.0) {
			throw new ArithmeticException("Line is vertical");
		} else {
			return (p2[1] - p1[1]) / (p2[0] - p1[0]);
		}
	}

	@Contract("_, _ -> new")
	public static double @NotNull [] rotatePoint(double @NotNull [] point, double angle) {
		double cosA = Math.cos(angle);
		double sinA = Math.sin(angle);
		return new double[]{point[0] * cosA - point[1] * sinA, point[0] * sinA + point[1] * cosA};
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

	public static int[] convertToIntArray(@NotNull Collection<Integer> integers) {
		return integers.stream().mapToInt(Integer::intValue).toArray();
	}

	public static int getArmorValue(org.bukkit.inventory.ItemStack armorItem) {
		return CraftItemStack.asNMSCopy(armorItem).getItem() instanceof ArmorItem item ? item.getDefense() : 0;
	}

	public static boolean attemptToTeleport(Entity entity, ServerLevel serverWorld, double destX, double destY, double destZ, double offsetX, double offsetY, double offsetZ, double areaHeight, boolean loadedChunksOnly, Heightmap.Types heightmap, Predicate<BlockInWorld> landingBlockCondition, Predicate<Entity> landingCondition) {

		BlockPos.MutableBlockPos blockPos = BlockPos.containing(destX, destY, destZ).mutable();
		boolean foundSurface = false;
		if (heightmap != null) {
			blockPos.set(serverWorld.getHeightmapPos(heightmap, blockPos).below());
			if (landingBlockCondition.test(new BlockInWorld(serverWorld, blockPos, true))) {
				blockPos.set(blockPos.above());
				foundSurface = true;
			}
		} else {

			for (double decrements = 0; decrements < areaHeight / 2; ++decrements) {

				blockPos.set(blockPos.below());
				if (landingBlockCondition.test(new BlockInWorld(serverWorld, blockPos, true))) {
					blockPos.set(blockPos.above());
					foundSurface = true;
					break;
				}
			}
		}

		destX = offsetX == 0 ? destX : Mth.floor(destX) + offsetX;
		destY = blockPos.getY() + offsetY;
		destZ = offsetZ == 0 ? destZ : Mth.floor(destZ) + offsetZ;

		blockPos.set(destX, destY, destZ);
		if (!foundSurface) {
			return false;
		}

		double prevX = entity.getX();
		double prevY = entity.getY();
		double prevZ = entity.getZ();

		ChunkPos chunkPos = new ChunkPos(blockPos);
		if (!serverWorld.hasChunk(chunkPos.x, chunkPos.z)) {

			if (loadedChunksOnly) {
				return false;
			}

			serverWorld.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkPos, 0, entity.getId());
			serverWorld.getChunk(chunkPos.x, chunkPos.z);

		}

		entity.teleportTo(destX, destY, destZ);

		if (!landingCondition.test(entity)) {
			entity.teleportTo(prevX, prevY, prevZ);
			return false;
		}

		if (entity instanceof PathfinderMob pathAwareEntity) {
			pathAwareEntity.getNavigation().stop();
		}

		return true;

	}

	public static void consumeItem(org.bukkit.inventory.@NotNull ItemStack item) {
		item.setAmount(item.getAmount() - 1);
	}

	@Contract("_ -> new")
	public static @NotNull ArgumentWrapper<Integer> wrappedIntegerSlot(String slot) {
		try {
			Integer t = SlotArgument.slot().parse(new StringReader(slot));
			return new ArgumentWrapper<>(t, slot);
		} catch (CommandSyntaxException var2) {
			throw new RuntimeException(var2.getMessage());
		}
	}

	public static @NotNull List<Integer> getSlots(@NotNull FactoryJsonObject data) {
		List<Integer> slotsList = new ArrayList<>();
		if (data.isPresent("slots")) {
			for (FactoryElement element : data.getJsonArray("slots").asList) {
				ArgumentWrapper<Integer> wrapped = wrappedIntegerSlot(element.getString());
				slotsList.add(wrapped.get());
			}
		}

		if (data.isPresent("slot")) {
			ArgumentWrapper<Integer> wrapped = wrappedIntegerSlot(data.getString("slot"));
			slotsList.add(wrapped.get());
		}

		if (slotsList.isEmpty()) {
			fillMissingNumbers(slotsList, 0, 40);
		}

		return slotsList;
	}

	public static int checkInventory(@NotNull FactoryJsonObject data, Entity entity, @Nullable me.dueris.originspaper.factory.powers.apoli.Inventory inventoryPower, Function<net.minecraft.world.item.ItemStack, Integer> processor) {
		FactoryJsonObject itemCondition = data.getJsonObject("item_condition");
		List<Integer> slots = getSlots(data);
		if (slots.isEmpty()) {
			fillMissingNumbers(slots, 0, 40);
		}

		int matches = 0;
		slots.removeIf(slotx -> slotNotWithinBounds(entity, inventoryPower, slotx));

		for (int slot : slots) {
			SlotAccess stackReference = getStackReference(entity, inventoryPower, slot);
			ItemStack stack = stackReference.get();
			if (itemCondition == null && !stack.isEmpty()
				|| (itemCondition == null || ConditionExecutor.testItem(itemCondition, stack.getBukkitStack())) && !stack.getBukkitStack().getType().isAir()) {
				matches += processor.apply(stack);
			}
		}

		return matches;
	}

	public static boolean slotNotWithinBounds(@NotNull Entity entity, @Nullable me.dueris.originspaper.factory.powers.apoli.Inventory inventoryPower, int slot) {
		return entity.getSlot(slot) == SlotAccess.NULL;
	}

	public static @NotNull SlotAccess getStackReference(@NotNull Entity entity, @Nullable me.dueris.originspaper.factory.powers.apoli.Inventory inventoryPower, int slot) {
		return entity.getSlot(slot);
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull SlotAccess createStackReference(final ItemStack startingStack) {
		return new SlotAccess() {
			ItemStack stack = startingStack;

			public ItemStack get() {
				return this.stack;
			}

			public boolean set(ItemStack stack) {
				this.stack = stack;
				return true;
			}
		};
	}

	public static void throwItem(Entity thrower, net.minecraft.world.item.@NotNull ItemStack itemStack, boolean throwRandomly, boolean retainOwnership) {

		if (itemStack.isEmpty()) {
			return;
		}

		if (thrower instanceof Player playerEntity && playerEntity.level().isClientSide) {
			playerEntity.swing(InteractionHand.MAIN_HAND);
		}

		double yOffset = thrower.getEyeY() - 0.30000001192092896D;
		ItemEntity itemEntity = new ItemEntity(thrower.level(), thrower.getX(), yOffset, thrower.getZ(), itemStack);
		itemEntity.setPickUpDelay(40);

		Random random = new Random();

		float f;
		float g;

		if (retainOwnership) itemEntity.setThrower(thrower);
		if (throwRandomly) {
			f = random.nextFloat() * 0.5F;
			g = random.nextFloat() * 6.2831855F;
			itemEntity.setDeltaMovement(-Mth.sin(g) * f, 0.20000000298023224D, Mth.cos(g) * f);
		} else {
			f = 0.3F;
			g = Mth.sin(thrower.getXRot() * 0.017453292F);
			float h = Mth.cos(thrower.getXRot() * 0.017453292F);
			float i = Mth.sin(thrower.getYRot() * 0.017453292F);
			float j = Mth.cos(thrower.getYRot() * 0.017453292F);
			float k = random.nextFloat() * 6.2831855F;
			float l = 0.02F * random.nextFloat();
			itemEntity.setDeltaMovement(
				(double) (-i * h * f) + Math.cos(k) * (double) l,
				(-g * f + 0.1F + (random.nextFloat() - random.nextFloat()) * 0.1F),
				(double) (j * h * f) + Math.sin(k) * (double) l
			);
		}

		thrower.level().addFreshEntity(itemEntity);

	}

	public static void modifyInventory(FactoryJsonObject data, Entity entity, me.dueris.originspaper.factory.powers.apoli.Inventory inventoryPower, Function<net.minecraft.world.item.ItemStack, Integer> processor, int limit) {
		if (limit <= 0) {
			limit = Integer.MAX_VALUE;
		}

		List<Integer> slots = getSlots(data);
		FactoryJsonObject entityAction = data.getJsonObject("entity_action");
		FactoryJsonObject itemCondition = data.getJsonObject("item_condition");
		FactoryJsonObject itemAction = data.getJsonObject("item_action");
		int processedItems = 0;
		slots.removeIf(slot -> slotNotWithinBounds(entity, inventoryPower, slot));

		modifyingItemsLoop:
		for (int slot : slots) {

			SlotAccess stack = getStackReference(entity, inventoryPower, slot);
			if (!(itemCondition == null || ConditionExecutor.testItem(itemCondition, stack.get().getBukkitStack()))) {
				continue;
			}

			int amount = processor.apply(stack.get());
			for (int i = 0; i < amount; i++) {

				if (entityAction != null && !entityAction.isEmpty()) {
					Actions.executeEntity(entity.getBukkitEntity(), entityAction);
				}

				Actions.executeItem(stack.get().getBukkitStack(), entity.getBukkitEntity().getWorld(), itemAction);
				++processedItems;

				if (processedItems >= limit) {
					break modifyingItemsLoop;
				}

			}

		}

	}

	public static void replaceInventory(FactoryJsonObject data, Entity entity, me.dueris.originspaper.factory.powers.apoli.Inventory inventoryPower) {
		List<Integer> slots = getSlots(data);
		FactoryJsonObject entityAction = data.getJsonObject("entity_action");
		FactoryJsonObject itemCondition = data.getJsonObject("item_condition");
		FactoryJsonObject itemAction = data.getJsonObject("item_action");
		ItemStack replacementStack = CraftItemStack.asNMSCopy(data.getItemStack("stack"));
		boolean mergeNbt = data.getBooleanOrDefault("merge_nbt", false);
		slots.removeIf(slotx -> slotNotWithinBounds(entity, inventoryPower, slotx));

		for (int slot : slots) {
			SlotAccess stackReference = getStackReference(entity, inventoryPower, slot);
			net.minecraft.world.item.ItemStack stack = stackReference.get();

			if (!(itemCondition == null || ConditionExecutor.testItem(itemCondition, stack.getBukkitStack()))) {
				continue;
			}

			if (entityAction != null && !entityAction.isEmpty()) {
				Actions.executeEntity(entity.getBukkitEntity(), entityAction);
			}

			net.minecraft.world.item.ItemStack stackAfterReplacement = replacementStack.copy();
			if (mergeNbt) {
				CompoundTag orgNbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).getUnsafe();
				CustomData.update(DataComponents.CUSTOM_DATA, stackAfterReplacement, repNbt -> repNbt.merge(orgNbt));
			}

			stackReference.set(stackAfterReplacement);
			if (itemAction != null && !itemAction.isEmpty()) {
				Actions.executeItem(stack.getBukkitStack(), entity.getBukkitEntity().getWorld(), itemAction);
			}

		}

	}

	public static void dropInventory(FactoryJsonObject data, Entity entity, me.dueris.originspaper.factory.powers.apoli.Inventory inventoryPower) {
		List<Integer> slots = getSlots(data);
		if (slots.isEmpty()) {
			fillMissingNumbers(slots, 0, 40);
		}

		int amount = data.getNumberOrDefault("amount", 0).getInt();
		boolean throwRandomly = data.getBooleanOrDefault("throw_randomly", false);
		boolean retainOwnership = data.getBooleanOrDefault("retain_ownership", true);
		FactoryJsonObject entityAction = data.getJsonObject("entity_action");
		FactoryJsonObject itemCondition = data.getJsonObject("item_condition");
		FactoryJsonObject itemAction = data.getJsonObject("item_action");
		slots.removeIf(slotx -> slotNotWithinBounds(entity, inventoryPower, slotx));

		for (int slot : slots) {
			SlotAccess stack = getStackReference(entity, inventoryPower, slot);
			if (!(itemCondition == null || ConditionExecutor.testItem(itemCondition, stack.get().getBukkitStack()))) {
				continue;
			}

			if (entityAction != null && !entityAction.isEmpty()) {
				Actions.executeEntity(entity.getBukkitEntity(), entityAction);
			}

			if (itemAction != null && !itemAction.isEmpty()) {
				Actions.executeItem(stack.get().getBukkitStack(), entity.getBukkitEntity().getWorld(), itemAction);
			}

			net.minecraft.world.item.ItemStack newStack = stack.get();
			net.minecraft.world.item.ItemStack droppedStack = net.minecraft.world.item.ItemStack.EMPTY;
			if (amount != 0) {
				int newAmount = amount < 0 ? amount * -1 : amount;
				droppedStack = newStack.split(newAmount);
			}

			throwItem(entity, droppedStack.isEmpty() ? stack.get() : droppedStack, throwRandomly, retainOwnership);
			stack.set(droppedStack.isEmpty() ? net.minecraft.world.item.ItemStack.EMPTY : newStack);

		}

	}

	public enum Calculation {
		SUM {
			@Override
			public int queryLevel(ItemStack stack, Holder<Enchantment> enchantmentEntry, boolean useModifications, int totalLevel) {
				return stack.getEnchantments().getLevel(enchantmentEntry);
			}
		},
		MAX {
			@Override
			public int queryLevel(ItemStack stack, Holder<Enchantment> enchantmentEntry, boolean useModifications, int totalLevel) {
				int potentialLevel = stack.getEnchantments().getLevel(enchantmentEntry);
				return potentialLevel >= totalLevel ? potentialLevel : 0;
			}
		};

		public int queryTotalLevel(LivingEntity entity, @NotNull Holder<Enchantment> enchantmentEntry, boolean useModifications) {
			Enchantment enchantment = enchantmentEntry.value();
			int totalLevel = 0;

			for (ItemStack stack : enchantment.getSlotItems(entity).values()) {
				totalLevel += this.queryLevel(stack, enchantmentEntry, useModifications, totalLevel);
			}

			return totalLevel;
		}

		public abstract int queryLevel(ItemStack var1, Holder<Enchantment> var2, boolean var3, int var4);
	}

	public enum OS {
		LINUX("linux"),
		SOLARIS("solaris"),
		WINDOWS("windows") {
			@Override
			protected String[] getOpenUrlArguments(@NotNull URL url) {
				return new String[]{"rundll32", "url.dll,FileProtocolHandler", url.toString()};
			}
		},
		OSX("mac") {
			@Override
			protected String[] getOpenUrlArguments(@NotNull URL url) {
				return new String[]{"open", url.toString()};
			}
		},
		UNKNOWN("unknown");

		private final String telemetryName;

		OS(final String name) {
			this.telemetryName = name;
		}

		public void openUrl(URL url) {
			throw new IllegalStateException("This method is not useful on dedicated servers.");
		}

		public void openUri(@NotNull URI uri) {
			try {
				this.openUrl(uri.toURL());
			} catch (MalformedURLException var3) {
				Util.LOGGER.error("Couldn't open uri '{}'", uri, var3);
			}
		}

		public void openFile(@NotNull File file) {
			try {
				this.openUrl(file.toURI().toURL());
			} catch (MalformedURLException var3) {
				Util.LOGGER.error("Couldn't open file '{}'", file, var3);
			}
		}

		protected String[] getOpenUrlArguments(@NotNull URL url) {
			String string = url.toString();
			if ("file".equals(url.getProtocol())) {
				string = string.replace("file:", "file://");
			}

			return new String[]{"xdg-open", string};
		}

		public void openUri(String uri) {
			try {
				this.openUrl(new URI(uri).toURL());
			} catch (IllegalArgumentException | URISyntaxException | MalformedURLException var3) {
				Util.LOGGER.error("Couldn't open uri '{}'", uri, var3);
			}
		}

		public String telemetryName() {
			return this.telemetryName;
		}
	}

	public enum ProcessMode {
		STACKS(stack -> 1),
		ITEMS(ItemStack::getCount);

		private final Function<ItemStack, Integer> processor;

		ProcessMode(Function<ItemStack, Integer> processor) {
			this.processor = processor;
		}

		public Function<ItemStack, Integer> getProcessor() {
			return this.processor;
		}
	}

	public static class ParserUtils {
		private static final Field JSON_READER_POS = net.minecraft.Util.make(() -> {
			try {
				Field field = JsonReader.class.getDeclaredField("pos");
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException var11) {
				throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", var11);
			}
		});
		private static final Field JSON_READER_LINESTART = net.minecraft.Util.make(() -> {
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

			T var4;
			try {
				JsonElement jsonElement = Streams.parse(jsonReader);
				var4 = getOrThrow(codec.parse(JsonOps.INSTANCE, jsonElement), JsonParseException::new);
			} catch (StackOverflowError var81) {
				throw new JsonParseException(var81);
			} finally {
				stringReader.setCursor(stringReader.getCursor() + getPos(jsonReader));
			}

			return var4;
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
