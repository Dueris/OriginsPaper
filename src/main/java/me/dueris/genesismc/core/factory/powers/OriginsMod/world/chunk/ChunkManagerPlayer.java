package me.dueris.genesismc.core.factory.powers.OriginsMod.world.chunk;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class ChunkManagerPlayer {
    Player player;
    String shape;
    ShapeData shapeData;
    boolean thing;

    public ChunkManagerPlayer(Player player, String shape) {
        this.player = player;
        this.shape = shape;
        //generateShapeData
        ShapeData shapedat = new ShapeData(shape, true);
        this.shapeData = shapedat;
    }

    public Player getPlayer() {
        return player;
    }

    public ShapeData getShapeData() {
        return shapeData;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setShapeData(ShapeData shapeData) {
        this.shapeData = shapeData;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
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
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public boolean isThing() {
        return thing;
    }

    public void setThing(boolean thing) {
        this.thing = thing;
    }

    public Block[] getPlayerChunkBlocks(ShapeData shapeData, Player player) {
        int radius = 8;
        if (shapeData.getShape().equalsIgnoreCase("sphere")) {
            Location centerLocation = player.getLocation();
            int centerX = centerLocation.getBlockX();
            int centerY = centerLocation.getBlockY();
            int centerZ = centerLocation.getBlockZ();
            int diameter = radius * 2 + 1;
            Block[] blocksInSphere = new Block[diameter * diameter * diameter];
            int index = 0;

            for (int x = centerX - radius; x <= centerX + radius; x++) {
                for (int y = centerY - radius; y <= centerY + radius; y++) {
                    for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                        Location blockLocation = new Location(centerLocation.getWorld(), x, y, z);
                        if (centerLocation.distance(blockLocation) <= radius) {
                            Block block = blockLocation.getBlock();
                            blocksInSphere[index++] = block;
                        }
                    }
                }
            }

            return blocksInSphere;
        } else if (shapeData.getShape().equalsIgnoreCase("box")) {
            Location centerLocation = player.getLocation();
            int centerX = centerLocation.getBlockX();
            int centerY = centerLocation.getBlockY();
            int centerZ = centerLocation.getBlockZ();
            int diameter = radius * 2 + 1;
            Block[] blocksInCube = new Block[diameter * diameter * diameter];
            int index = 0;

            for (int x = centerX - radius; x <= centerX + radius; x++) {
                for (int y = centerY - radius; y <= centerY + radius; y++) {
                    for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                        Block block = centerLocation.getWorld().getBlockAt(x, y, z);
                        blocksInCube[index++] = block;
                    }
                }
            }

            return blocksInCube;
        } else if (shapeData.getShape().equalsIgnoreCase("world_chunk")) {
            Location centerLocation = player.getLocation();
            int centerX = centerLocation.getBlockX();
            int centerY = centerLocation.getBlockY();
            int centerZ = centerLocation.getBlockZ();
            int diameter = radius * 2 + 1;
            int totalHeight = player.getWorld().getMaxHeight() - player.getWorld().getMinHeight() + 1;
            Block[] blocksInBox = new Block[diameter * diameter * totalHeight];
            int index = 0;

            for (int x = centerX - radius; x <= centerX + radius; x++) {
                for (int y = player.getWorld().getMaxHeight(); y >= player.getWorld().getMinHeight(); y--) {
                    for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                        Block block = centerLocation.getWorld().getBlockAt(x, y, z);
                        blocksInBox[index++] = block;
                    }
                }
            }

            return blocksInBox;
        }

        return null;
    }
}
