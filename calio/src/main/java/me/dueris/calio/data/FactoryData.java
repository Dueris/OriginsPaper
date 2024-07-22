package me.dueris.calio.data;

import com.google.common.base.Preconditions;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.calio.data.types.RequiredInstance;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentLinkedQueue;

public class FactoryData {
	private final ConcurrentLinkedQueue<FactoryDataDefiner> providers = new ConcurrentLinkedQueue<>();
	private ResourceLocation identifier;

	@NotNull
	public FactoryDataDefiner[] getProviders() {
		return this.providers.toArray(new FactoryDataDefiner[0]);
	}

	public Object retrieve(String dataKey) {
		for (FactoryDataDefiner definer : this.providers) {
			if (definer.getObjName().equalsIgnoreCase(dataKey)) {
				return definer.getDefaultValue();
			}
		}

		return null;
	}

	public FactoryData add(FactoryDataDefiner instance) {
		Preconditions.checkArgument(instance != null, "Provided instance cannot be null!");
		this.providers.add(instance);
		return this;
	}

	public <T> FactoryData add(String objName, Class<T> type, T defaultVal) {
		Preconditions.checkArgument(objName != null);
		Preconditions.checkArgument(type != null);
		this.providers.add(new FactoryDataDefiner<>(objName, type, defaultVal));
		return this;
	}

	public <T> FactoryData add(String objName, Class<T> type, RequiredInstance defaultVal) {
		Preconditions.checkArgument(objName != null);
		Preconditions.checkArgument(type != null);
		this.providers.add(new FactoryDataDefiner<>(objName, type, defaultVal));
		return this;
	}

	public <T> FactoryData add(String objName, Class<T> type, OptionalInstance defaultVal) {
		Preconditions.checkArgument(objName != null);
		Preconditions.checkArgument(type != null);
		this.providers.add(new FactoryDataDefiner<>(objName, type, defaultVal));
		return this;
	}

	public FactoryData remove(String objName) {
		FactoryDataDefiner toRemove = null;

		for (FactoryDataDefiner provider : this.providers) {
			if (provider.getObjName().equalsIgnoreCase(objName)) {
				toRemove = provider;
				break;
			}
		}

		if (toRemove != null) {
			this.providers.remove(toRemove);
		}

		return this;
	}

	public FactoryData ofNamespace(ResourceLocation identifier) {
		this.identifier = identifier;
		return this;
	}

	public ResourceLocation getIdentifier() {
		return this.identifier;
	}

	@Override
	public String toString() {
		return "FactoryData :: [%N%] : DataDefiners: [%%%]".replace("%%%", this.providers.toString()).replace("%N%", this.identifier.toString());
	}
}
