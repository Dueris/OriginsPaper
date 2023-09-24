package me.dueris.genesismc.factory.powers.world;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

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
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    Player p;

    public EntityGroupManager() {
        this.p = p;
    }

    @Override
    public void run(Player p) {
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
                if (entity instanceof Player) {
                    //Player case, check for power
                    for (OriginContainer origin : OriginPlayer.getOrigin(((Player) entity).getPlayer()).values()) {
                        ConditionExecutor executor = new ConditionExecutor();
                        for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                            if (executor.check("condition", "conditions", (Player) entity, power, getPowerFile(), entity, null, entity.getLocation().getBlock(), null, ((Player) entity).getItemInHand(), null)) {
                                if (!getPowerArray().contains(entity)) return;
                                setActive(power.getTag(), true);
                                if (entity_group.contains(entity)) {
                                    if (power.get("group", null).equalsIgnoreCase("undead")) {
                                        undead.put(entity.getEntityId(), entity.getType().name());
                                    } else if (power.get("group", null).equalsIgnoreCase("arthropod")) {
                                        arthropod.put(entity.getEntityId(), entity.getType().name());
                                    } else if (power.get("group", null).equalsIgnoreCase("illager")) {
                                        illager.put(entity.getEntityId(), entity.getType().name());
                                    } else if (power.get("group", null).equalsIgnoreCase("aquatic")) {
                                        aquatic.put(entity.getEntityId(), entity.getType().name());
                                    } else if (power.get("group", null).equalsIgnoreCase("default")) {
                                        default_group.put(entity.getEntityId(), entity.getType().name());
                                    }
                                }
                            } else {
                                if (!getPowerArray().contains(entity)) return;
                                setActive(power.getTag(), false);
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

    @Override
    public String getPowerFile() {
        return "origins:entity_group";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return entity_group;
    }

}
