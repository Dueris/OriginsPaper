package io.github.dueris.originspaper.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.jeff_media.morepersistentdatatypes.DataType;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.Error;
import com.mojang.serialization.JsonOps;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.util.ArgumentWrapper;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.power.factory.PowerType;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.damage.CraftDamageSource;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Util {
	private static final List<SlotRange> SLOTS = net.minecraft.Util.make(new LinkedList<>(), list -> {
		addSingleSlot(list, "contents", 0);
		addSlotRange(list, "container.", 0, 54);
		addSlotRange(list, "hotbar.", 0, 9);
		addSlotRange(list, "inventory.", 9, 27);
		addSlotRange(list, "enderchest.", 200, 27);
		addSlotRange(list, "villager.", 300, 8);
		addSlotRange(list, "horse.", 500, 15);
		int i = EquipmentSlot.MAINHAND.getIndex(98);
		int j = EquipmentSlot.OFFHAND.getIndex(98);
		addSingleSlot(list, "weapon", i);
		addSingleSlot(list, "weapon.mainhand", i);
		addSingleSlot(list, "weapon.offhand", j);
		addSlots(list, "weapon.*", i, j);
		i = EquipmentSlot.HEAD.getIndex(100);
		j = EquipmentSlot.CHEST.getIndex(100);
		int m = EquipmentSlot.LEGS.getIndex(100);
		int n = EquipmentSlot.FEET.getIndex(100);
		int o = EquipmentSlot.BODY.getIndex(105);
		addSingleSlot(list, "armor.head", i);
		addSingleSlot(list, "armor.chest", j);
		addSingleSlot(list, "armor.legs", m);
		addSingleSlot(list, "armor.feet", n);
		addSingleSlot(list, "armor.body", o);
		addSlots(list, "armor.*", i, j, m, n, o);
		addSingleSlot(list, "horse.saddle", 400);
		addSingleSlot(list, "horse.chest", 499);
		addSingleSlot(list, "player.cursor", 499);
		addSlotRange(list, "player.crafting.", 500, 4);
	});
	private static final List<String> EXEMPT_SLOTS = List.of("weapon", "weapon.mainhand");
	private static final NamespacedKey LIMBO_LOCATION_KEY = new NamespacedKey("apoli", "in_limbo");
	public static MinecraftServer server = OriginsPaper.server;
	public static Logger LOGGER = LogManager.getLogger("OriginsPaper");

	public static DamageSource getDamageSource(DamageType type) {
		return getDamageSource(type, null);
	}

	public static DamageSource getDamageSource(@NotNull DamageType type, Entity attacker) {
		DamageSource source = null;

		Registry<DamageType> registry = CraftRegistry.getMinecraftRegistry().registryOrThrow(Registries.DAMAGE_TYPE);
		for (ResourceKey<DamageType> dkey : registry.registryKeySet()) {
			if (dkey == null) continue;
			if (registry.get(dkey) == type) {
				source = new DamageSource(registry.getHolderOrThrow(dkey), attacker, attacker, attacker == null ? null : attacker.position());
				break;
			}
		}

		return source;
	}

	public static DamageSource damageSourceFromBukkit(org.bukkit.damage.DamageSource bukkit) {
		return ((CraftDamageSource) bukkit).getHandle();
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

	public static String getNameOrTag(@NotNull PowerType power) {
		return PlainTextComponentSerializer.plainText().serialize(power.name()).equalsIgnoreCase(("power.$namespace.$path.name")
			.replace("$namespace", power.getId().getNamespace()).replace("$path", power.getId().getPath())) ? power.getTag() : PlainTextComponentSerializer.plainText().serialize(power.name());
	}

	public static String getNameOrTag(@NotNull Origin origin) {
		return PlainTextComponentSerializer.plainText().serialize(origin.getName()).equalsIgnoreCase(("origin.$namespace.$path.name")
			.replace("$namespace", origin.getId().getNamespace()).replace("$path", origin.getId().getPath())) ? origin.getTag() : PlainTextComponentSerializer.plainText().serialize(origin.getName());
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
		if (fluidHeightMap.isPresent()) {
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
			return submergedSet.isPresent() && submergedSet.get().contains(tag);
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

	@SuppressWarnings("unchecked")
	protected static Optional<Object2DoubleMap<TagKey<Fluid>>> getFluidHeightMap(Entity entity) {
		try {
			return Optional.of(Reflector.accessField("fluidHeight", Entity.class, entity, Object2DoubleMap.class));
		} catch (Exception var2) {
			var2.printStackTrace();
			return Optional.empty();
		}
	}

	@SuppressWarnings("unchecked")
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

	@Unmodifiable
	public static <T> @NotNull List<T> collapseCollection(@NotNull Collection<List<T>> collection) {
		List<T> lC = new LinkedList<>();
		collection.forEach(lC::addAll);
		return Collections.unmodifiableList(lC);
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

	public static boolean allPresent(SerializableData.Instance data, String @NotNull ... fieldNames) {

		for (String field : fieldNames) {

			if (!data.isPresent(field)) {
				return false;
			}

		}

		return true;

	}

	public static boolean anyPresent(SerializableData.Instance data, String @NotNull ... fields) {

		for (String field : fields) {

			if (data.isPresent(field)) {
				return true;
			}

		}

		return false;

	}

	public static @NotNull JsonArray toJsonStringArray(@NotNull List<String> strings) {
		JsonArray array = new JsonArray();

		for (String s : strings) {
			array.add(s);
		}

		return array;
	}

	public static void createExplosion(Level world, Vec3 pos, float power, boolean createFire, Explosion.BlockInteraction destructionType, ExplosionDamageCalculator behavior) {
		createExplosion(world, null, pos, power, createFire, destructionType, behavior);
	}

	public static void createExplosion(Level world, Entity entity, @NotNull Vec3 pos, float power, boolean createFire, Explosion.BlockInteraction destructionType, ExplosionDamageCalculator behavior) {
		createExplosion(world, entity, null, pos.x(), pos.y(), pos.z(), power, createFire, destructionType, behavior);
	}

	public static void createExplosion(Level world, @Nullable Entity entity, @Nullable DamageSource damageSource, double x, double y, double z, float power, boolean createFire, Explosion.BlockInteraction destructionType, ExplosionDamageCalculator behavior) {

		Explosion explosion = new Explosion(world, entity, damageSource, behavior, x, y, z, power, createFire, destructionType, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE);

		explosion.explode();
		explosion.finalizeExplosion(world.isClientSide);

		//  Sync the explosion effect to the client if the explosion is created on the server
		if (!(world instanceof ServerLevel serverWorld)) {
			return;
		}

		if (!explosion.interactsWithBlocks()) {
			explosion.clearToBlow();
		}

		for (ServerPlayer serverPlayerEntity : serverWorld.players()) {
			if (serverPlayerEntity.distanceToSqr(x, y, z) < 4096.0) {
				serverPlayerEntity.connection.send(new ClientboundExplodePacket(x, y, z, power, explosion.getToBlow(), explosion.getHitPlayers().get(serverPlayerEntity), explosion.getBlockInteraction(), explosion.getSmallExplosionParticles(), explosion.getLargeExplosionParticles(), explosion.getExplosionSound()));
			}
		}

	}

	@Nullable
	public static ExplosionDamageCalculator getExplosionBehavior(Level world, float indestructibleResistance, @Nullable Predicate<BlockInWorld> indestructibleCondition) {
		return indestructibleCondition == null ? null : new ExplosionDamageCalculator() {

			@Override
			public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter blockView, BlockPos pos, BlockState blockState, FluidState fluidState) {

				BlockInWorld cachedBlockPosition = new BlockInWorld(world, pos, true);

				Optional<Float> defaultValue = super.getBlockExplosionResistance(explosion, world, pos, blockState, fluidState);
				Optional<Float> newValue = indestructibleCondition.test(cachedBlockPosition) ? Optional.of(indestructibleResistance) : Optional.empty();

				return defaultValue.isPresent() ? (newValue.isPresent() ? (defaultValue.get() > newValue.get() ? (defaultValue) : newValue) : defaultValue) : defaultValue;

			}

			@Override
			public boolean shouldBlockExplode(Explosion explosion, BlockGetter blockView, BlockPos pos, BlockState state, float power) {
				return !indestructibleCondition.test(new BlockInWorld(world, pos, true));
			}

		};
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

		String type = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString();
		entityToSpawnNbt.putString("id", type.equalsIgnoreCase("origins:enderian_pearl") ? "minecraft:ender_pearl" : type);
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

	public static float range(float min, float max, float val) {
		return val < min ? min : Math.min(val, max);
	}

	public static long factorial(int n) {
		if (n < 0) throw new IllegalArgumentException("n must be non-negative");
		return (n == 0) ? 1 : n * factorial(n - 1);
	}

	public static int[] convertToIntArray(@NotNull Collection<Integer> integers) {
		return integers.stream().mapToInt(Integer::intValue).toArray();
	}

	public static <T extends Enum<T>> @NotNull HashMap<String, T> buildEnumMap(@NotNull Class<T> enumClass, Function<T, String> enumToString) {
		HashMap<String, T> map = new HashMap<>();
		for (T enumConstant : enumClass.getEnumConstants()) {
			map.put(enumToString.apply(enumConstant), enumConstant);
		}
		return map;
	}

	public static String readResource(String resourcePath) {
		InputStream inputStream = Util.class.getResourceAsStream(resourcePath);
		if (inputStream == null) {
			throw new IllegalArgumentException("Resource not found: " + resourcePath);
		}

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			return reader.lines().collect(Collectors.joining(System.lineSeparator()));
		} catch (Exception e) {
			throw new RuntimeException("Failed to read resource: " + resourcePath, e);
		}
	}

	public static <T> Predicate<T> combineOr(Predicate<T> a, Predicate<T> b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}
		return a.or(b);
	}

	public static <T> Predicate<T> combineAnd(Predicate<T> a, Predicate<T> b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}
		return a.and(b);
	}

	public static void setLimboUntil(Entity entity, boolean blindness, @Nullable String notification, Future<?> future, Consumer<Entity> entityConsumer) {
		Scheduler.INSTANCE.repeating(new Scheduler.Task() {
			@Override
			public void accept(MinecraftServer minecraftServer) {
				if (future.isDone()) {
					try {
						clearLimbo(entity, blindness);
						entityConsumer.accept(entity);
					} catch (Exception e) {
						throw new RuntimeException(e);
					} finally {
						cancel();
					}
				} else {
					tickLimbo(entity, blindness, notification);
				}
			}
		}, 1, 1);
	}

	public static void tickLimbo(@NotNull Entity entity, boolean blindness, @Nullable String notification) {
		CraftEntity craftEntity = entity.getBukkitEntity();
		if (!craftEntity.getPersistentDataContainer().has(LIMBO_LOCATION_KEY)) {
			craftEntity.getPersistentDataContainer().set(LIMBO_LOCATION_KEY, DataType.LOCATION, craftEntity.getLocation());
		}

		if (blindness && entity instanceof LivingEntity livingEntity && !livingEntity.hasEffect(MobEffects.BLINDNESS)) {
			// Realistically it shouldn't need anything more than Integer.MAX_VALUE...
			livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, Integer.MAX_VALUE, 0, false, false, false));
		}

		if (notification != null && entity instanceof ServerPlayer player) {
			ClientboundSetActionBarTextPacket packet = new ClientboundSetActionBarTextPacket(Component.literal(notification));
			player.connection.send(packet);
		}

		craftEntity.teleport(new Location(craftEntity.getWorld(), 0, 500000, 0));
	}

	public static void clearLimbo(@NotNull Entity entity, boolean blindness) {
		CraftEntity craftEntity = entity.getBukkitEntity();
		if (craftEntity.getPersistentDataContainer().has(LIMBO_LOCATION_KEY)) {
			Location location = craftEntity.getPersistentDataContainer().get(LIMBO_LOCATION_KEY, DataType.LOCATION);
			craftEntity.teleport(location); // dont do async, we need this NOW.
			craftEntity.getPersistentDataContainer().remove(LIMBO_LOCATION_KEY);
		}

		if (blindness && entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(MobEffects.BLINDNESS)) {
			livingEntity.removeEffect(MobEffects.BLINDNESS);
		}
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

	public static @NotNull Set<Integer> getSlots(@NotNull SerializableData.Instance data) {

		Set<Integer> slots = new HashSet<>();

		data.<ArgumentWrapper<Integer>>ifPresent("slot", iaw -> slots.add(iaw.get()));
		data.<List<ArgumentWrapper<Integer>>>ifPresent("slots", iaws -> slots.addAll(iaws.stream().map(ArgumentWrapper::get).toList()));

		if (slots.isEmpty()) {
			slots.addAll(getAllSlots());
		}

		return slots;

	}

	public static List<Integer> getAllSlots() {
		return SLOTS
			.stream()
			.flatMapToInt(slotRange -> slotRange.slots().intStream())
			.boxed()
			.toList();
	}

	private static void addSlots(@NotNull List<SlotRange> list, String name, int... slots) {
		list.add(create(name, slots));
	}

	private static void addSingleSlot(@NotNull List<SlotRange> list, String name, int slotId) {
		list.add(create(name, slotId));
	}

	@Contract("_, _ -> new")
	private static @NotNull SlotRange create(String name, int slotId) {
		return SlotRange.of(name, IntLists.singleton(slotId));
	}

	@Contract("_, _ -> new")
	private static @NotNull SlotRange create(String name, IntList slotIds) {
		return SlotRange.of(name, IntLists.unmodifiable(slotIds));
	}

	@Contract("_, _ -> new")
	private static @NotNull SlotRange create(String name, int... slotIds) {
		return SlotRange.of(name, IntList.of(slotIds));
	}

	private static void addSlotRange(List<SlotRange> list, String baseName, int firstSlotId, int lastSlotId) {
		IntList intList = new IntArrayList(lastSlotId);

		for (int i = 0; i < lastSlotId; i++) {
			int j = firstSlotId + i;
			list.add(create(baseName + i, j));
			intList.add(j);
		}

		list.add(create(baseName + "*", intList));
	}

	public static SlotAccess getStackReferenceFromStack(Entity entity, ItemStack stack) {
		return getStackReferenceFromStack(entity, stack, (provStack, refStack) -> provStack == refStack);
	}

	public static SlotAccess getStackReferenceFromStack(Entity entity, ItemStack stack, BiPredicate<ItemStack, ItemStack> equalityPredicate) {

		int slotToSkip = getDuplicatedSlotIndex(entity);
		for (int slot : getAllSlots()) {

			if (slot == slotToSkip) {
				slotToSkip = Integer.MIN_VALUE;
				continue;
			}

			SlotAccess stackReference = entity.getSlot(slot);
			if (stackReference != SlotAccess.NULL && equalityPredicate.test(stack, stackReference.get())) {
				return stackReference;
			}

		}

		return SlotAccess.NULL;

	}

	private static int getDuplicatedSlotIndex(Entity entity) {
		SlotRange slotRange = entity instanceof Player player
			? SlotRanges.nameToIds("hotbar." + player.getInventory().selected)
			: null;

		return slotRange != null
			? slotRange.slots().getFirst()
			: Integer.MIN_VALUE;

	}

	private static void deduplicateSlots(Entity entity, Set<Integer> slots) {

		int selectedHotbarSlot = getDuplicatedSlotIndex(entity);
		if (selectedHotbarSlot != Integer.MIN_VALUE && slots.contains(selectedHotbarSlot)) {
			SLOTS
				.stream()
				.filter(sr -> EXEMPT_SLOTS.contains(sr.getSerializedName()))
				.flatMapToInt(sr -> sr.slots().intStream())
				.forEach(slots::remove);
		}

	}

	public static int checkInventory(@NotNull SerializableData.Instance data, Entity entity, Function<ItemStack, Integer> processor) {

		Predicate<Tuple<Level, ItemStack>> itemCondition = data.get("item_condition");
		Set<Integer> slots = getSlots(data);
		deduplicateSlots(entity, slots);

		int matches = 0;
		slots.removeIf(slot -> slotNotWithinBounds(entity, slot));

		for (int slot : slots) {

			SlotAccess stackReference = getStackReference(entity, slot);
			ItemStack stack = stackReference.get();

			if ((itemCondition == null && !stack.isEmpty()) || (itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), stack)))) {
				matches += processor.apply(stack);
			}

		}

		return matches;

	}

	public static boolean slotNotWithinBounds(@NotNull Entity entity, int slot) {
		return entity.getSlot(slot) == SlotAccess.NULL;
	}

	public static @NotNull SlotAccess getStackReference(@NotNull Entity entity, int slot) {
		return entity.getSlot(slot);
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull SlotAccess createStackReference(final ItemStack startingStack) {
		return new SlotAccess() {
			ItemStack stack = startingStack;

			public @NotNull ItemStack get() {
				return this.stack;
			}

			public boolean set(@NotNull ItemStack stack) {
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

	public static void modifyInventory(SerializableData.Instance data, Entity entity, Function<ItemStack, Integer> processor, int limit) {

		if (limit <= 0) {
			limit = Integer.MAX_VALUE;
		}

		Set<Integer> slots = getSlots(data);
		deduplicateSlots(entity, slots);

		Consumer<Entity> entityAction = data.get("entity_action");
		Predicate<Tuple<Level, ItemStack>> itemCondition = data.get("item_condition");
		ActionTypeFactory<Tuple<ServerLevel, org.bukkit.inventory.ItemStack>> itemAction = data.get("item_action");

		int processedItems = 0;
		slots.removeIf(slot -> slotNotWithinBounds(entity, slot));

		modifyingItemsLoop:
		for (int slot : slots) {

			SlotAccess stack = getStackReference(entity, slot);
			if (!(itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), stack.get())))) {
				continue;
			}

			int amount = processor.apply(stack.get());
			for (int i = 0; i < amount; i++) {

				if (entityAction != null) {
					entityAction.accept(entity);
				}

				itemAction.accept(new Tuple<>(((ServerLevel) entity.level()), stack.get().getBukkitStack()));
				++processedItems;

				if (processedItems >= limit) {
					break modifyingItemsLoop;
				}

			}

		}

	}

	public static void replaceInventory(SerializableData.Instance data, Entity entity) {

		Set<Integer> slots = getSlots(data);
		deduplicateSlots(entity, slots);

		Consumer<Entity> entityAction = data.get("entity_action");
		Predicate<Tuple<Level, ItemStack>> itemCondition = data.get("item_condition");
		Consumer<Tuple<Level, SlotAccess>> itemAction = data.get("item_action");

		ItemStack replacementStack = data.get("stack");
		boolean mergeNbt = data.getBoolean("merge_nbt");

		slots.removeIf(slot -> slotNotWithinBounds(entity, slot));
		for (int slot : slots) {

			SlotAccess stackReference = getStackReference(entity, slot);
			ItemStack stack = stackReference.get();

			if (!(itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), stack)))) {
				continue;
			}

			if (entityAction != null) {
				entityAction.accept(entity);
			}

			ItemStack stackAfterReplacement = replacementStack.copy();
			if (mergeNbt) {
				CompoundTag orgNbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).getUnsafe();
				CustomData.update(DataComponents.CUSTOM_DATA, stackAfterReplacement, repNbt -> repNbt.merge(orgNbt));
			}

			stackReference.set(stackAfterReplacement);
			if (itemAction != null) {
				itemAction.accept(new Tuple<>(entity.level(), stackReference));
			}

		}

	}

	public static void dropInventory(SerializableData.Instance data, Entity entity) {

		Set<Integer> slots = getSlots(data);
		deduplicateSlots(entity, slots);

		int amount = data.getInt("amount");
		boolean throwRandomly = data.getBoolean("throw_randomly");
		boolean retainOwnership = data.getBoolean("retain_ownership");

		Consumer<Entity> entityAction = data.get("entity_action");
		Predicate<Tuple<Level, ItemStack>> itemCondition = data.get("item_condition");
		Consumer<Tuple<Level, SlotAccess>> itemAction = data.get("item_action");

		slots.removeIf(slot -> slotNotWithinBounds(entity, slot));
		for (int slot : slots) {

			SlotAccess stack = getStackReference(entity, slot);
			if (stack.get().isEmpty() || !(itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), stack.get())))) {
				continue;
			}

			if (entityAction != null) {
				entityAction.accept(entity);
			}

			if (itemAction != null) {
				itemAction.accept(new Tuple<>(entity.level(), stack));
			}

			ItemStack newStack = stack.get();
			ItemStack droppedStack = ItemStack.EMPTY;
			if (amount != 0) {
				int newAmount = amount < 0 ? amount * -1 : amount;
				droppedStack = newStack.split(newAmount);
			}

			throwItem(entity, droppedStack.isEmpty() ? stack.get() : droppedStack, throwRandomly, retainOwnership);
			stack.set(droppedStack.isEmpty() ? ItemStack.EMPTY : newStack);

		}

	}

	@SuppressWarnings("javax.imageio.IIOException")
	public static BufferedImage downloadImage(String imageUrl, String savePath, String fileName) throws IOException {
		URL url = new URL(imageUrl);
		BufferedImage image = ImageIO.read(url);
		File outputDir = new File(savePath);
		outputDir.mkdirs();

		File outputFile = new File(savePath, fileName + ".png");
		if (outputFile.exists()) {
			Files.delete(outputFile.toPath());
		}
		ImageIO.write(image, "png", outputFile);

		return image;
	}


	public static void saveImage(BufferedImage image, String savePath, String fileName) throws IOException {
		File outputDir = new File(savePath);
		outputDir.mkdirs();

		File outputFile = new File(savePath, fileName);
		ImageIO.write(image, "png", outputFile);
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
