package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.powers.CraftPower;
import org.bukkit.entity.Player;

import java.util.ArrayList;

// Makes a placeholder so when getting this powerType from registry it doesnt throw
public class MultiplePower extends CraftPower {

    @Override
    public String getType() {
	return "apoli:multiple";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
	return multiple;
    }
}
