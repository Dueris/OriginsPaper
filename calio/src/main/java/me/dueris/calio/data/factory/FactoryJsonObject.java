package me.dueris.calio.data.factory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FactoryJsonObject {
	public JsonObject handle;

	public FactoryJsonObject(JsonObject object) {
		this.handle = object;
	}

	public boolean isEmpty() {
		return this.handle.isEmpty();
	}

	public FactoryJsonObject getJsonObject(String key) {
		return isPresent(key) ? new FactoryJsonObject(this.handle.get(key).getAsJsonObject()) : new FactoryJsonObject(new JsonObject());
	}

	public FactoryJsonArray getJsonArray(String key) {
		return isPresent(key) ? new FactoryJsonArray(this.handle.get(key).getAsJsonArray()) : new FactoryJsonArray(new JsonArray());
	}

	public boolean isJsonObject(String key) {
		return this.handle.get(key).isJsonObject();
	}

	public boolean isJsonArray(String key) {
		return this.handle.get(key).isJsonArray();
	}

	public boolean isGsonPrimative(String key) {
		return this.handle.get(key).isJsonPrimitive();
	}

	public FactoryNumber getNumber(String key) {
		return new FactoryNumber(this.handle.get(key).getAsJsonPrimitive());
	}

	public FactoryNumber getNumberOrDefault(String key, Number number) {
		return new FactoryNumber(this.isPresent(key) ? this.handle.get(key).getAsJsonPrimitive() : new JsonPrimitive(number));
	}

	public <T extends Enum<T>> T getEnumValue(String key, Class<T> enumClass) {
		String value = this.handle.get(key).getAsString().toLowerCase();
		return getEV(enumClass, value, null);
	}

	public <T extends Enum<T>> T getEnumValueOrDefault(String key, Class<T> enumClass, T def) {
		if (!this.handle.has(key)) return def;
		String value = this.handle.get(key).getAsString().toLowerCase();
		return getEV(enumClass, value, def);
	}

	public <T extends Enum<T>> T getEnumValue(String key, Class<T> enumClass, boolean checkNamespace) {
		String value = this.handle.get(key).getAsString().toLowerCase();
		if (checkNamespace && value.contains(":")) {
			value = value.split(":")[1];
		}
		return getEV(enumClass, value, null);
	}

	private <T extends Enum<T>> T getEV(Class<T> enumClass, String value, T def) {
		T[] enumConstants = enumClass.getEnumConstants();
		for (T enumValue : enumConstants) {
			if (enumValue.toString().toLowerCase().equalsIgnoreCase(value)) {
				return enumValue;
			}
		}
		if (def == null) {
			throw new IllegalArgumentException("Provided JsonValue from key \"{key}\" was not an instanceof enum \"{enum}\""
				.replace("{key}", value).replace("{enum}", enumClass.getSimpleName()));
		} else return def;
	}

	public boolean getBoolean(String key) {
		return this.handle.get(key).getAsJsonPrimitive().getAsBoolean();
	}

	public String getString(String key) {
		return this.handle.get(key).getAsString();
	}

	public boolean isPresent(String key) {
		return this.handle.has(key);
	}

	public boolean getBooleanOrDefault(String key, boolean def) {
		return isPresent(key) ? getBoolean(key) : def;
	}

	public String getStringOrDefault(String key, String def) {
		return isPresent(key) ? getString(key) : def;
	}

	public FactoryElement getElement(String key) {
		return new FactoryElement(this.handle.get(key));
	}

	public Set<String> keySet() {
		return this.handle.keySet();
	}

	public List<FactoryElement> values() {
		return this.handle.asMap().values().stream().map(FactoryElement::fromJson).collect(Collectors.toUnmodifiableList());
	}

	private NamespacedKey getBukkitNamespacedKey(String key) {
		return NamespacedKey.fromString(this.getString(key));
	}

	public ResourceLocation getResourceLocation(String key) {
		return ResourceLocation.parse(this.getString(key));
	}

	public Material getMaterial(String key) {
		NamespacedKey a = this.getBukkitNamespacedKey(key);
		return Material.matchMaterial(a.asString());
	}

	public <T> TagKey<T> getTagKey(String key, ResourceKey<Registry<T>> registry) {
		return TagKey.create(registry, this.getResourceLocation(key));
	}

	public ItemStack getItemStack(String key) {
		FactoryElement inst = this.getElement(key);
		if (inst != null && inst.isJsonObject()) {
			FactoryJsonObject obj = inst.toJsonObject();
			String materialVal = "player_head";
			int amt = 1;
			if (obj.isPresent("item")) {
				materialVal = obj.getString("item");
			}
			if (obj.isPresent("amount")) {
				amt = obj.getNumber("amount").getInt();
			}
			return new ItemStack(Material.valueOf(NamespacedKey.fromString(materialVal).asString().split(":")[1].toUpperCase()), amt);
		}
		if (inst != null && inst.isString()) return new ItemStack(this.getMaterial(key));
		return new ItemStack(Material.PLAYER_HEAD, 1);
	}

	public PotionEffectType getPotionEffectType(String key) {
		return PotionEffectType.getByKey(getBukkitNamespacedKey(key));
	}

	public ItemStack asItemStack() {
		String materialVal = "player_head";
		int amt = 1;
		if (this.isPresent("item")) {
			materialVal = this.getString("item");
		}
		if (this.isPresent("amount")) {
			amt = this.getNumber("amount").getInt();
		}
		return new ItemStack(Material.valueOf(NamespacedKey.fromString(materialVal).asString().split(":")[1].toUpperCase()), amt);
	}

	public <T> T transformWithCalio(String key, Function<JsonElement, T> transformer) {
		return transformWithCalio(key, transformer, null);
	}

	public <T> T transformWithCalio(String key, Function<JsonElement, T> transformer, T def) {
		if (!isPresent(key)) return def;
		return transformer.apply(this.handle.get(key));
	}
}
