package me.dueris.originspaper.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.data.types.modifier.Modifier;
import me.dueris.originspaper.util.DataConverter;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModifyJumpPower extends ModifierPower implements Listener {
	private static final HashMap<Player, Boolean> applied = new HashMap<>();
	private static final HashMap<Player, Pair<ModifyJumpPower, List<AttributeModifier>>> modifiers = new HashMap<>();
	private final FactoryJsonObject entityAction;

	public ModifyJumpPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject modifier, FactoryJsonArray modifiers, FactoryJsonObject entityAction) {
		super(name, description, hidden, condition, loading_priority, modifier, modifiers);
		this.entityAction = entityAction;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return ModifierPower.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("modify_jump"))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void onJump(@NotNull PlayerJumpEvent e) {
		if (getPlayers().contains(e.getPlayer()) && isActive(e.getPlayer())) {
			Actions.executeEntity(e.getPlayer(), entityAction);
		}
	}

	@Override
	public void tick(Player p) {
		modifiers.putIfAbsent(p, new Pair<>(this, new ArrayList<>()));
		if (Bukkit.getCurrentTick() % 2 == 0) { // Run at 10 TPS
			if (!applied.containsKey(p)) {
				applied.put(p, false);
			}
			if (isActive(p)) {
				if (!applied.get(p)) {
					for (Modifier modifier : getModifiers()) {
						AttributeModifier attributeModifier = DataConverter.convertToAttributeModifier(modifier);
						Attribute attribute = Attribute.GENERIC_JUMP_STRENGTH;
						p.getAttribute(attribute).addTransientModifier(attributeModifier);
						modifiers.get(p).getSecond().add(attributeModifier);
					}
					applied.put(p, true);
				}
			} else {
				if (applied.get(p)) {
					for (AttributeModifier modifier : modifiers.get(p).getSecond()) {
						p.getAttribute(Attribute.GENERIC_JUMP_STRENGTH).removeModifier(modifier);
					}
					applied.put(p, false);
				}
			}
		}
	}

}
