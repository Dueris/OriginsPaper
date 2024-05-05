package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class ModifyJumpPower extends CraftPower implements Listener {

	@EventHandler
	public void ruDn(PlayerJumpEvent e) {
		Player p = e.getPlayer();
		if (modify_jump.contains(p)) {
			for (Layer layer : CraftApoli.getLayersFromRegistry()) {
				for (Power power : OriginPlayerAccessor.getPowers(p, getType(), layer)) {
					if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
						for (Modifier modifier : power.getModifiers()) {
							float modifierValue = modifier.value();
							/*((modifierValue - 1.0) * 2.0)*/
							int jumpBoostLevel = Math.round(modifierValue * 4);

							if (jumpBoostLevel >= 0) {
								p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 20, jumpBoostLevel, false, false, false));
								setActive(p, power.getTag(), true);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public String getType() {
		return "apoli:modify_jump";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return modify_jump;
	}
}
