package me.dueris.genesismc.util.exception;

import me.dueris.genesismc.factory.powers.holder.PowerType;

/**
 * Thrown when a duplicate CraftPower is detected when
 */
public class DuplicateCraftPowerException extends RuntimeException {
	public DuplicateCraftPowerException(Class<? extends PowerType> power1, Class<? extends PowerType> power2) throws InstantiationException, IllegalAccessException {
		super("Duplicate CraftPower detected: " + power1.getSimpleName() + ", " + power2.getSimpleName() + " : " + power1.newInstance().getType());
	}
}
