package me.dueris.genesismc.factory.powers.holder;

import com.google.gson.JsonObject;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import me.dueris.calio.CraftCalio;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.FactoryHolder;
import me.dueris.calio.data.annotations.Register;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.apoli.provider.OriginSimpleContainer;
import me.dueris.genesismc.factory.powers.apoli.provider.origins.*;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PowerType implements Serializable, FactoryHolder, Listener {
	@Serial
	private static final long serialVersionUID = 2L;
	public static List<NamespacedKey> allowedSkips = new ArrayList<>();
	public static List<NamespacedKey> notPossibleTypes = new ArrayList<>();

	static {
		allowedSkips.add(new NamespacedKey("apoli", "model_color"));
		notPossibleTypes.add(new NamespacedKey("apoli", "lava_vision")); // Not possible
		notPossibleTypes.add(new NamespacedKey("apoli", "shader")); // Not possible
		allowedSkips.add(new NamespacedKey("apoli", "modify_attribute")); // Not planned, use origins:attribute
		notPossibleTypes.add(new NamespacedKey("apoli", "prevent_feature_render")); // Not possible
		notPossibleTypes.add(new NamespacedKey("apoli", "modify_insomnia_ticks")); // Not possible
		notPossibleTypes.add(new NamespacedKey("apoli", "modify_slipperiness"));
	}

	private final String name;
	private final String description;
	private final boolean hidden;
	private final FactoryJsonObject condition;
	private final int loadingPriority;
	private final ConcurrentLinkedQueue<CraftPlayer> players = new ConcurrentLinkedQueue<>();
	private final List<FactoryJsonObject> conditions = new ArrayList<>();
	protected boolean tagSet = false;
	private NamespacedKey tag = null;
	private String cachedTagString = null;
	private boolean hasPlayers = false;

	@Register
	public PowerType(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		this.name = name;
		this.description = description;
		this.hidden = hidden;
		this.condition = condition;
		this.loadingPriority = loading_priority;
		this.addCondition(condition);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return data.add("name", String.class, "craftapoli.name.not_found")
			.add("description", String.class, "craftapoli.description.not_found")
			.add("hidden", boolean.class, false)
			.add("condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("loading_priority", int.class, 1);
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

		// Apoli-Simple
		OriginSimpleContainer.registerPower(BounceSlimeBlock.class);
		OriginSimpleContainer.registerPower(LikeWater.class);
		OriginSimpleContainer.registerPower(NoCobWebSlowdown.class);
		OriginSimpleContainer.registerPower(PiglinNoAttack.class);
		OriginSimpleContainer.registerPower(ScareCreepers.class);
		OriginSimpleContainer.registerPower(WaterBreathe.class);
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

	public void tick(Player player) {
	}

	public void tickAsync(Player player) {
	}

	public void tick() {
	}

	public String getType() {
		try {
			return ((FactoryData) getClass().getDeclaredMethod("registerComponents", FactoryData.class).invoke(null, new FactoryData())).getIdentifier().asString();
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException("Unable to invoke type-getters!", e);
		}
	}

	public ConcurrentLinkedQueue<CraftPlayer> getPlayers() {
		return players;
	}

	public String getTag() {
		return cachedTagString;
	}

	public boolean hasPlayers() {
		return hasPlayers;
	}

	public void forPlayer(Player player) {
		this.hasPlayers = true;
		this.players.add((CraftPlayer) player);
	}

	public void removePlayer(Player player) {
		this.hasPlayers = false;
		this.players.remove((CraftPlayer) player);
	}

	public boolean isActive(Player player) {
		return conditions.stream().allMatch(condition -> ConditionExecutor.testEntity(condition, (CraftEntity) player));
	}

	public void addCondition(FactoryJsonObject condition) {
		this.conditions.add(condition);
	}

	@Override
	public PowerType ofResourceLocation(NamespacedKey key) {
		if (this.tagSet) return this;
		tagSet = true;
		this.tag = key;
		this.cachedTagString = key.asString();
		return this;
	}

	@Override
	public NamespacedKey getKey() {
		return this.tag;
	}
}
