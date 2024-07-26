package me.dueris.originspaper.factory.powers.holder;

import com.google.gson.JsonObject;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import me.dueris.calio.CraftCalio;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.FactoryHolder;
import me.dueris.calio.data.annotations.SourceProvider;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.powers.apoli.provider.OriginSimpleContainer;
import me.dueris.originspaper.factory.powers.apoli.provider.origins.*;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PowerType implements FactoryHolder, Listener {
	public static ConcurrentLinkedQueue<Class<? extends PowerType>> INSTANCE_TYPES = new ConcurrentLinkedQueue<>();
	private final String name;
	private final String description;
	private final boolean hidden;
	private final FactoryJsonObject condition;
	private final int loadingPriority;
	private final ConcurrentLinkedQueue<CraftPlayer> players = new ConcurrentLinkedQueue<>();
	private final List<FactoryJsonObject> conditions = new ArrayList<>();
	@SourceProvider
	public JsonObject sourceObject;
	protected boolean tagSet = false;
	private ResourceLocation tag = null;
	private String cachedTagString = null;
	private boolean hasPlayers = false;

	public PowerType(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		this.name = name;
		this.description = description;
		this.hidden = hidden;
		this.condition = condition;
		this.loadingPriority = loading_priority;
		this.addCondition(condition);
	}

	public static FactoryData registerComponents(@NotNull FactoryData data) {
		return data.add("name", String.class, "power.$namespace.$path.name")
			.add("description", String.class, "power.$namespace.$path.description")
			.add("hidden", boolean.class, false)
			.add("condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("loading_priority", int.class, 1);
	}

	public static void registerAll() {
		List<Class<FactoryHolder>> holders = new ArrayList<>();

		try {
			ScanResult result = new ClassGraph().whitelistPackages("me.dueris.originspaper.factory.powers").enableClassInfo().scan();

			try {
				holders.addAll(
					result.getSubclasses(PowerType.class)
						.loadClasses(FactoryHolder.class)
						.stream()
						.filter(clz -> !clz.isAnnotation() && !clz.isInterface() && !clz.isEnum())
						.toList()
				);
			} catch (Throwable var5) {
				if (result != null) {
					try {
						result.close();
					} catch (Throwable var4) {
						var5.addSuppressed(var4);
					}
				}

				throw var5;
			}

			if (result != null) {
				result.close();
			}
		} catch (Exception var6) {
			System.out.println("This would've been a zip error :P. Please tell us on discord if you see this ^-^");
		}

		holders.forEach(CraftCalio.INSTANCE::register);
		OriginSimpleContainer.registerPower(BounceSlimeBlock.class);
		OriginSimpleContainer.registerPower(LikeWater.class);
		OriginSimpleContainer.registerPower(PiglinNoAttack.class);
		OriginSimpleContainer.registerPower(ScareCreepers.class);
		OriginSimpleContainer.registerPower(WaterBreathe.class);
		OriginSimpleContainer.registerPower(SlimelingSizeChangers.AddSize.class);
		OriginSimpleContainer.registerPower(SlimelingSizeChangers.RemoveSize.class);
		Bukkit.getServer().getPluginManager().registerEvents(new SlimelingSizeChangers(), OriginsPaper.getPlugin());
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public FactoryJsonObject getCondition() {
		return this.condition;
	}

	public int getLoadingPriority() {
		return this.loadingPriority;
	}

	public void tick(Player player) {
	}

	public void tickAsync(Player player) {
	}

	public void tick() {
	}

	public void bootstrapUnapply(Player player) {
	}

	public void bootstrapApply(Player player) {
	}

	public String getType() {
		try {
			return ((FactoryData) this.getClass().getDeclaredMethod("registerComponents", FactoryData.class).invoke(null, new FactoryData()))
				.getIdentifier()
				.toString();
		} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException var2) {
			throw new RuntimeException("Unable to invoke type-getters!", var2);
		}
	}

	public ConcurrentLinkedQueue<CraftPlayer> getPlayers() {
		return this.players;
	}

	public String getTag() {
		return this.cachedTagString;
	}

	public boolean hasPlayers() {
		return this.hasPlayers;
	}

	public void forPlayer(Player player) {
		this.hasPlayers = true;
		this.players.add((CraftPlayer) player);
	}

	public void removePlayer(Player player) {
		this.players.remove((CraftPlayer) player);
		this.hasPlayers = !this.players.isEmpty();
	}

	public boolean isActive(Player player) {
		return this.conditions.stream().allMatch(condition -> ConditionExecutor.testEntity(condition, (CraftEntity) player));
	}

	public void addCondition(FactoryJsonObject condition) {
		this.conditions.add(condition);
	}

	public PowerType ofResourceLocation(ResourceLocation key) {
		if (this.tagSet) {
			return this;
		} else {
			this.tagSet = true;
			this.tag = key;
			this.cachedTagString = key.toString();
			return this;
		}
	}

	@Override
	public ResourceLocation key() {
		return this.tag;
	}
}
