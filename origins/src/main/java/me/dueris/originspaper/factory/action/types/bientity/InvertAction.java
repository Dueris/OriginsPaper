package me.dueris.originspaper.factory.action.types.bientity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.factory.action.ActionFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class InvertAction {

	public static void action(@NotNull DeserializedFactoryJson data, @NotNull Tuple<Entity, Entity> actorAndTarget) {
		data.<Consumer<Tuple<Entity, Entity>>>get("action").accept(new Tuple<>(actorAndTarget.getB(), actorAndTarget.getA()));
	}

	public static @NotNull ActionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("invert"),
			InstanceDefiner.instanceDefiner()
				.add("action", ApoliDataTypes.BIENTITY_ACTION),
			InvertAction::action
		);
	}
}
