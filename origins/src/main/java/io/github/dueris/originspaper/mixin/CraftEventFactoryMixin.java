package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.KeepInventoryPowerType;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.PluginManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static io.github.dueris.originspaper.power.type.KeepInventoryPowerType.DEFAULT_SLOTS;

@Mixin(CraftEventFactory.class)
public class CraftEventFactoryMixin {

	@WrapOperation(method = "callPlayerDeathEvent", at = @At(value = "INVOKE", target = "Lorg/bukkit/plugin/PluginManager;callEvent(Lorg/bukkit/event/Event;)V"))
	private static void apoli$keepInventoryPower(PluginManager instance, Event e, Operation<Void> original) {
		PlayerDeathEvent event = (PlayerDeathEvent) e;
		Player player = ((CraftPlayer) event.getPlayer()).getHandle();
		for (KeepInventoryPowerType powerType : PowerHolderComponent.getPowerTypes(player, KeepInventoryPowerType.class)) {
			ObjectOpenHashSet<Integer> slots = powerType.slotRanges
				.map(slotRanges -> powerType.slots)
				.orElse(DEFAULT_SLOTS);

			event.setKeepInventory(true);
			int containerSize = event.getPlayer().getInventory().getSize();
			ItemStack[] toDrop = new ItemStack[containerSize];
			org.bukkit.inventory.ItemStack[] savedInventory = new org.bukkit.inventory.ItemStack[containerSize];
			for (int i = 0; i < containerSize; i++) {
				org.bukkit.inventory.ItemStack bukkit = event.getPlayer().getInventory().getItem(i);
				if (bukkit == null) continue;
				ItemStack stack = CraftItemStack.unwrap(bukkit);
				if (slots != null && !slots.contains(i)) {
					toDrop[i] = stack;
					continue;
				}
				if (!stack.isEmpty()) {
					if (!(powerType.itemCondition.isEmpty() || powerType.itemCondition.get().test(player.level(), stack))) {
						toDrop[i] = stack;
					} else {
						savedInventory[i] = bukkit;
					}
				}
			}

			event.getDrops().clear();
			for (ItemStack stack : toDrop) {
				if (stack == null) continue;
				player.drop(stack, true, false);
				event.getDrops().add(stack.getBukkitStack());
			}
			event.getPlayer().getInventory().setContents(savedInventory);
		}

		original.call(instance, event);
	}
}
