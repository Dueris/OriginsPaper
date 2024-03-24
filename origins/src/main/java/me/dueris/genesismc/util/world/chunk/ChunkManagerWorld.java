package me.dueris.genesismc.util.world.chunk;

import io.papermc.paper.math.Position;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ChunkManagerWorld {
    World world;

    public ChunkManagerWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public Chunk getChunkAt(Location location) {
        return location.getChunk();
    }

    public Chunk getChunkAt(int x, int z, World world) {
        return world.getChunkAt(x, z);
    }

    public ChunkManagerPlayer getPlayerChunkManager(Player player, String shape, boolean usetoppybottom) {
        return new ChunkManagerPlayer(player, shape);
    }

    public Block getBlockAtChunkPos(Chunk chunk, int x, int y, int z) {
        return chunk.getBlock(x, y, z);
    }

    public Block getBlockAtChunkPos(Chunk chunk, Position position) {
        return chunk.getBlock(position.blockX(), position.blockY(), position.blockZ());
    }

    public Material getBlockTypeAtChunkPos(Chunk chunk, int x, int y, int z) {
        return chunk.getBlock(x, y, z).getType();
    }

    public Location getCenter(Chunk chunk) {
        return chunk.getBlock(0, 0, 0).getLocation();
    }

    public void replaceBlocks(Chunk chunk, Material oldBlock, Material newBlock, boolean updateFullServer, Player player) {
        for (Block block : getAllBlocksInChunk(chunk)) {
            if (block.getType() == oldBlock) {
                HashMap<Position, BlockData> blocksToChange = new HashMap<>();
                blocksToChange.put(block.getLocation(), newBlock.createBlockData());
                if (updateFullServer) {
                    for (Position pos : blocksToChange.keySet()) {
                        getBlockAtChunkPos(chunk, pos).setType(newBlock);
                    }
                } else {
                    if (player != null) {
                        player.sendMultiBlockChange(blocksToChange);
                    }
                }
            }
        }
    }

    public Chunk[] getChunksInPlayerViewDistance(Player player) {
        int viewDistance = player.getClientViewDistance();
        int playerChunkX = player.getLocation().getChunk().getX();
        int playerChunkZ = player.getLocation().getChunk().getZ();

        int startChunkX = playerChunkX - viewDistance;
        int endChunkX = playerChunkX + viewDistance;
        int startChunkZ = playerChunkZ - viewDistance;
        int endChunkZ = playerChunkZ + viewDistance;

        Chunk[] chunksInPlayerView = new Chunk[(endChunkX - startChunkX + 1) * (endChunkZ - startChunkZ + 1)];
        int index = 0;

        for (int x = startChunkX; x <= endChunkX; x++) {
            for (int z = startChunkZ; z <= endChunkZ; z++) {
                chunksInPlayerView[index] = player.getWorld().getChunkAt(x, z);
                index++;
            }
        }

        return chunksInPlayerView;
    }

    public Block[] getAllBlocksInChunk(Chunk chunk) {
        World world = chunk.getWorld();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        // Initialize arrays to store the blocks
        Block[] blocks = new Block[16 * 256 * 16];
        int index = 0;

        // Iterate through each block in the chunk
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
                for (int z = 0; z < 16; z++) {
                    Location blockLocation = new Location(world, chunkX * 16 + x, y, chunkZ * 16 + z);
                    Block block = blockLocation.getBlock();
                    blocks[index++] = block;
                }
            }
        }

        return blocks;
    }

    public Entity[] getAllEntitiesInChunk(Chunk chunk) {
        return chunk.getEntities();
    }

    public void killAllEntitiesInChunk(Chunk chunk) {
        for (Entity entity : getAllEntitiesInChunk(chunk)) {
            if (entity instanceof LivingEntity le) {
                le.damage(Integer.MAX_VALUE);
            }
        }
    }

    public void removeAllEntitiesInChunk(Chunk chunk) {
        for (Entity entity : getAllEntitiesInChunk(chunk)) {
            if (entity instanceof LivingEntity le) {
                le.remove();
            }
        }
    }

    public Biome getBiome(Chunk chunk, int x, int y, int z) {
        return chunk.getBlock(x, y, z).getBiome();
    }

    public void regenerateChunk(Chunk chunk) {
        World world = chunk.getWorld();

        // Unload the chunk
        boolean wasLoaded = chunk.isLoaded();
        if (wasLoaded) {
            world.unloadChunk(chunk.getX(), chunk.getZ());
        }

        // Regenerate the chunk
        chunk.load();

        // Optionally, you can reset individual blocks in the chunk to their default state
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < world.getMaxHeight(); y++) {
                for (int z = 0; z < 16; z++) {
                    Block block = chunk.getBlock(x, y, z);
                    // Reset the block to its default state (air for most blocks)
                    block.setType(org.bukkit.Material.AIR);
                }
            }
        }

        // Reload the chunk
        if (wasLoaded) {
            world.refreshChunk(chunk.getX(), chunk.getZ());
        }
    }

    public void setChunkBiome(Chunk chunk, Biome biome) {
        for (Block block : this.getAllBlocksInChunk(chunk)) {
            block.setBiome(biome);
        }
    }

    public void setChunkBlockData(Chunk chunk, BlockData data) {
        for (Block block : this.getAllBlocksInChunk(chunk)) {
            block.setBlockData(data);
        }
    }

    public void setChunkBlockData(Chunk chunk, BlockData data, boolean updatePhysics) {
        for (Block block : this.getAllBlocksInChunk(chunk)) {
            block.setBlockData(data, updatePhysics);
        }
    }

    public boolean isSlimeChunk(Chunk chunk) {
        return chunk.isSlimeChunk();
    }

}
