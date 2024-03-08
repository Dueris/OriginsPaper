package me.dueris.calio.data;

import org.bukkit.craftbukkit.v1_20_R3.util.CraftVector;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;

import net.minecraft.world.phys.Vec3;

public class VectorParser {
    public static Vector getVector(JSONObject object){
        float x = 0.0f;
        float y = 0.0f;
        float z = 0.0f;

        if(object.containsKey("x")) x = (float)object.get("x");
        if(object.containsKey("y")) y = (float)object.get("y");
        if(object.containsKey("z")) z = (float)object.get("z");

        return new Vector(x, y, z);
    }

    public static Vec3 getNMSVector(JSONObject object){
        return CraftVector.toNMS(getVector(object));
    }

    public static Vector createVector(float x, float y, float z){
        return new Vector(x, y, z);
    }

    public static Vec3 createNMSVector(float x, float y, float z){
        return new Vec3(x, y, z);
    }
}
