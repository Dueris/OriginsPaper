package me.dueris.genesismc.events;

import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class AttributeExecuteEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    Attribute attribute;
    String powerdata;
    OriginContainer origin;

    public AttributeExecuteEvent(@NotNull Player who, Attribute attribute, String powerdata, OriginContainer origin) {
        super(who);
        this.attribute = attribute;
        this.powerdata = powerdata;
        this.origin = origin;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Attribute getAttribute(){
        return attribute;
    }

    public String getPowerData(){
        return powerdata;
    }

    public OriginContainer getOrigin() {
        return origin;
    }

    @Override
    public String toString() {
        return "AttributeExecuteEvent{" +
                "attribute=" + attribute +
                ", powerdata='" + powerdata + '\'' +
                ", origin=" + origin +
                ", player=" + player +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
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
