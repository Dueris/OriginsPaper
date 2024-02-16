package me.dueris.genesismc.factory.powers.apoli.provider.origins;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.provider.PowerProvider;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
// import ru.beykerykt.minecraft.lightapi.common.LightAPI;

import java.util.ArrayList;
import java.util.HashMap;

public class Bioluminescent extends CraftPower implements Listener, PowerProvider {
    public static ArrayList<Player> players = new ArrayList<>();
    protected static NamespacedKey powerReference = GenesisMC.originIdentifier("allay_sparkle_light");

    @Override
    public void run(Player p) {
        // WE ARE ALIVE
    }

    @Override
    public String getPowerFile() {
        return null;
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return players;
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
