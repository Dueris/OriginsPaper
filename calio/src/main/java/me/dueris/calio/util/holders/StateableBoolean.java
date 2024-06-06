package me.dueris.calio.util.holders;

import java.util.Optional;

public class StateableBoolean {
	private Boolean bool;

	public StateableBoolean(State state) {
		switch (state) {
			case TRUE -> {
				bool = Boolean.TRUE;
			}
			case FALSE -> {
				bool = Boolean.FALSE;
			}
			default -> {
				bool = null;
			}
		}
	}

	public boolean isSet() {
		return this.bool != null;
	}

	public boolean get() {
		return this.bool;
	}

	public Optional<Boolean> getOptional() {
		if (this.bool == null) return Optional.empty();
		else return Optional.of(this.bool);
	}

	public void set(boolean bool) {
		this.bool = bool;
	}

	public enum State {
		TRUE,
		FALSE,
		NOT_SET
	}
}
