package me.dueris.genesismc.factory.conditions.fluid;

import me.dueris.genesismc.factory.TagRegistry;
import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Fluid;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Optional;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class FluidCondition implements Condition {
    @Override
    public String condition_type() {
        return "FLUID_CONDITION";
    }

    @Override
    public Optional<Boolean> check(HashMap<String, Object> condition, Player p, PowerContainer power, String powerfile, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent entityDamageEvent) {
        if (condition.isEmpty()) return Optional.empty();
        if (condition.get("type") == null) return Optional.empty();
        boolean inverted = (boolean) condition.getOrDefault("inverted", false);
        String type = condition.get("type").toString().toLowerCase();

        switch (type) {
            case "origins:empty" : {
                return getResult(inverted, Fluid.EMPTY.equals(fluid));
            }
            case "origins:in_tag" : {
                for(String flu : TagRegistry.getRegisteredTagFromFileKey(condition.get("tag").toString())){
                    if(flu == null) continue;
                    if(fluid == null) continue;
                    if(flu.equalsIgnoreCase(fluid.toString())){
                        return getResult(inverted, true);
                    }
                }
            }
            case "origins:still" : {
                return getResult(inverted, Fluid.LAVA.equals(fluid) || Fluid.WATER.equals(fluid));
            }
        }
        return getResult(inverted, false);
    }
}
