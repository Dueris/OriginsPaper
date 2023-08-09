package me.dueris.genesismc.core.factory.powers.OriginsMod.block;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.events.OriginChangeEvent;
import me.dueris.genesismc.core.utils.OriginContainer;
import me.dueris.genesismc.core.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.util.HashMap;
import java.util.List;

import static me.dueris.genesismc.core.factory.powers.Powers.recipe;

public class Recipe implements Listener {

    private static void loadRecipe(Player player, OriginContainer origin, String powerfile){
        if(recipe.contains(player)){
            if(origin.getPowerFileFromType(powerfile).getRecipe().get("type").toString() == "minecraft:crafting_shapeless"){
                //uses shapeless recipe
                ShapelessRecipe SHAPELESS_RECIPE = new ShapelessRecipe(NamespacedKey.minecraft(origin.getPowerFileFromType(powerfile).getRecipe().get("id").toString()), new ItemStack(Material.valueOf(origin.getPowerFileFromType(powerfile).getRecipeResult().get("item").toString().toUpperCase().split(":")[1])));
                SHAPELESS_RECIPE.setCategory(CraftingBookCategory.MISC);
                for(String ingredString : origin.getPowerFileFromType(powerfile).getRecipeIngredients()){
                    SHAPELESS_RECIPE.addIngredient(Material.valueOf(ingredString.toUpperCase().split(":")[1]));
                }
                Bukkit.addRecipe(SHAPELESS_RECIPE);
            }else{
                //uses shaped recipe
                ShapedRecipe SHAPED_RECIPE = new ShapedRecipe(NamespacedKey.minecraft(origin.getPowerFileFromType(powerfile).getRecipe().get("id").toString()), new ItemStack(Material.valueOf(origin.getPowerFileFromType(powerfile).getRecipeResult().get("item").toString().toUpperCase().split(":")[1])));
                SHAPED_RECIPE.setCategory(CraftingBookCategory.MISC);
                PowerContainer powerContainer = origin.getPowerFileFromType(powerfile);
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

    @EventHandler
    public void load(ServerLoadEvent e){

    }

    @EventHandler
    public void load(PlayerJoinEvent e){

    }

    @EventHandler
    public void load(OriginChangeEvent e){

    }
}
