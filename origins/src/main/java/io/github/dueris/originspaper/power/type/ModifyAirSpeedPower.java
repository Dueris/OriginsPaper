package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.data.types.modifier.ModifierUtil;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModifyAirSpeedPower extends ModifierPower {
	private final Map<Player, Tuple<Float, Float>> original2Modified = new ConcurrentHashMap<>();

	public ModifyAirSpeedPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
							   @Nullable Modifier modifier, @Nullable List<Modifier> modifiers) {
		super(key, type, name, description, hidden, condition, loadingPriority, modifier, modifiers);
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("modify_air_speed"), ModifierPower.getFactory().getSerializableData());
	}

	@Override
	public void tick(Player player) {
		if (!original2Modified.containsKey(player)) {
			onAdded(player);
		}
		if (isActive(player)) {
			((CraftPlayer) player.getBukkitEntity()).setFlySpeed(original2Modified.get(player).getB());
		} else {
			((CraftPlayer) player.getBukkitEntity()).setFlySpeed(original2Modified.get(player).getA());
		}
	}

	/**
	 * We need to manually reset the flying val on leave to prevent anything from stacking
	 */
	@EventHandler
	public void onLeave(@NotNull PlayerQuitEvent e) {
		if (original2Modified.containsKey(((CraftPlayer) e.getPlayer()).getHandle())) {
			e.getPlayer().setFlySpeed(original2Modified.get(((CraftPlayer) e.getPlayer()).getHandle()).getA());
		}
	}

	@Override
	public void onAdded(@NotNull Player player) {
		CraftPlayer craftPlayer = (CraftPlayer) player.getBukkitEntity();
		double modified = ModifierUtil.applyModifiers(player, getModifiers(), craftPlayer.getFlySpeed());

		original2Modified.put(player, new Tuple<>(Util.range(-1F, 1F, craftPlayer.getFlySpeed()), Util.range(-1F, 1F, (float) modified)));

		craftPlayer.setFlySpeed(original2Modified.get(player).getB());
	}

	@Override
	public void onRemoved(@NotNull Player player) {
		CraftPlayer craftPlayer = (CraftPlayer) player.getBukkitEntity();
		craftPlayer.setFlySpeed(original2Modified.get(player).getA());
	}
}
