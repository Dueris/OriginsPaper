package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.util.Utils;
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
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("modify_food"))
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
	public void saturationorwhateverRUN(PlayerItemConsumeEvent e) {
		Player player = e.getPlayer();
		if (getPlayers().contains(player)) {
			if (isActive(player) && ConditionExecutor.testItem(itemCondition, e.getItem())) {
				for (FactoryJsonObject jsonObject : foodModifiers) {
					if (jsonObject.isPresent("value")) {
						int val = jsonObject.getNumber("value").getInt();
						String operation = jsonObject.getString("operation");
						BinaryOperator mathOperator = Utils.getOperationMappingsDouble().get(operation);
						if (mathOperator != null && CraftItemStack.asNMSCopy(e.getItem()).get(DataComponents.FOOD) != null) {
							double finalValue = (double) mathOperator.apply(CraftItemStack.asNMSCopy(e.getItem()).get(DataComponents.FOOD).nutrition(), (double) val);
							player.setFoodLevel(Integer.parseInt(String.valueOf(Math.round(player.getFoodLevel() + finalValue))));
						}
					}
				}
				for (FactoryJsonObject jsonObject : saturationModifiers) {
					if (jsonObject.isPresent("value")) {
						int val = jsonObject.getNumber("value").getInt();
						String operation = jsonObject.getString("operation");
						BinaryOperator mathOperator = Utils.getOperationMappingsDouble().get(operation);
						if (mathOperator != null && CraftItemStack.asNMSCopy(e.getItem()).get(DataComponents.FOOD) != null) {
							double finalValue = (double) mathOperator.apply(CraftItemStack.asNMSCopy(e.getItem()).get(DataComponents.FOOD).saturation(), (double) val);
							player.setSaturation(Math.round(player.getFoodLevel() + finalValue));
						}
					}
				}

				if (replaceStack != null) {
					e.setReplacement(replaceStack);
				}
				Actions.executeEntity(player, entityAction);
				Actions.executeItem(e.getItem(), itemAction);

				if (shouldPreventEffects && !e.isCancelled()) {
					preventEffects.add(player);
					new BukkitRunnable() {
						@Override
						public void run() {
							preventEffects.remove(player);
						}
					}.runTaskLater(GenesisMC.getPlugin(), 1);
				}
			}
		}
	}

	@EventHandler
	public void effectAddEvent(EntityPotionEffectEvent e) {
		if (e.getEntity() instanceof Player p && e.getCause().equals(EntityPotionEffectEvent.Cause.FOOD) && preventEffects.contains(p)) {
			e.setCancelled(true);
			e.setOverride(false);
		}
	}

}
