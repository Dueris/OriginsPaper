package me.dueris.genesismc.core.api.entity;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class OriginPlayer {

    public static boolean hasOrigin(Player player){
        PersistentDataContainer data = player.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if(origintag.equalsIgnoreCase("")) {return false;}
        else{
            return true;
        }
    }


}
