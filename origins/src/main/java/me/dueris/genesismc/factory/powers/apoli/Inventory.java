package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.KeybindTriggerEvent;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.ContainerType;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.ColorConstants;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.KeybindingUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;

public class Inventory extends CraftPower implements Listener {

	@EventHandler
	public void MoveBackChange(OriginChangeEvent e) {
		Player p = e.getPlayer();
		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
				new BukkitRunnable() {
					@Override
					public void run() {
						if (!shulker_inventory.contains(p)) {
							ArrayList<ItemStack> vaultItems = InventoryUtils.getItems(p);
							org.bukkit.inventory.Inventory vault = Bukkit.createInventory(p, InventoryType.CHEST, "origin.getPowerFileFromType(origins:inventory).get(title)");

							vaultItems.stream()
								.forEach(itemStack -> vault.addItem(itemStack));
							for (ItemStack item : vault.getContents()) {
								if (item != null && item.getType() != Material.AIR) {
									p.getWorld().dropItemNaturally(p.getLocation(), item);
									vault.removeItem(item);
								}
							}
							ArrayList<ItemStack> prunedItems = new ArrayList<>();

							Arrays.stream(vault.getContents())
								.filter(itemStack -> {
									return itemStack != null;
								})
								.forEach(itemStack -> prunedItems.add(itemStack));

							InventoryUtils.storeItems(prunedItems, p);
							vault.clear();
							this.cancel();
						}
					}
				}.runTaskTimer(GenesisMC.getPlugin(), 1L, 1L);
			}
		}
	}

	@EventHandler
	public void keytrigger(KeybindTriggerEvent e) {
		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			if (getPowerArray().contains(e.getPlayer())) {
				for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
					if (CooldownUtils.isPlayerInCooldownFromTag(e.getPlayer(), Utils.getNameOrTag(power))) continue;
					if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) e.getPlayer())) {
						setActive(e.getPlayer(), power.getTag(), true);
						if (KeybindingUtils.isKeyActive(power.get("key").getOrDefault("key", "key.origins.primary_active").toString(), e.getPlayer())) {
							ArrayList<ItemStack> vaultItems = InventoryUtils.getItems(e.getPlayer());
							org.bukkit.inventory.Inventory vault = ContainerType.getContainerType(power.get("container_type").toString()).createInventory(e.getPlayer(), Utils.createIfPresent(power.getString("title")));
							vaultItems.stream()
								.forEach(itemStack -> vault.addItem(itemStack));
							e.getPlayer().openInventory(vault);
						}
					} else {
						setActive(e.getPlayer(), power.getTag(), false);
					}
				}
			}
		}
	}

	@EventHandler
	public void deathTIMEEE(PlayerDeathEvent e) {
		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			if (shulker_inventory.contains(e.getPlayer())) {
				Player p = e.getPlayer();
				for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
					if (power.getBooleanOrDefault("drop_on_death", false)) {
						ArrayList<ItemStack> vaultItems = InventoryUtils.getItems(p);
						org.bukkit.inventory.Inventory vault = Bukkit.createInventory(p, InventoryType.CHEST, "origin.getPowerFileFromType(origins:inventory).get(title)");

						vaultItems.stream()
							.forEach(itemStack -> vault.addItem(itemStack));
						for (ItemStack item : vault.getContents()) {
							if (item != null && item.getType() != Material.AIR) {
								p.getWorld().dropItemNaturally(p.getLocation(), item);
								vault.removeItem(item);
							}
						}
						ArrayList<ItemStack> prunedItems = new ArrayList<>();

						Arrays.stream(vault.getContents())
							.filter(itemStack -> {
								return itemStack != null;
							})
							.forEach(itemStack -> prunedItems.add(itemStack));

						InventoryUtils.storeItems(prunedItems, p);
						vault.clear();
					}
				}
			}
		}
	}

	@Override
	public void run(Player p) {

	}

	@Override
	public String getPowerFile() {
		return "apoli:inventory";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return shulker_inventory;
	}
}

