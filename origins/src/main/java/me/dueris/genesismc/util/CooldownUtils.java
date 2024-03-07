package me.dueris.genesismc.util;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.powers.apoli.Resource;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.util.render.TextureLocation;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.util.render.TextureLocation.textureMap;

public class CooldownUtils implements @NotNull Listener {

    public static HashMap<Player, ArrayList<String>> cooldownMap = new HashMap<>();

    public static void addCooldown(Player player, Pair<String, String> title, String dont_use, int cooldownTicks, JSONObject hudRender) {
        // first = name to display
        // second = tag
        if(isPlayerInCooldownFromTag(player, title) || cooldownTicks <= 1) return;
        BossBar bar = createCooldownBar(player, getBarColor(hudRender), getCooldownPegAMT(cooldownTicks), title.first());
        cooldownMap.putIfAbsent(player, new ArrayList<>());
        cooldownMap.get(player).add(title.first());
        Resource.registeredBars.putIfAbsent(player, new HashMap<>());
        Resource.registeredBars.get(player).put(title.second(), new Pair<BossBar, Double>() {
            @Override
            public BossBar left() {
                return bar;
            }

            @Override
            public Double right() {
                return bar.getProgress();
            }
        });
        startTickingCooldown(bar, player, cooldownTicks, title);
    }

    public static BarColor getBarColor(JSONObject hudRender){
        if(hudRender.isEmpty() || !hudRender.containsKey("sprite_location")) return BarColor.WHITE;
        TextureLocation location = (TextureLocation) GenesisMC.getPlugin().registry.retrieve(Registries.TEXTURE_LOCATION).get(NamespacedKey.fromString(hudRender.get("sprite_location").toString()));
        long index = ((long) hudRender.getOrDefault("bar_index", 1)) + 1;
        BarColor color = textureMap.get(location.getKey().asString() + "/-/" + index);
        return color != null ? color : BarColor.WHITE;
    }

    public static BarStyle getCooldownPegAMT(int ticks) {
        return BarStyle.SEGMENTED_6;
    }

    public static boolean isPlayerInCooldownFromTag(Player player, Pair<String, String> tag) {
        cooldownMap.putIfAbsent(player, new ArrayList<>());
        return cooldownMap.get(player).contains(tag.first());
    }

    public static boolean isPlayerInCooldownFromTag(Player player, String tag) {
        cooldownMap.putIfAbsent(player, new ArrayList<>());
        return cooldownMap.get(player).contains(tag);
    }

    public static BossBar createCooldownBar(Player player, BarColor color, BarStyle style, String title) {
        BossBar bossBar = Bukkit.createBossBar(title, color, style);
        bossBar.setProgress(1.0);
        bossBar.addPlayer(player);
        return bossBar;
    }

    public static BarColor convertToBarColor(Color color) {
        int rgb = color.getRGB();
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        if (red > green && red > blue) {
            if(red - green < 30) return BarColor.YELLOW;
            return BarColor.RED;
        } else if (green > red && green > blue) {
            return BarColor.GREEN;
        } else if (blue > red && blue > green) {
            return BarColor.BLUE;
        } else if (red == green && red == blue && blue == green) {
            return BarColor.WHITE;
        } else if (red == green) {
            return BarColor.YELLOW;
        } else if (red == blue) {
            return BarColor.PURPLE;
        } else if (green == blue) {
            return BarColor.GREEN;
        } else {
            return BarColor.WHITE;
        }
    }

    public static void startTickingCooldown(BossBar bar, Player player, int cooldownTicks, Pair<String, String> pair) {
        final double decreasePerTick = 1.0 / cooldownTicks;

        new BukkitRunnable() {
            int ticksElapsed = -1;

            @Override
            public void run() {
                try {
                    ticksElapsed++;
                    double progress = 1.0 - (ticksElapsed * decreasePerTick);
                    bar.setProgress(progress);

                    Resource.registeredBars.get(player).put(pair.right(), new Pair<BossBar, Double>() {
                        @Override
                        public BossBar left() {
                            return bar;
                        }

                        @Override
                        public Double right() {
                            return bar.getProgress();
                        }
                    });

                    if (ticksElapsed >= cooldownTicks || cooldownTicks == 0) {
                        bar.setProgress(0);
                        bar.setVisible(false);
                        bar.removePlayer(player);
                        cooldownMap.putIfAbsent(player, new ArrayList<>());
                        cooldownMap.get(player).remove(bar.getTitle());
                        Resource.registeredBars.get(player).remove(pair.right());
                        cancel();
                    }
                } catch (Exception e) {
                    //fail silent
                }

            }
        }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
    }

    @EventHandler
    public void runs(OriginChangeEvent e) {
        cooldownMap.putIfAbsent(e.getPlayer(), new ArrayList<>());
        cooldownMap.get(e.getPlayer()).clear();
    }

}
