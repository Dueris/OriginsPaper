package io.github.dueris.originspaper.screen;

import io.github.dueris.originspaper.component.OriginComponent;
import io.github.dueris.originspaper.origin.*;
import io.github.dueris.originspaper.util.LoopingLinkedObjectList;
import io.papermc.paper.adventure.PaperAdventure;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class ChooseOriginScreen {
	public static final List<ServerPlayer> CURRENTLY_RENDERING = new LinkedList<>();
	protected static final ItemStack NEXT_STACK;
	protected static final ItemStack BACK_STACK;

	static {
		NEXT_STACK = new ItemStack(Items.ARROW);
		NEXT_STACK.set(DataComponents.CUSTOM_NAME, Component.literal("Next Origin"));

		BACK_STACK = new ItemStack(Items.ARROW);
		BACK_STACK.set(DataComponents.CUSTOM_NAME, Component.literal("Previous Origin"));
	}

	private final List<OriginLayer> layerList;
	private final ServerPlayer holder;
	private final LoopingLinkedObjectList<Origin> originSelection;

	public ChooseOriginScreen(ServerPlayer holder) {
		this.holder = holder;
		List<OriginLayer> layers = new ObjectArrayList<>();
		OriginComponent component = OriginComponent.ORIGIN.get(holder);

		OriginLayerManager.values()
			.stream()
			.filter(OriginLayer::isEnabled)
			.filter(Predicate.not(component::hasOrigin))
			.forEach(layers::add);

		Collections.sort(layers);
		this.layerList = layers;
		this.originSelection = new LoopingLinkedObjectList<>();

		OriginLayer currentLayer = getCurrentLayer();
		for (ResourceLocation originId : Objects.requireNonNull(currentLayer, "Current layers are null!").getOrigins(this.holder)) {

			Origin origin = OriginManager.get(originId);
			if (!origin.isChoosable()) {
				continue;
			}

			ItemStack iconStack = origin.getDisplayItem();
			if (iconStack.is(Items.PLAYER_HEAD)) {
				iconStack.set(DataComponents.PROFILE, new ResolvableProfile(holder.getGameProfile()));
			}

			originSelection.add(origin);
		}

		originSelection.sort(Comparator.comparingInt((Origin o) -> o.getImpact().getImpactValue()).thenComparingInt(Origin::getOrder));
		openSelection();
	}

	private void openSelection() {
		// Build and open base
		CraftInventory menu = buildBaseMenu();
		CraftPlayer player = holder.getBukkitEntity();
		player.openInventory(menu);

		CURRENTLY_RENDERING.add(holder);

		ItemStack[] builtContents = updateContents();
		menu.setContents(Arrays.stream(builtContents)
			.map(ItemStack::asBukkitCopy)
			.distinct()
			.toArray(org.bukkit.inventory.ItemStack[]::new)
		);
	}

	private ItemStack @NotNull [] updateContents() {
		ItemStack[] built = new ItemStack[53];

		for (int i = 0; i <= 53; i++) {
			if ((i == 0 || i == 1 || i == 2) ||
				(i == 6 || i == 7 || i == 8)) {
				built[i] = impact(i);
				continue;
			}

			if (i == 45) {

			}

			built[i] = ItemStack.EMPTY;
		}

		return built;
	}

	private @NotNull ItemStack impact(int index) {
		Impact impact = getCurrentOrigin().getImpact();
		ItemStack impactItem = switch (impact) {
			case LOW -> onlyIndexOf(new ItemStack(Items.GREEN_STAINED_GLASS_PANE), index, 0, 8);
			case MEDIUM -> onlyIndexOf(new ItemStack(Items.YELLOW_STAINED_GLASS_PANE), index, 0, 1, 7, 8);
			case HIGH -> new ItemStack(Items.RED_STAINED_GLASS_PANE);
			case NONE -> new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
		};
		impactItem.set(DataComponents.CUSTOM_NAME, impact.getTextComponent());
		return impactItem;
	}

	private ItemStack onlyIndexOf(ItemStack ifTrue, int index, int... indexes) {
		return IntStream.of(indexes).anyMatch(i -> i == index) ? ifTrue : ItemStack.EMPTY;
	}

	private @NotNull CraftInventory buildBaseMenu() {
		return (CraftInventory) MinecraftServer.getServer().server.createInventory(this.holder.getBukkitEntity(), 54,
			PaperAdventure.asAdventure(this.originSelection.getCurrent().getName()));
	}

	@Nullable
	public OriginLayer getCurrentLayer() {
		return layerList.getFirst();
	}

	@NotNull
	public Origin getCurrentOrigin() {
		return originSelection.getCurrent();
	}
}
