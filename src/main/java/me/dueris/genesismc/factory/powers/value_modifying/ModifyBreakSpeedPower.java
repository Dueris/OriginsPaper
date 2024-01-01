package me.dueris.genesismc.factory.powers.value_modifying;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler;
import me.dueris.genesismc.utils.ErrorSystem;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.translation.LangConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_break_speed;

public class ModifyBreakSpeedPower extends CraftPower implements Listener {

    String MODIFYING_KEY = "modify_break_speed";
    public static ArrayList<Player> shouldDrop = new ArrayList<>();

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if(powers_active.containsKey(p)){
            if(powers_active.get(p).containsKey(tag)){
                powers_active.get(p).replace(tag, bool);
            }else{
                powers_active.get(p).put(tag, bool);
            }
        }else{
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    public int calculateHasteAmplifier(float value) {
        float maxValue = 10000.0f;
        float minValue = 0.1f;
        int maxAmplifier = 10;
        int minAmplifier = 0;

        float normalizedValue = Math.max(minValue, Math.min(value, maxValue));
        float percentage = (normalizedValue - minValue) / (maxValue - minValue);
        int amplifier = (int) (percentage * (maxAmplifier - minAmplifier)) + minAmplifier;

        return amplifier + 1;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if(getPowerArray().contains(e.getPlayer())){
            if(!shouldDrop.contains(e.getPlayer())) return;
            for(ItemStack item : e.getBlock().getDrops()) {
                e.getPlayer().getWorld().dropItemNaturally(e.getBlock().getLocation(), item);
            }
        }
    }

    @EventHandler
    public void swing(PlayerArmSwingEvent e){
        Player p = e.getPlayer();
        if (modify_break_speed.contains(p)) {
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
                try {
                    ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                    for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (conditionExecutor.check("condition", "condition", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                            if (conditionExecutor.check("block_condition", "block_condition", p, power, getPowerFile(), p, null, p.getTargetBlockExact(Math.toIntExact(Math.round(AttributeHandler.Reach.getFinalReach(p)))), null, p.getItemInHand(), null)) {
                                setActive(p, power.getTag(), true);
                                // if(power.getPossibleModifiers("modifier", "modifiers"))
                                for(HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifiers")){
                                    if(Float.valueOf(modifier.get("value").toString()) <= 0){
                                        // Slower mine
                                        p.addPotionEffect(
                                                new PotionEffect(
                                                        PotionEffectType.SLOW_DIGGING,
                                                        50,
                                                        Math.round(valueModifyingSuperClass.getPersistentAttributeContainer(p, MODIFYING_KEY)),
                                                        false, false, false
                                                )
                                        );
                                    }else{
                                        // Speed up
                                        p.addPotionEffect(
                                                new PotionEffect(
                                                        PotionEffectType.FAST_DIGGING,
                                                        50,
                                                        Math.round(valueModifyingSuperClass.getPersistentAttributeContainer(p, MODIFYING_KEY)),
                                                        false, false, false
                                                )
                                        );
                                    }
                                    shouldDrop.add(p);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            shouldDrop.remove(p);
                                        }
                                    }.runTaskLater(GenesisMC.getPlugin(), 40);
                                }
                            } else {
                                setActive(p, power.getTag(), false);
                            }
                        } else {
                            setActive(p, power.getTag(), false);
                        }
                    }
                } catch (Exception ev) {
                    ErrorSystem errorSystem = new ErrorSystem();
                    errorSystem.throwError("unable to set modifier", "origins:modify_break_speed", p, layer);
                    ev.printStackTrace();
                }
            }
        }
    }

    public void apply(Player p) {
        ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
        if (modify_break_speed.contains(p)) {
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    for (HashMap<String, Object> modifier : power.getConditionFromString("modifier", "modifiers")) {
                        Float value = Float.valueOf(modifier.get("value").toString());
                        valueModifyingSuperClass.saveValueInPDC(p, MODIFYING_KEY, value); // Why does there need to be a binary operator if the operator does nothing?
                    }
                }
            }
        } else {
            valueModifyingSuperClass.saveValueInPDC(p, MODIFYING_KEY, valueModifyingSuperClass.getDefaultValue(MODIFYING_KEY));
        }
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "origins:modify_break_speed";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_break_speed;
    }
}
