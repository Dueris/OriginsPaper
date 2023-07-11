package me.dueris.genesismc.core.factory.conditions.entity;

import me.dueris.genesismc.core.factory.powers.armour.RestrictArmor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class EntityCondition {

    public static String check(Player p, OriginContainer origin, String powerfile, Entity entity){
        if(origin.getPowerFileFromType(powerfile).getEntityCondition() == null) return "null";
        if(origin.getPowerFileFromType(powerfile).getEntityCondition().get("type") == null) return "null";
        p.sendMessage("entity_start");
        String type = origin.getPowerFileFromType(powerfile).getEntityCondition().get("type").toString();
        if(type.equalsIgnoreCase("origins:ability")){
            String ability = origin.getPowerFileFromType(powerfile).getEntityCondition().get("ability").toString();
            if(ability.equalsIgnoreCase("minecraft:flying")){
                if(entity instanceof Player){
                    Player player = (Player) entity;
                    if(player.isFlying()) return "true";
                }
            }
            if(ability.equalsIgnoreCase("minecraft:instabuild")){
                if(entity instanceof Player){
                    Player player = (Player) entity;
                    if(player.getGameMode().equals(GameMode.CREATIVE)) return "true";
                }
            }
            if(ability.equalsIgnoreCase("minecraft:invuln" +
                    "rable")){
                if(entity.isInvulnerable()) return "true";
            }
            if(ability.equalsIgnoreCase("minecraft:maybuild")){
                if(entity.hasPermission("minecraft.build")){
                    return "true";
                }
            }
            if(ability.equalsIgnoreCase("minecraft:mayfly")){
                if(entity instanceof Player){
                    Player player = (Player) entity;
                    if(player.getAllowFlight() == true) return "true";
                }
            }
        }

        if(type.equalsIgnoreCase("origins:advancement")){
            String advancementString = origin.getPowerFileFromType(powerfile).getEntityCondition().get("advancement").toString();
            if(entity instanceof Player){
                Player player = (Player) entity;
                World world = player.getWorld();
                File worldFolder = world.getWorldFolder();
                File advancementsFolder = new File(worldFolder, "advancements");
                File playerAdvancementFile = new File(advancementsFolder, player.getUniqueId() + ".json");

                if (playerAdvancementFile.exists()) {
                    try {
                        JSONParser parser = new JSONParser();
                        JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(playerAdvancementFile));
                        JSONObject advancementJson = (JSONObject) jsonObject.get(advancementString);

                        if (advancementJson != null) {
                            Boolean done = (Boolean) advancementJson.get("done");
                            if (done != null) {
                                if(done.toString() == "true"){
                                    return "true";
                                }
                            }else{return "false";}
                        }else{return "false";}
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }else{return "false";}
            }
        }

        if(type.equalsIgnoreCase("origins:air")){
            if(entity instanceof Player player){
                if(RestrictArmor.compareValues(player.getRemainingAir(), origin.getPowerFileFromType(powerfile).getEntityCondition().get("comparison").toString(), Integer.valueOf(origin.getPowerFileFromType(powerfile).getEntityCondition().get("compare_to").toString())) == true){
                    return "true";
                }
            }
        }

        if(type.equalsIgnoreCase("origins:attribute")){
            if(entity instanceof Player player){
                String attributeString = origin.getPowerFileFromType(powerfile).getEntityCondition().get("attribute").toString().split(":")[1].replace(".", "_").toUpperCase();
                if(RestrictArmor.compareValues(player.getAttribute(Attribute.valueOf(attributeString)).getValue(), origin.getPowerFileFromType(powerfile).getEntityCondition().get("comparison").toString(), Integer.valueOf(origin.getPowerFileFromType(powerfile).getEntityCondition().get("compare_to").toString())) == true){
                    return "true";
                }
            }
        }

        // TODO: continue entity_condition to use biome condition for origins:biome in some cases. see https://origins.readthedocs.io/en/latest/types/entity_condition_types/biome/

        if(type.equalsIgnoreCase("origins:biome")){
            String biomeString = origin.getPowerFileFromType(powerfile).getEntityCondition().get("biome").toString().split(":")[1].replace(".", "_").toUpperCase();
            if(entity.getLocation().getBlock().getBiome().equals(Biome.valueOf(biomeString))){
                return "true";
            }
        }

        return "false";
    }
}
