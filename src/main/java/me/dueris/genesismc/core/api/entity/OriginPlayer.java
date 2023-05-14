package me.dueris.genesismc.core.api.entity;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.choosing.contents.origins.ExpandedOriginContent;
import me.dueris.genesismc.core.choosing.contents.origins.OriginalOriginContent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class OriginPlayer {

    public static boolean hasChosenOrigin(Player player){
        PersistentDataContainer data = player.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if(origintag.equalsIgnoreCase("")) {return false;}
        else{
            return true;
        }
    }

    public static boolean hasOrigin(Player player, String origintag){
        PersistentDataContainer data = player.getPersistentDataContainer();
        @Nullable String origin = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if(origin.equalsIgnoreCase("")) return false;
        if(origin.contains(origintag)) {
            return true;
        }
        return false;
    }

    public static String getOriginTag(Player player){
        PersistentDataContainer data = player.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        return origintag;
    }

    public static void removeOrigin(Player player){
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "");
    }

    public static boolean hasCoreOrigin(Player player){
        PersistentDataContainer data = player.getPersistentDataContainer();
        @Nullable String origintagPlayer = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if(origintagPlayer.contains("genesis:origin-human")){
            return true;
        }else if(origintagPlayer.contains("genesis:origin-enderian")){
            return true;
        }else if(origintagPlayer.contains("genesis:origin-merling")){
            return true;
        }else if(origintagPlayer.contains("genesis:origin-phantom")){
            return true;
        }else if(origintagPlayer.contains("genesis:origin-elytrian")){
            return true;
        }else if(origintagPlayer.contains("genesis:origin-blazeborn")){
            return true;
        }else if(origintagPlayer.contains("genesis:origin-avian")){
            return true;
        }else if(origintagPlayer.contains("genesis:origin-arachnid")){
            return true;
        }else if(origintagPlayer.contains("genesis:origin-shulk")){
            return true;
        }else if(origintagPlayer.contains("genesis:origin-feline")){
            return true;
        }else if(origintagPlayer.contains("genesis:origin-starborne")){
            return true;
        }else if(origintagPlayer.contains("genesis:origin-allay")){
            return true;
        }else if(origintagPlayer.contains("genesis:origin-rabbit")){
            return true;
        }else if(origintagPlayer.contains("genesis:origin-bee")){
            return true;
        }else if(origintagPlayer.contains("genesis:origin-sculkling")){
            return true;
        }else if(origintagPlayer.contains("genesis:origin-creep")){
            return true;
        }else if(origintagPlayer.contains("genesis:origin-slimeling")){
            return true;
        }else if(origintagPlayer.contains("genesis:origin-piglin")){
            return true;
        }else{
            return false;
        }
    }

    public static void setOrigin(Player player, String origin){
        player.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, origin);
    }

}
