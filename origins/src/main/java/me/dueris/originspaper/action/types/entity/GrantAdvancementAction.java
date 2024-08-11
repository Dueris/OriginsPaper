package me.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.action.ActionFactory;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class GrantAdvancementAction {

	public static void action(@NotNull DeserializedFactoryJson data, @NotNull Entity entity) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement grant $1 only $2"
			.replace("$1", entity.getName().getString())
			.replace("$2", data.getString("advancement")));
	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("grant_advancement"),
			InstanceDefiner.instanceDefiner()
				.add("advancement", SerializableDataTypes.IDENTIFIER, null),
			GrantAdvancementAction::action
		);
	}
}
