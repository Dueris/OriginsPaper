package io.github.dueris.originspaper.power.provider;

import org.bukkit.entity.Player;

public interface PowerProvider {
	default void tick(Player player) {
	}

	default void tick() {
	}
}