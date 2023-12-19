package me.dueris.genesismc.enchantments;

import me.dueris.genesismc.Bootstrap;
import me.dueris.genesismc.GenesisMC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static me.dueris.genesismc.GenesisMC.waterProtectionEnchant;

@Deprecated(forRemoval = true)
public class Anvil implements Listener {
    public static WaterProtectionNMSImpl eimpl = Bootstrap.waterProtection;
    public static ArrayList<Enchantment> conflictenchantments = new ArrayList<>();
    static {
        conflictenchantments.add(Enchantment.PROTECTION_FIRE);
        conflictenchantments.add(Enchantment.PROTECTION_ENVIRONMENTAL);
        conflictenchantments.add(Enchantment.PROTECTION_EXPLOSIONS);
        conflictenchantments.add(Enchantment.PROTECTION_FALL);
        conflictenchantments.add(Enchantment.PROTECTION_PROJECTILE);
    }

    @EventHandler
    public void onAnvil(PrepareAnvilEvent e) {
        boolean conflicts = false;
        if(e.getInventory().getItem(0) != null && e.getInventory().getItem(1) != null){
            if(e.getInventory().getItem(0).containsEnchantment(CraftEnchantment.minecraftToBukkit(eimpl)) || e.getInventory().getItem(1).containsEnchantment(CraftEnchantment.minecraftToBukkit(eimpl))){
                Enchantment waterProt = CraftEnchantment.minecraftToBukkit(eimpl);

                for(Enchantment possConf : e.getInventory().getItem(0).getEnchantments().keySet()){
                    if(!conflicts){
                        if(!eimpl.isCompatibleWith(((CraftEnchantment)possConf).getHandle())){
                            conflicts = true;
                            e.setResult(null);
                        }
                    }
                }
                for(Enchantment possConf : e.getInventory().getItem(1).getEnchantments().keySet()){
                    if(!conflicts){
                        if(!eimpl.isCompatibleWith(((CraftEnchantment)possConf).getHandle())){
                            conflicts = true;
                            e.setResult(null);
                        }
                    }
                }
            }
        }
        if(e.getResult() != null && !conflicts && e.getInventory().getItem(0) != null && e.getInventory().getItem(1) != null){
            // begin anvil calculations. no conflicts and the result != null
            if(e.getInventory().getItem(0).containsEnchantment(CraftEnchantment.minecraftToBukkit(eimpl)) || e.getInventory().getItem(1).containsEnchantment(CraftEnchantment.minecraftToBukkit(eimpl))){
                boolean firstContains = e.getInventory().getItem(0).containsEnchantment(CraftEnchantment.minecraftToBukkit(eimpl));
                boolean secondContains = e.getInventory().getItem(1).containsEnchantment(CraftEnchantment.minecraftToBukkit(eimpl));
                if(firstContains && secondContains){
                    int firstlvl = e.getInventory().getItem(0).getEnchantments().get(CraftEnchantment.minecraftToBukkit(eimpl));
                    int secondlvl = e.getInventory().getItem(1).getEnchantments().get(CraftEnchantment.minecraftToBukkit(eimpl));
                    int finl = 1;
                    if(firstlvl > secondlvl){
                        finl = firstlvl;
                    } else if(firstlvl < secondlvl){
                        finl = secondlvl;
                    } else if(firstlvl == secondlvl){
                        finl = firstlvl + 1;
                    }
                    setWaterProtCustomEnchantLevel(finl, e.getResult());
                } else if(firstContains && !secondContains){
                    int firstlvl = e.getInventory().getItem(0).getEnchantments().get(CraftEnchantment.minecraftToBukkit(eimpl));
                    setWaterProtCustomEnchantLevel(firstlvl, e.getResult());
                } else if(!firstContains && !secondContains){
                    // why is this even coded bro this is unreachable
                } else if(!firstContains && secondContains){
                    int secondlvl = e.getInventory().getItem(1).getEnchantments().get(CraftEnchantment.minecraftToBukkit(eimpl));
                    setWaterProtCustomEnchantLevel(secondlvl, e.getResult());
                }
            }
        }
    }

    protected static void setWaterProtCustomEnchantLevel(int lvl, ItemStack item){
        String level = numberToRomanNum(lvl);
        ItemMeta meta = item.getItemMeta();
        meta.setCustomModelData(lvl);
        for(String loreString : item.getLore()){
            if(loreString.startsWith("Water Protection")){
                item.getLore().remove(loreString);
            }
        }
        item.getLore().add(ChatColor.GRAY + "{name} {lvl}".replace("{name}", "Water Protection").replace("{lvl}", level));
        net.minecraft.world.item.ItemStack stack = CraftItemStack.asNMSCopy(item);
        stack.enchant(eimpl, lvl);
    }

    private static String numberToRomanNum(int lvl){
        if(lvl > 10){
            Bukkit.getLogger().severe("Cannot translate value higher than max enchantment value in Genesis {4}");
            return null;
        }else{
            switch(lvl){
                case 1 -> {
                    return "I";
                }
                case 2 -> {
                    return "II";
                }
                case 3 -> {
                    return "III";
                }
                case 4 -> {
                    return "IV";
                }
                case 5 -> {
                    return "V";
                }
                case 6 -> {
                    return "VI";
                }
                case 7 -> {
                    return "VII";
                }
                case 8 -> {
                    return "VIII";
                }
                case 9 -> {
                    return "IX";
                }
                case 10 -> {
                    return "X";
                }
                default -> {
                    return "I";
                }
            }
        }
    }
}
