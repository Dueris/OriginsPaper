package me.dueris.genesismc.factory.conditions;

import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBiome;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.damage.DamageSource;
import org.bukkit.event.entity.EntityDamageEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.types.BiEntityConditions;
import me.dueris.genesismc.factory.conditions.types.BiomeConditions;
import me.dueris.genesismc.factory.conditions.types.BlockConditions;
import me.dueris.genesismc.factory.conditions.types.DamageConditions;
import me.dueris.genesismc.factory.conditions.types.EntityConditions;
import me.dueris.genesismc.factory.conditions.types.FluidConditions;
import me.dueris.genesismc.factory.conditions.types.ItemConditions;
import me.dueris.genesismc.registry.Registrar;
import me.dueris.genesismc.registry.Registries;

public class ConditionExecutor {
    public static BiEntityConditions biEntityCondition = new BiEntityConditions();
    public static BiomeConditions biomeCondition = new BiomeConditions();
    public static BlockConditions blockCondition = new BlockConditions();
    public static DamageConditions damageCondition = new DamageConditions();
    public static EntityConditions entityCondition = new EntityConditions();
    public static FluidConditions fluidCondition = new FluidConditions();
    public static ItemConditions itemCondition = new ItemConditions();

    public static void registerAll(){
        biEntityCondition.prep();
        biomeCondition.prep();
        blockCondition.prep();
        damageCondition.prep();
        entityCondition.prep();
        fluidCondition.prep();
        itemCondition.prep();
    }

    private static boolean isMetaCondition(JSONObject condition){
        return condition.containsKey("type") ?
            condition.get("type").toString().equals("apoli:and") ||
            condition.get("type").toString().equals("apoli:chance") ||
            condition.get("type").toString().equals("apoli:constant") ||
            condition.get("type").toString().equals("apoli:not") ||
            condition.get("type").toString().equals("apoli:or")
            : false;
    }

    private static boolean chance(JSONObject condition){
        float chance = (float) condition.get("chance");
        if(chance > 1f){
            chance = 1f;
        }
        return new Random().nextFloat(1.0f) < chance;
    }

