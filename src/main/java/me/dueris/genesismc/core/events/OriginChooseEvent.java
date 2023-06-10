package me.dueris.genesismc.core.events;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.enums.OriginType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OriginChooseEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();


    public OriginChooseEvent(@NotNull Player who) {
        super(who);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
