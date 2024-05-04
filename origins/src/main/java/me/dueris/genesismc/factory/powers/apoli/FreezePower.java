package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class FreezePower extends CraftPower {

    @Override
    public void run(Player p, Power power) {
	if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
	    setActive(p, power.getTag(), true);
	    if (p.getFreezeTicks() >= 138) {
		p.setFreezeTicks(150);
	    } else {
		p.setFreezeTicks(Math.min(p.getMaxFreezeTicks(), p.getFreezeTicks() + 3));
	    }
	} else {
	    setActive(p, power.getTag(), false);
	}
    }

    @Override
    public String getType() {
	return "apoli:freeze";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
	return freeze;
    }
}
