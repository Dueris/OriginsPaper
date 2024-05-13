package me.dueris.genesismc.factory.powers.apoli.provider.origins;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.apoli.provider.PowerProvider;
import me.dueris.genesismc.util.entity.PowerHolderComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class LikeWater implements Listener, PowerProvider {
	private static final AttributeModifier modifier = new AttributeModifier("LikeWater", -1, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
	protected static NamespacedKey powerReference = GenesisMC.originIdentifier("like_water");

	@Override
	public void tick(Player p) {
		if (!PowerHolderComponent.hasPower(p, powerReference.asString())) return;
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
