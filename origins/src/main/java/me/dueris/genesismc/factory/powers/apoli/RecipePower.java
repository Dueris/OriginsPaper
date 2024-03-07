package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.content.OrbOfOrigins;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipePower extends CraftPower implements Listener {

	public static HashMap<Player, List<String>> recipeMapping = new HashMap<>();
	public static HashMap<String, Recipe> taggedRegistry = new HashMap<>();
	public static List<String> tags = new ArrayList<>();
	private static boolean finishedLoad = false;

	public static void parseRecipes() {
		for (Power powerContainer : ((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).values().stream().filter(powerContainer -> powerContainer.getType().equalsIgnoreCase("apoli:recipe")).toList()) {
			JSONObject recipe = powerContainer.get("recipe");
			if (recipe == null)
				throw new IllegalArgumentException("Unable to find recipe data for power: " + powerContainer.getTag());
			NamespacedKey key = new NamespacedKey(recipe.get("id").toString().split(":")[0], recipe.get("id").toString().split(":")[1]);
			String type = recipe.get("type").toString();
			if (!type.startsWith("minecraft:")) {
				type = "minecraft:" + type;
			}
			if (type.equalsIgnoreCase("minecraft:crafting_shapeless")) {
				ShapelessRecipe rec = new ShapelessRecipe(key, computeResult((JSONObject) recipe.get("result")));
				for (Object object : ((JSONArray) recipe.get("ingredients"))) {
					if (object instanceof JSONObject jsonObject) {
						rec.addIngredient(computeResult(jsonObject));
					}
				}
				Bukkit.addRecipe(rec);
				tags.add(rec.key().asString());
				taggedRegistry.put(rec.key().asString(), rec);
			} else if (type.equalsIgnoreCase("minecraft:crafting_shaped")) {
				ShapedRecipe rec = new ShapedRecipe(key, computeResult((JSONObject) recipe.get("result")));
				rec.shape((String[]) ((JSONArray) recipe.get("pattern")).toArray(new String[0]));
				HashMap<String, JSONObject> map = new HashMap<>();
				if (recipe.containsKey("key")) {
					for (Object keyy : ((JSONObject) recipe.get("key")).keySet()) {
						String keyedObj = keyy.toString();
						if (((JSONObject) recipe.get("key")).get(keyedObj) instanceof JSONObject job) {
							map.put(keyedObj, job);
						}
					}
				}

				for (String T : map.keySet()) {
					JSONObject object = map.get(T);
					rec.setIngredient(T.charAt(0), computeResult(object));
				}

				Bukkit.addRecipe(rec);
				tags.add(rec.key().asString());
				taggedRegistry.put(rec.key().asString(), rec);
			} else {
				throw new IllegalArgumentException("Unable to get recipe type from power: " + powerContainer.getTag());
			}
		}

		Bukkit.updateRecipes();
		finishedLoad = true;
	}

	public static ItemStack computeResult(JSONObject object) {
		long amt = (long) object.getOrDefault("count", 1l);
		String item = object.get("item").toString();
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

	@Override
	public void run(Player p) {

	}
	// End

	@EventHandler
	public void load(ServerLoadEvent e) {
		parseRecipes();
		OrbOfOrigins.init();
		Bukkit.getOnlinePlayers().forEach((pl) -> applyRecipePower(pl));
	}

	// From PowerContainer
	public List<JSONObject> getJsonListSingularPlural(String singular, String plural, JSONObject object) {
		Object obj = object.get(singular);
		if (obj == null) {
			obj = object.get(plural);
		}

		List<JSONObject> result = new ArrayList<>();

		if (obj instanceof JSONArray jsonArray) {
			for (Object item : jsonArray) {
				if (item instanceof JSONObject jsonObject) {
					result.add(jsonObject);
				}
			}
		} else if (obj instanceof JSONObject jsonObject) {
			result.add(jsonObject);
		}
		return result;
	}

	public void applyRecipePower(Player p) {
		if (!finishedLoad) return;
		if (recipeMapping.containsKey(p)) {
			recipeMapping.clear();
		}
		if (getPowerArray().contains(p)) {
			for (Layer layer : CraftApoli.getLayersFromRegistry()) {
				for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
					JSONObject recipe = power.get("recipe");
					String id = recipe.get("id").toString();
					if (taggedRegistry.keySet().contains(id)) {
						if (recipeMapping.containsKey(p)) {
							recipeMapping.get(p).add(id);
						} else {
							List<String> put = new ArrayList<>();
							put.add(id);
							recipeMapping.put(p, put);
						}
					} else {
						throw new IllegalStateException("Unable to locate recipe id. Bug?");
					}
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
		if (e.getPower().getType().equalsIgnoreCase(getPowerFile())) {
			applyRecipePower(e.getPlayer());
		}
	}

	@EventHandler
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
			e.getInventory().setResult(null);
		}
	}

	@Override
	public String getPowerFile() {
		return "apoli:recipe";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return recipe;
	}

	@Override
	public void setActive(Player p, String tag, Boolean bool) {
		if (powers_active.containsKey(p)) {
			if (powers_active.get(p).containsKey(tag)) {
				powers_active.get(p).replace(tag, bool);
			} else {
				powers_active.get(p).put(tag, bool);
			}
		} else {
			powers_active.put(p, new HashMap());
			setActive(p, tag, bool);
		}
	}


}
