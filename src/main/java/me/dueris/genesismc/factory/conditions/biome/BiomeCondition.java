package me.dueris.genesismc.factory.conditions.biome;

import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.powers.player.RestrictArmor;
import me.dueris.genesismc.utils.OriginContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Climate;
import org.bukkit.Fluid;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.block.data.type.CraftWall;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class BiomeCondition implements Condition {

    @Override
    public String condition_type() {
        return "BIOME_CONDITION";
    }

    @Override
    public Optional<Boolean> check(HashMap<String, Object> condition, Player p, OriginContainer origin, String powerfile, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent entityDamageEvent) {
        if (origin == null) return Optional.empty();
        if (origin.getPowerFileFromType(powerfile) == null) return Optional.empty();
        if (origin.getPowerFileFromType(powerfile).getCondition().isEmpty()) return Optional.empty();
            if (condition.isEmpty()) return Optional.empty();
            if (condition.get("type") == null) return Optional.empty();
            boolean inverted = (boolean) condition.getOrDefault("inverted", false);
            String type = condition.get("type").toString().toLowerCase();
            if (type.equalsIgnoreCase("origins:biome") && condition.containsKey("condition")) {
                Map<String, Object> keyMap = (Map<String, Object>) condition.get("condition");
                if (keyMap.containsKey("type") && keyMap.get("type").equals("origins:temperature")) {
                    if (keyMap.containsKey("comparison") && keyMap.containsKey("compare_to")) {
                        if (RestrictArmor.compareValues(block.getTemperature(), keyMap.get("comparison").toString(), Double.parseDouble(keyMap.get("compare_to").toString()))) {
                            return getResult(inverted, true);
                        }
                    }
                }
            }
            return getResult(inverted, false);
    }
}
