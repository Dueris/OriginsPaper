package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.builder.inst.factory.FactoryElement;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.core.component.DataComponents;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.function.BinaryOperator;

public class ModifyFoodPower extends CraftPower implements Listener {
	public static ArrayList<Player> preventEffects = new ArrayList<>();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void saturationorwhateverRUN(PlayerItemConsumeEvent e) {
		Player player = e.getPlayer();
		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			if (modify_food.contains(player)) {
				for (Power power : OriginPlayerAccessor.getPowers(player, getType(), layer)) {
					if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) player) && ConditionExecutor.testItem(power.getJsonObject("item_condition"), e.getItem())) {
						if (power.isPresent("food_modifier")) {
							for (FactoryJsonObject jsonObject : power.getList$SingularPlural("food_modifier", "food_modifiers").stream().map(FactoryElement::toJsonObject).toList()) {
								if (jsonObject.isPresent("value")) {
									int val = jsonObject.getNumber("value").getInt();
									String operation = jsonObject.getString("operation");
									BinaryOperator mathOperator = Utils.getOperationMappingsDouble().get(operation);
									if (mathOperator != null && CraftItemStack.asNMSCopy(e.getItem()).get(DataComponents.FOOD) != null) {
										double finalValue = (double) mathOperator.apply(CraftItemStack.asNMSCopy(e.getItem()).get(DataComponents.FOOD).nutrition(), (double) val);
										player.setFoodLevel(Integer.parseInt(String.valueOf(Math.round(player.getFoodLevel() + finalValue))));
										setActive(player, power.getTag(), true);
									}
								}
							}
						}
						if (power.isPresent("saturation_modifier")) {
							for (FactoryJsonObject jsonObject : power.getList$SingularPlural("saturation_modifier", "saturation_modifiers").stream().map(FactoryElement::toJsonObject).toList()) {
								if (jsonObject.isPresent("value")) {
									int val = jsonObject.getNumber("value").getInt();
									String operation = jsonObject.getString("operation");
									BinaryOperator mathOperator = Utils.getOperationMappingsDouble().get(operation);
									if (mathOperator != null && CraftItemStack.asNMSCopy(e.getItem()).get(DataComponents.FOOD) != null) {
										double finalValue = (double) mathOperator.apply(CraftItemStack.asNMSCopy(e.getItem()).get(DataComponents.FOOD).saturation(), (double) val);
										player.setSaturation(Math.round(player.getFoodLevel() + finalValue));
										setActive(player, power.getTag(), true);
									}
								}
							}
						}

						if (power.isPresent("replace_stack")) {
							e.setReplacement(power.getItemStack("result_stack"));
						}
						Actions.executeEntity(player, power.getJsonObject("entity_action"));
						Actions.executeItem(e.getItem(), power.getJsonObject("item_action"));

						if (power.getBooleanOrDefault("prevent_effects", false) && !e.isCancelled()) {
							preventEffects.add(player);
							new BukkitRunnable() {
								@Override
								public void run() {
									preventEffects.remove(player);
								}
							}.runTaskLater(GenesisMC.getPlugin(), 1);
						}
					} else {
						setActive(player, power.getTag(), false);
					}
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

	@Override
	public String getType() {
		return "apoli:modify_food";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return modify_food;
	}
}
