package me.dueris.genesismc.factory.powers.apoli.provider.origins;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.provider.PowerProvider;
import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class LikeWater extends CraftPower implements Listener, PowerProvider {
	private static final AttributeModifier modifier = new AttributeModifier("LikeWater", -1, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
	public static ArrayList<Player> likeWaterPlayers = new ArrayList<>();
	protected static NamespacedKey powerReference = GenesisMC.originIdentifier("like_water");

	@Override
	public void run(Player p, Power power) {
		if (this.getPlayersWithPower().contains(p) && p.isInWaterOrBubbleColumn() && !p.isSneaking()) {
			if (!p.getAttribute(Attribute.GENERIC_GRAVITY).getModifiers().contains(modifier)) {
				p.getAttribute(Attribute.GENERIC_GRAVITY).addTransientModifier(modifier);
			}
		} else {
			if (p.getAttribute(Attribute.GENERIC_GRAVITY).getModifiers().contains(modifier)) {
				p.getAttribute(Attribute.GENERIC_GRAVITY).removeModifier(modifier);
			}
		}
	}

	@Override
	public String getType() {
		return null;
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return likeWaterPlayers;
	}

}
