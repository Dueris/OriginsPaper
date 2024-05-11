package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.AccessorKey;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.annotations.ProvideJsonConstructor;
import me.dueris.calio.data.annotations.Register;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.parse.CalioJsonParser;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.registry.Registries;
import org.bukkit.NamespacedKey;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

@ProvideJsonConstructor
public class Multiple extends PowerType {
	private final JsonObject source;
	private final List<PowerType> subPowers = new ArrayList<>();

	@Register
	public Multiple(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, JsonObject source) {
		super(name, description, hidden, condition, loading_priority);
		this.source = source;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data)
			.ofNamespace(GenesisMC.apoliIdentifier("multiple"));
	}

	public JsonObject getSource() {
		return this.source;
	}

	public List<PowerType> getSubPowers() {
		return subPowers;
	}

	@Override
	public void bootstrap() {
		source.keySet().forEach(k -> {
			if (source.get(k).isJsonObject()) {
				PowerType type = (PowerType) CalioJsonParser.initilize(
					new Pair<>(source.get(k).getAsJsonObject(), NamespacedKey.fromString(this.getKey().asString() + "_" + k.toLowerCase())),
					new AccessorKey("powers", this.getLoadingPriority(), true, Registries.CRAFT_POWER, PowerType.class)
				);
				if (type != null) {
					subPowers.add(type);
				}
			}
		});
	}
}
