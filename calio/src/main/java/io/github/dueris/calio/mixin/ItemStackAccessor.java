package io.github.dueris.calio.mixin;

import com.mojang.serialization.DataResult;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemStack.class)
public interface ItemStackAccessor {

	@Invoker
	static DataResult<ItemStack> callValidateStrict(ItemStack stack) {
		throw new AssertionError();
	}

	@Accessor("count")
	int getCountOverride();
}
