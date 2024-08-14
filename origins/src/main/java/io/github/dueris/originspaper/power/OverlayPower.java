package io.github.dueris.originspaper.power;

import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorldBorder;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;

public class OverlayPower extends PowerType {
	private static final CraftWorldBorder border;
	static {
		border = (CraftWorldBorder) Bukkit.createWorldBorder();
		border.setWarningDistance(999999999);
	}
	public OverlayPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}

	public static InstanceDefiner buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("overlay"));
	}

	@Override
	public void tick(Player player) {
		if (isActive(player)) {
			init(player);
		} else {
			reset(player);
		}
	}

	public static void init(@NotNull Player player) {
		border.setCenter(player.level().getWorld().getWorldBorder().getCenter());
		border.setSize(player.level().getWorld().getWorldBorder().getSize());
		((CraftPlayer) player.getBukkitEntity()).setWorldBorder(border);
	}

	public static void reset(@NotNull Player player) {
		((CraftPlayer) player.getBukkitEntity()).setWorldBorder(player.level().getWorld().getWorldBorder());
	}
}
