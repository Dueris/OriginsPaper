package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.BinaryOperator;

public class ModifySwimSpeedPower extends CraftPower {

    @Override
    public String getType() {
        return "apoli:modify_swim_speed";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return modify_swim_speed;
    }

    @Override
    public void run(Player p, Power power) {
        Block be = p.getLocation().getBlock();
        if (!getPlayersWithPower().contains(p) || p.isFlying() || be == null ||
                !p.getLocation().getBlock().isLiquid() || !p.isSwimming()) return;
        float multiplyBy = 0.6F;
        for (Modifier modifier : power.getModifiers()) {
            Map<String, BinaryOperator<Float>> floatBinaryOperator = Utils.getOperationMappingsFloat();
            floatBinaryOperator.get(modifier.operation()).apply(multiplyBy, modifier.value() * 10f);
        }
        p.setVelocity(p.getLocation().getDirection().multiply(multiplyBy));
    }
}
