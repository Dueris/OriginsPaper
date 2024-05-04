package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.powers.CraftPower;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class SimplePower extends CraftPower {
    @Override
    public String getType() {
        return "apoli:simple";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return simple;
    }
}
