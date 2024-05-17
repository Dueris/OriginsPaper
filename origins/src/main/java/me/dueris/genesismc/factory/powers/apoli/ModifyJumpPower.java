package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.data.types.Modifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ModifyJumpPower extends ModifierPower implements Listener {

	public ModifyJumpPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject modifier, FactoryJsonArray modifiers) {
		super(name, description, hidden, condition, loading_priority, modifier, modifiers);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return ModifierPower.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("modify_jump"));
	}

	@EventHandler
	public void ruDn(PlayerJumpEvent e) {
		Player p = e.getPlayer();
		if (getPlayers().contains(p)) {
			if (isActive(p)) {
				for (Modifier modifier : getModifiers()) {
					float modifierValue = modifier.value();
					/*((modifierValue - 1.0) * 2.0)*/
					int jumpBoostLevel = Math.round(modifierValue * 4);

					if (jumpBoostLevel >= 0) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 20, jumpBoostLevel, false, false, false));
					}
				}
			}
		}
	}

}
