package io.github.dueris.originspaper.power.type.simple;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Player;

public class LikeWaterPower {
	private static final AttributeModifier modifier = new AttributeModifier(CraftNamespacedKey.fromMinecraft(OriginsPaper.originIdentifier("likewater")), -1, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
	private static final String cachedPowerRefrenceString = "origins:like_water";

	public static void tick(Player p) {
		if (!PowerHolderComponent.hasPower(p, cachedPowerRefrenceString)) {
			if (p.getAttribute(Attribute.GENERIC_GRAVITY).getModifiers().contains(modifier)) {
				p.getAttribute(Attribute.GENERIC_GRAVITY).removeModifier(modifier);
			}
			return;
		}
		if (p.isInWaterOrBubbleColumn() && !p.isSneaking()) {
			if (!p.getAttribute(Attribute.GENERIC_GRAVITY).getModifiers().contains(modifier)) {
				p.getAttribute(Attribute.GENERIC_GRAVITY).addTransientModifier(modifier);
			}
		} else {
			if (p.getAttribute(Attribute.GENERIC_GRAVITY).getModifiers().contains(modifier)) {
				p.getAttribute(Attribute.GENERIC_GRAVITY).removeModifier(modifier);
			}
		}
	}
}
