package me.dueris.genesismc.factory.data.types;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftParticle;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.util.CraftLocation;

import java.util.ArrayList;
import java.util.List;

public class ExplosionMask {
	List<Block> blocks;
	Explosion explosion;
	ServerLevel level;

	public ExplosionMask(Explosion explosion, ServerLevel level) {
		this.blocks = new ArrayList<>();
		this.explosion = explosion;
		this.level = level;
	}

	public static ExplosionMask getExplosionMask(Explosion explosion, ServerLevel level) {
		return new ExplosionMask(explosion, level);
	}

	public ExplosionMask apply(FactoryJsonObject getter, boolean destroyAfterMask) {
		this.explosion.explode(); // Setup explosion stuff -- includes iterator for explosions
		this.blocks = createBlockList(this.explosion.getToBlow(), this.level);
		List<Block> finalBlocks = new ArrayList<>(this.blocks);
		boolean testFilters = getter.isPresent("indestructible") || getter.isPresent("destructible");

		if (testFilters) {
			this.blocks.forEach((block) -> {
				Utils.computeIfObjectPresent("indestructible", getter, (rawObjCondition) -> {
					FactoryJsonObject condition = rawObjCondition.toJsonObject();
					if (!ConditionExecutor.testBlock(condition, (CraftBlock) block)) {
						finalBlocks.add(block);
					}
				});
				Utils.computeIfObjectPresent("destructible", getter, (rawObjCondition) -> {
					FactoryJsonObject condition = rawObjCondition.toJsonObject();
					if (ConditionExecutor.testBlock(condition, (CraftBlock) block)) {
						finalBlocks.add(block);
					}
				});
			});
		}

		this.explosion.clearToBlow();
		this.explosion.getToBlow().addAll(createBlockPosList(finalBlocks));

		if (destroyAfterMask) {
			destroyBlocks();
		}
		return this;
	}

	public void destroyBlocks() {
		this.explosion.finalizeExplosion(false);
		ParticleOptions particleparam;

		if (this.explosion.radius() >= 2.0F && this.explosion.interactsWithBlocks()) {
			particleparam = this.explosion.getLargeExplosionParticles();
		} else {
			particleparam = this.explosion.getSmallExplosionParticles();
		}

		double x = this.explosion.center().x;
		double y = this.explosion.center().y;
		double z = this.explosion.center().z;

		this.level.getWorld().playSound(new Location(this.level.getWorld(), x, y, z), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
		this.level.getWorld().spawnParticle(CraftParticle.minecraftToBukkit(particleparam.getType()), new Location(this.level.getWorld(), x, y, z), 4);
	}

	private List<Block> createBlockList(List<BlockPos> blockPos, ServerLevel level) {
		List<Block> blocks = new ArrayList<>();
		blockPos.forEach(pos -> {
			blocks.add(level.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()));
		});
		return blocks;
	}

	private List<BlockPos> createBlockPosList(List<Block> blocks) {
		List<BlockPos> positions = new ArrayList<>();
		blocks.forEach(block -> {
			positions.add(CraftLocation.toBlockPosition(block.getLocation()));
		});
		return positions;
	}

	public Explosion getExplosion() {
		return this.explosion;
	}

	public List<Block> getBlocksToDestroy() {
		return this.blocks;
	}
}
