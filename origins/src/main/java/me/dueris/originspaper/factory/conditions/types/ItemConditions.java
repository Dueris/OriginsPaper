package me.dueris.originspaper.factory.conditions.types;

import me.dueris.calio.data.CalioDataTypes;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.content.OrbOfOrigins;
import me.dueris.originspaper.factory.data.types.Comparison;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class ItemConditions {

	public void registerConditions() {
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("food"), (data, itemStack) -> {
			return CraftItemStack.asNMSCopy(itemStack).has(DataComponents.FOOD);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("smeltable"), (data, itemStack) -> {
			return OriginsPaper.server.getLevel(Level.OVERWORLD).getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(CraftItemStack.asNMSCopy(itemStack)), OriginsPaper.server.getLevel(Level.OVERWORLD)).isPresent();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("ingredient"), (data, itemStack) -> {
			Ingredient ingredient = data.transformWithCalio("ingredient", CalioDataTypes::ingredient);
			return ingredient.test(CraftItemStack.asNMSCopy(itemStack));
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("armor_value"), (data, itemStack) -> {
			return CraftItemStack.asNMSCopy(itemStack).getItem() instanceof ArmorItem armorItem && Comparison.fromString(data.getString("comparison")).compare(
				armorItem.getDefense(), data.getNumber("compare_to").getInt()
			);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("enchantment"), (data, itemStack) -> {
			ResourceKey<Enchantment> enchantmentKey = data.resourceKey("enchantment", net.minecraft.core.registries.Registries.ENCHANTMENT);
			Holder<Enchantment> enchantment = enchantmentKey == null ? null : MinecraftServer.getServer().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
				.getHolder(enchantmentKey)
				.orElseThrow();

			Comparison comparison = Comparison.fromString(data.getString("comparison"));
			int compareTo = data.getNumberOrDefault("compare_to", 0).getInt();

			ItemEnchantments component = CraftItemStack.asNMSCopy(itemStack).getEnchantments();
			int level = enchantment != null ? component.getLevel(enchantment)
				: component.keySet().size();
			return comparison.compare(level, compareTo);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("meat"), (data, itemStack) -> {
			return CraftItemStack.asNMSCopy(itemStack).is(ItemTags.MEAT);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("nbt"), (data, itemStack) -> { // namespaced alias for custom_data defined in Bootstrap
			return CraftItemStack.asNMSCopy(itemStack).getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).matchedBy(data.transformWithCalio("nbt", CalioDataTypes::compoundTag));
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("fire_resistant"), (data, itemStack) -> {
			return CraftItemStack.asNMSCopy(itemStack).has(DataComponents.FIRE_RESISTANT);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("enchantable"), (data, itemStack) -> {
			return CraftItemStack.asNMSCopy(itemStack).isEnchantable();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("power_count"), (data, itemStack) -> {
			return Comparison.fromString(data.getString("comparison")).compare(0, data.getNumber("compare_to").getInt());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("has_power"), (data, itemStack) -> {
			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("empty"), (data, itemStack) -> {
			return CraftItemStack.asNMSCopy(itemStack).isEmpty();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("amount"), (data, itemStack) -> {
			return Comparison.fromString(data.getString("comparison")).compare(CraftItemStack.asNMSCopy(itemStack).getCount(), data.getNumber("compare_to").getInt());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("damageable"), (data, itemStack) -> {
			return CraftItemStack.asNMSCopy(itemStack).isDamageableItem();
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("durability"), (data, itemStack) -> {
			net.minecraft.world.item.ItemStack stack = CraftItemStack.asNMSCopy(itemStack);
			return Comparison.fromString(data.getString("comparison")).compare(
				stack.getMaxDamage() - stack.getDamageValue(),
				data.getNumber("compare_to").getInt()
			);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("relative_durability"), (data, itemStack) -> {
			net.minecraft.world.item.ItemStack stack = CraftItemStack.asNMSCopy(itemStack);
			return Comparison.fromString(data.getString("comparison")).compare(
				(float) Math.abs(stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage(),
				data.getNumber("compare_to").getFloat()
			);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("equippable"), (data, itemStack) -> {
			net.minecraft.world.item.ItemStack stack = CraftItemStack.asNMSCopy(itemStack);
			Equipable equipment = Equipable.get(stack);

			net.minecraft.world.entity.EquipmentSlot equipmentSlot = data.getEnumValue("equipment_slot", net.minecraft.world.entity.EquipmentSlot.class);
			return (equipmentSlot == null && equipment != null) || (equipment != null && equipment.getEquipmentSlot() == equipmentSlot);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("fuel"), (data, itemStack) -> {
			return Comparison.fromString(data.getStringOrDefault("comparison", ">")).compare(
				AbstractFurnaceBlockEntity.getFuel().get(CraftItemStack.asNMSCopy(itemStack).getItem()),
				data.getNumberOrDefault("compare_to", 0).getInt()
			);
		}));
	}

	public void register(ConditionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.ITEM_CONDITION).register(factory);
	}

	public class ConditionFactory implements Registrable {
		ResourceLocation key;
		BiPredicate<FactoryJsonObject, ItemStack> test;

		public ConditionFactory(ResourceLocation key, BiPredicate<FactoryJsonObject, ItemStack> test) {
			this.key = key;
			this.test = test;
		}

		public boolean test(FactoryJsonObject condition, ItemStack tester) {
			return test.test(condition, tester);
		}

		@Override
		public ResourceLocation key() {
			return key;
		}
	}

}
