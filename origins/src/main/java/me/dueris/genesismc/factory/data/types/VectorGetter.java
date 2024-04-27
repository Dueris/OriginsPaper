package me.dueris.genesismc.factory.data.types;

import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.util.CraftVector;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

public class VectorGetter {
    public static Vector getVector(FactoryJsonObject object) {
        float x = 0.0f;
        float y = 0.0f;
        float z = 0.0f;

        if (object.isPresent("x"))
            x = object.getNumber("x").getFloat();
        if (object.isPresent("y"))
            y = object.getNumber("y").getFloat();
        if (object.isPresent("z"))
            z = object.getNumber("z").getFloat();

        return new Vector(x, y, z);
    }

    public static Vec3 getNMSVector(FactoryJsonObject object) {
        return CraftVector.toNMS(getVector(object));
    }

    public static Vector createVector(float x, float y, float z) {
        return new Vector(x, y, z);
    }

    public static Vec3 createNMSVector(float x, float y, float z) {
        return new Vec3(x, y, z);
    }

    public static Vector3f getAsVector3f(FactoryJsonObject object) {
        return getVector(object).toVector3f();
    }
}
