package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;

public class Invisibility extends CraftPower {
    @Override
    public void run(Player p) {
        if (getPowerArray().contains(p)) {
            ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
            boolean shouldSetInvisible = false;

            for (LayerContainer layer : CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (conditionExecutor.check("condition", "conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getInventory().getItemInMainHand(), null)) {
                        shouldSetInvisible = true;
                        break;
                    }
                }
            }

            p.setInvisible(shouldSetInvisible || p.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY));
        } else {
            if (p.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY)) {
                return;
            }
            p.setInvisible(false);
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:invisibility";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return invisibility;
    }

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }
}
