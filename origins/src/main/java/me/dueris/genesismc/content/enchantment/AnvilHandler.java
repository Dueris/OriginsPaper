package me.dueris.genesismc.content.enchantment;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AnvilHandler implements Listener {
	public static @Nullable net.minecraft.world.item.enchantment.Enchantment minecraftEnchantment = CraftEnchantment.bukkitToMinecraft(CraftRegistry.ENCHANTMENT.get(new NamespacedKey("origins", "water_protection")));
	public static Enchantment bukkitEnchantment = Enchantment.getByKey(new NamespacedKey("origins", "water_protection"));
	public static ArrayList<Enchantment> conflictenchantments = new ArrayList<>();

	static {
		conflictenchantments.add(Enchantment.FIRE_PROTECTION);
		conflictenchantments.add(Enchantment.PROTECTION);
		conflictenchantments.add(Enchantment.BLAST_PROTECTION);
		conflictenchantments.add(Enchantment.FEATHER_FALLING);
		conflictenchantments.add(Enchantment.PROJECTILE_PROTECTION);
	}

	public static ItemStack setWaterProtCustomEnchantLevel(int lvl, ItemStack item) {
		if (lvl == 0) lvl = 1;
		ItemMeta meta = item.getItemMeta().clone();
		meta.setCustomModelData(lvl);
		meta.setLore(List.of(ChatColor.GRAY + "Water Protection " + numberToRomanNum(lvl)));

		item.setItemMeta(meta);

		net.minecraft.world.item.ItemStack stack = CraftItemStack.unwrap(item);
		stack.enchant(minecraftEnchantment, lvl);
		return CraftItemStack.asCraftMirror(stack);
	}

	public static String numberToRomanNum(int lvl) {
		if (lvl > 10) {
			Bukkit.getLogger().severe("Cannot translate value higher than max enchantment value in Genesis {4}");
			return null;
		} else {
			switch (lvl) {
				case 2 -> {
					return "II";
				}
				case 3 -> {
					return "III";
				}
				case 4 -> {
					return "IV";
				}
				case 5 -> {
					return "V";
				}
				case 6 -> {
					return "VI";
				}
				case 7 -> {
					return "VII";
				}
				case 8 -> {
					return "VIII";
				}
				case 9 -> {
					return "IX";
				}
				case 10 -> {
					return "X";
				}
				default -> {
					return "I";
				}
			}
		}
	}

	@EventHandler
	public void onAnvilResult(PrepareResultEvent e) {
		if (e.getInventory() instanceof AnvilInventory inv) {
			if (inv.getResult() == null) return;
			if (inv.getResult().containsEnchantment(bukkitEnchantment)) {
				inv.setResult(setWaterProtCustomEnchantLevel(inv.getResult().getEnchantmentLevel(bukkitEnchantment), inv.getResult()));
			}
		}
	}

	@EventHandler
	public void onAnvil(PrepareAnvilEvent e) {
		boolean conflicts = false;
		try {
			if (e.getInventory().getFirstItem() != null && e.getInventory().getSecondItem() != null) {
				if (e.getInventory().getFirstItem().containsEnchantment(bukkitEnchantment) || e.getInventory().getSecondItem().containsEnchantment(bukkitEnchantment)) {
					for (Enchantment possConf : e.getInventory().getFirstItem().getEnchantments().keySet()) {
						if (!conflicts) {
							if (!minecraftEnchantment.isCompatibleWith(((CraftEnchantment) possConf).getHandle())) {
								conflicts = true;
							}
						}
					}
					for (Enchantment possConf : e.getInventory().getSecondItem().getEnchantments().keySet()) {
						if (!conflicts) {
							if (!minecraftEnchantment.isCompatibleWith(((CraftEnchantment) possConf).getHandle())) {
								conflicts = true;
							}
						}
					}
				}
			}
			if (!conflicts) {
				// begin anvil calculations. no conflicts and the result != null
				if (e.getInventory().getFirstItem().containsEnchantment(bukkitEnchantment) || e.getInventory().getSecondItem().containsEnchantment(bukkitEnchantment)) {
					boolean firstContains = e.getInventory().getFirstItem().containsEnchantment(bukkitEnchantment);
					boolean secondContains = e.getInventory().getSecondItem().containsEnchantment(bukkitEnchantment);
					int resultLvl = 0;
					if (firstContains && secondContains) {
						int firstlvl = e.getInventory().getFirstItem().getEnchantments().get(bukkitEnchantment);
						int secondlvl = e.getInventory().getSecondItem().getEnchantments().get(bukkitEnchantment);
						int finl = 1;
						if (firstlvl > secondlvl) {
							finl = firstlvl;
						} else if (firstlvl < secondlvl) {
							finl = secondlvl;
						} else {
							finl = firstlvl + 1;
						}
						resultLvl = finl;
					} else if (firstContains) {
						resultLvl = e.getInventory().getFirstItem().getEnchantments().get(bukkitEnchantment);
					} else if (secondContains) {
						resultLvl = e.getInventory().getSecondItem().getEnchantments().get(bukkitEnchantment);
					}
					if (resultLvl != 0) {
//                        e.setResult(setWaterProtCustomEnchantLevel(resultLvl, new ItemStack(e.getInventory().getFirstItem())));
					}
				}
			}
		} catch (Exception ee) {
			// me personally i am done with this enchant system
		}

	}
}
