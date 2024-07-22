package me.dueris.originspaper.factory.powers.apoli;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.AddToSetEvent;
import me.dueris.originspaper.event.OriginChangeEvent;
import me.dueris.originspaper.event.RemoveFromSetEvent;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class EntitySetPower extends PowerType {
	public static HashMap<String, ArrayList<Entity>> entity_sets = new HashMap<>();
	private final FactoryJsonObject actionOnAdd;
	private final FactoryJsonObject actionOnRemove;

	public EntitySetPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject actionOnAdd, FactoryJsonObject actionOnRemove) {
		super(name, description, hidden, condition, loading_priority);
		this.actionOnAdd = actionOnAdd;
		this.actionOnRemove = actionOnRemove;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("entity_set"))
			.add("action_on_add", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("action_on_remove", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	public static void addToEntitySet(Entity p, String tag) {
		Preconditions.checkArgument(p != null, "Entity must not be null");
		Preconditions.checkArgument(tag != null, "EntitySetTag must not be null");
		if (entity_sets.containsKey(tag)) {
			entity_sets.get(tag).add(p);
		} else {
			entity_sets.put(tag, new ArrayList<>());
			addToEntitySet(p, tag);
		}
	}

	public static void removeFromEntitySet(Entity p, String tag) {
		Preconditions.checkArgument(p != null, "Entity must not be null");
		Preconditions.checkArgument(tag != null, "EntitySetTag must not be null");
		if (entity_sets.containsKey(tag)) {
			if (entity_sets.get(tag).isEmpty()) return;
			entity_sets.get(tag).removeIf(entity -> entity == p);
		} else {
			entity_sets.put(tag, new ArrayList<>());
			removeFromEntitySet(p, tag);
		}
	}

	public static boolean isInEntitySet(Entity p, String tag) {
		Preconditions.checkArgument(p != null, "Entity must not be null");
		Preconditions.checkArgument(tag != null, "EntitySetTag must not be null");
		if (entity_sets.containsKey(tag)) {
			return entity_sets.get(tag).contains(p);
		} else {
			entity_sets.put(tag, new ArrayList<>());
			return isInEntitySet(p, tag);
		}
	}

	public static boolean isInEntitySet(Entity p) {
		Preconditions.checkArgument(p != null, "Entity must not be null");
		boolean isIn = false;
		for (String tag : entity_sets.keySet()) {
			if (isIn) break;
			if (entity_sets.containsKey(tag)) {
				isIn = entity_sets.get(tag).contains(p);
			} else {
				entity_sets.put(tag, new ArrayList<>());
				isIn = isInEntitySet(p, tag);
			}
		}
		return isIn;
	}

	@EventHandler
	public void join(@NotNull PlayerJoinEvent e) {
		if (getPlayers().contains(e.getPlayer())) {
			for (String tag : entity_sets.keySet()) {
				if (entity_sets.containsKey(tag)) {
					entity_sets.get(tag).removeIf(entity -> entity == e.getPlayer());
				}
			}
			addToEntitySet(e.getPlayer(), getTag());
		}
	}

	@EventHandler
	public void addEvent(@NotNull AddToSetEvent e) {
		addToEntitySet(e.getEntity(), e.getTag());
		for (Entity entity : entity_sets.get(e.getTag())) {
			if (entity instanceof Player p && e.getTag().equalsIgnoreCase(getTag())) {
				if (getPlayers().contains(p)) {
					if (!isActive(p)) return;
					Actions.executeBiEntity(p, e.getEntity(), actionOnAdd);
				}
			}
		}
	}

	@EventHandler
	public void removeEvent(@NotNull RemoveFromSetEvent e) {
		removeFromEntitySet(e.getEntity(), e.getTag());
		for (Entity entity : entity_sets.get(e.getTag())) {
			if (entity instanceof Player p) {
				if (getPlayers().contains(p) && e.getTag().equalsIgnoreCase(getTag())) {
					if (!isActive(p)) return;
					Actions.executeBiEntity(p, e.getEntity(), actionOnRemove);
				}
			}
		}
	}

	@EventHandler
	public void changeOrigin(@NotNull OriginChangeEvent e) {
		if (getPlayers().contains(e.getPlayer())) {
			for (String tag : entity_sets.keySet()) {
				if (entity_sets.containsKey(tag)) {
					entity_sets.get(tag).removeIf(entity -> entity == e.getPlayer());
				}
			}
			if (!isActive(e.getPlayer())) return;
			addToEntitySet(e.getPlayer(), getTag());
		}
	}

}
