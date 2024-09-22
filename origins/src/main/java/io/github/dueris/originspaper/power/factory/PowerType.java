package io.github.dueris.originspaper.power.factory;

import com.google.gson.JsonObject;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.util.annotations.SourceProvider;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import io.github.dueris.originspaper.util.ComponentUtil;
import io.github.dueris.originspaper.util.LangFile;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PowerType implements Listener {
	public static Map<ResourceLocation, PowerType> REGISTRY;
	static Map<ResourceLocation, Class<? extends PowerType>> type2Class = new HashMap<>();
	private final ResourceLocation key;
	private final @NotNull TextComponent name;
	private final @NotNull TextComponent description;
	private final boolean hidden;
	private final ConditionTypeFactory<Entity> condition;
	private final int loadingPriority;
	private final String cachedTagString;
	private final String cachedTypeString;
	private final ConcurrentLinkedQueue<Player> players = new ConcurrentLinkedQueue<>();
	public @SourceProvider JsonObject sourceObject;
	private boolean hasPlayers = false;

	public PowerType(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority) {
		this.key = key;
		this.cachedTagString = key.toString();
		this.cachedTypeString = type.toString();
		this.name = ComponentUtil.nmsToKyori(
			LangFile.translatable((name != null ? name.getString() : "power.$namespace.$path.name")
				.replace("$namespace", key.getNamespace()).replace("$path", key.getPath()))
		);
		this.description = ComponentUtil.nmsToKyori(
			LangFile.translatable((description != null ? description.getString() : "power.$namespace.$path.description")
				.replace("$namespace", key.getNamespace()).replace("$path", key.getPath()))
		);
		this.hidden = hidden;
		this.condition = condition;
		this.loadingPriority = loadingPriority;

		if (REGISTRY == null) {
			REGISTRY = new ConcurrentHashMap<>();
		}
		REGISTRY.put(this.key, this);
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("simple"), SerializableData.serializableData()
			.add("type", SerializableDataTypes.IDENTIFIER)
			.add("name", SerializableDataTypes.TEXT, null)
			.add("description", SerializableDataTypes.TEXT, null)
			.add("hidden", SerializableDataTypes.BOOLEAN, false)
			.add("condition", ApoliDataTypes.ENTITY_CONDITION, null)
			.add("loading_priority", SerializableDataTypes.INT, 0));
	}

	@SuppressWarnings("unchecked")
	public static void register() {
		List<Class<? extends PowerType>> powerTypeSubclasses = new ArrayList<>();
		powerTypeSubclasses.add(PowerType.class);

		try (ScanResult scanResult = new ClassGraph()
			.acceptPackages("io.github.dueris.originspaper.power.type")
			.enableClassInfo()
			.scan()) {

			scanResult.getSubclasses(PowerType.class.getName())
				.forEach(classInfo -> {
					try {
						Class<?> clazz = Class.forName(classInfo.getName());
						powerTypeSubclasses.add((Class<? extends PowerType>) clazz);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				});
		}

		for (Class<? extends PowerType> powerType : powerTypeSubclasses) {
			try {
				PowerTypeFactory factory = (PowerTypeFactory) powerType.getDeclaredMethod("getFactory").invoke(null);
				register(factory, powerType);
			} catch (NoSuchMethodException ignored) {
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException("Unable to invoke PowerTypeFactory for '{}'!".replace("{}", powerType.getName()), e);
			}
		}
	}

	private static void register(@NotNull PowerTypeFactory factory, Class<? extends PowerType> type) {
		type2Class.put(factory.id, type);
		Registry.register(ApoliRegistries.POWER_TYPE_FACTORY, factory.id, factory);
	}

	public ResourceLocation getId() {
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

	public ConditionTypeFactory<Entity> getCondition() {
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
	public CompoundTag saveData(ServerPlayer player) {
		return null;
	}

	/**
	 * Allows parsing the data provided from the player repository for that power
	 */
	public void loadFromData(@NotNull CompoundTag tag, ServerPlayer player) {
	}
}
