package me.dueris.originspaper.registry.registries;

import com.google.gson.JsonObject;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.util.annotations.SourceProvider;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import me.dueris.originspaper.factory.powers.SimplePower;
import me.dueris.originspaper.factory.powers.provider.OriginSimpleContainer;
import me.dueris.originspaper.factory.powers.provider.origins.*;
import me.dueris.originspaper.util.LangFile;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

	public static InstanceDefiner buildDefiner() {
		return InstanceDefiner.instanceDefiner()
			.add("type", SerializableDataTypes.IDENTIFIER)
			.add("name", SerializableDataTypes.TEXT, null)
			.add("description", SerializableDataTypes.TEXT, null)
			.add("hidden", SerializableDataTypes.BOOLEAN, false)
			.add("condition", ApoliDataTypes.ENTITY_CONDITION, null)
			.add("loading_priority", SerializableDataTypes.INT, 0);
	}

	public static void registerAll() {
		List<Class<? extends PowerType>> holders = new ArrayList<>();

		try {
			ScanResult result = new ClassGraph().whitelistPackages("me.dueris.originspaper.factory.powers").enableClassInfo().scan();

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
		OriginSimpleContainer.registerPower(BounceSlimeBlock.class);
		OriginSimpleContainer.registerPower(LikeWater.class);
		OriginSimpleContainer.registerPower(PiglinNoAttack.class);
		OriginSimpleContainer.registerPower(ScareCreepers.class);
		OriginSimpleContainer.registerPower(WaterBreathe.class);
		OriginSimpleContainer.registerPower(SlimelingSizeChangers.AddSize.class);
		OriginSimpleContainer.registerPower(SlimelingSizeChangers.RemoveSize.class);
		Bukkit.getServer().getPluginManager().registerEvents(new SlimelingSizeChangers(), OriginsPaper.getPlugin());
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

	public boolean isActive(@NotNull Player player) {
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

	public void onBootstrap() {
	}
}
