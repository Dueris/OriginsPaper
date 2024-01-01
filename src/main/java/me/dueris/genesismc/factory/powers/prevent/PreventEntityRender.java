package me.dueris.genesismc.factory.powers.prevent;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.files.GenesisDataFiles;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.translation.LangConfig;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static me.dueris.genesismc.factory.powers.prevent.PreventSuperClass.prevent_entity_render;

public class PreventEntityRender extends CraftPower {

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if(powers_active.containsKey(p)){
            if(powers_active.get(p).containsKey(tag)){
                powers_active.get(p).replace(tag, bool);
            }else{
                powers_active.get(p).put(tag, bool);
            }
        }else{
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    private Long interval;
    private final int ticksE;

    public PreventEntityRender() {
        this.interval = 12L;
        this.ticksE = 0;
    }

    public void run(Player p, HashMap<Player, Integer> ticksEMap) {
        if(GenesisMC.disableRender) return;
        ticksEMap.putIfAbsent(p, 0);

        if (getPowerArray().contains(p)) {
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (power == null) continue;
                    if (power.getInterval() == null) {
                        Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.action_over_time"));
                        return;
                    }

                    interval = 12l;
                    int ticksE = ticksEMap.getOrDefault(p, 0);
                    if (ticksE <= interval) {
                        ticksE++;
                        ticksEMap.put(p, ticksE);
                    } else {
                        for (Entity entity : getEntitiesWithinRender(p)) {
                            ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                                if (conditionExecutor.check("entity_condition", "entity_condition", p, power, "origins:prevent_entity_render", entity, entity, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                                    if (conditionExecutor.check("bientity_condition", "bientity_condition", p, power, "origins:prevent_entity_render", entity, entity, p.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                                        if(p.canSee(entity)){
                                            p.hideEntity(GenesisMC.getPlugin(), entity);
                                        }
                                        setActive(p, power.getTag(), true);
                                    } else {
                                        setActive(p, power.getTag(), false);
                                        if(!p.canSee(entity)){
                                            p.showEntity(GenesisMC.getPlugin(), entity);
                                        }
                                    }
                                } else {
                                    setActive(p, power.getTag(), false);
                                    if(!p.canSee(entity)){
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
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "origins:prevent_entity_render";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_entity_render;
    }
}
