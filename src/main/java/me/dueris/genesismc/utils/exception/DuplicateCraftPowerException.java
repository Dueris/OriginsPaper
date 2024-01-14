package me.dueris.genesismc.utils.exception;

import me.dueris.genesismc.factory.powers.CraftPower;

/**
 * Thrown when a duplicate CraftPower is detected when
 */
public class DuplicateCraftPowerException extends RuntimeException{
    public DuplicateCraftPowerException(Class<? extends CraftPower> power1, Class<? extends CraftPower> power2) throws InstantiationException, IllegalAccessException {
        super("Duplicate CraftPower detected: " + power1.getSimpleName() + ", " + power2.getSimpleName() + " : " + power1.newInstance().getPowerFile());
    }
}
