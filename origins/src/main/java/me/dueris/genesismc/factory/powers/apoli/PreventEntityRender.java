package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass.prevent_entity_render;

public class PreventEntityRender extends CraftPower {

    private final int ticksE;
    private Long interval;

    public PreventEntityRender() {
        this.interval = 12L;
        this.ticksE = 0;
    }


    @Override
    public void run(Player p) {
        if (GenesisMC.disableRender) return;
        if (getPowerArray().contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    interval = 20L;
                    if (Bukkit.getServer().getCurrentTick() % interval != 0) {
                        return;
                    } else {
                        for (Entity entity : getEntitiesWithinRender(p)) {
                            ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                            if (ConditionExecutor.testEntity(power.get("entity_condition"), (CraftEntity) p)) {
                                if (ConditionExecutor.testBiEntity(power.get("bientity_condition"), (CraftEntity) p, (CraftEntity) entity)) {
                                    if (p.canSee(entity)) {
                                        p.hideEntity(GenesisMC.getPlugin(), entity);
                                    }
                                    setActive(p, power.getTag(), true);
                                } else {
                                    setActive(p, power.getTag(), false);
                                    if (!p.canSee(entity)) {
                                        p.showEntity(GenesisMC.getPlugin(), entity);
                                    }
                                }
                            } else {
                                setActive(p, power.getTag(), false);
                                if (!p.canSee(entity)) {
                                    p.showEntity(GenesisMC.getPlugin(), entity);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public List<Entity> getEntitiesWithinRender(Player player) {
        int maxChunkDistance = 5;
        int playerRenderDistance = player.getViewDistance();

        if (maxChunkDistance > playerRenderDistance) {
            maxChunkDistance = playerRenderDistance;
        }

        World world = player.getWorld();
        Location playerLocation = player.getLocation();
        int playerChunkX = playerLocation.getBlockX() >> 4;
        int playerChunkZ = playerLocation.getBlockZ() >> 4;

        List<Entity> entities = new ArrayList<>();

        for (int x = playerChunkX - maxChunkDistance; x <= playerChunkX + maxChunkDistance; x++) {
            for (int z = playerChunkZ - maxChunkDistance; z <= playerChunkZ + maxChunkDistance; z++) {
                Chunk chunk = world.getChunkAt(x, z);
                entities.addAll(Arrays.asList(chunk.getEntities()));
            }
        }

        return entities;
    }

    @Override
    public String getPowerFile() {
        return "apoli:prevent_entity_render";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_entity_render;
    }
}
