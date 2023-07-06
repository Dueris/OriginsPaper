package me.dueris.genesismc.core.factory.conditions.entity;

import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class EntityCondition {

    public static boolean check(Player p, OriginContainer origin, String powerfile, Entity entity){
        if(origin.getPowerFileFromType(powerfile).getEntityCondition() == null) return false;
        if(origin.getPowerFileFromType(powerfile).getEntityCondition().get("type") == null) return false;
        p.sendMessage("entity_start");
        String type = origin.getPowerFileFromType(powerfile).getEntityCondition().get("type").toString();
        if(type.equalsIgnoreCase("origins:ability")){
            String ability = origin.getPowerFileFromType(powerfile).getEntityCondition().get("ability").toString();
            if(ability.equalsIgnoreCase("minecraft:flying")){
                if(entity instanceof Player){
                    Player player = (Player) entity;
                    if(player.isFlying()) return true;
                }
            }
            if(ability.equalsIgnoreCase("minecraft:instabuild")){
                if(entity instanceof Player){
                    Player player = (Player) entity;
                    if(player.getGameMode().equals(GameMode.CREATIVE)) return true;
                }
            }
            if(ability.equalsIgnoreCase("minecraft:invuln" +
                    "rable")){
                if(entity.isInvulnerable()) return true;
            }
            if(ability.equalsIgnoreCase("minecraft:maybuild")){
                if(entity.hasPermission("minecraft.build")){
                    return true;
                }
            }
            if(ability.equalsIgnoreCase("minecraft:mayfly")){
                if(entity instanceof Player){
                    Player player = (Player) entity;
                    if(player.getAllowFlight() == true) return true;
                }
            }
        }

        if(type.equalsIgnoreCase("origins:advancement")){
            String advancementString = origin.getPowerFileFromType(powerfile).getEntityCondition().get("advancement").toString();
            if(entity instanceof Player){
                Player player = (Player) entity;
                player.sendMessage(advancementString);
                World world = player.getWorld();
                File worldFolder = world.getWorldFolder();
                File advancementsFolder = new File(worldFolder, "advancements");
                File playerAdvancementFile = new File(advancementsFolder, player.getUniqueId() + ".json");

                if (playerAdvancementFile.exists()) {
                    player.sendMessage("exists");
                    try {
                        JSONParser parser = new JSONParser();
                        JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(playerAdvancementFile));
                        JSONObject advancementJson = (JSONObject) jsonObject.get(advancementString);

                        if (advancementJson != null) {
                            player.sendMessage("json");
                            Boolean done = (Boolean) advancementJson.get("done");
                            if (done != null) {
                                player.sendMessage("notnull");
                                player.sendMessage(done.toString());
                                if(done.toString() == "true"){
                                    return true;
                                }
                            }else{return false;}
                        }else{return false;}
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }else{return false;}
            }
        }
        return false;
    }
}
