package io.github.dueris.originspaper;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import io.github.dueris.calio.data.AccessorKey;
import io.github.dueris.calio.parser.ParsingStrategy;
import io.github.dueris.calio.registry.IRegistry;
import io.github.dueris.calio.registry.impl.CalioRegistry;
import io.github.dueris.originspaper.action.Actions;
import io.github.dueris.originspaper.command.Commands;
import io.github.dueris.originspaper.command.OriginCommand;
import io.github.dueris.originspaper.condition.Conditions;
import io.github.dueris.originspaper.condition.types.BiEntityConditions;
import io.github.dueris.originspaper.content.OrbOfOrigins;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.OriginsDataTypes;
import io.github.dueris.originspaper.data.types.modifier.ModifierOperations;
import io.github.dueris.originspaper.integration.CraftPehuki;
import io.github.dueris.originspaper.mixin.OriginsMixins;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.PowerType;
import io.github.dueris.originspaper.power.RecipePower;
import io.github.dueris.originspaper.registry.BuiltinRegistry;
import io.github.dueris.originspaper.registry.Registries;
import io.github.dueris.originspaper.screen.ChoosingPage;
import io.github.dueris.originspaper.screen.GuiTicker;
import io.github.dueris.originspaper.screen.RandomOriginPage;
import io.github.dueris.originspaper.screen.ScreenNavigator;
import io.github.dueris.originspaper.storage.OriginConfiguration;
import io.github.dueris.originspaper.storage.PlayerPowerRepository;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.github.dueris.originspaper.util.*;
import io.github.dueris.originspaper.util.entity.GlowingEntitiesUtils;
import io.github.dueris.originspaper.util.entity.PlayerManager;
import io.papermc.paper.event.player.PlayerFailMoveEvent;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class OriginsPaper extends JavaPlugin implements Listener {
	public static final boolean isFolia = classExists("io.papermc.paper.threadedregions.RegionizedServer");
	public static final boolean isExpandedScheduler = classExists("io.papermc.paper.threadedregions.scheduler.ScheduledTask");
	public static List<Runnable> preShutdownTasks = new ArrayList<>();
	public static GlowingEntitiesUtils glowingEntitiesUtils;
	public static BstatsMetrics metrics;
	public static String apoliVersion = "2.12.0-alpha.9+mc.1.21.x";
	public static String LANGUAGE = "en_us";
	public static boolean showCommandOutput = false;
	public static File playerDataFolder;
	public static boolean forceUseCurrentVersion = false;
	public static OriginScheduler.MainTickerThread scheduler = null;
	public static String version = Bukkit.getVersion().split("\\(MC: ")[1].replace(")", "");
	public static boolean isCompatible = false;
	public static String pluginVersion;
	public static String world_container;
	public static ArrayList<String> versions = new ArrayList<>();
	public static MinecraftServer server;
	public static Origin EMPTY_ORIGIN;
	private static OriginsPaper plugin;
	public IRegistry registry;

	public OriginsPaper() {
		if (!Bootstrap.BOOTSTRAPPED.get()) {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.bootstrap(null);
		}

		Bootstrap.BOOTSTRAPPED.set(false);

		plugin = this;
		OriginsMixins.init(Bootstrap.MIXIN_LOADER.get());
	}

	public static OriginScheduler.MainTickerThread getScheduler() {
		return scheduler;
	}

	public static @NotNull ResourceLocation identifier(String path) {
		return ResourceLocation.fromNamespaceAndPath("originspaper", path);
	}

	public static @NotNull ResourceLocation originIdentifier(String path) {
		return ResourceLocation.fromNamespaceAndPath("origins", path);
	}

	public static @NotNull ResourceLocation apoliIdentifier(String path) {
		return ResourceLocation.fromNamespaceAndPath("apoli", path);
	}

	public static OriginLayer getLayer(ResourceLocation location) {
		return OriginsPaper.getPlugin().registry.retrieve(Registries.LAYER).get(location);
	}

	public static PowerType getPower(ResourceLocation location) {
		return OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(location);
	}

	public static Origin getOrigin(ResourceLocation location) {
		return OriginsPaper.getPlugin().registry.retrieve(Registries.ORIGIN).get(location);
	}

	private static void patchPowers() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			PowerHolderComponent.loadPowers(p);
		}
	}

	public static OriginsPaper getPlugin() {
		return plugin;
	}

	public static boolean classExists(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException var2) {
			return false;
		}
	}

	public void finalizePreboot() {
		PluginMeta meta = this.getPluginMeta();
		if (meta == null) {
			throw new RuntimeException("PluginMeta was null?");
		} else {
			pluginVersion = meta.getVersion().split("-")[1];

			try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("paper-plugin.yml")) {
				byte[] bytes = stream.readAllBytes();
				String contents = new String(bytes, StandardCharsets.UTF_8);
				YamlConfiguration yamlConfiguration = new YamlConfiguration();
				yamlConfiguration.loadFromString(contents);
				if (!yamlConfiguration.contains("supportedVersions"))
					throw new RuntimeException("Supported Versions list not found in plugin yaml!");
				versions.addAll(yamlConfiguration.getStringList("supportedVersions"));
				LANGUAGE = yamlConfiguration.getString("default-language");
			} catch (InvalidConfigurationException | IOException var8) {
				throw new RuntimeException(var8);
			}

			metrics = new BstatsMetrics(this, 18536);
		}
	}

	@Override
	public void onEnable() {
		Getter<Boolean> startup = () -> {
			this.finalizePreboot();
			this.registry = CalioRegistry.INSTANCE;
			Bukkit.getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.println();
			this.printComponent(
				Component.text(
						"* Loading Version OriginsPaper-{minecraftVersion-versionNumber} // CraftApoli-{apoliVersion}"
							.replace("minecraftVersion", "mc" + version)
							.replace("versionNumber", pluginVersion)
							.replace("apoliVersion", apoliVersion)
					)
					.color(TextColor.fromHexString("#4fec4f"))
			);
			System.out.println();
			server = ((CraftServer) Bukkit.getServer()).getServer();
			world_container = server.options.asMap().toString().split(", \\[W, universe, world-container, world-dir]=\\[")[1].split("], ")[0];
			playerDataFolder = server.playerDataStorage.getPlayerDir();
			glowingEntitiesUtils = new GlowingEntitiesUtils(this);

			try {
				OriginConfiguration.load();
				showCommandOutput = OriginConfiguration.getConfiguration().getBoolean("show-command-output", false);
				LANGUAGE = OriginConfiguration.getConfiguration().getString("language", LANGUAGE);
				LangFile.init();
			} catch (IOException var7) {
				throw new RuntimeException(var7);
			}

			isCompatible = !isFolia && isExpandedScheduler;
			boolean isCorrectVersion = false;

			for (String vers : versions) {
				if (vers.equalsIgnoreCase(String.valueOf(version))) {
					isCorrectVersion = true;
					break;
				}
			}

			if ((!isCompatible || !isCorrectVersion) && !forceUseCurrentVersion) {
				if (isCorrectVersion) {
					this.getLog4JLogger().error("Unable to start OriginsPaper due to it not being compatible with this server type");
				} else {
					this.getLog4JLogger().error("Unable to start OriginsPaper due to it not being compatible with this server version, {}", version);
				}

				Bukkit.getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				Bukkit.getServer().getPluginManager().disablePlugin(this);
			}

			try {
				io.github.dueris.calio.CraftCalio craftCalio = io.github.dueris.calio.CraftCalio.buildInstance(
					new String[]{"--async=true"}
				);
				ApoliDataTypes.init();
				OriginsDataTypes.init();
				ModifierOperations.registerAll();
				Conditions.registerAll();
				Actions.registerAll();
				PowerType.registerAll();
				craftCalio.startBuilder()
					.withAccessor(new AccessorKey<>(List.of("apoli", "origins"), "power", PowerType.class, 0, ParsingStrategy.TYPED, Registries.CRAFT_POWER))
					.withAccessor(new AccessorKey<>(List.of("origins"), "origin", Origin.class, 1, ParsingStrategy.DEFAULT, Registries.ORIGIN))
					.withAccessor(new AccessorKey<>(List.of("origins"), "origin_layer", OriginLayer.class, 2, ParsingStrategy.DEFAULT, Registries.LAYER))
					.build().parse();
				BuiltinRegistry.bootstrap();
				return true;
			} catch (Throwable throwable) {
				this.getLog4JLogger().error("An unhandled exception occurred when starting OriginsPaper!");
				this.throwable(throwable, true);
				return false;
			}
		};
		if (startup.get()) {
			this.debug(Component.text("  - Loaded @1 powers".replace("@1", String.valueOf(this.registry.retrieve(Registries.CRAFT_POWER).registrySize()))));
			this.debug(Component.text("  - Loaded @2 layers".replace("@2", String.valueOf(this.registry.retrieve(Registries.LAYER).registrySize()))));
			this.debug(Component.text("  - Loaded @3 origins".replace("@3", String.valueOf(this.registry.retrieve(Registries.ORIGIN).registrySize()))));
			this.debug(Component.text("  - Loaded @4 actions".replace("@4", String.valueOf(((Getter<Integer>) () -> {
				int bientity = this.registry.retrieve(Registries.BIENTITY_ACTION).registrySize();
				int block = this.registry.retrieve(Registries.BLOCK_ACTION).registrySize();
				int entity = this.registry.retrieve(Registries.ENTITY_ACTION).registrySize();
				int item = this.registry.retrieve(Registries.ITEM_ACTION).registrySize();
				return item + entity + block + bientity;
			}).get()))));
			this.debug(Component.text("  - Loaded @5 conditions".replace("@5", String.valueOf(((Getter<Integer>) () -> {
				int bientity = this.registry.retrieve(Registries.BIENTITY_CONDITION).registrySize();
				int biome = this.registry.retrieve(Registries.BIOME_CONDITION).registrySize();
				int block = this.registry.retrieve(Registries.BLOCK_CONDITION).registrySize();
				int damage = this.registry.retrieve(Registries.DAMAGE_CONDITION).registrySize();
				int entity = this.registry.retrieve(Registries.ENTITY_CONDITION).registrySize();
				int fluid = this.registry.retrieve(Registries.FLUID_CONDITION).registrySize();
				int item = this.registry.retrieve(Registries.ITEM_CONDITION).registrySize();
				return item + fluid + entity + damage + block + biome + bientity;
			}).get()))));
			scheduler = new OriginScheduler.MainTickerThread();
			scheduler.runTaskTimer(this, 0L, 1L);
			new BukkitRunnable() {
				@Override
				public void run() {
					OriginsPaper.scheduler.tickAsyncScheduler();
				}
			}.runTaskTimerAsynchronously(getPlugin(), 0L, 1L);
			this.start();
			patchPowers();
			this.debug(Component.text("  - Power thread starting with {originScheduler}".replace("originScheduler", scheduler.toString())));
			CommandDispatcher<CommandSourceStack> commandDispatcher = server.getCommands().getDispatcher();
			if (commandDispatcher.getRoot().getChildren().stream().map(CommandNode::getName).toList().contains("origin")) {
				Commands.unload(commandDispatcher);
			}

			Commands.bootstrap(commandDispatcher);
			Bukkit.updateRecipes();
			CraftPehuki.onLoad();
		}
		Bukkit.getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}

	public void throwable(@NotNull Throwable throwable, boolean kill) {
		String[] stacktrace = new String[]{"\n"};
		Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString).forEach(string -> stacktrace[0] = stacktrace[0] + "\tat " + string + "\n");
		this.getLog4JLogger().error("{}{}", throwable.getMessage(), stacktrace[0]);
		if (kill) {
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}

	public void printComponent(Component component) {
		Bukkit.getServer().getConsoleSender().sendMessage(component);
	}

	public void debug(Component component) {
		this.printComponent(component);
	}

	private void start() {
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(new PlayerManager(), this);
		this.getServer().getPluginManager().registerEvents(new ScreenNavigator(), this);
		this.getServer().getPluginManager().registerEvents(new OriginCommand(), this);
		this.getServer().getPluginManager().registerEvents(new LogoutBugWorkaround(), this);
		this.getServer().getPluginManager().registerEvents(new BiEntityConditions(), this);
		this.getServer().getPluginManager().registerEvents(new OriginScheduler.MainTickerThread(), this);
		this.getServer().getPluginManager().registerEvents(new KeybindUtil(), this);
		this.getServer().getPluginManager().registerEvents(new AsyncUpgradeTracker(), this);
		this.getServer().getPluginManager().registerEvents(new PowerHolderComponent(), this);
		this.getServer().getPluginManager().registerEvents(new CraftPehuki(), this);
		this.getServer().getPluginManager().registerEvents(EntityLinkedItemStack.getInstance(), this);
		this.getServer().getPluginManager().registerEvents(new ApoliScheduler(), this);
		this.registry.retrieve(Registries.CRAFT_POWER).values().forEach(powerType -> {
			if (powerType != null) {
				this.getServer().getPluginManager().registerEvents(powerType, this);
			}
		});
		BukkitRunnable[] independentTickers = new BukkitRunnable[]{new GuiTicker(), new OriginCommand()};

		for (BukkitRunnable runnable : independentTickers) {
			runnable.runTaskTimerAsynchronously(getPlugin(), 0L, 1L);
		}
	}

	@Override
	public void onDisable() {
		try {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.closeInventory();
				player.getPersistentDataContainer()
					.set(new NamespacedKey(this, "powers"), PersistentDataType.STRING, PlayerPowerRepository.getOrCreateRepo(((CraftPlayer) player).getHandle()).serializePowers(new CompoundTag()).toString());
				PowerHolderComponent.unloadPowers(player);
			}

			preShutdownTasks.forEach(Runnable::run);
			glowingEntitiesUtils.disable();
			RecipePower.recipeMapping.clear();
			RecipePower.tags.clear();
			this.registry.clearRegistries();
			if (scheduler != null) {
				scheduler.cancel();
			}
		} catch (Throwable var3) {
			this.getLog4JLogger().error("An unhandled exception occurred when disabling OriginsPaper!");
			this.throwable(var3, false);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void lagBackPatch(@NotNull PlayerFailMoveEvent e) {
		e.setAllowed(true);
		e.setLogWarning(false);
	}

	@EventHandler
	public void loadEvent(ServerLoadEvent e) {
		ChoosingPage.registerInstances();
		ScreenNavigator.layerPages.values().forEach(pages -> pages.add(pages.size(), new RandomOriginPage()));
		OrbOfOrigins.init();
	}

	private interface Getter<T> {
		T get();
	}
}
