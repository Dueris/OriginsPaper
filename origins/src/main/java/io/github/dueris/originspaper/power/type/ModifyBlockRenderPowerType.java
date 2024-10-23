package io.github.dueris.originspaper.power.type;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.Shape;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
import io.papermc.paper.math.Position;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
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
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class ModifyBlockRenderPowerType extends PowerType implements Listener {
	public static LinkedList<Runnable> que = new LinkedList<>();

	private final Predicate<BlockInWorld> predicate;
	private final BlockState blockState;

	public ModifyBlockRenderPowerType(Power power, LivingEntity entity, Predicate<BlockInWorld> predicate, BlockState state) {
		super(power, entity);
		this.predicate = predicate;
		this.blockState = state;
	}

	public static @NotNull PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("modify_block_render"),
			new SerializableData()
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("block", SerializableDataTypes.BLOCK_STATE),
			data -> (power, entity) -> new ModifyBlockRenderPowerType(power, entity,
				data.get("block_condition"),
				data.get("block")
			)
		);
	}

	public boolean doesApply(LevelReader world, BlockPos pos) {
		BlockInWorld cbp = new BlockInWorld(world, pos, true);
		return predicate == null || predicate.test(cbp);
	}

	public BlockState getBlockState() {
		return blockState;
	}

	@EventHandler
	public void chunkLoad(@NotNull PlayerChunkLoadEvent e) {
		Player p = e.getPlayer();
		if (this.entity != ((CraftPlayer) p).getHandle()) return;
		ServerLevel level = ((CraftWorld) e.getWorld()).getHandle();
		que.add(() -> {
			Map<Position, BlockData> updates = new ConcurrentHashMap<>();
			BlockData toSend = getBlockState().createCraftBlockData();
			for (Block block : getAllBlocksInChunk(e.getChunk())) {
				if (block == null) continue;
				Location location = block.getLocation();
				if (!doesApply(level, CraftLocation.toBlockPosition(location)))
					continue;
				updates.put(location, toSend);
			}
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
	public void tick() {
		if (entity instanceof ServerPlayer) {
			Player p = (CraftPlayer) entity.getBukkitLivingEntity();
			Map<Position, BlockData> updates = new ConcurrentHashMap<>();
			BlockData toSend = getBlockState().createCraftBlockData();
			ServerLevel level = ((CraftWorld) p.getWorld()).getHandle();
			Shape.executeAtPositions(CraftLocation.toBlockPosition(p.getLocation()), Shape.SPHERE, 10, (pos) -> {
				Block block = p.getWorld().getBlockAt(CraftLocation.toBukkit(pos));
				if (block == null || block.getType().isAir()) return;
				Location location = block.getLocation();
				if (!doesApply(level, CraftLocation.toBlockPosition(location))) return;
				updates.put(location, toSend);
			});
			p.sendMultiBlockChange(updates);
		}
	}

	@EventHandler
	public void tickEnd(ServerTickEndEvent e) {
		if (!que.isEmpty()) {
			que.getFirst().run();
			que.removeFirst();
		}
	}
}

