package me.dueris.api.entity;

import me.dueris.api.enums.OriginType;
import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.conversations.Conversable;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer; //im keeping this
import org.bukkit.entity.HumanEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.jetbrains.annotations.Nullable;

public interface OriginPlayer extends HumanEntity, Conversable, OfflinePlayer, PluginMessageRecipient, net.kyori.adventure.identity.Identified, com.destroystokyo.paper.network.NetworkClient{

    public default String getOriginTAG(){

        @Nullable String origintag = getPlayer().getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);

        return origintag;
    }

    public default void setOriginTAG(OriginType originType){

        if(originType.equals(OriginType.HUMAN)){

        }

    }




}
