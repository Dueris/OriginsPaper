package me.dueris.genesismc.core.factory.powers.entity;

import me.dueris.genesismc.core.entity.OriginPlayer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;

import static me.dueris.genesismc.core.factory.powers.Powers.strong_arms_break_speed;
import static org.bukkit.Material.*;

public class BetterMineSpeed implements Listener {
    public static EnumSet<Material> tools;
    public static EnumSet<Material> nat_stones;

    static {
        nat_stones = EnumSet.of(GRANITE, COBBLED_DEEPSLATE, TUFF, STONE, ANDESITE, BLACKSTONE, COBBLESTONE, CALCITE, AMETHYST_BLOCK, ANDESITE_SLAB,
                ANDESITE_STAIRS, ANDESITE_WALL, DIORITE, DIORITE_SLAB, DIORITE_STAIRS, DIORITE_WALL, BLACKSTONE_SLAB, BLACKSTONE_STAIRS, BLACKSTONE_WALL,
                COAL_ORE, DEEPSLATE, DEEPSLATE_COAL_ORE, NETHERRACK, END_STONE);
        tools = EnumSet.of(
                WOODEN_PICKAXE, STONE_PICKAXE, GOLDEN_PICKAXE, IRON_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE,
                WOODEN_AXE, STONE_AXE, GOLDEN_AXE, IRON_AXE, DIAMOND_AXE, NETHERITE_AXE,
                WOODEN_SHOVEL, STONE_SHOVEL, GOLDEN_SHOVEL, IRON_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL,
                SHEARS);
    }

    @EventHandler
    public void onBreakShulk(BlockBreakEvent e) {
        Player p = e.getPlayer();
        ItemStack i = new ItemStack(e.getBlock().getType(), 1);
        if (strong_arms_break_speed.contains(OriginPlayer.getOrigin(e.getPlayer()).getTag())) {
            if (nat_stones.contains(e.getBlock().getType())) {
                if (!tools.contains(p.getEquipment().getItemInMainHand().getType())) {
                    if (!p.getGameMode().equals(GameMode.CREATIVE)) {
                        if (e.getBlock().getType().equals(STONE)) {
                            p.getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(COBBLESTONE));
                        } else if (e.getBlock().getType().toString().contains("coal") && e.getBlock().getType().toString().contains("ore")) {
                            p.getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(COAL));
                        } else if (e.getBlock().getType().equals(DEEPSLATE)) {
                            p.getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(COBBLED_DEEPSLATE));
                        } else {
                            p.getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), i);
                        }
                    }
                }
            }
        }
    }

}
