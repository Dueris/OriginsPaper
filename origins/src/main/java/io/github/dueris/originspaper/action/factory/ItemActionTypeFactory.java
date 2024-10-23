package io.github.dueris.originspaper.action.factory;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.power.type.ModifyEnchantmentLevelPowerType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class ItemActionTypeFactory extends ActionTypeFactory<Tuple<Level, SlotAccess>> {

	public ItemActionTypeFactory(ResourceLocation identifier, SerializableData data, @NotNull BiConsumer<SerializableData.Instance, Tuple<Level, SlotAccess>> effect) {
		super(identifier, data, effect);
	}

	public static @NotNull ItemActionTypeFactory createItemStackBased(ResourceLocation identifier, SerializableData data, @NotNull BiConsumer<SerializableData.Instance, Tuple<Level, ItemStack>> legacyEffect) {
		return new ItemActionTypeFactory(identifier, data, (data1, worldAndStackRef) -> legacyEffect.accept(data1, new Tuple<>(worldAndStackRef.getA(), worldAndStackRef.getB().get())));
	}

	@Override
	public ItemActionTypeFactory.Instance fromData(SerializableData.Instance data) {
		return new ItemActionTypeFactory.Instance(data);
	}

	public class Instance extends ActionTypeFactory<Tuple<Level, SlotAccess>>.Instance {

		protected Instance(SerializableData.Instance data) {
			super(data);
		}

		@Override
		public void accept(@NotNull Tuple<Level, SlotAccess> worldAndStackReference) {

			//  Skip empty stack references since they're practically immutable
			SlotAccess stackReference = worldAndStackReference.getB();
			if (stackReference == SlotAccess.NULL) {
				return;
			}

			//  Replace the stack of the stack reference with a "workable" empty stack if the said stack is NOT
			//  already "workable", and if the said stack is an instance of ItemStack#EMPTY
			if (stackReference.get() == ItemStack.EMPTY) {
				stackReference.set(new ItemStack((Void) null));
			}

			//  Execute the specified effect of the item action
			this.effect.accept(worldAndStackReference);

			//  Replace the stack of the stack reference with ItemStack#EMPTY if the said stack is NOT
			//  "workable", and if the said stack is empty
			if (!ModifyEnchantmentLevelPowerType.isWorkableEmptyStack(stackReference) && stackReference.get().isEmpty()) {
				stackReference.set(ItemStack.EMPTY);
			}
		}

	}

}
