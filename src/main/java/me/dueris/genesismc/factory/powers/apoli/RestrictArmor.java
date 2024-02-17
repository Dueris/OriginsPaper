package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.TicksElapsedPower;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.LangConfig;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class RestrictArmor extends CraftPower implements Listener {

    private final int ticksE;
    private Long interval;

    public RestrictArmor() {
        this.interval = 1L;
        this.ticksE = 0;
    }

    @EventHandler
    public void tick(PlayerArmorChangeEvent e) {
        Player p = e.getPlayer();
        if (getPowerArray().contains(p)) {
            for (LayerContainer layer : CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (power == null) continue;
                    ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                    if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                        runPower(p, power);
                    }
                }
            }
        }
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

    @Override
    public void run(Player p) {
        if (getPowerArray().contains(p)) {
            for (LayerContainer layer : CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (power == null) continue;
                    if (power.getObjectOrDefault("interval", 1L) == null) {
                        Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.action_over_time"));
                        return;
                    }

                    interval = power.getLong("interval");
                    if (Bukkit.getServer().getCurrentTick() % interval != 0) {
                        return;
                    } else {
                        ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                        if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                            runPower(p, power);
                        } else {
                            setActive(p, power.getTag(), false);
                        }
                    }
                }
            }
        }
    }

    public void runPower(Player p, PowerContainer power) {
        setActive(p, power.getTag(), true);
        boolean headb = true;
        boolean chestb = true;
        boolean legsb = true;
        boolean feetb = true;
        boolean passFeet;
        boolean passLegs;
        boolean passChest;
        boolean passHead;
        JSONObject headObj = power.get("head");
        JSONObject chestObj = power.get("chest");
        JSONObject legsObj = power.get("legs");
        JSONObject feetObj = power.get("feet");

        if (headObj == null) headb = false;
        if (chestObj == null) chestb = false;
        if (legsObj == null) legsb = false;
        if (feetObj == null) feetb = false;

        if(headb){
            Optional<Boolean> condition = ConditionExecutor.itemCondition.check(headObj, p, null, p.getLocation().getBlock(), null, p.getInventory().getItem(EquipmentSlot.HEAD), null);
            if(condition.isPresent()){
                passHead = condition.get();
            }else{
                passHead = true;
            }
        }else{
            passHead = true;
        }

        if(chestb){
            Optional<Boolean> condition = ConditionExecutor.itemCondition.check(chestObj, p, null, p.getLocation().getBlock(), null, p.getInventory().getItem(EquipmentSlot.CHEST), null);
            if(condition.isPresent()){
                passChest = condition.get();
            }else{
                passChest = true;
            }
        }else{
            passChest = true;
        }

        if(legsb){
            Optional<Boolean> condition = ConditionExecutor.itemCondition.check(legsObj, p, null, p.getLocation().getBlock(), null, p.getInventory().getItem(EquipmentSlot.LEGS), null);
            if(condition.isPresent()){
                passLegs = condition.get();
            }else{
                passLegs = true;
            }
        }else{
            passLegs = true;
        }

        if(feetb){
            Optional<Boolean> condition = ConditionExecutor.itemCondition.check(feetObj, p, null, p.getLocation().getBlock(), null, p.getInventory().getItem(EquipmentSlot.FEET), null);
            if(condition.isPresent()){
                passFeet = condition.get();
            }else{
                passFeet = true;
            }
        }else{
            passFeet = true;
        }

        if(passFeet){
            OriginPlayerAccessor.moveEquipmentInventory(p, EquipmentSlot.FEET);
        }
        if(passChest){
            OriginPlayerAccessor.moveEquipmentInventory(p, EquipmentSlot.CHEST);
        }
        if(passHead){
            OriginPlayerAccessor.moveEquipmentInventory(p, EquipmentSlot.HEAD);
        }
        if(passLegs){
            OriginPlayerAccessor.moveEquipmentInventory(p, EquipmentSlot.LEGS);
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:restrict_armor";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return restrict_armor;
    }
}
