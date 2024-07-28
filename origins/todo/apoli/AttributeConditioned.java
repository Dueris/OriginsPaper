package me.dueris.originspaper.factory.powers.apoli;

import com.mojang.datafixers.util.Pair;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.data.types.modifier.Modifier;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.util.DataConverter;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AttributeConditioned extends AttributeHandler implements Listener {
	private static final HashMap<Player, Boolean> applied = new HashMap<>();
	private static final HashMap<Player, Pair<PowerType, List<Pair<AttributeModifier, Attribute>>>> modifiers = new HashMap<>();
	private final int tickRate;

	public AttributeConditioned(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, boolean updateHealth, FactoryJsonObject modifier, FactoryJsonArray modifiers, String attribute, int tickRate) {
		super(name, description, hidden, condition, loading_priority, updateHealth, modifier, modifiers, attribute);
		this.tickRate = tickRate;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return AttributeHandler.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("conditioned_attribute"))
			.add("tick_rate", int.class, 20);
	}

	@EventHandler
	public void join(@NotNull PlayerJoinEvent e) {
		applied.put(e.getPlayer(), false);
	}

	@Override
	public void tick(Player p) {
		modifiers.putIfAbsent(p, new Pair<>(this, new ArrayList<>()));
		if (Bukkit.getCurrentTick() % tickRate == 0) {
			if (!applied.containsKey(p)) {
				applied.put(p, false);
			}
			if (isActive(p)) {
				if (!applied.get(p)) {
					for (Modifier modifier : getModifiers()) {
						AttributeModifier attributeModifier = DataConverter.convertToAttributeModifier(modifier);
						Attribute attribute = modifier.handle.isPresent("attribute") ? DataConverter.resolveAttribute(modifier.handle.getString("attribute")) : DataConverter.resolveAttribute(getAttribute());
						p.getAttribute(attribute).addTransientModifier(attributeModifier);
						modifiers.get(p).getSecond().add(new Pair<>(attributeModifier, attribute));
					}
					applied.put(p, true);
				}
			} else {
				if (applied.get(p)) {
					for (Pair<AttributeModifier, Attribute> modifier : modifiers.get(p).getSecond()) {
						p.getAttribute(modifier.getSecond()).removeModifier(modifier.getFirst());
					}
					applied.put(p, false);
				}
			}
		}
	}

	public int getTickRate() {
		return tickRate;
	}
}
