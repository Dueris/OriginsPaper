package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.TicksElapsedPower;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.LangConfig;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static me.dueris.genesismc.util.ArmorUtils.getArmorValue;

public class RestrictArmor extends CraftPower implements Listener, TicksElapsedPower {

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
    public void run(Player p, HashMap<Player, Integer> ticksEMap) {
        ticksEMap.putIfAbsent(p, 0);
        if (getPowerArray().contains(p)) {
            for (LayerContainer layer : CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (power == null) continue;
                    if (power.getObjectOrDefault("interval", 1L) == null) {
                        Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.action_over_time"));
                        return;
                    }

                    interval = power.getLong("interval");
                    int ticksE = ticksEMap.getOrDefault(p, 0);
                    if (ticksE <= interval) {
                        ticksE++;
                        ticksEMap.put(p, ticksE);
                    } else {
                        ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                        if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                            runPower(p, power);
                        } else {
                            setActive(p, power.getTag(), false);
                        }
                        ticksEMap.put(p, 0);
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
        JSONObject headObj = power.get("head");
        JSONObject chestObj = power.get("head");
        JSONObject legsObj = power.get("head");
        JSONObject feetObj = power.get("head");

        if (headObj == null) headb = false;
        if (chestObj == null) chestb = false;
        if (legsObj == null) legsb = false;
        if (feetObj == null) feetb = false;

        if (headObj.get("type").toString().equalsIgnoreCase("apoli:armor_value")) {
            String comparisonh = headObj.get("comparison").toString();
            String comparisontoh = headObj.get("compare_to").toString();
            if (!headb) return;
            ItemStack item = p.getInventory().getHelmet();
            if (item != null) {
                double armorValue = getArmorValue(item);
                double compareValue = Double.parseDouble(comparisontoh);
                if (Utils.compareValues(armorValue, comparisonh, compareValue)) {
                    OriginPlayerAccessor.moveEquipmentInventory(p, EquipmentSlot.HEAD);
                }
            }
        } else if (headObj.get("type").toString().equalsIgnoreCase("apoli:ingredient")) {
            if (!headb) return;
            if (p.getInventory().getHelmet() != null) {
                Map<String, Object> ingredientMap = (Map<String, Object>) headObj.get("ingredient");
                if (ingredientMap.containsKey("item")) {
                    String itemValue = ingredientMap.get("item").toString();
                    String item = null;
                    if (itemValue.contains(":")) {
                        item = itemValue.split(":")[1];
                    } else {
                        item = itemValue;
                    }
                    if (p.getInventory().getHelmet().getType().equals(Material.valueOf(item.toUpperCase()))) {
                        OriginPlayerAccessor.moveEquipmentInventory(p, EquipmentSlot.HEAD);
                    }
                }
            }
        }

        if (chestObj.get("type").toString().equalsIgnoreCase("apoli:armor_value")) {
            String comparisonc = chestObj.get("comparison").toString();
            String comparisontoc = chestObj.get("compare_to").toString();
            if (!chestb) return;
            ItemStack item = p.getInventory().getChestplate();
            if (item != null) {
                double armorValue = getArmorValue(item);
                double compareValue = Double.parseDouble(comparisontoc);
                if (Utils.compareValues(armorValue, comparisonc, compareValue)) {
                    OriginPlayerAccessor.moveEquipmentInventory(p, EquipmentSlot.CHEST);
                }
            }
        } else if (chestObj.get("type").toString().equalsIgnoreCase("apoli:ingredient")) {
            if (!chestb) return;
            if (p.getInventory().getChestplate() != null) {
                Map<String, Object> ingredientMap = (Map<String, Object>) chestObj.get("ingredient");
                if (ingredientMap.containsKey("item")) {
                    String itemValue = ingredientMap.get("item").toString();
                    String item = null;
                    if (itemValue.contains(":")) {
                        item = itemValue.split(":")[1];
                    } else {
                        item = itemValue;
                    }
                    if (p.getInventory().getChestplate().getType().equals(Material.valueOf(item.toUpperCase()))) {
                        OriginPlayerAccessor.moveEquipmentInventory(p, EquipmentSlot.CHEST);
                    }
                }
            }
        }

        if (legsObj.get("type").toString().equalsIgnoreCase("apoli:armor_value")) {
            String comparisonl = legsObj.get("comparison").toString();
            String comparisontol = legsObj.get("compare_to").toString();
            if (!legsb) return;
            ItemStack item = p.getInventory().getLeggings();
            if (item != null) {
                double armorValue = getArmorValue(item);
                double compareValue = Double.parseDouble(comparisontol);
                if (Utils.compareValues(armorValue, comparisonl, compareValue)) {
                    OriginPlayerAccessor.moveEquipmentInventory(p, EquipmentSlot.LEGS);
                }
            }
        } else if (legsObj.get("type").toString().equalsIgnoreCase("apoli:ingredient")) {
            if (!legsb) return;
            if (p.getInventory().getLeggings() != null) {
                Map<String, Object> ingredientMap = (Map<String, Object>) legsObj.get("ingredient");
                if (ingredientMap.containsKey("item")) {
                    String itemValue = ingredientMap.get("item").toString();
                    String item = null;
                    if (itemValue.contains(":")) {
                        item = itemValue.split(":")[1];
                    } else {
                        item = itemValue;
                    }
                    if (p.getInventory().getLeggings().getType().equals(Material.valueOf(item.toUpperCase()))) {
                        OriginPlayerAccessor.moveEquipmentInventory(p, EquipmentSlot.LEGS);
                    }
                }
            }
        }

        if (feetObj.get("type").toString().equalsIgnoreCase("apoli:armor_value")) {
            String comparisonf = feetObj.get("comparison").toString();
            String comparisontof = feetObj.get("compare_to").toString();
            if (!feetb) return;
            ItemStack item = p.getInventory().getBoots();
            if (item != null) {
                double armorValue = getArmorValue(item);
                double compareValue = Double.parseDouble(comparisontof);
                if (Utils.compareValues(armorValue, comparisonf, compareValue)) {
                    OriginPlayerAccessor.moveEquipmentInventory(p, EquipmentSlot.FEET);
                }
            }
        } else if (feetObj.get("type").toString().equalsIgnoreCase("apoli:ingredient")) {
            if (!feetb) return;
            if (p.getInventory().getBoots() != null) {
                Map<String, Object> ingredientMap = (Map<String, Object>) feetObj.get("ingredient");
                if (ingredientMap.containsKey("item")) {
                    String itemValue = ingredientMap.get("item").toString();
                    String item = null;
                    if (itemValue.contains(":")) {
                        item = itemValue.split(":")[1];
                    } else {
                        item = itemValue;
                    }
                    if (p.getInventory().getBoots().getType().equals(Material.valueOf(item.toUpperCase()))) {
                        OriginPlayerAccessor.moveEquipmentInventory(p, EquipmentSlot.FEET);
                    }
                }
            }
        }
    }

    @Override
    public void run(Player p) {

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
