package me.dueris.genesismc.factory.conditions.types;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.apoli.EntitySetPower;
import me.dueris.genesismc.registry.Registerable;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Fluid;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import it.unimi.dsi.fastutil.Pair;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class BiEntityConditions implements Listener {

    public void prep(){
        register(new ConditionFactory(GenesisMC.apoliIdentifier("both"), (condition, pair) -> {
            AtomicBoolean a = new AtomicBoolean(true);
            AtomicBoolean t = new AtomicBoolean(true);
            a.set(ConditionExecutor.testEntity((JSONObject) condition.get("condition"), pair.first())); // actor
            t.set(ConditionExecutor.testEntity((JSONObject) condition.get("condition"), pair.second())); // target

            return a.get() && t.get();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("either"), (condition, pair) -> {
            AtomicBoolean a = new AtomicBoolean(true);
            AtomicBoolean t = new AtomicBoolean(true);
            a.set(ConditionExecutor.testEntity((JSONObject) condition.get("condition"), pair.first())); // actor
            t.set(ConditionExecutor.testEntity((JSONObject) condition.get("condition"), pair.second())); // target

            return a.get() || t.get();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("invert"), (condition, pair) -> {
            return ConditionExecutor.testBiEntity((JSONObject) condition.get("condition"), pair.second(), pair.first());
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("undirected"), (condition, pair) -> {
            AtomicBoolean a = new AtomicBoolean(true); // Not swapped
            AtomicBoolean b = new AtomicBoolean(true); // Swapped

            a.set(ConditionExecutor.testBiEntity((JSONObject) condition.get("condition"), pair.first(), pair.second())); // actor, target
            b.set(ConditionExecutor.testBiEntity((JSONObject) condition.get("condition"), pair.second(), pair.first())); // target, actor

            return a.get() || b.get();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("actor_condition"), (condition, pair) -> {
            return ConditionExecutor.testEntity((JSONObject) condition.get("condition"), pair.first());
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("target_condition"), (condition, pair) -> {
            return ConditionExecutor.testEntity((JSONObject) condition.get("condition"), pair.second());
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("relative_rotation"), (condition, pair) -> {
            net.minecraft.world.entity.Entity nmsActor = pair.first().getHandle();
            net.minecraft.world.entity.Entity nmsTarget = pair.second().getHandle();

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

            return Utils.compareValues(getAngleBetween(actorRotation, targetRotation), comparison, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("attack_target"), (condition, pair) -> {
            net.minecraft.world.entity.Entity craftActor = pair.first().getHandle();
            net.minecraft.world.entity.Entity craftTarget = pair.second().getHandle();

            return (craftActor instanceof Mob mobActor && craftTarget.equals(mobActor.getTarget())) || (craftActor instanceof NeutralMob angerableActor && craftTarget.equals(angerableActor.getTarget()));
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("attacker"), (condition, pair) -> {
            net.minecraft.world.entity.Entity craftActor = pair.first().getHandle();
            net.minecraft.world.entity.Entity craftTarget = pair.second().getHandle();

            return craftTarget instanceof LivingEntity livingEntity && craftActor.equals(livingEntity.lastHurtByMob);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("distance"), (condition, pair) -> {
            String comparison = condition.get("comparison").toString();
            double compare_to = Double.parseDouble(condition.get("compare_to").toString());
            return Utils.compareValues(pair.first().getHandle().position().distanceToSqr(pair.second().getHandle().position()), comparison, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("in_set"), (condition, pair) -> {
            return EntitySetPower.isInEntitySet(pair.second(), condition.get("set").toString());
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("can_see"), (condition, pair) -> {
            if (pair.first() instanceof Player pl) {
                return pl.canSee(pair.second());
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("owner"), (condition, pair) -> {
            if (pair.second() instanceof Tameable tameable) {
                return tameable.getOwner().equals(pair.first());
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("riding_recursive"), (condition, pair) -> {
            return pair.first().getPassengers().contains(pair.second());
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("riding_root"), (condition, pair) -> {
            for (int i = 0; i < pair.first().getPassengers().toArray().length; i++) {
                if (pair.first().getPassengers().isEmpty()) return false;
                if (pair.first().getPassengers().get(i) != null) {
                    return i == pair.first().getPassengers().toArray().length;
                } else {
                    return false;
                }
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("riding"), (condition, pair) -> {
            return pair.second().getPassengers().contains(pair.first());
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("equals"), (condition, pair) -> {
            return pair.first() == pair.second();
        }));
    }

    private void register(ConditionFactory factory){
        GenesisMC.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION).register(factory);
    }

    public class ConditionFactory implements Registerable {
        NamespacedKey key;
        BiPredicate<JSONObject, Pair<CraftEntity, CraftEntity>> test;

        public ConditionFactory(NamespacedKey key, BiPredicate<JSONObject, Pair<CraftEntity, CraftEntity>> test){
            this.key = key;
            this.test = test;
        }

        public boolean test(JSONObject condition, Pair<CraftEntity, CraftEntity> tester){
            return test.test(condition, tester);
        }

        @Override
        public NamespacedKey getKey() {
            return key;
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