package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.util.Util;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;

public class ModifyBreakSpeedPower extends ModifierPower implements Listener {
	private static final HashMap<Player, Double> base = new HashMap<>();
	private final FactoryJsonObject blockCondition;

	public ModifyBreakSpeedPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject modifier, FactoryJsonArray modifiers, FactoryJsonObject blockCondition) {
		super(name, description, hidden, condition, loading_priority, modifier, modifiers);
		this.blockCondition = blockCondition;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return ModifierPower.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("modify_break_speed"))
			.add("block_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	public void compute(@NotNull Player p) {
		double b = p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).getDefaultValue();
		Arrays.stream(getModifiers()).forEach(modifier -> p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).setBaseValue(Util.getOperationMappingsDouble().get(modifier.operation()).apply(b, modifier.value().doubleValue() * 100)));
		base.put(p, b);
	}

	@EventHandler
	public void swing(BlockDamageEvent e) {
		if (getPlayers().contains(e.getPlayer())) {
			Player p = e.getPlayer();
			if (!isActive(p) || !ConditionExecutor.testBlock(blockCondition, e.getBlock())) {
				p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).setBaseValue(p.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).getDefaultValue());
				return;
			}
			compute(p);
		}
	}

	public FactoryJsonObject getBlockCondition() {
		return blockCondition;
	}
}
