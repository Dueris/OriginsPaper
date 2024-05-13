package me.dueris.genesismc.factory.powers.apoli.provider;

import org.bukkit.entity.Player;

public interface PowerProvider {
	default void tick(Player player) {
	}
}