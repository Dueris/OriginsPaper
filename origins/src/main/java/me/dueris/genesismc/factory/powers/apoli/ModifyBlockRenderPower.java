package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.math.Position;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Shape;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.chunk.ChunkManagerWorld;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_block_render;

public class ModifyBlockRenderPower extends CraftPower implements Listener {
    public static ArrayList<Runnable> que = new ArrayList<>();

    @EventHandler
    public void chunkLoad(PlayerChunkLoadEvent e) {
        Player p = e.getPlayer();
        if (!getPlayersWithPower().contains(p)) return;
        final ChunkManagerWorld worldChunkAccessor = new ChunkManagerWorld(e.getWorld());
        ServerLevel level = ((CraftWorld) e.getWorld()).getHandle();
        for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType())) {
            que.add(() -> {
                Map<Position, BlockData> updates = new ConcurrentHashMap<>();
                BlockData toSend = power.getMaterial("block").createBlockData();
                FactoryJsonObject bc = power.getJsonObject("block_condition");
                for (Block block : worldChunkAccessor.getAllBlocksInChunk(e.getChunk())) {
                    if (block == null) continue;
                    Location location = block.getLocation();
                    if (!ConditionExecutor.testBlock(bc, CraftBlock.at(level, CraftLocation.toBlockPosition(location))))
                        continue;
                    updates.put(location, toSend);
                }
                // We send in multi-block-changes to save on network spam
                p.sendMultiBlockChange(updates);
            });
        }
    }

    @Override
    public void run(Player p, Power power) {
        Map<Position, BlockData> updates = new ConcurrentHashMap<>();
        BlockData toSend = power.getMaterial("block").createBlockData();
        FactoryJsonObject bc = power.getJsonObject("block_condition");
        ServerLevel level = ((CraftWorld) p.getWorld()).getHandle();
        Shape.executeAtPositions(CraftLocation.toBlockPosition(p.getLocation()), Shape.SPHERE, 10, (pos) -> {
            Block block = p.getWorld().getBlockAt(CraftLocation.toBukkit(pos));
            if (block == null || block.getType().isAir()) return;
            Location location = block.getLocation();
            if (!ConditionExecutor.testBlock(bc, CraftBlock.at(level, CraftLocation.toBlockPosition(location)))) return;
            updates.put(location, toSend);
        });
        // We send in multi-block-changes to save on network spam
        p.sendMultiBlockChange(updates);
    }

    @EventHandler
    public void tickEnd(ServerTickEndEvent e) {
        if (!que.isEmpty()) {
            que.get(0).run();
            que.remove(0);
        }
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