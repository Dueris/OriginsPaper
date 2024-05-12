package me.dueris.genesismc.factory.powers.apoli.provider.origins;

import io.papermc.paper.event.entity.EntityInsideBlockEvent;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.apoli.provider.PowerProvider;
import me.dueris.genesismc.util.entity.PowerHolderComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

// TODO: 1.21 attribute for movement efficiency
public class NoCobWebSlowdown implements Listener, PowerProvider {
	protected static NamespacedKey powerReference = GenesisMC.originIdentifier("master_of_webs_no_slowdown");

	@EventHandler
	public void insideBlock(EntityInsideBlockEvent e) {
		if (!PowerHolderComponent.hasPower(e.getEntity(), powerReference.asString())) return;
		if (e.getBlock().getType().equals(Material.COBWEB)) {
			e.setCancelled(true);
		}
	}

}
