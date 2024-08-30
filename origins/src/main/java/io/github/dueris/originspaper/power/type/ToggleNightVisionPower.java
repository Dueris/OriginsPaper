package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Keybind;
import io.github.dueris.originspaper.event.KeybindTriggerEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ToggleNightVisionPower extends NightVisionPower {
	private final Keybind keybind;
	private final boolean activeByDefault;

	private final Map<Player, Boolean> playerToggledStates = new HashMap<>();
	private final List<Player> TICKED = new LinkedList<>();

	public ToggleNightVisionPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								  float strength, boolean activeByDefault, Keybind keybind) {
		super(key, type, name, description, hidden, condition, loadingPriority, strength);
		this.activeByDefault = activeByDefault;
		this.keybind = keybind;
	}

	public static SerializableData getFactory() {
		return NightVisionPower.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("toggle_night_vision"))
			.add("active_by_default", SerializableDataTypes.BOOLEAN, false)
			.add("key", ApoliDataTypes.KEYBIND, Keybind.DEFAULT_KEYBIND);
	}

	@EventHandler
	public void onKey(@NotNull KeybindTriggerEvent e) {
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (e.getKey().equalsIgnoreCase(keybind.key()) && getPlayers().contains(player) && !TICKED.contains(player)) {
			boolean currentState = playerToggledStates.getOrDefault(player, activeByDefault);
			playerToggledStates.put(player, !currentState);
			TICKED.add(player);
			new BukkitRunnable() {
				@Override
				public void run() {
					TICKED.remove(player);
				}
			}.runTaskLater(OriginsPaper.getPlugin(), 1);
		}
	}

	@Override
	public boolean isActive(@NotNull Entity player) {
		boolean isToggled = playerToggledStates.getOrDefault(player, activeByDefault);
		return isToggled && super.isActive(player);
	}

	public Keybind getKeybind() {
		return keybind;
	}
}
