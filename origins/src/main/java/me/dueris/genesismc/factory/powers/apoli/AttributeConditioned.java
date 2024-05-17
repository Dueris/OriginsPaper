package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.util.DataConverter;
import me.dueris.genesismc.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

public class AttributeConditioned extends AttributeHandler implements Listener {

	private static final HashMap<Player, Boolean> applied = new HashMap<>();
	private final int tickRate;

	public AttributeConditioned(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, boolean updateHealth, FactoryJsonObject modifier, FactoryJsonArray modifiers, int tickRate) {
		super(name, description, hidden, condition, loading_priority, updateHealth, modifier, modifiers);
		this.tickRate = tickRate;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return AttributeHandler.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("conditioned_attribute"))
			.add("tick_rate", int.class, 20);
	}

	public static void executeAttributeModify(String operation, Attribute attribute_modifier, double base_value, Player p, Double value) {
		BinaryOperator<Double> operator = Utils.getOperationMappingsDouble().get(operation);
		if (operator != null) {
			double result = Double.parseDouble(String.valueOf(operator.apply(base_value, value)));
			p.getAttribute(attribute_modifier).setBaseValue(result);
		} else {
			Bukkit.getLogger().warning("An unexpected error occurred when retrieving the BinaryOperator for attribute_conditioned!");
			new Throwable().printStackTrace();
		}
		p.sendHealthUpdate();
	}

	public void executeConditionAttribute(Player p) {
		for (Modifier modifier : getModifiers()) {
			Attribute attributeModifier = DataConverter.resolveAttribute(modifier.handle.getString("attribute"));
			if (p.getAttribute(attributeModifier) != null && !appliedAttributes.get(p).contains(this)) {
				double val = DataConverter.convertToAttributeModifier(modifier).getAmount();
				double baseVal = p.getAttribute(attributeModifier).getBaseValue();
				String operation = modifier.operation();
				BinaryOperator<Double> operator = Utils.getOperationMappingsDouble().get(operation);
				if (operator != null) {
					double result = Double.parseDouble(String.valueOf(operator.apply(baseVal, val)));
					p.getAttribute(attributeModifier).setBaseValue(result);
				} else {
					Bukkit.getLogger().warning("An unexpected error occurred when retrieving the BinaryOperator for attribute_conditioned!");
					new Throwable().printStackTrace();
				}
			}
		}
	}

	public void inverseConditionAttribute(Player p) {
		for (Modifier modifier : getModifiers()) {
			Attribute attribute_modifier = DataConverter.resolveAttribute(modifier.handle.getString("attribute"));
			double value = DataConverter.convertToAttributeModifier(modifier).getAmount();
			double base_value = p.getAttribute(attribute_modifier).getBaseValue();
			String operation = modifier.operation();
			executeAttributeModify(operation, attribute_modifier, base_value, p, -value);
		}
	}

	@EventHandler
	public void join(PlayerJoinEvent e) {
		applied.put(e.getPlayer(), false);
	}

	@Override
	public void tick(Player p) {
		if (Bukkit.getCurrentTick() % tickRate == 0) {
			appliedAttributes.putIfAbsent(p, new ArrayList<>());
			if (!applied.containsKey(p)) {
				applied.put(p, false);
			}
			if (isActive(p)) {
				if (!applied.get(p)) {
					executeConditionAttribute(p);
					applied.put(p, true);
				}
			} else {
				if (applied.get(p)) {
					inverseConditionAttribute(p);
					applied.put(p, false);
				}
			}
		}
	}

	public int getTickRate() {
		return tickRate;
	}
}
