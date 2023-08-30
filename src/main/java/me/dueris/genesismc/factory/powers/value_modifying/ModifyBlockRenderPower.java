package me.dueris.genesismc.factory.powers.value_modifying;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.world.chunk.ChunkManagerWorld;
import me.dueris.genesismc.utils.ErrorSystem;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_block_render;

public class ModifyBlockRenderPower extends CraftPower {

    String MODIFYING_KEY = "modify_block_render";

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ChunkManagerWorld chunkManagerWorld = new ChunkManagerWorld(player.getWorld());
            CraftPlayer craftPlayer = (CraftPlayer) player;

            if (modify_block_render.contains(player)) {
                List<BlockState> blockChanges = new ArrayList<>();
                boolean conditionMet = false;

                for (OriginContainer origin : OriginPlayer.getOrigin(player).values()) {
                    Material targetMaterial = Material.AIR;
                    if (conditionMet) {
                        targetMaterial = Material.getMaterial(origin.getPowerFileFromType("origins:modify_block_render").get("block", null).toUpperCase());
                    }

                    for (Chunk chunk : chunkManagerWorld.getChunksInPlayerViewDistance(craftPlayer)) {
                        for (Block block : chunkManagerWorld.getAllBlocksInChunk(chunk)) {
                            if (block.getType() != Material.AIR) {
                                try {
                                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                                    if (conditionExecutor.check("block_condition", "block_conditions", player, origin, "origins:modify_block_render", player, null, block, null, player.getItemInHand(), null)) {
                                        conditionMet = true;
                                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                                        BlockState blockState = block.getState();
                                        blockState.setType(targetMaterial);
                                        blockChanges.add(blockState);
                                        break;
                                    } else {
                                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                                    }
                                } catch (Exception e) {
                                    ErrorSystem errorSystem = new ErrorSystem();
                                    errorSystem.throwError("unable to send block_render_change", "origins:modify_block_render", player, origin, OriginPlayer.getLayer(player, origin));
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    craftPlayer.sendBlockChanges(blockChanges);
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:modify_block_render";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_block_render;
    }
}