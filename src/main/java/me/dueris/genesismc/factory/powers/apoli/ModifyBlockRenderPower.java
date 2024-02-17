package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import me.dueris.genesismc.util.world.chunk.ChunkManagerWorld;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_block_render;

public class ModifyBlockRenderPower extends CraftPower {

    String MODIFYING_KEY = "modify_block_render";

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    @Override
    public void run(Player player) {
        ChunkManagerWorld chunkManagerWorld = new ChunkManagerWorld(player.getWorld());
        CraftPlayer craftPlayer = (CraftPlayer) player;

        if (modify_block_render.contains(player)) {
            List<BlockState> blockChanges = new ArrayList<>();
            boolean conditionMet = false;

            for (LayerContainer layer : CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                    Material targetMaterial = Material.AIR;
                    if (conditionMet) {
                        targetMaterial = Material.getMaterial(power.getStringOrDefault("block", null).toUpperCase());
                    }

                    for (Chunk chunk : chunkManagerWorld.getChunksInPlayerViewDistance(craftPlayer)) {
                        for (Block block : chunkManagerWorld.getAllBlocksInChunk(chunk)) {
                            if (block.getType() != Material.AIR) {
                                try {
                                    ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                                    if (conditionExecutor.check("block_condition", "block_conditions", player, power, "apoli:modify_block_render", player, null, block, null, player.getInventory().getItemInHand(), null)) {
                                        conditionMet = true;
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
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:modify_block_render";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_block_render;
    }
}