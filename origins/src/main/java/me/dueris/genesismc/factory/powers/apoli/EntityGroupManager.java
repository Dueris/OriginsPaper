package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.RequiredInstance;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.data.types.EntityGroup;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;

import java.util.HashMap;

public class EntityGroupManager extends PowerType {
	public static HashMap<Entity, EntityGroup> modifiedEntityGroups = new HashMap<>();
	private final EntityGroup group;

	public EntityGroupManager(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, EntityGroup group) {
		super(name, description, hidden, condition, loading_priority);
		this.group = group;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("entity_group"))
			.add("group", EntityGroup.class, new RequiredInstance());
	}

	@EventHandler
	public void modifyFromPower(PowerUpdateEvent e) {
		if (e.getPower().getTag().equalsIgnoreCase(getTag())) {
			if (e.isRemoved() && modifiedEntityGroups.containsKey(e.getPlayer())) {
				modifiedEntityGroups.remove(e.getPlayer());
			} else {
				modifiedEntityGroups.put(e.getPlayer(), group);
			}
		}
	}

	public EntityGroup getGroup() {
		return group;
	}
}
