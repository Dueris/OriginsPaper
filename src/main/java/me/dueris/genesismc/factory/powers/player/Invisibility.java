package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class Invisibility extends CraftPower {
    @Override
    public void run(Player p) {
        if (getPowerArray().contains(p)) {
            ConditionExecutor conditionExecutor = new ConditionExecutor();
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (conditionExecutor.check("condition", "conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getInventory().getItemInMainHand(), null)) {
                        p.setInvisible(true);
                    } else {
                        if (p.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY)) return;
                        p.setInvisible(false);
                    }
                }
            }
        } else {
            if (p.getActivePotionEffects().contains(PotionEffectType.INVISIBILITY)) return;
            p.setInvisible(false);
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:invisibility";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return invisibility;
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }
}
