package me.dueris.genesismc.core.utils;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.CraftApoli;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

import static me.dueris.genesismc.core.GenesisMC.metrics;

public class SendCharts {

    public static void originPopularity(Player p) {
        OriginContainer origin = CraftApoli.toOriginContainer(p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origin"), PersistentDataType.BYTE_ARRAY));
        if (origin == null) return;
        metrics.addCustomChart(new Metrics.DrilldownPie("originPopularity", () -> {
            Map<String, Map<String, Integer>> map = new HashMap<>();
            Map<String, Integer> entry = new HashMap<>();
            String originName = switch (origin.getTag()) {
                case "origins:human" -> "Human";
                case "origins:enderian" -> "Enderian";
                case "origins:merling" -> "Merling";
                case "origins:phantom" -> "Phantom";
                case "origins:elytrian" -> "Elytrian";
                case "origins:blazeborn" -> "Blazeborn";
                case "origins:avian" -> "Avian";
                case "origins:arachnid" -> "Arachnid";
                case "origins:shulk" -> "Shulk";
                case "origins:feline" -> "Feline";
                case "origins:starborne" -> "Starborn";
                case "origins:allay" -> "Allay";
                case "origins:rabbit" -> "Rabbit";
                case "origins:bee" -> "Bee";
                case "origins:sculkling" -> "Sculkling";
                case "origins:creep" -> "Creep";
                case "origins:slimeling" -> "Slimeling";
                case "origins:piglin" -> "Piglin";
                default -> "Custom Origin";
            };
            entry.put(originName, 1);
            map.put(originName, entry);
            return map;
        }));
    }
}
