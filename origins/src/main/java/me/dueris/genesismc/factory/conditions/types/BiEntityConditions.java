package me.dueris.genesismc.factory.conditions.types;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.builder.inst.factory.FactoryElement;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.calio.util.ClipContextUtils;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Comparison;
import me.dueris.genesismc.factory.data.types.RotationType;
import me.dueris.genesismc.factory.powers.apoli.EntitySetPower;
import me.dueris.genesismc.registry.Registries;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;

public class BiEntityConditions implements Listener {

    public void prep() {
        register(new ConditionFactory(GenesisMC.apoliIdentifier("both"), (condition, pair) -> {
            AtomicBoolean a = new AtomicBoolean(true);
            AtomicBoolean t = new AtomicBoolean(true);
            a.set(ConditionExecutor.testEntity(condition.getJsonObject("condition"), pair.first())); // actor
            t.set(ConditionExecutor.testEntity(condition.getJsonObject("condition"), pair.second())); // target

            return a.get() && t.get();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("either"), (condition, pair) -> {
            AtomicBoolean a = new AtomicBoolean(true);
            AtomicBoolean t = new AtomicBoolean(true);
            a.set(ConditionExecutor.testEntity(condition.getJsonObject("condition"), pair.first())); // actor
            t.set(ConditionExecutor.testEntity(condition.getJsonObject("condition"), pair.second())); // target

            return a.get() || t.get();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("invert"), (condition, pair) -> ConditionExecutor.testBiEntity(condition.getJsonObject("condition"), pair.second(), pair.first())));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("undirected"), (condition, pair) -> {
            AtomicBoolean a = new AtomicBoolean(true); // Not swapped
            AtomicBoolean b = new AtomicBoolean(true); // Swapped

            a.set(ConditionExecutor.testBiEntity(condition.getJsonObject("condition"), pair.first(), pair.second())); // actor, target
            b.set(ConditionExecutor.testBiEntity(condition.getJsonObject("condition"), pair.second(), pair.first())); // target, actor

            return a.get() || b.get();
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("actor_condition"), (condition, pair) -> ConditionExecutor.testEntity(condition.getJsonObject("condition"), pair.first())));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("target_condition"), (condition, pair) -> ConditionExecutor.testEntity(condition.getJsonObject("condition"), pair.second())));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("relative_rotation"), (condition, pair) -> {
            net.minecraft.world.entity.Entity nmsActor = pair.first().getHandle();
            net.minecraft.world.entity.Entity nmsTarget = pair.second().getHandle();

            RotationType actorRotationType = RotationType.getRotationType(condition.getString("actor_rotation"));
            RotationType targetRotationType = RotationType.getRotationType(condition.getString("target_rotation"));

            Vec3 actorRotation = actorRotationType.getRotation(nmsActor);
            Vec3 targetRotation = targetRotationType.getRotation(nmsTarget);

            ArrayList<String> strings = new ArrayList<>();
            if (condition.isPresent("axes")) {
                strings.addAll(condition.getJsonArray("axes").asList().stream().map(FactoryElement::getString).toList());
            } else {
                ArrayList<String> deSt = new ArrayList<>();
                deSt.add("x");
                deSt.add("y");
                deSt.add("z");
                strings.addAll(deSt);
            }

            EnumSet<Direction.Axis> axes = EnumSet.noneOf(Direction.Axis.class);
            strings.forEach(axis -> axes.add(Direction.Axis.valueOf(axis)));

            actorRotation = RotationType.reduceAxes(actorRotation, axes);
            targetRotation = RotationType.reduceAxes(targetRotation, axes);
            String comparison = condition.getString("comparison");
            float compare_to = condition.getNumber("compare_to").getFloat();

            return Comparison.getFromString(comparison).compare(RotationType.getAngleBetween(actorRotation, targetRotation), compare_to);
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
            String comparison = condition.getString("comparison");
            float compare_to = condition.getNumber("compare_to").getFloat();
            return Comparison.getFromString(comparison).compare(pair.first().getHandle().position().distanceToSqr(pair.second().getHandle().position()), compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("in_set"), (condition, pair) -> EntitySetPower.isInEntitySet(pair.second(), condition.getString("set"))));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("can_see"), (condition, pair) -> {
            Entity nmsActor = pair.first().getHandle();
            Entity nmsTarget = pair.second().getHandle();

            if ((nmsActor == null || nmsTarget == null) || nmsActor.level() != nmsTarget.level()) return false;

            ClipContext.Block shapeType = ClipContextUtils.getShapeType(condition.getStringOrDefault("shape_type", "visual"));
            ClipContext.Fluid fluidHandling = ClipContextUtils.getFluidHandling(condition.getStringOrDefault("fluid_handling", "none"));

            Vec3 actorEyePos = nmsActor.getEyePosition();
            Vec3 targetEyePos = nmsTarget.getEyePosition();
            return nmsActor.level().clip(new ClipContext(actorEyePos, targetEyePos, shapeType, fluidHandling, nmsActor)).getType() == HitResult.Type.MISS;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("owner"), (condition, pair) -> {
            if (pair.second() instanceof Tameable tameable) {
                return tameable.getOwner().equals(pair.first());
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("riding_recursive"), (condition, pair) -> pair.first().getPassengers().contains(pair.second())));
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
        register(new ConditionFactory(GenesisMC.apoliIdentifier("riding"), (condition, pair) -> pair.second().getPassengers().contains(pair.first())));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("equals"), (condition, pair) -> pair.first() == pair.second()));
    }

    private void register(ConditionFactory factory) {
        GenesisMC.getPlugin().registry.retrieve(Registries.BIENTITY_CONDITION).register(factory);
    }

    public class ConditionFactory implements Registrable {
        NamespacedKey key;
        BiPredicate<FactoryJsonObject, Pair<CraftEntity, CraftEntity>> test;

        public ConditionFactory(NamespacedKey key, BiPredicate<FactoryJsonObject, Pair<CraftEntity, CraftEntity>> test) {
            this.key = key;
            this.test = test;
        }

        public boolean test(FactoryJsonObject condition, Pair<CraftEntity, CraftEntity> tester) {
            return test.test(condition, tester);
        }

        @Override
        public NamespacedKey getKey() {
            return key;
        }
    }
}
