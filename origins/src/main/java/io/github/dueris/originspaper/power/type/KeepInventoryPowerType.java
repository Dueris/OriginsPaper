package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class KeepInventoryPowerType extends PowerType implements Listener {

	private final Predicate<Tuple<Level, ItemStack>> keepItemCondition;
	private final Set<Integer> slots;

	public KeepInventoryPowerType(Power power, LivingEntity entity, Predicate<Tuple<Level, ItemStack>> keepItemCondition, Collection<Integer> slots) {
		super(power, entity);
		this.keepItemCondition = keepItemCondition;
		if (slots == null) {
			this.slots = null;
		} else {
			this.slots = new HashSet<>(slots);
		}
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("keep_inventory"),
			new SerializableData()
				.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
				.add("slots", SerializableDataTypes.INTS, null),
			data -> (power, entity) -> new KeepInventoryPowerType(power, entity,
				data.get("item_condition"),
				data.get("slots")
			)
		).allowCondition();
	}

	@EventHandler
	public void onDeath(@NotNull PlayerDeathEvent e) {
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (entity == player && isActive()) {
			e.setKeepInventory(true);
			int containerSize = e.getPlayer().getInventory().getSize();
			ItemStack[] toDrop = new ItemStack[containerSize];
			org.bukkit.inventory.ItemStack[] savedInventory = new org.bukkit.inventory.ItemStack[containerSize];
			for (int i = 0; i < containerSize; i++) {
				org.bukkit.inventory.ItemStack bukkit = e.getPlayer().getInventory().getItem(i);
				if (bukkit == null) continue;
				ItemStack stack = CraftItemStack.unwrap(bukkit);
				if (slots != null && !slots.contains(i)) {
					toDrop[i] = stack;
					continue;
				}
				if (!stack.isEmpty()) {
					if (!(keepItemCondition == null || keepItemCondition.test(new Tuple<>(player.level(), stack)))) {
						toDrop[i] = stack;
					} else {
						savedInventory[i] = bukkit;
					}
				}
			}

			e.getDrops().clear();
			for (ItemStack stack : toDrop) {
				if (stack == null) continue;
				player.drop(stack, true, false);
				e.getDrops().add(stack.getBukkitStack());
			}
			e.getPlayer().getInventory().setContents(savedInventory);
		}
	}
}
