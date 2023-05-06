package me.dueris.api.events;

import me.dueris.api.enums.OriginType;
import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OriginChooseEvent extends OriginEvent {

    public OriginChooseEvent(@NotNull Player who) {
        super(who);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return null;
    }

    public String getOriginTAG() {

        @Nullable String origitag = getPlayer().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);

        return origitag;
    }

    public boolean isOriginType(OriginType originType){
        @Nullable String origitag = getPlayer().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if(originType.equals(OriginType.HUMAN)){
            if(origitag.equalsIgnoreCase("genesis:origin-human")){
                return true;
            }
        } else if(originType.equals(OriginType.ENDERIAN)){
            if(origitag.equalsIgnoreCase("genesis:origin-enderian")){
                return true;
            }
        } else if(originType.equals(OriginType.SHULK)){
            if(origitag.equalsIgnoreCase("genesis:origin-shulk")){
                return true;
            }
        } else if(originType.equals(OriginType.ARACHNID)){
            if(origitag.equalsIgnoreCase("genesis:origin-arachnid")){
                return true;
            }
        } else if(originType.equals(OriginType.CREEP)){
            if(origitag.equalsIgnoreCase("genesis:origin-creep")){
                return true;
            }
        } else if(originType.equals(OriginType.PHANTOM)){
            if(origitag.equalsIgnoreCase("genesis:origin-phantom")){
                return true;
            }
        } else if(originType.equals(OriginType.SLIMELING)){
            if(origitag.equalsIgnoreCase("genesis:origin-slimeling")){
                return true;
            }
        } else if(originType.equals(OriginType.VEXIAN)){
            if(origitag.equalsIgnoreCase("genesis:origin-vexian")){
                return true;
            }
        } else if(originType.equals(OriginType.BLAZEBORN)){
            if(origitag.equalsIgnoreCase("genesis:origin-blazeborn")){
                return true;
            }
        } else if(originType.equals(OriginType.STARBORNE)){
            if(origitag.equalsIgnoreCase("genesis:origin-starborne")){
                return true;
            }
        } else if(originType.equals(OriginType.MERLING)){
            if(origitag.equalsIgnoreCase("genesis:origin-merling")){
                return true;
            }
        } else if(originType.equals(OriginType.ALLAY)){
            if(origitag.equalsIgnoreCase("genesis:origin-allay")){
                return true;
            }
        } else if(originType.equals(OriginType.RABBIT)){
            if(origitag.equalsIgnoreCase("genesis:origin-rabbit")){
                return true;
            }
        } else if(originType.equals(OriginType.ELYTRIAN)){
            if(origitag.equalsIgnoreCase("genesis:origin-elytrian")){
                return true;
            }
        } else if(originType.equals(OriginType.PIGLIN)){
            if(origitag.equalsIgnoreCase("genesis:origin-piglin")){
                return true;
            }
        } else if(originType.equals(OriginType.AVIAN)){
            if(origitag.equalsIgnoreCase("genesis:origin-avian")){
                return true;
            }
        } else if(originType.equals(OriginType.SCULK)){
            if(origitag.equalsIgnoreCase("genesis:origin-sculk")){
                return true;
            }
        }
        return false;
    }
}
