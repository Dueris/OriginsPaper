package me.dueris.genesismc.factory.data.types;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Vector3f;

/**
 * Original code by Apace100 in the repository of apace100/apoli
 * https://github.com/apace100/apoli/blob/1.20/src/main/java/io/github/apace100/apoli/util/Space.java
 * <p>
 * This code has been modified from its original form to support the Paper-provided mappings for Mojang.
 */
public enum Space {
    WORLD, LOCAL, LOCAL_HORIZONTAL, LOCAL_HORIZONTAL_NORMALIZED, VELOCITY, VELOCITY_NORMALIZED, VELOCITY_HORIZONTAL, VELOCITY_HORIZONTAL_NORMALIZED;

    public static void transformVectorToBase(Vec3 baseForwardVector, Vector3f vector, float baseYaw, boolean normalizeBase) {

        double baseScaleD = baseForwardVector.length();
        if (baseScaleD <= 0.007D) {
            vector.zero();
        } else {
            float baseScale = (float) baseScaleD;

            Vec3 normalizedBase = baseForwardVector.normalize();

            Matrix3f transformMatrix = getBaseTransformMatrixFromNormalizedDirectionVector(normalizedBase, baseYaw);
            if (!normalizeBase)
                transformMatrix.scale(baseScale, baseScale, baseScale);
            vector.mulTranspose(transformMatrix);
        }
    }

    private static Matrix3f getBaseTransformMatrixFromNormalizedDirectionVector(Vec3 vector, float yaw) {
        double xX, xZ,
                zX = 0.0D, zY = vector.y(), zZ = 0.0D;

        if (Math.abs(zY) != 1.0F) {
            zX = vector.x();
            zZ = vector.z();

            xX = vector.z();
            xZ = -vector.x();

            float xFactor = (float) (1 / Math.sqrt(xX * xX + xZ * xZ));
            xX *= xFactor;
            xZ *= xFactor;
        } else {

            float trigonometricYaw = -yaw * 0.0174532925F; // pi / 180 = 0.0174532925
            xX = Mth.cos(trigonometricYaw);
            xZ = -Mth.sin(trigonometricYaw);
        }

        Matrix3f res = new Matrix3f();

        res.set(0, 0, (float) xX);
        res.set(1, 0, 0.0F);
        res.set(2, 0, (float) xZ);

        res.set(0, 1, (float) (zY * xZ));
        res.set(1, 1, (float) (zZ * xX - zX * xZ));
        res.set(2, 1, (float) (-zY * xX));

        res.set(0, 2, (float) zX);
        res.set(1, 2, (float) zY);
        res.set(2, 2, (float) zZ);
        return res;
    }

    public static Space getSpace(String string) {
        return Space.valueOf(string.toUpperCase());
    }

    public void toGlobal(Vector3f vector, Entity entity) {
        Vec3 baseForwardVector;

        switch (this) {

            case WORLD:
                break;

            case LOCAL:
            case LOCAL_HORIZONTAL:
            case LOCAL_HORIZONTAL_NORMALIZED:
                baseForwardVector = entity.getLookAngle();
                if (this != LOCAL) // horizontal
                    baseForwardVector = new Vec3(baseForwardVector.x(), 0, baseForwardVector.z());
                transformVectorToBase(baseForwardVector, vector, entity.getYRot(), this == LOCAL_HORIZONTAL_NORMALIZED);
                break;

            case VELOCITY:
            case VELOCITY_NORMALIZED:
            case VELOCITY_HORIZONTAL:
            case VELOCITY_HORIZONTAL_NORMALIZED:
                baseForwardVector = entity.getDeltaMovement();
                if (this == VELOCITY_HORIZONTAL || this == VELOCITY_HORIZONTAL_NORMALIZED)
                    baseForwardVector = new Vec3(baseForwardVector.x(), 0, baseForwardVector.z());
                transformVectorToBase(baseForwardVector, vector, entity.getYRot(), this == VELOCITY_NORMALIZED || this == VELOCITY_HORIZONTAL_NORMALIZED);
                break;
        }
    }
}
