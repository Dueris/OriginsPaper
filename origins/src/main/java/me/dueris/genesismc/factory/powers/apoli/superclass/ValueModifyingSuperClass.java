package me.dueris.genesismc.factory.powers.apoli.superclass;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.powers.apoli.ModifyAirSpeedPower;
import me.dueris.genesismc.factory.powers.apoli.ModifyBreakSpeedPower;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ValueModifyingSuperClass extends BukkitRunnable implements Listener {

    public static ArrayList<Player> modify_air_speed = new ArrayList<>();
    public static ArrayList<Player> modify_block_render = new ArrayList<>();
    public static ArrayList<Player> modify_break_speed = new ArrayList<>();
    public static ArrayList<Player> modify_crafting = new ArrayList<>();
    public static ArrayList<Player> modify_damage_dealt = new ArrayList<>();
    public static ArrayList<Player> modify_damage_taken = new ArrayList<>();
    public static ArrayList<Player> modify_exhaustion = new ArrayList<>();
    public static ArrayList<Player> modify_falling = new ArrayList<>();
    public static ArrayList<Player> modify_food = new ArrayList<>();
    public static ArrayList<Player> modify_harvest = new ArrayList<>();
    public static ArrayList<Player> modify_healing = new ArrayList<>();
    public static ArrayList<Player> modify_jump = new ArrayList<>();
    public static ArrayList<Player> modify_lava_speed = new ArrayList<>();
    public static ArrayList<Player> modify_world_spawn = new ArrayList<>();
    public static ArrayList<Player> modify_projectile_damage = new ArrayList<>();
    public static ArrayList<Player> modify_effect_amplifier = new ArrayList<>();
    public static ArrayList<Player> modify_effect_duration = new ArrayList<>();
    public static ArrayList<Player> modify_enchantment_level = new ArrayList<>();
    public static ArrayList<Player> modify_swim_speed = new ArrayList<>();
    public static ArrayList<Player> modify_velocity = new ArrayList<>();
    public static ArrayList<Player> modify_xp_gain = new ArrayList<>();

    public void runModifierChanges(Player p) {
        ModifyAirSpeedPower modifyAirSpeedPower = new ModifyAirSpeedPower();
        ModifyBreakSpeedPower modifyBreakSpeedPower = new ModifyBreakSpeedPower();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (modify_air_speed.contains(p)) {
                    modifyAirSpeedPower.apply(p);
                } else if (modify_break_speed.contains(p)) {
                    modifyBreakSpeedPower.apply(p);
                } else {
                    saveValueInPDC(p, "modify_air_speed", 0.1f);
                    saveValueInPDC(p, "modify_break_speed", 0.1f);
                    saveValueInPDC(p, "modify_swim_speed", 1f);
                }
            }
        }.runTaskLater(GenesisMC.getPlugin(), 4);
    }

    @EventHandler
    public void ORIGINCHANGE(OriginChangeEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runModifierChanges(e.getPlayer());
            }
        }.runTaskLater(GenesisMC.getPlugin(), 5);
    }

    @EventHandler
    public void ORIGINCHANGE(PlayerJoinEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runModifierChanges(e.getPlayer());
            }
        }.runTaskLater(GenesisMC.getPlugin(), 5);
    }

    public Float getPersistentAttributeContainer(Player player, String key) {
        PersistentDataContainer PDC = player.getPersistentDataContainer();
        if (PDC.has(new NamespacedKey(GenesisMC.getPlugin(), key))) {
            return PDC.get(new NamespacedKey(GenesisMC.getPlugin(), key), PersistentDataType.FLOAT);
        } else {
            createValueInPDC(player, key);
            return PDC.get(new NamespacedKey(GenesisMC.getPlugin(), key), PersistentDataType.FLOAT);
        }
    }

    public void createValueInPDC(Player player, String key) {
        PersistentDataContainer PDC = player.getPersistentDataContainer();
        PDC.set(new NamespacedKey(GenesisMC.getPlugin(), key), PersistentDataType.FLOAT, getDefaultValue(key));
    }

    public Float getDefaultValue(String string) {
        switch (string) {
            case "modify_air_speed":
                return 0.1f;
            case "modify_break_speed":
                return 0.1f;
            case "modify_swim_speed":
                return 1f;
            default:
                return 0f;
        }
    }

    public void saveValueInPDC(Player player, String key, Float value) {
        PersistentDataContainer PDC = player.getPersistentDataContainer();
        PDC.set(new NamespacedKey(GenesisMC.getPlugin(), key), PersistentDataType.FLOAT, value);
    }

    @Override
    public void run() {

    }
}
