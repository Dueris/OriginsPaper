package me.dueris.originspaper;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import io.github.dueris.calio.data.AccessorKey;
import io.github.dueris.calio.parser.JsonObjectRemapper;
import io.github.dueris.calio.parser.ParsingStrategy;
import io.github.dueris.calio.registry.RegistryKey;
import io.github.dueris.calio.test.ModMeta;
import io.papermc.paper.event.player.PlayerFailMoveEvent;
import io.papermc.paper.event.server.ServerResourcesReloadedEvent;
import io.papermc.paper.plugin.configuration.PluginMeta;
import me.dueris.calio.CraftCalio;
import me.dueris.calio.data.AssetIdentifier;
import me.dueris.calio.registry.IRegistry;
import me.dueris.calio.registry.impl.CalioRegistry;
import me.dueris.originspaper.command.Commands;
import me.dueris.originspaper.command.OriginCommand;
import me.dueris.originspaper.content.ContentTicker;
import me.dueris.originspaper.content.OrbOfOrigins;
import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.conditions.types.BiEntityConditions;
import me.dueris.originspaper.factory.powers.apoli.RecipePower;
import me.dueris.originspaper.factory.powers.apoli.provider.origins.BounceSlimeBlock;
import me.dueris.originspaper.factory.powers.apoli.provider.origins.WaterBreathe;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.integration.PlaceHolderAPI;
import me.dueris.originspaper.integration.pehuki.CraftPehuki;
import me.dueris.originspaper.registry.BuiltinRegistry;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.registry.registries.DatapackRepository;
import me.dueris.originspaper.registry.registries.Layer;
import me.dueris.originspaper.registry.registries.Origin;
import me.dueris.originspaper.screen.ChoosingPage;
import me.dueris.originspaper.screen.GuiTicker;
import me.dueris.originspaper.screen.RandomOriginPage;
import me.dueris.originspaper.screen.ScreenNavigator;
import me.dueris.originspaper.storage.OriginConfiguration;
import me.dueris.originspaper.storage.OriginDataContainer;
import me.dueris.originspaper.storage.nbt.NBTFixerUpper;
import me.dueris.originspaper.util.*;
import me.dueris.originspaper.util.entity.GlowingEntitiesUtils;
import me.dueris.originspaper.util.entity.PlayerManager;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class OriginsPaper extends JavaPlugin implements Listener {
	public static final boolean isFolia = classExists("io.papermc.paper.threadedregions.RegionizedServer");
	public static final boolean isExpandedScheduler = classExists("io.papermc.paper.threadedregions.scheduler.ScheduledTask");
	public static List<Runnable> preShutdownTasks = new ArrayList<>();
	public static GlowingEntitiesUtils glowingEntitiesUtils;
	public static BstatsMetrics metrics;
	public static ConditionExecutor conditionExecutor;
	public static String apoliVersion = "2.12.0-alpha.3";
	public static String LANGUAGE;
	public static boolean placeholderapi = false;
	public static boolean showCommandOutput = false;
	public static File playerDataFolder;
	public static boolean forceUseCurrentVersion = false;
	public static OriginScheduler.MainTickerThread scheduler = null;
	public static String version = Bukkit.getVersion().split("\\(MC: ")[1].replace(")", "");
	public static boolean isCompatible = false;
	public static String pluginVersion;
	public static String world_container;
	public static ExecutorService loaderThreadPool;
	public static ArrayList<String> versions = new ArrayList<>();
	public static MinecraftServer server;
	private static OriginsPaper plugin;
	public IRegistry registry;

	public static OriginScheduler.MainTickerThread getScheduler() {
		return scheduler;
	}

	@Contract("_ -> new")
	public static @NotNull ResourceLocation identifier(String path) {
		return ResourceLocation.fromNamespaceAndPath("originspaper", path);
	}

	@Contract("_ -> new")
	public static @NotNull ResourceLocation originIdentifier(String path) {
		return ResourceLocation.fromNamespaceAndPath("origins", path);
	}

	@Contract("_ -> new")
	public static @NotNull ResourceLocation apoliIdentifier(String path) {
		return ResourceLocation.fromNamespaceAndPath("apoli", path);
	}

	public static @NotNull File getTmpFolder() {
		return Path.of(getPlugin().getDataFolder().getAbsolutePath() + File.separator + ".tmp" + File.separator).toFile();
	}

	private static void patchPowers() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			OriginDataContainer.loadData();
			PowerHolderComponent.setupPowers(p);
			PowerHolderComponent.assignPowers(p);
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
				versions.add(yamlConfiguration.getString("supportedVer"));
				LANGUAGE = yamlConfiguration.getString("default-language");
			} catch (InvalidConfigurationException | IOException var8) {
				throw new RuntimeException(var8);
			}

			metrics = new BstatsMetrics(this, 18536);
		}
	}

	@Override
	public void onEnable() {
		if (!Bootstrap.BOOTSTRAPPED.get()) {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.bootstrap(null);
		}

		Bootstrap.BOOTSTRAPPED.set(false);
		plugin = this;
		BooleanGetter startup = () -> {
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
					this.getLogger().severe("Unable to start OriginsPaper due to it not being compatible with this server type");
				} else {
					this.getLogger().severe("Unable to start OriginsPaper due to it not being compatible with this server version");
				}

				Bukkit.getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				Bukkit.getServer().getPluginManager().disablePlugin(this);
			}

			this.debug(Component.text("* (-debugOrigins={true}) || BEGINNING DEBUG {"));
			ThreadFactory threadFactory = new NamedTickThreadFactory("OriginParsingPool");
			placeholderapi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
			if (placeholderapi) {
				new PlaceHolderAPI(this).register();
			}

			if (!getTmpFolder().exists()) {
				getTmpFolder().mkdirs();
			}

			OriginDataContainer.loadData();
			conditionExecutor = new ConditionExecutor();
			int avalibleJVMThreads = Runtime.getRuntime().availableProcessors() * 2;
			int dynamic_thread_count = avalibleJVMThreads < 4
				? avalibleJVMThreads
				: Math.min(avalibleJVMThreads, OriginConfiguration.getConfiguration().getInt("max-loader-threads"));
			loaderThreadPool = Executors.newFixedThreadPool(dynamic_thread_count, threadFactory);
			this.registry.retrieve(Registries.PACK_SOURCE).register(new DatapackRepository(originIdentifier("plugins"), Bukkit.getPluginsFolder().toPath()));
			this.registry
				.retrieve(Registries.PACK_SOURCE)
				.register(new DatapackRepository(originIdentifier("datapacks"), server.getWorldPath(LevelResource.DATAPACK_DIR)));

			try {
				ConditionExecutor.registerAll();
				Actions.registerAll();
				PowerType.registerAll();
				CraftCalio calio = CraftCalio.INSTANCE;
				this.registry.retrieve(Registries.PACK_SOURCE).values().stream().map(DatapackRepository::path).forEach(calio::addDatapackPath);
				calio.registerAccessor(
					"powers", 0,
					true, PowerType.class,
					Registries.CRAFT_POWER, "apoli:simple");
				calio.registerAccessor(
					"origins", 1,
					false, Origin.class,
					Registries.ORIGIN);
				calio.registerAccessor(
					"origin_layers", 2,
					false, Layer.class,
					Registries.LAYER);
				calio.registerAsset("textures", 0, "png", AssetIdentifier.AssetType.IMAGE, Registries.TEXTURE_LOCATION);
				calio.registerAsset("lang", 1, "json", AssetIdentifier.AssetType.JSON, Registries.LANG);
				calio.start(true, loaderThreadPool);
				BuiltinRegistry.bootstrap();
				NBTFixerUpper.runFixerUpper();
				return true;
			} catch (Throwable throwable) {
				this.getLogger().severe("An unhandled exception occurred when starting OriginsPaper!");
				this.throwable(throwable, true);
				return false;
			}
		};
		if (!startup.get()) {
			Bukkit.getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		} else {
			this.debug(Component.text("  - Loaded @1 powers".replace("@1", String.valueOf(this.registry.retrieve(Registries.CRAFT_POWER).registrySize()))));
			this.debug(Component.text("  - Loaded @2 layers".replace("@2", String.valueOf(this.registry.retrieve(Registries.LAYER).registrySize()))));
			this.debug(Component.text("  - Loaded @3 origins".replace("@3", String.valueOf(this.registry.retrieve(Registries.ORIGIN).registrySize()))));
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
			this.debug(Component.text("}"));
			loaderThreadPool.shutdown();
			CommandDispatcher<CommandSourceStack> commandDispatcher = server.getCommands().getDispatcher();
			if (commandDispatcher.getRoot().getChildren().stream().map(CommandNode::getName).toList().contains("origin")) {
				Commands.unload(commandDispatcher);
			}

			Commands.bootstrap(commandDispatcher);
			CraftPehuki.onLoad();
			Bukkit.getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		}
	}

	public void throwable(@NotNull Throwable throwable, boolean kill) {
		String[] stacktrace = new String[]{"\n"};
		Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString).forEach(string -> stacktrace[0] = stacktrace[0] + "\tat " + string + "\n");
		this.getLogger().severe(throwable.getMessage() + stacktrace[0]);
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
		this.getServer().getPluginManager().registerEvents(new ContentTicker(), this);
		this.getServer().getPluginManager().registerEvents(new LogoutBugWorkaround(), this);
		this.getServer().getPluginManager().registerEvents(new BounceSlimeBlock(), this);
		this.getServer().getPluginManager().registerEvents(new BiEntityConditions(), this);
		this.getServer().getPluginManager().registerEvents(new OriginScheduler.MainTickerThread(), this);
		this.getServer().getPluginManager().registerEvents(new KeybindUtil(), this);
		this.getServer().getPluginManager().registerEvents(new AsyncUpgradeTracker(), this);
		this.getServer().getPluginManager().registerEvents(new PowerHolderComponent(), this);
		this.getServer().getPluginManager().registerEvents(new CraftPehuki(), this);
		this.getServer().getPluginManager().registerEvents(EntityLinkedItemStack.getInstance(), this);
		this.registry.retrieve(Registries.CRAFT_POWER).values().forEach(powerType -> {
			if (powerType != null) {
				this.getServer().getPluginManager().registerEvents(powerType, this);
			}
		});
		BukkitRunnable[] independentTickers = new BukkitRunnable[]{new GuiTicker(), new ContentTicker(), new OriginCommand()};
		WaterBreathe.start();

		for (BukkitRunnable runnable : independentTickers) {
			runnable.runTaskTimerAsynchronously(getPlugin(), 0L, 1L);
		}
	}

	public void onDisable() {
		try {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.closeInventory();
				player.getPersistentDataContainer()
					.set(new NamespacedKey(this, "originLayer"), PersistentDataType.STRING, CraftApoli.toSaveFormat(PowerHolderComponent.getOrigin(player), player));
				PowerHolderComponent.unassignPowers(player);
				OriginDataContainer.unloadData(player);
			}

			preShutdownTasks.forEach(Runnable::run);
			glowingEntitiesUtils.disable();
			CraftApoli.unloadData();
			PowerHolderComponent.playerPowerMapping.clear();
			PowerHolderComponent.powersAppliedList.clear();
			RecipePower.recipeMapping.clear();
			RecipePower.tags.clear();
			this.registry.clearRegistries();
			if (scheduler != null) {
				scheduler.cancel();
			}
		} catch (Throwable var3) {
			this.getLogger().severe("An unhandled exception occurred when disabling OriginsPaper!");
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
		CraftApoli.getPowersFromRegistry().addAll(this.registry.retrieve(Registries.CRAFT_POWER).values());
		CraftApoli.getOriginsFromRegistry().addAll(this.registry.retrieve(Registries.ORIGIN).values());
		CraftApoli.getLayersFromRegistry().addAll(this.registry.retrieve(Registries.LAYER).values());
		ChoosingPage.registerInstances();
		ScreenNavigator.layerPages.values().forEach(pages -> pages.add(pages.size(), new RandomOriginPage()));
		RecipePower.parseRecipes();
		OrbOfOrigins.init();
	}

	@EventHandler
	public void resourceReload(ServerResourcesReloadedEvent e) {
		// beta testing
		io.github.dueris.calio.CraftCalio craftCalio = io.github.dueris.calio.CraftCalio.buildInstance(
			new String[]{"--debug=true", "--async=true"}
		);
		JsonObjectRemapper.main(versions.toArray(new String[0]));
		try {
			RegistryKey<ModMeta> registryKey = new RegistryKey<>(ModMeta.class, apoliIdentifier("mods"));
			craftCalio.startBuilder()
				.withAccessor(new AccessorKey<>("mods", ModMeta.class, 0, ParsingStrategy.TYPED, registryKey))
				.build().parse();
			io.github.dueris.calio.registry.impl.CalioRegistry.INSTANCE.retrieve(registryKey).forEach(((location, modMeta) -> {
				System.out.println(modMeta);
			}));
		} catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
		// end beta
	}

	private interface BooleanGetter {
		boolean get();
	}
}
