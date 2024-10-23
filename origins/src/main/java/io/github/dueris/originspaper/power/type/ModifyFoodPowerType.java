package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.util.modifier.Modifier;
import io.github.dueris.originspaper.util.modifier.ModifierUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Predicate;

// TODO - improve impl for full server-sided operations
public class ModifyFoodPowerType extends PowerType {

	private final Predicate<Tuple<Level, ItemStack>> itemCondition;
	private final ItemStack replaceStack;
	private final Consumer<Tuple<Level, SlotAccess>> itemAction;
	private final List<Modifier> foodModifiers;
	private final List<Modifier> saturationModifiers;
	private final List<Modifier> eatTicksModifiers;
	private final Consumer<Entity> entityActionWhenEaten;
	private final boolean preventFoodEffects;
	private final boolean makeAlwaysEdible;

	public ModifyFoodPowerType(Power power, LivingEntity entity, Predicate<Tuple<Level, ItemStack>> itemCondition, ItemStack replaceStack, Consumer<Tuple<Level, SlotAccess>> itemAction, Modifier foodModifier, List<Modifier> foodModifiers, Modifier saturationModifier, List<Modifier> saturationModifiers, Modifier eatSecondsModifier, List<Modifier> eatTicksModifiers, Consumer<Entity> entityActionWhenEaten, boolean makeAlwaysEdible, boolean preventFoodEffects) {

		super(power, entity);

		this.itemCondition = itemCondition;
		this.replaceStack = replaceStack;
		this.itemAction = itemAction;

		this.foodModifiers = new LinkedList<>();

		if (foodModifier != null) {
			this.foodModifiers.add(foodModifier);
		}

		if (foodModifiers != null) {
			this.foodModifiers.addAll(foodModifiers);
		}

		this.saturationModifiers = new LinkedList<>();

		if (saturationModifier != null) {
			this.saturationModifiers.add(saturationModifier);
		}

		if (saturationModifiers != null) {
			this.saturationModifiers.addAll(saturationModifiers);
		}

		this.eatTicksModifiers = new LinkedList<>();

		if (eatSecondsModifier != null) {
			this.eatTicksModifiers.add(eatSecondsModifier);
		}

		if (eatTicksModifiers != null) {
			this.eatTicksModifiers.addAll(eatTicksModifiers);
		}

		this.entityActionWhenEaten = entityActionWhenEaten;
		this.makeAlwaysEdible = makeAlwaysEdible;
		this.preventFoodEffects = preventFoodEffects;

	}

	public static OptionalInt modifyEatTicks(@Nullable Entity entity, ItemStack stack) {

		FoodProperties foodComponent = EdibleItemPowerType.get(stack)
			.map(EdibleItemPowerType::getFoodComponent)
			.orElseGet(() -> stack.get(DataComponents.FOOD));

		if (foodComponent == null) {
			return OptionalInt.empty();
		}

		List<Modifier> modifiers = PowerHolderComponent.getPowerTypes(entity, ModifyFoodPowerType.class)
			.stream()
			.filter(p -> p.doesApply(stack))
			.flatMap(p -> p.getEatTicksModifiers().stream())
			.toList();

		return OptionalInt.of((int) ModifierUtil.applyModifiers(entity, modifiers, foodComponent.eatDurationTicks()));

	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(OriginsPaper.apoliIdentifier("modify_food"),
			new SerializableData()
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("replace_stack", SerializableDataTypes.ITEM_STACK, null)
				.add("item_action", ApoliDataTypes.ITEM_ACTION, null)
				.add("food_modifier", Modifier.DATA_TYPE, null)
				.add("food_modifiers", Modifier.LIST_TYPE, null)
				.add("saturation_modifier", Modifier.DATA_TYPE, null)
				.add("saturation_modifiers", Modifier.LIST_TYPE, null)
				.add("eat_ticks_modifier", Modifier.DATA_TYPE, null)
				.add("eat_ticks_modifiers", Modifier.LIST_TYPE, null)
				.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
				.add("always_edible", SerializableDataTypes.BOOLEAN, false)
				.add("prevent_effects", SerializableDataTypes.BOOLEAN, false),
			data -> (power, entity) -> new ModifyFoodPowerType(power, entity,
				data.get("item_condition"),
				data.get("replace_stack"),
				data.get("item_action"),
				data.get("food_modifier"),
				data.get("food_modifiers"),
				data.get("saturation_modifier"),
				data.get("saturation_modifiers"),
				data.get("eat_ticks_modifier"),
				data.get("eat_ticks_modifiers"),
				data.get("entity_action"),
				data.get("always_edible"),
				data.get("prevent_effects")
			)
		).allowCondition();
	}

	public boolean doesApply(ItemStack stack) {
		return itemCondition == null || itemCondition.test(new Tuple<>(entity.level(), stack));
	}

	public void setConsumedItemStackReference(SlotAccess stack) {

		if (replaceStack != null) {
			stack.set(this.replaceStack);
		}

		if (itemAction != null) {
			itemAction.accept(new Tuple<>(entity.level(), stack));
		}

	}

	public void eat() {
		if (entityActionWhenEaten != null) entityActionWhenEaten.accept(entity);
	}

	public List<Modifier> getFoodModifiers() {
		return foodModifiers;
	}

	public List<Modifier> getSaturationModifiers() {
		return saturationModifiers;
	}

	public List<Modifier> getEatTicksModifiers() {
		return eatTicksModifiers;
	}

	public boolean doesMakeAlwaysEdible() {
		return makeAlwaysEdible;
	}

	public boolean doesPreventEffects() {
		return preventFoodEffects;
	}
}
