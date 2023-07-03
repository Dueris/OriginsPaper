package me.dueris.genesismc.core.factory.powers.genesis;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.utils.OriginContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.LightBlock;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.bukkit.util.VoxelShape;
import org.inventivetalent.glow.GlowAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import static me.dueris.genesismc.core.factory.powers.Powers.bioluminescent;

public class Bioluminescent extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!bioluminescent.contains(p)) return;
//            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
//                Block block = p.getLocation().getBlock();
//                block.getState().update(true, false);
//                for(Player player : Bukkit.getOnlinePlayers()){
//                    GlowAPI.setGlowing(p, GlowAPI.Color.RED, p);
//                }
//            }
            //DO NOT UNCOMMENT THIS CODE BROKE RENDERING SO BADLY LOL
        }
    }
}
