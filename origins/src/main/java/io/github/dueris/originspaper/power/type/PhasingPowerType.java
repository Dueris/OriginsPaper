package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.access.PhasingEntity;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
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

	private final Predicate<BlockInWorld> blockCondition;
	private final boolean blacklist;

	private final Predicate<Entity> phaseDownCondition;

	private final RenderType renderType;
	private final float viewDistance;

	public PhasingPowerType(Power power, LivingEntity entity, Predicate<BlockInWorld> blockCondition, boolean blacklist, RenderType renderType, float viewDistance, Predicate<Entity> phaseDownCondition) {
		super(power, entity);
		this.blockCondition = blockCondition;
		this.blacklist = blacklist;
		this.renderType = renderType;
		this.viewDistance = viewDistance;
		this.phaseDownCondition = phaseDownCondition;
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

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(OriginsPaper.apoliIdentifier("phasing"),
			new SerializableData()
				.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("blacklist", SerializableDataTypes.BOOLEAN, false)
				.add("render_type", SerializableDataType.enumValue(PhasingPowerType.RenderType.class), PhasingPowerType.RenderType.BLINDNESS)
				.add("view_distance", SerializableDataTypes.FLOAT, 10F)
				.add("phase_down_condition", ApoliDataTypes.ENTITY_CONDITION, null),
			data -> (power, entity) -> new PhasingPowerType(power, entity,
				data.get("block_condition"),
				data.getBoolean("blacklist"),
				data.get("render_type"),
				data.getFloat("view_distance"),
				data.get("phase_down_condition")
			)
		).allowCondition();
	}

	public boolean doesApply(BlockPos pos) {
		return blockCondition == null
			|| blacklist != blockCondition.test(new BlockInWorld(entity.level(), pos, true));
	}

	public boolean shouldPhase(@NotNull VoxelShape shape, @NotNull BlockPos pos) {
		return (entity.getY() < (double) pos.getY() + shape.max(Direction.Axis.Y) - (entity.onGround() ? 8.05 / 16.0 : 0.0015)
			|| this.shouldPhaseDown())
			&& this.doesApply(pos);
	}

	public boolean shouldPhaseDown() {
		return phaseDownCondition != null
			? phaseDownCondition.test(entity)
			: entity.isShiftKeyDown();
	}

	public RenderType getRenderType() {
		return renderType;
	}

	public float getViewDistance() {
		return viewDistance;
	}

	@Override
	public void onRemoved() {
		if (this.entity instanceof ServerPlayer player) {
			player.connection.send(prepareResync(player));
			((PhasingEntity) player).apoli$setPhasing(false);
			if (this.getRenderType().equals(PhasingPowerType.RenderType.BLINDNESS)) {
				player.getBukkitEntity().removePotionEffect(PotionEffectType.BLINDNESS);
			}
			player.getBukkitEntity().setFlySpeed(0.1F);
		}
	}

	public enum RenderType {
		BLINDNESS, REMOVE_BLOCKS, NONE
	}
}
