package me.dueris.originspaper.factory.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.ActionFactory;
import me.dueris.originspaper.factory.data.types.modifier.Modifier;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ModifyResourceAction {

	public static void action(DeserializedFactoryJson data, Entity entity) {
		// TODO
	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(OriginsPaper.apoliIdentifier("modify_resource"),
			InstanceDefiner.instanceDefiner()
				.add("modifier", Modifier.DATA_TYPE)
				.add("resource", SerializableDataTypes.IDENTIFIER),
			ModifyResourceAction::action
		);
	}
}
