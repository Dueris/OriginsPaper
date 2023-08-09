package me.dueris.genesismc.core;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class CooldownStuff {

    //TODO: ADD COOLDOWN POWER WHEN ACTIONS ARE DONE

    public static HashMap<Player, String> cooldowns = new HashMap<>();
    public static HashMap<Player, BossBar> cooldownBars = new HashMap<>();

    public static void addCooldown(Player player, String title, int cooldownTicks, String cooldownKeybindType) {
        if (isPlayerInCooldown(player, cooldownKeybindType)) {
            resetCooldown(player, cooldownKeybindType);
        }

        BossBar bar = createCooldownBar(player, BarColor.WHITE, getCooldownPegAMT(cooldownTicks), title);
        bar.addPlayer(player);
        startTickingCooldown(bar, player, cooldownTicks, cooldownKeybindType);
        cooldownBars.put(player, bar);
        cooldowns.put(player, cooldownKeybindType);
    }

    private static BarStyle getCooldownPegAMT(int ticks){
        if(ticks >= 20){
            return BarStyle.SEGMENTED_20;
        } else if (ticks >= 12) {
            return BarStyle.SEGMENTED_12;
        } else if (ticks >= 10) {
            return BarStyle.SEGMENTED_10;
        } else if (ticks >= 6) {
            return BarStyle.SEGMENTED_6;
        }else{
            return BarStyle.SOLID;
        }
    }

    public static boolean isPlayerInCooldown(Player player, String cooldownKeybindType) {
        return cooldowns.containsKey(player) && cooldownKeybindType.equals(cooldowns.get(player))
                && cooldownBars.containsKey(player);
    }

    public static void resetCooldown(Player player, String cooldownKeybindType) {
        if (isPlayerInCooldown(player, cooldownKeybindType)) {
            BossBar bar = cooldownBars.get(player);
            bar.removePlayer(player);
            cooldownBars.remove(player);
            cooldowns.remove(player);
        }
    }

    public static BossBar createCooldownBar(Player player, BarColor color, BarStyle style, String title) {
        BossBar bossBar = Bukkit.createBossBar(title, color, style);
        bossBar.setProgress(1.0);
        return bossBar;
    }

    public static void startTickingCooldown(BossBar bar, Player player, int cooldownTicks, String cooldownKeybindType) {
        final double decreasePerTick = 1.0 / cooldownTicks;

        new BukkitRunnable() {
            int ticksElapsed = -1;

            @Override
            public void run() {
                ticksElapsed++;
                double progress = 1.0 - (ticksElapsed * decreasePerTick);
                bar.setProgress(progress);

                int remainingTicks = cooldownTicks - ticksElapsed;

                if (ticksElapsed >= cooldownTicks) {
                    resetCooldown(player, cooldownKeybindType);
                    cancel();
                }
            }
        }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
    }

}
