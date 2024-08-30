package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.power.factory.PowerType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RecipePower extends PowerType {
	public static HashMap<Player, List<String>> recipeMapping = new HashMap<>();
	public static HashMap<String, org.bukkit.inventory.Recipe> taggedRegistry = new HashMap<>();
	public static List<String> tags = new LinkedList<>();
	public static List<org.bukkit.inventory.Recipe> BUILT_RECIPES = new CopyOnWriteArrayList<>();
	private final RecipeHolder<? extends Recipe<?>> recipeHolder;
	private final org.bukkit.inventory.Recipe bukkitRecipe;

	public RecipePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
					   @NotNull RecipeHolder<? extends Recipe<?>> recipeHolder) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.recipeHolder = recipeHolder;
		this.bukkitRecipe = recipeHolder.toBukkitRecipe();
		BUILT_RECIPES.add(bukkitRecipe);
		tags.add(computeTag(bukkitRecipe));
		taggedRegistry.put(computeTag(bukkitRecipe), bukkitRecipe);
	}

	public static SerializableData getFactory() {
		return PowerType.getFactory().typedRegistry(OriginsPaper.apoliIdentifier("recipe"))
			.add("recipe", SerializableDataTypes.RECIPE);
	}

	@NotNull
	public static String computeTag(org.bukkit.inventory.Recipe recipe) {
		if (recipe instanceof ShapedRecipe ee) {
			return ee.key().asString();
		} else {
			return recipe instanceof ShapelessRecipe e ? e.key().asString() : null;
		}
	}

	public RecipeHolder<? extends Recipe<?>> getNmsRecipe() {
		return recipeHolder;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void load(ServerLoadEvent e) {
		Bukkit.getOnlinePlayers().forEach(this::updateTaggedRegistry);
		BUILT_RECIPES.forEach(Bukkit::addRecipe);
		Bukkit.updateRecipes();
	}

	public void updateTaggedRegistry(Player p) {
		if (recipeMapping.containsKey(p)) {
			recipeMapping.clear();
		}

		if (this.getPlayers().contains(((CraftPlayer) p).getHandle())) {
			String id = computeTag(bukkitRecipe);
			if (!taggedRegistry.containsKey(id)) {
				throw new IllegalStateException("Unable to locate recipe id. Bug?");
			}

			if (recipeMapping.containsKey(p)) {
				recipeMapping.get(p).add(id);
			} else {
				recipeMapping.put(p, new LinkedList<>(List.of(id)));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void craft(@NotNull PrepareItemCraftEvent e) {
		if (e.getRecipe() != null) {
			String key = computeTag(e.getRecipe());
			if (key != null) {
				updateTaggedRegistry((Player) e.getView().getPlayer());
				if (tags.contains(key) && !recipeMapping.getOrDefault(e.getView().getPlayer(), new LinkedList<>()).contains(key)) {
					e.getInventory().setResult(null);
				}
			}
		}
	}
}
