package me.dueris.calio.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.BlockFace;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import me.dueris.calio.CraftCalio;
import me.dueris.calio.util.block.vector.RotationUtils;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;

public class InstanceGetter {
    public static List<BlockFace> getBlockFaceFromDirection(JSONArray objects){
        List<BlockFace> list = new ArrayList();
        for(Object object : objects){
            if(object instanceof String string){
                list.add(BlockFace.valueOf(string.toUpperCase()));
            }else{
                CraftCalio.INSTANCE.getLogger().severe("Provided Direction instance is not a String! : " + object.toString());
            }
        }
        return list;
    }

    public static ClipContext.Fluid getFluidHandling(String string){
        switch (string.toLowerCase()) {
            case "none" -> {
                return ClipContext.Fluid.NONE;
            }
            case "any" -> {
                return ClipContext.Fluid.ANY;
            }
            case "source_only" -> {
                return ClipContext.Fluid.SOURCE_ONLY;
            }
            default -> {
                return null;
            }
        }
    }

    public static ClipContext.Block getShapeType(String string){
        switch (string.toLowerCase()) {
            case "collider" -> {
                return ClipContext.Block.COLLIDER;
            }
            case "outline" -> {
                return ClipContext.Block.OUTLINE;
            }
            case "visual" -> {
                return ClipContext.Block.VISUAL;
            }
            default -> {
                return null;
            }
        }
    }

    public static Vec3 createDirection(JSONObject jsonObject){
        if(jsonObject == null || jsonObject.isEmpty()) return null;
        float x = 0;
        float z = 0;
        float y = 0;
        if(jsonObject.containsKey("x")) x = (float) jsonObject.get("x");
        if(jsonObject.containsKey("z")) z = (float) jsonObject.get("z");
        if(jsonObject.containsKey("y")) y = (float) jsonObject.get("y");
        return new Vec3(x, y, z);
    }

    public static RotationUtils.RotationType getRotationType(String string){
        switch (string.toLowerCase()) {
            case "head" -> {
                return RotationUtils.RotationType.HEAD;
            }
            case "body" -> {
                return RotationUtils.RotationType.BODY;
            }
            default -> {
                return RotationUtils.RotationType.BODY;
            }
        }
    }

    public static Space getSpaceFromString(String space) {
        switch (space.toLowerCase()) {
            case "world" -> {
                return Space.WORLD;
            }
            case "local" -> {
                return Space.LOCAL;
            }
            case "local_horizontal" -> {
                return Space.LOCAL_HORIZONTAL;
            }
            case "local_horizontal_normalized" -> {
                return Space.LOCAL_HORIZONTAL_NORMALIZED;
            }
            case "velocity" -> {
                return Space.VELOCITY;
            }
            case "velocity_normalized" -> {
                return Space.VELOCITY_NORMALIZED;
            }
            case "velocity_horizontal" -> {
                return Space.VELOCITY_HORIZONTAL;
            }
            case "velocity_horizontal_normalized" -> {
                return Space.VELOCITY_HORIZONTAL_NORMALIZED;
            }
            default -> {
                return Space.WORLD;
            }
        }
    }
}
