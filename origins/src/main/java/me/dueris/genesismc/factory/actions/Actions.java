package me.dueris.genesismc.factory.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.types.BiEntityActions;
import me.dueris.genesismc.factory.actions.types.BlockActions;
import me.dueris.genesismc.factory.actions.types.EntityActions;
import me.dueris.genesismc.factory.actions.types.ItemActions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Actions {

	public static HashMap<Entity, Boolean> resourceChangeTimeout = new HashMap<>();

	public static void BiEntityActionType(Entity actor, Entity target, JSONObject action) {
		if (!action.containsKey("type")) return;
		String type = action.get("type").toString();

		if (type.equals("apoli:invert")) {
			BiEntityActionType(target, actor, (JSONObject) action.get("action"));
		} else if (type.equals("apoli:and")) {
			JSONArray andActions = (JSONArray) action.get("actions");
			for (Object actionObj : andActions) {
				JSONObject actionn = (JSONObject) actionObj;
				BiEntityActionType(actor, target, actionn);
			}
		} else if (type.equals("apoli:chance")) {
			double chance = Double.parseDouble(action.get("chance").toString());
			double randomValue = new Random().nextDouble(1);

			if (randomValue <= chance) {
				JSONObject actionn = (JSONObject) action.get("action");
				BiEntityActionType(actor, target, actionn);
			}
		} else if (type.equals("apoli:choice")) {
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
				int randomIndex = (int) new Random().nextInt(actionsList.size());
				JSONObject chosenAction = actionsList.get(randomIndex);
				BiEntityActionType(actor, target, chosenAction);
			}
		} else if (type.equals("apoli:delay")) {
			int ticks = Integer.parseInt(action.get("ticks").toString());
			JSONObject delayedAction = (JSONObject) action.get("action");

			Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
				BiEntityActionType(actor, target, delayedAction);
			}, ticks);
		} else if (type.equals("apoli:nothing")) {
			// Literally does nothing
		} else if (type.equals("apoli:side")) {
			if (action.get("side").toString().toLowerCase().equals("server")) {
				JSONObject actionn = (JSONObject) action.get("action");
				BiEntityActionType(actor, target, actionn);
			}
		} else if (type.equals("apoli:if_else")) {
			boolean bool = ConditionExecutor.testBiEntity((JSONObject) action.get("condition"), (CraftEntity) actor, (CraftEntity) target);
			if (bool) {
				BiEntityActionType(actor, target, (JSONObject) action.get("if_action"));
			} else {
				BiEntityActionType(actor, target, (JSONObject) action.get("else_action"));
			}
		} else {
			BiEntityActions.runbiEntity(actor, target, action);
		}
	}

	public static void ItemActionType(ItemStack item, JSONObject power) {
		if (power == null || power.isEmpty()) return;
		String type = power.get("type").toString();

		if (type.equals("apoli:and")) {
			JSONArray andActions = (JSONArray) power.get("actions");
			for (Object actionObj : andActions) {
				JSONObject action = (JSONObject) actionObj;
				ItemActionType(item, action);
			}
		} else if (type.equals("apoli:chance")) {
			double chance = Double.parseDouble(power.get("chance").toString());
			double randomValue = new Random().nextDouble(1);

			if (randomValue <= chance) {
				JSONObject action = (JSONObject) power.get("action");
				ItemActionType(item, action);
			} else if (power.containsKey("fail_action")) {
				JSONObject failAction = (JSONObject) power.get("fail_action");
				ItemActionType(item, failAction);
			}
		} else if (type.equals("apoli:choice")) {
			JSONArray actionsArray = (JSONArray) power.get("actions");
			List<JSONObject> actionsList = new ArrayList<>();

			for (Object actionObj : actionsArray) {
				JSONObject action = (JSONObject) actionObj;
				JSONObject element = (JSONObject) action.get("element");
				int weight = Integer.parseInt(action.get("weight").toString());
				for (int i = 0; i < weight; i++) {
					actionsList.add(element);
				}
			}

			if (!actionsList.isEmpty()) {
				int randomIndex = (int) (Math.random() * actionsList.size());
				JSONObject chosenAction = actionsList.get(randomIndex);
				ItemActionType(item, chosenAction);
			}
		} else if (type.equals("apoli:delay")) {
			int ticks = Integer.parseInt(power.get("ticks").toString());
			JSONObject delayedAction = (JSONObject) power.get("action");

			Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
				ItemActionType(item, delayedAction);
			}, ticks);
		} else if (type.equals("apoli:nothing")) {
			// Literally does nothing
		} else if (type.equals("apoli:if_else")) {
			if (ConditionExecutor.testItem((JSONObject) power.get("item_condition"), item)) {
				ItemActionType(item, (JSONObject) power.get("if_action"));
			} else {
				ItemActionType(item, (JSONObject) power.get("else_action"));
			}
		} else if (type.equals("apoli:side")) {
			if (power.get("side").toString().toLowerCase().equals("server")) {
				JSONObject action = (JSONObject) power.get("action");
				ItemActionType(item, action);
			}
		} else {
			ItemActions.runItem(item, power);
		}
	}

	public static void EntityActionType(Entity entity, JSONObject action) {
		if (action == null || action.isEmpty()) return;
		String type = action.get("type").toString();

		if (type.equals("apoli:and")) {
			JSONArray andActions = (JSONArray) action.get("actions");
			for (Object actionObj : andActions) {
				JSONObject actionn = (JSONObject) actionObj;
				EntityActionType(entity, actionn);
			}
		} else if (type.equals("apoli:chance")) {
			double chance = Double.parseDouble(action.get("chance").toString());
			double randomValue = new Random().nextDouble(1);

			if (randomValue <= chance) {
				JSONObject actionn = (JSONObject) action.get("action");
				EntityActionType(entity, actionn);
			} else if (action.containsKey("fail_action")) {
				JSONObject failAction = (JSONObject) action.get("fail_action");
				EntityActionType(entity, failAction);
			}
		} else if (type.equals("apoli:choice")) {
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
				int randomIndex = (int) (Math.random() * actionsList.size());
				JSONObject chosenAction = actionsList.get(randomIndex);
				EntityActionType(entity, chosenAction);
			}
		} else if (type.equals("apoli:delay")) {
			int ticks = Integer.parseInt(action.get("ticks").toString());
			JSONObject delayedAction = (JSONObject) action.get("action");

			Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
				EntityActionType(entity, delayedAction);
			}, ticks);
		} else if (type.equals("apoli:nothing")) {
			//literally does nothin
		} else if (type.equals("apoli:if_else")) {
			boolean bool = ConditionExecutor.testEntity((JSONObject) action.get("condition"), (CraftEntity) entity);
			if (bool) {
				EntityActionType(entity, (JSONObject) action.get("if_action"));
			} else {
				EntityActionType(entity, (JSONObject) action.get("else_action"));
			}
		} else if (type.equals("apoli:side")) {
			if (action.get("side").toString().toLowerCase().equals("server")) {
				EntityActionType(entity, (JSONObject) action.get("action"));
			}
		} else {
			EntityActions.runEntity(entity, action);
		}
	}

	public static void BlockActionType(Location location, JSONObject action) {
		if (action == null || action.isEmpty() || !action.containsKey("type")) return;
		String type = action.get("type").toString();

		if (type.equals("apoli:and")) {
			JSONArray andActions = (JSONArray) action.get("actions");
			for (Object actionObj : andActions) {
				JSONObject actionn = (JSONObject) actionObj;
				BlockActionType(location, actionn);
			}
		} else if (type.equals("apoli:chance")) {
			double chance = Double.parseDouble(action.get("chance").toString());
			double randomValue = new Random().nextDouble(1);

			if (randomValue <= chance) {
				JSONObject actionn = (JSONObject) action.get("action");
				BlockActionType(location, actionn);
			} else if (action.containsKey("fail_action")) {
				JSONObject failAction = (JSONObject) action.get("fail_action");
				BlockActionType(location, failAction);
			}
		} else if (type.equals("apoli:choice")) {
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
				int randomIndex = (int) (Math.random() * actionsList.size());
				JSONObject chosenAction = actionsList.get(randomIndex);
				BlockActionType(location, chosenAction);
			}
		} else if (type.equals("apoli:delay")) {
			int ticks = Integer.parseInt(action.get("ticks").toString());
			JSONObject delayedAction = (JSONObject) action.get("action");

			Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
				BlockActionType(location, delayedAction);
			}, ticks);
		} else if (type.equals("apoli:nothing")) {
			// Literally does nothing
		} else if (type.equals("apoli:if_else")) {
			if (ConditionExecutor.testBlock((JSONObject) action.get("block_condition"), (CraftBlock) location.getBlock())) {
				BlockActionType(location, (JSONObject) action.get("if_action"));
			} else {
				BlockActionType(location, (JSONObject) action.get("else_action"));
			}
		} else if (type.equals("apoli:side")) {
			if (action.get("side").toString().toLowerCase().equals("server")) {
				JSONObject actionn = (JSONObject) action.get("action");
				BlockActionType(location, actionn);
			}
		} else {
			BlockActions.runBlock(location, action);
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

	public static void spawnEffectCloud(Entity entity, float radius, int waitTime, PotionEffect effect) {
		if (entity != null) {
			Location entityLocation = entity.getLocation();

			org.bukkit.entity.AreaEffectCloud effectCloud = entityLocation.getWorld()
				.spawn(entityLocation, org.bukkit.entity.AreaEffectCloud.class);

			effectCloud.setRadius(radius);
			effectCloud.setDuration(waitTime);

			if (effect != null) {
				effectCloud.addCustomEffect(effect, true);
			}
		}
	}
}
