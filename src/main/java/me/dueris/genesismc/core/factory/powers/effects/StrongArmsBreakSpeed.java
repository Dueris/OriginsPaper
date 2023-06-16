package me.dueris.genesismc.core.factory.powers.effects;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.EnumSet;

import static org.bukkit.Material.*;

public class StrongArmsBreakSpeed implements Listener {

    public static EnumSet<Material> stones;
    public static EnumSet<Material> tools;

    static {
        stones = EnumSet.of(GRANITE, COBBLED_DEEPSLATE, TUFF, STONE, ANDESITE, BLACKSTONE, COBBLESTONE, CALCITE, AMETHYST_BLOCK, ANDESITE_SLAB,
                ANDESITE_STAIRS, ANDESITE_WALL, DIORITE, DIORITE_SLAB, DIORITE_STAIRS, DIORITE_WALL, BLACKSTONE_SLAB, BLACKSTONE_STAIRS, BLACKSTONE_WALL,
                COAL_ORE, DEEPSLATE, DEEPSLATE_COAL_ORE, NETHERRACK, END_STONE);
        tools = EnumSet.of(
                WOODEN_PICKAXE, STONE_PICKAXE, GOLDEN_PICKAXE, IRON_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE,
                WOODEN_AXE, STONE_AXE, GOLDEN_AXE, IRON_AXE, DIAMOND_AXE, NETHERITE_AXE,
                WOODEN_SHOVEL, STONE_SHOVEL, GOLDEN_SHOVEL, IRON_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL,
                SHEARS);
    }

    @EventHandler
    public void breakBlock(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getClickedBlock() != null && stones.contains(e.getClickedBlock().getType()) && e.getAction().isLeftClick() && !tools.contains(p.getEquipment().getItemInMainHand().getType())) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 60, 15, false, false, false ));
        } else if (p.getEquipment().getItemInMainHand().getType() == AIR) { //beacons exist
            e.getPlayer().removePotionEffect(PotionEffectType.FAST_DIGGING);
        }
    }
}
