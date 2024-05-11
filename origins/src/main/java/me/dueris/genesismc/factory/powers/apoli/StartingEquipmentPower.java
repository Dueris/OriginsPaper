package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class StartingEquipmentPower extends CraftPower implements Listener {

	@EventHandler
	public void runGive(PowerUpdateEvent e) {
		if (!e.getPower().getType().equalsIgnoreCase(getType())) return;
		if (starting_equip.contains(e.getPlayer()) && e.isNew()) {
			if (ConditionExecutor.testEntity(e.getPower().getJsonObject("condition"), (CraftEntity) e.getPlayer())) {
				setActive(e.getPlayer(), e.getPower().getTag(), true);
				runGiveItems(e.getPlayer(), e.getPower());
			} else {
				setActive(e.getPlayer(), e.getPower().getTag(), false);
			}
		}
	}

	public void runGiveItems(Player p, Power power) {
		for (FactoryJsonObject jsonObject : power.getList$SingularPlural("stack", "stacks").stream().map(FactoryElement::toJsonObject).toList()) {
			ItemStack stack = jsonObject.asItemStack();
			if (jsonObject.isPresent("slot")) {
				Utils.addPositionedItemStack(p.getInventory(), stack, jsonObject.getNumber("slot").getInt());
			} else p.getInventory().addItem(stack);
		}
	}

	@EventHandler
	public void runRespawn(PlayerRespawnEvent e) {
		if (starting_equip.contains(e.getPlayer())) {
			for (Layer layer : CraftApoli.getLayersFromRegistry()) {
				for (Power power : OriginPlayerAccessor.getPowers(e.getPlayer(), getType(), layer)) {
					if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) e.getPlayer()) && power.getBooleanOrDefault("recurrent", false)) {
						setActive(e.getPlayer(), power.getTag(), true);
						runGiveItems(e.getPlayer(), power);
					} else {
						setActive(e.getPlayer(), power.getTag(), false);
					}
				}
			}
		}
	}

	@Override
	public String getType() {
		return "apoli:starting_equipment";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return starting_equip;
	}
}
