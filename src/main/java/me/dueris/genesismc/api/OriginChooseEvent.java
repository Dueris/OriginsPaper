package me.dueris.genesismc.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class OriginChooseEvent extends PlayerEvent implements Cancellable {
    protected Player player;

    public OriginChooseEvent(@NotNull Player who, boolean async) {
        super(who, async);
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancel) {

    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return null;
    }


    /**
     * Returns the chosen origin
     */
    public void getOrigin(){



    }

    /**
     * Sets the origin of the given player
     */
    public void setOrigin(Integer originid){

    }


}
