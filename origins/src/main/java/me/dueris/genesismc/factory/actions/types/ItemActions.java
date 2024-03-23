package me.dueris.genesismc.factory.actions.types;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import me.dueris.calio.util.MiscUtils;
import net.minecraft.nbt.CompoundTag;

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
		if (type.equals("apoli:merge_nbt")) {
			net.minecraft.world.item.ItemStack stack = CraftItemStack.unwrap(item);
			stack.getOrCreateTag().merge(MiscUtils.ParserUtils.parseJson(new com.mojang.brigadier.StringReader(action.get("nbt").toString()), CompoundTag.CODEC));
		}
	}
}
