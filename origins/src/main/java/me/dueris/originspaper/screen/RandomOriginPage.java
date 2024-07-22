package me.dueris.originspaper.screen;

import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.content.OrbOfOrigins;
import me.dueris.originspaper.event.OriginChangeEvent;
import me.dueris.originspaper.registry.registries.Layer;
import me.dueris.originspaper.registry.registries.Origin;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RandomOriginPage implements ChoosingPage {
	private final Random random = new Random(232342333L);

	@Override
	public ItemStack[] createDisplay(Player player, @NotNull Layer layer) {
		List<ItemStack> stacks = new ArrayList<>();
		List<Origin> randomOrigins = new ArrayList<>(layer.getRandomOrigins());
		randomOrigins.sort(Comparator.comparingInt(Origin::getImpact).thenComparingInt(Origin::getOrder));
		List<List<Origin>> texts = new ArrayList<>();
		int batchSize = 12;

		for (int i = 0; i < randomOrigins.size(); i += batchSize) {
			int endIndex = Math.min(i + batchSize, randomOrigins.size());
			List<Origin> sublist = randomOrigins.subList(i, endIndex);
			texts.add(new ArrayList<>(sublist));
		}

		int iE = 1;

		for (int i = 0; i < 54; i++) {
			if (i > 2 && (i < 6 || i > 8)) {
				if (i == 13) {
					stacks.add(this.getChoosingStack(player));
				} else if (i == 22) {
					ItemStack stack = new ItemStack(Material.FILLED_MAP);
					ItemMeta meta = stack.getItemMeta();
					meta.displayName(Component.text("You will be assigned one of the following:"));
					stack.setItemMeta(meta);
					stacks.add(stack);
				} else if (i >= 30 && i <= 32 || i >= 39 && i <= 41 || i >= 48 && i <= 50) {
					if (texts.isEmpty()) {
						ItemStack blank = new ItemStack(Material.MAP);
						Arrays.stream(ItemFlag.values()).toList().forEach(xva$0 -> blank.addItemFlags(xva$0));
						stacks.add(blank);
					} else {
						List<TextComponent> cL = texts.get(0).stream().map(Origin::getName).map(Component::text).toList();
						texts.remove(0);
						ItemStack stack = new ItemStack(Material.FILLED_MAP);
						stack.lore(cL);
						ItemMeta meta = stack.getItemMeta();
						meta.setDisplayName("Page {}".replace("{}", String.valueOf(iE)));
						iE++;
						stack.setItemMeta(meta);
						stacks.add(stack);
					}
				} else if (i == 45) {
					stacks.add(ScreenNavigator.BACK_ITEMSTACK);
				} else if (i == 53) {
					stacks.add(ScreenNavigator.NEXT_ITEMSTACK);
				} else {
					stacks.add(new ItemStack(Material.AIR));
				}
			} else {
				Material impactMaterial = Material.GRAY_STAINED_GLASS_PANE;
				Component impactComponent = Component.text("None").color(TextColor.color(11053224));
				Component fullImpactComponent = Component.textOfChildren(new ComponentLike[]{Component.text("Impact: "), impactComponent})
					.decorate(TextDecoration.ITALIC.as(false).decoration());
				ItemStack impact = OriginPage.itemProperties(new ItemStack(impactMaterial), fullImpactComponent, ItemFlag.values(), null, null);
				stacks.add(impact);
			}
		}

		return stacks.toArray(new ItemStack[0]);
	}

	@Override
	public ItemStack getChoosingStack(Player player) {
		ItemStack orb = OrbOfOrigins.orb == null ? OrbOfOrigins.createOrb() : OrbOfOrigins.orb;
		ItemMeta meta = orb.getItemMeta();
		meta.displayName(Component.text("Random Origin").color(TextColor.color(2664373)));
		orb.setItemMeta(meta);
		return orb;
	}

	@Override
	public void onChoose(@NotNull Player player, @NotNull Layer layer) {
		int r = this.random.nextInt(layer.getRandomOrigins().size());
		Origin origin = layer.getRandomOrigins().get(Math.max(r, 1));
		PowerHolderComponent.setOrigin((org.bukkit.entity.Player) player.getBukkitEntity(), layer, origin);
		OriginChangeEvent e = new OriginChangeEvent((org.bukkit.entity.Player) player.getBukkitEntity(), origin, ScreenNavigator.orbChoosing.contains(player));
		Bukkit.getPluginManager().callEvent(e);
		player.getBukkitEntity().getOpenInventory().close();
	}

	@Override
	public ResourceLocation key() {
		return OriginsPaper.originIdentifier("random_page");
	}
}
