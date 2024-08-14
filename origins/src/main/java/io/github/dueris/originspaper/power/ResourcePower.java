package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.util.holder.ObjectProvider;
import io.github.dueris.calio.util.holder.Pair;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import io.github.dueris.originspaper.data.types.HudRender;
import io.github.dueris.originspaper.event.PowerUpdateEvent;
import io.github.dueris.originspaper.registry.Registries;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BinaryOperator;

public class ResourcePower extends PowerType implements ResourceInterface {
	public static HashMap<String, Bar> serverLoadedBars = new HashMap<>();
	public static HashMap<Player, List<Bar>> currentlyDisplayed = new HashMap<>();

	static {
		OriginsPaper.preShutdownTasks.add(() -> {
			serverLoadedBars.values().stream().filter(Objects::nonNull).forEach(Bar::delete);
			currentlyDisplayed.forEach((player, list) -> list.forEach(Bar::delete));
		});
	}

	protected final int min;
	protected final int max;
	protected final int startValue;
	protected final ActionFactory<Entity> minAction;
	protected final ActionFactory<Entity> maxAction;
	protected final HudRender hudRender;

	public ResourcePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
						 int min, int max, Optional<Integer> startValue, ActionFactory<Entity> minAction, ActionFactory<Entity> maxAction, HudRender hudRender) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.min = min;
		this.max = max;
		this.startValue = ((ObjectProvider<Integer>) () -> startValue.isPresent() ? startValue.get() : min).get();
		this.minAction = minAction;
		this.maxAction = maxAction;
		this.hudRender = hudRender;
	}

	public static InstanceDefiner buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("resource"))
			.add("min", SerializableDataTypes.INT)
			.add("max", SerializableDataTypes.INT)
			.add("start_value", SerializableDataTypes.optional(SerializableDataTypes.INT), Optional.empty())
			.add("min_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("max_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER);
	}

	public static Optional<Bar> getDisplayedBar(@NotNull Entity player, String identifier) {
		currentlyDisplayed.putIfAbsent((Player) player.getBukkitEntity(), new ArrayList<>());

		for (Bar bar : currentlyDisplayed.get((Player) player.getBukkitEntity())) {
			if (bar.power.getTag().equalsIgnoreCase(identifier)) {
				return Optional.of(bar);
			}
		}

		return Optional.empty();
	}

	protected static @NotNull KeyedBossBar createRender(String title, double currentProgress, final @NotNull ResourceInterface power, final Player player) {
		ResourceLocation f = ResourceLocation.parse(power.getTag() + "_bar_server_loaded");
		final KeyedBossBar bossBar = Bukkit.createBossBar(
			player == null ? CraftNamespacedKey.fromMinecraft(f) : NamespacedKey.fromString(power.getTag() + "_bar_" + player.getName().toLowerCase()),
			title,
			Bar.getBarColor(power.getHudRender()),
			BarStyle.SEGMENTED_6
		);
		bossBar.setProgress(currentProgress);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!power.getHudRender().shouldRender()) {
					bossBar.setVisible(false);
					this.cancel();
				} else {
					if (power.getHudRender().condition() != null) {
						if (player == null) return;
						bossBar.setVisible(power.getHudRender().condition().test(((CraftPlayer) player).getHandle()));
					} else {
						bossBar.setVisible(true);
					}
				}
			}
		}.runTaskTimer(OriginsPaper.getPlugin(), 0, 1);
		return bossBar;
	}

	@Deprecated(forRemoval = true)
	private static <T extends Number> @NotNull Map<String, BinaryOperator<T>> createOperationMappings(
		BinaryOperator<T> addition,
		BinaryOperator<T> subtraction,
		BinaryOperator<T> multiplication,
		BinaryOperator<T> division,
		BinaryOperator<T> multiplyBase,
		BinaryOperator<T> multiplyTotal,
		BinaryOperator<T> multiplyTotalAddictive,
		BinaryOperator<T> minBase,
		BinaryOperator<T> maxBase) {

		Map<String, BinaryOperator<T>> operationMap = new HashMap<>();
		operationMap.put("addition", addition);
		operationMap.put("add", addition);
		operationMap.put("add_value", addition);
		operationMap.put("subtract_value", addition);
		operationMap.put("subtraction", subtraction);
		operationMap.put("subtract", subtraction);
		operationMap.put("multiplication", multiplication);
		operationMap.put("multiply", multiplication);
		operationMap.put("division", division);
		operationMap.put("divide", division);
		operationMap.put("multiply_base", multiplyBase);
		operationMap.put("multiply_total", multiplyTotal);
		operationMap.put("set_total", (a, b) -> b);
		operationMap.put("set", (a, b) -> b);
		operationMap.put("add_base_early", addition);
		operationMap.put("multiply_base_additive", multiplyBase);
		operationMap.put("multiply_base_multiplicative", multiplyTotal);
		operationMap.put("add_base_late", addition);
		operationMap.put("multiply_total_additive", multiplyTotalAddictive);
		operationMap.put("multiply_total_multiplicative", multiplyTotal);
		operationMap.put("min_base", minBase);
		operationMap.put("max_base", maxBase);
		operationMap.put("min_total", minBase);
		operationMap.put("max_total", maxBase);
		return operationMap;
	}

	@Deprecated(forRemoval = true)
	public static @NotNull Map<String, BinaryOperator<Double>> getOperationMappingsDouble() {
		return createOperationMappings(
			Double::sum,
			(a, b) -> a - b,
			(a, b) -> a * b,
			(a, b) -> a / b,
			(a, b) -> a + a * b,
			(a, b) -> a * (1.0 + b),
			(a, b) -> a * a * b,
			(a, b) -> a > b ? a : b,
			(a, b) -> a < b ? a : b
		);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void preLoad(ServerLoadEvent e) {
		OriginsPaper.getPlugin()
			.registry
			.retrieve(Registries.CRAFT_POWER)
			.values()
			.stream()
			.filter(p -> p.getType().equalsIgnoreCase(this.getType()))
			.forEach(power -> {
				Bar bar = new Bar((ResourcePower) power, null);
				serverLoadedBars.put(power.getTag(), bar);
			});

		for (Player player : Bukkit.getOnlinePlayers()) {
			PowerHolderComponent.getPowers(player, ResourcePower.class).forEach(power -> this.powerAdd(new PowerUpdateEvent(player, power, false, false)));
		}
	}

	@EventHandler
	public void powerAdd(@NotNull PowerUpdateEvent e) {
		if (e.getPower() instanceof ResourcePower && e.getPower().getTag().equalsIgnoreCase(this.getTag())) {
			currentlyDisplayed.putIfAbsent(e.getPlayer(), new ArrayList<>());
			if (!e.isRemoved()) {
				if (serverLoadedBars.containsKey(e.getPower().getTag())) {
					Bar displayed = serverLoadedBars.get(e.getPower().getTag());
					if (!currentlyDisplayed.get(e.getPlayer()).contains(displayed) &&
						!currentlyDisplayed.get(e.getPlayer()).stream().map(Bar::getPower).toList().contains(displayed.power)) {
						currentlyDisplayed.get(e.getPlayer()).add(displayed.cloneForPlayer(e.getPlayer()));
					}
				}
			} else if (currentlyDisplayed.containsKey(e.getPlayer())) {
				Bar cD = getDisplayedBar(((CraftPlayer) e.getPlayer()).getHandle(), e.getPower().getTag()).orElse(null);
				if (cD == null) {
					return;
				}

				cD.delete();
			}
		}
	}

	@EventHandler
	public void leave(@NotNull PlayerQuitEvent e) {
		Player p = e.getPlayer();
		StringBuilder cooldownBuilder = new StringBuilder();
		cooldownBuilder.append("[");
		CooldownPower.cooldowns.putIfAbsent(p, new ArrayList<>());

		for (Pair<KeyedBossBar, ResourceInterface> barPair : CooldownPower.cooldowns.get(p)) {
			cooldownBuilder.append(barPair.first().getKey().asString() + "<::>" + barPair.first().getProgress());
			cooldownBuilder.append(",");
		}

		cooldownBuilder.append("]");
		p.getPersistentDataContainer()
			.set(
				CraftNamespacedKey.fromMinecraft(OriginsPaper.apoliIdentifier("current_cooldowns")),
				PersistentDataType.STRING,
				new String(cooldownBuilder).replace(",]", "]")
			);
		if (currentlyDisplayed.containsKey(p)) {
			currentlyDisplayed.get(p).forEach(Bar::delete);
			currentlyDisplayed.get(p).clear();
		}

		if (CooldownPower.cooldowns.containsKey(p)) {
			CooldownPower.cooldowns.get(p).forEach(pair -> Bukkit.getServer().removeBossBar(pair.first().getKey()));
			CooldownPower.cooldowns.get(p).clear();
		}
	}

	@EventHandler
	public void join(@NotNull PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (p.getPersistentDataContainer().has(CraftNamespacedKey.fromMinecraft(OriginsPaper.apoliIdentifier("current_cooldowns")))) {
			String encoded = p.getPersistentDataContainer()
				.get(CraftNamespacedKey.fromMinecraft(OriginsPaper.apoliIdentifier("current_cooldowns")), PersistentDataType.STRING);
			encoded = encoded.replace("[", "").replace("]", "");
			if (encoded.equalsIgnoreCase("")) {
				return;
			}

			Arrays.stream(encoded.split(",")).forEach(key -> {
				String a = key.split("<::>")[0];
				double b = Double.parseDouble(key.split("<::>")[1]);
				if (OriginsPaper.getPower(ResourceLocation.parse(a.split("_cooldown_")[0])) instanceof CooldownInterface cooldownInterface) {
					CooldownPower.addCooldown(p, cooldownInterface);
				}
			});
		}
	}

	@Override
	public HudRender getHudRender() {
		return hudRender;
	}

	public static class Bar {
		String title;
		ResourceInterface power;
		int min;
		int max;
		Double currentProgress;
		Integer mappedProgress;
		KeyedBossBar renderedBar;
		double oneInc;

		Bar(@NotNull CooldownInterface power, @NotNull Player player) {
			this.title = Util.getNameOrTag((PowerType) power);
			this.power = power;
			this.min = 0;
			this.max = power.getCooldown();
			this.currentProgress = (double) power.getCooldown();
			this.mappedProgress = power.getCooldown();
			this.renderedBar = ResourcePower.createRender(Util.getNameOrTag((PowerType) power), this.formatForFirstRender(this.currentProgress), power, player);
			this.renderedBar.setVisible(true);
			this.oneInc = 1.0 / (double) this.max;
			this.renderedBar.addPlayer(player);
			this.change(power.getCooldown(), "set", false);
		}

		Bar(ResourcePower power, Player player) {
			this.title = Util.getNameOrTag(power);
			this.power = power;
			this.min = power.min;
			this.max = power.max;
			this.currentProgress = Double.valueOf(power.startValue);
			this.mappedProgress = this.currentProgress.intValue();
			this.renderedBar = ResourcePower.createRender(this.title, this.formatForFirstRender(this.currentProgress), power, player);
			this.renderedBar.setVisible(true);
			this.oneInc = 1.0 / (double) this.max;
			if (player != null) {
				this.renderedBar.addPlayer(player);
			}

			this.change(power.startValue, "set", false);
		}

		public static BarColor getBarColor(HudRender element) {
			return BarColor.WHITE;
		}

		public Bar cloneForPlayer(Player player) {
			return new Bar((ResourcePower) this.power, player);
		}

		public void delete() {
			this.renderedBar.setVisible(false);
			this.renderedBar.setProgress(0.0);
			this.renderedBar.removeAll();
			Bukkit.getServer().removeBossBar(this.renderedBar.getKey());
		}

		public void change(int by, String operation, boolean updateMapped) {
			Map<String, BinaryOperator<Double>> operator = getOperationMappingsDouble();
			double change = this.oneInc * (double) by;
			this.renderedBar
				.setProgress(this.preVerifyProgress(operator.get(operation).apply(Double.valueOf(this.renderedBar.getProgress()), Double.valueOf(change))));
			this.currentProgress = this.renderedBar.getProgress();
			if (updateMapped) {
				int f = operator.get(operation).apply(Double.valueOf(this.mappedProgress.doubleValue()), Double.valueOf(by)).intValue();
				if (f < 0) {
					f = 0;
				}

				this.mappedProgress = f;
			}

			this.renderedBar.getPlayers().forEach(entity -> {
				if (this.power instanceof ResourcePower resource) {
					if (this.renderedBar.getProgress() == 1.0) {
						if (resource.maxAction != null) resource.maxAction.accept(((CraftEntity) entity).getHandle());
					} else if (this.renderedBar.getProgress() == 0.0) {
						if (resource.minAction != null) resource.minAction.accept(((CraftEntity) entity).getHandle());
					}
				}
			});
		}

		public void change(int by, String operation) {
			this.change(by, operation, true);
		}

		public boolean meetsComparison(@NotNull Comparison comparison, double e) {
			return comparison.compare(this.mappedProgress.intValue(), e);
		}

		private double formatForFirstRender(double e) {
			if (e == 0.0) {
				return 0.0;
			} else {
				double f = 1.0 / e;
				if (f > 1.0) {
					return 1.0;
				} else {
					return f < 0.0 ? 0.0 : f;
				}
			}
		}

		private double preVerifyProgress(double e) {
			if (e > 1.0) {
				return 1.0;
			} else {
				return e < 0.0 ? 0.0 : e;
			}
		}

		public ResourceInterface getPower() {
			return this.power;
		}

		public Integer getMappedProgress() {
			return this.mappedProgress;
		}

		public Double getCurrentProgress() {
			return this.currentProgress;
		}
	}
}
