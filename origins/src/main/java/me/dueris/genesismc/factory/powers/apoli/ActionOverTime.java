package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ActionOverTime extends PowerType {
	private static final HashMap<String /*tag*/, Boolean /*allowed*/> taggedAllowedMap = new HashMap<>();
	private final int interval;
	private final FactoryJsonObject risingAction;
	private final FactoryJsonObject entityAction;
	private final FactoryJsonObject fallingAction;

	public ActionOverTime(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, int interval, FactoryJsonObject risingAction, FactoryJsonObject entityAction, FactoryJsonObject fallingAction) {
		super(name, description, hidden, condition, loading_priority);
		this.interval = interval;
		this.risingAction = risingAction;
		this.entityAction = entityAction;
		this.fallingAction = fallingAction;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("action_over_time"))
			.add("interval", int.class, 20)
			.add("rising_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("falling_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@Override
	public void tick(Player p) {
		if (Bukkit.getServer().getCurrentTick() % interval == 0) {
			taggedAllowedMap.putIfAbsent(getTag(), false);
			if (isActive(p)) {
				if (!taggedAllowedMap.get(getTag())) {
					taggedAllowedMap.put(getTag(), true);
					Actions.executeEntity(p, risingAction);
				}
				Actions.executeEntity(p, entityAction);
			} else {
				if (taggedAllowedMap.get(getTag())) {
					taggedAllowedMap.put(getTag(), false);
					Actions.executeEntity(p, fallingAction);
				}
			}
		}
	}

}
