package me.dueris.genesismc.factory.powers;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Objects;

public class OriginMethods {

    public static void statusEffectInstance(LivingEntity player, JSONObject power) {
        JSONObject singleEffect = (JSONObject) power.get("effect");
        JSONArray effects = (JSONArray) power.getOrDefault("effects", new JSONArray());

        //System.out.println(effects);

        if (singleEffect != null) {
            effects.add(singleEffect);
        }

        for (Object obj : effects) {
            JSONObject effect = (JSONObject) obj;
            String potionEffect = "minecraft:luck";
            int duration = 100;
            int amplifier = 0;
            boolean isAmbient = false;
            boolean showParticles = true;
            boolean showIcon = true;

            if (effect.containsKey("effect")) potionEffect = effect.get("effect").toString();
            if (effect.containsKey("duration")) duration = Integer.parseInt(effect.get("duration").toString());
            if (effect.containsKey("amplifier")) amplifier = Integer.parseInt(effect.get("amplifier").toString());
            if (effect.containsKey("is_ambient")) isAmbient = Boolean.parseBoolean(effect.get("is_ambient").toString());
            if (effect.containsKey("show_particles"))
                showParticles = Boolean.parseBoolean(effect.get("show_particles").toString());
            if (effect.containsKey("show_icon")) showIcon = Boolean.parseBoolean(effect.get("show_icon").toString());

            //System.out.println(PotionEffectType.getByKey(new NamespacedKey(potionEffect.split(":")[0], potionEffect.split(":")[1])));

            player.addPotionEffect(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByKey(new NamespacedKey(potionEffect.split(":")[0], potionEffect.split(":")[1]))), duration, amplifier, isAmbient, showParticles, showIcon));
        }
    }
}
