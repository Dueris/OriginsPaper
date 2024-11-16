package io.github.dueris.originspaper.power.type;

import com.mojang.serialization.DataResult;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.PowerConfiguration;
import io.github.dueris.originspaper.util.Util;
import io.papermc.paper.math.Position;
import io.papermc.paper.util.MCUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.PlayerChunkSender;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Chunk;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ModifyBlockRenderPowerType extends PowerType {
	private static boolean sentMessage = false;
	public static final TypedDataObjectFactory<ModifyBlockRenderPowerType> DATA_FACTORY = TypedDataObjectFactory.simple(
		new SerializableData()
			.add("block_condition", BlockCondition.DATA_TYPE.optional(), Optional.empty())
			.add("block", SerializableDataTypes.BLOCK_STATE)
			.validate((data) -> {
				if (!sentMessage) {
					sentMessage = true;
					OriginsPaper.LOGGER.warn("ModifyBlockRender power type was detected in validation, please keep in mind this power is very performance-heavy with chunk senders.");
				}
				return DataResult.success(data);
			}),
		data -> new ModifyBlockRenderPowerType(
			data.get("block_condition"),
			data.get("block")
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("block_condition", powerType.blockCondition)
			.set("block", powerType.blockState)
	);
	private final Optional<BlockCondition> blockCondition;
	private final BlockState blockState;
	private boolean refreshingChunks = false;
	private boolean sending;

	public ModifyBlockRenderPowerType(Optional<BlockCondition> blockCondition, BlockState state) {
		this.blockCondition = blockCondition;
		this.blockState = state;
		setTicking(true);
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.MODIFY_BLOCK_RENDER;
	}

	@Override
	public void onAdded() {
		refreshChunks(false);
	}

	@Override
	public void onRemoved() {
		refreshChunks(true);
	}

	private void refreshChunks(boolean removing) {
		refreshingChunks = removing;
		if (getHolder() instanceof ServerPlayer player && player.moonrise$getChunkLoader() != null) {
			PlayerChunkSender sender = player.connection.chunkSender;
			for (ChunkPos chunkPos : player.getBukkitEntity().getSentChunks().stream().map(Util::bukkitChunk2ChunkPos).collect(Collectors.toSet())) {
				sender.dropChunk(player, chunkPos);
				PlayerChunkSender.sendChunk(player.connection, player.serverLevel(), player.level().getChunk(chunkPos.x, chunkPos.z));
			}
		}
		refreshingChunks = false;
	}

	public boolean doesPrevent(Level world, BlockPos pos) {
		return blockCondition
			.map(condition -> condition.test(world, pos))
			.orElse(true);
	}

	public BlockState getBlockState() {
		return blockState;
	}

	public boolean isSending() {
		return this.sending;
	}

	public record RenderUpdate(Chunk chunk, ServerLevel level,
							   ServerPlayer player) implements Consumer<ModifyBlockRenderPowerType> {

		@Override
		public void accept(@NotNull ModifyBlockRenderPowerType mbrpt) {
			if (player.hasDisconnected() || mbrpt.refreshingChunks) return;

			Map<Position, BlockData> updates = new ConcurrentHashMap<>();
			BlockData toSend = mbrpt.getBlockState().createCraftBlockData();
			Util.runOnAllMatchingBlocks(chunk, mbrpt::doesPrevent, (pos) -> updates.put(MCUtil.toPosition(pos), toSend));

			mbrpt.sending = true;
			player.getBukkitEntity().sendMultiBlockChange(updates);
			mbrpt.sending = false;
		}
	}
}
