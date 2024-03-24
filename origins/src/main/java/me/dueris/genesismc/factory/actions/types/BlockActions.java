package me.dueris.genesismc.factory.actions.types;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.data.types.DestructionType;
import me.dueris.genesismc.factory.data.types.ExplosionMask;
import me.dueris.genesismc.factory.data.types.ResourceOperation;
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
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.Collection;
import java.util.Optional;
import java.util.Random;

import static me.dueris.genesismc.factory.actions.Actions.BlockActionType;

public class BlockActions {

    public static void runBlock(Location location, JSONObject action) {
        if (action == null || action.isEmpty()) return;
        String type = action.get("type").toString();

        if (type.equals("apoli:add_block")) {
            if (action.containsKey("block")) {
                Material block;
                block = Material.getMaterial(action.get("block").toString().split(":")[1].toUpperCase());
                if (block == null) return;

                //i experimented with it, and it seemed that it just set it one block above?
                //still unsure about this one tho
                location.add(0d, 1d, 0d);
                location.getWorld().getBlockAt(location).setType(block);
            }
        }
        if (type.equals("apoli:offset")) {
            BlockActionType(location.add(Double.valueOf(action.getOrDefault("x", "0").toString()), Double.valueOf(action.getOrDefault("y", "0").toString()), Double.valueOf(action.getOrDefault("z", "0").toString())), (JSONObject) action.get("action"));
        }
        if (type.equals("genesis:grow_sculk")) {
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

        }
        if (type.equals("apoli:bonemeal")) {
            Block block = location.getWorld().getBlockAt(location);
            block.applyBoneMeal(BlockFace.UP);
        }
        if (type.equals("apoli:explode")) {
            long explosionPower = 1L;
            if (action.get("power") instanceof Long lep) {
                explosionPower = lep;
            } else if (action.get("power") instanceof Double dep) {
                explosionPower = Math.round(dep);
            }
            String destruction_type = "break";
            boolean create_fire = false;
            ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();

            if (action.containsKey("destruction_type"))
                destruction_type = action.get("destruction_type").toString();
            if (action.containsKey("create_fire"))
                create_fire = Boolean.parseBoolean(action.get("create_fire").toString());

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
        }
        if (type.equals("apoli:set_block")) {
            location.getBlock().setType(Material.valueOf(action.get("block").toString().split(":")[1].toUpperCase()));
        }
        if (type.equals("apoli:modify_block_state")) {
            ServerLevel level = ((CraftWorld) location.getBlock().getWorld()).getHandle();
            BlockState state = level.getBlockState(CraftLocation.toBlockPosition(location));
            Collection<Property<?>> properties = state.getProperties();
            String desiredPropertyName = action.get("property").toString();
            Property<?> property = null;
            for (Property<?> p : properties) {
                if (p.getName().equals(desiredPropertyName)) {
                    property = p;
                    break;
                }
            }
            if (property != null) {
                if ((boolean) action.getOrDefault("cycle", false)) {
                    level.setBlockAndUpdate(CraftLocation.toBlockPosition(location), state.cycle(property));
                } else {
                    Object value = state.getValue(property);
                    if (action.containsKey("enum") && value instanceof Enum) {
                        modifyEnumState(level, CraftLocation.toBlockPosition(location), state, property, action.get("enum").toString());
                    } else if (action.containsKey("value") && value instanceof Boolean) {
                        level.setBlockAndUpdate(CraftLocation.toBlockPosition(location), state.setValue((Property<Boolean>) property, (boolean) action.get("value")));
                    } else if (action.containsKey("operation") && action.containsKey("change") && value instanceof Integer) {
                        ResourceOperation op = action.get("operation").toString().toUpperCase().equals("ADD") ? ResourceOperation.ADD : ResourceOperation.SET;
                        int opValue = (int) action.get("change");
                        int newValue = (int) value;
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
        }
    }

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
}
