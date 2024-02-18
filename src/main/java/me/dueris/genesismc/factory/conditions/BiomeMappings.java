package me.dueris.genesismc.factory.conditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BiomeMappings {

    public static final List<String> BEACH = Arrays.asList("minecraft:beach", "minecraft:snowy_beach");
    public static final List<String> DESERT = List.of("minecraft:desert");
    public static final List<String> EXTREME_HILLS = Arrays.asList("minecraft:windswept_forest", "minecraft:windswept_gravelly_hills", "minecraft:windswept_hills");
    public static final List<String> FOREST = Arrays.asList("minecraft:birch_forest", "minecraft:dark_forest", "minecraft:flower_forest", "minecraft:forest", "minecraft:grove", "minecraft:old_growth_birch_forest");
    public static final List<String> ICY = Arrays.asList("minecraft:ice_spikes", "minecraft:snowy_plains");
    public static final List<String> JUNGLE = Arrays.asList("minecraft:bamboo_jungle", "minecraft:jungle", "minecraft:sparse_jungle");
    public static final List<String> MESA = Arrays.asList("minecraft:badlands", "minecraft:eroded_badlands", "minecraft:wooded_badlands");
    public static final List<String> MOUNTAIN = Arrays.asList("minecraft:frozen_peaks", "minecraft:jagged_peaks", "minecraft:meadow", "minecraft:snowy_slopes", "minecraft:stony_peaks");
    public static final List<String> MUSHROOM = List.of("minecraft:mushroom_fields");
    public static final List<String> NETHER = Arrays.asList("minecraft:basalt_deltas", "minecraft:crimson_forest", "minecraft:nether_wastes", "minecraft:soul_sand_valley", "minecraft:warped_forest");
    public static final List<String> NONE = List.of("minecraft:the_void");
    public static final List<String> OCEAN = Arrays.asList("minecraft:cold_ocean", "minecraft:deep_cold_ocean", "minecraft:deep_frozen_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:deep_ocean", "minecraft:frozen_ocean", "minecraft:lukewarm_ocean", "minecraft:ocean", "minecraft:warm_ocean");
    public static final List<String> PLAINS = Arrays.asList("minecraft:plains", "minecraft:sunflower_plains");
    public static final List<String> RIVER = Arrays.asList("minecraft:frozen_river", "minecraft:river");
    public static final List<String> SAVANNA = Arrays.asList("minecraft:savanna", "minecraft:savanna_plateau", "minecraft:windswept_savanna");
    public static final List<String> SWAMP = List.of("minecraft:swamp");
    public static final List<String> TAIGA = Arrays.asList("minecraft:old_growth_pine_taiga", "minecraft:old_growth_spruce_taiga", "minecraft:snowy_taiga", "minecraft:taiga");
    public static final List<String> THE_END = Arrays.asList("minecraft:end_barrens", "minecraft:end_highlands", "minecraft:end_midlands", "minecraft:small_end_islands", "minecraft:the_end");
    public static final List<String> UNDERGROUND = Arrays.asList("minecraft:dripstone_caves", "minecraft:lush_caves");

    public static List<String> getBiomeIDs(String biomeType) {
        switch (biomeType.toLowerCase()) {
            case "beach":
                return BEACH;
            case "desert":
                return DESERT;
            case "extreme_hills":
                return EXTREME_HILLS;
            case "forest":
                return FOREST;
            case "icy":
                return ICY;
            case "jungle":
                return JUNGLE;
            case "mesa":
                return MESA;
            case "mountain":
                return MOUNTAIN;
            case "mushroom":
                return MUSHROOM;
            case "nether":
                return NETHER;
            case "none":
                return NONE;
            case "ocean":
                return OCEAN;
            case "plains":
                return PLAINS;
            case "river":
                return RIVER;
            case "savanna":
                return SAVANNA;
            case "swamp":
                return SWAMP;
            case "taiga":
                return TAIGA;
            case "the_end":
                return THE_END;
            case "underground":
                return UNDERGROUND;
            default:
                return new ArrayList<>();
        }
    }
}
