package me.dueris.genesismc.events;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.Power;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.ActivationRange;

import java.util.ArrayList;

public class PowerUnassignEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    ArrayList<Class<? extends CraftPower>> powers;
    ArrayList<String> types;
    OriginContainer origin;

    public PowerUnassignEvent(@NotNull Player who, ArrayList<Class<? extends CraftPower>> powers, ArrayList<String> types, OriginContainer origin) {
        super(who);
        this.powers = powers;
        this.types = types;
        this.origin = origin;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public OriginContainer getOrigin() {
        return origin;
    }

    public ArrayList<Class<? extends CraftPower>> getPowers() {
        return powers;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    @Override
    public String toString() {
        return "PowerUnassignEvent{" +
                "powers=" + powers +
                ", types=" + types +
                ", origin=" + origin +
                ", player=" + player +
                '}';
    }

    @Override
    public @NotNull String getEventName() {
        return super.getEventName();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean callEvent() {
        return super.callEvent();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
