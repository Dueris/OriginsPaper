package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import io.github.dueris.originspaper.data.types.modifier.ModifierUtil;
import io.github.dueris.originspaper.power.type.ModifyEnchantmentLevelPower;
import it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ItemEnchantments.class)
public class ItemEnchantmentsMixin {
	public static Map<ItemEnchantments, ModifyEnchantmentLevelPower> TO_MODIFY = new ConcurrentHashMap<>();

	public static void mark(ItemEnchantments itemEnchantments, ModifyEnchantmentLevelPower power) {
		TO_MODIFY.put(itemEnchantments, power);
	}

	public static void unmark(ItemEnchantments itemEnchantments) {
		TO_MODIFY.remove(itemEnchantments);
	}

	@Inject(method = "getLevel", locator = At.Value.RETURN)
	@SuppressWarnings("unchecked")
	public static void apoli$modifyEnchantmentLevel(ItemEnchantments instance, Holder<Enchantment> enchantment, CallbackInfo info) {
		Object2IntAVLTreeMap<Holder<Enchantment>> instanceEnchantments;
		try {
			Field field = ItemEnchantments.class.getDeclaredField("enchantments");
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			instanceEnchantments = (Object2IntAVLTreeMap<Holder<Enchantment>>) field.get(instance);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		int original = instanceEnchantments.getInt(enchantment);
		if (TO_MODIFY.containsKey(instance)) {
			ModifyEnchantmentLevelPower power = TO_MODIFY.get(instance);
			if (enchantment.is(power.getEnchantment())) {
				info.setReturned(true);
				info.setReturnValue((int) Math.round(ModifierUtil.applyModifiers(null, power.getModifiers(), original)));
			}
		}
	}
}
