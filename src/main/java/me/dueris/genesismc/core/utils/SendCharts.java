package me.dueris.genesismc.core.utils;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

import static me.dueris.genesismc.core.GenesisMC.metrics;

public class SendCharts {

    public static void originPopularity(Player p) {
        String originTag = p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (originTag == null) return;
        metrics.addCustomChart(new Metrics.DrilldownPie("originPopularity", () -> {
            Map<String, Map<String, Integer>> map = new HashMap<>();
            Map<String, Integer> entry = new HashMap<>();
            String origin = switch (originTag) {
                case "genesis:origin-human" -> "Human";
                case "genesis:origin-enderian" -> "Enderian";
                case "genesis:origin-merling" -> "Merling";
                case "genesis:origin-phantom" -> "Phantom";
                case "genesis:origin-elytrian" -> "Elytrian";
                case "genesis:origin-blazeborn" -> "Blazeborn";
                case "genesis:origin-avian" -> "Avian";
                case "genesis:origin-arachnid" -> "Arachnid";
                case "genesis:origin-shulk" -> "Shulk";
                case "genesis:origin-feline" -> "Feline";
                case "genesis:origin-starborne" -> "Starborn";
                case "genesis:origin-allay" -> "Allay";
                case "genesis:origin-rabbit" -> "Rabbit";
                case "genesis:origin-bee" -> "Bee";
                case "genesis:origin-sculkling" -> "Sculkling";
                case "genesis:origin-creep" -> "Creep";
                case "genesis:origin-slimeling" -> "Slimeling";
                case "genesis:origin-piglin" -> "Piglin";
                default -> "Custom Origin";
            };
            entry.put(origin, 1);
            map.put(origin, entry);
            p.sendMessage(origin);
            return map;
        }));
    }
}
