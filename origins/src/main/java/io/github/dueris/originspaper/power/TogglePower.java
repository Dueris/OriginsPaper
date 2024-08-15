package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
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
import java.util.List;

public class TogglePower extends PowerType {
	private final boolean retainState;
	private final Keybind keybind;

	private final List<Player> TICKED = new ArrayList<>();
	private boolean toggled;

	public TogglePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
					   boolean activeByDefault, boolean retainState, Keybind keybind) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.retainState = retainState;
		this.keybind = keybind;
		this.toggled = activeByDefault;
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

		if (!super.isActive(player) && this.toggled) {
			this.toggled = false;
		}
	}

	@EventHandler
	public void onKey(@NotNull KeybindTriggerEvent e) {
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (e.getKey().equalsIgnoreCase(keybind.key()) && getPlayers().contains(player) && !TICKED.contains(player)) {
			this.toggled = !this.toggled;
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
		return this.toggled && super.isActive(player);
	}

	public Keybind getKeybind() {
		return keybind;
	}
}
