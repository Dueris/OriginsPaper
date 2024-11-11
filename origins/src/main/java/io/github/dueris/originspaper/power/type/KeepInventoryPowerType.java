package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.condition.ItemCondition;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KeepInventoryPowerType extends PowerType implements Listener {

	private static final ObjectOpenHashSet<Integer> DEFAULT_SLOTS = ObjectOpenHashSet.of(SlotRanges.nameToIds("inventory.*"), SlotRanges.nameToIds("hotbar.*"), SlotRanges.nameToIds("armor.*"))
		.stream()
		.map(SlotRange::slots)
		.map(IntCollection::intStream)
		.flatMap(IntStream::boxed)
		.collect(Collectors.toCollection(ObjectOpenHashSet::new));

	public static final TypedDataObjectFactory<KeepInventoryPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("item_condition", ItemCondition.DATA_TYPE.optional(), Optional.empty())
			.add("slots", ApoliDataTypes.SINGLE_SLOT_RANGES.optional(), Optional.empty()),
		(data, condition) -> new KeepInventoryPowerType(
			data.get("item_condition"),
			data.get("slots"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("item_condition", powerType.itemCondition)
			.set("slots", powerType.slotRanges)
	);

	private final Optional<ItemCondition> itemCondition;
	private final Optional<List<SlotRange>> slotRanges;

	private final ObjectOpenHashSet<Integer> slots;
	private final Int2ObjectOpenHashMap<ItemStack> cachedStacks;

	public KeepInventoryPowerType(Optional<ItemCondition> itemCondition, Optional<List<SlotRange>> slotRanges, Optional<EntityCondition> condition) {
		super(condition);

		this.itemCondition = itemCondition;
		this.slotRanges = slotRanges;

		this.slots = new ObjectOpenHashSet<>();
		this.cachedStacks = new Int2ObjectOpenHashMap<>();

		this.slotRanges.stream()
			.flatMap(Collection::stream)
			.map(SlotRange::slots)
			.map(IntCollection::intStream)
			.flatMap(IntStream::boxed)
			.forEach(this.slots::add);

		this.slots.trim();

	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.KEEP_INVENTORY;
	}

	@EventHandler
	public void onDeath(@NotNull PlayerDeathEvent e) {
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		ObjectOpenHashSet<Integer> slots = slotRanges
			.map(slotRanges -> this.slots)
			.orElse(DEFAULT_SLOTS);

		if (getHolder() == player && isActive()) {
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
					if (!(itemCondition.isEmpty() || itemCondition.get().test(player.level(), stack))) {
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
