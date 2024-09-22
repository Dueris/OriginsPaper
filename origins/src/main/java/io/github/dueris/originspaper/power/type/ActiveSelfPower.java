package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.HudRender;
import io.github.dueris.originspaper.data.types.Keybind;
import io.github.dueris.originspaper.event.KeybindTriggerEvent;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import io.github.dueris.originspaper.util.KeybindUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class ActiveSelfPower extends CooldownPower {
	private final ActionTypeFactory<Entity> entityAction;
	private final Keybind keybind;
	private final List<Player> alreadyTicked = new LinkedList<>();

	public ActiveSelfPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
						   ActionTypeFactory<Entity> entityAction, int cooldown, HudRender hudRender, Keybind keybind) {
		super(key, type, name, description, hidden, condition, loadingPriority, hudRender, cooldown);
		this.entityAction = entityAction;
		this.keybind = keybind;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("active_self"), PowerType.getFactory().getSerializableData()
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION)
			.add("cooldown", SerializableDataTypes.INT, 1)
			.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
			.add("key", ApoliDataTypes.KEYBIND, Keybind.DEFAULT_KEYBIND));
	}

	@EventHandler
	public void onKey(@NotNull KeybindTriggerEvent e) {
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (!getPlayers().contains(player)) return;
		if (isActive(player) && canUse(player)) {
			if (KeybindUtil.isKeyActive(keybind.key(), e.getPlayer()) && keybind.key().equalsIgnoreCase(e.getKey()) && !alreadyTicked.contains(player)) {
				entityAction.accept(player);
				if (cooldown > 1) {
					use(player);
				}
				alreadyTicked.add(player);
				new BukkitRunnable() {
					@Override
					public void run() {
						alreadyTicked.remove(player);
					}
				}.runTaskLater(OriginsPaper.getPlugin(), 1);
			}
		}
	}

}
