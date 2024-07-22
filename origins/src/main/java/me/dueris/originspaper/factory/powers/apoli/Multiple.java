package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.AccessorKey;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.parse.CalioJsonParser;
import me.dueris.calio.util.holders.Pair;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class Multiple extends PowerType {
	private final List<PowerType> subPowers = new ArrayList<>();

	public Multiple(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		super(name, description, hidden, condition, loading_priority);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data)
			.ofNamespace(OriginsPaper.apoliIdentifier("multiple"));
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
				PowerType type = (PowerType) CalioJsonParser.init(
					new Pair<>(getSource().get(k).getAsJsonObject(), ResourceLocation.parse(this.key().toString() + "_" + k.toLowerCase())),
					new AccessorKey("powers", this.getLoadingPriority(), true, Registries.CRAFT_POWER, PowerType.class)
				);
				if (type != null) {
					subPowers.add(type);
				}
			}
		});
	}
}
