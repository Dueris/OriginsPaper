package me.dueris.calio.util.holders;

import java.util.Optional;

public class StateableBoolean {
	private Boolean bool;

	public StateableBoolean(State state) {
		switch (state) {
			case TRUE:
				this.bool = Boolean.TRUE;
				break;
			case FALSE:
				this.bool = Boolean.FALSE;
				break;
			default:
				this.bool = null;
		}
	}

	public boolean isSet() {
		return this.bool != null;
	}

	public boolean get() {
		return this.bool;
	}

	public Optional<Boolean> getOptional() {
		return this.bool == null ? Optional.empty() : Optional.of(this.bool);
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
