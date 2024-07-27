package me.dueris.originspaper.factory.actions;

import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrar;
import me.dueris.calio.util.holders.Pair;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.types.BiEntityActions;
import me.dueris.originspaper.factory.actions.types.BlockActions;
import me.dueris.originspaper.factory.actions.types.EntityActions;
import me.dueris.originspaper.factory.actions.types.ItemActions;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class Actions {
	public static BiEntityActions bientityActions = new BiEntityActions();
	public static BlockActions blockActions = new BlockActions();
	public static EntityActions entityActions = new EntityActions();
	public static ItemActions itemActions = new ItemActions();
	public static HashMap<Entity, Boolean> resourceChangeTimeout = new HashMap<>();

	private static void chance(@NotNull FactoryJsonObject action, Consumer<FactoryJsonObject> returnAction) {
		float chance = action.getNumber("chance").getFloat();
		double randomValue = new Random().nextFloat(1.0F);
		if (randomValue <= (double) chance) {
			FactoryJsonObject actionn = action.getJsonObject("action");
			returnAction.accept(actionn);
		}
	}

	private static void delay(@NotNull FactoryJsonObject action, Consumer<FactoryJsonObject> returnAction) {
		int ticks = action.getNumber("ticks").getInt();
		FactoryJsonObject delayedAction = action.getJsonObject("action");
		Bukkit.getScheduler().runTaskLater(OriginsPaper.getPlugin(), () -> returnAction.accept(delayedAction), ticks);
	}

	private static void and(@NotNull FactoryJsonObject action, Consumer<FactoryJsonObject> returnAction) {
		FactoryJsonArray andActions = action.getJsonArray("actions");

		for (FactoryJsonObject actionObj : andActions.asJsonObjectList()) {
			returnAction.accept(actionObj);
		}
	}

	private static void choice(@NotNull FactoryJsonObject action, Consumer<FactoryJsonObject> returnAction) {
		FactoryJsonArray actionsArray = action.getJsonArray("actions");
		List<FactoryJsonObject> actionsList = new ArrayList<>();

		for (FactoryJsonObject actionObj : actionsArray.asJsonObjectList()) {
			FactoryJsonObject element = actionObj.getJsonObject("element");
			int weight = actionObj.getNumber("weight").getInt();

			for (int i = 0; i < weight; i++) {
				actionsList.add(element);
			}
		}

		if (!actionsList.isEmpty()) {
			int randomIndex = new Random().nextInt(actionsList.size());
			FactoryJsonObject chosenAction = actionsList.get(randomIndex);
			returnAction.accept(chosenAction);
		}
	}

	private static void side(@NotNull FactoryJsonObject action, Consumer<FactoryJsonObject> returnAction) {
		if (action.getString("side").equalsIgnoreCase("server")) {
			FactoryJsonObject actionn = action.getJsonObject("action");
			returnAction.accept(actionn);
		}
	}

	private static boolean testMetaAction(@NotNull FactoryJsonObject action, String[] extras) {
		return action.isPresent("type") && (action.getString("type").equals("apoli:and")
			|| action.getString("type").equals("apoli:choice")
			|| action.getString("type").equals("apoli:chance")
			|| action.getString("type").equals("apoli:delay")
			|| action.getString("type").equals("apoli:if_else_list")
			|| action.getString("type").equals("apoli:if_else")
			|| action.getString("type").equals("apoli:side")
			|| action.getString("type").equals("apoli:nothing")
			|| List.of(extras).contains(action.getString("type")));
	}

	public static void executeBiEntity(Entity actor, Entity target, @NotNull FactoryJsonObject action) {
		if (action.isPresent("type") && !action.isEmpty() && target != actor) {
			String type = action.getString("type");
			Pair<CraftEntity, CraftEntity> entityPair = new Pair<>((CraftEntity) actor, (CraftEntity) target);
			if (testMetaAction(action, new String[]{"apoli:actor_action", "apoli:invert", "apoli:target_action"})) {
				switch (type) {
					case "apoli:invert":
						executeBiEntity(target, actor, action.getJsonObject("action"));
						break;
					case "apoli:actor_action":
						executeEntity(actor, action.getJsonObject("action"));
						break;
					case "apoli:target_action":
						executeEntity(target, action.getJsonObject("action"));
						break;
					case "apoli:and":
						and(action, actionn -> executeBiEntity(actor, target, actionn));
						break;
					case "apoli:chance":
						chance(action, actionn -> executeBiEntity(actor, target, actionn));
						break;
					case "apoli:choice":
						choice(action, actionn -> executeBiEntity(actor, target, actionn));
						break;
					case "apoli:delay":
						delay(action, actionn -> executeBiEntity(actor, target, actionn));
					case "apoli:nothing":
					default:
						break;
					case "apoli:side":
						side(action, actionn -> executeBiEntity(actor, target, actionn));
						break;
					case "apoli:if_else":
						boolean bool = ConditionExecutor.testBiEntity(action.getJsonObject("condition"), (CraftEntity) actor, (CraftEntity) target);
						if (bool) {
							executeBiEntity(actor, target, action.getJsonObject("if_action"));
						} else {
							executeBiEntity(actor, target, action.getJsonObject("else_action"));
						}
						break;
					case "apoli:if_else_list":
						if (action.isPresent("actions") && action.isJsonArray("actions")) {
							for (FactoryJsonObject arrayObject : action.getJsonArray("actions").asJsonObjectList()) {
								if (arrayObject.isPresent("condition")
									&& arrayObject.isPresent("action")
									&& ConditionExecutor.testBiEntity(arrayObject.getJsonObject("condition"), (CraftEntity) actor, (CraftEntity) target)) {
									executeBiEntity(actor, target, arrayObject.getJsonObject("action"));
								}
							}
						}
				}
			} else {
				Registrar<BiEntityActions.ActionFactory> factory = OriginsPaper.getPlugin().registry.retrieve(Registries.BIENTITY_ACTION);
				BiEntityActions.ActionFactory finAction = factory.get(ResourceLocation.parse(action.getString("type")));
				if (finAction != null) {
					finAction.test(action, entityPair);
				}
			}
		}
	}

	public static void executeItem(ItemStack item, World world, @NotNull FactoryJsonObject action) {
		if (action.isPresent("type") && action != null && !action.isEmpty()) {
			String type = action.getString("type");
			if (testMetaAction(action, new String[0])) {
				switch (type) {
					case "apoli:and":
						and(action, actionn -> executeItem(item, world, actionn));
						break;
					case "apoli:chance":
						chance(action, actionn -> executeItem(item, world, actionn));
						break;
					case "apoli:choice":
						choice(action, actionn -> executeItem(item, world, actionn));
						break;
					case "apoli:delay":
						delay(action, actionn -> executeItem(item, world, actionn));
					case "apoli:nothing":
					default:
						break;
					case "apoli:side":
						side(action, actionn -> executeItem(item, world, actionn));
						break;
					case "apoli:if_else":
						boolean bool = ConditionExecutor.testItem(action.getJsonObject("condition"), item);
						if (bool) {
							executeItem(item, world, action.getJsonObject("if_action"));
						} else {
							executeItem(item, world, action.getJsonObject("else_action"));
						}
						break;
					case "apoli:if_else_list":
						if (action.isPresent("actions") && action.getElement("actions").isJsonArray()) {
							for (FactoryJsonObject arrayObject : action.getJsonArray("actions").asJsonObjectList()) {
								if (arrayObject.isPresent("condition")
									&& arrayObject.isPresent("action")
									&& ConditionExecutor.testItem(arrayObject.getJsonObject("condition"), item)) {
									executeItem(item, world, arrayObject.getJsonObject("action"));
								}
							}
						}
				}
			} else {
				Registrar<ItemActions.ActionFactory> factory = OriginsPaper.getPlugin().registry.retrieve(Registries.ITEM_ACTION);
				ItemActions.ActionFactory finAction = factory.get(ResourceLocation.parse(action.getString("type")));
				if (action != null) {
					finAction.test(action, item, world);
				}
			}
		}
	}

	public static void executeEntity(Entity entity, @NotNull FactoryJsonObject action) {
		if (action.isPresent("type") && action != null && !action.isEmpty()) {
			String type = action.getString("type");
			if (testMetaAction(action, new String[0])) {
				switch (type) {
					case "apoli:and":
						and(action, actionn -> executeEntity(entity, actionn));
						break;
					case "apoli:chance":
						chance(action, actionn -> executeEntity(entity, actionn));
						break;
					case "apoli:choice":
						choice(action, actionn -> executeEntity(entity, actionn));
						break;
					case "apoli:delay":
						delay(action, actionn -> executeEntity(entity, actionn));
					case "apoli:nothing":
					default:
						break;
					case "apoli:side":
						side(action, actionn -> executeEntity(entity, actionn));
						break;
					case "apoli:if_else":
						boolean bool = ConditionExecutor.testEntity(action.getJsonObject("condition"), (CraftEntity) entity);
						if (bool) {
							executeEntity(entity, action.getJsonObject("if_action"));
						} else {
							executeEntity(entity, action.getJsonObject("else_action"));
						}
						break;
					case "apoli:if_else_list":
						if (action.isPresent("actions") && action.getElement("actions").isJsonArray()) {
							for (FactoryJsonObject arrayObject : action.getJsonArray("actions").asJsonObjectList()) {
								if (arrayObject.isPresent("condition")
									&& arrayObject.isPresent("action")
									&& ConditionExecutor.testEntity(arrayObject.getJsonObject("condition"), (CraftEntity) entity)) {
									executeEntity(entity, arrayObject.getJsonObject("action"));
								}
							}
						}
				}
			} else {
				Registrar<EntityActions.ActionFactory> factory = OriginsPaper.getPlugin().registry.retrieve(Registries.ENTITY_ACTION);
				EntityActions.ActionFactory finAction = factory.get(ResourceLocation.parse(action.getString("type")));
				if (finAction != null) {
					finAction.test(action, (CraftEntity) entity);
				}
			}
		}
	}

	public static void executeBlock(Location location, FactoryJsonObject action) {
		if (action != null && !action.isEmpty() && action.isPresent("type")) {
			String type = action.getString("type");
			if (testMetaAction(action, new String[]{"apoli:offset"})) {
				switch (type) {
					case "apoli:offset":
						executeBlock(
							location.add(
								action.getNumberOrDefault("x", 0).getDouble(),
								action.getNumberOrDefault("y", 0).getDouble(),
								action.getNumberOrDefault("z", 0).getDouble()
							),
							action.getJsonObject("action")
						);
						break;
					case "apoli:and":
						and(action, actionn -> executeBlock(location, actionn));
						break;
					case "apoli:chance":
						chance(action, actionn -> executeBlock(location, actionn));
						break;
					case "apoli:choice":
						choice(action, actionn -> executeBlock(location, actionn));
						break;
					case "apoli:delay":
						delay(action, actionn -> executeBlock(location, actionn));
					case "apoli:nothing":
					default:
						break;
					case "apoli:side":
						side(action, actionn -> executeBlock(location, actionn));
						break;
					case "apoli:if_else":
						boolean bool = ConditionExecutor.testBlock(
							action.getJsonObject("condition"), CraftBlock.at(((CraftWorld) location.getWorld()).getHandle(), CraftLocation.toBlockPosition(location))
						);
						if (bool) {
							executeBlock(location, action.getJsonObject("if_action"));
						} else {
							executeBlock(location, action.getJsonObject("else_action"));
						}
						break;
					case "apoli:if_else_list":
						if (action.isPresent("actions") && action.getElement("actions").isJsonArray()) {
							for (FactoryJsonObject arrayObject : action.getJsonArray("actions").asJsonObjectList()) {
								if (arrayObject.isPresent("condition")
									&& arrayObject.isPresent("action")
									&& ConditionExecutor.testBlock(
									arrayObject.getJsonObject("condition"),
									CraftBlock.at(((CraftWorld) location.getWorld()).getHandle(), CraftLocation.toBlockPosition(location))
								)) {
									executeBlock(location, arrayObject.getJsonObject("action"));
								}
							}
						}
				}
			} else {
				Registrar<BlockActions.ActionFactory> factory = OriginsPaper.getPlugin().registry.retrieve(Registries.BLOCK_ACTION);
				BlockActions.ActionFactory finAction = factory.get(ResourceLocation.parse(action.getString("type")));
				if (finAction != null) {
					finAction.test(action, location);
				}
			}
		}
	}

	@Contract(pure = true)
	@Nullable
	public static EquipmentSlot getSlotFromString(@NotNull String slotName) {
		String var1 = slotName.toLowerCase();
		switch (var1) {
			case "armor.helmet":
			case "head":
				return EquipmentSlot.HEAD;
			case "armor.chest":
			case "chest":
				return EquipmentSlot.CHEST;
			case "armor.legs":
			case "legs":
				return EquipmentSlot.LEGS;
			case "armor.feet":
			case "feet":
				return EquipmentSlot.FEET;
			case "hand":
			case "mainhand":
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
