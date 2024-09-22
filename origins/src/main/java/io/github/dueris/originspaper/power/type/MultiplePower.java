package io.github.dueris.originspaper.power.type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.dueris.calio.data.DataBuildDirective;
import io.github.dueris.calio.parser.CalioParser;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MultiplePower extends PowerType {
	private final List<PowerType> subPowers = new LinkedList<>();

	public MultiplePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("multiple"), PowerType.getFactory().getSerializableData());
	}

	@Override
	public void onBootstrap() {
		for (String key : sourceObject.keySet()) {
			JsonElement element = sourceObject.get(key);
			if (!element.isJsonObject()) continue;

			JsonObject jo = sourceObject.getAsJsonObject(key);
			DataBuildDirective<PowerType> dataBuildDirective = new DataBuildDirective<>(List.of("apoli", "origins"), "power", PowerTypeFactory.DATA, 0, null);
			PowerType type = CalioParser.parseFile(
				new Tuple<>(ResourceLocation.fromNamespaceAndPath(this.getId().getNamespace(), getId().getPath() + "_" + key.toLowerCase(Locale.getDefault())), jo.toString()), dataBuildDirective
			);
			if (type == null) {
				continue;
			}
			subPowers.add(type);
		}
	}

	public List<PowerType> getSubPowers() {
		return subPowers;
	}

}
