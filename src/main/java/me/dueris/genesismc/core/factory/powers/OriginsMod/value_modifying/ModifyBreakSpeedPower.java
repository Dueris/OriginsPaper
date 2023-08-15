package me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.utils.ErrorSystem;
import me.dueris.genesismc.core.utils.Lang;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.http.WebSocket;
import java.util.function.BinaryOperator;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_air_speed;
import static me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_break_speed;

public class ModifyBreakSpeedPower implements Listener {
    String MODIFYING_KEY = "modify_break_speed";

    public int calculateHasteAmplifier(float value) {
        float maxValue = 10000.0f;
        float minValue = 0.1f;
        int maxAmplifier = 10;
        int minAmplifier = 0;

        float normalizedValue = Math.max(minValue, Math.min(value, maxValue));
        float percentage = (normalizedValue - minValue) / (maxValue - minValue);
        int amplifier = (int) (percentage * (maxAmplifier - minAmplifier)) + minAmplifier;

        return amplifier;
    }

    @EventHandler
    public void run(PlayerArmSwingEvent e){
        Player p = e.getPlayer();
        for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
            ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
            try{
                if(ConditionExecutor.check("condition", p, origin, "origins:modify_air_speed", null, p)){
                    //TODO: add block condition
                    if(modify_break_speed.contains(p)){
                        p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 50, calculateHasteAmplifier(valueModifyingSuperClass.getPersistentAttributeContainer(p, MODIFYING_KEY)), false, false, false));
                    }
                }
            } catch (Exception ev){
                ErrorSystem errorSystem = new ErrorSystem();
                errorSystem.throwError("unable to set modifier", "origins:modify_break_speed", p, origin, OriginPlayer.getLayer(p, origin));
                ev.printStackTrace();
            }
        }
    }

    public void apply(Player p){
        ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
        if(modify_break_speed.contains(p)){
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                Float value = Float.valueOf(origin.getPowerFileFromType("origins:modify_break_speed").getModifier().get("value").toString());
                String operation = origin.getPowerFileFromType("origins:modify_break_speed").getModifier().get("operation").toString();
                BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                if (mathOperator != null) {
                    float result = (float) mathOperator.apply(valueModifyingSuperClass.getDefaultValue(MODIFYING_KEY), value);
                    valueModifyingSuperClass.saveValueInPDC(p, MODIFYING_KEY, result);
                } else {
                    Bukkit.getLogger().warning(Lang.getLocalizedString("powers.errors.value_modifier_save").replace("%modifier%", MODIFYING_KEY));
                }
            }
        }else{
            valueModifyingSuperClass.saveValueInPDC(p, MODIFYING_KEY, valueModifyingSuperClass.getDefaultValue(MODIFYING_KEY));
        }
    }
}
