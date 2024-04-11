package me.dueris.genesismc.util;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.powers.apoli.Resource;
import me.dueris.genesismc.registry.Registries;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static me.dueris.genesismc.util.TextureLocation.textureMap;

public class CooldownUtils implements @NotNull Listener {

    public static HashMap<Player, ArrayList<String>> cooldownMap = new HashMap<>();
    public static HashMap<Player, HashMap<String/*PowerTag*/, Integer/*cooldownTicks*/>> cooldownTicksMap = new HashMap<>();

    public static void addCooldown(Player player, Pair<String, String> title, String putNull, int cooldownTicks, JSONObject hudRender) {
        // first = name to display
        // second = tag
        if (isPlayerInCooldownFromTag(player, title) || cooldownTicks <= 1) return;
        BossBar bar = createCooldownBar(player, getBarColor(hudRender), BarStyle.SEGMENTED_6, title.first());
        cooldownMap.putIfAbsent(player, new ArrayList<>());
        cooldownMap.get(player).add(title.first());
        cooldownTicksMap.putIfAbsent(player, new HashMap<>());
        cooldownTicksMap.get(player).put(title.second(), cooldownTicks);
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
        startTickingCooldown(bar, player, cooldownTicks, title, 1.0);
    }

    public static BarColor getBarColor(JSONObject hudRender) {
        if (hudRender.isEmpty() || !hudRender.containsKey("sprite_location")) return BarColor.WHITE;
        TextureLocation location = (TextureLocation) GenesisMC.getPlugin().registry.retrieve(Registries.TEXTURE_LOCATION).get(NamespacedKey.fromString(hudRender.get("sprite_location").toString()));
        long index = ((long) hudRender.getOrDefault("bar_index", 1)) + 1;
        BarColor color = textureMap.get(location.getKey().asString() + "/-/" + index);
        return color != null ? color : BarColor.WHITE;
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
            if (red - green < 30) return BarColor.YELLOW;
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

    public static void startTickingCooldown(BossBar bar, Player player, int cooldownTicks, Pair<String, String> pair, double startingProgress) {
        final double decreasePerTick = 1.0 / cooldownTicks;

        new BukkitRunnable() {
            int ticksElapsed = -1;

            @Override
            public void run() {
                try {
                    ticksElapsed++;
                    double progress = startingProgress - (ticksElapsed * decreasePerTick);
                    if (ticksElapsed >= cooldownTicks || cooldownTicks == 0 || progress <= 0) {
                        bar.setProgress(0);
                        bar.setVisible(false);
                        bar.removePlayer(player);
                        cooldownMap.putIfAbsent(player, new ArrayList<>());
                        cooldownMap.get(player).remove(bar.getTitle());
                        Resource.registeredBars.get(player).remove(pair.right());
                        cancel();
                    }

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
                } catch (Exception e) {
                    //fail silent
                }

            }
        }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
    }

    @EventHandler
    public void runsQuit(PlayerQuitEvent e) {
        Resource.registeredBars.putIfAbsent(e.getPlayer(), new HashMap<>());
        cooldownMap.putIfAbsent(e.getPlayer(), new ArrayList<>());
        cooldownTicksMap.putIfAbsent(e.getPlayer(), new HashMap<>());
        HashMap<String, Pair<BossBar, Double>> map = Resource.registeredBars.get(e.getPlayer());
        String t = ";";
        for (Map.Entry<String, Pair<BossBar, Double>> entry : map.entrySet()) {
            t += entry.getKey() + "," + entry.getValue().left().getTitle() + "," + entry.getValue().left().getColor() + "," + entry.getValue().right() + "," + cooldownTicksMap.get(e.getPlayer()).get(entry.getKey()) + "||";
        }
        t += ";";
        e.getPlayer().getPersistentDataContainer().set(GenesisMC.identifier("saved_cooldowns"), PersistentDataType.STRING, t);
        cooldownMap.get(e.getPlayer()).clear();
        Resource.registeredBars.get(e.getPlayer()).clear();
    }

    @EventHandler
    public void runsJoin(PlayerJoinEvent e) {
        cooldownMap.putIfAbsent(e.getPlayer(), new ArrayList<>());
        if (e.getPlayer().getPersistentDataContainer().has(GenesisMC.identifier("saved_cooldowns"), PersistentDataType.STRING)) {
            String t = e.getPlayer().getPersistentDataContainer().get(GenesisMC.identifier("saved_cooldowns"), PersistentDataType.STRING);
            if (t != null) {
                String[] split = t.replace(";", "").split("\\|\\|");
                for (String cooldown : split) {
                    if (cooldown.toCharArray().length < 10) continue; // Wasnt saved right
                    String[] split2 = cooldown.split(",");
                    String tag = split2[0];
                    String title = split2[1];
                    BarColor color = BarColor.valueOf(split2[2]);
                    BossBar bar = createCooldownBar(e.getPlayer(), color, BarStyle.SEGMENTED_6, title);
                    bar.setVisible(true);
                    bar.setProgress(Double.valueOf(split2[3]));
                    cooldownMap.putIfAbsent(e.getPlayer(), new ArrayList<>());
                    cooldownMap.get(e.getPlayer()).add(title);
                    cooldownTicksMap.putIfAbsent(e.getPlayer(), new HashMap<>());
                    cooldownTicksMap.get(e.getPlayer()).put(tag, Integer.valueOf(split2[4]));
                    Resource.registeredBars.putIfAbsent(e.getPlayer(), new HashMap<>());
                    Resource.registeredBars.get(e.getPlayer()).put(tag, new Pair<BossBar, Double>() {
                        @Override
                        public BossBar left() {
                            return bar;
                        }

                        @Override
                        public Double right() {
                            return bar.getProgress();
                        }
                    });
                    startTickingCooldown(bar, e.getPlayer(), Integer.valueOf(split2[4]), new Pair<>() {
                        @Override
                        public String left() {
                            return title;
                        }

                        @Override
                        public String right() {
                            return tag;
                        }
                    }, Double.valueOf(split2[3]));
                }
            }
        }
        e.getPlayer().getPersistentDataContainer().set(GenesisMC.identifier("saved_cooldowns"), PersistentDataType.STRING, "");
    }

    @EventHandler
    public void runs(OriginChangeEvent e) {
        cooldownMap.putIfAbsent(e.getPlayer(), new ArrayList<>());
        cooldownMap.get(e.getPlayer()).clear();
    }

}
