package me.dueris.genesismc.factory.powers.apoli;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.builder.inst.factory.FactoryElement;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.data.types.Comparison;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.DataConverter;
import me.dueris.genesismc.util.TextureLocation;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.util.TextureLocation.textureMap;

public class Resource extends CraftPower implements Listener {
    public static HashMap<String, Bar> serverLoadedBars = new HashMap<>(); // IDENTIFIER || BAR_IMPL
    public static HashMap<Player, List<Bar>> currentlyDisplayed = new HashMap<>();

    static {
        GenesisMC.preShutdownTasks.add(() -> {
            serverLoadedBars.values().forEach(Bar::delete);
            currentlyDisplayed.forEach((player, list) -> list.forEach(Bar::delete));
        });
    }

    public static Optional<Bar> getDisplayedBar(Entity player, String identifier) {
        currentlyDisplayed.putIfAbsent((Player) player, new ArrayList<>());
        for (Bar bar : currentlyDisplayed.get(player)) {
            if (bar.power.getTag().equalsIgnoreCase(identifier)) return Optional.of(bar);
        }
        return Optional.empty();
    }

    protected static KeyedBossBar createRender(String title, double currentProgress, Power power, Player player) {
        NamespacedKey f = NamespacedKey.fromString(power.getTag() + "_bar_server_loaded");
        KeyedBossBar bossBar = Bukkit.createBossBar(
                player == null ? f : NamespacedKey.fromString(power.getTag() + "_bar_" + player.getName().toLowerCase()),
                title, Bar.getBarColor(power.getElement("hud_render")), BarStyle.SEGMENTED_6);
        bossBar.setProgress(currentProgress);
        return bossBar;
    }

    @EventHandler
    public void powerAdd(PowerUpdateEvent e) {
        if (e.getPower().getType().equalsIgnoreCase(getType())) {
            currentlyDisplayed.putIfAbsent(e.getPlayer(), new ArrayList<>());
            if (!e.isRemoved()) {
                // Power is added, display bar
                if (serverLoadedBars.containsKey(e.getPower().getTag())) {
                    Bar displayed = serverLoadedBars.get(e.getPower().getTag()).cloneForPlayer(e.getPlayer());
                    currentlyDisplayed.get(e.getPlayer()).add(displayed);
                }
            } else if (currentlyDisplayed.containsKey(e.getPlayer())) {
                // Power is removed, remove the bar
                Bar cD = getDisplayedBar(e.getPlayer(), e.getPower().getTag()).orElse(null);
                if (cD == null) return;
                cD.delete();
            }
        }
    }

