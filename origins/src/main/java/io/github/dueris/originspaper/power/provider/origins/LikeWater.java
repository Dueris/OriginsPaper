package io.github.dueris.originspaper.power.provider.origins;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.provider.PowerProvider;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class LikeWater implements Listener, PowerProvider {
	private static final AttributeModifier modifier = new AttributeModifier("LikeWater", -1, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
	protected static ResourceLocation powerReference = OriginsPaper.originIdentifier("like_water");
	private static final String cachedPowerRefrenceString = powerReference.toString();

	@Override
	public void tick(Player p) {
		if (!PowerHolderComponent.hasPower(p, cachedPowerRefrenceString)) return;
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
