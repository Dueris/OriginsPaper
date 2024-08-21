package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.HudRender;
import io.github.dueris.originspaper.data.types.Keybind;
import io.github.dueris.originspaper.event.KeybindTriggerEvent;
import io.github.dueris.originspaper.util.KeybindUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ActiveSelfPower extends PowerType implements CooldownInterface {
	private final ActionFactory<Entity> entityAction;
	private final int cooldown;
	private final HudRender hudRender;
	private final Keybind keybind;
	private final List<Player> alreadyTicked = new ArrayList<>();

	public ActiveSelfPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
						   ActionFactory<Entity> entityAction, int cooldown, HudRender hudRender, Keybind keybind) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityAction = entityAction;
		this.cooldown = cooldown;
		this.hudRender = hudRender;
		this.keybind = keybind;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("active_self"))
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION)
			.add("cooldown", SerializableDataTypes.INT, 1)
			.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
			.add("key", ApoliDataTypes.KEYBIND, Keybind.DEFAULT_KEYBIND);
	}

	@EventHandler
	public void onKey(@NotNull KeybindTriggerEvent e) {
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (!getPlayers().contains(player)) return;
		if (isActive(player) && !CooldownPower.isInCooldown(e.getPlayer(), this)) {
			if (KeybindUtil.isKeyActive(keybind.key(), e.getPlayer()) && keybind.key().equalsIgnoreCase(e.getKey()) && !alreadyTicked.contains(player)) {
				entityAction.accept(player);
				if (cooldown > 1) {
					CooldownPower.addCooldown(e.getPlayer(), this);
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


	@Override
	public int getCooldown() {
		return cooldown;
	}

	@Override
	public HudRender getHudRender() {
		return hudRender;
	}
}
