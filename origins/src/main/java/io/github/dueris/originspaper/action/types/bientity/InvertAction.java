package io.github.dueris.originspaper.action.types.bientity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class InvertAction {

	public static void action(@NotNull SerializableData.Instance data, @NotNull Tuple<Entity, Entity> actorAndTarget) {
		data.<Consumer<Tuple<Entity, Entity>>>get("action").accept(new Tuple<>(actorAndTarget.getB(), actorAndTarget.getA()));
	}

	public static @NotNull ActionTypeFactory<Tuple<Entity, Entity>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("invert"),
			SerializableData.serializableData()
				.add("action", ApoliDataTypes.BIENTITY_ACTION),
			InvertAction::action
		);
	}
}
