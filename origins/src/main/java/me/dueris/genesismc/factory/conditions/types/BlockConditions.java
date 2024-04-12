package me.dueris.genesismc.factory.conditions.types;

import me.dueris.calio.registry.Registerable;
import me.dueris.calio.util.MiscUtils;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Comparison;
import me.dueris.genesismc.factory.data.types.Shape;
import me.dueris.genesismc.factory.data.types.VectorGetter;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.json.simple.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

public class BlockConditions {
    public static HashMap<Power, ArrayList<String>> inTagValues = new HashMap<>();
    public static HashMap<String, ArrayList<Material>> blockTagMappings = new HashMap<>();

    public void prep() {
        register(new ConditionFactory(GenesisMC.apoliIdentifier("material"), (condition, block) -> {
            try {
                Material mat = MiscUtils.getBukkitMaterial(condition.get("material").toString());
                return block.getType().equals(mat);
            } catch (Exception e) {
                e.printStackTrace();
                //yeah imma fail this silently for some weird out of bounds error
                return false;
            }
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("in_tag"), (condition, block) -> {
            if (block == null || block.getNMS() == null) return false;
            NamespacedKey tag = NamespacedKey.fromString(condition.get("tag").toString());
            TagKey key = TagKey.create(net.minecraft.core.registries.Registries.BLOCK, CraftNamespacedKey.toMinecraft(tag));
            return block.getHandle().getBlockState(CraftLocation.toBlockPosition(block.getLocation())).is(key);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("adjacent"), (condition, block) -> {
            int adj = 0;
            for (Direction direction : Direction.values()) {
                boolean p = true;
                if (condition.containsKey("adjacent_condition")) {
                    p = ConditionExecutor.testBlock((JSONObject) condition.get("adjacent_condition"), (CraftBlock) block.getWorld().getBlockAt(CraftLocation.toBukkit(block.getPosition().offset(direction.getNormal()))));
                }
                if (p) {
                    adj++;
                }
            }
            String comparison = condition.get("comparison").toString();
            float compare_to = Float.parseFloat(condition.get("compare_to").toString());

            return Comparison.getFromString(comparison).compare(adj, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("attachable"), (condition, block) -> {
            ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
            for (Direction d : Direction.values()) {
                BlockPos adjacent = CraftLocation.toBlockPosition(block.getLocation()).relative(d);
                if (level.getBlockState(adjacent).isFaceSturdy(level, CraftLocation.toBlockPosition(block.getLocation()), d.getOpposite())) {
                    return true;
                }
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("blast_resistance"), (condition, block) -> {
            String comparison = condition.get("comparison").toString();
            float compare_to = Float.parseFloat(condition.get("compare_to").toString());
            float bR = block.getType().getBlastResistance();
            return Comparison.getFromString(comparison).compare(bR, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("block_entity"), (condition, block) -> block.getState() instanceof TileState));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("block"), (condition, block) -> block.getType().equals(MiscUtils.getBukkitMaterial(condition.get("block").toString()))));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("distance_from_coordinates"), (condition, block) -> {
            boolean scaleReferenceToDimension = (boolean) condition.getOrDefault("scale_reference_to_dimension", true);
            boolean setResultOnWrongDimension = condition.containsKey("result_on_wrong_dimension"), resultOnWrongDimension = setResultOnWrongDimension && (boolean) condition.get("result_on_wrong_dimension");
            double x = 0, y = 0, z = 0;
            Vec3 pos = CraftLocation.toVec3D(block.getLocation());
            ServerLevel level = block.getHandle().getMinecraftWorld();

            double currentDimensionCoordinateScale = level.dimensionType().coordinateScale();
            switch (condition.getOrDefault("reference", "world_origin").toString()) {
                case "player_natural_spawn", "world_spawn", "player_spawn" :
                    if(setResultOnWrongDimension && level.dimension() != Level.OVERWORLD)
                        return resultOnWrongDimension;
                    BlockPos spawnPos = level.getSharedSpawnPos();
                    x = spawnPos.getX();
                    y = spawnPos.getY();
                    z = spawnPos.getZ();
                    break;
                case "world_origin":
                    break;
            }

            Vec3 coords = VectorGetter.getNMSVector((JSONObject) condition.getOrDefault("coordinates", new JSONObject(Map.of("x", 0, "y", 0, "z", 0))));
            Vec3 offset = VectorGetter.getNMSVector((JSONObject) condition.getOrDefault("offset", new JSONObject(Map.of("x", 0, "y", 0, "z", 0))));
            x += coords.x + offset.x;
            y += coords.y + offset.y;
            z += coords.z + offset.z;
            if(scaleReferenceToDimension && (x != 0 || z != 0)){
                Comparison comparison = Comparison.getFromString(condition.get("comparison").toString());
                if(currentDimensionCoordinateScale == 0) return comparison == Comparison.NOT_EQUAL || comparison == Comparison.GREATER_THAN || comparison == Comparison.GREATER_THAN_OR_EQUAL;

                x /= currentDimensionCoordinateScale;
                z /= currentDimensionCoordinateScale;
            }

            double distance,
                xDistance = (boolean) condition.getOrDefault("ignore_x", false) ? 0 : Math.abs(pos.x() - x),
                yDistance = (boolean) condition.getOrDefault("ignore_y", false) ? 0 : Math.abs(pos.y() - y),
                zDistance = (boolean) condition.getOrDefault("ignore_z", false) ? 0 : Math.abs(pos.z() - z);
            if((boolean) condition.getOrDefault("scale_distance_to_dimension", false)){
                xDistance *= currentDimensionCoordinateScale;
                zDistance *= currentDimensionCoordinateScale;
            }

            distance = Shape.getDistance(Shape.getShape(condition.getOrDefault("shape", "cube")), xDistance, yDistance, zDistance);

            if(condition.containsKey("round_to_digit")){
                distance = new BigDecimal(distance).setScale((int) condition.get("round_to_digit"), RoundingMode.HALF_UP).doubleValue();
            }

            return Comparison.getFromString(condition.get("comparison").toString()).compare(distance, Utils.getToDouble(condition.get("compare_to")));
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("block_state"), (condition, block) -> {
            BlockState state = block.getNMS();
            Collection<Property<?>> properties = state.getProperties();
            String desiredPropertyName = condition.get("property").toString();
            Property<?> property = null;
            for (Property<?> p : properties) {
                if (p.getName().equals(desiredPropertyName)) {
                    property = p;
                    break;
                }
            }
            if (property != null) {
                Object value = state.getValue(property);
                if (condition.containsKey("enum") && value instanceof Enum) {
                    return ((Enum) value).name().equalsIgnoreCase(condition.get("enum").toString());
                } else if (condition.containsKey("value") && value instanceof Boolean) {
                    return value == condition.get("value");
                } else if (condition.containsKey("comparison") && condition.containsKey("compare_to") && value instanceof Integer valInt) {
                    return Comparison.getFromString(condition.get("comparison").toString()).compare(valInt, (int) condition.get("compare_to"));
                }
                return true;
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("exposed_to_sky"), (condition, block) -> block.getLightFromSky() > 0));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("fluid"), (condition, block) -> ConditionExecutor.testFluid((JSONObject) condition.get("fluid_condition"), block.getHandle().getFluidState(new BlockPos(block.getX(), block.getY(), block.getZ())).getType())));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("hardness"), (condition, block) -> {
            String comparison = condition.get("comparison").toString();
            float compare_to = Float.parseFloat(condition.get("compare_to").toString());
            float bR = block.getType().getHardness();
            return Comparison.getFromString(comparison).compare(bR, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("height"), (condition, block) -> {
            String comparison = condition.get("comparison").toString();
            float compare_to = Float.parseFloat(condition.get("compare_to").toString());
            float bR = block.getLocation().getBlockY();
            return Comparison.getFromString(comparison).compare(bR, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("light_blocking"), (condition, block) -> !block.getType().isOccluding()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("light_level"), (condition, block) -> {
            String lightType = condition.get("light_type").toString();
            CraftBlock bl = block;
            int level = 0;
            switch (lightType) {
                case "sky" -> {
                    level = bl.getLightFromSky();
                }
                case "block" -> {
                    level = bl.getLightFromBlocks();
                }
                default -> {
                    level = bl.getLightLevel();
                }
            }

            String comparison = condition.get("comparison").toString();
            float compare_to = Float.parseFloat(condition.get("compare_to").toString());
            float bR = level;
            return Comparison.getFromString(comparison).compare(bR, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("slipperiness"), (condition, block) -> {
            String comparison = condition.get("comparison").toString();
            float compare_to = Float.parseFloat(condition.get("compare_to").toString());
            return Comparison.getFromString(comparison).compare(block.getBlockData().getMaterial().getSlipperiness(), compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("movement_blocking"), (condition, block) -> block.getType().isCollidable()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("replacable"), (condition, block) -> block.getType().isAir() || block.isReplaceable()));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("water_loggable"), (condition, block) -> block.getHandle().getBlockState(block.getPosition()).getBlock() instanceof LiquidBlockContainer));
    }

    private void register(ConditionFactory factory) {
        GenesisMC.getPlugin().registry.retrieve(Registries.BLOCK_CONDITION).register(factory);
    }

    public class ConditionFactory implements Registerable {
        NamespacedKey key;
        BiPredicate<JSONObject, CraftBlock> test;

        public ConditionFactory(NamespacedKey key, BiPredicate<JSONObject, CraftBlock> test) {
            this.key = key;
            this.test = test;
        }

        public boolean test(JSONObject condition, CraftBlock tester) {
            return test.test(condition, tester);
        }

        @Override
        public NamespacedKey getKey() {
            return key;
        }
    }
}
