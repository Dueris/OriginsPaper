package me.dueris.originspaper.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.RequiredInstance;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.PowerUpdateEvent;
import me.dueris.originspaper.factory.data.types.EntityGroup;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class EntityGroupManager extends PowerType {
	public static HashMap<Entity, EntityGroup> modifiedEntityGroups = new HashMap<>();
	private final EntityGroup group;

	public EntityGroupManager(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, EntityGroup group) {
		super(name, description, hidden, condition, loading_priority);
		this.group = group;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("entity_group"))
			.add("group", EntityGroup.class, new RequiredInstance());
	}

	@EventHandler
	public void modifyFromPower(@NotNull PowerUpdateEvent e) {
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
