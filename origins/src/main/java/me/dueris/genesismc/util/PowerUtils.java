package me.dueris.genesismc.util;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PowerUtils {

	public static void grant(CommandSender executor, Power power, Player p, Layer layer, boolean suppress) throws InstantiationException, IllegalAccessException {
		GenesisMC.getScheduler().parent.offMain(() -> {
			if (!OriginPlayerAccessor.playerPowerMapping.get(p).get(layer).contains(power)) {
				OriginPlayerAccessor.playerPowerMapping.get(p).get(layer).add(power);
				OriginPlayerAccessor.applyPower(p, power, suppress, true);
				if (!suppress) {
					executor.sendMessage("Entity %name% was granted the power %power%"
						.replace("%power%", power.getName())
						.replace("%name%", p.getName())
					);
				}
			}
		});
	}

	public static void remove(CommandSender executor, Power poweR, Player p, Layer layer, boolean suppress) throws InstantiationException, IllegalAccessException {
		GenesisMC.getScheduler().parent.offMain(() -> {
			if (OriginPlayerAccessor.playerPowerMapping.get(p) != null) {
				ArrayList<Power> powersToEdit = new ArrayList<>();
				powersToEdit.add(poweR);
				powersToEdit.addAll(CraftApoli.getNestedPowers(poweR));
				for (Power power : powersToEdit) {
					try {
						if (OriginPlayerAccessor.playerPowerMapping.get(p).get(layer).contains(power)) {
							OriginPlayerAccessor.playerPowerMapping.get(p).get(layer).remove(power);
							OriginPlayerAccessor.removePower(p, power, suppress, true);
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
}
