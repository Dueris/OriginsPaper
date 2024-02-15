package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.event.KeybindTriggerEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.util.KeybindingUtils.isKeyBeingPressed;

public class ActiveSelf extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void k(KeybindTriggerEvent e) {
        for (LayerContainer layer : CraftApoli.getLayers()) {
            if (getPowerArray().contains(e.getPlayer())) {
                ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
                    if (CooldownUtils.isPlayerInCooldown(e.getPlayer(), e.getKey())) return;
                    if (executor.check("condition", "conditions", e.getPlayer(), power, getPowerFile(), e.getPlayer(), null, null, null, null, null)) {
                        if (!getPowerArray().contains(e.getPlayer())) return;
                        setActive(e.getPlayer(), power.getTag(), true);
                        if (isKeyBeingPressed(e.getPlayer(), power.get("key").getOrDefault("key", "key.origins.primary_active").toString(), true)) {
                            Actions.EntityActionType(e.getPlayer(), power.getEntityAction());
                            if (power.getObjectOrDefault("cooldown", 1) != null) {
                                CooldownUtils.addCooldown(e.getPlayer(), Utils.getNameOrTag(power), power.getType(), power.getIntOrDefault("cooldown", power.getIntOrDefault("max", 1)), e.getKey());
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
        return "apoli:active_self";
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
