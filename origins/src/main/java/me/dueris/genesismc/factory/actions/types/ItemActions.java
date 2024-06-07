package me.dueris.genesismc.factory.actions.types;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.util.EntityLinkedItemStack;
import me.dueris.genesismc.util.Reflector;
import me.dueris.genesismc.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemCooldowns.CooldownInstance;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.BiConsumer;

public class ItemActions {

	public void register() {
		register(new ActionFactory(GenesisMC.apoliIdentifier("damage"), (action, item) -> item.setDurability((short) (item.getDurability() + action.getNumber("amount").getFloat()))));
		register(new ActionFactory(GenesisMC.apoliIdentifier("consume"), (action, item) -> {
			int amount = !action.isPresent("amount") ? 1 : action.getNumber("amount").getInt();
			item.setAmount(item.getAmount() - amount);
		}));
		register(new ActionFactory(GenesisMC.apoliIdentifier("remove_enchantment"), (action, item) -> {
			Enchantment enchantment = Enchantment.getByKey(new NamespacedKey(action.getString("enchantment").split(":")[0], action.getString("enchantment").split(":")[1]));
			if (item.containsEnchantment(enchantment)) {
				item.removeEnchantment(enchantment);
			}
		}));
		register(new ActionFactory(GenesisMC.apoliIdentifier("modify_item_cooldown"), (action, item) -> {
			net.minecraft.world.item.ItemStack nms = CraftItemStack.asNMSCopy(item);
			Entity entity = ((CraftEntity) EntityLinkedItemStack.getInstance().getHolder(item)).getHandle();

			if (nms.isEmpty() || !(entity instanceof Player player)) return;
			Item i = nms.getItem();
			ItemCooldowns cooldowns = player.getCooldowns();
			Map<Item, CooldownInstance> instanceMap = cooldowns.cooldowns;
			CooldownInstance cooldownEntry = instanceMap.get(i);
			int duration = cooldownEntry != null ? cooldownEntry.endTime - Reflector.accessField("startTime", CooldownInstance.class, cooldownEntry, int.class) : 0;

			for (Modifier modifier : Modifier.getModifiers(action.getJsonObject("modifier"), action.getJsonArray("modifiers"))) {
				duration = Util.getOperationMappingsInteger().get(modifier.operation()).apply(duration, modifier.value().intValue());
			}
			cooldowns.addCooldown(i, duration);
		}));
	}

	public void register(ItemActions.ActionFactory factory) {
		GenesisMC.getPlugin().registry.retrieve(Registries.ITEM_ACTION).register(factory);
	}

	public static class ActionFactory implements Registrable {
		NamespacedKey key;
		BiConsumer<FactoryJsonObject, ItemStack> test;

		public ActionFactory(NamespacedKey key, BiConsumer<FactoryJsonObject, ItemStack> test) {
			this.key = key;
			this.test = test;
		}

		public void test(FactoryJsonObject action, ItemStack tester) {
			if (action == null || action.isEmpty()) return; // Dont execute empty actions
			try {
				test.accept(action, tester);
			} catch (Exception e) {
				GenesisMC.getPlugin().getLogger().severe("An Error occurred while running an action: " + e.getMessage());
				e.printStackTrace();
			}
		}

		@Override
		public NamespacedKey key() {
			return key;
		}
	}
}
