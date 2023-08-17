package me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.core.factory.powers.OriginsMod.world.chunk.ChunkManagerWorld;
import me.dueris.genesismc.core.utils.ErrorSystem;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_block_render;

public class ModifyBlockRenderPower extends BukkitRunnable {

    String MODIFYING_KEY = "modify_block_render";

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ChunkManagerWorld chunkManagerWorld = new ChunkManagerWorld(player.getWorld());
            CraftPlayer craftPlayer = (CraftPlayer) player;

            if (modify_block_render.contains(player)) {
                List<BlockState> blockChanges = new ArrayList<>();
                boolean conditionMet = false;

                for (OriginContainer origin : OriginPlayer.getOrigin(player).values()) {
                    try {
                        if (ConditionExecutor.check("block_condition", "block_conditions", player, origin, "origins:modify_block_render", null, player)) {
                            conditionMet = true;
                            break;
                        }
                    } catch (Exception e) {
                        ErrorSystem errorSystem = new ErrorSystem();
                        errorSystem.throwError("unable to send block_render_change", "origins:modify_block_render", player, origin, OriginPlayer.getLayer(player, origin));
                        e.printStackTrace();
                    }

                Material targetMaterial = Material.AIR;
                if (conditionMet) {
                    targetMaterial = Material.getMaterial(origin.getPowerFileFromType("origins:modify_block_render").get("block", null).toString().toUpperCase());
                }

                for (Chunk chunk : chunkManagerWorld.getChunksInPlayerViewDistance(craftPlayer)) {
                    for (Block block : chunkManagerWorld.getAllBlocksInChunk(chunk)) {
                        if (block.getType() != Material.AIR) {
                            //TODO: add blcok condityion for the materail
                            BlockState blockState = block.getState();
                            blockState.setType(targetMaterial);
                            blockChanges.add(blockState);
                        }
                    }
                }

                craftPlayer.sendBlockChanges(blockChanges);
                }
            }
        }
    }
}