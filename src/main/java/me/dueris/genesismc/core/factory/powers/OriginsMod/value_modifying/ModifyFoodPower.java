package me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_food;

public class ModifyFoodPower implements Listener {
    @EventHandler
    public void saturationorwhateverRUN(PlayerItemConsumeEvent e){
        Player player = e.getPlayer();
        for(OriginContainer origin : OriginPlayer.getOrigin(player).values()){
            if(ConditionExecutor.check("item_condition", "item_condition", player, origin, "origins:modify_food", null, player)) {
                if (modify_food.contains(player)) {
                    if (origin.getPowerFileFromType("origins:modify_food").getJsonHashMap("food_modifier") != null) {
                        Map.Entry<Double, Double> modifiers = getModifiers(player, origin);

                        double modifiedFoodLevel = player.getFoodLevel() * modifiers.getKey();
                        modifiedFoodLevel = Math.min(modifiedFoodLevel, 20.0);

                        player.setFoodLevel((int) modifiedFoodLevel);
                    }
                    if (origin.getPowerFileFromType("origins:modify_food").getJsonHashMap("saturation_modifier") != null) {
                        Map.Entry<Double, Double> modifiers = getModifiers(player, origin);

                        double modifiedSaturation = player.getSaturation() * modifiers.getValue();
                        modifiedSaturation = Math.min(modifiedSaturation, 20.0);

                        player.setSaturation((float) modifiedSaturation);
                    }
                }
            }
        }
    }

    private Map.Entry<Double, Double> getModifiers(Player player, OriginContainer origin) {
        for(HashMap<String, Object> modifier : origin.getPowerFileFromType("origins:modify_food").getPossibleModifiers("modifier", "modifiers")){
            float value = Float.valueOf(modifier.get("value").toString());
            String operation = modifier.get("operation").toString();
            BinaryOperator<Float> mathOperator = getOperationMappingsFloat().get(operation);

            double foodModifier = (double) (mathOperator != null ? mathOperator.apply(Float.valueOf(player.getFoodLevel()), value) : player.getFoodLevel());
            double saturationModifier = (double) (mathOperator != null ? mathOperator.apply(player.getSaturation(), value) : player.getSaturation());

            return new AbstractMap.SimpleEntry<>(foodModifier, saturationModifier);
        }
        return null;
    }

    private static final Map<Material, Double> foodModifiers = new HashMap<>();
    private static final Map<Material, Double> saturationModifiers = new HashMap<>();

    public static double getFoodModifier(Material material) {
        return foodModifiers.getOrDefault(material, 1.0);
    }

    public static double getSaturationModifier(Material material) {
        return saturationModifiers.getOrDefault(material, 1.0);
    }

    static {
        foodModifiers.put(Material.APPLE, 1.0);
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

}
