package me.dueris.originspaper.factory.actions.types;

import me.dueris.calio.data.CalioDataTypes;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.data.types.ExplosionMask;
import me.dueris.originspaper.factory.data.types.ResourceOperation;
import me.dueris.originspaper.factory.data.types.Shape;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.util.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.CraftLocation;

import java.util.Collection;
import java.util.Optional;

public class BlockActions {

	private static <T extends Comparable<T>> void modifyEnumState(ServerLevel world, BlockPos pos, BlockState originalState, Property<T> property, String value) {
		Optional<T> enumValue = property.getValue(value);
		enumValue.ifPresent(v -> world.setBlockAndUpdate(pos, originalState.setValue(property, v)));
	}

	public void register() {
		register(new ActionFactory(OriginsPaper.apoliIdentifier("explode"), (data, location) -> {
			float explosionPower = data.getNumber("power").getFloat();
			String destruction_type = "break";
			boolean create_fire = false;
			ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();

			if (data.isPresent("destruction_type"))
				destruction_type = data.getString("destruction_type");
			if (data.isPresent("create_fire"))
				create_fire = data.getBoolean("create_fire");

			Explosion explosion = new Explosion(
				level,
				null,
				level.damageSources().generic(),
				new ExplosionDamageCalculator(),
				location.getX(),
				location.getY(),
				location.getZ(),
				explosionPower,
				create_fire,
				DestructionType.parse(destruction_type).getNMS(),
				ParticleTypes.EXPLOSION,
				ParticleTypes.EXPLOSION_EMITTER,
				SoundEvents.GENERIC_EXPLODE
			);
			ExplosionMask.getExplosionMask(explosion, level).apply(data, true);
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("offset"), (data, block) -> {
			FactoryJsonObject blockAction = data.getJsonObject("action");
			Actions.executeBlock(block.offset(
				data.getNumberOrDefault("x", 0).getInt(),
				data.getNumberOrDefault("y", 0).getInt(),
				data.getNumberOrDefault("z", 0).getInt()
			).toLocation(block.getWorld()), blockAction);
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("set_block"), (data, block) -> {
			BlockState state = CraftRegistry.getMinecraftRegistry().registry(net.minecraft.core.registries.Registries.BLOCK).get().get(data.getResourceLocation("block")).defaultBlockState();
			((CraftWorld) block.getWorld()).getHandle().setBlockAndUpdate(CraftLocation.toBlockPosition(block), state);
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("add_block"), (data, block) -> {
			BlockState state = CraftRegistry.getMinecraftRegistry().registry(net.minecraft.core.registries.Registries.BLOCK).get().get(data.getResourceLocation("block")).defaultBlockState();
			((CraftWorld) block.getWorld()).getHandle().setBlockAndUpdate(CraftLocation.toBlockPosition(block), state);
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("execute_command"), (data, block) -> {
			MinecraftServer server = ((CraftWorld) block.getWorld()).getHandle().getServer();
			if (server != null) {
				String blockName = ((CraftWorld) block.getWorld()).getHandle().getBlockState(CraftLocation.toBlockPosition(block)).getBlock().getDescriptionId();
				CommandSourceStack source = new CommandSourceStack(
					OriginsPaper.showCommandOutput ? server : CommandSource.NULL,
					new Vec3(CraftLocation.toBlockPosition(block).getX() + 0.5, CraftLocation.toBlockPosition(block).getY() + 0.5, CraftLocation.toBlockPosition(block).getZ() + 0.5),
					new Vec2(0, 0),
					((CraftWorld) block.getWorld()).getHandle(),
					4,
					blockName,
					Component.translatable(blockName),
					server,
					null);
				server.getCommands().performPrefixedCommand(source, data.getString("command"));
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("area_of_effect"), (data, block) -> {
			ServerLevel world = ((CraftWorld) block.getWorld()).getHandle();
			BlockPos blockPos = CraftLocation.toBlockPosition(block);

			int radius = data.getNumberOrDefault("radius", 16).getInt();

			Shape shape = data.getEnumValueOrDefault("shape", Shape.class, Shape.CUBE);
			FactoryJsonObject blockCondition = data.getJsonObject("block_condition");
			FactoryJsonObject blockAction = data.getJsonObject("block_action");

			for (BlockPos collectedBlockPos : Shape.getPositions(blockPos, shape, radius)) {
				if (!(blockCondition == null || ConditionExecutor.testBlock(blockCondition, world.getWorld().getBlockAt(CraftLocation.toBukkit(collectedBlockPos)))))
					continue;
				if (blockAction != null && !blockAction.isEmpty()) {
					Location location = CraftLocation.toBukkit(collectedBlockPos);
					location.setWorld(world.getWorld()); // BlockPos doesnt define a level in it, we need to redefine it ourselves.
					Actions.executeBlock(location, blockAction);
				}
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("bonemeal"), (data, block) -> {
			ServerLevel world = ((CraftWorld) block.getWorld()).getHandle();
			BlockPos blockPos = CraftLocation.toBlockPosition(block);
			Direction side = Direction.UP;
			BlockPos blockPos2 = blockPos.relative(side);

			boolean spawnEffects = data.getBoolean("effects");

			if (BoneMealItem.growCrop(ItemStack.EMPTY, world, blockPos)) {
				if (spawnEffects && !world.isClientSide) {
					world.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, blockPos, 0);
				}
			} else {
				BlockState blockState = world.getBlockState(blockPos);
				boolean bl = blockState.isFaceSturdy(world, blockPos, side);
				if (bl && BoneMealItem.growWaterPlant(ItemStack.EMPTY, world, blockPos2, side)) {
					if (spawnEffects && !world.isClientSide) {
						world.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, blockPos2, 0);
					}
				}
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("modify_block_state"), (data, block) -> {
			ServerLevel world = ((CraftWorld) block.getWorld()).getHandle();
			BlockPos blockPos = CraftLocation.toBlockPosition(block);
			BlockState state = world.getBlockState(blockPos);
			Collection<Property<?>> properties = state.getProperties();
			String desiredPropertyName = data.getString("property");
			Property<?> property = null;
			for (Property<?> p : properties) {
				if (p.getName().equals(desiredPropertyName)) {
					property = p;
					break;
				}
			}
			if (property != null) {
				if (data.getBooleanOrDefault("cycle", false)) {
					world.setBlockAndUpdate(blockPos, state.cycle(property));
				} else {
					Object value = state.getValue(property);
					if (data.isPresent("enum") && value instanceof Enum) {
						modifyEnumState(world, blockPos, state, property, data.getString("enum"));
					} else if (data.isPresent("value") && value instanceof Boolean) {
						world.setBlockAndUpdate(blockPos, state.setValue((Property<Boolean>) property, data.getBoolean("value")));
					} else if (data.isPresent("operation") && data.isPresent("change") && value instanceof Integer) {
						ResourceOperation op = data.getEnumValueOrDefault("operation", ResourceOperation.class, ResourceOperation.ADD);
						int opValue = data.getNumber("change").getInt();
						int newValue = (int) value;
						switch (op) {
							case ADD -> newValue += opValue;
							case SET -> newValue = opValue;
						}
						Property<Integer> integerProperty = (Property<Integer>) property;
						if (integerProperty.getPossibleValues().contains(newValue)) {
							world.setBlockAndUpdate(blockPos, state.setValue(integerProperty, newValue));
						}
					}
				}
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("spawn_entity"), (data, block) -> {
			ServerLevel world = ((CraftWorld) block.getWorld()).getHandle();
			BlockPos pos = CraftLocation.toBlockPosition(block);

			if (!(world instanceof ServerLevel serverWorld)) {
				return;
			}
			EntityType<?> entityType = CraftRegistry.getMinecraftRegistry().registry(net.minecraft.core.registries.Registries.ENTITY_TYPE).get().get(data.getResourceLocation("entity_type"));
			CompoundTag entityNbt = data.transformWithCalio("tag", CalioDataTypes::compoundTag, new CompoundTag());

			Entity entityToSpawn = Util.getEntityWithPassengers(
				serverWorld,
				entityType,
				entityNbt,
				pos.getCenter(),
				Optional.empty(),
				Optional.empty()
			).orElse(null);

			if (entityToSpawn == null) {
				return;
			}

			serverWorld.tryAddFreshEntityWithPassengers(entityToSpawn);
			if (data.isPresent("entity_action")) {
				Actions.executeEntity(entityToSpawn.getBukkitEntity(), data.getJsonObject("entity_action"));
			}
		}));
	}

	public void register(BlockActions.ActionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BLOCK_ACTION).register(factory);
	}

}
