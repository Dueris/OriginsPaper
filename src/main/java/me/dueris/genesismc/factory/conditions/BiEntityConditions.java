package me.dueris.genesismc.factory.conditions;

import me.dueris.genesismc.factory.powers.apoli.EntitySetPower;
import me.dueris.genesismc.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.phys.Vec3;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class BiEntityConditions implements Condition, Listener {

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
            case "apoli:both" -> {
                AtomicBoolean a = new AtomicBoolean(true);
                AtomicBoolean t = new AtomicBoolean(true);
                ConditionExecutor.entityCondition.check((JSONObject) condition.get("condition"), actor, null, block, fluid, itemStack, entityDamageEvent).ifPresent(bool -> a.set(bool));
                ConditionExecutor.entityCondition.check((JSONObject) condition.get("condition"), target, null, block, fluid, itemStack, entityDamageEvent).ifPresent(bool -> t.set(bool));

                return getResult(inverted, Optional.of(a.get() && t.get()));
            }
            case "apoli:either" -> {
                AtomicBoolean a = new AtomicBoolean(true);
                AtomicBoolean t = new AtomicBoolean(true);
                ConditionExecutor.entityCondition.check((JSONObject) condition.get("condition"), actor, null, block, fluid, itemStack, entityDamageEvent).ifPresent(bool -> a.set(bool));
                ConditionExecutor.entityCondition.check((JSONObject) condition.get("condition"), target, null, block, fluid, itemStack, entityDamageEvent).ifPresent(bool -> t.set(bool));

                return getResult(inverted, Optional.of(a.get() || t.get()));
            }
            case "apoli:invert" -> {
                return ConditionExecutor.biEntityCondition.check((JSONObject) condition.get("condition"), target, actor, block, fluid, itemStack, entityDamageEvent);
            }
            case "apoli:undirected" -> {
                AtomicBoolean a = new AtomicBoolean(true); // Not swapped
                AtomicBoolean b = new AtomicBoolean(true); // Swapped

                ConditionExecutor.entityCondition.check((JSONObject) condition.get("condition"), actor, target, block, fluid, itemStack, entityDamageEvent).ifPresent(bool -> a.set(bool));
                ConditionExecutor.entityCondition.check((JSONObject) condition.get("condition"), target, actor, block, fluid, itemStack, entityDamageEvent).ifPresent(bool -> b.set(bool));

                return getResult(inverted, Optional.of(a.get() || b.get()));
            }
            case "apoli:actor_condition" -> {
                if (actor == null) {
                    return getResult(inverted, Optional.of(false));
                }
                return ConditionExecutor.entityCondition.check((JSONObject) condition.get("condition"), actor, null, block, fluid, itemStack, entityDamageEvent);
            }
            case "apoli:target_condition" -> {
                if (target == null) {
                    return getResult(inverted, Optional.of(false));
                }
                return ConditionExecutor.entityCondition.check((JSONObject) condition.get("condition"), target, null, block, fluid, itemStack, entityDamageEvent);
            }
            case "apoli:relative_rotation" -> {
                net.minecraft.world.entity.Entity nmsActor = ((CraftEntity)actor).getHandle();
                net.minecraft.world.entity.Entity nmsTarget = ((CraftEntity)target).getHandle();

                RotationType actorRotationType = Utils.getRotationType(condition.get("actor_rotation").toString());
                RotationType targetRotationType = Utils.getRotationType(condition.get("target_rotation").toString());

                Vec3 actorRotation = actorRotationType.getRotation(nmsActor);
                Vec3 targetRotation = targetRotationType.getRotation(nmsTarget);

                ArrayList<String> strings = new ArrayList<>();
                if(condition.containsKey("axes")){
                    for(Object object : ((JSONArray)condition.get("axes"))){
                        strings.add(object.toString());
                    }
                }else{
                    ArrayList<String> deSt = new ArrayList<>();
                    deSt.add("x");
                    deSt.add("y");
                    deSt.add("z");
                    strings.addAll(deSt);
                }

                EnumSet<Direction.Axis> axes = EnumSet.noneOf(Direction.Axis.class);
                strings.forEach(axis -> axes.add(Direction.Axis.valueOf(axis)));

                actorRotation = reduceAxes(actorRotation, axes);
                targetRotation = reduceAxes(targetRotation, axes);
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());

                return getResult(inverted, Optional.of(Utils.compareValues(getAngleBetween(actorRotation, targetRotation), comparison, compare_to)));
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
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                return getResult(inverted, Optional.of(Utils.compareValues(((CraftEntity)actor).getHandle().position().distanceToSqr(((CraftEntity)target).getHandle().position()), comparison, compare_to)));
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
            case "apoli:equal" -> {
                return getResult(inverted, Optional.of(actor == target));
            }
            default -> {
                return Optional.empty();
            }
        }
    }
    // Apoli -- remapped -- added for accuracy

    private static double getAngleBetween(Vec3 a, Vec3 b) {
        double dot = a.dot(b);
        return dot / (a.length() * b.length());
    }

    private static Vec3 reduceAxes(Vec3 vector, EnumSet<Direction.Axis> axesToKeep) {
        return new Vec3(
                axesToKeep.contains(Direction.Axis.X) ? vector.x : 0,
                axesToKeep.contains(Direction.Axis.Y) ? vector.y : 0,
                axesToKeep.contains(Direction.Axis.Z) ? vector.z : 0
        );
    }

    private static Vec3 getBodyRotationVector(net.minecraft.world.entity.Entity entity) {

        if (!(entity instanceof LivingEntity livingEntity)) {
            return entity.getViewVector(1.0f);
        }

        float f = livingEntity.getXRot() * ((float) Math.PI / 180);
        float g = -livingEntity.getYRot() * ((float) Math.PI / 180);

        float h = Mth.cos(g);
        float i = Mth.sin(g);
        float j = Mth.cos(f);
        float k = Mth.sin(f);

        return new Vec3(i * j, -k, h * j);

    }

    public enum RotationType {

        HEAD(e -> e.getViewVector(1.0F)),
        BODY(BiEntityConditions::getBodyRotationVector);

        private final Function<net.minecraft.world.entity.Entity, Vec3> function;
        RotationType(Function<net.minecraft.world.entity.Entity, Vec3> function) {
            this.function = function;
        }

        public Vec3 getRotation(net.minecraft.world.entity.Entity entity) {
            return function.apply(entity);
        }

    }
    // Apoli end
}
