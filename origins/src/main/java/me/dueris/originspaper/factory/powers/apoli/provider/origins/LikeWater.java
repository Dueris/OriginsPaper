package me.dueris.originspaper.factory.powers.apoli.provider.origins;

import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.powers.apoli.provider.PowerProvider;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class LikeWater implements Listener, PowerProvider {
	private static final AttributeModifier modifier = new AttributeModifier("LikeWater", -1, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
	protected static NamespacedKey powerReference = OriginsPaper.originIdentifier("like_water");
	private static final String cachedPowerRefrenceString = powerReference.asString();

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
