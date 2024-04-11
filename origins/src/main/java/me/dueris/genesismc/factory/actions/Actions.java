package me.dueris.genesismc.factory.actions;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.OriginScheduler;
import me.dueris.genesismc.factory.actions.types.BiEntityActions;
import me.dueris.genesismc.factory.actions.types.BlockActions;
import me.dueris.genesismc.factory.actions.types.EntityActions;
import me.dueris.genesismc.factory.actions.types.ItemActions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Actions {

    public static HashMap<Entity, Boolean> resourceChangeTimeout = new HashMap<>();

    private static void chance(JSONObject action, Consumer<JSONObject> returnAction) {
        double chance = Double.parseDouble(action.get("chance").toString());
        double randomValue = new Random().nextDouble(1);

        if (randomValue <= chance) {
            JSONObject actionn = (JSONObject) action.get("action");
            returnAction.accept(actionn);
        }
    }

    private static void delay(JSONObject action, Consumer<JSONObject> returnAction) {
        int ticks = Integer.parseInt(action.get("ticks").toString());
        JSONObject delayedAction = (JSONObject) action.get("action");

        Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
            returnAction.accept(delayedAction);
        }, ticks);
    }

    private static void and(JSONObject action, Consumer<JSONObject> returnAction) {
        JSONArray andActions = (JSONArray) action.get("actions");
        for (Object actionObj : andActions) {
            JSONObject actionn = (JSONObject) actionObj;
            returnAction.accept(actionn);
        }
    }

    private static void choice(JSONObject action, Consumer<JSONObject> returnAction) {
        JSONArray actionsArray = (JSONArray) action.get("actions");
        List<JSONObject> actionsList = new ArrayList<>();

        for (Object actionObj : actionsArray) {
            JSONObject actionn = (JSONObject) actionObj;
            JSONObject element = (JSONObject) actionn.get("element");
            int weight = Integer.parseInt(actionn.get("weight").toString());
            for (int i = 0; i < weight; i++) {
                actionsList.add(element);
            }
        }

        if (!actionsList.isEmpty()) {
            int randomIndex = new Random().nextInt(actionsList.size());
            JSONObject chosenAction = actionsList.get(randomIndex);
            returnAction.accept(chosenAction);
        }
    }

    private static void side(JSONObject action, Consumer<JSONObject> returnAction) {
        if (action.get("side").toString().equalsIgnoreCase("server")) {
            JSONObject actionn = (JSONObject) action.get("action");
            returnAction.accept(actionn);
        }
    }

    private static boolean testMetaAction(JSONObject condition, String[] extras) {
        if (condition.containsKey("type")) {
            return condition.get("type").toString().equals("apoli:and") ||
                condition.get("type").toString().equals("apoli:choice") ||
                condition.get("type").toString().equals("apoli:chance") ||
                condition.get("type").toString().equals("apoli:delay") ||
                condition.get("type").toString().equals("apoli:if_else_list") ||
                condition.get("type").toString().equals("apoli:if_else") ||
                condition.get("type").toString().equals("apoli:side") ||
                condition.get("type").toString().equals("apoli:nothing") ||
                List.of(extras).contains(condition.get("type").toString());
        }
        return false;
    }

    public static void executeBiEntity(Power power, Entity actor, Entity target, JSONObject action) {
        OriginScheduler.updateTickingPower(actor, power);
        if (!action.containsKey("type") || action == null || action.isEmpty()) return;
        String type = action.get("type").toString();
        Pair entityPair = new Pair<CraftEntity, CraftEntity>() {

            @Override
            public CraftEntity left() {
                return (CraftEntity) actor;
            }

            @Override
            public CraftEntity right() {
                return (CraftEntity) target;
            }
        };
        if (testMetaAction(action, new String[]{"apoli:actor_action", "apoli:invert", "apoli:target_action"})) {
            switch (type) {
                case "apoli:invert" -> executeBiEntity(power, target, actor, (JSONObject) action.get("action"));

                case "apoli:actor_action" -> executeEntity(OriginScheduler.getCurrentTickingPower(actor).orElse(null), actor, (JSONObject) action.get("action"));

                case "apoli:target_action" -> executeEntity(OriginScheduler.getCurrentTickingPower(actor).orElse(null), target, (JSONObject) action.get("action"));

                case "apoli:and" -> and(action, actionn -> executeBiEntity(power, actor, target, actionn));

                case "apoli:chance" -> chance(action, actionn -> executeBiEntity(power, actor, target, actionn));

                case "apoli:choice" -> choice(action, actionn -> executeBiEntity(power, actor, target, actionn));

                case "apoli:delay" -> delay(action, actionn -> executeBiEntity(power, actor, target, actionn));

                case "apoli:nothing" -> {
                } // Literally does nothing

                case "apoli:side" -> side(action, actionn -> executeBiEntity(power, actor, target, actionn));

                case "if_else" -> {
                    boolean bool = ConditionExecutor.testBiEntity(OriginScheduler.getCurrentTickingPower(actor).orElse(null), (JSONObject) action.get("condition"), (CraftEntity) actor, (CraftEntity) target);
                    if (bool) {
                        executeBiEntity(power, actor, target, (JSONObject) action.get("if_action"));
                    } else {
                        executeBiEntity(power, actor, target, (JSONObject) action.get("else_action"));
                    }
                }
                case "if_else_list" -> {
                    if (action.containsKey("actions") && action.get("actions") instanceof JSONArray) {
                        for (Object o : (JSONArray) action.get("actions")) {
                            JSONObject arrayObject = (JSONObject) o;
                            if (arrayObject.containsKey("condition") && arrayObject.containsKey("action")) {
                                if (ConditionExecutor.testBiEntity(OriginScheduler.getCurrentTickingPower(actor).orElse(null), (JSONObject) arrayObject.get("condition"), (CraftEntity) actor, (CraftEntity) target)) {
                                    executeBiEntity(power, actor, target, (JSONObject) arrayObject.get("action"));
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Registrar<BiEntityActions.ActionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BIENTITY_ACTION);
            BiEntityActions.ActionFactory finAction = factory.get(NamespacedKey.fromString(action.get("type").toString()));
            if (finAction != null) {
                finAction.test(action, entityPair);
            }
        }
    }

    public static void executeItem(ItemStack item, JSONObject action) {
        if (!action.containsKey("type") || action == null || action.isEmpty()) return;
        String type = action.get("type").toString();
        if (testMetaAction(action, new String[]{})) {
            switch (type) {
                case "apoli:and" -> and(action, actionn -> executeItem(item, actionn));

                case "apoli:chance" -> chance(action, actionn -> executeItem(item, actionn));

                case "apoli:choice" -> choice(action, actionn -> executeItem(item, actionn));

                case "apoli:delay" -> delay(action, actionn -> executeItem(item, actionn));

                case "apoli:nothing" -> {
                } // Literally does nothing

                case "apoli:side" -> side(action, actionn -> executeItem(item, actionn));

                case "if_else" -> {
                    boolean bool = ConditionExecutor.testItem((JSONObject) action.get("condition"), item);
                    if (bool) {
                        executeItem(item, (JSONObject) action.get("if_action"));
                    } else {
                        executeItem(item, (JSONObject) action.get("else_action"));
                    }
                }
                case "if_else_list" -> {
                    if (action.containsKey("actions") && action.get("actions") instanceof JSONArray) {
                        for (Object o : (JSONArray) action.get("actions")) {
                            JSONObject arrayObject = (JSONObject) o;
                            if (arrayObject.containsKey("condition") && arrayObject.containsKey("action")) {
                                if (ConditionExecutor.testItem((JSONObject) arrayObject.get("condition"), item)) {
                                    executeItem(item, (JSONObject) arrayObject.get("action"));
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Registrar<ItemActions.ActionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ITEM_ACTION);
            ItemActions.ActionFactory finAction = factory.get(NamespacedKey.fromString(action.get("type").toString()));
            if (action != null) {
                finAction.test(action, item);
            }
        }
    }

    public static void executeEntity(Power power, Entity entity, JSONObject action) {
        OriginScheduler.updateTickingPower(entity, power);
        if (!action.containsKey("type") || action == null || action.isEmpty()) return;
        String type = action.get("type").toString();
        if (testMetaAction(action, new String[]{})) {
            switch (type) {
                case "apoli:and" -> and(action, actionn -> executeEntity(power, entity, actionn));

                case "apoli:chance" -> chance(action, actionn -> executeEntity(power, entity, actionn));

                case "apoli:choice" -> choice(action, actionn -> executeEntity(power, entity, actionn));

                case "apoli:delay" -> delay(action, actionn -> executeEntity(power, entity, actionn));

                case "apoli:nothing" -> {
                } // Literally does nothing

                case "apoli:side" -> side(action, actionn -> executeEntity(power, entity, actionn));

                case "if_else" -> {
                    boolean bool = ConditionExecutor.testEntity(power, (JSONObject) action.get("condition"), (CraftEntity) entity);
                    if (bool) {
                        executeEntity(power, entity, (JSONObject) action.get("if_action"));
                    } else {
                        executeEntity(power, entity, (JSONObject) action.get("else_action"));
                    }
                }
                case "if_else_list" -> {
                    if (action.containsKey("actions") && action.get("actions") instanceof JSONArray) {
                        for (Object o : (JSONArray) action.get("actions")) {
                            JSONObject arrayObject = (JSONObject) o;
                            if (arrayObject.containsKey("condition") && arrayObject.containsKey("action")) {
                                if (ConditionExecutor.testEntity(power, (JSONObject) arrayObject.get("condition"), (CraftEntity) entity)) {
                                    executeEntity(power, entity, (JSONObject) arrayObject.get("action"));
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Registrar<EntityActions.ActionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.ENTITY_ACTION);
            EntityActions.ActionFactory finAction = factory.get(NamespacedKey.fromString(action.get("type").toString()));
            if (finAction != null) {
                finAction.test(action, entity);
            }
        }
    }

    public static void executeBlock(Location location, JSONObject action) {
        if (!action.containsKey("type") || action == null || action.isEmpty()) return;
        String type = action.get("type").toString();
        if (testMetaAction(action, new String[]{"apoli:offset"})) {
            switch (type) {
                case "apoli:offset" ->
                    executeBlock(location.add(Double.valueOf(action.getOrDefault("x", "0").toString()), Double.valueOf(action.getOrDefault("y", "0").toString()), Double.valueOf(action.getOrDefault("z", "0").toString())), (JSONObject) action.get("action"));

                case "apoli:and" -> and(action, actionn -> executeBlock(location, actionn));

                case "apoli:chance" -> chance(action, actionn -> executeBlock(location, actionn));

                case "apoli:choice" -> choice(action, actionn -> executeBlock(location, actionn));

                case "apoli:delay" -> delay(action, actionn -> executeBlock(location, actionn));

                case "apoli:nothing" -> {
                } // Literally does nothing

                case "apoli:side" -> side(action, actionn -> executeBlock(location, actionn));

                case "if_else" -> {
                    boolean bool = ConditionExecutor.testBlock((JSONObject) action.get("condition"), CraftBlock.at(((CraftWorld) location.getWorld()).getHandle(), CraftLocation.toBlockPosition(location)));
                    if (bool) {
                        executeBlock(location, (JSONObject) action.get("if_action"));
                    } else {
                        executeBlock(location, (JSONObject) action.get("else_action"));
                    }
                }
                case "if_else_list" -> {
                    if (action.containsKey("actions") && action.get("actions") instanceof JSONArray) {
                        for (Object o : (JSONArray) action.get("actions")) {
                            JSONObject arrayObject = (JSONObject) o;
                            if (arrayObject.containsKey("condition") && arrayObject.containsKey("action")) {
                                if (ConditionExecutor.testBlock((JSONObject) arrayObject.get("condition"), CraftBlock.at(((CraftWorld) location.getWorld()).getHandle(), CraftLocation.toBlockPosition(location)))) {
                                    executeBlock(location, (JSONObject) arrayObject.get("action"));
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Registrar<BlockActions.ActionFactory> factory = GenesisMC.getPlugin().registry.retrieve(Registries.BLOCK_ACTION);
            BlockActions.ActionFactory finAction = factory.get(NamespacedKey.fromString(action.get("type").toString()));
            if (finAction != null) {
                finAction.test(action, location);
            }
        }
    }

    public static EquipmentSlot getSlotFromString(String slotName) {
        switch (slotName.toLowerCase()) {
            case "armor.helmet", "head":
                return EquipmentSlot.HEAD;
            case "armor.chest", "chest":
                return EquipmentSlot.CHEST;
            case "armor.legs", "legs":
                return EquipmentSlot.LEGS;
            case "armor.feet", "feet":
                return EquipmentSlot.FEET;
            case "hand", "mainhand": // Have "hand" for legacy support
                return EquipmentSlot.HAND;
            case "offhand":
                return EquipmentSlot.OFF_HAND;
            default:
                return null;
        }
    }

    public static void registerAll() {
        new BiEntityActions().register();
        new BlockActions().register();
        new EntityActions().register();
        new ItemActions().register();
    }
}
