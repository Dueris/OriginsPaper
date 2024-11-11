package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.access.PhasingEntity;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.condition.BlockCondition;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.type.entity.SneakingEntityConditionType;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.Optionull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Predicate;

public class PhasingPowerType extends PowerType {
	public static Vector[] offsets = new Vector[]{
		new Vector(0.55, 0, 0.55),
		new Vector(0.55, 0, 0),
		new Vector(0, 0, 0.55),
		new Vector(-0.55, 0, -0.55),
		new Vector(0, 0, -0.55),
		new Vector(-0.55, 0, 0),
		new Vector(0.55, 0, -0.55),
		new Vector(-0.55, 0, 0.55),
		new Vector(0, 0.5, 0),

		new Vector(0.55, 0, 0.55),
		new Vector(0.55, 0, 0),
		new Vector(0, 0, 0.55),
		new Vector(-0.55, 0, -0.55),
		new Vector(0, 0, -0.55),
		new Vector(-0.55, 0, 0),
		new Vector(0.55, 0, -0.55),
		new Vector(-0.55, 0, 0.55)
	};

	public static final TypedDataObjectFactory<PhasingPowerType> DATA_FACTORY = createConditionedDataFactory(
		new SerializableData()
			.addSupplied("phase_down_condition", EntityCondition.DATA_TYPE, () -> new SneakingEntityConditionType().createCondition())
			.add("block_condition", BlockCondition.DATA_TYPE.optional(), Optional.empty())
			.add("render_type", SerializableDataType.enumValue(RenderType.class), RenderType.BLINDNESS)
			.add("view_distance", SerializableDataTypes.POSITIVE_FLOAT, 10.0F)
			.add("blacklist", SerializableDataTypes.BOOLEAN, false),
		(data, condition) -> new PhasingPowerType(
			data.get("phase_down_condition"),
			data.get("block_condition"),
			data.get("render_type"),
			data.get("view_distance"),
			data.get("blacklist"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("phase_down_condition", powerType.phaseDownCondition)
			.set("block_condition", powerType.blockCondition)
			.set("render_type", powerType.getRenderType())
			.set("view_distance", powerType.getViewDistance())
			.set("blacklist", powerType.blacklist)
	);

	private final EntityCondition phaseDownCondition;
	private final Optional<BlockCondition> blockCondition;

	private final RenderType renderType;
	private final float viewDistance;

	private final boolean blacklist;

	public PhasingPowerType(EntityCondition phaseDownCondition, Optional<BlockCondition> blockCondition, RenderType renderType, float viewDistance, boolean blacklist, Optional<EntityCondition> condition) {
		super(condition);
		this.phaseDownCondition = phaseDownCondition;
		this.blockCondition = blockCondition;
		this.blacklist = blacklist;
		this.renderType = renderType;
		this.viewDistance = viewDistance;
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.PHASING;
	}

	public boolean doesApply(BlockPos pos) {
		return blockCondition
			.map(condition -> blacklist != condition.test(getHolder().level(), pos))
			.orElse(true);
	}

	@Override
	public void onRemoved() {
		if (this.getHolder() instanceof ServerPlayer player) {
			player.connection.send(prepareResync(player));
			((PhasingEntity) player).apoli$setPhasing(false);
			if (this.getRenderType().equals(PhasingPowerType.RenderType.BLINDNESS)) {
				player.getBukkitEntity().removePotionEffect(PotionEffectType.BLINDNESS);
			}
			player.getBukkitEntity().setFlySpeed(0.1F);
		}
	}

	public boolean shouldPhase(VoxelShape shape, BlockPos pos) {
		LivingEntity holder = getHolder();
		return (holder.getY() < (double) pos.getY() + shape.max(Direction.Axis.Y) - (holder.onGround() ? 8.05 / 16.0 : 0.0015) || this.shouldPhaseDown())
			&& this.doesApply(pos);
	}

	public boolean shouldPhaseDown() {
		return phaseDownCondition.test(getHolder());
	}

	public RenderType getRenderType() {
		return renderType;
	}

	public float getViewDistance() {
		return viewDistance;
	}

	public enum RenderType {
		BLINDNESS, REMOVE_BLOCKS, NONE
	}

	public static boolean shouldPhase(CollisionContext context, VoxelShape shape, BlockPos pos) {
		return context instanceof EntityCollisionContext entityContext
			&& PowerHolderComponent.hasPowerType(entityContext.getEntity(), PhasingPowerType.class, p -> p.shouldPhase(shape, pos));
	}

	public static @NotNull ClientboundPlayerInfoUpdatePacket preparePacket(@NotNull ServerPlayer player) {
		GameType gamemode = GameType.SPECTATOR;
		ClientboundPlayerInfoUpdatePacket.Entry entry = new ClientboundPlayerInfoUpdatePacket.Entry(
			player.getUUID(),
			player.getGameProfile(),
			true,
			1,
			gamemode,
			player.getTabListDisplayName(),
			Optionull.map(player.getChatSession(), RemoteChatSession::asData)
		);
		return new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE), entry);
	}

	public static @NotNull ClientboundPlayerInfoUpdatePacket prepareResync(@NotNull ServerPlayer player) {
		GameType gamemode = player.gameMode.getGameModeForPlayer();
		ClientboundPlayerInfoUpdatePacket.Entry entry = new ClientboundPlayerInfoUpdatePacket.Entry(player.getUUID(), player.getGameProfile(),
			true, 1, gamemode, player.getTabListDisplayName(), Optionull.map(player.getChatSession(), RemoteChatSession::asData));
		return new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE), entry);
	}
}
