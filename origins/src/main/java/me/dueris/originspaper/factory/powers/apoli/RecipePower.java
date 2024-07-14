package me.dueris.originspaper.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.RequiredInstance;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.OriginChangeEvent;
import me.dueris.originspaper.event.PowerUpdateEvent;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RecipePower extends PowerType {
	public static HashMap<Player, List<String>> recipeMapping = new HashMap<>();
	public static HashMap<String, Recipe> taggedRegistry = new HashMap<>();
	public static List<String> tags = new ArrayList<>();
	private static boolean finishedLoad = false;
	private final FactoryJsonObject recipe;

	public RecipePower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject recipe) {
		super(name, description, hidden, condition, loading_priority);
		this.recipe = recipe;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("recipe"))
			.add("recipe", FactoryJsonObject.class, new RequiredInstance());
	}

	public static void parseRecipes() {
		for (PowerType powerContainer : OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).values().stream().filter(powerContainer -> powerContainer.getType().equalsIgnoreCase("apoli:recipe")).toList()) {
			if (!(powerContainer instanceof RecipePower recipePower)) continue;
			FactoryJsonObject recipe = recipePower.getRecipe();
			if (recipe == null)
				throw new IllegalArgumentException("Unable to find recipe data for power: " + recipePower.getTag());
			NamespacedKey key = CraftNamespacedKey.fromMinecraft(recipe.getString("id").contains(":") ? recipe.getResourceLocation("id") : ResourceLocation.parse(recipePower.getTag() + "_" + recipe.getString("id")));
			String type = recipe.getString("type");
			if (!type.startsWith("minecraft:")) {
				type = "minecraft:" + type;
			}
			if (type.equalsIgnoreCase("minecraft:crafting_shapeless")) {
				ShapelessRecipe rec = new ShapelessRecipe(key, computeResult(recipe.getJsonObject("result")));
				for (FactoryJsonObject jsonObject : recipe.getJsonArray("ingredients").asJsonObjectList()) {
					rec.addIngredient(computeResult(jsonObject));
				}
				Bukkit.addRecipe(rec);
				tags.add(rec.getKey().asString());
				taggedRegistry.put(rec.getKey().asString(), rec);
			} else if (type.equalsIgnoreCase("minecraft:crafting_shaped")) {
				ShapedRecipe rec = new ShapedRecipe(key, computeResult(recipe.getJsonObject("result")));
				rec.shape(Arrays.stream(recipe.getJsonArray("pattern").asArray()).map(FactoryElement::getString).toList().toArray(new String[0]));
				HashMap<String, FactoryJsonObject> map = new HashMap<>();
				if (recipe.isPresent("key")) {
					for (Object keyy : (recipe.getJsonObject("key")).keySet()) {
						String keyedObj = keyy.toString();
						if (((recipe.getJsonObject("key")).getElement(keyedObj).isJsonObject())) {
							map.put(keyedObj, recipe.getJsonObject("key").getElement(keyedObj).toJsonObject());
						}
					}
				}

				for (String T : map.keySet()) {
					FactoryJsonObject object = map.get(T);
					rec.setIngredient(T.charAt(0), computeResult(object));
				}

				Bukkit.addRecipe(rec);
				tags.add(rec.getKey().asString());
				taggedRegistry.put(rec.getKey().asString(), rec);
			} else {
				throw new IllegalArgumentException("Unable to get recipe type from power: " + recipePower.getTag());
			}
		}

		Bukkit.updateRecipes();
		finishedLoad = true;
	}

	public static ItemStack computeResult(FactoryJsonObject object) {
		long amt = object.getNumberOrDefault("count", 1L).getLong();
		String item = object.getString("item");
		if (item.contains(":")) {
			item = item.split(":")[1];
		}
		return new ItemStack(Material.valueOf(item.toUpperCase()), Math.toIntExact(amt));
	}

	public static String computeTag(Recipe recipe) {
		if (recipe instanceof ShapedRecipe ee) {
			return ee.key().asString();
		} else if (recipe instanceof ShapelessRecipe e) {
			return e.key().asString();
		}
		return null;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void load(ServerLoadEvent e) {
		Bukkit.getOnlinePlayers().forEach((pl) -> applyRecipePower(pl));
	}

	public void applyRecipePower(Player p) {
		if (!finishedLoad) return;
		if (recipeMapping.containsKey(p)) {
			recipeMapping.clear();
		}
		if (getPlayers().contains(p)) {
			for (RecipePower power : PowerHolderComponent.getPowers(p, RecipePower.class)) {
				FactoryJsonObject recipe = power.getRecipe();
				NamespacedKey idKey = CraftNamespacedKey.fromMinecraft(recipe.getString("id").contains(":") ? recipe.getResourceLocation("id") : ResourceLocation.parse(power.getTag() + "_" + recipe.getString("id")));
				String id = idKey.asString();
				if (taggedRegistry.containsKey(id)) {
					if (recipeMapping.containsKey(p)) {
						recipeMapping.get(p).add(id);
					} else {
						recipeMapping.put(p, new ArrayList<>(List.of(id)));
					}
				} else {
					throw new IllegalStateException("Unable to locate recipe id. Bug?");
				}
			}
		}
	}

	@EventHandler
	public void join(PlayerJoinEvent e) {
		applyRecipePower(e.getPlayer());
	}

	@EventHandler
	public void reload(OriginChangeEvent e) {
		applyRecipePower(e.getPlayer());
	}

	@EventHandler
	public void update(PowerUpdateEvent e) {
		if (e.getPower().getType().equalsIgnoreCase(getType()) && e.getPower().getTag().equalsIgnoreCase(getTag())) {
			applyRecipePower(e.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void craft(PrepareItemCraftEvent e) {
		boolean cancel = true;
		if (e.getRecipe() == null) return;
		String key = computeTag(e.getRecipe());
		if (key == null) return;
		if (recipeMapping.containsKey(e.getView().getPlayer())) {
			if (recipeMapping.get(e.getView().getPlayer()).contains(key)) {
				cancel = false;
			}
		}
		if (cancel && !key.startsWith("minecraft:")) { // Assumed to be a minecraft key if it has that namespace, so allow that to pass.
			if (key.equalsIgnoreCase("origins:orb_of_origins")) return;
			if (Bukkit.getRecipe(CraftNamespacedKey.fromMinecraft(ResourceLocation.parse(key))) != null) return;
			e.getInventory().setResult(null);
		}
	}

	public FactoryJsonObject getRecipe() {
		return recipe;
	}
}
