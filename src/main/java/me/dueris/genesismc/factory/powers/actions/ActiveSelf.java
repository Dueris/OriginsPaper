package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.CooldownManager;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.events.KeybindTriggerEvent;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.utils.KeybindUtils.isKeyBeingPressed;

public class ActiveSelf extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void k(KeybindTriggerEvent e) {
        for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
            if (getPowerArray().contains(e.getPlayer())) {
                ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
                    if (CooldownManager.isPlayerInCooldown(e.getPlayer(), e.getKey())) return;
                    if (executor.check("condition", "conditions", e.getPlayer(), power, getPowerFile(), e.getPlayer(), null, null, null, null, null)) {
                        if (!getPowerArray().contains(e.getPlayer())) return;
                        setActive(e.getPlayer(), power.getTag(), true);
                        if (isKeyBeingPressed(e.getPlayer(), power.get("key").getOrDefault("key", "key.origins.primary_active").toString(), true)) {
                            Actions.EntityActionType(e.getPlayer(), power.getEntityAction());
                            if (power.getObjectOrDefault("cooldown", 1) != null) {
                                CooldownManager.addCooldown(e.getPlayer(), Utils.getNameOrTag(power.getName(), power.getTag()), power.getType(), power.getIntOrDefault("cooldown", power.getIntOrDefault("max", 1)), e.getKey());
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
