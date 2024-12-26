package io.github.dueris.originspaper.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.DataResult;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.access.ThrownEnderianPearlEntity;
import io.github.dueris.originspaper.condition.context.BlockConditionContext;
import it.unimi.dsi.fastutil.ints.IntCollection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Util {
	public static MinecraftServer server = OriginsPaper.server;
	public static Logger LOGGER = LogManager.getLogger("OriginsPaper");

	public static boolean inSnow(Level world, BlockPos... blockPositions) {
		return Arrays.stream(blockPositions)
			.anyMatch(blockPos -> {
				Biome biome = world.getBiome(blockPos).value();
				return biome.getPrecipitationAt(blockPos) == Biome.Precipitation.SNOW
					&& isRainingAndExposed(world, blockPos);
			});
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

	public static boolean shouldOverride(InteractionResult oldResult, @NotNull InteractionResult newResult) {
		return (newResult.consumesAction() && !oldResult.consumesAction())
			|| (newResult.shouldSwing() && !oldResult.shouldSwing());
	}

	@Nullable
	public static Entity getEntityByUuid(@Nullable UUID uuid, @Nullable MinecraftServer server) {

		if (uuid == null || server == null) {
			return null;
		}

		Entity entity;
		for (ServerLevel serverWorld : server.getAllLevels()) {

			if ((entity = serverWorld.getEntity(uuid)) != null) {
				return entity;
			}

		}

		return null;

	}

	@Nullable
	public static ExplosionDamageCalculator createExplosionBehavior(@Nullable Predicate<BlockConditionContext> indestructibleCondition, float resistance) {
		return indestructibleCondition == null ? null : new ExplosionDamageCalculator() {

			@Override
			public @NotNull Optional<Float> getBlockExplosionResistance(@NotNull Explosion explosion, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull BlockState blockState, @NotNull FluidState fluidState) {

				Optional<Float> defaultValue = super.getBlockExplosionResistance(explosion, world, pos, blockState, fluidState);
				Optional<Float> newValue = indestructibleCondition.test(new BlockConditionContext((Level) world, pos))
					? Optional.of(resistance)
					: Optional.empty();

				return defaultValue
					.flatMap(defVal -> newValue
						.map(newVal -> defVal > newVal ? defVal : newVal));

			}

			@Override
			public boolean shouldBlockExplode(@NotNull Explosion explosion, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull BlockState state, float power) {
				return !indestructibleCondition.test(new BlockConditionContext((Level) world, pos));
			}

		};
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

	public static Optional<Entity> getEntityWithPassengersSafe(Level world, EntityType<?> entityType, @Nullable CompoundTag entityNbt, Vec3 pos, float yaw, float pitch) {
		return getEntityWithPassengersSafe(world, entityType, entityNbt, pos, Optional.of(yaw), Optional.of(pitch));
	}

	public static Optional<Entity> getEntityWithPassengersSafe(Level world, EntityType<?> entityType, @Nullable CompoundTag entityNbt, Vec3 pos, Optional<Float> yaw, Optional<Float> pitch) {
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

		if (entityToSpawn instanceof ThrownEnderpearl thrownEnderpearl && type.equalsIgnoreCase("origins:enderian_pearl")) {
			((ThrownEnderianPearlEntity) thrownEnderpearl).originspaper$setEnderianPearl();
		}

		return Optional.of(entityToSpawn);

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

	public static void runOnAllMatchingBlocks(@NotNull Chunk chunk, @Nullable BiPredicate<Level, BlockPos> condition, @NotNull Consumer<BlockPos> posConsumer) {
		ServerLevel world = ((CraftWorld) chunk.getWorld()).getHandle();
		int minY = world.getMinBuildHeight();
		int maxY = world.getMaxBuildHeight();
		int cXF = chunk.getX() * 16;
		int cZF = chunk.getZ() * 16;

		for (int x = 0; x < 16; x++) {
			for (int y = minY; y < maxY; y++) {
				for (int z = 0; z < 16; z++) {
					BlockPos position = new BlockPos(cXF + x, y, cZF + z);
					if (condition == null || condition.test(world, position)) {
						posConsumer.accept(position);
					}
				}
			}
		}

	}

	public static <R extends Recipe<?>> @NotNull DataResult<R> validateRecipe(@NotNull R recipe) {
		return DataResult.success(recipe);
	}

	public static <R extends Recipe<?>> DataResult<CraftingRecipe> validateCraftingRecipe(@NotNull R recipe) {
		return validateRecipe(recipe).flatMap(r -> r instanceof CraftingRecipe craftingRecipe
			? DataResult.success(craftingRecipe)
			: DataResult.error(() -> "Recipe is not a crafting recipe!"));
	}

	public static Function<SerializableData.Instance, DataResult<SerializableData.Instance>> validateAnyFieldsPresent(String... fields) {
		return data -> {

			if (anyPresent(data, fields)) {
				return DataResult.success(data);
			} else {

				StringBuilder message = new StringBuilder(fields.length > 1 ? "Any of the " : "The ");
				String separator = "";

				for (int i = 0; i < fields.length; i++) {

					String field = fields[i];
					message
						.append(separator)
						.append("'").append(field).append("'");

					separator = i == fields.length - 2
						? ", and "
						: ", ";

				}

				message
					.append(" field").append(fields.length > 1 ? "s" : "")
					.append(" must be defined!");

				return DataResult.error(message::toString);

			}

		};
	}

	public static Function<SerializableData.Instance, DataResult<SerializableData.Instance>> validateAllFieldsPresent(String... fields) {
		return data -> {

			if (allPresent(data, fields)) {
				return DataResult.success(data);
			}

			else {

				StringBuilder message = new StringBuilder(fields.length > 1 ? "All of " : "The ");
				String separator = "";

				for (int i = 0; i < fields.length; i++) {

					String field = fields[i];
					message
						.append(separator)
						.append("'").append(field).append("'");

					separator = i == fields.length - 2
						? ", and "
						: ", ";

				}

				message
					.append(" field").append(fields.length > 1 ? "s" : "")
					.append(" must be defined!");

				return DataResult.error(message::toString);

			}

		};
	}

	public static boolean hasSpaceInInventory(@NotNull Player player, ItemStack stack) {
		return getSpaceInInventory(player, stack).isPresent();
	}

	public static boolean hasSpaceInInventory(Inventory playerInventory, ItemStack stack) {
		return getSpaceInInventory(playerInventory, stack).isPresent();
	}

	public static OptionalInt getSpaceInInventory(@NotNull Player player, ItemStack stack) {
		return getSpaceInInventory(player.getInventory(), stack);
	}

	public static OptionalInt getSpaceInInventory(@NotNull Inventory playerInventory, ItemStack stack) {

		int slot = playerInventory.getSlotWithRemainingSpace(stack);
		if (slot == -1) {
			slot = playerInventory.getFreeSlot();
		}

		return slot == -1
			? OptionalInt.empty()
			: OptionalInt.of(slot);

	}

	public static <E, C extends Collection<E>> @NotNull BinaryOperator<C> mergeCollections() {
		return (coll1, coll2) -> {
			coll1.addAll(coll2);
			return coll1;
		};
	}

	@Nullable
	public static <T> List<T> singletonListOrNull(@Nullable T value) {
		return mapOr(value, List::of, () -> null);
	}

	public static <T> List<T> singletonListOrEmpty(@Nullable T value) {
		return mapOr(value, List::of, List::of);
	}

	public static <T, U> U mapOr(@Nullable T value, Function<T, U> mapper, Supplier<U> defaultValue) {
		return Optional.ofNullable(value)
			.map(mapper)
			.orElseGet(defaultValue);
	}

	public static Set<Integer> toSlotIdSet(Collection<SlotRange> slotRanges) {
		return slotRanges
			.stream()
			.map(SlotRange::slots)
			.map(IntCollection::intStream)
			.flatMap(IntStream::boxed)
			.collect(Collectors.toSet());
	}

	public static @NotNull Collection<? extends Entity> getEntities(CommandContext<io.papermc.paper.command.brigadier.CommandSourceStack> context, String name) throws CommandSyntaxException {
		Collection<? extends Entity> collection = getOptionalEntities(context, name);

		if (collection.isEmpty()) {
			throw EntityArgument.NO_ENTITIES_FOUND.create();
		} else {
			return collection;
		}
	}

	public static @NotNull Collection<? extends Entity> getOptionalEntities(@NotNull CommandContext<io.papermc.paper.command.brigadier.CommandSourceStack> context, String name) throws CommandSyntaxException {
		return context.getArgument(name, EntitySelector.class).findEntities((CommandSourceStack) context.getSource());
	}

	public static @NotNull Entity getEntity(@NotNull CommandContext<io.papermc.paper.command.brigadier.CommandSourceStack> context, String name) throws CommandSyntaxException {
		return context.getArgument(name, EntitySelector.class).findSingleEntity((net.minecraft.commands.CommandSourceStack) context.getSource());
	}

	public static ScoreHolder getName(CommandContext<io.papermc.paper.command.brigadier.CommandSourceStack> context, String name) throws CommandSyntaxException {
		return getNames(context, name).iterator().next();
	}

	public static @NotNull Collection<ScoreHolder> getNames(CommandContext<io.papermc.paper.command.brigadier.CommandSourceStack> context, String name) throws CommandSyntaxException {
		return getNames(context, name, Collections::emptyList);
	}

	public static @NotNull Collection<ScoreHolder> getNames(@NotNull CommandContext<io.papermc.paper.command.brigadier.CommandSourceStack> context, String name, Supplier<Collection<ScoreHolder>> players) throws CommandSyntaxException {
		Collection<ScoreHolder> collection = context.getArgument(name, ScoreHolderArgument.Result.class).getNames((CommandSourceStack) context.getSource(), players);
		if (collection.isEmpty()) {
			throw EntityArgument.NO_ENTITIES_FOUND.create();
		} else {
			return collection;
		}
	}

	public static @NotNull Objective getObjective(@NotNull CommandContext<io.papermc.paper.command.brigadier.CommandSourceStack> context, String name) throws CommandSyntaxException {
		String string = context.getArgument(name, String.class);
		Scoreboard scoreboard = ((CommandSourceStack) context.getSource()).getServer().getScoreboard();
		Objective objective = scoreboard.getObjective(string);
		if (objective == null) {
			throw new DynamicCommandExceptionType(
				comName -> Component.translatableEscape("arguments.objective.notFound", comName)
			).create(string);
		} else {
			return objective;
		}
	}

	public static ResourceLocation getId(@NotNull CommandContext<io.papermc.paper.command.brigadier.CommandSourceStack> context, String name) {
		return context.getArgument(name, ResourceLocation.class);
	}

	public static @NotNull Collection<ServerPlayer> getPlayers(@NotNull CommandContext<io.papermc.paper.command.brigadier.CommandSourceStack> context, String name) throws CommandSyntaxException {
		List<ServerPlayer> list = context.getArgument(name, EntitySelector.class).findPlayers((CommandSourceStack) context.getSource());

		if (list.isEmpty()) {
			throw EntityArgument.NO_PLAYERS_FOUND.create();
		} else {
			return list;
		}
	}

	public static @NotNull ServerPlayer getPlayer(@NotNull CommandContext<io.papermc.paper.command.brigadier.CommandSourceStack> context, String name) throws CommandSyntaxException {
		return context.getArgument(name, EntitySelector.class).findSinglePlayer((CommandSourceStack) context.getSource());
	}

	public static @NotNull ChunkPos bukkitChunk2ChunkPos(@NotNull Chunk chunk) {
		return new ChunkPos(chunk.getX(), chunk.getZ());
	}
}
