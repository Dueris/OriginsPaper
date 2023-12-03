package me.dueris.genesismc.factory.powers.value_modifying;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.ErrorSystem;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_crafting;

public class ModifyCraftingPower extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    Player p;

    public ModifyCraftingPower() {
        this.p = p;
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void runD(PrepareItemCraftEvent e) {
        Player p = (Player) e.getInventory().getHolder();
        if (modify_crafting.contains(p)) {
            if (e.getRecipe() == null) return;
            if (e.getInventory().getResult() == null) return;
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
                try {
                    ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                    for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                        if (conditionExecutor.check("condition", "condition", p, power, "origins:modify_crafting", p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                            if (conditionExecutor.check("item_condition", "item_condition", p, power, "origins:modify_crafting", p, null, p.getLocation().getBlock(), null, e.getInventory().getResult(), null)) {
                                if (e.getInventory().getResult().getType() == Material.valueOf(power.get("recipe", null).split(":")[1].toUpperCase())) {
                                    e.getInventory().setResult(new ItemStack(Material.valueOf(power.getJsonHashMap("result").get("item").toString().toUpperCase().split(":")[1])));
                                    setActive(power.getTag(), true);
                                }
                            } else {
                                setActive(power.getTag(), false);
                            }
                        } else {
                            setActive(power.getTag(), false);
                        }
                    }
                } catch (Exception ev) {
                    ErrorSystem errorSystem = new ErrorSystem();
                    errorSystem.throwError("unable to get recipe or result", "origins:modify_crafting", p, origin, OriginPlayer.getLayer(p, origin));
                    ev.printStackTrace();
                }
            }
        }
    }

    public Recipe getRecipeForMaterial(Material material) {
        // Iterate through all recipes to find the one with the specified material
        for (Recipe recipe : Bukkit.getRecipesFor(new ItemStack(material))) {
            if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe) {
                return recipe;
            }
        }
        return null; // No matching recipe found
    }

    @Override
    public String getPowerFile() {
        return "origins:modify_crafting_power";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_crafting;
    }
}
