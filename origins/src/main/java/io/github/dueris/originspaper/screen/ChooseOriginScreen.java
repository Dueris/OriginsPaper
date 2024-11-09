package io.github.dueris.originspaper.screen;

import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.component.OriginComponent;
import io.github.dueris.originspaper.origin.*;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.registry.ModItems;
import io.github.dueris.originspaper.util.LoopingLinkedObjectList;
import io.papermc.paper.adventure.PaperAdventure;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class ChooseOriginScreen {
	public static final List<ServerPlayer> CURRENTLY_RENDERING = new LinkedList<>();
	protected static final Map<ServerPlayer, ChooseOriginScreen> RENDER_MAP = new ConcurrentHashMap<>();
	protected static final ItemStack NEXT_STACK;
	protected static final ItemStack BACK_STACK;

	static {
		NEXT_STACK = new ItemStack(Items.ARROW);
		NEXT_STACK.set(DataComponents.CUSTOM_NAME, noItalic(Component.literal("Next Origin")));

		BACK_STACK = new ItemStack(Items.ARROW);
		BACK_STACK.set(DataComponents.CUSTOM_NAME, noItalic(Component.literal("Previous Origin")));
	}

	private final List<OriginLayer> layerList;
	private final ServerPlayer holder;
	private final LoopingLinkedObjectList<OriginReference> originSelection;
	protected InventoryView inventoryView;

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

		buildOrigins(holder);
		openSelection();
	}

	private static Component noItalic(Component component) {
		net.kyori.adventure.text.Component kyori = PaperAdventure.asAdventure(component);
		return PaperAdventure.asVanilla(kyori.decoration(TextDecoration.ITALIC, false).color(NamedTextColor.GRAY));
	}

	private void buildOrigins(ServerPlayer holder) {
		originSelection.clear();
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

			originSelection.add(new OriginReference(origin, false));
		}

		originSelection.sort(Comparator.comparingInt((OriginReference o) ->
			Objects.requireNonNull(o.origin, "Origin was null when it shouldn't!").getImpact().getImpactValue()).thenComparingInt(OriginReference::getOrder));
		originSelection.addLast(new OriginReference(null, true));
	}

	private void openSelection() {
		update();
		CURRENTLY_RENDERING.add(holder);
		RENDER_MAP.put(holder, this);
	}

	protected void next() {
		this.originSelection.next();
	}

	protected void back() {
		this.originSelection.previous();
	}

	protected void update() {
		CraftInventory menu = buildBaseMenu();
		ItemStack[] builtContents = updateContents();
		for (int i = 0; i < builtContents.length; i++) {
			menu.getInventory().setItem(i, builtContents[i]);
		}
		holder.getBukkitEntity().openInventory(menu);
		inventoryView = holder.getBukkitEntity().getOpenInventory();
		RENDER_MAP.put(holder, this);
	}

	private ItemStack @NotNull [] updateContents() {
		ItemStack[] built = new ItemStack[54];

		Origin origin = this.getCurrentOrigin();
		List<Power> toDisplay = new ArrayList<>(origin == null ? List.of() : origin.getPowers());
		for (int i = 0; i <= 53; i++) {
			if ((i == 0 || i == 1 || i == 2) ||
				(i == 6 || i == 7 || i == 8)) {
				built[i] = impact(i);
				continue;
			}

			if (i == 45) {
				built[i] = BACK_STACK;
				continue;
			} else if (i == 53) {
				built[i] = NEXT_STACK;
				continue;
			}

			if (i == 13) {
				ItemStack stack = origin == null ? ModItems.ORB_OF_ORIGINS : origin.getDisplayItem();

				stack.set(DataComponents.CUSTOM_NAME, noItalic(origin == null ? Component.translatable("origin.origins.random.name") : origin.getName()).copy().withColor(0xffffff));
				stack.set(DataComponents.LORE, new ItemLore(List.of(noItalic(origin == null ? Component.translatable("origin.origins.random.description") : origin.getDescription()))));

				built[i] = stack;
				continue;
			}

			if ((i >= 20 && i <= 24) || (i >= 29 && i <= 33)
				|| i == 39 || i == 40 || i == 41) {
				ItemStack stack = new ItemStack(Items.MAP);
				if (toDisplay.isEmpty()) {
					stack.set(DataComponents.HIDE_TOOLTIP, Unit.INSTANCE);
					built[i] = stack;
				} else {
					Power power = toDisplay.removeFirst();

					if (power.isHidden()) {
						stack.set(DataComponents.HIDE_TOOLTIP, Unit.INSTANCE);
						built[i] = stack;
						continue;
					}

					stack.set(DataComponents.CUSTOM_NAME, noItalic(power.getName()).copy().withColor(0xffffff));
					stack.set(DataComponents.LORE, new ItemLore(List.of(noItalic(power.getDescription()))));

					built[i] = stack;
				}
				continue;
			}

			built[i] = ItemStack.EMPTY;
		}

		return built;
	}

	private @NotNull ItemStack impact(int index) {
		Impact impact = getCurrentOrigin() == null ? Impact.NONE : getCurrentOrigin().getImpact();
		ItemStack impactItem = switch (impact) {
			case LOW -> onlyIndexOf(new ItemStack(Items.GREEN_STAINED_GLASS_PANE), index, 0, 8);
			case MEDIUM -> onlyIndexOf(new ItemStack(Items.YELLOW_STAINED_GLASS_PANE), index, 0, 1, 7, 8);
			case HIGH -> new ItemStack(Items.RED_STAINED_GLASS_PANE);
			case NONE -> new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
		};
		impactItem.set(DataComponents.CUSTOM_NAME, noItalic(impact.getTextComponent()).copy().withStyle(impact.getTextStyle()));
		return impactItem;
	}

	private ItemStack onlyIndexOf(ItemStack ifTrue, int index, int... indexes) {
		return IntStream.of(indexes).anyMatch(i -> i == index) ? ifTrue : ItemStack.EMPTY;
	}

	private @NotNull CraftInventory buildBaseMenu() {
		return (CraftInventory) MinecraftServer.getServer().server.createInventory(this.holder.getBukkitEntity(), 54,
			PaperAdventure.asAdventure(this.originSelection.getCurrent().origin == null && this.originSelection.getCurrent().random ?
				Component.translatable("origin.origins.random.name") : this.originSelection.getCurrent().origin.getName()));
	}

	@Nullable
	public OriginLayer getCurrentLayer() {
		return layerList.getFirst();
	}

	@Nullable
	public Origin getCurrentOrigin() {
		return originSelection.getCurrent().origin;
	}

	public void choose() {
		ServerPlayer player = this.holder;

		OriginComponent component = OriginComponent.ORIGIN.get(player);
		OriginLayer layer = OriginLayerManager.get(Objects.requireNonNull(this.getCurrentLayer(), "Current layer is null! How are you even choosing right now...?").getId());

		if (component.hasAllOrigins() && component.hasOrigin(layer)) {
			OriginsPaper.LOGGER.warn("Player {} tried to choose origin for layer \"{}\" while having one already.", player.getName().getString(), layer.getId());
			return;
		}

		OriginReference reference = originSelection.getCurrent();
		List<ResourceLocation> randomOrigins = layer.getRandomOrigins(player);
		Origin origin = reference.random ?
			OriginManager.get(randomOrigins.get(new Random().nextInt(randomOrigins.size()))) :
			OriginManager.get(Objects.requireNonNull(this.getCurrentOrigin(), "Current origin is null!").getId());
		if (!(origin.isChoosable() || layer.contains(origin, player))) {
			OriginsPaper.LOGGER.warn("Player {} tried to choose unchoosable origin \"{}\" from layer \"{}\"!", player.getName().getString(), origin.getId(), layer.getId());
			component.setOrigin(layer, Origin.EMPTY);
		} else {

			boolean hadOriginBefore = component.hadOriginBefore();
			boolean hadAllOrigins = component.hasAllOrigins();

			component.setOrigin(layer, origin);
			component.checkAutoChoosingLayers(player, false);

			if (component.hasAllOrigins() && !hadAllOrigins) {
				OriginComponent.onChosen(player, hadOriginBefore);
			}

			OriginsPaper.LOGGER.info("Player {} chose origin \"{}\" for layer \"{}\"", player.getName().getString(), origin.getId(), layer.getId());

		}

		component.selectingOrigin(false);
		component.sync();

		layerList.removeFirst();

		if (!layerList.isEmpty()) {
			buildOrigins(holder);
			openSelection();
		} else {
			inventoryView = null;
			CURRENTLY_RENDERING.remove(holder);
			RENDER_MAP.remove(holder);
			holder.getBukkitEntity().closeInventory();
		}
	}

	protected record OriginReference(@Nullable Origin origin, boolean random) {

		public int getOrder() {
			return origin == null ? -1 : origin.getOrder();
		}
	}
}
