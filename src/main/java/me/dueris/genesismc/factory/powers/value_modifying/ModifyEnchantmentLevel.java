package me.dueris.genesismc.factory.powers.value_modifying;

import static me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler.getOperationMappingsFloat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.factory.powers.*;

public class ModifyEnchantmentLevel extends CraftPower{

    @Override
    public void run(Player p) {
        if(getPowerArray().contains(p)){
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for(PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)){
                    if(conditionExecutor.check("condition", "conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getInventory().getItemInMainHand(), null)){
                        if(conditionExecutor.check("item_condition", "item_conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getInventory().getItemInMainHand(), null)){
                            ItemStack item = p.getInventory().getItemInMainHand();
                            for(HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifiers")){
                                Enchantment enchant = Enchantment.getByKey(NamespacedKey.fromString(power.get("enchantment").toString()));
                                if(item.containsEnchantment(enchant)){
                                    item.removeEnchantment(enchant);
                                }
                                int result = 1;
                                Float value = Float.valueOf(modifier.get("value").toString());
                                String operation = modifier.get("operation").toString();
                                BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                                if (mathOperator != null) {
                                    result = Math.round((float)mathOperator.apply(0, value));
                                }
                                if(result < 0){
                                    result = 1;
                                }
                                item.addEnchantment(enchant, result);
                            }
                        }
                    }
                }     
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:modify_enchantment_level";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return ValueModifyingSuperClass.modify_enchantment_level;
    }

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if(powers_active.containsKey(p)){
            if(powers_active.get(p).containsKey(tag)){
                powers_active.get(p).replace(tag, bool);
            }else{
                powers_active.get(p).put(tag, bool);
            }
        }else{
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }
    
}
