package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.util.Util;
import net.minecraft.core.component.DataComponents;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

public class ModifyFoodPower extends PowerType implements Listener {
	public static ArrayList<Player> preventEffects = new ArrayList<>();
	private final FactoryJsonObject itemCondition;
	private final FactoryJsonObject itemAction;
	private final ItemStack replaceStack;
	private final List<FactoryJsonObject> foodModifiers;
	private final List<FactoryJsonObject> saturationModifiers;
	private final FactoryJsonObject entityAction;
	private final boolean shouldPreventEffects;

	public ModifyFoodPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject itemCondition, FactoryJsonObject itemAction, ItemStack replaceStack, FactoryJsonObject foodModifier, FactoryJsonArray foodModifiers, FactoryJsonObject saturationModifier, FactoryJsonArray saturationModifiers, FactoryJsonObject entityAction, boolean preventEffects) {
		super(name, description, hidden, condition, loading_priority);
		this.itemCondition = itemCondition;
		this.itemAction = itemAction;
		this.replaceStack = replaceStack;
		this.foodModifiers = foodModifier == null ? foodModifiers.asJsonObjectList() : List.of(foodModifier);
		this.saturationModifiers = saturationModifier == null ? saturationModifiers.asJsonObjectList() : List.of(saturationModifier);
		this.entityAction = entityAction;
		this.shouldPreventEffects = preventEffects;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("modify_food"))
			.add("item_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("item_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("replace_stack", ItemStack.class, new OptionalInstance())
			.add("food_modifier", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("food_modifiers", FactoryJsonArray.class, new FactoryJsonArray(new JsonArray()))
			.add("saturation_modifier", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("saturation_modifiers", FactoryJsonArray.class, new FactoryJsonArray(new JsonArray()))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("prevent_effects", boolean.class, false);
	}


	@EventHandler(priority = EventPriority.HIGHEST)
	public void saturationorwhateverRUN(@NotNull PlayerItemConsumeEvent e) {
		Player player = e.getPlayer();
		if (getPlayers().contains(player)) {
			if (isActive(player) && ConditionExecutor.testItem(itemCondition, e.getItem())) {
				for (FactoryJsonObject jsonObject : foodModifiers) {
					if (jsonObject.isPresent("value")) {
						Double val = jsonObject.getNumber("value").getDouble();
						String operation = jsonObject.getString("operation");
						BinaryOperator mathOperator = Util.getOperationMappingsDouble().get(operation);
						if (mathOperator != null && CraftItemStack.asNMSCopy(e.getItem()).get(DataComponents.FOOD) != null) {
							double finalValue = (double) mathOperator.apply(Integer.valueOf(CraftItemStack.asNMSCopy(e.getItem()).get(DataComponents.FOOD).nutrition()).doubleValue(), val);
							finalValue = finalValue - Integer.valueOf(CraftItemStack.asNMSCopy(e.getItem()).get(DataComponents.FOOD).nutrition()).doubleValue();
							player.setFoodLevel(Integer.parseInt(String.valueOf(Math.round(player.getFoodLevel() + finalValue))));
						}
					}
				}
				for (FactoryJsonObject jsonObject : saturationModifiers) {
					if (jsonObject.isPresent("value")) {
						Double val = jsonObject.getNumber("value").getDouble();
						String operation = jsonObject.getString("operation");
						BinaryOperator mathOperator = Util.getOperationMappingsDouble().get(operation);
						if (mathOperator != null && CraftItemStack.asNMSCopy(e.getItem()).get(DataComponents.FOOD) != null) {
							double finalValue = (double) mathOperator.apply(Float.valueOf(CraftItemStack.asNMSCopy(e.getItem()).get(DataComponents.FOOD).saturation()).doubleValue(), val);
							finalValue = finalValue - Float.valueOf(CraftItemStack.asNMSCopy(e.getItem()).get(DataComponents.FOOD).saturation()).doubleValue();
							player.setSaturation(Math.round(player.getSaturation() + finalValue));
						}
					}
				}

				if (replaceStack != null) {
					e.setReplacement(replaceStack);
				}
				Actions.executeEntity(player, entityAction);
				Actions.executeItem(e.getItem(), e.getPlayer().getWorld(), itemAction);

				if (shouldPreventEffects && !e.isCancelled()) {
					preventEffects.add(player);
					new BukkitRunnable() {
						@Override
						public void run() {
							preventEffects.remove(player);
						}
					}.runTaskLater(OriginsPaper.getPlugin(), 1);
				}
			}
		}
	}

	@EventHandler
	public void effectAddEvent(@NotNull EntityPotionEffectEvent e) {
		if (e.getEntity() instanceof Player p && e.getCause().equals(EntityPotionEffectEvent.Cause.FOOD) && preventEffects.contains(p)) {
			e.setCancelled(true);
			e.setOverride(false);
		}
	}

}
