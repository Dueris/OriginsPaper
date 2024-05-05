package me.dueris.genesismc;

import com.mojang.brigadier.tree.CommandNode;
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
import me.dueris.genesismc.content.ContentTicker;
import me.dueris.genesismc.content.WaterProtBook;
import me.dueris.genesismc.content.enchantment.AnvilHandler;
import me.dueris.genesismc.content.enchantment.EnchantTableHandler;
import me.dueris.genesismc.content.enchantment.generation.StructureGeneration;
import me.dueris.genesismc.content.enchantment.generation.VillagerTradeHook;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.actions.types.BiEntityActions;
import me.dueris.genesismc.factory.actions.types.BlockActions;
import me.dueris.genesismc.factory.actions.types.EntityActions;
import me.dueris.genesismc.factory.actions.types.ItemActions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.conditions.types.*;
import me.dueris.genesismc.factory.powers.ApoliPower;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.Cooldown;
import me.dueris.genesismc.factory.powers.apoli.ModelColor;
import me.dueris.genesismc.factory.powers.apoli.RecipePower;
import me.dueris.genesismc.factory.powers.apoli.WaterBreathe;
import me.dueris.genesismc.factory.powers.apoli.provider.origins.BounceSlimeBlock;
import me.dueris.genesismc.integration.PlaceHolderAPI;
import me.dueris.genesismc.integration.pehuki.CraftPehuki;
import me.dueris.genesismc.registry.BuiltinRegistry;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.DatapackRepository;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.screen.ChoosingPage;
import me.dueris.genesismc.screen.GuiTicker;
import me.dueris.genesismc.screen.RandomOriginPage;
import me.dueris.genesismc.screen.ScreenNavigator;
import me.dueris.genesismc.storage.OriginConfiguration;
import me.dueris.genesismc.storage.OriginDataContainer;
import me.dueris.genesismc.storage.nbt.NBTFixerUpper;
import me.dueris.genesismc.util.*;
import me.dueris.genesismc.util.entity.InventorySerializer;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

import static me.dueris.genesismc.util.ColorConstants.AQUA;

public final class GenesisMC extends JavaPlugin implements Listener {
    public static final boolean isFolia = classExists("io.papermc.paper.threadedregions.RegionizedServer");
    public static final boolean isExpandedScheduler = classExists("io.papermc.paper.threadedregions.scheduler.ScheduledTask");
    public static List<Runnable> preShutdownTasks = new ArrayList<>();
    public static EnumSet<Material> tool;
    public static Metrics metrics;
    public static ConditionExecutor conditionExecutor;
    public static String apoliVersion = "1.12.8";
    public static boolean placeholderapi = false;
    public static File playerDataFolder;
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
        versions.add("1.20.5");
        versions.add("1.20.6");
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

    public static File getTmpFolder() {
        return Path.of(GenesisMC.getPlugin().getDataFolder().getAbsolutePath() + File.separator + ".tmp" + File.separator).toFile();
    }

    private static void patchPowers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            OriginDataContainer.loadData();
            OriginPlayerAccessor.setupPowers(p);
            PlayerManager.originValidCheck(p);
            OriginPlayerAccessor.assignPowers(p);
            if (p.isOp())
                p.sendMessage(Component.text("Origins Reloaded!").color(TextColor.fromHexString(AQUA)));
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
        metrics = new Metrics(this, 18536);
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
        // Our version of restricted_armor allows handling of both.
        ObjectRemapper.typeAlias.put("apoli:conditioned_restrict_armor", "apoli:restrict_armor");
        ThreadFactory threadFactory = new NamedTickThreadFactory("OriginParsingPool");
        CraftPower.tryPreloadClass(AsyncTaskWorker.class); // Preload worker
        placeholderapi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        if (placeholderapi) new PlaceHolderAPI(this).register();