    @EventHandler
    public void preLoad(ServerLoadEvent e) {
        // We preload the bars and then display a clone of them to each player
        ((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).values().stream()
                .filter(p -> p.getType().equalsIgnoreCase(getType())).forEach(power -> {
                    Bar bar = new Bar(power, null);
                    serverLoadedBars.put(power.getTag(), bar);
                });
        for (Player player : Bukkit.getOnlinePlayers())
            OriginPlayerAccessor.getMultiPowerFileFromType(player, getType()).forEach(power -> powerAdd(new PowerUpdateEvent(player, power, false)));
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        StringBuilder cooldownBuilder = new StringBuilder();
        cooldownBuilder.append("[");
        Cooldown.cooldowns.putIfAbsent(p, new ArrayList<>());
        for (Pair<KeyedBossBar, Power> barPair : Cooldown.cooldowns.get(p)) {
            cooldownBuilder.append(barPair.left().getKey().asString() + "<::>" + barPair.left().getProgress());
            cooldownBuilder.append(",");
        }
        cooldownBuilder.append("]");
        p.getPersistentDataContainer().set(GenesisMC.apoliIdentifier("current_cooldowns"), PersistentDataType.STRING, new String(cooldownBuilder).replace(",]", "]"));
        if (currentlyDisplayed.containsKey(p)) {
            currentlyDisplayed.get(p).forEach(Bar::delete);
            currentlyDisplayed.get(p).clear();
        }
        if (Cooldown.cooldowns.containsKey(p)) {
            Cooldown.cooldowns.get(p).forEach(pair -> Bukkit.getServer().removeBossBar(pair.left().getKey()));
            Cooldown.cooldowns.get(p).clear();
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (p.getPersistentDataContainer().has(GenesisMC.apoliIdentifier("current_cooldowns"))) {
            String encoded = p.getPersistentDataContainer().get(GenesisMC.apoliIdentifier("current_cooldowns"), PersistentDataType.STRING);
            encoded = encoded.replace("[", "").replace("]", "");
            if (encoded.equalsIgnoreCase("")) return;
            Arrays.stream(encoded.split(",")).forEach(key -> {
                String a = key.split("<::>")[0];
                double b = Double.parseDouble(key.split("<::>")[1]);
                Power power = CraftApoli.getPowerFromTag(a.split("_cooldown_")[0]);
                Cooldown.addCooldown(p, power.getNumberOrDefault("cooldown", 1).getInt(), power, b);
            });
        }
    }

    @Override
    public String getType() {
        return "apoli:resource";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return resource;
    }

    public static class Bar {
        String title;
        Power power;
        int min;
        int max;
        Double currentProgress; // Use lang class to use Number#intValue()
        Integer mappedProgress;
        KeyedBossBar renderedBar;
        double oneInc;

        Bar(Power power, Player player) {
            this.title = Utils.getNameOrTag(power).left();
            this.power = power;
            this.min = power.getNumber("min").getInt();
            this.max = power.getNumber("max").getInt();
            this.currentProgress = (double) (power.isPresent("start_value") ? power.getNumber("start_value").getInt() : this.min);
            this.mappedProgress = this.currentProgress.intValue();
            this.renderedBar = Resource.createRender(title, formatForFirstRender(this.currentProgress), power, player);
            this.renderedBar.setVisible(true);
            this.oneInc = 1.0 / this.max;
            if (player != null) {
                this.renderedBar.addPlayer(player);
            }

            change(power.isPresent("start_value") ? power.getNumber("start_value").getInt() : this.min, "set", false);
        }

        public static BarColor getBarColor(FactoryElement element) {
            if (element.isJsonObject()) {
                FactoryJsonObject hudRender = element.toJsonObject();
                if (hudRender.isEmpty() || !hudRender.isPresent("sprite_location")) return BarColor.WHITE;
                TextureLocation loc = ((Registrar<TextureLocation>) GenesisMC.getPlugin().registry.retrieve(Registries.TEXTURE_LOCATION))
                        .get(DataConverter.resolveTextureLocationNamespace(hudRender.getNamespacedKey("sprite_location")));
                long index = (hudRender.getNumberOrDefault("bar_index", 1).getLong()) + 1;
                BarColor color = textureMap.get(loc.getKey().asString() + "/-/" + index);
                return color != null ? color : BarColor.WHITE;
            }
            return BarColor.WHITE;
        }

        public Bar cloneForPlayer(Player player) {
            return new Bar(this.power, player);
        }

        public void delete() {
            this.renderedBar.setVisible(false);
            this.renderedBar.setProgress(0);
            this.renderedBar.removeAll();
            Bukkit.getServer().removeBossBar(this.renderedBar.getKey());
        }

        public void change(int by, String operation, boolean updateMapped) {
            Map<String, BinaryOperator<Double>> operator = Utils.getOperationMappingsDouble();
            double change = oneInc * by;
            this.renderedBar.setProgress(preVerifyProgress(operator.get(operation).apply(this.renderedBar.getProgress(), change)));
            this.currentProgress = this.renderedBar.getProgress();
            if (updateMapped) {
                int f = operator.get(operation).apply(this.mappedProgress.doubleValue(), (double) by).intValue();
                if (f < 0) f = 0;
                this.mappedProgress = f;
            }
            this.renderedBar.getPlayers().forEach(entity -> {
                if (this.renderedBar.getProgress() == 1.0) {
                    Actions.executeEntity(entity, this.power.getJsonObject("max_action"));
                } else if (this.renderedBar.getProgress() == 0.0) {
                    Actions.executeEntity(entity, this.power.getJsonObject("min_action"));
                }
            });
        }

        public void change(int by, String operation) {
            change(by, operation, true);
        }

        public boolean meetsComparison(Comparison comparison, double e) {
            return comparison.compare(this.mappedProgress, e);
        }

        private double formatForFirstRender(double e) {
            if (e == 0) return 0;
            double f = 1.0 / e;
            if (f > 1) return 1;
            if (f < 0) return 0;
            return f;
        }

        private double preVerifyProgress(double e) {
            if (e > 1) return 1.0;
            if (e < 0) return 0.0;
            return e;
        }
    }

}
