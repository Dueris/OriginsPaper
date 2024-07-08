package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.annotations.DontRegister;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.factory.data.types.Modifier;
import me.dueris.originspaper.factory.powers.holder.PowerType;

@DontRegister
public class ModifierPower extends PowerType {
	private final Modifier[] modifiers;

	public ModifierPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject modifier, FactoryJsonArray modifiers) {
		super(name, description, hidden, condition, loading_priority);
		this.modifiers = Modifier.getModifiers(modifier, modifiers);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data)
			.add("modifier", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("modifiers", FactoryJsonArray.class, new FactoryJsonArray(new JsonArray()));
	}

	public Modifier[] getModifiers() {
		return modifiers;
	}
}
