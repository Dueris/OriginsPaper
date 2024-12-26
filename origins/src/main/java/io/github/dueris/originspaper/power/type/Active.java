package io.github.dueris.originspaper.power.type;

import org.bukkit.event.Listener;

import java.util.Objects;

public interface Active extends Listener {

	void onUse();

	Key getKey();

	default void setKey(Key key) {

	}

	default boolean canTrigger() {
		return true;
	}

	default boolean canUse() {
		return true;
	}

	class Key {

		public String key = "key.origins.primary_active";
		public boolean continuous = false;

		@Override
		public boolean equals(final Object obj) {
			if (obj == this)
				return true;

			if (!(obj instanceof Active.Key otherKey))
				return false;

			return otherKey.key.equals(this.key) && otherKey.continuous == this.continuous;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.key, this.continuous);
		}
	}

}

