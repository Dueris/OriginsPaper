package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.builder.inst.factory.FactoryElement;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.ArrayList;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_food;

public class ModifyFoodPower extends CraftPower implements Listener {

    @EventHandler
    public void saturationorwhateverRUN(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            if (modify_food.contains(player)) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                    if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) player) && ConditionExecutor.testItem(power.getJsonObject("item_condition"), e.getItem())) {
                        if (modify_food.contains(player)) {
                            for (FactoryJsonObject jsonObject : power.getList$SingularPlural("food_modifier", "food_modifiers").stream().map(FactoryElement::toJsonObject).toList()) {
                                if (jsonObject.isPresent("value")) {
                                    int val = jsonObject.getNumber("value").getInt();
                                    String operation = jsonObject.getString("operation");
                                    BinaryOperator mathOperator = Utils.getOperationMappingsDouble().get(operation);
                                    if (mathOperator != null && CraftItemStack.asNMSCopy(e.getItem()).getItem().getFoodProperties() != null) {

                                        double finalValue = (double) mathOperator.apply(CraftItemStack.asNMSCopy(e.getItem()).getItem().getFoodProperties().getNutrition(), (double) val);
                                        player.setFoodLevel(Integer.parseInt(String.valueOf(Math.round(player.getFoodLevel() + finalValue))));
                                        setActive(player, power.getTag(), true);
                                    }
                                }
                            }
                            for (FactoryJsonObject jsonObject : power.getList$SingularPlural("saturation_modifier", "saturation_modifiers").stream().map(FactoryElement::toJsonObject).toList()) {
                                if (jsonObject.isPresent("value")) {
                                    int val = jsonObject.getNumber("value").getInt();
                                    String operation = jsonObject.getString("operation");
                                    BinaryOperator mathOperator = Utils.getOperationMappingsDouble().get(operation);
                                    if (mathOperator != null && CraftItemStack.asNMSCopy(e.getItem()).getItem().getFoodProperties() != null) {
                                        double finalValue = (double) mathOperator.apply(CraftItemStack.asNMSCopy(e.getItem()).getItem().getFoodProperties().getSaturationModifier(), (double) val);
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
    public String getPowerFile() {
        return "apoli:modify_food";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_food;
    }
}
