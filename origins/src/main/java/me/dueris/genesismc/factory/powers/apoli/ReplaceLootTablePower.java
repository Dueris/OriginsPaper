package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftLootTable;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ReplaceLootTablePower extends PowerType {
	private final FactoryJsonObject replace;
	private final FactoryJsonObject bientityCondition;
	private final FactoryJsonObject blockCondition;
	private final FactoryJsonObject itemCondition;

	public ReplaceLootTablePower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject replace, FactoryJsonObject bientityCondition, FactoryJsonObject blockCondition, FactoryJsonObject itemCondition) {
		super(name, description, hidden, condition, loading_priority);
		this.replace = replace;
		this.bientityCondition = bientityCondition;
		this.blockCondition = blockCondition;
		this.itemCondition = itemCondition;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("replace_loot_table"))
			.add("replace", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("bientity_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("block_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("item_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void inventoryPopulate(LootGenerateEvent e) {
		if (getPlayers().contains(e.getEntity())) {
			e.setLoot(modifyLoot(e.getLoot(), e.getLootTable().getKey(), e.getWorld(), e.getEntity().getLocation()));
		}
	}

	@EventHandler
	public void dropEvent(EntityDeathEvent e) {
		if ((e.getEntity().getKiller() != null)) {
			Player p = e.getEntity().getKiller();
			String key = "minecraft:entities/" + e.getEntityType().getKey().getKey();
			if (Bukkit.getLootTable(NamespacedKey.fromString(key)) != null) {
				if (getPlayers().contains(p) && ConditionExecutor.testBiEntity(bientityCondition, p, e.getEntity())) {
					modifyLoot(e.getDrops(), NamespacedKey.fromString(key), e.getEntity().getWorld(), e.getEntity().getLocation());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void blockDropEvent(BlockDropItemEvent e) {
		if (getPlayers().contains(e.getPlayer())) {
			String formattedKey = "minecraft:blocks/" + e.getBlockState().getType().getKey().getKey();
			List<ItemStack> drops = new ArrayList<>();
			drops.addAll(modifyLoot(new ArrayList<>(e.getBlockState().getDrops()), NamespacedKey.fromString(formattedKey), e.getBlock().getWorld(), e.getBlock().getLocation()));
			if (!drops.isEmpty() && ConditionExecutor.testBlock(blockCondition, e.getBlock())) {
				e.setCancelled(true); // Genesis overrides the drops because the loottable returned was not empty
				for (ItemStack drop : drops) {
					e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), drop);
				}
			}
		}
	}

	protected List<ItemStack> modifyLoot(List<ItemStack> items, NamespacedKey table, World world, Location origin) {
		for (String toReplaceRaw : replace.keySet()) {
			boolean canPass;
			if (toReplaceRaw.contains("(") || toReplaceRaw.contains(")")) { // Pattern?
				canPass = Pattern.compile(toReplaceRaw).matcher(table.asString()).matches();
			} else {
				canPass = table.asString().equals(toReplaceRaw);
			}

			if (canPass) {
				// Clear current loottable
				items.clear();
				NamespacedKey replaceWith = replace.getNamespacedKey(toReplaceRaw);
				if (Bukkit.getLootTable(replaceWith) != null) {
					// Modify loot table
					@NotNull LootTable l = Bukkit.getLootTable(replaceWith);
					CraftLootTable lootTable = ((CraftLootTable) l);
					LootParams.Builder builder = new LootParams.Builder(
						((CraftWorld) world).getHandle()).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(CraftLocation.toBlockPosition(origin)));
					lootTable.getHandle().getRandomItems(builder.create(LootContextParamSets.CHEST)).stream()
						.filter(Objects::nonNull)
						.map(net.minecraft.world.item.ItemStack::getBukkitStack).filter(item -> ConditionExecutor.testItem(itemCondition, item)).forEach(items::add);
					return items;
				}
			}
		}
		return new ArrayList<>();
	}
}
