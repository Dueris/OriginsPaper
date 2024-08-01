package me.dueris.originspaper.factory.actions.types.bientity;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.ActionFactory;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;

public class InvertAction {

	public static void action(DeserializedFactoryJson data, Tuple<Entity, Entity> actorAndTarget) {
		data.<Consumer<Tuple<Entity, Entity>>>get("action").accept(new Tuple<>(actorAndTarget.getB(), actorAndTarget.getA()));
	}

	public static ActionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ActionFactory<>(
			OriginsPaper.apoliIdentifier("invert"),
			InstanceDefiner.instanceDefiner()
				.add("action", ApoliDataTypes.BIENTITY_ACTION),
			InvertAction::action
		);
	}
}
