package io.github.dueris.originspaper.action.type.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.util.ArgumentWrapper;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class SelectorActionType {

	public static void action(@NotNull Entity entity, EntitySelector selector, Consumer<Tuple<Entity, Entity>> biEntityAction, Predicate<Tuple<Entity, Entity>> biEntityCondition) {

		MinecraftServer server = entity.level().getServer();
		if (server == null) {
			return;
		}

		CommandSourceStack source = entity.createCommandSourceStack()
			.withSource(OriginsPaper.showCommandOutput ? entity : CommandSource.NULL)
			.withPermission(4);

		try {
			selector.findEntities(source)
				.stream()
				.map(selected -> new Tuple<>(entity, (Entity) selected))
				.filter(biEntityCondition)
				.forEach(biEntityAction);
		} catch (CommandSyntaxException ignored) {

		}

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("selector_action"),
			new SerializableData()
				.add("selector", ApoliDataTypes.ENTITIES_SELECTOR)
				.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
			(data, entity) -> action(entity,
				data.<ArgumentWrapper<EntitySelector>>get("selector").get(),
				data.get("bientity_action"),
				data.getOrElse("bientity_condition", actorAndTarget -> true)
			)
		);
	}

}
