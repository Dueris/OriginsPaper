package io.github.dueris.originspaper.power.type;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Shape;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.math.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModifyBlockRenderPower extends PowerType {
	public static LinkedList<Runnable> que = new LinkedList<>();
	private final ConditionTypeFactory<BlockInWorld> blockCondition;
	private final BlockState state;

	public ModifyBlockRenderPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								  ConditionTypeFactory<BlockInWorld> blockCondition, BlockState state) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.blockCondition = blockCondition;
		this.state = state;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("modify_block_render"), PowerType.getFactory().getSerializableData()
			.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
			.add("block", SerializableDataTypes.BLOCK_STATE));
	}

	@EventHandler
	public void chunkLoad(@NotNull PlayerChunkLoadEvent e) {
		Player p = e.getPlayer();
		if (!getPlayers().contains(((CraftPlayer) p).getHandle())) return;
		ServerLevel level = ((CraftWorld) e.getWorld()).getHandle();
		que.add(() -> {
			Map<Position, BlockData> updates = new ConcurrentHashMap<>();
			BlockData toSend = state.createCraftBlockData();
			for (Block block : getAllBlocksInChunk(e.getChunk())) {
				if (block == null) continue;
				Location location = block.getLocation();
				if (!blockCondition.test(new BlockInWorld(level, CraftLocation.toBlockPosition(location), true)))
					continue;
				updates.put(location, toSend);
			}
			// We send in multi-block-changes to save on network spam
			p.sendMultiBlockChange(updates);
		});
	}

	public Block[] getAllBlocksInChunk(@NotNull Chunk chunk) {
		World world = chunk.getWorld();
		int chunkX = chunk.getX();
		int chunkZ = chunk.getZ();

		Block[] blocks = new Block[16 * 256 * 16];
		int index = 0;

		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 256; y++) {
				for (int z = 0; z < 16; z++) {
					Location blockLocation = new Location(world, chunkX * 16 + x, y, chunkZ * 16 + z);
					Block block = blockLocation.getBlock();
					if (block.getType().isAir()) continue;
					blocks[index++] = block;
				}
			}
		}

		return blocks;
	}

	@Override
	public void tick(@NotNull net.minecraft.world.entity.player.Player player) {
		Player p = (CraftPlayer) player.getBukkitEntity();
		Map<Position, BlockData> updates = new ConcurrentHashMap<>();
		BlockData toSend = state.createCraftBlockData();
		ServerLevel level = ((CraftWorld) p.getWorld()).getHandle();
		Shape.executeAtPositions(CraftLocation.toBlockPosition(p.getLocation()), Shape.SPHERE, 10, (pos) -> {
			Block block = p.getWorld().getBlockAt(CraftLocation.toBukkit(pos));
			if (block == null || block.getType().isAir()) return;
			Location location = block.getLocation();
			if (!blockCondition.test(new BlockInWorld(level, CraftLocation.toBlockPosition(location), true))) return;
			updates.put(location, toSend);
		});
		// We send in multi-block-changes to save on network spam
		p.sendMultiBlockChange(updates);
	}

	@EventHandler
	public void tickEnd(ServerTickEndEvent e) {
		if (!que.isEmpty()) {
			que.getFirst().run();
			que.removeFirst();
		}
	}
}
