package io.github.dueris.originspaper.condition.factory;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.type.item.*;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.registry.Registries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class ItemConditions {

	public static void register() {
		MetaConditions.register(ApoliDataTypes.ITEM_CONDITION, ItemConditions::register);
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("food"), FoodConditionType::condition));
		register(SmeltableConditionType.getFactory());
		register(IngredientConditionType.getFactory());
		register(ArmorValueConditionType.getFactory());
		register(EnchantmentConditionType.getFactory());
		register(CustomDataConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("fire_resistant"), (stack) -> {
			return stack.has(DataComponents.FIRE_RESISTANT);
		}));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("enchantable"), ItemStack::isEnchantable));
		register(PowerCountConditionType.getFactory());
		register(HasPowerConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("empty"), ItemStack::isEmpty));
		register(AmountConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("damageable"), ItemStack::isDamageableItem));
		register(DurabilityConditionType.getFactory());
		register(RelativeDurabilityConditionType.getFactory());
		register(EquippableConditionType.getFactory());
		register(FuelConditionType.getFactory());
	}

	public static ConditionTypeFactory<Tuple<Level, ItemStack>> createSimpleFactory(ResourceLocation id, Predicate<ItemStack> predicate) {
		return new ConditionTypeFactory<>(id, new SerializableData(), (data, worldAndStack) -> {
			return predicate.test(worldAndStack.getB());
		});
	}

	public static ConditionTypeFactory<Tuple<Level, ItemStack>> register(ConditionTypeFactory<Tuple<Level, ItemStack>> conditionFactory) {
		return OriginsPaper.getRegistry().retrieve(Registries.ITEM_CONDITION).register(conditionFactory, conditionFactory.getSerializerId());
	}
}
