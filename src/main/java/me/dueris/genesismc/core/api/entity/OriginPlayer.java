package me.dueris.genesismc.core.api.entity;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static me.dueris.genesismc.core.choosing.ChoosingCORE.*;
import static me.dueris.genesismc.core.choosing.ChoosingCORE.removeItemEnder;
import static org.bukkit.ChatColor.GRAY;

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
        if(player.getPersistentDataContainer() != null){
            PersistentDataContainer data = player.getPersistentDataContainer();
            data.set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "");
        }
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
        if(origin.contains("genesis:origin-human")){
            setAttributesToDefault(player);
            removeItemPhantom(player);
            removeItemEnder(player);
        } else if(origin.contains("genesis:origin-enderian")){
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(),()->{
                ItemStack infinpearl = new ItemStack(Material.ENDER_PEARL);
                ItemMeta pearl_meta = infinpearl.getItemMeta();
                pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
                ArrayList<String> pearl_lore = new ArrayList();
                pearl_meta.setUnbreakable(true);
                pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                pearl_meta.setLore(pearl_lore);
                infinpearl.setItemMeta(pearl_meta);
                player.getInventory().addItem(infinpearl);
                removeItemPhantom(player);
            },1);
        } else if(origin.contains("genesis:origin-shulk")){
            float walk = 0.185F;
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(),()->{
                player.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(8.0);
                player.setWalkSpeed(walk);
                player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.45F);
                player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(2.2);
                removeItemPhantom(player);
                removeItemEnder(player);
            },1);
        } else if(origin.contains("genesis:origin-arachnid")){
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(),()->{
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
                removeItemPhantom(player);
                removeItemEnder(player);
            },1);
        } else if(origin.contains("genesis:origin-creep")){
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(),()->{
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(18);
                removeItemPhantom(player);
                removeItemEnder(player);
            },1);
        } else if(origin.contains("genesis:origin-phantom")){
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(),()->{
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
                player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.11);
                ItemStack spectatorswitch = new ItemStack(Material.PHANTOM_MEMBRANE);
                ItemMeta switch_meta = spectatorswitch.getItemMeta();
                switch_meta.setDisplayName(GRAY + "Phantom Form");
                ArrayList<String> pearl_lore = new ArrayList();
                switch_meta.setUnbreakable(true);
                switch_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                switch_meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
                switch_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                switch_meta.setLore(pearl_lore);
                spectatorswitch.setItemMeta(switch_meta);
                player.getInventory().addItem(spectatorswitch);
                removeItemEnder(player);
            },1);
        } else if(origin.contains("genesis:origin-slimeling")){
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(),()->{
                removeItemPhantom(player);
                removeItemEnder(player);
            },1);
        } else if(origin.contains("genesis:origin-feline")){
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(),()->{
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(18);
                removeItemPhantom(player);
                removeItemEnder(player);
            },1);
        } else if(origin.contains("genesis:origin-blaze")){
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(),()->{
                removeItemPhantom(player);
                removeItemEnder(player);
            },1);
        } else if(origin.contains("genesis:origin-starborne")){
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(),()->{
                removeItemPhantom(player);
                removeItemEnder(player);
            },1);
        } else if(origin.contains("geneis:origin-merling")){
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(),()->{
                removeItemPhantom(player);
                removeItemEnder(player);
            },1);
        } else if(origin.contains("genesis:origin-allay")){
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(),()->{
                removeItemPhantom(player);
                removeItemEnder(player);
            },1);
        } else if(origin.contains("genesis:origin-rabbit")){
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(),()->{
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
                removeItemPhantom(player);
                removeItemEnder(player);
            },1);
        } else if(origin.contains("genesis:origin-bee")){
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(),()->{
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
                removeItemPhantom(player);
                removeItemEnder(player);
            },1);
        } else if(origin.contains("genesis:origin-elytrian")){
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(),()->{
                removeItemPhantom(player);
                removeItemEnder(player);
            },1);
        } else if(origin.contains("genesis:origin-avian")){
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(),()->{
                player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.17);
                removeItemPhantom(player);
                removeItemEnder(player);
            },1);
        } else if(origin.contains("genesis:origin-piglin")){
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(),()->{
                removeItemPhantom(player);
                removeItemEnder(player);
            },1);
        } else if(origin.contains("genesis:origin-sculkling")){
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(),()->{
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(24);
                removeItemPhantom(player);
                removeItemEnder(player);
            },1);
        } else {
            setAttributesToDefault(player);
            Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(),()->{
                removeItemPhantom(player);
                removeItemEnder(player);
            },1);
        }
    }

}
