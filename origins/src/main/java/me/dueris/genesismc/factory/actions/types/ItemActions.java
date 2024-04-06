package me.dueris.genesismc.factory.actions.types;

import me.dueris.calio.registry.Registerable;
import me.dueris.calio.util.MiscUtils;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.registry.Registries;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.function.BiConsumer;

public class ItemActions {

    public void register() {
        register(new ActionFactory(GenesisMC.apoliIdentifier("damage"), (action, item) -> item.setDurability((short) (item.getDurability() + Short.parseShort(action.get("amount").toString())))));
        register(new ActionFactory(GenesisMC.apoliIdentifier("consume"), (action, item) -> {
            int amount = action.get("amount") == null ? 1 : action.get("amount") instanceof Long ? Math.toIntExact((long) action.get("amount")) : (int) action.get("amount");
            item.setAmount(item.getAmount() - amount);
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("remove_enchantment"), (action, item) -> {
            Enchantment enchantment = Enchantment.getByKey(new NamespacedKey(action.get("enchantment").toString().split(":")[0], action.get("enchantment").toString().split(":")[1]));
            if (item.containsEnchantment(enchantment)) {
                item.removeEnchantment(enchantment);
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("merge_nbt"), (action, item) -> {
            net.minecraft.world.item.ItemStack stack = CraftItemStack.unwrap(item);
            stack.getOrCreateTag().merge(MiscUtils.ParserUtils.parseJson(new com.mojang.brigadier.StringReader(action.get("nbt").toString()), CompoundTag.CODEC));
        }));
    }

    private void register(ItemActions.ActionFactory factory) {
        GenesisMC.getPlugin().registry.retrieve(Registries.ITEM_ACTION).register(factory);
    }

    public static class ActionFactory implements Registerable {
        NamespacedKey key;
        BiConsumer<JSONObject, ItemStack> test;

        public ActionFactory(NamespacedKey key, BiConsumer<JSONObject, ItemStack> test) {
            this.key = key;
            this.test = test;
        }

        public void test(JSONObject action, ItemStack tester) {
            if (action == null || action.isEmpty()) return; // Dont execute empty actions
            try {
                test.accept(action, tester);
            } catch (Exception e) {
                GenesisMC.getPlugin().getLogger().severe("An Error occurred while running an action: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public NamespacedKey getKey() {
            return key;
        }
    }
}
