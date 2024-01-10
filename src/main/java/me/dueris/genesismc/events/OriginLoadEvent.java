package me.dueris.genesismc.events;

import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

@Deprecated(forRemoval = true)
public class OriginLoadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    OriginContainer origin;
    ArrayList<PowerContainer> powerContainers;
    LayerContainer layerContainer;
    File datapack;

    public OriginLoadEvent(OriginContainer origin, ArrayList<PowerContainer> powerContainers, File datapack) {
        this.origin = origin;
        this.powerContainers = powerContainers;
        this.layerContainer = layerContainer;
        this.datapack = datapack;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public OriginContainer getOrigin() {
        return origin;
    }

    public File getDatapack() {
        return datapack;
    }

    public LayerContainer getLayerContainer() {
        return layerContainer;
    }

    public ArrayList<PowerContainer> getPowerContainers() {
        return powerContainers;
    }

    @Override
    public @NotNull String getEventName() {
        return super.getEventName();
    }

    @Override
    public String toString() {
        return "OriginLoadEvent{" +
                "origin=" + origin +
                ", powerContainers=" + powerContainers.toString() +
                ", layerContainer=" + layerContainer +
                ", datapack=" + datapack +
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
