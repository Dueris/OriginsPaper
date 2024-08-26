package io.github.dueris.originspaper.action;

import io.github.dueris.calio.parser.SerializableData;
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

	public static ItemActionTypeFactory createItemStackBased(ResourceLocation identifier, SerializableData data, @NotNull BiConsumer<SerializableData.Instance, Tuple<Level, ItemStack>> legacyEffect) {
		return new ItemActionTypeFactory(identifier, data, (data1, worldAndStackRef) -> legacyEffect.accept(data1, new Tuple<>(worldAndStackRef.getA(), worldAndStackRef.getB().get())));
	}

	@Override
	public void accept(Tuple<Level, SlotAccess> worldAndStackReference) {
		SlotAccess stackReference = worldAndStackReference.getB();
		if (stackReference == SlotAccess.NULL) {
			return;
		}
		ItemActionTypeFactory.this.effect.accept(this.deserializedFactory, worldAndStackReference);
	}

}