        try {
            Bootstrap.deleteDirectory(GenesisMC.getTmpFolder().toPath(), true);
        } catch (Throwable e) {
            throwable(e, false);
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
        this.registry.create(Registries.ENTITY_ACTION, new Registrar<EntityActions.ActionFactory>());
        this.registry.create(Registries.ITEM_ACTION, new Registrar<ItemActions.ActionFactory>());
        this.registry.create(Registries.BLOCK_ACTION, new Registrar<BlockActions.ActionFactory>());
        this.registry.create(Registries.BIENTITY_ACTION, new Registrar<BiEntityActions.ActionFactory>());
        this.registry.create(Registries.TEXTURE_LOCATION, new Registrar<TextureLocation>());
        this.registry.create(Registries.PACK_SOURCE, new Registrar<DatapackRepository>());
        this.registry.create(Registries.CHOOSING_PAGE, new Registrar<ChoosingPage>());

        Utils.unpackOriginPack();
        List<File> list = new ArrayList<>(Arrays.asList(server.getWorldPath(LevelResource.DATAPACK_DIR).toFile().listFiles()));
        list.forEach(pack -> {
            if (pack.isFile() && pack.getName().endsWith(".zip")) {
                Utils.unzip(pack.getPath(), getTmpFolder().getAbsolutePath());
            }
        });
        int avalibleJVMThreads = Runtime.getRuntime().availableProcessors() * 2;
        int dynamic_thread_count = avalibleJVMThreads < 4 ? avalibleJVMThreads : Math.min(avalibleJVMThreads, OriginConfiguration.getConfiguration().getInt("max-loader-threads"));
        loaderThreadPool = Executors.newFixedThreadPool(dynamic_thread_count, threadFactory);

        this.registry.retrieve(Registries.PACK_SOURCE).register(new DatapackRepository(GenesisMC.originIdentifier("builtin"), getTmpFolder().toPath()));
        this.registry.retrieve(Registries.PACK_SOURCE).register(new DatapackRepository(GenesisMC.originIdentifier("default"), server.getWorldPath(LevelResource.DATAPACK_DIR)));

        try {
            // Register builtin instances
            Reflector.accessMethod$Invoke("registerBuiltinPowers", CraftPower.class, null);

            ConditionExecutor.registerAll();
            Actions.registerAll();
            if (Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer")) {
                CraftPower.registerNewPower(ModelColor.ModelTransformer.class);
            }
            TextureLocation.parseAll();
            // Start calio parser for data driven instances
            final CraftCalio calio = CraftCalio.INSTANCE;
            ((Registrar<DatapackRepository>) this.registry.retrieve(Registries.PACK_SOURCE)).values().stream()
                    .map(DatapackRepository::getPath).forEach(calio::addDatapackPath);

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
            calio.start(OriginConfiguration.getConfiguration().getBoolean("debug"), loaderThreadPool);
            BuiltinRegistry.bootstrap();
            // End calio parsing
        } catch (Throwable e) {
            throwable(e, true);
        }

        debug(Component.text("  - Loaded @1 powers".replace("@1", String.valueOf(this.registry.retrieve(Registries.POWER).registrySize()))));
        debug(Component.text("  - Loaded @4 layers".replace("@4", String.valueOf(this.registry.retrieve(Registries.LAYER).registrySize()))));
        debug(Component.text("  - Loaded @2 origins = [".replace("@2", String.valueOf(this.registry.retrieve(Registries.ORIGIN).registrySize()))));
        ((Registrar<Origin>) this.registry.retrieve(Registries.ORIGIN)).forEach((u, o) -> debug(Component.text("     () -> {@3}".replace("@3", o.getTag()))));
        debug(Component.text("  ]"));
        try {
            NBTFixerUpper.runFixerUpper();
        } catch (Throwable e) {
            throwable(e, false);
        }

        GenesisMC.scheduler = new OriginScheduler.OriginSchedulerTree();
        GenesisMC.scheduler.runTaskTimer(this, 0, 1);
        new BukkitRunnable() {
            @Override
            public void run() {
                GenesisMC.scheduler.tickAsyncScheduler();
            }
        }.runTaskTimerAsynchronously(GenesisMC.getPlugin(), 0, 1);
		ConcurrentHashMap<Player, List<ApoliPower>> playerListConcurrentHashMap = OriginScheduler.tickedPowers;
		for (Player player : Bukkit.getOnlinePlayers()) {
            OriginPlayerAccessor.powersAppliedList.putIfAbsent(player, new ConcurrentLinkedQueue<>());
			playerListConcurrentHashMap.put(player, new ArrayList<>());
		}
		WaterProtBook.init();
        start();
        patchPowers();
        debug(Component.text("  - Power thread starting with {originScheduler}".replace("originScheduler", GenesisMC.scheduler.toString())));
        debug(Component.text("}"));

        // Shutdown executor, we don't need it anymore
        loaderThreadPool.shutdown();
        OriginCommand.commandProvidedPowers.addAll(((Registrar<Power>) this.registry.retrieve(Registries.POWER)).values().stream().toList());
        OriginCommand.commandProvidedOrigins.addAll(((Registrar<Origin>) this.registry.retrieve(Registries.ORIGIN)).values().stream().toList());
        OriginCommand.commandProvidedLayers.addAll(((Registrar<Layer>) this.registry.retrieve(Registries.LAYER)).values().stream().filter(Layer::isEnabled).toList());

        if (((CraftServer) Bukkit.getServer()).getServer().vanillaCommandDispatcher.getDispatcher().getRoot().getChildren().stream().map(CommandNode::getName).toList().contains("origin")) {
            // Already registered, lets change that ;)
            ((CraftServer) Bukkit.getServer()).getServer().vanillaCommandDispatcher.getDispatcher().getRoot().removeCommand("origin");
            ((CraftServer) Bukkit.getServer()).getServer().vanillaCommandDispatcher.getDispatcher().getRoot().removeCommand("power");
        }
        OriginCommand.register(((CraftServer) Bukkit.getServer()).getServer().vanillaCommandDispatcher.getDispatcher());
        PowerCommand.register(((CraftServer) Bukkit.getServer()).getServer().vanillaCommandDispatcher.getDispatcher());
        // Load addons
        CraftPehuki.onLoad();
        CraftPower.tryPreloadClass(CraftApoli.class);

        try {
            Bootstrap.deleteDirectory(GenesisMC.getTmpFolder().toPath(), true);
        } catch (Throwable e) {
            throwable(e, false);
        }

        Bukkit.getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    public void throwable(Throwable throwable, boolean kill) {
        String[] stacktrace = {"\n"};
        Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString).forEach(string -> stacktrace[0] += ("\tat " + string + "\n"));
        this.getLogger().severe("An unhandled exception occurred when starting Genesis!");
        this.getLogger().severe(stacktrace[0]);
        if (kill) Bukkit.getPluginManager().disablePlugin(this);
    }

