package me.dueris.genesismc.factory.powers.OriginsMod.player;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Freeze extends CraftPower {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                if (freeze.contains(p)) {
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if (conditionExecutor.check("condition", "conditions", p, origin, "origins:freeze", null, p)) {
                        p.setFreezeTicks(300);
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:freeze";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return freeze;
    }
}
