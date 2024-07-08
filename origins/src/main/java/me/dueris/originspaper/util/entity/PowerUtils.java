package me.dueris.originspaper.util.entity;

import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.registry.registries.Layer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PowerUtils {
	public static void removePower(CommandSender executor, PowerType poweR, Player p, Layer layer, boolean suppress) throws InstantiationException, IllegalAccessException {
		if (PowerHolderComponent.playerPowerMapping.get(p) != null) {
			ArrayList<PowerType> powersToEdit = new ArrayList<>();
			powersToEdit.add(poweR);
			powersToEdit.addAll(CraftApoli.getNestedPowerTypes(poweR));
			for (PowerType power : powersToEdit) {
				try {
					if (PowerHolderComponent.playerPowerMapping.get(p).get(layer).contains(power)) {
						PowerHolderComponent.playerPowerMapping.get(p).get(layer).remove(power);
						PowerHolderComponent.removePower(p, power, suppress, true);
						if (!suppress) {
							executor.sendMessage("Entity %name% had the power %power% removed"
								.replace("%power%", power.getName())
								.replace("%name%", p.getName())
							);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void grantPower(CommandSender executor, PowerType power, Player p, Layer layer, boolean suppress) throws InstantiationException, IllegalAccessException {
		if (!PowerHolderComponent.playerPowerMapping.get(p).get(layer).contains(power)) {
			PowerHolderComponent.playerPowerMapping.get(p).get(layer).add(power);
			PowerHolderComponent.applyPower(p, power, suppress, true);
			if (!suppress) {
				executor.sendMessage("Entity %name% was granted the power %power%"
					.replace("%power%", power.getName())
					.replace("%name%", p.getName())
				);
			}
		}
	}
}
