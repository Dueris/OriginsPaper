package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class Invisibility extends CraftPower implements Listener {

    @Override
    public void run(Player p, Power power) {
	boolean shouldSetInvisible = ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p);

	p.setInvisible(shouldSetInvisible || p.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY));
    }

    @Override
    public void doesntHavePower(Player p) {
	if (p.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY)) {
	    return;
	}
	p.setInvisible(false);
    }

    @EventHandler
    public void powerUpdate(PowerUpdateEvent e) {
	if (!getPlayersWithPower().contains(e.getPlayer())) {
	    doesntHavePower(e.getPlayer());
	    return;
	}
	run(e.getPlayer(), e.getPower());
    }

    @Override
    public String getType() {
	return "apoli:invisibility";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
	return invisibility;
    }

}
