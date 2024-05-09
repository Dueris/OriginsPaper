package me.dueris.calio.builder.inst;

import com.google.common.base.Preconditions;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentLinkedQueue;

public class FactoryData {
	private final ConcurrentLinkedQueue<FactoryDataDefiner> providers;
	private NamespacedKey identifier;

	public FactoryData() {
		this.providers = new ConcurrentLinkedQueue<>();
	}

	@NotNull
	public FactoryDataDefiner[] getProviders() {
		return providers.toArray(new FactoryDataDefiner[0]);
	}

	public FactoryData add(FactoryDataDefiner instance) {
		Preconditions.checkArgument(instance != null, "Provided instance cannot be null!");
		this.providers.add(instance);
		return this;
	}

	public <T> FactoryData add(String objName, Class<T> type, T defaultVal) {
		Preconditions.checkArgument(objName != null);
		Preconditions.checkArgument(type != null);
		Preconditions.checkArgument(defaultVal != null);
		this.providers.add(new FactoryDataDefiner(objName, type, defaultVal));
		return this;
	}

	public FactoryData ofNamespace(NamespacedKey identifier) {
		this.identifier = identifier;
		return this;
	}

	public NamespacedKey getIdentifier() {
		return identifier;
	}
}
