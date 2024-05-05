package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
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
import org.bukkit.event.Listener;
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

public class ReplaceLootTablePower extends CraftPower implements Listener {

	@EventHandler
	public void inventoryPopulate(LootGenerateEvent e) {
		if (getPlayersWithPower().contains(e.getEntity())) {
			OriginPlayerAccessor.getPowers((Player) e.getEntity(), getType()).forEach(power -> modifyLoot(e.getLoot(), power, e.getLootTable().getKey(), e.getWorld(), e.getEntity().getLocation()));
		}
	}

	@EventHandler
	public void dropEvent(EntityDeathEvent e) {
		if (getPlayersWithPower().contains(e.getEntity()) || (e.getEntity().getKiller() != null && getPlayersWithPower().contains(e.getEntity().getKiller()))) {
			Player p = e.getEntity().getKiller();
			String key = "minecraft:entities/" + e.getEntityType().getKey().getKey();
			if (Bukkit.getLootTable(NamespacedKey.fromString(key)) != null) {
				if (getPlayersWithPower().contains(p)) {
					OriginPlayerAccessor.getPowers(p, getType()).forEach(power -> modifyLoot(e.getDrops(), power, NamespacedKey.fromString(key), e.getEntity().getWorld(), e.getEntity().getLocation()));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void blockDropEvent(BlockDropItemEvent e) {
		if (getPlayersWithPower().contains(e.getPlayer())) {
			String formattedKey = "minecraft:blocks/" + e.getBlockState().getType().getKey().getKey();
			List<ItemStack> drops = new ArrayList<>();
			OriginPlayerAccessor.getPowers(e.getPlayer(), getType())
				.forEach(power -> drops.addAll(modifyLoot(new ArrayList<>(e.getBlockState().getDrops()), power, NamespacedKey.fromString(formattedKey), e.getBlock().getWorld(), e.getBlock().getLocation())));
			if (!drops.isEmpty()) {
				e.setCancelled(true); // Genesis overrides the drops because the loottable returned was not empty
				for (ItemStack drop : drops) {
					e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), drop);
				}
			}
		}
	}

	protected List<ItemStack> modifyLoot(List<ItemStack> items, Power power, NamespacedKey table, World world, Location origin) {
		FactoryJsonObject replace = power.getJsonObject("replace"); // Required
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
						.map(net.minecraft.world.item.ItemStack::getBukkitStack).forEach(items::add);
					return items;
				}
			}
		}
		return new ArrayList<>();
	}

	@Override
	public String getType() {
		return "apoli:replace_loot_table";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return replace_loot_table;
	}
}
