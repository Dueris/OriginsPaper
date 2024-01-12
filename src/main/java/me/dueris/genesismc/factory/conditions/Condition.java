package me.dueris.genesismc.factory.conditions;

import org.bukkit.Fluid;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Optional;

public interface Condition {
    String condition_type();

    Optional<Boolean> check(HashMap<String, Object> condition, Player p, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent dmgevent);
}