    public void printComponent(Component component) {
        Bukkit.getServer().getConsoleSender().sendMessage(component);
    }

    public void debug(Component component) {
        if (OriginConfiguration.getConfiguration().getBoolean("debug")) printComponent(component);
    }

    private void start() {
        getServer().getPluginManager().registerEvents(new InventorySerializer(), this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new PlayerManager(), this);
        getServer().getPluginManager().registerEvents(new EnchantTableHandler(), this);
        getServer().getPluginManager().registerEvents(new AnvilHandler(), this);
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
        getServer().getPluginManager().registerEvents(new AsyncUpgradeTracker(), this);

        BukkitRunnable[] independentTickers = {new GuiTicker(), new ContentTicker(), new OriginCommand(), new Cooldown()};
        WaterBreathe.start();
        for (BukkitRunnable runnable : independentTickers) {
            runnable.runTaskTimerAsynchronously(GenesisMC.getPlugin(), 0, 1);
        }
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.closeInventory(); // Ensure that all choosing players have closed inventories during reload
            player.getPersistentDataContainer().set(GenesisMC.identifier("originLayer"), PersistentDataType.STRING, CraftApoli.toSaveFormat(OriginPlayerAccessor.getOrigin(player), player));
            OriginPlayerAccessor.unassignPowers(player);
            OriginDataContainer.unloadData(player);
        }
        preShutdownTasks.forEach(Runnable::run);
        CraftApoli.unloadData();
        OriginPlayerAccessor.playerPowerMapping.clear();
        OriginPlayerAccessor.powersAppliedList.clear();
        OriginCommand.commandProvidedLayers.clear();
        OriginCommand.commandProvidedOrigins.clear();
        OriginCommand.commandProvidedPowers.clear();
        RecipePower.recipeMapping.clear();
        RecipePower.tags.clear();
        this.registry.clearRegistries();
        scheduler.cancel();
        AsyncTaskWorker.shutdown();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void lagBackPatch(PlayerFailMoveEvent e) {
        e.setAllowed(true);
        e.setLogWarning(false);
    }

    @EventHandler
    /*
      This action is purely for development reasons, which is why there is a broadcast saying that its not supported.
      Its NOT intended for normal gameplay, its just a fast way to reload parsing to test new data
     */
    public void reload(ServerResourcesReloadedEvent e) {
        if (!(e.getCause().equals(ServerResourcesReloadedEvent.Cause.COMMAND) || e.getCause().equals(ServerResourcesReloadedEvent.Cause.PLUGIN)))
            return;
        Bukkit.broadcast(Component.text("GENESIS IS CONDUCTING A RESOURCE RELOAD, DO NOT REPORT BUGS OR CRASHES TO THE AUTHOR, THIS ACTION IS UNSUPPORTED").color(TextColor.color(230, 37, 23)));

        onDisable();
        onLoad();
        onEnable();
    }

    @EventHandler
    /*
      Post-Startup of the plugin lifecycle, where registries will be finalized after plugins have loaded
      @param e
     */
    public void loadEvent(ServerLoadEvent e) {
        ChoosingPage.registerInstances();
        ScreenNavigator.layerPages.values().forEach((pages) -> pages.add(pages.size(), new RandomOriginPage()));
    }
}
