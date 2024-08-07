package me.dueris.originspaper.factory.powers;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.data.types.HudRender;
import me.dueris.originspaper.data.types.Keybind;
import me.dueris.originspaper.event.KeybindTriggerEvent;
import me.dueris.originspaper.factory.action.ActionFactory;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import me.dueris.originspaper.registry.registries.PowerType;
import me.dueris.originspaper.util.KeybindUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class ActiveSelfPower extends PowerType implements CooldownInterface {
	private final ActionFactory<Entity> entityAction;
	private final int cooldown;
	private final HudRender hudRender;
	private final Keybind keybind;

	public ActiveSelfPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
						   ActionFactory<Entity> entityAction, int cooldown, HudRender hudRender, Keybind keybind) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityAction = entityAction;
		this.cooldown = cooldown;
		this.hudRender = hudRender;
		this.keybind = keybind;
	}

	public static InstanceDefiner buildDefiner() {
		return PowerType.buildDefiner().typedRegistry(OriginsPaper.apoliIdentifier("active_self"))
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
			if (KeybindUtil.isKeyActive(keybind.key(), e.getPlayer())) {
				entityAction.accept(player);
				if (cooldown > 1) {
					CooldownPower.addCooldown(e.getPlayer(), cooldown, this);
				}
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
