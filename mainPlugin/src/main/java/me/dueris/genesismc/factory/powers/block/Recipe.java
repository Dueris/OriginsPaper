package me.dueris.genesismc.factory.powers.block;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Recipe extends CraftPower implements Listener {

    private static void loadRecipe(Player player, OriginContainer origin, String powerfile) {
        if (recipe.contains(player)) {
            for (PowerContainer power : origin.getMultiPowerFileFromType(powerfile)) {
                if (power.getRecipe().get("type").toString() == "minecraft:crafting_shapeless") {
                    //uses shapeless recipe
                    ShapelessRecipe SHAPELESS_RECIPE = new ShapelessRecipe(NamespacedKey.minecraft(power.getRecipe().get("id").toString()), new ItemStack(Material.valueOf(power.getRecipeResult().get("item").toString().toUpperCase().split(":")[1])));
                    SHAPELESS_RECIPE.setCategory(CraftingBookCategory.MISC);
                    for (String ingredString : power.getRecipeIngredients()) {
                        SHAPELESS_RECIPE.addIngredient(Material.valueOf(ingredString.toUpperCase().split(":")[1]));
                    }
                    Bukkit.addRecipe(SHAPELESS_RECIPE);
                } else {
                    //uses shaped recipe
                    ShapedRecipe SHAPED_RECIPE = new ShapedRecipe(NamespacedKey.minecraft(power.getRecipe().get("id").toString()), new ItemStack(Material.valueOf(power.getRecipeResult().get("item").toString().toUpperCase().split(":")[1])));
                    SHAPED_RECIPE.setCategory(CraftingBookCategory.MISC);
                    PowerContainer powerContainer = power;
                    HashMap<String, Object> recipeData = powerContainer.getRecipe();

                    if (!recipeData.isEmpty()) {
                        String recipeId = (String) recipeData.get("id");

                        ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(GenesisMC.getPlugin(), recipeId), getResultItem(powerContainer));
                        shapedRecipe.shape(getPatternLines(powerContainer));

                        HashMap<String, Object> keyMap = powerContainer.getKey();
                        for (String key : keyMap.keySet()) {
                            String keyChar = key;
                            String keyItem = (String) ((HashMap<String, Object>) keyMap.get(keyChar)).get("item");
                            shapedRecipe.setIngredient(keyChar.charAt(0), Material.valueOf(keyItem));
                        }

                        Bukkit.addRecipe(shapedRecipe);
                    }
                }
            }

        }
    }

    private static ItemStack getResultItem(PowerContainer powerContainer) {
        String resultItemString = (String) powerContainer.getRecipeResult().get("item");
        String resultMaterialName = resultItemString.toUpperCase().split(":")[1];
        Material resultMaterial = Material.valueOf(resultMaterialName);
        return new ItemStack(resultMaterial);
    }

    private static String[] getPatternLines(PowerContainer powerContainer) {
        List<String> patternLines = powerContainer.getPatternLine();
        return patternLines.toArray(new String[0]);
    }

//    @EventHandler
//    public void load(ServerLoadEvent e) {
//        for (Player player : Bukkit.getOnlinePlayers()) {
//            for (OriginContainer origin : OriginPlayer.getOrigin(player).values()) {
//                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
//                if (conditionExecutor.check("condition", "conditions", player, origin, getPowerFile(), player, null, null, null, player.getInventory().getItemInHand(), null)) {
//                    if (!getPowerArray().contains(player)) return;
//                    setActive(power.getTag(), true);
//                    loadRecipe(player, origin, "origins:recipe");
//                } else {
//                    if (!getPowerArray().contains(player)) return;
//                    setActive(power.getTag(), false);
//                }
//            }
//        }
//    }
//
//    @EventHandler
//    public void load(PlayerJoinEvent e) {
//        for (Player player : Bukkit.getOnlinePlayers()) {
//            for (OriginContainer origin : OriginPlayer.getOrigin(player).values()) {
//                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
//                if (conditionExecutor.check("condition", "conditions", player, origin, getPowerFile(), player, null, null, null, player.getInventory().getItemInHand(), null)) {
//                    if (!getPowerArray().contains(player)) return;
//                    setActive(power.getTag(), true);
//                    loadRecipe(player, origin, "origins:recipe");
//                } else {
//                    if (!getPowerArray().contains(player)) return;
//                    setActive(power.getTag(), false);
//                }
//            }
//        }
//    }
//
//    @EventHandler
//    public void load(OriginChangeEvent e) {
//        for (Player player : Bukkit.getOnlinePlayers()) {
//            for (OriginContainer origin : OriginPlayer.getOrigin(player).values()) {
//                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
//                if (conditionExecutor.check("condition", "conditions", player, origin, getPowerFile(), player, null, null, null, player.getInventory().getItemInHand(), null)) {
//                    if (!getPowerArray().contains(player)) return;
//                    setActive(power.getTag(), true);
//                    loadRecipe(player, origin, "origins:recipe");
//                } else {
//                    if (!getPowerArray().contains(player)) return;
//                    setActive(power.getTag(), false);
//                }
//            }
//        }
//    }

    //TODO: PATCH THIS CAUSING ERRORS CHECK ORIGINS:ARACHNID

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "origins:recipe";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return recipe;
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }


}
