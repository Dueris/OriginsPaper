package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.chunk.ChunkManagerWorld;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_block_render;

// TODO: Make better
public class ModifyBlockRenderPower extends CraftPower {

    @Override
    public void run(Player player, Power power) {
        ChunkManagerWorld chunkManagerWorld = new ChunkManagerWorld(player.getWorld());
        CraftPlayer craftPlayer = (CraftPlayer) player;
        List<BlockState> blockChanges = new ArrayList<>();
        boolean conditionMet = false;
        Material targetMaterial = Material.AIR;

        for (Chunk chunk : chunkManagerWorld.getChunksInPlayerViewDistance(craftPlayer)) {
            for (Block block : chunkManagerWorld.getAllBlocksInChunk(chunk)) {
                if (block.getType() != Material.AIR) {
                    try {
                        if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) player) && ConditionExecutor.testBlock(power.getJsonObject("block_condition"), (CraftBlock) block)) {
                            conditionMet = true;
                            targetMaterial = Material.getMaterial(power.getStringOrDefault("block", null).toUpperCase());
                            setActive(player, power.getTag(), true);
                            BlockState blockState = block.getState();
                            blockState.setType(targetMaterial);
                            blockChanges.add(blockState);
                            break;
                        } else {
                            setActive(player, power.getTag(), false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        craftPlayer.sendBlockChanges(blockChanges);
    }

    @Override
    public String getType() {
        return "apoli:modify_block_render";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return modify_block_render;
    }
}