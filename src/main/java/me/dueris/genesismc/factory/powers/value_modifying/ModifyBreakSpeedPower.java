package me.dueris.genesismc.factory.powers.value_modifying;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler;
import me.dueris.genesismc.utils.ErrorSystem;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.translation.LangConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.factory.powers.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_break_speed;

public class ModifyBreakSpeedPower extends CraftPower implements Listener {

    String MODIFYING_KEY = "modify_break_speed";

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
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
    public void runD(PlayerArmSwingEvent e) {
        Player p = e.getPlayer();
        if (modify_break_speed.contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
                try {
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if (conditionExecutor.check("condition", "condition", p, origin, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                        if (conditionExecutor.check("block_condition", "block_condition", p, origin, getPowerFile(), p, null, e.getPlayer().getTargetBlockExact(AttributeHandler.Reach.getDefaultReach(p)), null, p.getItemInHand(), null)) {
                            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                                p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 50, calculateHasteAmplifier(valueModifyingSuperClass.getPersistentAttributeContainer(p, MODIFYING_KEY)), false, false, false));
                        } else {
                            setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                        }
                    } else {
                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                    }
                } catch (Exception ev) {
                    ErrorSystem errorSystem = new ErrorSystem();
                    errorSystem.throwError("unable to set modifier", "origins:modify_break_speed", p, origin, OriginPlayer.getLayer(p, origin));
                    ev.printStackTrace();
                }
            }
        }
    }

    public void apply(Player p) {
        ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
        if (modify_break_speed.contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                for (HashMap<String, Object> modifier : origin.getPowerFileFromType("origins:modify_break_speed").getConditionFromString("modifier", "modifiers")) {
                    Float value = Float.valueOf(modifier.get("value").toString());
                    String operation = modifier.get("operation").toString();
                    BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                    if (mathOperator != null) {
                        float result = (float) mathOperator.apply(valueModifyingSuperClass.getPersistentAttributeContainer(p, MODIFYING_KEY), value);
                        valueModifyingSuperClass.saveValueInPDC(p, MODIFYING_KEY, result);
                    } else {
                        Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.value_modifier_save").replace("%modifier%", MODIFYING_KEY));
                    }
                }
            }
        } else {
            valueModifyingSuperClass.saveValueInPDC(p, MODIFYING_KEY, valueModifyingSuperClass.getDefaultValue(MODIFYING_KEY));
        }
    }

    Player p;

    public ModifyBreakSpeedPower(){
        this.p = p;
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
