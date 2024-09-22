package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.dueris.originspaper.data.types.modifier.ModifierUtil;
import io.github.dueris.originspaper.power.type.ModifyEnchantmentLevelPower;
import it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import static io.github.dueris.originspaper.power.type.ModifyEnchantmentLevelPower.TO_MODIFY;

@Mixin(ItemEnchantments.class)
public class ItemEnchantmentsMixin {

	@Shadow
	@Final
	private Object2IntAVLTreeMap<Holder<Enchantment>> enchantments;

	@ModifyReturnValue(method = "getLevel", at = @At("RETURN"))
	private int apoli$modifyEnchantmentLevel(int original, Holder<Enchantment> enchantmentHolder) {
		ItemEnchantments instance = (ItemEnchantments) (Object) this;

		if (TO_MODIFY.containsKey(instance)) {
			ModifyEnchantmentLevelPower power = TO_MODIFY.get(instance);
			if (enchantmentHolder.is(power.getEnchantment())) {
				return (int) Math.round(ModifierUtil.applyModifiers(null, power.getModifiers(), original));
			}
		}
		return original;
	}
}