    public static boolean testBiEntity(JSONObject condition, CraftEntity actor, CraftEntity target){
        if(condition.isEmpty()) return true; // Empty condition, do nothing
        Pair entityPair = new Pair<CraftEntity,CraftEntity>() {

            @Override
            public CraftEntity left() {
                return actor;
            }

            @Override
            public CraftEntity right() {
                return target;
            }
        };
        if(isMetaCondition(condition)){
            String type = condition.get("type").toString();
            switch(type) {
                case "apoli:and" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<BiEntityConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION);
                            BiEntityConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, testBiEntity(obj, (CraftEntity) entityPair.first(), (CraftEntity) entityPair.second())));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(!b) return false;
                    }
                    return true;
                }
                case "apoli:or" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<BiEntityConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION);
                            BiEntityConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, testBiEntity(obj, (CraftEntity) entityPair.first(), (CraftEntity) entityPair.second())));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(b) return true;
                    }
                    return false;
                }
                case "apoli:constant" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), (boolean)condition.get("value"));
                }
                case "apoli:chance" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), chance(condition));
                }
            }
        }else{
            // return the condition
            Registrar<BiEntityConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION);
            BiEntityConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.get("type").toString()));
            boolean invert = (boolean) condition.getOrDefault("inverted", false);
            if(con != null){
                return getPossibleInvert(invert, con.test(condition, entityPair));
            }else{
                return getPossibleInvert(invert, true); // Condition null or not found.
            }
        }
        return false;
    }

    public static boolean testBiome(JSONObject condition, org.bukkit.block.Biome biome){
        if(condition.isEmpty()) return true; // Empty condition, do nothing
        if(isMetaCondition(condition)){
            String type = condition.get("type").toString();
            switch(type) {
                case "apoli:and" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<BiomeConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BIOME_CONDITION);
                            BiomeConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, testBiome(obj, biome)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(!b) return false;
                    }
                    return true;
                }
                case "apoli:or" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<BiomeConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BIOME_CONDITION);
                            BiomeConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, testBiome(obj, biome)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(b) return true;
                    }
                    return false;
                }
                case "apoli:constant" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), (boolean)condition.get("value"));
                }
                case "apoli:chance" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), chance(condition));
                }
            }
        }else{
            // return the condition
            Registrar<BiomeConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BIOME_CONDITION);
            BiomeConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.get("type").toString()));
            boolean invert = (boolean) condition.getOrDefault("inverted", false);
            if(con != null){
                return getPossibleInvert(invert, con.test(condition, CraftBiome.bukkitToMinecraft(biome)));
            }else{
                return getPossibleInvert(invert, true); // Condition null or not found.
            }
        }
        return false;
    }

    @SuppressWarnings("index out of bounds")
    public static boolean testBlock(JSONObject condition, CraftBlock block){
        if(condition.isEmpty()) return true; // Empty condition, do nothing
        if(isMetaCondition(condition)){
            String type = condition.get("type").toString();
            switch(type) {
                case "apoli:and" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<BlockConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BLOCK_CONDITION);
                            BlockConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, testBlock(obj, block)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(!b) return false;
                    }
                    return true;
                }
                case "apoli:or" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<BlockConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BLOCK_CONDITION);
                            BlockConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, testBlock(obj, block)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(b) return true;
                    }
                    return false;
                }
                case "apoli:constant" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), (boolean)condition.get("value"));
                }
                case "apoli:chance" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), chance(condition));
                }
            }
        }else{
            // return the condition
            Registrar<BlockConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BLOCK_CONDITION);
            BlockConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.get("type").toString()));
            boolean invert = (boolean) condition.getOrDefault("inverted", false);
            if(con != null){
                return getPossibleInvert(invert, con.test(condition, block));
            }else{
                return getPossibleInvert(invert, true); // Condition null or not found.
            }
        }
        return false;
    }

    public static boolean testDamage(JSONObject condition, EntityDamageEvent event){
        if(condition.isEmpty()) return true; // Empty condition, do nothing
        if(isMetaCondition(condition)){
            String type = condition.get("type").toString();
            switch(type) {
                case "apoli:and" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<DamageConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.DAMAGE_CONDITION);
                            DamageConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, testDamage(obj, event)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(!b) return false;
                    }
                    return true;
                }
                case "apoli:or" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<DamageConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.DAMAGE_CONDITION);
                            DamageConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, testDamage(obj, event)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(b) return true;
                    }
                    return false;
                }
                case "apoli:constant" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), (boolean)condition.get("value"));
                }
                case "apoli:chance" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), chance(condition));
                }
            }
        }else{
            // return the condition
            Registrar<DamageConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.DAMAGE_CONDITION);
            DamageConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.get("type").toString()));
            boolean invert = (boolean) condition.getOrDefault("inverted", false);
            if(con != null){
                return getPossibleInvert(invert, con.test(condition, event));
            }else{
                return getPossibleInvert(invert, true); // Condition null or not found.
            }
        }
        return false;
    }

    public static boolean testEntity(JSONObject condition, CraftEntity entity){
        if(condition.isEmpty()) return true; // Empty condition, do nothing
        if(isMetaCondition(condition)){
            String type = condition.get("type").toString();
            switch(type) {
                case "apoli:and" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<EntityConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ENTITY_CONDITION);
                            EntityConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, testEntity(obj, entity)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(!b) return false;
                    }
                    return true;
                }
                case "apoli:or" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<EntityConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ENTITY_CONDITION);
                            EntityConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, testEntity(obj, entity)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(b) return true;
                    }
                    return false;
                }
                case "apoli:constant" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), (boolean)condition.get("value"));
                }
                case "apoli:chance" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), chance(condition));
                }
            }
        }else{
            // return the condition
            Registrar<EntityConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ENTITY_CONDITION);
            EntityConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.get("type").toString()));
            boolean invert = (boolean) condition.getOrDefault("inverted", false);
            if(con != null){
                return getPossibleInvert(invert, con.test(condition, entity));
            }else{
                return getPossibleInvert(invert, true); // Condition null or not found.
            }
        }
        return false;
    }

    public static boolean testItem(JSONObject condition, CraftItemStack itemStack){
        if(condition.isEmpty()) return true; // Empty condition, do nothing
        if(isMetaCondition(condition)){
            String type = condition.get("type").toString();
            switch(type) {
                case "apoli:and" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<ItemConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ITEM_CONDITION);
                            ItemConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, testItem(obj, itemStack)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(!b) return false;
                    }
                    return true;
                }
                case "apoli:or" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<ItemConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ITEM_CONDITION);
                            ItemConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, testItem(obj, itemStack)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(b) return true;
                    }
                    return false;
                }
                case "apoli:constant" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), (boolean)condition.get("value"));
                }
                case "apoli:chance" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), chance(condition));
                }
            }
        }else{
            // return the condition
            Registrar<ItemConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ITEM_CONDITION);
            ItemConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.get("type").toString()));
            boolean invert = (boolean) condition.getOrDefault("inverted", false);
            if(con != null){
                return getPossibleInvert(invert, con.test(condition, itemStack));
            }else{
                return getPossibleInvert(invert, true); // Condition null or not found.
            }
        }
        return false;
    }

    public static boolean testFluid(JSONObject condition, net.minecraft.world.level.material.Fluid fluid){
        if(condition.isEmpty()) return true; // Empty condition, do nothing
        if(isMetaCondition(condition)){
            String type = condition.get("type").toString();
            switch(type) {
                case "apoli:and" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<FluidConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.FLUID_CONDITION);
                            FluidConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, testFluid(obj, fluid)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(!b) return false;
                    }
                    return true;
                }
                case "apoli:or" -> {
                    JSONArray array = (JSONArray) condition.get("conditions");
                    List<Boolean> cons = new ArrayList<>();
                    array.forEach(object -> {
                        if(object instanceof JSONObject obj){
                            Registrar<FluidConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.FLUID_CONDITION);
                            FluidConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(obj.get("type").toString()));
                            boolean invert = (boolean) obj.getOrDefault("inverted", false);
                            if(con != null){
                                cons.add(getPossibleInvert(invert, testFluid(obj, fluid)));
                            }else{
                                cons.add(getPossibleInvert(invert, true)); // Condition null or not found.
                            }
                        }
                    });
                    for(boolean b : cons){
                        if(b) return true;
                    }
                    return false;
                }
                case "apoli:constant" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), (boolean)condition.get("value"));
                }
                case "apoli:chance" -> {
                    return getPossibleInvert((boolean)condition.getOrDefault("inverted", false), chance(condition));
                }
            }
        }else{
            // return the condition
            Registrar<FluidConditions.ConditionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.FLUID_CONDITION);
            FluidConditions.ConditionFactory con = factory.get(NamespacedKey.fromString(condition.get("type").toString()));
            boolean invert = (boolean) condition.getOrDefault("inverted", false);
            if(con != null){
                return getPossibleInvert(invert, con.test(condition, fluid));
            }else{
                return getPossibleInvert(invert, true); // Condition null or not found.
            }
        }
        return false;
    }

    protected static boolean getPossibleInvert(boolean inverted, boolean original){
        return inverted ? !original : original;
    }
}
