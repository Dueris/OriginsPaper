package me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.events.OriginChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ValueModifyingSuperClass implements Listener {

    public void runTasks(){
        ModifyAirSpeedPower modifyAirSpeedPower = new ModifyAirSpeedPower();
        modifyAirSpeedPower.runTaskTimer(GenesisMC.getPlugin(), 0, 1);

        ModifyBlockRenderPower modifyBlockRender = new ModifyBlockRenderPower();
        modifyBlockRender.runTaskTimer(GenesisMC.getPlugin(), 0, 1);

        Bukkit.getServer().getPluginManager().registerEvents(new ModifyBreakSpeedPower(), GenesisMC.getPlugin());
        Bukkit.getServer().getPluginManager().registerEvents(new ModifyCraftingPower(), GenesisMC.getPlugin());
        Bukkit.getServer().getPluginManager().registerEvents(new ModifyDamageDealtPower(), GenesisMC.getPlugin());
        Bukkit.getServer().getPluginManager().registerEvents(new ModifyExhaustionPower(), GenesisMC.getPlugin());
        Bukkit.getServer().getPluginManager().registerEvents(new ModifyFallingPower(), GenesisMC.getPlugin());
        Bukkit.getServer().getPluginManager().registerEvents(new ModifyFoodPower(), GenesisMC.getPlugin());
        Bukkit.getServer().getPluginManager().registerEvents(new ModifyHarvestPower(), GenesisMC.getPlugin());
    }

    public void runModifierChanges(Player p){
        ModifyAirSpeedPower modifyAirSpeedPower = new ModifyAirSpeedPower();
        ModifyBreakSpeedPower modifyBreakSpeedPower = new ModifyBreakSpeedPower();
        new BukkitRunnable(){
            @Override
            public void run() {
                if(modify_air_speed.contains(p)){
                    modifyAirSpeedPower.apply(p);
                    p.sendMessage("apply");
                }else if(modify_break_speed.contains(p)){
                    modifyBreakSpeedPower.apply(p);
                    p.sendMessage("apply");
                }else{
                    saveValueInPDC(p, "modify_air_speed", 0.1f);
                    p.sendMessage("esle");
                }
            }
        }.runTaskLater(GenesisMC.getPlugin(), 4);
    }

    @EventHandler
    public void ORIGINCHANGE(OriginChangeEvent e){
            runModifierChanges(e.getPlayer());
            e.getPlayer().sendMessage("run_modify");
    }

    public Float getPersistentAttributeContainer(Player player, String key){
        PersistentDataContainer PDC = player.getPersistentDataContainer();
        if(PDC.has(new NamespacedKey(GenesisMC.getPlugin(), key))){
            return PDC.get(new NamespacedKey(GenesisMC.getPlugin(), key), PersistentDataType.FLOAT);
        }else{
            createValueInPDC(player, key);
            return PDC.get(new NamespacedKey(GenesisMC.getPlugin(), key), PersistentDataType.FLOAT);
        }
    }

    public void createValueInPDC(Player player, String key){
        PersistentDataContainer PDC = player.getPersistentDataContainer();
        PDC.set(new NamespacedKey(GenesisMC.getPlugin(), key), PersistentDataType.FLOAT, getDefaultValue(key));
    }

    public Float getDefaultValue(String string){
        switch (string){
            case "modify_air_speed":
                return 0.1f;
            case "modify_break_speed":
                return 0.1f;
            default:
                return 0f;
        }
    }

    public void saveValueInPDC(Player player, String key, Float value){
        PersistentDataContainer PDC = player.getPersistentDataContainer();
        if(PDC.has(new NamespacedKey(GenesisMC.getPlugin(), key))){
            PDC.set(new NamespacedKey(GenesisMC.getPlugin(), key), PersistentDataType.FLOAT, value);
        }else{
            PDC.set(new NamespacedKey(GenesisMC.getPlugin(), key), PersistentDataType.FLOAT, value);
        }
    }

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
}
