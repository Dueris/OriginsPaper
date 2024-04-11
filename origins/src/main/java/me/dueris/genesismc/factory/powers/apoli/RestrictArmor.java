package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.LangConfig;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.json.simple.JSONObject;

import java.util.ArrayList;

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
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (power == null) continue;
                    if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
                        runPower(p, power);
                    }
                }
            }
        }
    }


    @Override
    public void run(Player p) {
        if (getPowerArray().contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (power == null) continue;
                    if (power.getObjectOrDefault("interval", 1L) == null) {
                        Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.action_over_time"));
                        return;
                    }

                    interval = power.getLong("interval");
                    if (interval == 0) interval = 1L;
                    if (Bukkit.getServer().getCurrentTick() % interval != 0) {
                        return;
                    } else {
                        ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                        if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
                            runPower(p, power);
                        } else {
                            setActive(p, power.getTag(), false);
                        }
                    }
                }
            }
        }
    }

    public void runPower(Player p, Power power) {
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

        if (headb) {
            passHead = ConditionExecutor.testItem(headObj, p.getInventory().getItem(EquipmentSlot.HEAD));
        } else {
            passHead = true;
        }

        if (chestb) {
            passChest = ConditionExecutor.testItem(chestObj, p.getInventory().getItem(EquipmentSlot.CHEST));
        } else {
            passChest = true;
        }

        if (legsb) {
            passLegs = ConditionExecutor.testItem(legsObj, p.getInventory().getItem(EquipmentSlot.LEGS));
        } else {
            passLegs = true;
        }

        if (feetb) {
            passFeet = ConditionExecutor.testItem(feetObj, p.getInventory().getItem(EquipmentSlot.FEET));
        } else {
            passFeet = true;
        }

        if (passFeet) {
            OriginPlayerAccessor.moveEquipmentInventory(p, EquipmentSlot.FEET);
        }
        if (passChest) {
            OriginPlayerAccessor.moveEquipmentInventory(p, EquipmentSlot.CHEST);
        }
        if (passHead) {
            OriginPlayerAccessor.moveEquipmentInventory(p, EquipmentSlot.HEAD);
        }
        if (passLegs) {
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
