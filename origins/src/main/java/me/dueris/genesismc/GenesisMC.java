package me.dueris.genesismc;

import io.papermc.paper.event.player.PlayerFailMoveEvent;
import io.papermc.paper.event.server.ServerResourcesReloadedEvent;
import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.CraftCalio;
import me.dueris.calio.builder.ObjectRemapper;
import me.dueris.calio.registry.IRegistry;
import me.dueris.calio.registry.Registrar;
import me.dueris.calio.registry.impl.CalioRegistry;
import me.dueris.genesismc.command.OriginCommand;
import me.dueris.genesismc.command.PowerCommand;
import me.dueris.genesismc.command.ResourceCommand;
import me.dueris.genesismc.content.ContentTicker;
import me.dueris.genesismc.content.WaterProtBook;
import me.dueris.genesismc.content.enchantment.AnvilHandler;
import me.dueris.genesismc.content.enchantment.EnchantTableHandler;
import me.dueris.genesismc.content.enchantment.generation.StructureGeneration;
import me.dueris.genesismc.content.enchantment.generation.VillagerTradeHook;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.TagRegistryParser;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.conditions.types.*;
import me.dueris.genesismc.factory.powers.ApoliPower;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.*;
import me.dueris.genesismc.factory.powers.apoli.provider.origins.BounceSlimeBlock;
import me.dueris.genesismc.integration.PlaceHolderAPI;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.DatapackRepository;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.screen.GuiTicker;
import me.dueris.genesismc.screen.OriginChoosing;
import me.dueris.genesismc.screen.ScreenNavigator;
import me.dueris.genesismc.storage.GenesisConfigs;
import me.dueris.genesismc.storage.OriginDataContainer;
import me.dueris.genesismc.storage.nbt.NBTFixerUpper;
import me.dueris.genesismc.util.*;
import me.dueris.genesismc.util.entity.InventorySerializer;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import me.dueris.genesismc.util.render.TextureLocation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.NamedThreadFactory;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static me.dueris.genesismc.util.ColorConstants.AQUA;

public final class GenesisMC extends JavaPlugin implements Listener {
	public static final boolean isFolia = classExists("io.papermc.paper.threadedregions.RegionizedServer");
	public static final boolean isExpandedScheduler = classExists("io.papermc.paper.threadedregions.scheduler.ScheduledTask");
	public static EnumSet<Material> tool;
	public static Metrics metrics;
	public static boolean disableRender = true;
	public static String MODID = "genesismc";
	public static ConditionExecutor conditionExecutor;
	public static String apoliVersion = "1.12.4";
	public static boolean placeholderapi = false;
	public static File playerDataFolder;
	public static boolean debugOrigins = false;
	public static boolean forceUseCurrentVersion = false;
	public static OriginScheduler.OriginSchedulerTree scheduler = null;
	public static String version = Bukkit.getVersion().split("\\(MC: ")[1].replace(")", "");
	public static boolean isCompatible = false;
	public static String pluginVersion = "v1.0.0";
	public static String world_container;
	public static ExecutorService loaderThreadPool;
	public static ArrayList<String> versions = new ArrayList<>();
	public static MinecraftServer server;
	private static GenesisMC plugin;

	static {
		tool = EnumSet.of(Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_SWORD, Material.GOLDEN_AXE, Material.GOLDEN_HOE, Material.GOLDEN_PICKAXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_SWORD, Material.NETHERITE_AXE, Material.NETHERITE_HOE, Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_SWORD, Material.IRON_AXE, Material.IRON_HOE, Material.IRON_PICKAXE, Material.IRON_SHOVEL, Material.IRON_SWORD, Material.WOODEN_AXE, Material.WOODEN_HOE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_SWORD, Material.SHEARS);
	}

	static {
		versions.add("1.20.4");
		versions.add("1.20.3");
	}

	public IRegistry registry;

