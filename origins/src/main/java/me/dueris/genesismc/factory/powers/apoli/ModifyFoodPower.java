package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.apoli.AttributeHandler.getOperationMappingsDouble;
import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_food;

public class ModifyFoodPower extends CraftPower implements Listener {

    private static final Map<Material, Double> foodModifiers = new HashMap<>();
    private static final Map<Material, Double> saturationModifiers = new HashMap<>();

    static {
        foodModifiers.put(Material.APPLE, 1.0);
        foodModifiers.put(Material.COOKIE, 1.0);
        foodModifiers.put(Material.BAKED_POTATO, 1.0);
        foodModifiers.put(Material.BEETROOT, 1.0);
        foodModifiers.put(Material.BREAD, 1.0);
        foodModifiers.put(Material.CARROT, 1.0);
        foodModifiers.put(Material.CHICKEN, 1.0);
        foodModifiers.put(Material.COD, 1.0);
        foodModifiers.put(Material.COOKED_CHICKEN, 1.0);
        foodModifiers.put(Material.COOKED_COD, 1.0);
        foodModifiers.put(Material.COOKED_MUTTON, 1.0);
        foodModifiers.put(Material.COOKED_PORKCHOP, 1.0);
        foodModifiers.put(Material.COOKED_RABBIT, 1.0);
        foodModifiers.put(Material.COOKED_SALMON, 1.0);
        foodModifiers.put(Material.COOKED_BEEF, 1.0);
        foodModifiers.put(Material.COOKED_CHICKEN, 1.0);
        foodModifiers.put(Material.COOKED_COD, 1.0);
        foodModifiers.put(Material.COOKED_MUTTON, 1.0);
        foodModifiers.put(Material.COOKED_PORKCHOP, 1.0);
        foodModifiers.put(Material.COOKED_RABBIT, 1.0);
        foodModifiers.put(Material.COOKED_SALMON, 1.0);
        foodModifiers.put(Material.COOKED_BEEF, 1.0);
        foodModifiers.put(Material.COOKED_CHICKEN, 1.0);
        foodModifiers.put(Material.COOKED_COD, 1.0);
        foodModifiers.put(Material.COOKED_MUTTON, 1.0);
        foodModifiers.put(Material.COOKED_PORKCHOP, 1.0);
        foodModifiers.put(Material.COOKED_RABBIT, 1.0);
        foodModifiers.put(Material.COOKED_SALMON, 1.0);
        foodModifiers.put(Material.COOKED_BEEF, 1.0);
        foodModifiers.put(Material.GOLDEN_APPLE, 1.2);
        foodModifiers.put(Material.ENCHANTED_GOLDEN_APPLE, 1.2);
        foodModifiers.put(Material.MELON_SLICE, 1.0);
        foodModifiers.put(Material.MUSHROOM_STEW, 1.0);
        foodModifiers.put(Material.POISONOUS_POTATO, 1.0);
        foodModifiers.put(Material.PORKCHOP, 1.0);
        foodModifiers.put(Material.POTATO, 1.0);
        foodModifiers.put(Material.PUFFERFISH, 1.0);
        foodModifiers.put(Material.RABBIT, 1.0);
        foodModifiers.put(Material.BEEF, 1.0);
        foodModifiers.put(Material.CHICKEN, 1.0);
        foodModifiers.put(Material.COD, 1.0);
        foodModifiers.put(Material.MUTTON, 1.0);
        foodModifiers.put(Material.PORKCHOP, 1.0);
        foodModifiers.put(Material.RABBIT, 1.0);
        foodModifiers.put(Material.SALMON, 1.0);
        foodModifiers.put(Material.ROTTEN_FLESH, 1.0);
        foodModifiers.put(Material.SPIDER_EYE, 1.0);
        foodModifiers.put(Material.SUSPICIOUS_STEW, 1.0);

        saturationModifiers.put(Material.APPLE, 1.0);
        saturationModifiers.put(Material.COOKIE, 1.0);
        saturationModifiers.put(Material.BAKED_POTATO, 0.6);
        saturationModifiers.put(Material.BEETROOT, 0.6);
        saturationModifiers.put(Material.BREAD, 0.6);
        saturationModifiers.put(Material.CARROT, 0.6);
        saturationModifiers.put(Material.CHICKEN, 0.6);
        saturationModifiers.put(Material.COD, 0.6);
        saturationModifiers.put(Material.COOKED_CHICKEN, 0.6);
        saturationModifiers.put(Material.COOKED_COD, 0.6);
        saturationModifiers.put(Material.COOKED_MUTTON, 0.6);
        saturationModifiers.put(Material.COOKED_PORKCHOP, 0.6);
        saturationModifiers.put(Material.COOKED_RABBIT, 0.6);
        saturationModifiers.put(Material.COOKED_SALMON, 0.6);
        saturationModifiers.put(Material.COOKED_BEEF, 0.6);
        saturationModifiers.put(Material.COOKED_CHICKEN, 0.6);
        saturationModifiers.put(Material.COOKED_COD, 0.6);
        saturationModifiers.put(Material.COOKED_MUTTON, 0.6);
        saturationModifiers.put(Material.COOKED_PORKCHOP, 0.6);
        saturationModifiers.put(Material.COOKED_RABBIT, 0.6);
        saturationModifiers.put(Material.COOKED_SALMON, 0.6);
        saturationModifiers.put(Material.COOKED_BEEF, 0.6);
        saturationModifiers.put(Material.COOKED_CHICKEN, 0.6);
        saturationModifiers.put(Material.COOKED_COD, 0.6);
        saturationModifiers.put(Material.COOKED_MUTTON, 0.6);
        saturationModifiers.put(Material.COOKED_PORKCHOP, 0.6);
        saturationModifiers.put(Material.COOKED_RABBIT, 0.6);
        saturationModifiers.put(Material.COOKED_SALMON, 0.6);
        saturationModifiers.put(Material.COOKED_BEEF, 0.6);
        saturationModifiers.put(Material.GOLDEN_APPLE, 1.2);
        saturationModifiers.put(Material.ENCHANTED_GOLDEN_APPLE, 1.2);
        saturationModifiers.put(Material.MELON_SLICE, 1.0);
        saturationModifiers.put(Material.MUSHROOM_STEW, 0.6);
        saturationModifiers.put(Material.POISONOUS_POTATO, 0.6);
        saturationModifiers.put(Material.PORKCHOP, 0.8);
        saturationModifiers.put(Material.POTATO, 0.6);
        saturationModifiers.put(Material.PUFFERFISH, 0.6);
        saturationModifiers.put(Material.RABBIT, 0.6);
        saturationModifiers.put(Material.BEEF, 0.8);
        saturationModifiers.put(Material.CHICKEN, 0.6);
        saturationModifiers.put(Material.COD, 0.6);
        saturationModifiers.put(Material.MUTTON, 0.6);
        saturationModifiers.put(Material.PORKCHOP, 0.8);
        saturationModifiers.put(Material.RABBIT, 0.6);
        saturationModifiers.put(Material.SALMON, 0.6);
        saturationModifiers.put(Material.ROTTEN_FLESH, 0.8);
        saturationModifiers.put(Material.SPIDER_EYE, 0.8);
        saturationModifiers.put(Material.SUSPICIOUS_STEW, 0.6);
    }

