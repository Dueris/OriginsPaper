package me.dueris.genesismc.screen;

import me.dueris.genesismc.event.OriginChooseEvent;
import me.dueris.genesismc.util.SendCharts;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class ScreenConstants {

	public static void DefaultChoose(Player p) {
		p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);

		//default choose
		p.closeInventory();
		p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10, 2);
		p.spawnParticle(Particle.CLOUD, p.getLocation(), 100);
		p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, p.getLocation(), 6);
		p.setCustomNameVisible(false);
		p.setHealthScaled(false);

		OriginChooseEvent chooseEvent = new OriginChooseEvent(p);
		getServer().getPluginManager().callEvent(chooseEvent);

		SendCharts.originPopularity(p);

	}

	public static void setAttributesToDefault(Player p) {
		p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(0);
		p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
		p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1);
		p.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
		p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0);
		p.getAttribute(Attribute.GENERIC_LUCK).setBaseValue(0);
		p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
		p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.10000000149011612F);
	}

	public static ItemStack itemProperties(ItemStack item, String displayName, ItemFlag itemFlag, Enchantment enchantment, String lore) {
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(displayName);
		if (itemFlag != null) itemMeta.addItemFlags(itemFlag);
		if (enchantment != null) itemMeta.addEnchant(enchantment, 1, true);
		if (lore != null) {
			ArrayList<String> itemLore = new ArrayList<>();
			itemLore.add(lore);
			itemMeta.setLore(itemLore);
		}
		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack itemPropertiesMultipleLore(ItemStack item, String displayName, ItemFlag itemFlag, Enchantment enchantment, List lore) {
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(displayName);
		if (itemFlag != null) itemMeta.addItemFlags(itemFlag);
		if (enchantment != null) itemMeta.addEnchant(enchantment, 1, true);
		if (lore != null) itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static List<String> cutStringIntoLines(String string) {
		ArrayList<String> strings = new ArrayList<>();
		int startStringLength = string.length();
		while (string.length() > 40) {
			for (int i = 40; i > 1; i--) {
				if (String.valueOf(string.charAt(i)).matches("[\\s\\n]") || String.valueOf(string.charAt(i)).equals(" ")) {
					strings.add(string.substring(0, i));
					string = string.substring(i + 1);
					break;
				}
			}
			if (startStringLength == string.length()) return List.of(string);
		}
		if (strings.isEmpty()) return List.of(string);
		strings.add(string);
		return strings.stream().toList();
	}
}
