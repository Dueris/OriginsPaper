package me.dueris.genesismc.factory.conditions.biEntity;

import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.player.RestrictArmor;
import me.dueris.genesismc.factory.powers.world.EntitySetPower;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import org.bukkit.Fluid;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.util.Optional;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class BiEntityCondition implements Condition, Listener {

    @Override
    public String condition_type() {
        return "BIENTITY_CONDITION";
    }

    @Override
    public Optional<Boolean> check(JSONObject condition, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent entityDamageEvent) {
        if (condition.isEmpty()) return Optional.empty();
        if (condition.get("type") == null) return Optional.empty();
        boolean inverted = (boolean) condition.getOrDefault("inverted", false);
        String type = condition.get("type").toString().toLowerCase();
        switch (type) {
            case "apoli:actor_condition" -> {
                if (actor == null || target == null) {
                    return getResult(inverted, Optional.of(false));
                }
                return ConditionExecutor.biEntityCondition.check((JSONObject) condition.get("condition"), actor, target, block, fluid, itemStack, entityDamageEvent);
            }
            case "apoli:target_condition" -> {
                if (actor == null || target == null) {
                    return getResult(inverted, Optional.of(false));
                }
                return ConditionExecutor.biEntityCondition.check((JSONObject) condition.get("condition"), target, actor, block, fluid, itemStack, entityDamageEvent);
            }
            case "apoli:attack_target" -> {
                if (actor == null || target == null) {
                    return getResult(inverted, Optional.of(false));
                }
                net.minecraft.world.entity.Entity craftActor = ((CraftEntity) actor).getHandle();
                net.minecraft.world.entity.Entity craftTarget = ((CraftEntity) target).getHandle();

                return getResult(inverted, Optional.of((craftActor instanceof Mob mobActor && craftTarget.equals(mobActor.getTarget())) || (craftActor instanceof NeutralMob angerableActor && craftTarget.equals(angerableActor.getTarget()))));
            }
            case "apoli:attacker" -> {
                if (actor == null || target == null) {
                    return getResult(inverted, Optional.of(false));
                }
                net.minecraft.world.entity.Entity craftActor = ((CraftEntity) actor).getHandle();
                net.minecraft.world.entity.Entity craftTarget = ((CraftEntity) target).getHandle();

                return getResult(inverted, Optional.of(craftTarget instanceof LivingEntity livingEntity && craftActor.equals(livingEntity.lastHurtByMob)));
            }
            case "apoli:distance" -> {
                if (actor == null || target == null) {
                    return getResult(inverted, Optional.of(false));
                }
                @NotNull Vector actorVector = actor.getLocation().toVector();
                @NotNull Vector targetVector = target.getLocation().toVector();
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(actorVector.distance(targetVector), comparison, compare_to)));
            }
            case "apoli:in_set" -> {
                if (actor == null || target == null) {
                    return getResult(inverted, Optional.of(false));
                }
                return getResult(inverted, Optional.of(EntitySetPower.isInEntitySet(target, condition.get("set").toString())));
            }
            case "apoli:can_see" -> {
                if (actor == null || target == null) {
                    return getResult(inverted, Optional.of(false));
                }
                if (actor instanceof Player pl) {
                    return getResult(inverted, Optional.of(pl.canSee(target)));
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:owner" -> {
                if (actor == null || target == null) {
                    return getResult(inverted, Optional.of(false));
                }
                if (target instanceof Tameable tameable) {
                    return getResult(inverted, Optional.of(tameable.getOwner().equals(actor)));
                } else {
                    return getResult(inverted, Optional.of(false));
                }
            }
            case "apoli:riding_recursive" -> {
                if (actor == null || target == null) {
                    return getResult(inverted, Optional.of(false));
                }
                return getResult(inverted, Optional.of(actor.getPassengers().contains(target)));
            }
            case "apoli:riding_root" -> {
                if (actor == null || target == null) {
                    return getResult(inverted, Optional.of(false));
                }
                for (int i = 0; i < actor.getPassengers().toArray().length; i++) {
                    if (actor.getPassengers().isEmpty()) return getResult(inverted, Optional.of(false));
                    if (actor.getPassengers().get(i) != null) {
                        return getResult(inverted, Optional.of(i == actor.getPassengers().toArray().length));
                    } else {
                        return getResult(inverted, Optional.of(false));
                    }
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:riding" -> {
                if (actor == null || target == null) {
                    return getResult(inverted, Optional.of(false));
                }
                return getResult(inverted, Optional.of(target.getPassengers().contains(actor)));
            }
            default -> {
                return Optional.empty();
            }
        }
    }
}
