package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.util.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class StartingEquipmentPower extends PowerType {
	private final List<FactoryJsonObject> stacks;
	private final boolean recurrent;

	public StartingEquipmentPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject stack, FactoryJsonArray stacks, boolean recurrent) {
		super(name, description, hidden, condition, loading_priority);
		this.stacks = stack != null ? List.of(stack) : stacks.asJsonObjectList();
		this.recurrent = recurrent;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("starting_equipment"))
			.add("stack", FactoryJsonObject.class, new OptionalInstance())
			.add("stacks", FactoryJsonArray.class, new OptionalInstance())
			.add("recurrent", boolean.class, false);
	}

	@EventHandler
	public void runGive(PowerUpdateEvent e) {
		if (!e.getPower().getType().equalsIgnoreCase(getType())) return;
		if (getPlayers().contains(e.getPlayer()) && e.isNew()) {
			if (isActive(e.getPlayer())) {
				runGiveItems(e.getPlayer());
			} else {
			}
		}
	}

	public void runGiveItems(Player p) {
		for (FactoryJsonObject jsonObject : stacks) {
			ItemStack stack = jsonObject.asItemStack();
			if (jsonObject.isPresent("slot")) {
				Util.addPositionedItemStack(p.getInventory(), stack, jsonObject.getNumber("slot").getInt());
			} else p.getInventory().addItem(stack);
		}
	}

	@EventHandler
	public void runRespawn(PlayerRespawnEvent e) {
		if (getPlayers().contains(e.getPlayer())) {
			if (isActive(e.getPlayer()) && recurrent) {
				runGiveItems(e.getPlayer());
			}
		}
	}
}
