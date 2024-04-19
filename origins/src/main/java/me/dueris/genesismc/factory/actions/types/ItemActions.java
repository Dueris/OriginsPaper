package me.dueris.genesismc.factory.actions.types;

import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.calio.util.MiscUtils;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.registry.Registries;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

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
        register(new ActionFactory(GenesisMC.apoliIdentifier("merge_nbt"), (action, item) -> {
            net.minecraft.world.item.ItemStack stack = CraftItemStack.unwrap(item);
            stack.getOrCreateTag().merge(MiscUtils.ParserUtils.parseJson(new com.mojang.brigadier.StringReader(action.getString("nbt")), CompoundTag.CODEC));
        }));
    }

    private void register(ItemActions.ActionFactory factory) {
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
        public NamespacedKey getKey() {
            return key;
        }
    }
}
