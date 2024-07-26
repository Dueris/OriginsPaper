package me.dueris.calio.util;

import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClipContextUtils {

	@Contract(pure = true)
	public static Fluid getFluidHandling(@NotNull String string) {
		String var1 = string.toLowerCase();
		switch (var1) {
			case "any":
				return Fluid.ANY;
			case "source_only":
				return Fluid.SOURCE_ONLY;
			default:
				return Fluid.NONE;
		}
	}

	@Contract(pure = true)
	public static @Nullable Block getShapeType(@NotNull String string) {
		String var1 = string.toLowerCase();
		switch (var1) {
			case "collider":
				return Block.COLLIDER;
			case "outline":
				return Block.OUTLINE;
			case "visual":
				return Block.VISUAL;
			default:
				return null;
		}
	}
}
