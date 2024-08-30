package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.power.factory.PowerType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftVector;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class GroundedPower extends PowerType {
	public GroundedPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("grounded"));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void doubleJump(@NotNull PlayerToggleFlightEvent e) {
		Player p = e.getPlayer();
		if (!getPlayers().contains(((CraftPlayer) p).getHandle()) || p.getGameMode().equals(GameMode.SPECTATOR)) return;
		if (isActive(((CraftPlayer) p).getHandle())) {
			e.setCancelled(true);
			p.setFlying(false);
			ServerPlayer player = ((CraftPlayer) p).getHandle();
			Vec3 vec3d = player.getDeltaMovement();

			new BukkitRunnable() {
				@Override
				public void run() {
					player.getBukkitEntity().setVelocity(CraftVector.toBukkit(new Vec3(vec3d.x, (0.42F * entity$getBlockJumpFactor(player) + player.getJumpBoostPower()), vec3d.z)));
					if (player.isSprinting()) {
						float fe = player.getYRot() * 0.017453292F;
						player.getBukkitEntity().setVelocity(CraftVector.toBukkit(player.getDeltaMovement().add((-Mth.sin(fe) * 0.2F), 0.0D, (Mth.cos(fe) * 0.2F))));
					}
				}
			}.runTaskLater(OriginsPaper.getPlugin(), 1);
		}
	}

	public float entity$getBlockJumpFactor(@NotNull Entity entity) {
		float l = entity.level().getBlockState(entity.blockPosition()).getBlock().getJumpFactor();
		float l9 = entity.level().getBlockState(this.entity$getOnPos(entity, 0.500001F)).getBlock().getJumpFactor();

		return (double) l == 1.0D ? l9 : l;
	}

	public BlockPos entity$getOnPos(@NotNull Entity entity, float offset) {
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void negateFallDamage(@NotNull EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player p)) return;
		if (!getPlayers().contains(p) || !(e.getCause().equals(EntityDamageEvent.DamageCause.FALL) && !e.isCancelled()))
			return;
		if (isActive(((CraftPlayer) p).getHandle())) {
			e.setCancelled(true);
		}
	}
}
