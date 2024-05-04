package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;

public class RestrictArmor extends CraftPower implements Listener {

    @EventHandler
    public void tick(PlayerArmorChangeEvent e) {
	Player p = e.getPlayer();
	if (getPlayersWithPower().contains(p)) {
	    for (Layer layer : CraftApoli.getLayersFromRegistry()) {
		for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType(), layer)) {
		    if (power == null) continue;
		    if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
			runPower(p, power);
		    }
		}
	    }
	}
    }


    @Override
    public void run(Player p, Power power) {
	long interval = power.getNumberOrDefault("interval", 1L).getLong();
	if (interval == 0) interval = 1L;
	if (Bukkit.getServer().getCurrentTick() % interval == 0) {
	    if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
		runPower(p, power);
	    } else {
		setActive(p, power.getTag(), false);
	    }
	}
    }

    public void runPower(Player p, Power power) {
	setActive(p, power.getTag(), true);
	boolean headb = true;
	boolean chestb = true;
	boolean legsb = true;
	boolean feetb = true;
	boolean passFeet;
	boolean passLegs;
	boolean passChest;
	boolean passHead;
	FactoryJsonObject headObj = power.getJsonObject("head");
	FactoryJsonObject chestObj = power.getJsonObject("chest");
	FactoryJsonObject legsObj = power.getJsonObject("legs");
	FactoryJsonObject feetObj = power.getJsonObject("feet");

	if (headObj == null) headb = false;
	if (chestObj == null) chestb = false;
	if (legsObj == null) legsb = false;
	if (feetObj == null) feetb = false;

	passHead = !headb || ConditionExecutor.testItem(headObj, p.getInventory().getItem(EquipmentSlot.HEAD));
	passChest = !chestb || ConditionExecutor.testItem(chestObj, p.getInventory().getItem(EquipmentSlot.CHEST));
	passLegs = !legsb || ConditionExecutor.testItem(legsObj, p.getInventory().getItem(EquipmentSlot.LEGS));
	passFeet = !feetb || ConditionExecutor.testItem(feetObj, p.getInventory().getItem(EquipmentSlot.FEET));

	if (passFeet) {
	    OriginPlayerAccessor.moveEquipmentInventory(p, EquipmentSlot.FEET);
	}
	if (passChest) {
	    OriginPlayerAccessor.moveEquipmentInventory(p, EquipmentSlot.CHEST);
	}
	if (passHead) {
	    OriginPlayerAccessor.moveEquipmentInventory(p, EquipmentSlot.HEAD);
	}
	if (passLegs) {
	    OriginPlayerAccessor.moveEquipmentInventory(p, EquipmentSlot.LEGS);
	}
    }

    @Override
    public String getType() {
	return "apoli:restrict_armor";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
	return restrict_armor;
    }
}
