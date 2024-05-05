package me.dueris.genesismc.factory.powers.apoli;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.potion.CraftPotionUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class StackingStatusEffect extends CraftPower implements Listener {
    protected final ConcurrentHashMap<Power, List<MobEffectInstance>> effects = new ConcurrentHashMap<>();
    protected final Object2IntMap<Power> currentStackPowers = new Object2IntOpenHashMap<>();

    @Override
    public void run(Player p, Power power) {
        int currentStack = currentStackPowers.getOrDefault(power, 0); // Retrieve current stack or default to 0
        int minStack = power.getNumber("min_stacks").getInt();
        int maxStack = power.getNumber("max_stacks").getInt();
        int tickRate = power.getNumberOrDefault("tick_rate", 10).getInt();

        if(p.getTicksLived() % tickRate == 0) {
            if(ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
                currentStack += 1;
                if(currentStack > maxStack) {
                    currentStack = maxStack;
                }
                if(currentStack > 0) {
                    apoli$StackingStatusEffectPower$applyEffects(((CraftPlayer)p).getHandle(), power);
                }
            } else {
                currentStack -= 1;
                if(currentStack < minStack) {
                    currentStack = minStack;
                }
            }
        }

        currentStackPowers.put(power, currentStack);
    }

    @Override
    public String getType() {
        return "apoli:stacking_status_effect";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return stacking_status_effect;
    }

    public void addEffect(MobEffect effect, Power power) {
        addEffect(effect, 80, power);
    }

    public void addEffect(MobEffect effect, int lingerDuration, Power power) {
        addEffect(effect, lingerDuration, 0, power);
    }

    public void addEffect(MobEffect effect, int lingerDuration, int amplifier, Power power) {
        addEffect(new MobEffectInstance(CraftPotionEffectType.bukkitToMinecraftHolder(CraftPotionEffectType.minecraftToBukkit(effect)), lingerDuration, amplifier), power);
    }

    public void addEffect(MobEffectInstance instance, Power power) {
        effects.putIfAbsent(power, new ArrayList<>());
        effects.get(power).add(instance);
    }

    public void applyEffects(LivingEntity entity, Power power) {
        effects.getOrDefault(power, new ArrayList<>()).stream().map(MobEffectInstance::new).forEach(entity::addEffect);
    }

    public void apoli$StackingStatusEffectPower$applyEffects(LivingEntity entity, Power power) {
        effects.putIfAbsent(power, new ArrayList<>());
        if (effects.get(power).isEmpty()) {
            effects.put(power, Utils.parseAndReturnPotionEffects(power).stream().map(CraftPotionUtil::fromBukkit).toList());
        }
        List<MobEffectInstance> effectInstances = effects.get(power);
        effectInstances.forEach(sei -> {
            int duration = power.getNumber("duration_per_stack").getInt() * currentStackPowers.getOrDefault(power, 0);
            if (duration > 0) {
                MobEffectInstance applySei = new MobEffectInstance(sei.getEffect(), duration, sei.getAmplifier(), sei.isAmbient(), sei.isVisible(), sei.showIcon());
                // GenesisMC - Paper/Spigot makers lots of changes to potion effects, making it work differently. This fixes it
                PotionEffectType bukkitType = CraftPotionEffectType.minecraftHolderToBukkit(sei.getEffect());
                if (entity.getBukkitLivingEntity().hasPotionEffect(bukkitType)) {
                    entity.getBukkitLivingEntity().removePotionEffect(bukkitType);
                }
                entity.getBukkitLivingEntity().addPotionEffect(CraftPotionUtil.toBukkit(applySei), true);
            }
        });
    }
}
