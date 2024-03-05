package me.dueris.calio.builder.inst;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class FactoryProvider extends JSONObject {
    JSONObject raw;

    public FactoryProvider(){
        super();
    }

    public FactoryProvider(Map map){
        super(map);
    }

    public ItemStack getItemStack(String accessor){
        Object inst = this.get(accessor);
        if(inst instanceof ItemStack){
            return (ItemStack)inst;
        } else if(inst instanceof JSONObject obj){
            String materialVal = "head";
            int amt = 1;
            if(obj.containsKey("item")){
                materialVal = obj.get("item").toString();
            }
            if(obj.containsKey("amount")){
                amt = Integer.valueOf(obj.get("amount").toString());
            }
            return new ItemStack(Material.valueOf(NamespacedKey.fromString(materialVal).asString().split(":")[1].toUpperCase()), amt);
        }else{ // Assuming provided string
            return new ItemStack(Material.valueOf(NamespacedKey.fromString(inst.toString()).asString().split(":")[1].toUpperCase()));
        }
    }

    public NamespacedKey getNamespacedKey(String accessor){
        Object inst = this.get(accessor);
        if(inst instanceof NamespacedKey) return (NamespacedKey) inst;
        return NamespacedKey.fromString(inst.toString().toLowerCase());
    }

    public FactoryProvider getFactoryProvider(String accessor){
        Object inst = this.get(accessor);
        if(inst == null) return new FactoryProvider(new JSONObject());
        return new FactoryProvider((JSONObject) inst);
    }

    public JSONObject getJsonObject(String accessor){
        Object inst = this.get(accessor);
        if(inst == null) return new JSONObject();
        return (JSONObject) inst;
    }

    public JSONArray getJsonArray(String accessor){
        Object inst = this.get(accessor);
        if(inst == null) return new JSONArray();
        return (JSONArray) inst;
    }

    public JsonElement getGsonElement(){
        String string = this.toJSONString();
        return JsonParser.parseString(string);
    }

    public Object retrive(String type, Class objType){
        if(this.containsKey(type)){
            if(objType.equals(ItemStack.class)){
                return getItemStack(type);
            } else
            if(objType.equals(NamespacedKey.class)){
                return getNamespacedKey(type);
            } else
            if(objType.equals(FactoryProvider.class)){
                return getFactoryProvider(type);
            }
            if(objType.equals(JSONObject.class)){
                return getJsonObject(type);
            }
            {
                return this.getFromGson(type);
            }
        }
        return null;
    }

    public Object getFromGson(String type){
        if(this.get(type) instanceof JSONArray objj){
            return objj;
        }
        JsonObject root = this.getGsonElement().getAsJsonObject();
        if(root.has(type)){
            JsonElement element = root.get(type);
            if(element.isJsonPrimitive()){
                JsonPrimitive primitive = element.getAsJsonPrimitive();
                if(primitive.isString()){
                    return element.getAsString();
                }
                if(primitive.isBoolean()){
                    return element.getAsBoolean();
                }
                if(primitive.isNumber()){
                    Number number = primitive.getAsNumber();
                    if(number.toString().contains(".")){
                        return primitive.getAsFloat();
                    }else{
                        return primitive.getAsInt();
                    }
                }
            }
        }
        return null;
    }
}
