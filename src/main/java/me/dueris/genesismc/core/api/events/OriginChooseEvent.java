package me.dueris.genesismc.core.api.events;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.api.enums.OriginType;
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
    
    public static HandlerList getHandlerList(){
        return handlers;
    }
    
   @Override
    public HandlerList getHandlers(){
        return handlers;
    }

    public String getOriginTAG() {

        @Nullable String origitag = getPlayer().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);

        return origitag;
    }

    public boolean isOriginType(OriginType originType){
        @Nullable String origitag = getPlayer().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if(originType.equals(OriginType.HUMAN)){
            return origitag.equalsIgnoreCase("genesis:origin-human");
        } else if(originType.equals(OriginType.ENDERIAN)){
            return origitag.equalsIgnoreCase("genesis:origin-enderian");
        } else if(originType.equals(OriginType.SHULK)){
            return origitag.equalsIgnoreCase("genesis:origin-shulk");
        } else if(originType.equals(OriginType.ARACHNID)){
            return origitag.equalsIgnoreCase("genesis:origin-arachnid");
        } else if(originType.equals(OriginType.CREEP)){
            return origitag.equalsIgnoreCase("genesis:origin-creep");
        } else if(originType.equals(OriginType.PHANTOM)){
            return origitag.equalsIgnoreCase("genesis:origin-phantom");
        } else if(originType.equals(OriginType.SLIMELING)){
            return origitag.equalsIgnoreCase("genesis:origin-slimeling");
        } else if(originType.equals(OriginType.VEXIAN)){
            return origitag.equalsIgnoreCase("genesis:origin-vexian");
        } else if(originType.equals(OriginType.BLAZEBORN)){
            return origitag.equalsIgnoreCase("genesis:origin-blazeborn");
        } else if(originType.equals(OriginType.STARBORNE)){
            return origitag.equalsIgnoreCase("genesis:origin-starborne");
        } else if(originType.equals(OriginType.MERLING)){
            return origitag.equalsIgnoreCase("genesis:origin-merling");
        } else if(originType.equals(OriginType.ALLAY)){
            return origitag.equalsIgnoreCase("genesis:origin-allay");
        } else if(originType.equals(OriginType.RABBIT)){
            return origitag.equalsIgnoreCase("genesis:origin-rabbit");
        } else if(originType.equals(OriginType.ELYTRIAN)){
            return origitag.equalsIgnoreCase("genesis:origin-elytrian");
        } else if(originType.equals(OriginType.PIGLIN)){
            return origitag.equalsIgnoreCase("genesis:origin-piglin");
        } else if(originType.equals(OriginType.AVIAN)){
            return origitag.equalsIgnoreCase("genesis:origin-avian");
        } else if(originType.equals(OriginType.SCULK)){
            return origitag.equalsIgnoreCase("genesis:origin-sculk");
        }
        return false;
    }
}
