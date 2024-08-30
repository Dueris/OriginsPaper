package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftLootTable;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class ReplaceLootTablePower extends PowerType {
	private final Map<Pattern, ResourceLocation> replace;
	private final ConditionTypeFactory<Tuple<Entity, Entity>> bientityCondition;
	private final ConditionTypeFactory<BlockInWorld> blockCondition;
	private final ConditionTypeFactory<Tuple<Level, ItemStack>> itemCondition;

	public ReplaceLootTablePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								 Map<Pattern, ResourceLocation> replace, ConditionTypeFactory<Tuple<Entity, Entity>> bientityCondition, ConditionTypeFactory<BlockInWorld> blockCondition, ConditionTypeFactory<Tuple<Level, ItemStack>> itemCondition) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.replace = replace;
		this.bientityCondition = bientityCondition;
		this.blockCondition = blockCondition;
		this.itemCondition = itemCondition;
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("replace_loot_table"))
			.add("replace", ApoliDataTypes.REGEX_MAP)
			.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
			.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
			.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null);
	}

	@EventHandler
	public void inventoryPopulate(@NotNull LootGenerateEvent e) {
		if (e.getEntity() instanceof org.bukkit.entity.Player p && getPlayers().contains(((CraftPlayer) p).getHandle()) && isActive(((CraftPlayer) p).getHandle())) {
			e.setLoot(modifyLootTable(
				new LinkedList<>(e.getLoot().stream().map(CraftItemStack::unwrap).toList()),
				CraftNamespacedKey.toMinecraft(e.getLootTable().getKey()), ((CraftPlayer) p).getHandle().level(), e.getEntity().getLocation()).stream().map(ItemStack::getBukkitStack).toList());
		}
	}

	@EventHandler
	public void dropEvent(@NotNull EntityDeathEvent e) {
		if ((e.getEntity().getKiller() != null)) {
			Player p = e.getEntity().getKiller();
			String key = "minecraft:entities/" + e.getEntityType().getKey().getKey();
			NamespacedKey k = CraftNamespacedKey.fromMinecraft(ResourceLocation.parse(key));
			if (Bukkit.getLootTable(k) != null) {
				net.minecraft.world.entity.player.Player player = ((CraftPlayer) p).getHandle();
				Entity entity = ((CraftEntity) e.getEntity()).getHandle();
				if (getPlayers().contains(player) && isActive(player) && (bientityCondition == null || bientityCondition.test(new Tuple<>(player, entity)))) {
					modifyLootTable(new LinkedList<>(e.getDrops().stream().map(CraftItemStack::unwrap).toList()),
						CraftNamespacedKey.toMinecraft(k), player.level(), e.getEntity().getLocation());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void blockDropEvent(@NotNull BlockDropItemEvent e) {
		net.minecraft.world.entity.player.Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (getPlayers().contains(player)) {
			String formattedKey = "minecraft:blocks/" + e.getBlockState().getType().getKey().getKey();
			List<ItemStack> drops = new LinkedList<>(modifyLootTable(new LinkedList<>(e.getBlockState().getDrops().stream().map(CraftItemStack::unwrap).toList()),
				ResourceLocation.parse(formattedKey), player.level(), e.getBlock().getLocation()));
			if (!drops.isEmpty() && isActive(player) && (blockCondition == null || blockCondition.test(new BlockInWorld(player.level(), ((CraftBlock) e.getBlock()).getPosition(), false)))) {
				e.setCancelled(true);
				for (ItemStack drop : drops) {
					e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), drop.getBukkitStack());
				}
			}
		}
	}

	protected List<ItemStack> modifyLootTable(List<ItemStack> items, ResourceLocation table, Level world, Location origin) {
		for (Pattern pattern : replace.keySet()) {
			if (!pattern.matcher(table.toString()).matches()) continue;
			items.clear();
			ResourceLocation replaceWith = replace.get(pattern);
			NamespacedKey bukkitReplace = CraftNamespacedKey.fromMinecraft(replaceWith);
			if (Bukkit.getLootTable(bukkitReplace) != null) {
				CraftLootTable bukkitTable = (CraftLootTable) Bukkit.getLootTable(bukkitReplace);
				LootParams.Builder builder = new LootParams.Builder((ServerLevel) world)
					.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(CraftLocation.toBlockPosition(origin)));
				bukkitTable.getHandle().getRandomItems(builder.create(LootContextParamSets.CHEST)).stream()
					.filter(Objects::nonNull)
					.filter(itemStack -> itemCondition == null || itemCondition.test(new Tuple<>(world, itemStack))).forEach(items::add);
				return items;
			}
		}
		return new LinkedList<>();
	}
}
