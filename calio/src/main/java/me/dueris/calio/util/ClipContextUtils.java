package me.dueris.calio.util;

import net.minecraft.world.level.ClipContext;

public class ClipContextUtils {

    /**
     * Returns the corresponding ClipContext.Fluid enum value based on the given string.
     *
     * @param string the string representing the desired ClipContext.Fluid value
     *               (case insensitive)
     * @return the corresponding ClipContext.Fluid enum value, or null if the string
     * does not match any valid value
     */
    public static ClipContext.Fluid getFluidHandling(String string) {
        switch (string.toLowerCase()) {
            case "none" -> {
                return ClipContext.Fluid.NONE;
            }
            case "any" -> {
                return ClipContext.Fluid.ANY;
            }
            case "source_only" -> {
                return ClipContext.Fluid.SOURCE_ONLY;
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Returns the corresponding ClipContext.Block value based on the input string.
     *
     * @param string the string representing the shape type
     * @return the corresponding ClipContext.Block value, or null if the input string is not recognized
     */
    public static ClipContext.Block getShapeType(String string) {
        switch (string.toLowerCase()) {
            case "collider" -> {
                return ClipContext.Block.COLLIDER;
            }
            case "outline" -> {
                return ClipContext.Block.OUTLINE;
            }
            case "visual" -> {
                return ClipContext.Block.VISUAL;
            }
            default -> {
                return null;
            }
        }
    }
}