    public static double getFoodModifier(Material material) {
        return foodModifiers.getOrDefault(material, 1.0);
    }

    public static double getSaturationModifier(Material material) {
        return saturationModifiers.getOrDefault(material, 1.0);
    }

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    @EventHandler
    public void saturationorwhateverRUN(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            if (modify_food.contains(player)) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                    if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) player) && ConditionExecutor.testItem(power.get("item_condition"), e.getItem())) {
                        if (modify_food.contains(player)) {
                            for (JSONObject jsonObject : power.getJsonListSingularPlural("food_modifier", "food_modifiers")) {
                                if (jsonObject.containsKey("value")) {
                                    int val = Integer.parseInt(jsonObject.get("value").toString());
                                    String operation = jsonObject.get("operation").toString();
                                    BinaryOperator mathOperator = getOperationMappingsDouble().get(operation);
                                    if (mathOperator != null) {
                                        double finalValue = (double) mathOperator.apply(getFoodModifier(e.getItem().getType()), (double) val);
                                        player.setFoodLevel(Integer.parseInt(String.valueOf(Math.round(player.getFoodLevel() + finalValue))));
                                        setActive(player, power.getTag(), true);
                                    }
                                }
                            }
                            for (JSONObject jsonObject : power.getJsonListSingularPlural("saturation_modifier", "saturation_modifiers")) {
                                if (jsonObject.containsKey("value")) {
                                    int val = Integer.parseInt(jsonObject.get("value").toString());
                                    String operation = jsonObject.get("operation").toString();
                                    BinaryOperator mathOperator = getOperationMappingsDouble().get(operation);
                                    if (mathOperator != null) {
                                        double finalValue = (double) mathOperator.apply(getSaturationModifier(e.getItem().getType()), (double) val);
                                        player.setSaturation(Math.round(player.getFoodLevel() + finalValue));
                                        setActive(player, power.getTag(), true);
                                    }
                                }
                            }
                        }
                    } else {
                        setActive(player, power.getTag(), false);
                    }
                }
            }
        }
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "apoli:modify_food";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_food;
    }
}
