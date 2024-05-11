package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftVector;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class Grounded extends CraftPower implements Listener {

	@Override
	public String getType() {
		return "apoli:grounded";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return grounded;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void doubleJump(PlayerToggleFlightEvent e) {
		Player p = e.getPlayer();
		if (!getPlayersWithPower().contains(p) || p.getGameMode().equals(GameMode.SPECTATOR)) return;
		for (Power power : OriginPlayerAccessor.getPowers(p, getType())) {
			if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
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
				}.runTaskLater(GenesisMC.getPlugin(), 1);
			}
		}
	}

	public float entity$getBlockJumpFactor(Entity entity) {
		float l = entity.level().getBlockState(entity.blockPosition()).getBlock().getJumpFactor();
		float l9 = entity.level().getBlockState(this.entity$getOnPos(entity, 0.500001F)).getBlock().getJumpFactor();

		return (double) l == 1.0D ? l9 : l;
	}

	public BlockPos entity$getOnPos(Entity entity, float offset) {
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
	public void negateFallDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player p)) return;
		if (!getPlayersWithPower().contains(p) || !(e.getCause().equals(EntityDamageEvent.DamageCause.FALL) && !e.isCancelled()))
			return;
		for (Power power : OriginPlayerAccessor.getPowers(p, getType())) {
			if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
				e.setCancelled(true);
			}
		}
	}

}