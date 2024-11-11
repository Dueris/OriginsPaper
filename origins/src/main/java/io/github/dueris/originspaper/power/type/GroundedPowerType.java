package io.github.dueris.originspaper.power.type;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.util.CraftVector;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class GroundedPowerType extends PowerType {

	public GroundedPowerType(Optional<EntityCondition> condition) {
		super(condition);
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.GROUNDED;
	}

	public static void action(Player entity) {
		if (!(PowerHolderComponent.KEY.isProvidedBy(entity) && !PowerHolderComponent.KEY.get(entity).getPowerTypes(GroundedPowerType.class).isEmpty())) {
			return;
		}
		Vec3 vec3d = entity.getDeltaMovement();

		new BukkitRunnable() {
			@Override
			public void run() {
				entity.getBukkitEntity().setVelocity(CraftVector.toBukkit(new Vec3(vec3d.x, (0.42F * entity$getBlockJumpFactor(entity) + entity.getJumpBoostPower()), vec3d.z)));
				if (entity.isSprinting()) {
					float fe = entity.getYRot() * 0.017453292F;
					entity.getBukkitEntity().setVelocity(CraftVector.toBukkit(entity.getDeltaMovement().add((-Mth.sin(fe) * 0.2F), 0.0D, (Mth.cos(fe) * 0.2F))));
				}
			}
		}.runTaskLater(OriginsPaper.getPlugin(), 1);
	}

	private static float entity$getBlockJumpFactor(@NotNull Entity entity) {
		float l = entity.level().getBlockState(entity.blockPosition()).getBlock().getJumpFactor();
		float l9 = entity.level().getBlockState(entity$getOnPos(entity, 0.500001F)).getBlock().getJumpFactor();

		return (double) l == 1.0D ? l9 : l;
	}

	private static BlockPos entity$getOnPos(@NotNull Entity entity, float offset) {
		if (entity.mainSupportingBlockPos.isPresent() && entity.level().getChunkIfLoadedImmediately(entity.mainSupportingBlockPos.get()) != null) {
			BlockPos bp = entity.mainSupportingBlockPos.get();

			if (offset <= 1.0E-5F) return bp;
			else {
				BlockState bs = entity.level().getBlockState(bp);

				return ((double) offset > 0.5D || !bs.is(BlockTags.FENCES)) && !bs.is(BlockTags.WALLS) && !(bs.getBlock() instanceof FenceGateBlock) ? bp.atY(Mth.floor(entity.position().y - (double) offset)) : bp;
			}
		} else
			return new BlockPos(Mth.floor(entity.position().x), Mth.floor(entity.position().y - (double) offset), Mth.floor(entity.position().z));
	}

	@Override
	public void onRemoved() {
		if (getHolder() instanceof ServerPlayer player) {
			player.getAbilities().mayfly = false;
			player.onUpdateAbilities();
		}
	}
}
