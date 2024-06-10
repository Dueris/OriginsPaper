package me.dueris.genesismc;

import com.mojang.brigadier.tree.CommandNode;
import io.papermc.paper.event.player.PlayerFailMoveEvent;
import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.CraftCalio;
import me.dueris.calio.data.JsonObjectRemapper;
import me.dueris.calio.data.AssetIdentifier.AssetType;
import me.dueris.calio.registry.IRegistry;
import me.dueris.calio.registry.Registrar;
import me.dueris.calio.registry.impl.CalioRegistry;
import me.dueris.genesismc.command.OriginCommand;
import me.dueris.genesismc.command.PowerCommand;
import me.dueris.genesismc.content.ContentTicker;
import me.dueris.genesismc.content.OrbOfOrigins;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.actions.types.BiEntityActions;
import me.dueris.genesismc.factory.actions.types.BlockActions;
import me.dueris.genesismc.factory.actions.types.EntityActions;
import me.dueris.genesismc.factory.actions.types.ItemActions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.conditions.types.*;
import me.dueris.genesismc.factory.powers.apoli.RecipePower;
import me.dueris.genesismc.factory.powers.apoli.provider.origins.BounceSlimeBlock;
import me.dueris.genesismc.factory.powers.apoli.provider.origins.WaterBreathe;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.integration.PlaceHolderAPI;
import me.dueris.genesismc.integration.pehuki.CraftPehuki;
import me.dueris.genesismc.registry.BuiltinRegistry;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.DatapackRepository;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.screen.ChoosingPage;
import me.dueris.genesismc.screen.GuiTicker;
import me.dueris.genesismc.screen.RandomOriginPage;
import me.dueris.genesismc.screen.ScreenNavigator;
import me.dueris.genesismc.storage.OriginConfiguration;
import me.dueris.genesismc.storage.OriginDataContainer;
import me.dueris.genesismc.storage.nbt.NBTFixerUpper;
import me.dueris.genesismc.util.*;
import me.dueris.genesismc.util.entity.GlowingEntitiesUtils;
import me.dueris.genesismc.util.entity.PlayerManager;
import me.dueris.genesismc.util.entity.PowerHolderComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static me.dueris.genesismc.factory.powers.apoli.RecipePower.parseRecipes;

public final class GenesisMC extends JavaPlugin implements Listener {
	public static final boolean isFolia = classExists("io.papermc.paper.threadedregions.RegionizedServer");
	public static final boolean isExpandedScheduler = classExists("io.papermc.paper.threadedregions.scheduler.ScheduledTask");
	public static List<Runnable> preShutdownTasks = new ArrayList<>();
	public static GlowingEntitiesUtils glowingEntitiesUtils;
	public static BstatsMetrics metrics;
	public static ConditionExecutor conditionExecutor;
	public static String apoliVersion = "2.12.0-alpha.3";
	public static boolean placeholderapi = false;
	public static File playerDataFolder;
	public static boolean forceUseCurrentVersion = false;
	public static OriginScheduler.MainTickerThread scheduler = null;
	public static String version = Bukkit.getVersion().split("\\(MC: ")[1].replace(")", "");
	public static boolean isCompatible = false;
	public static String pluginVersion = "v1.0.1";
	public static String world_container;
	public static ExecutorService loaderThreadPool;
	public static ArrayList<String> versions = new ArrayList<>();
	public static MinecraftServer server;
	private static GenesisMC plugin;

	static {
		versions.add("1.20.5");
		versions.add("1.20.6");
	}

	public IRegistry registry;

	public static OriginScheduler.MainTickerThread getScheduler() {
		return scheduler;
	}

	public static NamespacedKey identifier(String path) {
		return new NamespacedKey(getPlugin(), path);
	}

	public static NamespacedKey originIdentifier(String path) {
		return new NamespacedKey("origins", path);
	}

