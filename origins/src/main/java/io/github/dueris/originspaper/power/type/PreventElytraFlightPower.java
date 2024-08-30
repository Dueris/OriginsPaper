package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.jetbrains.annotations.NotNull;

public class PreventElytraFlightPower extends PowerType {
	private final ActionTypeFactory<Entity> entityAction;

	public PreventElytraFlightPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
									ActionTypeFactory<Entity> entityAction) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityAction = entityAction;
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("prevent_elytra_flight"))
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null);
	}

	public void executeAction(Entity entity) {

		if (entityAction != null) {
			entityAction.accept(entity);
		}

	}

	@EventHandler
	public void toggleElytra(@NotNull EntityToggleGlideEvent e) {
		if (e.getEntity() instanceof org.bukkit.entity.Player p && e.isGliding()) {
			Player player = ((CraftPlayer) p).getHandle();
			if (getPlayers().contains(player) && isActive(player)) {
				executeAction(player);
				e.setCancelled(true);
			}
		}
	}
}
