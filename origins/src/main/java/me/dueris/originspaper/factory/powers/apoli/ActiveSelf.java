package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.KeybindTriggerEvent;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.data.types.Keybind;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.util.KeybindUtil;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public class ActiveSelf extends PowerType implements KeyedPower, CooldownPower {
	private final FactoryJsonObject entityAction;
	private final int cooldown;
	private final HudRender hudRender;
	private final Keybind keybind;

	public ActiveSelf(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject entityAction, int cooldown, FactoryJsonObject hudRender, FactoryElement key) {
		super(name, description, hidden, condition, loading_priority);
		this.entityAction = entityAction;
		this.cooldown = cooldown;
		this.hudRender = HudRender.createHudRender(hudRender);
		this.keybind = Keybind.createJsonKeybind(key);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("active_self"))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("cooldown", int.class, 1)
			.add("hud_render", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("key", FactoryElement.class, new FactoryElement(new Gson().fromJson("{\"key\": \"key.origins.primary_active\"}", JsonElement.class)));
	}

	@EventHandler
	public void k(@NotNull KeybindTriggerEvent e) {
		if (getPlayers().contains(e.getPlayer())) {
			if (Cooldown.isInCooldown(e.getPlayer(), this)) return;
			if (isActive(e.getPlayer())) {
				if (KeybindUtil.isKeyActive(getJsonKey().key(), e.getPlayer())) {
					Actions.executeEntity(e.getPlayer(), entityAction);
					if (cooldown > 1) {
						Cooldown.addCooldown(e.getPlayer(), cooldown, this);
					}
				}
			}

		}
	}

	@Override
	public int getCooldown() {
		return cooldown;
	}

	@Override
	public Keybind getJsonKey() {
		return keybind;
	}

	@Override
	public HudRender getHudRender() {
		return hudRender;
	}
}
