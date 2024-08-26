package io.github.dueris.originspaper.action.types.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.calio.util.ArgumentWrapper;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class SelectorAction {

	public static void action(SerializableData.Instance data, @NotNull Entity entity) {

		MinecraftServer server = entity.level().getServer();
		if (server == null) return;

		EntitySelector selector = data.<ArgumentWrapper<EntitySelector>>get("selector").get();
		Predicate<Tuple<Entity, Entity>> biEntityCondition = data.get("bientity_condition");
		Consumer<Tuple<Entity, Entity>> biEntityAction = data.get("bientity_action");

		CommandSourceStack source = new CommandSourceStack(
			CommandSource.NULL,
			entity.position(),
			entity.getRotationVector(),
			(ServerLevel) entity.level(),
			2,
			entity.getScoreboardName(),
			entity.getName(),
			server,
			entity
		);

		try {
			selector.findEntities(source)
				.stream()
				.filter(e -> biEntityCondition == null || biEntityCondition.test(new Tuple<>(entity, e)))
				.forEach(e -> biEntityAction.accept(new Tuple<>(entity, e)));
		} catch (CommandSyntaxException ignored) {
		}

	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("selector_action"),
			SerializableData.serializableData()
				.add("selector", ApoliDataTypes.ENTITIES_SELECTOR)
				.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
			SelectorAction::action
		);
	}
}
