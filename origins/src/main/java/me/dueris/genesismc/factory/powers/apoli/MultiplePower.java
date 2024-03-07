package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.builder.inst.FactoryObjectInstance;
import me.dueris.genesismc.factory.powers.CraftPower;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Makes a placeholder so when getting this powerType from registry it doesnt throw
public class MultiplePower extends CraftPower {
    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "apoli:multiple";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return multiple;
    }

    @Override
    public List<FactoryObjectInstance> getValidObjectFactory() {
        return super.getDefaultObjectFactory(List.of());
    }
}
