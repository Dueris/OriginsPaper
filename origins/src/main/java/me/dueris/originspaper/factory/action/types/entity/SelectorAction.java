package me.dueris.originspaper.factory.action.types.entity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import io.github.dueris.calio.util.ArgumentWrapper;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.factory.action.ActionFactory;
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

	public static void action(DeserializedFactoryJson data, @NotNull Entity entity) {

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
		} catch (CommandSyntaxException ignored) {}

	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("selector_action"),
			InstanceDefiner.instanceDefiner()
				.add("selector", ApoliDataTypes.ENTITIES_SELECTOR)
				.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION)
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null),
			SelectorAction::action
		);
	}
}
