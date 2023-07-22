package me.dueris.genesismc.core.factory.powers.OriginsMod.world;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

import static me.dueris.genesismc.core.factory.powers.Powers.entity_group;

public class EntityGroupManager extends BukkitRunnable {
    @Override
    public void run() {
        for (World world : Bukkit.getWorlds()){
            for (Entity entity : world.getEntities()) {
                //Begin entity cases for removal
                if(!entity.getType().isAlive()) {
                    continue;
                }
                if(!entity.getType().isSpawnable()) {
                    continue;
                }
                if(entity.getType() == EntityType.DROPPED_ITEM) {
                    continue;
                }
                if(entity instanceof Player){
                    //Player case, check for power
                    for (OriginContainer origin : OriginPlayer.getOrigin(((Player) entity).getPlayer()).values()){
                        if(entity_group.contains(entity)){
                            if(origin.getPowerFileFromType("origins:entity_group").get("group").equalsIgnoreCase("undead")){
                                undead.put(entity.getEntityId(), entity.getType().name());
                            } else if (origin.getPowerFileFromType("origins:entity_group").get("group").equalsIgnoreCase("arthropod")){
                                arthropod.put(entity.getEntityId(), entity.getType().name());
                            } else if (origin.getPowerFileFromType("origins:entity_group").get("group").equalsIgnoreCase("illager")){
                                illager.put(entity.getEntityId(), entity.getType().name());
                            } else if (origin.getPowerFileFromType("origins:entity_group").get("group").equalsIgnoreCase("aquatic")){
                                aquatic.put(entity.getEntityId(), entity.getType().name());
                            } else if (origin.getPowerFileFromType("origins:entity_group").get("group").equalsIgnoreCase("default")){
                                default_group.put(entity.getEntityId(), entity.getType().name());
                            }
                        }
                    }
                }

                //Sort into array groups
                if(sortEntity(entity.getType()).split("%")[1].equalsIgnoreCase("undead")){
                    undead.put(entity.getEntityId(), entity.getType().name());
                } else if (sortEntity(entity.getType()).split("%")[1].equalsIgnoreCase("arthropod")){
                    arthropod.put(entity.getEntityId(), entity.getType().name());
                } else if (sortEntity(entity.getType()).split("%")[1].equalsIgnoreCase("illager")){
                    illager.put(entity.getEntityId(), entity.getType().name());
                } else if (sortEntity(entity.getType()).split("%")[1].equalsIgnoreCase("aquatic")){
                    aquatic.put(entity.getEntityId(), entity.getType().name());
                } else if (sortEntity(entity.getType()).split("%")[1].equalsIgnoreCase("default")){
                    default_group.put(entity.getEntityId(), entity.getType().name());
                }
            }
        }
    }

    private static final Map<String, String> entityCategories = new HashMap<>();
    public static final Map<Integer, String> undead = new HashMap<>();
    public static final Map<Integer, String> arthropod = new HashMap<>();
    public static final Map<Integer, String> illager = new HashMap<>();
    public static final Map<Integer, String> aquatic = new HashMap<>();
    public static final Map<Integer, String> default_group = new HashMap<>();

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

}
