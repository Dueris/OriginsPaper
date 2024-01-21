package me.dueris.genesismc.factory.powers.value_modifying;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.ErrorSystem;
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
import java.util.HashMap;

import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_crafting;

public class ModifyCraftingPower extends CraftPower implements Listener {

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

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void runD(PrepareItemCraftEvent e) {
        Player p = (Player) e.getInventory().getHolder();
        if (modify_crafting.contains(p)) {
            if (e.getRecipe() == null) return;
            if (e.getInventory().getResult() == null) return;
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
                try {
                    ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                    for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (conditionExecutor.check("condition", "condition", p, power, "apoli:modify_crafting", p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                            if (conditionExecutor.check("item_condition", "item_condition", p, power, "apoli:modify_crafting", p, null, p.getLocation().getBlock(), null, e.getInventory().getResult(), null)) {
                                if (power.get("recipe") != null) {
                                    if (e.getInventory().getResult().getType() == Material.valueOf(power.getStringOrDefault("recipe", null).split(":")[1].toUpperCase())) {
                                        if (power.get("result") != null) {
                                            e.getInventory().setResult(new ItemStack(Material.valueOf(power.get("result").get("item").toString().toUpperCase().split(":")[1])));
                                        }
                                        setActive(p, power.getTag(), true);
                                        Actions.EntityActionType(p, power.getEntityAction());
                                        if (power.getActionOrNull("item_action_after_crafting") != null) {
                                            Actions.ItemActionType(e.getInventory().getResult(), power.getAction("item_action_after_crafting"));
                                        }
                                    }
                                } else {
                                    if (power.get("result") != null && power.get("result").get("item") != null) {
                                        e.getInventory().setResult(new ItemStack(Material.valueOf(power.get("result").get("item").toString().toUpperCase().split(":")[1])));
                                    }
                                    setActive(p, power.getTag(), true);
                                    Actions.EntityActionType(p, power.getEntityAction());
                                    if (power.getActionOrNull("item_action_after_crafting") != null) {
                                        Actions.ItemActionType(e.getInventory().getResult(), power.getAction("item_action_after_crafting"));
                                    }
                                }
                            } else {
                                setActive(p, power.getTag(), false);
                            }
                        } else {
                            setActive(p, power.getTag(), false);
                        }
                    }
                } catch (Exception ev) {
                    ErrorSystem errorSystem = new ErrorSystem();
                    errorSystem.throwError("unable to get recipe or result", "apoli:modify_crafting", p, layer);
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
        return "apoli:modify_crafting";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_crafting;
    }
}
