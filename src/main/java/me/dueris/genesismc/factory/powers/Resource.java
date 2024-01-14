package me.dueris.genesismc.factory.powers;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.conditions.entity.EntityCondition;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class Resource extends CraftPower implements Listener {
    public static HashMap<String, org.apache.commons.lang3.tuple.Pair<BossBar, Integer>> registeredBars = new HashMap();

    public static int countNumbersBetween(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("Start integer should be less than or equal to end integer.");
        }

        int count = 0;

        for (int i = start + 1; i < end; i++) {
            count++;
        }

        return count + 1;
    }

    public static org.apache.commons.lang3.tuple.Pair<BossBar, Integer> getResource(String tag) {
        if (registeredBars.containsKey(tag)) {
            return registeredBars.get(tag);
        }
        return null;
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void start(PlayerJoinEvent e) {
        execute(e.getPlayer());
    }

    @EventHandler
    public void start(OriginChangeEvent e) {
        execute(e.getPlayer());
    }

    private void execute(Player p) {
        for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
            for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                final String tag = power.getTag();
                BossBar bar = Bukkit.createBossBar(power.getTag(), BarColor.WHITE, BarStyle.SOLID);
                bar.setProgress(1.0);
                org.apache.commons.lang3.tuple.Pair<BossBar, Integer> pair = new org.apache.commons.lang3.tuple.Pair<BossBar, Integer>() {
                    @Override
                    public Integer setValue(Integer value) {
                        return null;
                    }

                    @Override
                    public BossBar getLeft() {
                        return bar;
                    }

                    @Override
                    public Integer getRight() {
                        return countNumbersBetween(power.getIntOrDefault("start_value", power.getInt("min")), power.getInt("max"));
                    }
                };
                registeredBars.put(tag, pair);
                final boolean[] shouldRender = {false};
                if (power.get("hud_render") != null) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            HashMap<String, Object> hud_render = power.get("hud_render");
                            final boolean[] canRender = {true};
                            if ((boolean) hud_render.getOrDefault("should_render", false)) {
                                shouldRender[0] = canRender[0];
                            }
                            if (hud_render.containsKey("condition")) {
                                EntityCondition conditionExecutor = ConditionExecutor.entityCondition;
                                Optional<Boolean> conditionMet = conditionExecutor.check(power.get("hud_render"), p, p, null, p.getLocation().getBlock(), null, p.getInventory().getItemInMainHand(), null);
                                if (conditionMet.isPresent()) {
                                    canRender[0] = conditionMet.get();
                                }
                            }
                        }
                    }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
                }
                bar.setVisible(shouldRender[0]);
                bar.addPlayer(p);
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:resource";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return resource;
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
}
