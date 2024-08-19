package io.github.dueris.originspaper.screen;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.data.types.Impact;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.PowerType;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.github.dueris.originspaper.util.ComponentUtil;
import io.github.dueris.originspaper.util.LangFile;
import io.github.dueris.originspaper.util.entity.PlayerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record OriginPage(Origin origin) implements ChoosingPage {

	public static void setAttributesToDefault(net.minecraft.world.entity.player.@NotNull Player p) {
		p.getBukkitEntity().getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(0.0);
		p.getBukkitEntity().getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0.0);
		p.getBukkitEntity().getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1.0);
		p.getBukkitEntity().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4.0);
		p.getBukkitEntity().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0);
		p.getBukkitEntity().getAttribute(Attribute.GENERIC_LUCK).setBaseValue(0.0);
		p.getBukkitEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
		p.getBukkitEntity().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1F);
		p.getBukkitEntity().getAttribute(Attribute.GENERIC_MOVEMENT_EFFICIENCY).setBaseValue(0.0);
		p.getBukkitEntity().getAttribute(Attribute.PLAYER_MINING_EFFICIENCY).setBaseValue(0.0);
		p.getBukkitEntity().getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).setBaseValue(p.getBukkitEntity().getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).getDefaultValue());
		p.getBukkitEntity().getAttribute(Attribute.PLAYER_SUBMERGED_MINING_SPEED).setBaseValue(0.2);
		p.getBukkitEntity().getAttribute(Attribute.GENERIC_GRAVITY).setBaseValue(0.08);
	}

	@Contract("_, _, _, _, _ -> param1")
	public static @NotNull ItemStack itemProperties(@NotNull ItemStack item, Component displayName, ItemFlag[] itemFlag, Enchantment enchantment, String lore) {
		ItemMeta itemMeta = item.getItemMeta();
		if (displayName != null) {
			itemMeta.displayName(displayName);
		}

		if (itemFlag != null) {
			itemMeta.addItemFlags(itemFlag);
		}

		if (enchantment != null) {
			itemMeta.addEnchant(enchantment, 1, true);
		}

		if (lore != null) {
			itemMeta.lore(cutStringIntoLines(lore).stream().map(OriginPage::noItalic).toList());
		}

		item.setItemMeta(itemMeta);
		return item;
	}

	private static @NotNull Component noItalic(String string) {
		return Component.text(string)
			.decoration(TextDecoration.ITALIC, false)
			.color(NamedTextColor.GRAY);
	}

	public static List<String> cutStringIntoLines(@NotNull String string) {
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

			if (startStringLength == string.length()) {
				return List.of(string);
			}
		}

		if (strings.isEmpty()) {
			return List.of(string);
		} else {
			strings.add(string);
			return strings.stream().toList();
		}
	}

	public int getOrder() {
		return this.origin.order();
	}

	@Override
	public @NotNull ResourceLocation key() {
		return ResourceLocation.parse(this.origin.key() + "_page");
	}

	@Override
	public ItemStack @NotNull [] createDisplay(net.minecraft.world.entity.player.Player player, OriginLayer layer) {
		List<ItemStack> stacks = new ArrayList<>();
		List<PowerType> powerContainers = new ArrayList<>(this.origin.powers().stream()
			.filter(Objects::nonNull).map(OriginsPaper::getPower).filter(p -> p != null && !p.isHidden()).toList());

		for (int i = 0; i < 54; i++) {
			if (i <= 2 || i >= 6 && i <= 8) {
				Impact originImpact = this.origin.impact();
				int impactInt = originImpact.getImpactValue();
				Material impactMaterial = originImpact.equals(Impact.LOW)
					? Material.GREEN_STAINED_GLASS_PANE
					: (
					originImpact.equals(Impact.MEDIUM)
						? Material.YELLOW_STAINED_GLASS_PANE
						: (originImpact.equals(Impact.HIGH) ? Material.RED_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE)
				);
				Component impactComponent = originImpact.equals(Impact.LOW)
					? Component.text("Low").color(TextColor.color(5569620))
					: (
					originImpact.equals(Impact.MEDIUM)
						? Component.text("Medium").color(TextColor.color(14535987))
						: (
						originImpact.equals(Impact.HIGH)
							? Component.text("High").color(TextColor.color(16536660))
							: Component.text("None").color(TextColor.color(11053224))
					)
				);
				Component fullImpactComponent = Component.textOfChildren(Component.text("Impact: "), impactComponent)
					.decoration(TextDecoration.ITALIC, false);
				ItemStack impact = itemProperties(new ItemStack(impactMaterial), fullImpactComponent, ItemFlag.values(), null, null);
				if ((impactInt != 1 || i != 0 && i != 8)
					&& (impactInt != 2 || i != 0 && i != 8 && i != 1 && i != 7)
					&& impactInt != 3
					&& impactInt != 0) {
					stacks.add(new ItemStack(Material.AIR));
				} else {
					stacks.add(impact);
				}
			} else if (i == 13) {
				stacks.add(this.getChoosingStack(player));
			} else if ((i < 20 || i > 24) && (i < 29 || i > 33) && i != 39 && i != 40 && i != 41) {
				if (i == 45) {
					stacks.add(ScreenNavigator.BACK_ITEMSTACK);
				} else if (i == 53) {
					stacks.add(ScreenNavigator.NEXT_ITEMSTACK);
				} else {
					stacks.add(new ItemStack(Material.AIR));
				}
			} else {
				while (!powerContainers.isEmpty() && powerContainers.get(0).isHidden()) {
					powerContainers.remove(0);
				}

				if (!powerContainers.isEmpty()) {
					ItemStack originPower = new ItemStack(Material.FILLED_MAP);
					ItemMeta meta = originPower.getItemMeta();
					meta.displayName(ComponentUtil.stringToComponent(LangFile.transform(PlainTextComponentSerializer.plainText().serialize(powerContainers.get(0).name()))).decoration(TextDecoration.ITALIC, false));
					Arrays.stream(ItemFlag.values()).toList().forEach(originPower::addItemFlags);
					List<Component> lore = cutStringIntoLines(LangFile.transform(PlainTextComponentSerializer.plainText().serialize(powerContainers.get(0).description()))).stream().map(OriginPage::noItalic).toList();
					meta.lore(lore);
					originPower.setItemMeta(meta);
					Arrays.stream(ItemFlag.values()).toList().forEach(originPower::addItemFlags);
					stacks.add(originPower);
					powerContainers.remove(0);
				} else {
					ItemStack blank = new ItemStack(Material.MAP);
					ItemMeta meta = blank.getItemMeta();
					meta.displayName(
						Component.text("")
					);
					meta.setHideTooltip(true);
					blank.setItemMeta(meta);
					Arrays.stream(ItemFlag.values()).toList().forEach(blank::addItemFlags);
					stacks.add(blank);
				}
			}
		}

		return stacks.toArray(new ItemStack[0]);
	}

	@Override
	public @NotNull ItemStack getChoosingStack(net.minecraft.world.entity.player.@NotNull Player player) {
		ItemStack originIcon = new ItemStack(this.origin.icon().getBukkitStack());
		Player bukkit = (Player) player.getBukkitEntity();
		if (originIcon.getType().equals(Material.PLAYER_HEAD)) {
			SkullMeta skull_p = (SkullMeta) originIcon.getItemMeta();
			skull_p.setOwningPlayer(bukkit);
			skull_p.setOwner(bukkit.getName());
			skull_p.setPlayerProfile(bukkit.getPlayerProfile());
			skull_p.setOwnerProfile(bukkit.getPlayerProfile());
			originIcon.setItemMeta(skull_p);
		}

		return itemProperties(originIcon, this.origin.name()
			.decoration(TextDecoration.ITALIC, false)
			.color(NamedTextColor.WHITE), ItemFlag.values(), null, PlainTextComponentSerializer.plainText().serialize(this.origin.description()));
	}

	@Override
	public void onChoose(net.minecraft.world.entity.player.@NotNull Player player, OriginLayer layer) {
		PowerHolderComponent.setOrigin(player.getBukkitEntity(), layer, this.origin);
		player.getBukkitEntity().getOpenInventory().close();
		final Player bukkitEntity = (Player) player.getBukkitEntity();
		if (PlayerManager.firstJoin.contains(bukkitEntity)) {
			new BukkitRunnable() {
				@Override
				public void run() {
//					if (PowerHolderComponent.hasPowerType(bukkitEntity, ModifyPlayerSpawnPower.class)) {
//						ModifyPlayerSpawnPower.suspendPlayer(bukkitEntity);
//					}
//
//					for (ModifyPlayerSpawnPower power : PowerHolderComponent.getPowers(bukkitEntity, ModifyPlayerSpawnPower.class)) {
//						power.runD(new PlayerPostRespawnEvent(bukkitEntity, bukkitEntity.getLocation(), false));
//					}

					PlayerManager.firstJoin.remove(bukkitEntity);
				}
			}.runTaskLater(OriginsPaper.getPlugin(), 4);
		}
	}
}