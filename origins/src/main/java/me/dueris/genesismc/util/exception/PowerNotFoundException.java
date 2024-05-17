package me.dueris.genesismc.util.exception;

public class PowerNotFoundException extends RuntimeException {
	public PowerNotFoundException(String powerTag) {
		super("Unable to find power: " + powerTag);
	}
}
