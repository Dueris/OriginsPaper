package io.github.dueris.originspaper.access;

import io.github.dueris.originspaper.power.type.ModifyFoodPowerType;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ModifiableFoodEntity {

	ItemStack apoli$getOriginalFoodStack();

	void apoli$setOriginalFoodStack(ItemStack original);

	List<ModifyFoodPowerType> apoli$getCurrentModifyFoodPowers();

	void apoli$setCurrentModifyFoodPowers(List<ModifyFoodPowerType> powers);

}
