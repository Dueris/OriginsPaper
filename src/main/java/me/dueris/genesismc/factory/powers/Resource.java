package me.dueris.genesismc.factory.powers;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.conditions.entity.EntityCondition;
import me.dueris.genesismc.utils.OriginContainer;
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
    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void start(PlayerJoinEvent e){
        execute(e.getPlayer());
    }

    @EventHandler
    public void start(OriginChangeEvent e){
        execute(e.getPlayer());
    }

    private void execute(Player p){
        for(OriginContainer origin : OriginPlayerUtils.getOrigin(p).values()){
            for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                p.sendMessage("2");
                final String tag = power.getTag();
                final String originTag = origin.getTag();
                BossBar bar = Bukkit.createBossBar(power.getTag(), BarColor.WHITE, BarStyle.SOLID);
                bar.setProgress(1.0);
                p.sendMessage("3");
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
                        return countNumbersBetween(Integer.valueOf(power.get("start_value", power.get("min")).toString()), Integer.valueOf(power.get("max").toString()));
                    }
                };
                p.sendMessage("4");
                registeredBars.put(tag, pair);
                p.sendMessage(registeredBars.keySet().toString());
                final boolean[] shouldRender = {false};
                if(power.getJsonHashMap("hud_render") != null){
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                                HashMap<String, Object> hud_render = power.getJsonHashMap("hud_render");
                                final boolean[] canRender = {true};
                                if((boolean) hud_render.getOrDefault("should_render", false)){
                                    if(canRender[0]){
                                        shouldRender[0] = true;
                                    }else{
                                        shouldRender[0] = false;
                                    }
                                }
                                if(hud_render.containsKey("condition")){
                                    EntityCondition conditionExecutor = ConditionExecutor.entityCondition;
                                    Optional<Boolean> conditionMet = conditionExecutor.check(power.getJsonHashMap("hud_render"), p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getInventory().getItemInMainHand(), null);
                                    if(conditionMet.isPresent()){
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

    public static HashMap<String, org.apache.commons.lang3.tuple.Pair<BossBar, Integer>> registeredBars= new HashMap();

    public static org.apache.commons.lang3.tuple.Pair<BossBar, Integer> getResource(String tag){
        if(registeredBars.containsKey(tag)){
            return registeredBars.get(tag);
        }
        return null;
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
        if(powers_active.containsKey(p)){
            if(powers_active.get(p).containsKey(tag)){
                powers_active.get(p).replace(tag, bool);
            }else{
                powers_active.get(p).put(tag, bool);
            }
        }else{
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }
}
