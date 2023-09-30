package me.dueris.genesismc.factory.powers;

import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.CooldownStuff.*;
import static me.dueris.genesismc.factory.powers.player.FireProjectile.in_cooldown_patch;

public class Resource extends CraftPower{
    @Override
    public void run(Player p) {

    }

    //TODO: finish resource code

    private static HashMap<String, BossBar> registeredBars= new HashMap();

    public static BossBar getResource(String tag){
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
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }
}
