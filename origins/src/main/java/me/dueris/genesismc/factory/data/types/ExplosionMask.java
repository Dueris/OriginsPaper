package me.dueris.genesismc.factory.data.types;

import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
import org.json.simple.JSONObject;

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

    public ExplosionMask apply(JSONObject getter, boolean destroyAfterMask) {
        this.explosion.explode(); // Setup explosion stuff -- includes iterator for explosions
        this.blocks = createBlockList(this.explosion.getToBlow(), this.level);
        List<Block> finalBlocks = new ArrayList<>(this.blocks);
        boolean testFilters = getter.containsKey("indestructible") || getter.containsKey("destructible");

        if (testFilters) {
            this.blocks.forEach((block) -> {
                Utils.computeIfObjectPresent("indestructible", getter, (rawObjCondition) -> {
                    JSONObject condition = (JSONObject) rawObjCondition;
                    if (!ConditionExecutor.testBlock(condition, (CraftBlock) block)) {
                        finalBlocks.add(block);
                    }
                });
                Utils.computeIfObjectPresent("destructible", getter, (rawObjCondition) -> {
                    JSONObject condition = (JSONObject) getter.get("destructible");
                    if (ConditionExecutor.testBlock(condition, (CraftBlock) block)) {
                        finalBlocks.add(block);
                    }
                });
            });
        }

        this.explosion.clearToBlow();
        this.explosion.getToBlow().addAll(createBlockPosList(finalBlocks));

        if (destroyAfterMask) {
            destroyBlocks(true);
        }
        return this;
    }

    public void destroyBlocks(boolean particles) {
        this.explosion.finalizeExplosion(particles);
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
