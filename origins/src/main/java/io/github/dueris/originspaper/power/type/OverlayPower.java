package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.power.factory.PowerType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorldBorder;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;

public class OverlayPower extends PowerType {
	private static CraftWorldBorder border = null;

	public OverlayPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("overlay"));
	}

	public static void init(@NotNull Player player) {
		if (border == null) {
			border = buildBorder();
		}
		border.setCenter(player.level().getWorld().getWorldBorder().getCenter());
		border.setSize(player.level().getWorld().getWorldBorder().getSize());
		((CraftPlayer) player.getBukkitEntity()).setWorldBorder(border);
	}

	public static void reset(@NotNull Player player) {
		((CraftPlayer) player.getBukkitEntity()).setWorldBorder(player.level().getWorld().getWorldBorder());
	}

	public static @NotNull CraftWorldBorder buildBorder() {
		CraftWorldBorder worldBorder = (CraftWorldBorder) Bukkit.createWorldBorder();
		worldBorder.setWarningDistance(999999999);
		return worldBorder;
	}

	@Override
	public void onRemoved(Player player) {
		reset(player);
	}

	@Override
	public void tick(Player player) {
		if (isActive(player)) {
			init(player);
		} else {
			reset(player);
		}
	}
}
