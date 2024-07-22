package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonArray;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.data.types.Modifier;
import me.dueris.originspaper.util.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

public class ModifyVelocityPower extends ModifierPower implements Listener {
	private final FactoryJsonArray axes;

	public ModifyVelocityPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject modifier, FactoryJsonArray modifiers, FactoryJsonArray axes) {
		super(name, description, hidden, condition, loading_priority, modifier, modifiers);
		this.axes = axes;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return ModifierPower.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("modify_velocity"))
			.add("axes", FactoryJsonArray.class, new FactoryJsonArray(new JsonArray()));
	}

	@EventHandler
	public void velocityModify(@NotNull PlayerVelocityEvent e) {
		if (getPlayers().contains(e.getPlayer())) {
			Player p = e.getPlayer();
			if (isActive(p)) {
				List<String> identifiers = new ArrayList<>(axes.asList().stream().map(FactoryElement::getString).toList());
				if (identifiers.isEmpty()) {
					identifiers.add("x");
					identifiers.add("y");
					identifiers.add("z");
				}
				Vector vel = e.getVelocity();
				for (Modifier modifier : getModifiers()) {
					Float value = modifier.value();
					String operation = modifier.operation();
					BinaryOperator mathOperator = Util.getOperationMappingsFloat().get(operation);
					for (String axis : identifiers) {
						if (axis.equals("x")) {
							vel.setX((float) mathOperator.apply(vel.getX(), value));
						}
						if (axis.equals("y")) {
							vel.setY((float) mathOperator.apply(vel.getY(), value));
						}
						if (axis.equals("z")) {
							vel.setZ((float) mathOperator.apply(vel.getZ(), value));
						}
					}
				}
				e.setVelocity(vel);
			}
		}
	}

}
