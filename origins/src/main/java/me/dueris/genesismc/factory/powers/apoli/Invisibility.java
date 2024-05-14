package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.annotations.Register;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

public class Invisibility extends PowerType implements Listener {

	@Register
	public Invisibility(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		super(name, description, hidden, condition, loading_priority);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("invisibility"));
	}

	@Override
	public void tick(Player p) {
		boolean shouldSetInvisible = isActive(p);

		p.setInvisible(shouldSetInvisible || p.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY));
	}

	public void doesntHavePower(Player p) {
		if (p.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY)) {
			return;
		}
		p.setInvisible(false);
	}

	@EventHandler
	public void powerUpdate(PowerUpdateEvent e) {
		if (!getPlayers().contains(e.getPlayer())) {
			doesntHavePower(e.getPlayer());
			return;
		}
		tick(e.getPlayer());
	}

}
