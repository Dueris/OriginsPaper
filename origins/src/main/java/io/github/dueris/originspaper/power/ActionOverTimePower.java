package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class ActionOverTimePower extends PowerType {
	private final int interval;
	private final ActionFactory<Entity> entityAction;
	private final ActionFactory<Entity> risingAction;
	private final ActionFactory<Entity> fallingAction;

	private Integer startTicks = null;
	private Integer endTicks = null;

	private boolean wasActive = false;

	public ActionOverTimePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
							   int interval, ActionFactory<Entity> entityAction, ActionFactory<Entity> risingAction, ActionFactory<Entity> fallingAction) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.interval = interval;
		this.entityAction = entityAction;
		this.risingAction = risingAction;
		this.fallingAction = fallingAction;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("action_over_time"))
			.add("interval", SerializableDataTypes.POSITIVE_INT, 20)
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("rising_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("falling_action", ApoliDataTypes.ENTITY_ACTION, null);
	}

	@Override
	public void tick(Player entity) {
		if (isActive(entity)) {

			if (startTicks == null) {

				startTicks = entity.tickCount % interval;
				endTicks = null;

				return;

			}

			if (entity.tickCount % interval != startTicks) {
				return;
			}

			if (!wasActive && risingAction != null) {
				risingAction.accept(entity);
			}

			if (entityAction != null) {
				entityAction.accept(entity);
			}

			wasActive = true;

		} else if (wasActive) {

			if (endTicks == null) {

				endTicks = entity.tickCount % interval;
				startTicks = null;

				return;

			}

			if (entity.tickCount % interval != endTicks) {
				return;
			}

			if (fallingAction != null) {
				fallingAction.accept(entity);
			}

			wasActive = false;

		}
	}
}
