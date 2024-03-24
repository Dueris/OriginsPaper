package me.dueris.genesismc.factory.conditions.types;

import me.dueris.calio.registry.Registerable;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.TagRegistryParser;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.DataTypeUtils;
import me.dueris.genesismc.factory.data.types.Comparison;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Power;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiPredicate;

public class BlockConditions {
    public static HashMap<Power, ArrayList<String>> inTagValues = new HashMap<>();
    public static HashMap<String, ArrayList<Material>> blockTagMappings = new HashMap<>();

    public void prep() {
        // Meta conditions, shouldnt execute
        // Meta conditions are added in each file to ensure they dont error and skip them when running
        // a meta condition inside another meta condition
        register(new ConditionFactory(GenesisMC.apoliIdentifier("and"), (condition, obj) -> {
            throw new IllegalStateException("Executor should not be here right now! Report to Dueris!");
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("or"), (condition, obj) -> {
            throw new IllegalStateException("Executor should not be here right now! Report to Dueris!");
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("chance"), (condition, obj) -> {
            throw new IllegalStateException("Executor should not be here right now! Report to Dueris!");
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("constant"), (condition, obj) -> {
            throw new IllegalStateException("Executor should not be here right now! Report to Dueris!");
        }));
        // Meta conditions end
        register(new ConditionFactory(GenesisMC.apoliIdentifier("material"), (condition, block) -> {
            try {
                Material mat = DataTypeUtils.getMaterial(condition.get("material"));
                return block.getType().equals(mat);
            } catch (Exception e) {
                e.printStackTrace();
                //yeah imma fail this silently for some weird out of bounds error
                return false;
            }
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("in_tag"), (condition, block) -> {
            if (block == null) return false;
            if (TagRegistryParser.getRegisteredTagFromFileKey(condition.get("tag").toString()) != null) {
                if (!blockTagMappings.containsKey(condition.get("tag"))) {
                    blockTagMappings.put(condition.get("tag").toString(), new ArrayList<>());
                    for (String mat : TagRegistryParser.getRegisteredTagFromFileKey(condition.get("tag").toString())) {
                        blockTagMappings.get(condition.get("tag")).add(Material.valueOf(mat.split(":")[1].toUpperCase()));
                    }
                    return false;
                } else {
                    // mappings exist, now we can start stuff
                    return blockTagMappings.get(condition.get("tag")).contains(block.getType());
                }
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("adjacent"), (condition, block) -> {
            int adj = 0;
            for (Direction direction : Direction.values()) {
                boolean p = true;
                if (condition.containsKey("adjacent_condition")) {
                    p = ConditionExecutor.testBlock((JSONObject) condition.get("adjacent_condition"), (CraftBlock) block.getWorld().getBlockAt(CraftLocation.toBukkit(((CraftBlock) block).getPosition().offset(direction.getNormal()))));
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
            if (block != null && block.getType() != Material.AIR) {
                Block[] adjBlcs = new Block[]{
                    block.getRelative(0, 1, 0), // Up
                    block.getRelative(0, -1, 0), // Down
                    block.getRelative(0, 0, -1), // North
                    block.getRelative(0, 0, 1), // South
                    block.getRelative(-1, 0, 0), // West
                    block.getRelative(1, 0, 0)  // East
                };

                for (Block adj : adjBlcs) {
                    if (adj != null && adj.getType().isSolid()) {
                        return true;
                    }
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
        register(new ConditionFactory(GenesisMC.apoliIdentifier("block_entity"), (condition, block) -> {
            return block.getState() instanceof TileState;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("block"), (condition, block) -> {
            return block.getType().equals(Material.valueOf(condition.get("block").toString().split(":")[1].toUpperCase()));
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
        register(new ConditionFactory(GenesisMC.apoliIdentifier("exposed_to_sky"), (condition, block) -> {
            return block.getLightFromSky() > 0;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("fluid"), (condition, block) -> {
            return ConditionExecutor.testFluid((JSONObject) condition.get("fluid_condition"), block.getHandle().getFluidState(new BlockPos(block.getX(), block.getY(), block.getZ())).getType());
        }));
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
        register(new ConditionFactory(GenesisMC.apoliIdentifier("light_blocking"), (condition, block) -> {
            return !block.getType().isOccluding();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("light_level"), (condition, block) -> {
            String lightType = condition.get("light_type").toString();
            CraftBlock bl = (CraftBlock) block;
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
        register(new ConditionFactory(GenesisMC.apoliIdentifier("movement_blocking"), (condition, block) -> {
            return block.getType().isCollidable();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("replacable"), (condition, block) -> {
            return block.getType().isAir() || block.isReplaceable();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("water_loggable"), (condition, block) -> {
            return ((CraftBlock) block).getHandle().getBlockState(((CraftBlock) block).getPosition()).getBlock() instanceof LiquidBlockContainer;
        }));
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