	public static OriginScheduler.OriginSchedulerTree getScheduler() {
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

	public static ConditionExecutor getConditionExecutor() {
		return conditionExecutor;
	}

	public static File getTmpFolder() {
		return Path.of(GenesisMC.getPlugin().getDataFolder().getAbsolutePath() + File.separator + ".tmp" + File.separator).toFile();
	}

	private static void patchPowers() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					PlayerManager.ReapplyEntityReachPowers(p);
				}
			}.runTaskLater(GenesisMC.getPlugin(), 5L);
			OriginDataContainer.loadData();
			OriginPlayerAccessor.setupPowers(p);
			PlayerManager.originValidCheck(p);
			OriginPlayerAccessor.assignPowers(p);
			if (p.isOp())
				p.sendMessage(Component.text(LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "reloadMessage")).color(TextColor.fromHexString(AQUA)));
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

	private static boolean getBooleanOrDefault(boolean arg1, boolean arg2) {
		return arg1 ? arg1 : arg2;
	}

	@Override
	public void onLoad() {
		// Prepare the plugin
		plugin = this;
		metrics = new Metrics(this, 18536);
		GenesisMC.server = ((CraftServer) Bukkit.getServer()).getServer();
		world_container = server.options.asMap().toString().split(", \\[W, universe, world-container, world-dir]=\\[")[1].split("], ")[0];
		playerDataFolder = server.playerDataStorage.getPlayerDir();
		GenesisConfigs.loadLangConfig();
		GenesisConfigs.loadMainConfig();
		GenesisConfigs.loadOrbConfig();
		GenesisMC.disableRender = GenesisConfigs.getMainConfig().getBoolean("disable-render-power");
		isCompatible = (!isFolia && (isExpandedScheduler));
		if (!isCompatible) {
			if (forceUseCurrentVersion) return;
			Bukkit.getLogger().severe("Unable to start GenesisMC due to it not being compatible with this server type");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
		}
		boolean isCorrectVersion = false;
		for (String vers : versions) {
			if (isCorrectVersion) break;
			if (vers.equalsIgnoreCase(String.valueOf(version))) {
				isCorrectVersion = true;
				break;
			}
		}

		if (!isCorrectVersion) {
			if (forceUseCurrentVersion) return;
			Bukkit.getLogger().severe("Unable to start GenesisMC due to it not being compatible with this server version");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
		}
		CraftApoli.setupDynamicThreadCount();
		ObjectRemapper.typeMappings.add(new Pair<String, String>() {
			@Override
			public String left() {
				return "origins";
			}

			@Override
			public String right() {
				return "apoli";
			}
		});
		ObjectRemapper.addObjectMapping("key", new Pair<Object, Object>() {
			@Override
			public Object left() {
				return "primary";
			}

			@Override
			public Object right() {
				return new JSONObject(Map.of("key", "key.origins.primary_active"));
			}
		});
		ObjectRemapper.addObjectMapping("key", new Pair<Object, Object>() {
			@Override
			public Object left() {
				return "secondary";
			}

			@Override
			public Object right() {
				return new JSONObject(Map.of("key", "key.origins.secondary_active"));
			}
		});
		ThreadFactory threadFactory = new NamedThreadFactory("OriginParsingPool");
		loaderThreadPool = Executors.newFixedThreadPool(CraftApoli.getDynamicThreadCount(), threadFactory);
		debugOrigins = getBooleanOrDefault(GenesisConfigs.getMainConfig().getBoolean("console-startup-debug") /* add arg compat in future version */, false);
		if (LangConfig.getLangFile() == null) {
			Bukkit.getLogger().severe("Unable to start GenesisMC due to lang not being loaded properly");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
		}
		placeholderapi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
		if (placeholderapi) {
			new PlaceHolderAPI(this).register();
		}

		try {
			Bootstrap.deleteDirectory(GenesisMC.getTmpFolder().toPath(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (!getTmpFolder().exists()) {
			getTmpFolder().mkdirs();
		}

		OriginDataContainer.loadData();
		conditionExecutor = new ConditionExecutor();

		this.registry = CalioRegistry.INSTANCE;
		// Create new registry instances
		this.registry.create(Registries.POWER, new Registrar<Power>());
		this.registry.create(Registries.ORIGIN, new Registrar<Origin>());
		this.registry.create(Registries.LAYER, new Registrar<Layer>());
		this.registry.create(Registries.CRAFT_POWER, new Registrar<ApoliPower>());
		this.registry.create(Registries.FLUID_CONDITION, new Registrar<FluidConditions.ConditionFactory>());
		this.registry.create(Registries.ENTITY_CONDITION, new Registrar<EntityConditions.ConditionFactory>());
		this.registry.create(Registries.BIOME_CONDITION, new Registrar<BiomeConditions.ConditionFactory>());
		this.registry.create(Registries.BIENTITY_CONDITION, new Registrar<BiEntityConditions.ConditionFactory>());
		this.registry.create(Registries.BLOCK_CONDITION, new Registrar<BlockConditions.ConditionFactory>());
		this.registry.create(Registries.ITEM_CONDITION, new Registrar<ItemConditions.ConditionFactory>());
		this.registry.create(Registries.DAMAGE_CONDITION, new Registrar<DamageConditions.ConditionFactory>());
		this.registry.create(Registries.TEXTURE_LOCATION, new Registrar<TextureLocation>());
		this.registry.create(Registries.PACK_SOURCE, new Registrar<DatapackRepository>());

		try {
			CodeSource src = Utils.class.getProtectionDomain().getCodeSource();
			URL jar = src.getLocation();
			ZipInputStream zip = new ZipInputStream(jar.openStream());
			while (true) {
				ZipEntry entry = zip.getNextEntry();
				if (entry == null)
					break;
				String name = entry.getName();

				if (!name.startsWith("datapack/")) continue;
				if (!name.startsWith("datapack/builtin")) continue;
				if (FilenameUtils.getExtension(name).equals("zip")) continue;
				if (name.equals("datapack/")) continue;

				name = name.substring(9);
				File file = new File(this.getTmpFolder().getAbsolutePath().replace(".\\", "") + File.separator + name);
				if (!file.getName().contains(".")) {
					Files.createDirectory(Path.of(file.getAbsolutePath()));
					continue;
				}

				File parentDir = file.getParentFile();
				if (!parentDir.exists()) {
					parentDir.mkdirs();
				}

				Files.writeString(Path.of(file.getAbsolutePath()), new String(zip.readAllBytes()));
			}
			zip.close();
		} catch (Exception e) {
		}

		this.registry.retrieve(Registries.PACK_SOURCE).register(new DatapackRepository(GenesisMC.originIdentifier("builtin"), this.getTmpFolder().toPath()));
		this.registry.retrieve(Registries.PACK_SOURCE).register(new DatapackRepository(GenesisMC.originIdentifier("default"), this.server.getWorldPath(LevelResource.DATAPACK_DIR)));
	}

	@Override
	public void onEnable() {
		try {
			// CraftApoli.loadOrigins(this.registry);
			// Register builtin instances
			Method registerMethod;
			try {
				registerMethod = CraftPower.class.getDeclaredMethod("registerBuiltinPowers");
				registerMethod.setAccessible(true);
				registerMethod.invoke(null);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
					 InvocationTargetException e) {
				e.printStackTrace();
			}

			ConditionExecutor.registerAll();
			if (Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer")) {
				CraftPower.registerNewPower(ModelColor.ModelTransformer.class);
			}
			TextureLocation.parseAll();
			// Start calio parser for data driven instances
			final CraftCalio calio = CraftCalio.INSTANCE;
			((Registrar<DatapackRepository>) this.registry.retrieve(Registries.PACK_SOURCE)).values().forEach(repo -> {
				calio.addDatapackPath(repo.getPath());
			});

			calio.getBuilder().addAccessorRoot(
				"powers",
				Registries.POWER,
				new Power(true), 0
			);
			calio.getBuilder().addAccessorRoot(
				"origins",
				Registries.ORIGIN,
				new Origin(true), 1
			);
			calio.getBuilder().addAccessorRoot(
				"origin_layers",
				Registries.LAYER,
				new Layer(true), 2
			);
			calio.start(debugOrigins, this.loaderThreadPool);
			// End calio parsing
		} catch (InstantiationException | IllegalAccessException |
				 IOException ee) {
			ee.printStackTrace();
		}

		GenesisMC.scheduler = new OriginScheduler.OriginSchedulerTree();
		GenesisMC.scheduler.runTaskTimer(this, 0, 1);

		WaterProtBook.init();
		start();
		patchPowers();
		TagRegistryParser.runParse();
		try {
			NBTFixerUpper.runFixerUpper();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC]   ____                          _       __  __   ____").color(TextColor.fromHexString("#b9362f")));
		Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC]  / ___|  ___  _ __    ___  ___ (_) ___ |  \\/  | / ___|").color(TextColor.fromHexString("#bebe42")));
		Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] | |  _  / _ \\| '_ \\  / _ \\/ __|| |/ __|| |\\/| || |").color(TextColor.fromHexString("#4fec4f")));
		Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] | |_| ||  __/| | | ||  __/\\__ \\| |\\__ \\| |  | || |___").color(TextColor.fromHexString("#4de4e4")));
		Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC]  \\____| \\___||_| |_| \\___||___/|_||___/|_|  |_| \\____|").color(TextColor.fromHexString("#333fb7")));
		Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC]                     ~ Made by Dueris ~        ").color(TextColor.fromHexString("#dd50ff")));
		Bukkit.getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		Bukkit.getServer().getConsoleSender().sendMessage("");
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "* Loading Version GenesisMC-{minecraftVersion-versionNumber} // CraftApoli-{apoliVersion}"
			.replace("minecraftVersion", "mc" + version)
			.replace("versionNumber", pluginVersion)
			.replace("apoliVersion", apoliVersion)
		);
		VersionControl.pluginVersionCheck();
		Bukkit.getServer().getConsoleSender().sendMessage("");
		if (debugOrigins) {
			Bukkit.getServer().getConsoleSender().sendMessage("* (-debugOrigins={true}) || BEGINNING DEBUG {");
			Bukkit.getServer().getConsoleSender().sendMessage("  - Loaded @1 powers".replace("@1", String.valueOf(this.registry.retrieve(Registries.CRAFT_POWER).registrySize())));
			Bukkit.getServer().getConsoleSender().sendMessage("  - Loaded @4 layers".replace("@4", String.valueOf(this.registry.retrieve(Registries.LAYER).registrySize())));
			Bukkit.getServer().getConsoleSender().sendMessage("  - Loaded @2 origins = [".replace("@2", String.valueOf(this.registry.retrieve(Registries.ORIGIN).registrySize())));
			((Registrar<Origin>) this.registry.retrieve(Registries.ORIGIN)).forEach((k, o) -> {
				Bukkit.getServer().getConsoleSender().sendMessage("     () -> {@3}".replace("@3", o.getTag()));
			});
			Bukkit.getServer().getConsoleSender().sendMessage("  ]");
			Bukkit.getServer().getConsoleSender().sendMessage("  - Power thread starting with {originScheduler}".replace("originScheduler", GenesisMC.scheduler.toString()));
			Bukkit.getServer().getConsoleSender().sendMessage("  - Lang testing = {true}");
			Bukkit.getServer().getConsoleSender().sendMessage("}");
		}
		Bukkit.getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		// Shutdown executor, we dont need it anymore
		loaderThreadPool.shutdown();
		OriginCommand.commandProvidedTaggedRecipies.addAll(RecipePower.taggedRegistry.keySet());
		OriginCommand.commandProvidedPowers.addAll(((Registrar<Power>) this.registry.retrieve(Registries.POWER)).values().stream().toList());
		OriginCommand.commandProvidedOrigins.addAll(((Registrar<Origin>) this.registry.retrieve(Registries.ORIGIN)).values().stream().toList());
		OriginCommand.commandProvidedLayers.addAll(((Registrar<Layer>) this.registry.retrieve(Registries.LAYER)).values().stream().toList());
		ResourceCommand.registeredBars.putAll(Resource.registeredBars);

		OriginCommand.register(((CraftServer) Bukkit.getServer()).getServer().vanillaCommandDispatcher.getDispatcher());
		ResourceCommand.register(((CraftServer) Bukkit.getServer()).getServer().vanillaCommandDispatcher.getDispatcher());
		PowerCommand.register(((CraftServer) Bukkit.getServer()).getServer().vanillaCommandDispatcher.getDispatcher());
		try {
			Bootstrap.deleteDirectory(GenesisMC.getTmpFolder().toPath(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void start() {
		getServer().getPluginManager().registerEvents(new InventorySerializer(), this);
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new CooldownUtils(), this);
		getServer().getPluginManager().registerEvents(new PlayerManager(), this);
		getServer().getPluginManager().registerEvents(new EnchantTableHandler(), this);
		getServer().getPluginManager().registerEvents(new AnvilHandler(), this);
		getServer().getPluginManager().registerEvents(new OriginChoosing(), this);
		getServer().getPluginManager().registerEvents(new ScreenNavigator(), this);
		getServer().getPluginManager().registerEvents(new OriginCommand(), this);
		getServer().getPluginManager().registerEvents(new ContentTicker(), this);
		getServer().getPluginManager().registerEvents(new BounceSlimeBlock(), this);
		getServer().getPluginManager().registerEvents(new BiEntityConditions(), this);
		getServer().getPluginManager().registerEvents(new LogoutBugWorkaround(), this);
		getServer().getPluginManager().registerEvents(new VillagerTradeHook(), this);
		getServer().getPluginManager().registerEvents(new OriginScheduler.OriginSchedulerTree(), this);
		getServer().getPluginManager().registerEvents(new StructureGeneration(), this);
		getServer().getPluginManager().registerEvents(new KeybindingUtils(), this);

		BukkitRunnable[] independentTickers = {new GuiTicker(), new ContentTicker(), new OriginCommand()};
		WaterBreathe.start();
		for (BukkitRunnable runnable : independentTickers) {
			runnable.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
		}

		EntityGroupManager.INSTANCE.startTick();
	}

	@Override
	public void onDisable() {
		for(Player player : Bukkit.getOnlinePlayers()){
			player.getPersistentDataContainer().set(GenesisMC.identifier("originLayer"), PersistentDataType.STRING, CraftApoli.toSaveFormat(OriginPlayerAccessor.getOrigin(player), player));
			OriginPlayerAccessor.unassignPowers(player);
			OriginDataContainer.unloadData(player);
		}
		CraftApoli.unloadData();
		OriginPlayerAccessor.playerPowerMapping.clear();
		OriginPlayerAccessor.powersAppliedList.clear();
		OriginCommand.commandProvidedLayers.clear();
		OriginCommand.commandProvidedOrigins.clear();
		OriginCommand.commandProvidedPowers.clear();
		OriginCommand.commandProvidedTaggedRecipies.clear();
		RecipePower.recipeMapping.clear();
		RecipePower.tags.clear();
		this.registry.clearRegistries();
		scheduler.cancel();
		EntityGroupManager.stop();

		getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] " + LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "disable")).color(TextColor.fromHexString("#fb5454")));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void lagBackPatch(PlayerFailMoveEvent e) {
		e.setAllowed(true);
		e.setLogWarning(false);
	}

	@EventHandler
	/**
	 * This action is purely for development reasons, which is why there is a broadcast saying that its not supported.
	 * Its NOT intended for normal gameplay, its just a fast way to reload parsing to test new data
	 */
	public void reload(ServerResourcesReloadedEvent e) {
		if (!(e.getCause().equals(ServerResourcesReloadedEvent.Cause.COMMAND) || e.getCause().equals(ServerResourcesReloadedEvent.Cause.PLUGIN)))
			return;
		Bukkit.broadcast(Component.text("GENESIS IS CONDUCTING A RESOURCE RELOAD, DO NOT REPORT BUGS OR CRASHES TO THE AUTHOR, THIS ACTION IS UNSUPPORTED").color(TextColor.color(230, 37, 23)));

		onDisable();
		onLoad();
		onEnable();
	}
}
