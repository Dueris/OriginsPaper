package io.github.dueris.originspaper.condition.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.condition.type.item.*;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

public class ItemConditionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static void register() {
		MetaConditionTypes.register(ApoliDataTypes.ITEM_CONDITION, ItemConditionTypes::register);
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("food"), FoodConditionType::condition));
		register(SmeltableConditionType.getFactory());
		register(IngredientConditionType.getFactory());
		register(ArmorValueConditionType.getFactory());
		register(EnchantmentConditionType.getFactory());
		register(CustomDataConditionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("fire_resistant"), stack -> stack.has(DataComponents.FIRE_RESISTANT)));
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
		register(ItemCooldownConditionType.getFactory());
		register(RelativeItemCooldownConditionType.getFactory());
	}

	public static ConditionTypeFactory<Tuple<Level, ItemStack>> createSimpleFactory(ResourceLocation id, Predicate<ItemStack> predicate) {
		return new ConditionTypeFactory<>(id, new SerializableData(), (data, worldAndStack) -> predicate.test(worldAndStack.getB()));
	}

	public static <F extends ConditionTypeFactory<Tuple<Level, ItemStack>>> F register(F conditionFactory) {
		return Registry.register(ApoliRegistries.ITEM_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
	}

}
