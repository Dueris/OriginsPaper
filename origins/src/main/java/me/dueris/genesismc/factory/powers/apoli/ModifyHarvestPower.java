package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_harvest;

public class ModifyHarvestPower extends CraftPower implements Listener {

	@EventHandler
	public void runD(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (modify_harvest.contains(p)) {
			if (p.getGameMode().equals(GameMode.CREATIVE)) return;
			try {
				for (Layer layer : CraftApoli.getLayersFromRegistry()) {
					for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
						if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
							setActive(p, power.getTag(), true);
							if (power.getBooleanOrDefault("allow", true) && !e.isDropItems()) {
								System.out.println("ksflkfjlskdfsdfssddvfsd");
								e.getBlock().getDrops().forEach((itemStack -> p.getWorld().dropItemNaturally(e.getBlock().getLocation(), itemStack)));
							} else if (e.isDropItems() && !power.getBooleanOrDefault("allow", true)) {
								System.out.println("lsfjkfdjls");
								e.setDropItems(false);
							}
						} else {
							setActive(p, power.getTag(), false);
						}
					}
				}
			} catch (Exception ee) {

			}
		}
	}

	@Override
	public void run(Player p) {

	}

	@Override
	public String getPowerFile() {
		return "apoli:modify_harvest";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return modify_harvest;
	}
}
