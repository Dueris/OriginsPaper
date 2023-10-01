package me.dueris.genesismc.factory.powers.effects;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;

public class StackingStatusEffect extends CraftPower {
    public static PotionEffectType getPotionEffectType(String effectString) {
        if (effectString == null) {
            return null;
        }

        if(effectString.startsWith("minecraft:")){
            switch (effectString) {
                case "minecraft:speed":
                    return PotionEffectType.SPEED;
                case "minecraft:slowness":
                    return PotionEffectType.SLOW;
                case "minecraft:slow_falling":
                    return PotionEffectType.SLOW_FALLING;
                case "minecraft:haste":
                    return PotionEffectType.FAST_DIGGING;
                case "minecraft:mining_fatigue":
                    return PotionEffectType.SLOW_DIGGING;
                case "minecraft:strength":
                    return PotionEffectType.INCREASE_DAMAGE;
                case "minecraft:instant_health":
                    return PotionEffectType.HEAL;
                case "minecraft:instant_damage":
                    return PotionEffectType.HARM;
                case "minecraft:jump_boost":
                    return PotionEffectType.JUMP;
                case "minecraft:nausea":
                    return PotionEffectType.CONFUSION;
                case "minecraft:regeneration":
                    return PotionEffectType.REGENERATION;
                case "minecraft:resistance":
                    return PotionEffectType.DAMAGE_RESISTANCE;
                case "minecraft:fire_resistance":
                    return PotionEffectType.FIRE_RESISTANCE;
                case "minecraft:water_breathing":
                    return PotionEffectType.WATER_BREATHING;
                case "minecraft:invisibility":
                    return PotionEffectType.INVISIBILITY;
                case "minecraft:blindness":
                    return PotionEffectType.BLINDNESS;
                case "minecraft:night_vision":
                    return PotionEffectType.NIGHT_VISION;
                case "minecraft:hunger":
                    return PotionEffectType.HUNGER;
                case "minecraft:weakness":
                    return PotionEffectType.WEAKNESS;
                case "minecraft:poison":
                    return PotionEffectType.POISON;
                case "minecraft:wither":
                    return PotionEffectType.WITHER;
                case "minecraft:health_boost":
                    return PotionEffectType.HEALTH_BOOST;
                case "minecraft:absorption":
                    return PotionEffectType.ABSORPTION;
                case "minecraft:saturation":
                    return PotionEffectType.SATURATION;
                case "minecraft:glowing":
                    return PotionEffectType.GLOWING;
                case "minecraft:levitation":
                    return PotionEffectType.LEVITATION;
                case "minecraft:luck":
                    return PotionEffectType.LUCK;
                case "minecraft:unluck":
                    return PotionEffectType.UNLUCK;
                case "minecraft:darkness":
                    return PotionEffectType.DARKNESS;
                case "minecraft:hero_of_the_village":
                    return PotionEffectType.HERO_OF_THE_VILLAGE;
                default:
                    return null;
            }
        }else{
            switch (effectString) {
                case "speed":
                    return PotionEffectType.SPEED;
                case "slowness":
                    return PotionEffectType.SLOW;
                case "slow_falling":
                    return PotionEffectType.SLOW_FALLING;
                case "haste":
                    return PotionEffectType.FAST_DIGGING;
                case "mining_fatigue":
                    return PotionEffectType.SLOW_DIGGING;
                case "strength":
                    return PotionEffectType.INCREASE_DAMAGE;
                case "instant_health":
                    return PotionEffectType.HEAL;
                case "instant_damage":
                    return PotionEffectType.HARM;
                case "jump_boost":
                    return PotionEffectType.JUMP;
                case "nausea":
                    return PotionEffectType.CONFUSION;
                case "regeneration":
                    return PotionEffectType.REGENERATION;
                case "resistance":
                    return PotionEffectType.DAMAGE_RESISTANCE;
                case "fire_resistance":
                    return PotionEffectType.FIRE_RESISTANCE;
                case "water_breathing":
                    return PotionEffectType.WATER_BREATHING;
                case "invisibility":
                    return PotionEffectType.INVISIBILITY;
                case "blindness":
                    return PotionEffectType.BLINDNESS;
                case "night_vision":
                    return PotionEffectType.NIGHT_VISION;
                case "hunger":
                    return PotionEffectType.HUNGER;
                case "weakness":
                    return PotionEffectType.WEAKNESS;
                case "poison":
                    return PotionEffectType.POISON;
                case "wither":
                    return PotionEffectType.WITHER;
                case "health_boost":
                    return PotionEffectType.HEALTH_BOOST;
                case "absorption":
                    return PotionEffectType.ABSORPTION;
                case "saturation":
                    return PotionEffectType.SATURATION;
                case "glowing":
                    return PotionEffectType.GLOWING;
                case "levitation":
                    return PotionEffectType.LEVITATION;
                case "luck":
                    return PotionEffectType.LUCK;
                case "unluck":
                    return PotionEffectType.UNLUCK;
                case "darkness":
                    return PotionEffectType.DARKNESS;
                case "hero_of_the_village":
                    return PotionEffectType.HERO_OF_THE_VILLAGE;
                default:
                    return null;
            }
        }
    }

    Player p;

    public StackingStatusEffect() {
        this.p = p;
    }

    @Override
    public void run(Player p) {
        if (getPowerArray().contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                ConditionExecutor executor = new ConditionExecutor();
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {
                        setActive(power.getTag(), true);
                        applyStackingEffect(p, calculateStacks(p, 10, origin, power), origin, power);
                    } else {
                        setActive(power.getTag(), false);
                    }
                }
            }
        }
    }

    private int calculateStacks(Player player, int durationPerStack, OriginContainer origin, PowerContainer power) {
        double healthPercentage = player.getHealth() / player.getMaxHealth();
        double saturationPercentage = player.getSaturation() / 20.0;

        double combinedPercentage = (healthPercentage + saturationPercentage) / 2.0;

        int minStacks = Integer.parseInt(power.get("min_stacks"));
        int maxStacks = Integer.parseInt(power.get("max_stacks"));
        int calculatedStacks = (int) Math.round(combinedPercentage * (maxStacks - minStacks) + minStacks);

        int actualDuration = calculatedStacks * durationPerStack;

        return actualDuration;
    }

    private void applyStackingEffect(Player player, int stacks, OriginContainer origin, PowerContainer power) {

        for (HashMap<String, Object> effect : power.getSingularAndPlural("effect", "effects")) {

            PotionEffectType potionEffectType = getPotionEffectType(effect.get("effect").toString());
            if (potionEffectType != null) {
                try {
                    player.addPotionEffect(new PotionEffect(potionEffectType, 5, 1, false, false, false));
                } catch (Exception e) {
                    //AHHHHHHHHHH
                }
            } else {
                Bukkit.getLogger().warning("Unknown effect ID: " + effect.get("effect").toString());
            }
        }
        player.sendHealthUpdate();
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:stacking_status_effect";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return stacking_status_effect;
    }
}