	public static NamespacedKey apoliIdentifier(String path) {
		return new NamespacedKey("apoli", path);
	}

	public static File getTmpFolder() {
		return Path.of(GenesisMC.getPlugin().getDataFolder().getAbsolutePath() + File.separator + ".tmp" + File.separator).toFile();
	}

	private static void patchPowers() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			OriginDataContainer.loadData();
			PowerHolderComponent.setupPowers(p);
			PlayerManager.originValidCheck(p);
			PowerHolderComponent.assignPowers(p);
		}
	}

	public static GenesisMC getPlugin() {
		return plugin;
	}

	public static boolean classExists(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	@Override
	public void onEnable() {
		plugin = this;
		metrics = new BstatsMetrics(this, 18536);
		this.registry = CalioRegistry.INSTANCE;
		Bukkit.getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println(); // Split new line
		printComponent(Component.text("* Loading Version GenesisMC-{minecraftVersion-versionNumber} // CraftApoli-{apoliVersion}"
			.replace("minecraftVersion", "mc" + version)
			.replace("versionNumber", pluginVersion)
			.replace("apoliVersion", apoliVersion)).color(TextColor.fromHexString("#4fec4f")));
		System.out.println(); // Split new line
		GenesisMC.server = ((CraftServer) Bukkit.getServer()).getServer();
		world_container = server.options.asMap().toString().split(", \\[W, universe, world-container, world-dir]=\\[")[1].split("], ")[0];
		playerDataFolder = server.playerDataStorage.getPlayerDir();
		glowingEntitiesUtils = new GlowingEntitiesUtils(this);
		try {
			OriginConfiguration.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		isCompatible = (!isFolia && (isExpandedScheduler));
		boolean isCorrectVersion = false;
		for (String vers : versions) {
			if (vers.equalsIgnoreCase(String.valueOf(version))) {
				isCorrectVersion = true;
				break;
			}
		}
		if (!isCompatible || !isCorrectVersion) {
			if (forceUseCurrentVersion) return;
			if (isCorrectVersion)
				this.getLogger().severe("Unable to start GenesisMC due to it not being compatible with this server type");
			else
				this.getLogger().severe("Unable to start GenesisMC due to it not being compatible with this server version");
			Bukkit.getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
		}

		debug(Component.text("* (-debugOrigins={true}) || BEGINNING DEBUG {"));
		ThreadFactory threadFactory = new NamedTickThreadFactory("OriginParsingPool");
		placeholderapi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
		if (placeholderapi) new PlaceHolderAPI(this).register();

		if (!getTmpFolder().exists()) {
			getTmpFolder().mkdirs();
		}

		OriginDataContainer.loadData();
		conditionExecutor = new ConditionExecutor();

		int avalibleJVMThreads = Runtime.getRuntime().availableProcessors() * 2;
		int dynamic_thread_count = avalibleJVMThreads < 4 ? avalibleJVMThreads : Math.min(avalibleJVMThreads, OriginConfiguration.getConfiguration().getInt("max-loader-threads"));
		loaderThreadPool = Executors.newFixedThreadPool(dynamic_thread_count, threadFactory);

		this.registry.retrieve(Registries.PACK_SOURCE).register(new DatapackRepository(GenesisMC.originIdentifier("plugins"), Bukkit.getPluginsFolder().toPath()));
		this.registry.retrieve(Registries.PACK_SOURCE).register(new DatapackRepository(GenesisMC.originIdentifier("datapacks"), server.getWorldPath(LevelResource.DATAPACK_DIR)));

		try {
			// Register builtin instances
			ConditionExecutor.registerAll();
			Actions.registerAll();
			PowerType.registerAll();
			// Start calio parser for data driven instances
			final CraftCalio calio = CraftCalio.INSTANCE;
			this.registry.retrieve(Registries.PACK_SOURCE).values().stream()
				.map(DatapackRepository::getPath).forEach(calio::addDatapackPath);
			calio.registerAccessor(
				"powers", 0,
				true, PowerType.class,
				Registries.CRAFT_POWER, "apoli:simple"
			);
			calio.registerAccessor(
				"origins", 1,
				false, Origin.class,
				Registries.ORIGIN
			);
			calio.registerAccessor(
				"origin_layers", 2,
				false, Layer.class,
				Registries.LAYER
			);
			calio.registerAsset("textures", 0, "png", AssetType.IMAGE, Registries.TEXTURE_LOCATION);
			calio.registerAsset("lang", 1, "json", AssetType.JSON, Registries.LANG);
			calio.start(OriginConfiguration.getConfiguration().getBoolean("debug"));
			BuiltinRegistry.bootstrap();
			// End calio parsing
		} catch (Throwable e) {
			this.getLogger().severe("An unhandled exception occurred when starting Genesis!");
			throwable(e, true);
		}

		debug(Component.text("  - Loaded @1 powers".replace("@1", String.valueOf(this.registry.retrieve(Registries.CRAFT_POWER).registrySize()))));
		debug(Component.text("  - Loaded @2 layers".replace("@2", String.valueOf(this.registry.retrieve(Registries.LAYER).registrySize()))));
		debug(Component.text("  - Loaded @3 origins".replace("@3", String.valueOf(this.registry.retrieve(Registries.ORIGIN).registrySize()))));
		try {
			NBTFixerUpper.runFixerUpper();
		} catch (Throwable e) {
			this.getLogger().severe("An unhandled exception occurred when starting Genesis!");
			throwable(e, false);
		}

		GenesisMC.scheduler = new OriginScheduler.MainTickerThread();
		GenesisMC.scheduler.runTaskTimer(this, 0, 1);
		new BukkitRunnable() {
			@Override
			public void run() {
				GenesisMC.scheduler.tickAsyncScheduler();
			}
		}.runTaskTimerAsynchronously(GenesisMC.getPlugin(), 0, 1);
		start();
		patchPowers();
		debug(Component.text("  - Power thread starting with {originScheduler}".replace("originScheduler", GenesisMC.scheduler.toString())));
		debug(Component.text("}"));

		// Shutdown executor, we don't need it anymore
		loaderThreadPool.shutdown();
		OriginCommand.commandProvidedPowers.addAll(this.registry.retrieve(Registries.CRAFT_POWER).values().stream().toList());
		OriginCommand.commandProvidedOrigins.addAll(this.registry.retrieve(Registries.ORIGIN).values().stream().toList());
		OriginCommand.commandProvidedLayers.addAll(this.registry.retrieve(Registries.LAYER).values().stream().filter(Layer::isEnabled).toList());

		if (GenesisMC.server.getCommands().getDispatcher().getRoot().getChildren().stream().map(CommandNode::getName).toList().contains("origin")) {
			// Already registered, lets change that ;)
			((CraftServer) Bukkit.getServer()).getServer().getCommands().getDispatcher().getRoot().removeCommand("origin");
			((CraftServer) Bukkit.getServer()).getServer().getCommands().getDispatcher().getRoot().removeCommand("power");
		}
		OriginCommand.register(((CraftServer) Bukkit.getServer()).getServer().getCommands().getDispatcher());
		PowerCommand.register(((CraftServer) Bukkit.getServer()).getServer().getCommands().getDispatcher());
		// Load addons
		CraftPehuki.onLoad();
		Bukkit.getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}

	public void throwable(Throwable throwable, boolean kill) {
		String[] stacktrace = {"\n"};
		Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString).forEach(string -> stacktrace[0] += ("\tat " + string + "\n"));
		this.getLogger().severe(throwable.getMessage() + stacktrace[0]);
		if (kill) Bukkit.getPluginManager().disablePlugin(this);
	}

	public void printComponent(Component component) {
		Bukkit.getServer().getConsoleSender().sendMessage(component);
	}

	public void debug(Component component) {
		if (OriginConfiguration.getConfiguration().getBoolean("debug")) printComponent(component);
	}

	private void start() {
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new PlayerManager(), this);
		getServer().getPluginManager().registerEvents(new ScreenNavigator(), this);
		getServer().getPluginManager().registerEvents(new OriginCommand(), this);
		getServer().getPluginManager().registerEvents(new ContentTicker(), this);
		getServer().getPluginManager().registerEvents(new LogoutBugWorkaround(), this);
		getServer().getPluginManager().registerEvents(new BounceSlimeBlock(), this);
		getServer().getPluginManager().registerEvents(new BiEntityConditions(), this);
		getServer().getPluginManager().registerEvents(new OriginScheduler.MainTickerThread(), this);
		getServer().getPluginManager().registerEvents(new KeybindUtil(), this);
		getServer().getPluginManager().registerEvents(new AsyncUpgradeTracker(), this);
		getServer().getPluginManager().registerEvents(new PowerHolderComponent(), this);
		getServer().getPluginManager().registerEvents(new CraftPehuki(), this);
		getServer().getPluginManager().registerEvents(new ItemStackPowerHolder().startTicking(), this);
		getServer().getPluginManager().registerEvents(EntityLinkedItemStack.getInstance(), this);
		this.registry.retrieve(Registries.CRAFT_POWER).values().forEach(powerType -> {
			if (powerType != null) {
				getServer().getPluginManager().registerEvents(powerType, this);
			}
		});

		BukkitRunnable[] independentTickers = {new GuiTicker(), new ContentTicker(), new OriginCommand()};
		WaterBreathe.start();
		for (BukkitRunnable runnable : independentTickers) {
			runnable.runTaskTimerAsynchronously(GenesisMC.getPlugin(), 0, 1);
		}
	}

	@Override
	public void onDisable() {
		try {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.closeInventory(); // Ensure that all choosing players have closed inventories during reload
				player.getPersistentDataContainer().set(GenesisMC.identifier("originLayer"), PersistentDataType.STRING, CraftApoli.toSaveFormat(PowerHolderComponent.getOrigin(player), player));
				PowerHolderComponent.unassignPowers(player);
				OriginDataContainer.unloadData(player);
			}
			preShutdownTasks.forEach(Runnable::run);
			glowingEntitiesUtils.disable();
			CraftApoli.unloadData();
			PowerHolderComponent.playerPowerMapping.clear();
			PowerHolderComponent.powersAppliedList.clear();
			OriginCommand.commandProvidedLayers.clear();
			OriginCommand.commandProvidedOrigins.clear();
			OriginCommand.commandProvidedPowers.clear();
			RecipePower.recipeMapping.clear();
			RecipePower.tags.clear();
			this.registry.clearRegistries();
			scheduler.cancel();
		} catch (Throwable throwable) {
			getLogger().severe("An unhandled exception occured when disabling GenesisMC!");
			throwable(throwable, false);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void lagBackPatch(PlayerFailMoveEvent e) {
		e.setAllowed(true);
		e.setLogWarning(false);
	}

	@EventHandler
    /*
      Post-Startup of the plugin lifecycle, where registries will be finalized after plugins have loaded
      @param e
     */
	public void loadEvent(ServerLoadEvent e) {
		CraftApoli.getPowersFromRegistry().addAll(this.registry.retrieve(Registries.CRAFT_POWER).values());
		CraftApoli.getOriginsFromRegistry().addAll(this.registry.retrieve(Registries.ORIGIN).values());
		CraftApoli.getLayersFromRegistry().addAll(this.registry.retrieve(Registries.LAYER).values());
		ChoosingPage.registerInstances();
		ScreenNavigator.layerPages.values().forEach((pages) -> pages.add(pages.size(), new RandomOriginPage()));
		parseRecipes();
		OrbOfOrigins.init();
	}
}
