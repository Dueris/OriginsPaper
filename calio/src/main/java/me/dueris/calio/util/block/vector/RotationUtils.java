package me.dueris.calio.util.block.vector;

import java.util.EnumSet;
import java.util.function.Function;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class RotationUtils {
    
    // Apoli -- remapped -- added for accuracy
    public static double getAngleBetween(Vec3 a, Vec3 b) { // GenesisMC private -> public
        double dot = a.dot(b);
        return dot / (a.length() * b.length());
    }

    public static Vec3 reduceAxes(Vec3 vector, EnumSet<Direction.Axis> axesToKeep) { // GenesisMC private -> public
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
        BODY(RotationUtils::getBodyRotationVector);

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
