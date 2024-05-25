package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import me.dueris.calio.data.AccessorKey;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.parse.CalioJsonParser;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.registry.Registries;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.List;

public class Multiple extends PowerType {
	private final List<PowerType> subPowers = new ArrayList<>();

	public Multiple(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		super(name, description, hidden, condition, loading_priority);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data)
			.ofNamespace(GenesisMC.apoliIdentifier("multiple"));
	}

	public JsonObject getSource() {
		return super.sourceObject;
	}

	@Override
	public String getType() {
		return "apoli:multiple";
	}

	public List<PowerType> getSubPowers() {
		return subPowers;
	}

	@Override
	public void bootstrap() {
		getSource().keySet().forEach(k -> {
			if (getSource().get(k).isJsonObject()) {
				PowerType type = (PowerType) CalioJsonParser.initilize(
					new Pair<>(getSource().get(k).getAsJsonObject(), NamespacedKey.fromString(this.key().asString() + "_" + k.toLowerCase())),
					new AccessorKey("powers", this.getLoadingPriority(), true, Registries.CRAFT_POWER, PowerType.class)
				);
				if (type != null) {
					subPowers.add(type);
				}
			}
		});
	}
}
