package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class ModifyBreakSpeedPower extends CraftPower implements Listener {
    private static final HashMap<Player, Double> base = new HashMap<>();

    public static void compute(Player p, Power power) {
        double b = p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).getDefaultValue();
        power.getModifiers().forEach(modifier -> p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).setBaseValue(Utils.getOperationMappingsDouble().get(modifier.operation()).apply(b, modifier.value().doubleValue() * 100)));
        base.put(p, b);
    }

    @EventHandler
    public void swing(BlockDamageEvent e) {
        if (getPlayersWithPower().contains(e.getPlayer())) {
            Player p = e.getPlayer();
            OriginPlayerAccessor.getMultiPowerFileFromType(p, getType()).forEach(power -> {
                if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p) || !ConditionExecutor.testBlock(power.getJsonObject("block_condition"), CraftBlock.at(((CraftWorld) e.getPlayer().getWorld()).getHandle(), CraftLocation.toBlockPosition(e.getBlock().getLocation())))) {
                    setActive(p, power.getTag(), false);
                    p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).setBaseValue(p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).getDefaultValue());
                    return;
                }
                setActive(p, power.getTag(), true);
                compute(p, power);
            });
        }
    }

    @Override
    public String getType() {
        return "apoli:modify_break_speed";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return modify_break_speed;
    }
}
