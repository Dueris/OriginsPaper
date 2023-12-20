package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.CooldownManager;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.events.KeybindTriggerEvent;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static me.dueris.genesismc.utils.KeybindUtils.isKeyBeingPressed;

import java.util.ArrayList;
import java.util.HashMap;

public class ActiveSelf extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void k(KeybindTriggerEvent e) {
        for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
            if (getPowerArray().contains(e.getPlayer())) {
                ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (CooldownManager.isPlayerInCooldown(e.getPlayer(), e.getKey())) return;
                    if (executor.check("condition", "conditions", e.getPlayer(), power, getPowerFile(), e.getPlayer(), null, null, null, null, null)) {
                        if (!getPowerArray().contains(e.getPlayer())) return;
                        setActive(e.getPlayer(), power.getTag(), true);
                        if (isKeyBeingPressed(e.getPlayer(), power.getKey().getOrDefault("key", "key.origins.primary_active").toString(), true)) {
                            Actions.EntityActionType(e.getPlayer(), power.getEntityAction());
                            if (power.get("cooldown", "1") != null) {
                                CooldownManager.addCooldown(e.getPlayer(), origin, power.getTag(), power.getType(), Integer.parseInt(power.get("cooldown", power.get("max", "1"))), e.getKey());
                            }
                        }
                    } else {
                        setActive(e.getPlayer(), power.getTag(), false);
                    }

                }

            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:active_self";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return active_self;
    }

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if(powers_active.containsKey(p)){
            if(powers_active.get(p).containsKey(tag)){
                powers_active.get(p).replace(tag, bool);
            }else{
                powers_active.get(p).put(tag, bool);
            }
        }else{
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }
}
