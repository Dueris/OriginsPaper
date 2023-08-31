package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.CooldownStuff;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.events.KeybindTriggerEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

import static me.dueris.genesismc.KeybindHandler.isKeyBeingPressed;

public class ActiveSelf extends CraftPower implements Listener {
    @Override
    public void run() {

    }

    @EventHandler
    public void k(KeybindTriggerEvent e) {
        for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
            if (getPowerArray().contains(e.getPlayer())) {
                ConditionExecutor executor = new ConditionExecutor();
                if (CooldownStuff.isPlayerInCooldown(e.getPlayer(), e.getKey())) return;
                if (executor.check("condition", "conditions", e.getPlayer(), origin, getPowerFile(), e.getPlayer(), null, null, null, null, null)) {
                    if (!getPowerArray().contains(e.getPlayer())) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                    if (isKeyBeingPressed(e.getPlayer(), origin.getPowerFileFromType(getPowerFile()).getKey().get("key").toString(), true)) {
                        ActionTypes.EntityActionType(e.getPlayer(), origin.getPowerFileFromType(getPowerFile()).getEntityAction());
                        if (origin.getPowerFileFromType(getPowerFile()).get("cooldown", "1") != null) {
                            CooldownStuff.addCooldown(e.getPlayer(), origin.getPowerFileFromType(getPowerFile()).getTag(), Integer.parseInt(origin.getPowerFileFromType(getPowerFile()).get("cooldown", "1")), e.getKey());
                        }
                    }
                } else {
                    if (!getPowerArray().contains(e.getPlayer())) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
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
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }
}
