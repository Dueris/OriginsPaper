package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_crafting;

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
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (conditionExecutor.check("condition", "condition", p, power, "apoli:modify_crafting", p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
//                        if (conditionExecutor.check("item_condition", "item_condition", p, power, "apoli:modify_crafting", p, null, p.getLocation().getBlock(), null, e.getInventory().getResult(), null)) {
                            String currKey = RecipePower.computeTag(e.getRecipe());
                            if(currKey == null) return;
                            String provKey = power.getStringOrDefault("recipe", currKey);
                            boolean set = false;
                            if(currKey == provKey){ // Matched on crafting
                                Optional<Boolean> condition = ConditionExecutor.itemCondition.check(power.get("item_condition"), p, null, p.getLocation().getBlock(), null, e.getInventory().getResult(), null);
                                if(condition.isPresent()){
                                    if(condition.get()){
                                        set = true;
                                    }
                                }else{
                                    set = true;
                                }
                            }
                            if(set){
                                if(power.getOrDefault("result", null) != null) {
                                    e.getInventory().setResult(RecipePower.computeResult(power.get("result")));
                                }
                                Actions.EntityActionType(p, power.getAction("entity_action"));
                                Actions.ItemActionType(e.getInventory().getResult(), power.getItemAction());
                                Actions.BlockActionType(p.getLocation(), power.getBlockAction());
                            }
//                        } else {
//                            setActive(p, power.getTag(), false);
//                        }
                    } else {
                        setActive(p, power.getTag(), false);
                    }
                }
            }
        }
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
