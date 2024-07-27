package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.RequiredInstance;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.data.types.modifier.Modifier;
import me.dueris.originspaper.util.Util;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.BinaryOperator;

public class ModifyEnchantmentLevel extends ModifierPower {
	private final FactoryJsonObject itemCondition;
	private final ResourceLocation enchantment;

	public ModifyEnchantmentLevel(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject modifier, FactoryJsonArray modifiers, FactoryJsonObject itemCondition, ResourceLocation enchantment) {
		super(name, description, hidden, condition, loading_priority, modifier, modifiers);
		this.itemCondition = itemCondition;
		this.enchantment = enchantment;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return ModifierPower.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("modify_enchantment_level"))
			.add("item_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("enchantment", ResourceLocation.class, new RequiredInstance());
	}

	@Override
	public void tick(@NotNull Player p) {
		HashSet<ItemStack> items = new HashSet<>(Arrays.stream(p.getInventory().getArmorContents()).toList());
		items.add(p.getInventory().getItemInMainHand());
		for (ItemStack item : items) {
			if (!isActive(p)) continue;
			if (!ConditionExecutor.testItem(itemCondition, item)) continue;
			for (Modifier modifier : getModifiers()) {
				Enchantment enchant = Enchantment.getByKey(CraftNamespacedKey.fromMinecraft(enchantment));
				if (item.containsEnchantment(enchant)) {
					item.removeEnchantment(enchant);
				}
				int result = 1;
				float value = modifier.value();
				String operation = modifier.operation();
				BinaryOperator mathOperator = Util.getOperationMappingsInteger().get(operation);
				if (mathOperator != null) {
					result = Integer.valueOf(String.valueOf(mathOperator.apply(0, value)));
				}
				if (result < 0) {
					result = 1;
				}
				try {
					item.addEnchantment(enchant, result);
				} catch (Exception e) {
					// ignore. -- cannot apply enchant to itemstack
				}
			}
		}
	}

}
