package io.github.dueris.originspaper.condition.type;

import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.condition.type.item.*;
import io.github.dueris.originspaper.condition.type.item.meta.AllOfItemConditionType;
import io.github.dueris.originspaper.condition.type.item.meta.AnyOfItemConditionType;
import io.github.dueris.originspaper.condition.type.item.meta.ConstantItemConditionType;
import io.github.dueris.originspaper.condition.type.item.meta.RandomChanceItemConditionType;
import io.github.dueris.originspaper.condition.type.meta.AllOfMetaConditionType;
import io.github.dueris.originspaper.condition.type.meta.AnyOfMetaConditionType;
import io.github.dueris.originspaper.condition.type.meta.ConstantMetaConditionType;
import io.github.dueris.originspaper.condition.type.meta.RandomChanceMetaConditionType;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;

public class ItemConditionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();
	public static final SerializableDataType<ConditionConfiguration<ItemConditionType>> DATA_TYPE = SerializableDataType.registry(ApoliRegistries.ITEM_CONDITION_TYPE, "apoli", ALIASES, (configurations, id) -> "Item condition type \"" + id + "\" is undefined!");

	public static final ConditionConfiguration<AllOfItemConditionType> ALL_OF = register(AllOfMetaConditionType.createConfiguration(ItemCondition.DATA_TYPE, AllOfItemConditionType::new));
	public static final ConditionConfiguration<AnyOfItemConditionType> ANY_OF = register(AnyOfMetaConditionType.createConfiguration(ItemCondition.DATA_TYPE, AnyOfItemConditionType::new));
	public static final ConditionConfiguration<ConstantItemConditionType> CONSTANT = register(ConstantMetaConditionType.createConfiguration(ConstantItemConditionType::new));
	public static final ConditionConfiguration<RandomChanceItemConditionType> RANDOM_CHANCE = register(RandomChanceMetaConditionType.createConfiguration(RandomChanceItemConditionType::new));

	public static final ConditionConfiguration<AmountItemConditionType> AMOUNT = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("amount"), AmountItemConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<ArmorValueItemConditionType> ARMOR_VALUE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("armor_value"), ArmorValueItemConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<CustomDataItemConditionType> CUSTOM_DATA = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("custom_data"), CustomDataItemConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<DamageableItemConditionType> DAMAGEABLE = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("damageable"), DamageableItemConditionType::new));
	public static final ConditionConfiguration<DurabilityItemConditionType> DURABILITY = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("durability"), DurabilityItemConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<EmptyItemConditionType> EMPTY = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("empty"), EmptyItemConditionType::new));
	public static final ConditionConfiguration<EnchantableItemConditionType> ENCHANTABLE = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("enchantable"), EnchantableItemConditionType::new));
	public static final ConditionConfiguration<EnchantmentItemConditionType> ENCHANTMENT = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("enchantment"), EnchantmentItemConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<EquippableItemConditionType> EQUIPPABLE = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("equippable"), EquippableItemConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<FireResistantItemConditionType> FIRE_RESISTANT = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("fire_resistant"), FireResistantItemConditionType::new));
	public static final ConditionConfiguration<FoodItemConditionType> FOOD = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("food"), FoodItemConditionType::new));
	public static final ConditionConfiguration<FuelItemConditionType> FUEL = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("fuel"), FuelItemConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<HasPowerItemConditionType> HAS_POWER = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("has_power"), HasPowerItemConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<IngredientItemConditionType> INGREDIENT = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("ingredient"), IngredientItemConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<ItemCooldownItemConditionType> ITEM_COOLDOWN = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("item_cooldown"), ItemCooldownItemConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<PowerCountItemConditionType> POWER_COUNT = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("power_count"), PowerCountItemConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<RelativeDurabilityItemConditionType> RELATIVE_DURABILITY = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("relative_durability"), RelativeDurabilityItemConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<RelativeItemCooldownItemConditionType> RELATIVE_ITEM_COOLDOWN = register(ConditionConfiguration.of(OriginsPaper.apoliIdentifier("relative_item_cooldown"), RelativeItemCooldownItemConditionType.DATA_FACTORY));
	public static final ConditionConfiguration<SmeltableItemConditionType> SMELTABLE = register(ConditionConfiguration.simple(OriginsPaper.apoliIdentifier("smeltable"), SmeltableItemConditionType::new));

	public static void register() {

	}

	@SuppressWarnings("unchecked")
	public static <CT extends ItemConditionType> ConditionConfiguration<CT> register(ConditionConfiguration<CT> configuration) {

		ConditionConfiguration<ItemConditionType> casted = (ConditionConfiguration<ItemConditionType>) configuration;
		Registry.register(ApoliRegistries.ITEM_CONDITION_TYPE, casted.id(), casted);

		return configuration;

	}

}
