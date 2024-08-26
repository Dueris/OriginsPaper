package io.github.dueris.originspaper.power;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.event.PlayerHitGroundEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class ActionOnLandPower extends PowerType {
	private final ActionTypeFactory<Entity> entityAction;

	public ActionOnLandPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority, ActionTypeFactory<Entity> entityAction) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityAction = entityAction;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("action_on_land"))
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null);
	}

	@EventHandler
	public void onLand(@NotNull PlayerHitGroundEvent e) {
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (getPlayers().contains(player) && isActive(player)) {
			entityAction.accept(player);
		}
	}
}
