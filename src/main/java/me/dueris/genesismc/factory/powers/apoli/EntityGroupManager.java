package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EntityGroupManager extends CraftPower {

    public static final Map<Integer, String> undead = new HashMap<>();
    public static final Map<Integer, String> arthropod = new HashMap<>();
    public static final Map<Integer, String> illager = new HashMap<>();
    public static final Map<Integer, String> aquatic = new HashMap<>();
    public static final Map<Integer, String> default_group = new HashMap<>();
    private static final Map<String, String> entityCategories = new HashMap<>();
    public static EntityGroupManager INSTANCE = new EntityGroupManager();
    public static boolean stop = false;

    public static void stop(){
        stop = true;
    }

    static {
        // Undead
        entityCategories.put("zombie", "undead");
        entityCategories.put("skeleton", "undead");
        entityCategories.put("creeper", "undead");
        entityCategories.put("wither", "undead");
        entityCategories.put("zombified_piglin", "undead");
        entityCategories.put("drowned", "undead");
        entityCategories.put("husk", "undead");
        entityCategories.put("stray", "undead");
        entityCategories.put("phantom", "undead");
        entityCategories.put("skeleton_horse", "undead");
        entityCategories.put("zoglin", "undead");
        entityCategories.put("giant", "undead");

        // Arthropod
        entityCategories.put("spider", "arthropod");
        entityCategories.put("bee", "arthropod");
        entityCategories.put("silverfish", "arthropod");
        entityCategories.put("endermite", "arthropod");
        entityCategories.put("cave_spider", "arthropod");

        // Illager
        entityCategories.put("vindicator", "illager");
        entityCategories.put("evoker", "illager");
        entityCategories.put("pillager", "illager");
        entityCategories.put("illusioner", "illager");
        entityCategories.put("ravager", "illager");
        entityCategories.put("illusioner", "undead");

        // Aquatic
        entityCategories.put("dolphin", "aquatic");
        entityCategories.put("cod", "aquatic");
        entityCategories.put("salmon", "aquatic");
        entityCategories.put("squid", "aquatic");
        entityCategories.put("tropical_fish", "aquatic");
        entityCategories.put("pufferfish", "aquatic");
        entityCategories.put("turtle", "aquatic");
        entityCategories.put("guardian", "aquatic");
        entityCategories.put("elder_guardian", "aquatic");
        entityCategories.put("axolotl", "aquatic");
    }

    public static String sortEntity(EntityType entityType) {
        String entityName = entityType.name().toLowerCase();

        // Sort entities into categories
        String category = entityCategories.getOrDefault(entityName, "default");
        return entityType.name() + "%" + category;
    }

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

    public void startTick() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(stop) cancel();
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        //Begin entity cases for removal
                        if (!entity.getType().isAlive()) {
                            continue;
                        }
                        if (!entity.getType().isSpawnable()) {
                            continue;
                        }
                        if (entity.getType() == EntityType.DROPPED_ITEM) {
                            continue;
                        }
                        if (entity instanceof Player p) {
                            //Player case, check for power
                            ConditionExecutor executor = GenesisMC.getConditionExecutor();
                            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                                    if (executor.check("condition", "conditions", p, power, getPowerFile(), entity, null, entity.getLocation().getBlock(), null, p.getItemInHand(), null)) {
                                        if (!getPowerArray().contains(p)) return;
                                        setActive(p, power.getTag(), true);
                                        if (entity_group.contains(entity)) {
                                            if(power.getObjectOrDefault("group", null) == null) throw new IllegalArgumentException("Group in entity_group power was not defined.");
                                            if (power.getStringOrDefault("group", null).equalsIgnoreCase("undead")) {
                                                undead.put(entity.getEntityId(), entity.getType().name());
                                            } else if (power.getStringOrDefault("group", null).equalsIgnoreCase("arthropod")) {
                                                arthropod.put(entity.getEntityId(), entity.getType().name());
                                            } else if (power.getStringOrDefault("group", null).equalsIgnoreCase("illager")) {
                                                illager.put(entity.getEntityId(), entity.getType().name());
                                            } else if (power.getStringOrDefault("group", null).equalsIgnoreCase("aquatic")) {
                                                aquatic.put(entity.getEntityId(), entity.getType().name());
                                            } else if (power.getStringOrDefault("group", null).equalsIgnoreCase("default")) {
                                                default_group.put(entity.getEntityId(), entity.getType().name());
                                            }
                                        }
                                    } else {
                                        setActive(p, power.getTag(), false);
                                    }
                                }
                            }
                        }

                        //Sort into array groups
                        if (sortEntity(entity.getType()).split("%")[1].equalsIgnoreCase("undead")) {
                            undead.put(entity.getEntityId(), entity.getType().name());
                        } else if (sortEntity(entity.getType()).split("%")[1].equalsIgnoreCase("arthropod")) {
                            arthropod.put(entity.getEntityId(), entity.getType().name());
                        } else if (sortEntity(entity.getType()).split("%")[1].equalsIgnoreCase("illager")) {
                            illager.put(entity.getEntityId(), entity.getType().name());
                        } else if (sortEntity(entity.getType()).split("%")[1].equalsIgnoreCase("aquatic")) {
                            aquatic.put(entity.getEntityId(), entity.getType().name());
                        } else if (sortEntity(entity.getType()).split("%")[1].equalsIgnoreCase("default")) {
                            default_group.put(entity.getEntityId(), entity.getType().name());
                        }
                    }
                }
            }
        }.runTaskLater(GenesisMC.getPlugin(), 200L);
    }

    @Override
    public String getPowerFile() {
        return "apoli:entity_group";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return entity_group;
    }

}
