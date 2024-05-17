package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.data.types.JsonKeybind;
import org.bukkit.entity.Player;

public interface KeyedPower {
	boolean isActive(Player p);

	JsonKeybind getJsonKey();
}
