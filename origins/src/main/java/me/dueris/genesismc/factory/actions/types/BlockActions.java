package me.dueris.genesismc.factory.actions.types;

import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.DestructionType;
import me.dueris.genesismc.factory.data.types.ExplosionMask;
import me.dueris.genesismc.factory.data.types.ResourceOperation;
import me.dueris.genesismc.factory.data.types.Shape;
import me.dueris.genesismc.registry.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;

public class BlockActions {

	private static <T extends Comparable<T>> void modifyEnumState(ServerLevel world, BlockPos pos, BlockState originalState, Property<T> property, String value) {
		Optional<T> enumValue = property.getValue(value);
		enumValue.ifPresent(v -> world.setBlockAndUpdate(pos, originalState.setValue(property, v)));
	}

	public static void iterateAndChangeBlocks(World world, int centerX, int centerY, int centerZ,
											  Material targetMaterial1, float initialChance, float chanceDecrease,
											  Material targetMaterial2, float thresholdPercentage) {
		Random random = new Random();

		for (int radius = 0; radius < 20; radius++) {
			for (int x = centerX - radius; x <= centerX + radius; x++) {
				for (int z = centerZ - radius; z <= centerZ + radius; z++) {
					Block block = world.getHighestBlockAt(x, z);
					if (block.getType().isSolid()) {
						float chance = initialChance - radius * chanceDecrease;
						if (chance <= 0.0f) {
							break;
						}
						if (random.nextFloat() <= chance) {
							block.setType(targetMaterial1);
						}
					}
				}
			}

			int totalBlocks = (2 * radius + 1) * (2 * radius + 1);
			int changedBlocks = totalBlocks - world.getHighestBlockYAt(centerX, centerZ);

			float percentage = (float) changedBlocks / totalBlocks;

			if (percentage < thresholdPercentage) {
				for (int x = centerX - radius; x <= centerX + radius; x++) {
					for (int z = centerZ - radius; z <= centerZ + radius; z++) {
						Block block = world.getHighestBlockAt(x, z);
						block.setType(targetMaterial2);
					}
				}
				for (int x = centerX - radius; x <= centerX + radius; x++) {
					for (int z = centerZ - radius; z <= centerZ + radius; z++) {
						Block block = world.getBlockAt(x, centerY + 1, z);
						block.setType(targetMaterial2);
					}
				}
				break;
			}
		}
	}

