package me.dueris.genesismc.factory.powers.holder;

import com.google.gson.JsonObject;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import me.dueris.calio.CraftCalio;
import me.dueris.calio.builder.inst.FactoryData;
import me.dueris.calio.builder.inst.FactoryHolder;
import me.dueris.calio.builder.inst.annotations.Register;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PowerType implements FactoryHolder, Listener {
	private final String name;
	private final String description;
	private final boolean hidden;
	private final FactoryJsonObject condition;
	private final int loadingPriority;
	private final ConcurrentLinkedQueue<CraftPlayer> players = new ConcurrentLinkedQueue<>();
	protected boolean tagSet = false;
	private NamespacedKey tag = null;

	@Register
	public PowerType(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		this.name = name;
		this.description = description;
		this.hidden = hidden;
		this.condition = condition;
		this.loadingPriority = loading_priority;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return data.add("name", String.class, "craftapoli.name.not_found")
			.add("description", String.class, "craftapoli.description.not_found")
			.add("hidden", boolean.class, (boolean) false)
			.add("condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("loading_priority", int.class, (int) 1);
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public boolean isHidden() {
		return hidden;
	}

	public FactoryJsonObject getCondition() {
		return condition;
	}

	public int getLoadingPriority() {
		return loadingPriority;
	}

	public void tick() {}

	public ConcurrentLinkedQueue<CraftPlayer> getPlayers() {
		return players;
	}

	public void forPlayer(Player player) {
		this.players.add((CraftPlayer) player);
	}

	public static void registerAll() {
		List<Class<FactoryHolder>> holders = new ArrayList<>();
		try (ScanResult result = new ClassGraph().whitelistPackages("me.dueris.genesismc.factory.powers").enableClassInfo().scan()) {
			holders.addAll(result.getSubclasses(PowerType.class).loadClasses(FactoryHolder.class).stream().filter(clz -> {
				return !clz.isAnnotation() && !clz.isInterface() && !clz.isEnum();
			}).toList());
		} catch (Exception e) {
			e.printStackTrace();
		}

		holders.forEach(CraftCalio.INSTANCE::register);
	}

	@Override
	public NamespacedKey ofResourceLocation(NamespacedKey key) {
		if (this.tagSet) return this.tag;
		tagSet = true;
		this.tag = key;
		return key;
	}

	@Override
	public NamespacedKey getKey() {
		return this.tag;
	}
}
