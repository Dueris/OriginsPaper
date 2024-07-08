package me.dueris.originspaper.factory.powers.apoli;

import me.dueris.originspaper.factory.data.types.JsonKeybind;
import org.bukkit.entity.Player;

public interface KeyedPower {
	boolean isActive(Player p);

	JsonKeybind getJsonKey();
}
