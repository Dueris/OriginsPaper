package me.dueris.originspaper.factory.powers.apoli;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.google.gson.JsonObject;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.math.Position;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.RequiredInstance;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.data.types.Shape;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.util.chunk.ChunkManagerWorld;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModifyBlockRenderPower extends PowerType implements Listener {
	public static ArrayList<Runnable> que = new ArrayList<>();
	private final Material block;
	private final FactoryJsonObject blockCondition;

	public ModifyBlockRenderPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, Material block, FactoryJsonObject blockCondition) {
		super(name, description, hidden, condition, loading_priority);
		this.block = block;
		this.blockCondition = blockCondition;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("modify_block_render"))
			.add("block", Material.class, new RequiredInstance())
			.add("block_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void chunkLoad(@NotNull PlayerChunkLoadEvent e) {
		Player p = e.getPlayer();
		if (!getPlayers().contains(p)) return;
		final ChunkManagerWorld worldChunkAccessor = new ChunkManagerWorld(e.getWorld());
		ServerLevel level = ((CraftWorld) e.getWorld()).getHandle();
		que.add(() -> {
			Map<Position, BlockData> updates = new ConcurrentHashMap<>();
			BlockData toSend = block.createBlockData();
			for (Block block : worldChunkAccessor.getAllBlocksInChunk(e.getChunk())) {
				if (block == null) continue;
				Location location = block.getLocation();
				if (!ConditionExecutor.testBlock(blockCondition, CraftBlock.at(level, CraftLocation.toBlockPosition(location))))
					continue;
				updates.put(location, toSend);
			}
			// We send in multi-block-changes to save on network spam
			p.sendMultiBlockChange(updates);
		});
	}

	@Override
	public void tick(@NotNull Player p) {
		Map<Position, BlockData> updates = new ConcurrentHashMap<>();
		BlockData toSend = block.createBlockData();
		ServerLevel level = ((CraftWorld) p.getWorld()).getHandle();
		Shape.executeAtPositions(CraftLocation.toBlockPosition(p.getLocation()), Shape.SPHERE, 10, (pos) -> {
			Block block = p.getWorld().getBlockAt(CraftLocation.toBukkit(pos));
			if (block == null || block.getType().isAir()) return;
			Location location = block.getLocation();
			if (!ConditionExecutor.testBlock(blockCondition, CraftBlock.at(level, CraftLocation.toBlockPosition(location))))
				return;
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

	public Material getBlock() {
		return block;
	}

	public FactoryJsonObject getBlockCondition() {
		return blockCondition;
	}
}