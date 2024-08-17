package io.github.dueris.originspaper.power;

import com.google.gson.JsonObject;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.calio.util.annotations.SourceProvider;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.provider.OriginSimpleContainer;
import io.github.dueris.originspaper.power.provider.origins.*;
import io.github.dueris.originspaper.util.LangFile;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PowerType implements Listener {
	public static ConcurrentLinkedQueue<Class<? extends PowerType>> INSTANCE_TYPES = new ConcurrentLinkedQueue<>();
	public static Class<? extends PowerType> DEFAULT_TYPE = SimplePower.class;
	private final ResourceLocation key;
	private final @NotNull TextComponent name;
	private final @NotNull TextComponent description;
	private final boolean hidden;
	private final ConditionFactory<Entity> condition;
	private final int loadingPriority;
	private final String cachedTagString;
	private final String cachedTypeString;
	private final ConcurrentLinkedQueue<Player> players = new ConcurrentLinkedQueue<>();
	public @SourceProvider JsonObject sourceObject;
	private boolean hasPlayers = false;

	public PowerType(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority) {
		this.key = key;
		this.cachedTagString = key.toString();
		this.cachedTypeString = type.toString();
		this.name = net.kyori.adventure.text.Component.text(
			LangFile.transform((name != null ? name.getString() : "power.$namespace.$path.name")
				.replace("$namespace", key.getNamespace()).replace("$path", key.getPath()))
		);
		this.description = net.kyori.adventure.text.Component.text(
			LangFile.transform((description != null ? description.getString() : "power.$namespace.$path.description")
				.replace("$namespace", key.getNamespace()).replace("$path", key.getPath()))
		);
		this.hidden = hidden;
		this.condition = condition;
		this.loadingPriority = loadingPriority;
		Bukkit.getPluginManager().registerEvents(this, OriginsPaper.getPlugin());
	}

	public static SerializableData buildFactory() {
		return SerializableData.serializableData()
			.add("type", SerializableDataTypes.IDENTIFIER)
			.add("name", SerializableDataTypes.TEXT, null)
			.add("description", SerializableDataTypes.TEXT, null)
			.add("hidden", SerializableDataTypes.BOOLEAN, false)
			.add("condition", ApoliDataTypes.ENTITY_CONDITION, null)
			.add("loading_priority", SerializableDataTypes.INT, 0);
	}

	public static void registerAll() {
		List<Class<? extends PowerType>> holders = new ArrayList<>() {
			@Override
			public boolean addAll(@NotNull Collection<? extends Class<? extends PowerType>> c) {
				for (Class<?> clazz : c) {
					try {
						Class.forName(clazz.getName(), true, clazz.getClassLoader());
					} catch (ClassNotFoundException e) {
						System.err.println("Error Preloading a powertype!");
						e.printStackTrace();
					}
				}
				return super.addAll(c);
			}
		};

		try {
			ScanResult result = new ClassGraph().whitelistPackages("io.github.dueris.originspaper.power").enableClassInfo().scan();

			try {
				holders.addAll(
					result.getSubclasses(PowerType.class)
						.loadClasses(PowerType.class)
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

			result.close();
		} catch (Exception var6) {
			System.out.println("This would've been a zip error :P. Please tell us on discord if you see this ^-^");
		}

		INSTANCE_TYPES.addAll(holders);
		OriginSimpleContainer.registerPower(LikeWater.class);
		OriginSimpleContainer.registerPower(ScareCreepers.class);
		OriginSimpleContainer.registerPower(WaterBreathe.class);
	}

	public ResourceLocation key() {
		return key;
	}

	public String getType() {
		return cachedTypeString;
	}

	public @NotNull TextComponent name() {
		return name;
	}

	public @NotNull TextComponent description() {
		return description;
	}

	public boolean isHidden() {
		return hidden;
	}

	public ConditionFactory<Entity> getCondition() {
		return condition;
	}

	public int getLoadingPriority() {
		return loadingPriority;
	}

	public ConcurrentLinkedQueue<Player> getPlayers() {
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
		this.players.add(player);
	}

	public void removePlayer(Player player) {
		this.players.remove(player);
		this.hasPlayers = !this.players.isEmpty();
	}

	public boolean isActive(@NotNull Entity player) {
		if (!(player instanceof Player) || !getPlayers().contains(player)) return false;
		return condition == null || condition.test(player);
	}

	public void tick(Player player) {
	}

	public void tickAsync(Player player) {
	}

	public void tick() {
	}

	public void onRemoved(Player player) {
	}

	public void onAdded(Player player) {
	}

	public void onGained(Player player) {
	}

	public void onLost(Player player) {
	}

	public void onBootstrap() {
	}

	public boolean shouldTick() {
		return true;
	}

	/**
	 * Allows for saving data in the player repository for that power
	 */
	@Nullable
	public CompoundTag saveData() {
		return null;
	}

	/**
	 * Allows parsing the data provided from the player repository for that power
	 */
	public void loadFromData(@NotNull CompoundTag tag) {
	}
}
