package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass.prevent_sprinting;

public class PreventSprinting extends CraftPower {

    @Override
    public void run(Player p, Power power) {
	if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
	    setActive(p, power.getTag(), true);
	    p.setSprinting(false);
	} else {
	    setActive(p, power.getTag(), false);
	}
    }

    @Override
    public String getType() {
	return "apoli:prevent_sprinting";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
	return prevent_sprinting;
    }
}
