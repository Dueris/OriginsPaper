package me.dueris.genesismc.factory.actions.types;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

public class ItemActions {

	public static void runItem(ItemStack item, JSONObject action) {
		if (action == null || action.isEmpty()) return;
		String type = action.get("type").toString();
		if (type.equals("apoli:damage")) {
			item.setDurability((short) (item.getDurability() + Short.parseShort(action.get("amount").toString())));
		}
		if (type.equals("apoli:consume")) {
			item.setAmount(item.getAmount() - 1);
		}
		if (type.equals("apoli:remove_enchantment")) {
			Enchantment enchantment = Enchantment.getByKey(new NamespacedKey(action.get("enchantment").toString().split(":")[0], action.get("enchantment").toString().split(":")[1]));
			if (item.containsEnchantment(enchantment)) {
				item.removeEnchantment(enchantment);
			}
		}
	}
}
