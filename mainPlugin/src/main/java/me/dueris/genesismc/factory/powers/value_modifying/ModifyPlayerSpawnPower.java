package me.dueris.genesismc.factory.powers.value_modifying;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;

import org.apache.logging.log4j.core.lookup.EnvironmentLookup;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.generator.structure.Structure;

import com.mojang.logging.LogUtils;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_world_spawn;
import static org.bukkit.Material.AIR;
import static org.bukkit.Material.OBSIDIAN;

public class ModifyPlayerSpawnPower extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool) {
        powers_active.put(tag, bool); // Simplified code
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void runD(PlayerRespawnEvent e) {
        if (e.getRespawnReason().equals(PlayerRespawnEvent.RespawnReason.END_PORTAL)) return;
            if (e.getRespawnReason().equals(PlayerRespawnEvent.RespawnReason.PLUGIN)) return;
            runHandle(e.getPlayer());
    }

    @EventHandler
    public void runO(OriginChangeEvent e){
        if(!OriginPlayer.hasFirstChose(e.getPlayer())){
            runHandle(e.getPlayer());
        }
    }

    public void runHandle(Player p){
        if (modify_world_spawn.contains(p)) {
            // if(p.getBedSpawnLocation() != null) return;
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                PowerContainer power = origin.getSinglePowerFileFromType(getPowerFile());
                if(executor.check("condition", "conditions", p, power,getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getInventory().getItemInMainHand(), null)){
                    String spawnStrat = power.get("spawn_strategy", "default");
                    float dimMult = 0.125f;
                    String dimension = power.get("dimension");
                    if(power.get("dimension_distance_multiplier") != null){
                        dimMult = Float.valueOf(power.get("dimension_distance_multiplier"));
                    }
                    if(!dimension.startsWith("minecraft:") && !dimension.contains(":")){
                        dimension = "minecraft:" + dimension;
                    }
                    World world = Bukkit.getWorld(NamespacedKey.fromString(dimension));
                    Location teleportLoc = new Location(world, 0, 0, 0);
                    Location centerPosLoc = new Location(world, 0, 70, 0);
                    if(world.getEnvironment() != Environment.NETHER){
                        centerPosLoc.setY(world.getHighestBlockYAt(0, 0));
                    }
                    GenesisMC.sendDebug(world.getName());
                    int[] possibleVerticalMovement = {0, 1};
                    GenesisMC.sendDebug(spawnStrat);
                    GenesisMC.sendDebug("DimensionBuilder started");
                        // Obsidian platform
                        for(int x = -2; x < 3; x++){
                            for(int z = -2; z < 3; z++){
                                Block bl = world.getBlockAt(centerPosLoc.clone().add(x, 0, z));
                                if(!bl.isCollidable()){
                                    bl.setType(OBSIDIAN);
                                }
                                GenesisMC.sendDebug(centerPosLoc.clone().add(x, 0, z));
                            }
                        }
                        for(int x = -2; x < 3; x++){
                            for(int z = -2; z < 3; z++){
                                Block bl = world.getBlockAt(centerPosLoc.clone().add(x, 1, z));
                                if(bl.isCollidable() || bl.isLiquid()){
                                    bl.setType(AIR);
                                }
                                GenesisMC.sendDebug(centerPosLoc.clone().add(x, 1, z));
                            }
                        }
                        for(int x = -2; x < 3; x++){
                            for(int z = -2; z < 3; z++){
                                Block bl = world.getBlockAt(centerPosLoc.clone().add(x, 2, z));
                                if(bl.isCollidable() || bl.isLiquid()){
                                    bl.setType(AIR);
                                }
                                GenesisMC.sendDebug(centerPosLoc.clone().add(x, 2, z));
                            }
                        }
                        p.sendMessage(String.valueOf(centerPosLoc.y()) + " == y location of block to spawn at");
                        teleportLoc = centerPosLoc.add(0, 2, 0);
                    GenesisMC.sendDebug("DimensionBuilder finished");
                    if(teleportLoc != null){
                        p.teleportAsync(teleportLoc);
                    }else{
                        throw new RuntimeException("Unable to create suitable spawn for player({p})."
                            .replace("{p}", p.getName())
                        );
                    }
                }
            }
        }
    }

    public static org.bukkit.generator.structure.StructureType translate(String input) {
        String cleanedInput = input.replaceAll(" ", "").toLowerCase().split(":")[1];

        switch (cleanedInput) {
            case "igloo":
                return Structure.IGLOO.getStructureType();
            case "desert_pyramid":
                return Structure.DESERT_PYRAMID.getStructureType();
            case "end_city":
                return Structure.END_CITY.getStructureType();
            case "fortress":
                return Structure.FORTRESS.getStructureType();
            case "jungle_pyramid":
                return Structure.JUNGLE_PYRAMID.getStructureType();
            case "mansion":
                return Structure.MANSION.getStructureType();
            case "mineshaft":
                return Structure.MINESHAFT.getStructureType();
            case "monument":
                return Structure.MONUMENT.getStructureType();
            case "nether_fossil":
                return Structure.NETHER_FOSSIL.getStructureType();
            case "ocean_ruin_cold":
                return Structure.OCEAN_RUIN_COLD.getStructureType();
            case "ocean_ruin_warm":
                return Structure.OCEAN_RUIN_WARM.getStructureType();
            case "pillager_outpost":
                return Structure.PILLAGER_OUTPOST.getStructureType();
            case "shipwreck":
                return Structure.SHIPWRECK.getStructureType();
            case "stronghold":
                return Structure.STRONGHOLD.getStructureType();
            case "swamp_hut":
                return Structure.SWAMP_HUT.getStructureType();
            case "village":
                return Structure.VILLAGE_PLAINS.getStructureType();
            case "bastion_remnant":
                return Structure.BASTION_REMNANT.getStructureType();
            case "ancient_city":
                return Structure.ANCIENT_CITY.getStructureType();
            case "ruined_portal":
                return Structure.RUINED_PORTAL.getStructureType();
            default:
                return null;
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:modify_player_spawn";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_world_spawn;
    }
}
