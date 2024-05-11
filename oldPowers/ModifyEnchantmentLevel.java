package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.BinaryOperator;

public class ModifyEnchantmentLevel extends CraftPower {

	@Override
	public void run(Player p, Power power) {
		HashSet<ItemStack> items = new HashSet<>(Arrays.stream(p.getInventory().getArmorContents()).toList());
		items.add(p.getInventory().getItemInMainHand());
		for (ItemStack item : items) {
			if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) continue;
			if (!ConditionExecutor.testItem(power.getJsonObject("item_condition"), item)) continue;
			for (Modifier modifier : power.getModifiers()) {
				Enchantment enchant = Enchantment.getByKey(NamespacedKey.fromString(power.getString("enchantment")));
				if (item.containsEnchantment(enchant)) {
					item.removeEnchantment(enchant);
				}
				int result = 1;
				float value = modifier.value();
				String operation = modifier.operation();
				BinaryOperator mathOperator = Utils.getOperationMappingsInteger().get(operation);
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

	@Override
	public String getType() {
		return "apoli:modify_enchantment_level";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return modify_enchantment_level;
	}

}
