package me.dueris.genesismc.core.factory.powers.armour;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.utils.Lang;
import me.dueris.genesismc.core.utils.OriginContainer;
import me.dueris.genesismc.core.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.Powers.restrict_armor;
import static me.dueris.genesismc.core.utils.ArmorUtils.getArmorValue;

public class RestrictArmor extends BukkitRunnable {

    private Long interval;

    private int ticksE;

    public RestrictArmor() {
        this.interval = 1L;
        this.ticksE = 0;
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (restrict_armor.contains(p)) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    PowerContainer power = origin.getPowerFileFromType("origins:restrict_armor");
                    if (power == null) continue;
                    interval = power.getTickRate();
                    if (power.getTickRate() != null) {
                        if (power.getInterval() == null) {
                            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Unable to parse interval for origins:restrict_armor");
                            return;
                        }
                        if (ticksE < interval) {
                            ticksE++;
                            return;
                        } else {

                            ticksE = 0;
                        }

                    } else {
                        //not conditioned
                        boolean headb = true;
                        boolean chestb = true;
                        boolean legsb = true;
                        boolean feetb = true;

                        if (power.getHead() == null) headb = false;
                        if (power.getChest() == null) chestb = false;
                        if (power.getLegs() == null) legsb = false;
                        if (power.getFeet() == null) feetb = false;

                        String comparisonh = power.getHead().get("comparison").toString();
                        String comparisonc = power.getChest().get("comparison").toString();
                        String comparisonl = power.getLegs().get("comparison").toString();
                        String comparisonf = power.getFeet().get("comparison").toString();

                        String comparisontoh = power.getHead().get("compare_to").toString();
                        String comparisontoc = power.getChest().get("compare_to").toString();
                        String comparisontol = power.getLegs().get("compare_to").toString();
                        String comparisontof = power.getFeet().get("compare_to").toString();

                        if (power.getHead().get("type").toString().equalsIgnoreCase("origins:armor_value")) {
                            if (!headb) return;
                            ItemStack item = p.getInventory().getHelmet();
                            if (item != null) {
                                double armorValue = getArmorValue(item);
                                double compareValue = Double.parseDouble(comparisontoh);
                                if (compareValues(armorValue, comparisonh, compareValue)) {
                                    OriginPlayer.moveEquipmentInventory(p, EquipmentSlot.HEAD);
                                }
                            }
                        } else if (power.getHead().get("type").toString().equalsIgnoreCase("origins:ingredient")) {
                            if (!headb) return;
                            //need to code some methods for that
                        }

                        if (power.getChest().get("type").toString().equalsIgnoreCase("origins:armor_value")) {
                            if (!chestb) return;
                            ItemStack item = p.getInventory().getChestplate();
                            if (item != null) {
                                double armorValue = getArmorValue(item);
                                double compareValue = Double.parseDouble(comparisontoc);
                                if (compareValues(armorValue, comparisonc, compareValue)) {
                                    OriginPlayer.moveEquipmentInventory(p, EquipmentSlot.CHEST);
                                }
                            }
                        } else if (power.getChest().get("type").toString().equalsIgnoreCase("origins:ingredient")) {
                            if (!chestb) return;
                            //need to code some methods for that
                        }

                        if (power.getLegs().get("type").toString().equalsIgnoreCase("origins:armor_value")) {
                            if (!legsb) return;
                            ItemStack item = p.getInventory().getLeggings();
                            if (item != null) {
                                double armorValue = getArmorValue(item);
                                double compareValue = Double.parseDouble(comparisontol);
                                if (compareValues(armorValue, comparisonl, compareValue)) {
                                    OriginPlayer.moveEquipmentInventory(p, EquipmentSlot.LEGS);
                                }
                            }
                        } else if (power.getLegs().get("type").toString().equalsIgnoreCase("origins:ingredient")) {
                            if (!legsb) return;
                            //need to code some methods for that
                        }

                        if (power.getFeet().get("type").toString().equalsIgnoreCase("origins:armor_value")) {
                            if (!feetb) return;
                            ItemStack item = p.getInventory().getBoots();
                            if (item != null) {
                                double armorValue = getArmorValue(item);
                                double compareValue = Double.parseDouble(comparisontof);
                                if (compareValues(armorValue, comparisonf, compareValue)) {
                                    OriginPlayer.moveEquipmentInventory(p, EquipmentSlot.FEET);
                                }
                            }
                        } else if (power.getFeet().get("type").toString().equalsIgnoreCase("origins:ingredient")) {
                            if (!feetb) return;
                            //need to code some methods for that
                        }


                    }
                }
            }
        }
    }

    public static boolean compareValues(double value1, String comparison, double value2) {
        switch (comparison) {
            case ">":
                return value1 > value2;
            case ">=":
                return value1 >= value2;
            case "<":
                return value1 < value2;
            case "<=":
                return value1 <= value2;
            case "==":
                return value1 == value2;
            case "=":
                return value1 == value2;
            default:
                return false;
        }
    }
}
