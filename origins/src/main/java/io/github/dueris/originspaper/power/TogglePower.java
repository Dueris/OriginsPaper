package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TogglePower extends PowerType {
	private final boolean retainState;
	private final Keybind keybind;
	private final boolean activeByDefault;

	private final Map<Player, Boolean> playerToggledStates = new HashMap<>();
	private final List<Player> TICKED = new ArrayList<>();

	public TogglePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
					   boolean activeByDefault, boolean retainState, Keybind keybind) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.activeByDefault = activeByDefault;
		this.retainState = retainState;
		this.keybind = keybind;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("toggle"))
			.add("active_by_default", SerializableDataTypes.BOOLEAN, true)
			.add("retain_state", SerializableDataTypes.BOOLEAN, true)
			.add("key", ApoliDataTypes.KEYBIND, Keybind.DEFAULT_KEYBIND);
	}

	@Override
	public boolean shouldTick() {
		return !retainState && !(this.getCondition() == null);
	}

	@Override
	public void tick(Player player) {
		if (!super.isActive(player)) {
			if (playerToggledStates.getOrDefault(player, activeByDefault)) {
				playerToggledStates.put(player, false);
			}
		}
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