	public void register() {
		register(new ActionFactory(GenesisMC.apoliIdentifier("add_block"), (action, location) -> {
			if (action.isPresent("block")) {
				Material block = action.getMaterial("block");
				location.getWorld().getBlockAt(location).setType(block);
			}
		}));
		register(new ActionFactory(GenesisMC.apoliIdentifier("area_of_effect"), (action, location) -> {
			ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();
			BlockPos pos = CraftLocation.toBlockPosition(location);

			int radius = action.getNumber("radius").getInt();
			Shape shape = action.getEnumValue("shape", Shape.class);
			boolean hasCondition = action.isPresent("block_condition");

			for (BlockPos blockPos : Shape.getPositions(pos, shape, radius)) {
				boolean run = true;
				if (hasCondition) {
					if (!ConditionExecutor.testBlock(action.getJsonObject("block_condition"), CraftBlock.at(level, blockPos))) {
						run = false;
					}
				}
				if (run) {
					Actions.executeBlock(new Location(location.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ()), action.getJsonObject("block_action"));
				}
			}
		}));
		register(new ActionFactory(GenesisMC.identifier("grow_sculk"), (action, location) -> {
			location.getBlock().setType(Material.SCULK_CATALYST);
			new BukkitRunnable() {
				@Override
				public void run() {
					int centerX = location.getBlockX();
					int centerY = location.getBlockY();
					int centerZ = location.getBlockZ();
					Material sculkStage1 = Material.SCULK;
					float initialChance = 0.8f;
					float chanceDecrease = 0.05f;
					Material sculkStage2 = Material.SCULK_VEIN;
					float thresholdPercentage = 0.2f;

					World world = location.getWorld();

					iterateAndChangeBlocks(world, centerX, centerY, centerX, sculkStage1, initialChance, chanceDecrease, sculkStage2, thresholdPercentage);
				}
			}.runTaskLater(GenesisMC.getPlugin(), 1);
		}));
		register(new ActionFactory(GenesisMC.apoliIdentifier("bonemeal"), (action, location) -> {
			Block block = location.getWorld().getBlockAt(location);
			block.applyBoneMeal(BlockFace.UP);
		}));
		register(new ActionFactory(GenesisMC.apoliIdentifier("explode"), (action, location) -> {
			float explosionPower = action.getNumber("power").getFloat();
			String destruction_type = "break";
			boolean create_fire = false;
			ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();

			if (action.isPresent("destruction_type"))
				destruction_type = action.getString("destruction_type");
			if (action.isPresent("create_fire"))
				create_fire = action.getBoolean("create_fire");

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
			ExplosionMask.getExplosionMask(explosion, level).apply(action, true);
		}));
		register(new ActionFactory(GenesisMC.apoliIdentifier("set_block"), (action, location) -> location.getBlock().setType(action.getMaterial("block"))));
		register(new ActionFactory(GenesisMC.apoliIdentifier("modify_block_state"), (action, location) -> {
			ServerLevel level = ((CraftWorld) location.getBlock().getWorld()).getHandle();
			BlockState state = level.getBlockState(CraftLocation.toBlockPosition(location));
			Collection<Property<?>> properties = state.getProperties();
			String desiredPropertyName = action.getString("property");
			Property<?> property = null;
			for (Property<?> p : properties) {
				if (p.getName().equals(desiredPropertyName)) {
					property = p;
					break;
				}
			}
			if (property != null) {
				if (action.getBooleanOrDefault("cycle", false)) {
					level.setBlockAndUpdate(CraftLocation.toBlockPosition(location), state.cycle(property));
				} else {
					Object value = state.getValue(property);
					if (action.isPresent("enum") && value instanceof Enum) {
						modifyEnumState(level, CraftLocation.toBlockPosition(location), state, property, action.getString("enum"));
					} else if (action.isPresent("value") && value instanceof Boolean) {
						level.setBlockAndUpdate(CraftLocation.toBlockPosition(location), state.setValue((Property<Boolean>) property, action.getBoolean("value")));
					} else if (action.isPresent("operation") && action.isPresent("change") && value instanceof Integer newValue) {
						ResourceOperation op = action.getString("operation").equalsIgnoreCase("ADD") ? ResourceOperation.ADD : ResourceOperation.SET;
						int opValue = action.getNumber("change").getInt();
						switch (op) {
							case ADD -> newValue += opValue;
							case SET -> newValue = opValue;
						}
						Property<Integer> integerProperty = (Property<Integer>) property;
						if (integerProperty.getPossibleValues().contains(newValue)) {
							level.setBlockAndUpdate(CraftLocation.toBlockPosition(location), state.setValue(integerProperty, newValue));
						}
					}
				}
			}
		}));
	}

	private void register(BlockActions.ActionFactory factory) {
		GenesisMC.getPlugin().registry.retrieve(Registries.BLOCK_ACTION).register(factory);
	}

	public static class ActionFactory implements Registrable {
		NamespacedKey key;
		BiConsumer<FactoryJsonObject, Location> test;

		public ActionFactory(NamespacedKey key, BiConsumer<FactoryJsonObject, Location> test) {
			this.key = key;
			this.test = test;
		}

		public void test(FactoryJsonObject action, Location tester) {
			if (action == null || action.isEmpty()) return; // Dont execute empty actions
			try {
				test.accept(action, tester);
			} catch (Exception e) {
				GenesisMC.getPlugin().getLogger().severe("An Error occurred while running an action: " + e.getMessage());
				e.printStackTrace();
			}
		}

		@Override
		public NamespacedKey getKey() {
			return key;
		}
	}
}
