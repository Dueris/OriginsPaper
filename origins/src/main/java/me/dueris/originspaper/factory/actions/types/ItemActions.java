package me.dueris.originspaper.factory.actions.types;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.registry.Registries;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class ItemActions {

	public void register() {

	}

	public void register(ItemActions.ActionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.ITEM_ACTION).register(factory);
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
				OriginsPaper.getPlugin().getLogger().severe("An Error occurred while running an action: " + e.getMessage());
				e.printStackTrace();
			}
		}

		@Override
		public NamespacedKey key() {
			return key;
		}
	}
}
