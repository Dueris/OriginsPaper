package me.dueris.genesismc.factory.powers.genesismc;


import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.LangConfig;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

public class SilkTouch extends CraftPower implements Listener {

    private static final EnumSet<Material> m;

    static {
        m = EnumSet.of(Material.PISTON_HEAD, Material.VINE, Material.WHEAT, Material.MELON_STEM, Material.ATTACHED_MELON_STEM, Material.PUMPKIN_STEM, Material.ATTACHED_PUMPKIN_STEM, Material.BEETROOTS, Material.CARROTS, Material.POTATOES, Material.END_PORTAL, Material.NETHER_PORTAL, Material.FIRE, Material.SOUL_FIRE);
    }

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!e.getBlock().getType().equals(Material.AIR)) {
            Player p = e.getPlayer();
            if (silk_touch.contains(e.getPlayer())) {
                ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                    for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, e.getBlock(), null, p.getItemInHand(), null)) {
                            setActive(p, power.getTag(), true);
                            if (p.getGameMode().equals(GameMode.SURVIVAL) && p.getEquipment().getItemInMainHand().getType().equals(Material.AIR)) {
                                if (!e.getBlock().getType().isItem()) {
                                    return;
                                }
                                if (!m.contains(e.getBlock().getType())) {
                                    if (e.getBlock().getState() instanceof ShulkerBox) {
                                        return;
                                    }
                                    if (!m.contains(e.getBlock().getType())) {
                                        if (e.getBlock().getState() instanceof CreatureSpawner) {
                                            return;
                                        }

                                        if (e.getBlock().getType().toString().endsWith("BANNER")) {
                                            return;
                                        }

                                        e.setDropItems(false);
                                        ItemStack i = new ItemStack(e.getBlock().getType(), 1);

                                        try {
                                            p.getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), i);
                                        } catch (Exception exception) {
                                            Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.silkTouch"));
                                            exception.printStackTrace();
                                        }
                                    }

                                }
                            }
                        } else {

                            setActive(p, power.getTag(), false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "genesis:silk_touch";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return silk_touch;
    }
}

