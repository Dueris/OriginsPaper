package me.dueris.genesismc.factory.powers.value_modifying;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_jump;

public class ModifyJumpPower extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }


    @EventHandler
    public void ruDn(PlayerJumpEvent e) {
        Player p = e.getPlayer();
        if (modify_jump.contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (conditionExecutor.check("condition", "conditions", p, power, "origins:modify_jump", p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                        for (HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifiers")) {
                            if (modifier.get("value") instanceof Number) {
                                double modifierValue = ((Number) modifier.get("value")).doubleValue();
                                int jumpBoostLevel = (int) ((modifierValue - 1.0) * 2.0);

                                if (jumpBoostLevel >= 0) {
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20, jumpBoostLevel, true, false));
                                    setActive(power.getTag(), true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public ModifyJumpPower() {

    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "origins:modify_jump";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_jump;
    }
}
