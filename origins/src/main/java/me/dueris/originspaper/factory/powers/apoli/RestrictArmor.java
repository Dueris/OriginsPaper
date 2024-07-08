package me.dueris.originspaper.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EquipmentSlot;

public class RestrictArmor extends PowerType {
	private final FactoryJsonObject head;
	private final FactoryJsonObject chest;
	private final FactoryJsonObject legs;
	private final FactoryJsonObject feet;

	public RestrictArmor(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject head, FactoryJsonObject chest, FactoryJsonObject legs, FactoryJsonObject feet) {
		super(name, description, hidden, condition, loading_priority);
		this.head = head;
		this.chest = chest;
		this.legs = legs;
		this.feet = feet;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("restrict_armor"))
			.add("head", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("chest", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("legs", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("feet", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void tickArmorChange(PlayerArmorChangeEvent e) {
		Player p = e.getPlayer();
		if (getPlayers().contains(p)) {
			if (isActive(p)) {
				runPower(p);
			}
		}
	}


	@Override
	public void tick(Player p) {
		if (isActive(p)) {
			runPower(p);
		}
	}

	public void runPower(Player p) {
		boolean passFeet = false;
		boolean passLegs = false;
		boolean passChest = false;
		boolean passHead = false;

		if (!head.isEmpty())
			passHead = ConditionExecutor.testItem(head, p.getInventory().getItem(EquipmentSlot.HEAD));
		if (!chest.isEmpty())
			passChest = ConditionExecutor.testItem(chest, p.getInventory().getItem(EquipmentSlot.CHEST));
		if (!legs.isEmpty())
			passLegs = ConditionExecutor.testItem(legs, p.getInventory().getItem(EquipmentSlot.LEGS));
		if (!feet.isEmpty())
			passFeet = ConditionExecutor.testItem(feet, p.getInventory().getItem(EquipmentSlot.FEET));

		if (passFeet)
			PowerHolderComponent.moveEquipmentInventory(p, EquipmentSlot.FEET);
		if (passChest)
			PowerHolderComponent.moveEquipmentInventory(p, EquipmentSlot.CHEST);
		if (passHead)
			PowerHolderComponent.moveEquipmentInventory(p, EquipmentSlot.HEAD);
		if (passLegs)
			PowerHolderComponent.moveEquipmentInventory(p, EquipmentSlot.LEGS);
	}
}
