package me.dueris.originspaper.factory.powers.apoli.provider;

import org.bukkit.entity.Player;

public interface PowerProvider {
	default void tick(Player player) {
	}

	default void tick() {
	}
}