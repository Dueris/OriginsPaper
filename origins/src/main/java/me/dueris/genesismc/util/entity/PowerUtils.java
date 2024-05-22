package me.dueris.genesismc.util.entity;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.registry.registries.Layer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PowerUtils {
	public static void removePower(CommandSender executor, PowerType poweR, Player p, Layer layer, boolean suppress) throws InstantiationException, IllegalAccessException {
		GenesisMC.getScheduler().parent.offMain(() -> {
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
		});
	}

	// New Rewrite
	public static void grantPower(CommandSender executor, PowerType power, Player p, Layer layer, boolean suppress) throws InstantiationException, IllegalAccessException {
		GenesisMC.getScheduler().parent.offMain(() -> {
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
		});
	}
}
