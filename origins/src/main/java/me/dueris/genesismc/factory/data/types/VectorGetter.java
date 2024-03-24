package me.dueris.genesismc.factory.data.types;

import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftVector;
import org.bukkit.util.Vector;
import org.joml.Vector3f;
import org.json.simple.JSONObject;

public class VectorGetter {
    public static Vector getVector(JSONObject object) {
        float x = 0.0f;
        float y = 0.0f;
        float z = 0.0f;

        if (object.containsKey("x"))
            x = object.get("x") instanceof Float ? (float) object.get("x") : Float.valueOf(String.valueOf(object.get("x")));
        if (object.containsKey("y"))
            y = object.get("y") instanceof Float ? (float) object.get("y") : Float.valueOf(String.valueOf(object.get("y")));
        if (object.containsKey("z"))
            z = object.get("z") instanceof Float ? (float) object.get("z") : Float.valueOf(String.valueOf(object.get("z")));

        return new Vector(x, y, z);
    }

    public static Vec3 getNMSVector(JSONObject object) {
        return CraftVector.toNMS(getVector(object));
    }

    public static Vector createVector(float x, float y, float z) {
        return new Vector(x, y, z);
    }

    public static Vec3 createNMSVector(float x, float y, float z) {
        return new Vec3(x, y, z);
    }

    public static Vector3f getAsVector3f(JSONObject object) {
        return getVector(object).toVector3f();
    }
}
