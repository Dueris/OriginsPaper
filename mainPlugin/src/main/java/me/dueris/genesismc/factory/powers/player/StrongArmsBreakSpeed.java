package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.EnumSet;

import static org.bukkit.Material.*;

public class StrongArmsBreakSpeed extends CraftPower implements Listener {

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

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    @EventHandler
    public void breakBlock(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!strong_arms_break_speed.contains(p)) return;
        for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
            for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, e.getClickedBlock(), null, p.getItemInHand(), null)) {
                    if (power == null) {
                        getPowerArray().remove(p);
                        return;
                    }
                    if (!getPowerArray().contains(p)) return;
                    setActive(power.getTag(), true);
                    if (e.getClickedBlock() != null && stones.contains(e.getClickedBlock().getType()) && e.getAction().isLeftClick() && !tools.contains(p.getEquipment().getItemInMainHand().getType())) {
                        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 60, 15, false, false, false));
                    } else if (p.getEquipment().getItemInMainHand().getType() == AIR) { //beacons exist
                        e.getPlayer().removePotionEffect(PotionEffectType.FAST_DIGGING);
                    }
                } else {
                    if (power == null) {
                        getPowerArray().remove(p);
                        return;
                    }
                    if (!getPowerArray().contains(p)) return;
                    setActive(power.getTag(), false);
                }
            }
        }
    }

    Player p;

    public StrongArmsBreakSpeed() {
        this.p = p;
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "origins:modify_break_speed";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return strong_arms_break_speed;
    }
}
