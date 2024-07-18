package me.dueris.originspaper.factory.data.types;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftParticle;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExplosionMask {
	List<Block> blocks;
	Explosion explosion;
	ServerLevel level;
	boolean fire;

	public ExplosionMask(Explosion explosion, ServerLevel level) {
		this.blocks = new ArrayList<>();
		this.explosion = explosion;
		this.level = level;
	}

	public static ExplosionMask getExplosionMask(Explosion explosion, ServerLevel level) {
		return new ExplosionMask(explosion, level);
	}

	private static void addOrAppendStack(List<Pair<ItemStack, BlockPos>> stacks, ItemStack stack, BlockPos pos) {
		if (stack.isEmpty()) return;
		for (int i = 0; i < stacks.size(); ++i) {
			Pair<ItemStack, BlockPos> pair = stacks.get(i);
			ItemStack itemstack1 = pair.getFirst();

			if (ItemEntity.areMergable(itemstack1, stack)) {
				stacks.set(i, Pair.of(ItemEntity.merge(itemstack1, stack, 16), pair.getSecond()));
				if (stack.isEmpty()) {
					return;
				}
			}
		}

		stacks.add(Pair.of(stack, pos));
	}

	public ExplosionMask apply(FactoryJsonObject getter, boolean destroyAfterMask) {
		this.explosion.explode(); // Setup explosion stuff -- includes iterator for explosions
		this.fire = getter.getBooleanOrDefault("create_fire", false);
		this.blocks = createBlockList(this.explosion.getToBlow(), this.level);
		List<Block> finalBlocks = new ArrayList<>();

		boolean testFilters = getter.isPresent("indestructible") || getter.isPresent("destructible");

		if (testFilters) {
			this.blocks.forEach((block) -> {
				boolean addBlock = true;

				if (getter.isPresent("indestructible")) {
					FactoryJsonObject condition = getter.getJsonObject("indestructible");
					if (ConditionExecutor.testBlock(condition, (CraftBlock) block)) {
						addBlock = false;
					}
				}

				if (getter.isPresent("destructible")) {
					FactoryJsonObject condition = getter.getJsonObject("destructible");
					if (!ConditionExecutor.testBlock(condition, (CraftBlock) block)) {
						addBlock = false;
					}
				}

				if (addBlock) {
					finalBlocks.add(block);
				}
			});
		} else {
			finalBlocks.addAll(this.blocks);
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

		boolean flag1 = this.explosion.interactsWithBlocks();

		if (flag1) {
			List<Pair<ItemStack, BlockPos>> list = new ArrayList();

			net.minecraft.Util.shuffle(this.explosion.getToBlow(), this.level.random);
			Iterator<BlockPos> objectlistiterator = this.explosion.getToBlow().iterator();
			org.bukkit.World bworld = this.level.getWorld();
			org.bukkit.entity.Entity explode = this.explosion.source == null ? null : this.explosion.source.getBukkitEntity();
			Location location = new Location(bworld, this.explosion.center().x, this.explosion.center().y, this.explosion.center().z);

			List<org.bukkit.block.Block> blockList = new ObjectArrayList<>();
			for (int i1 = this.explosion.getToBlow().size() - 1; i1 >= 0; i1--) {
				BlockPos cpos = this.explosion.getToBlow().get(i1);
				org.bukkit.block.Block bblock = bworld.getBlockAt(cpos.getX(), cpos.getY(), cpos.getZ());
				if (!bblock.getType().isAir()) {
					blockList.add(bblock);
				}
			}

			List<org.bukkit.block.Block> bukkitBlocks;

			if (explode != null) {
				EntityExplodeEvent event = CraftEventFactory.callEntityExplodeEvent(this.explosion.source, blockList, this.explosion.yield, this.explosion.getBlockInteraction());
				this.explosion.wasCanceled = event.isCancelled();
				bukkitBlocks = event.blockList();
				this.explosion.yield = event.getYield();
			} else {
				org.bukkit.block.Block block = location.getBlock();
				org.bukkit.block.BlockState blockState = block.getState();
				BlockExplodeEvent event = CraftEventFactory.callBlockExplodeEvent(block, blockState, blockList, this.explosion.yield, this.explosion.getBlockInteraction());
				this.explosion.wasCanceled = event.isCancelled();
				bukkitBlocks = event.blockList();
				this.explosion.yield = event.getYield();
			}

			this.explosion.getToBlow().clear();

			for (org.bukkit.block.Block bblock : bukkitBlocks) {
				BlockPos coords = new BlockPos(bblock.getX(), bblock.getY(), bblock.getZ());
				this.explosion.getToBlow().add(coords);
			}

			if (this.explosion.wasCanceled) {
				return;
			}
			objectlistiterator = this.explosion.getToBlow().iterator();

			while (objectlistiterator.hasNext()) {
				BlockPos blockposition = objectlistiterator.next();
				BlockState iblockdata = this.level.getBlockState(blockposition);
				net.minecraft.world.level.block.Block block = iblockdata.getBlock();
				if (block instanceof net.minecraft.world.level.block.TntBlock) {
					Entity sourceEntity = this.explosion.source == null ? null : this.explosion.source;
					BlockPos sourceBlock = sourceEntity == null ? BlockPos.containing(this.explosion.center().x, this.explosion.center().y, this.explosion.center().z) : null;
					if (!CraftEventFactory.callTNTPrimeEvent(this.level, blockposition, org.bukkit.event.block.TNTPrimeEvent.PrimeCause.EXPLOSION, sourceEntity, sourceBlock)) {
						this.level.sendBlockUpdated(blockposition, Blocks.AIR.defaultBlockState(), iblockdata, 3); // Update the block on the client
						continue;
					}
				}

				this.level.getBlockState(blockposition).onExplosionHit(this.level, blockposition, this.explosion, (itemstack, blockposition1) -> {
					addOrAppendStack(list, itemstack, blockposition1);
				});
			}

			Iterator<Pair<ItemStack, BlockPos>> iterator = list.iterator();

			while (iterator.hasNext()) {
				Pair<ItemStack, BlockPos> pair = iterator.next();

				net.minecraft.world.level.block.Block.popResource(this.level, pair.getSecond(), pair.getFirst());
			}
		}

		if (this.fire) {

			for (BlockPos blockposition1 : this.explosion.getToBlow()) {
				if (this.level.random.nextInt(3) == 0 && this.level.getBlockState(blockposition1).isAir() && this.level.getBlockState(blockposition1.below()).isSolidRender(this.level, blockposition1.below())) {
					if (!CraftEventFactory.callBlockIgniteEvent(this.level, blockposition1, this.explosion).isCancelled()) {
						this.level.setBlockAndUpdate(blockposition1, BaseFireBlock.getState(this.level, blockposition1));
					}
				}
			}
		}
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
