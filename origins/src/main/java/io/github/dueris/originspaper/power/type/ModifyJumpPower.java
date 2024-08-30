package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.types.modifier.Modifier;
import io.github.dueris.originspaper.data.types.modifier.ModifierUtil;
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

import static org.bukkit.attribute.Attribute.GENERIC_JUMP_STRENGTH;

public class ModifyJumpPower extends ModifierPower {
	private final Map<Player, Tuple<Float, Float>> original2Modified = new ConcurrentHashMap<>();

	public ModifyJumpPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
						   @Nullable Modifier modifier, @Nullable List<Modifier> modifiers) {
		super(key, type, name, description, hidden, condition, loadingPriority, modifier, modifiers);
	}

	public static SerializableData getFactory() {
		return ModifierPower.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("modify_jump"));
	}

	@Override
	public void tick(Player player) {
		if (!original2Modified.containsKey(player)) {
			onAdded(player);
		}
		if (isActive(player)) {
			player.getBukkitEntity().getAttribute(GENERIC_JUMP_STRENGTH).setBaseValue(original2Modified.get(player).getB());
		} else {
			player.getBukkitEntity().getAttribute(GENERIC_JUMP_STRENGTH).setBaseValue(original2Modified.get(player).getA());
		}
	}

	/**
	 * We need to manually reset the jump-power val on leave to prevent anything from stacking
	 */
	@EventHandler
	public void onLeave(@NotNull PlayerQuitEvent e) {
		if (original2Modified.containsKey(((CraftPlayer) e.getPlayer()).getHandle())) {
			e.getPlayer().getAttribute(GENERIC_JUMP_STRENGTH).setBaseValue(original2Modified.get(((CraftPlayer) e.getPlayer()).getHandle()).getA());
		}
	}

	@Override
	public void onAdded(@NotNull Player player) {
		CraftPlayer craftPlayer = (CraftPlayer) player.getBukkitEntity();
		double modified = ModifierUtil.applyModifiers(player, getModifiers(), craftPlayer.getAttribute(GENERIC_JUMP_STRENGTH).getBaseValue());

		original2Modified.put(player, new Tuple<>(Util.range(-1F, 1F, (float) craftPlayer.getAttribute(GENERIC_JUMP_STRENGTH).getDefaultValue()), Util.range(-1F, 1F, (float) modified)));

		craftPlayer.getAttribute(GENERIC_JUMP_STRENGTH).setBaseValue(original2Modified.get(player).getB());
	}

	@Override
	public void onRemoved(@NotNull Player player) {
		CraftPlayer craftPlayer = (CraftPlayer) player.getBukkitEntity();
		craftPlayer.getAttribute(GENERIC_JUMP_STRENGTH).setBaseValue(original2Modified.get(player).getA());
	}
}
