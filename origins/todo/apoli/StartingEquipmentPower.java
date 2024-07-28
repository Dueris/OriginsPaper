package me.dueris.originspaper.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.PowerUpdateEvent;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.util.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("starting_equipment"))
			.add("stack", FactoryJsonObject.class, new OptionalInstance())
			.add("stacks", FactoryJsonArray.class, new OptionalInstance())
			.add("recurrent", boolean.class, false);
	}

	@EventHandler
	public void runGive(@NotNull PowerUpdateEvent e) {
		if (!e.getPower().getType().equalsIgnoreCase(getType())) return;
		if (getPlayers().contains(e.getPlayer()) && e.isNew()) {
			if (isActive(e.getPlayer())) {
				runGiveItems(e.getPlayer());
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
	public void runRespawn(@NotNull PlayerRespawnEvent e) {
		if (getPlayers().contains(e.getPlayer())) {
			if (isActive(e.getPlayer()) && recurrent) {
				runGiveItems(e.getPlayer());
			}
		}
	}
}
