package me.dueris.genesismc.factory.powers.value_modifying;

import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import io.papermc.paper.registry.RegistryKey;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.events.PowerUpdateEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.PowerContainer;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftVector;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_world_spawn;
import static org.bukkit.Material.AIR;
import static org.bukkit.Material.OBSIDIAN;

public class ModifyPlayerSpawnPower extends CraftPower implements Listener {

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void powerRemove(PowerUpdateEvent e){
        if(e.isRemoved() && e.getPower().getType().equals(getPowerFile())){
            // Apoli remap start
            ServerPlayer serverPlayerEntity = ((CraftPlayer)e.getPlayer()).getHandle();
            if (serverPlayerEntity.hasDisconnected() || serverPlayerEntity.getRespawnPosition() == null || !serverPlayerEntity.isRespawnForced()) return;

            serverPlayerEntity.setRespawnPosition(Level.OVERWORLD, null, 0F, false, false);
            // Apoli remap end
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void runD(PlayerRespawnEvent e) {
        if (e.getRespawnReason().equals(PlayerRespawnEvent.RespawnReason.END_PORTAL)) return;
        if (e.getRespawnReason().equals(PlayerRespawnEvent.RespawnReason.PLUGIN)) return;
        if (!getPowerArray().contains(e.getPlayer())) return;
        if (e.getPlayer().getBedSpawnLocation() != null) {
            e.getPlayer().teleport(e.getPlayer().getBedSpawnLocation());
        } else {
            runHandle(e.getPlayer());
        }
    }

    public void runHandle(Player p) {
        if (modify_world_spawn.contains(p)) {
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                PowerContainer power = OriginPlayerUtils.getSinglePowerFileFromType(p, getPowerFile(), layer);
                if (power == null) continue;
                if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getInventory().getItemInMainHand(), null)) {
                    ApoliSpawnUtils utils = new ApoliSpawnUtils();
                    Pair<ServerLevel, BlockPos> spawn = utils.getSpawn(false, ((CraftPlayer)p).getHandle(), getDimension(power), ApoliSpawnUtils.SpawnStrategy.CENTER, 12);

                    if(spawn == null) return;
                    ServerLevel spawnDimension = spawn.getFirst();
                    BlockPos spawnPos = spawn.getSecond();
                    Vec3 tpPos = DismountHelper.findSafeDismountLocation(((CraftPlayer)p).getHandle().getType(), spawnDimension, spawnPos, true);
                    if(tpPos == null){
                        LogManager.getLogger("ModifyPlayerSpawn").warn("Power {} could not find a suitable spawnpoint for {}! Teleporting to the desired location directly...", new ModifyPlayerSpawnPower().getPowerFile(), p.getName());
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ((CraftPlayer)p).getHandle().teleportTo(spawnDimension, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), p.getPitch(), p.getYaw());
                        }
                    }.runTaskLater(GenesisMC.getPlugin(), 1);
                }
            }
        }
    }

    private ResourceKey<Level> getDimension(PowerContainer power){
        NamespacedKey key = NamespacedKey.fromString(power.getString("dimension"));
        return ((CraftWorld)Bukkit.getWorld(key)).getHandle().dimension();
    }

    @Override
    public String getPowerFile() {
        return "apoli:modify_player_spawn";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_world_spawn;
    }

    /**
     * This class uses remapped utils from Apoli to ensure players wont spawn in random places amongst
     * the world, because that happens a bit too often
     */
    public static class ApoliSpawnUtils {
        public enum SpawnStrategy {

            CENTER((blockPos, center, multiplier) -> new BlockPos(0, center, 0)),
            DEFAULT(
                    (blockPos, center, multiplier) -> {
                        BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();

                        if (multiplier != 0) mut.set(blockPos.getX() * multiplier, blockPos.getY(), blockPos.getZ() * multiplier);
                        else mut.set(blockPos);

                        return mut;

                    }
            );

            final TriFunction<BlockPos, Integer, Float, BlockPos> strategyApplier;
            SpawnStrategy(TriFunction<BlockPos, Integer, Float, BlockPos> strategyApplier) {
                this.strategyApplier = strategyApplier;
            }

            public BlockPos apply(BlockPos blockPos, int center, float multiplier) {
                return strategyApplier.apply(blockPos, center, multiplier);
            }

        }

        private Optional<Vec3> getValidSpawn(ServerLevel targetDimension, BlockPos startPos, int range, Entity entity) {

            //  The 'direction' vector that determines the direction of the iteration
            int dx = 1;
            int dz = 0;

            //  The length of the current segment
            int segmentLength = 1;

            //  The center of the structure/dimension
            int center = startPos.getY();

            //  The valid spawn position and (mutable) starting position
            Vec3 spawnPos;
            BlockPos.MutableBlockPos mutableStartPos = startPos.mutable();

            //  The current position
            int x = startPos.getX();
            int z = startPos.getZ();

            //  Determines how much of the current segment has been passed
            int segmentPassed = 0;

            //  Vertical offsets
            int upOffset = 0;
            int downOffset = 0;

            //  The min and max Y values of the target dimension
            int maxY = targetDimension.getLogicalHeight();
            int minY = targetDimension.dimensionTypeRegistration().value().minY();

            while (upOffset < maxY || downOffset > minY) {

                for (int steps = 0; steps < range; ++steps) {

                    //  Make a step by adding the 'direction' vector to the current position
                    x += dx;
                    z += dz;
                    mutableStartPos.setX(x);
                    mutableStartPos.setZ(z);

                    //  Increment how much of the current segment has been passed
                    ++segmentPassed;

                    //  Offset the Y axis (up and down) of the current position to check for valid spawn positions
                    mutableStartPos.setY(center + upOffset);
                    spawnPos = DismountHelper.findSafeDismountLocation(entity.getType(), targetDimension, mutableStartPos, true);
                    if (spawnPos != null) return Optional.of(spawnPos);

                    mutableStartPos.setY(center + downOffset);
                    spawnPos = DismountHelper.findSafeDismountLocation(entity.getType(), targetDimension, mutableStartPos, true);
                    if (spawnPos != null) return Optional.of(spawnPos);

                    //  If the current segment has not been passed, continue the loop
                    if (segmentPassed != segmentLength) continue;

                    //  Otherwise, reset the value of how much of the current segment has been passed
                    segmentPassed = 0;

                    //  'Rotate' the 'direction' vector
                    int bdx = dx;
                    dx = -dz;
                    dz = bdx;

                    //  Increment the length of the current segment if necessary
                    if (dz == 0) ++segmentLength;

                }

                //  Increment/decrement the up/down offsets until it's no longer less/greater than the max/min Y
                if (upOffset < maxY) upOffset++;
                if (downOffset > minY) downOffset--;

            }

            return Optional.empty();

        }

        public Pair<ServerLevel, BlockPos> getSpawn(boolean isSpawnObstructed, Entity entity, ResourceKey<Level> dimension, SpawnStrategy spawnStrategy, float dimensionDistanceMultiplier) {
            ServerPlayer serverPlayerEntity = (ServerPlayer) entity;
            MinecraftServer server = serverPlayerEntity.getServer();
            if (server == null) return null;

            ServerLevel overworldDimension = server.getLevel(Level.OVERWORLD);
            if (overworldDimension == null) return null;

            ServerLevel targetDimension = server.getLevel(dimension);
            if (targetDimension == null) {
                LogManager.getLogger("ModifyPlayerSpawn").warn("Power {} could not set {}'s spawnpoint at dimension as it's not registered! Falling back to default spawnpoint...", new ModifyPlayerSpawnPower().getPowerFile(), entity.getBukkitEntity().getName());
                return null;
            }

            int center = targetDimension.getLogicalHeight() / 2;
            int range = 64;

            AtomicReference<Vec3> modifiedSpawnPos = new AtomicReference<>();

            BlockPos regularSpawnBlockPos = overworldDimension.getSharedSpawnPos();
            BlockPos.MutableBlockPos modifiedSpawnBlockPos = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos dimensionSpawnPos = spawnStrategy.apply(regularSpawnBlockPos, center, dimensionDistanceMultiplier).mutable();
            getValidSpawn(targetDimension, dimensionSpawnPos, range, entity).ifPresent(modifiedSpawnPos::set);

            if (modifiedSpawnPos.get() == null) return null;

            Vec3 msp = modifiedSpawnPos.get();
            modifiedSpawnBlockPos.set(msp.x, msp.y, msp.z);
            targetDimension.getChunkSource().addRegionTicket(TicketType.START, new ChunkPos(modifiedSpawnBlockPos), 11, Unit.INSTANCE);

            return new Pair<>(targetDimension, modifiedSpawnBlockPos);

        }
    }
}
