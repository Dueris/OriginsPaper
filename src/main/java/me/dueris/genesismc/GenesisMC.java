package me.dueris.genesismc;

import io.papermc.paper.event.player.PlayerFailMoveEvent;
import io.papermc.paper.event.server.ServerResourcesReloadedEvent;
import me.dueris.genesismc.choosing.ChoosingCustomOrigins;
import me.dueris.genesismc.choosing.ChoosingMain;
import me.dueris.genesismc.choosing.GuiTicker;
import me.dueris.genesismc.commands.OriginCommand;
import me.dueris.genesismc.commands.PowerCommand;
import me.dueris.genesismc.commands.ResourceCommand;
import me.dueris.genesismc.commands.subcommands.origin.Info.InInfoCheck;
import me.dueris.genesismc.commands.subcommands.origin.Info.Info;
import me.dueris.genesismc.commands.subcommands.origin.Recipe;
import me.dueris.genesismc.enchantments.Anvil;
import me.dueris.genesismc.enchantments.EnchantTable;
import me.dueris.genesismc.enchantments.generation.StructureGeneration;
import me.dueris.genesismc.enchantments.generation.VillagerTradeHook;
import me.dueris.genesismc.entity.InventorySerializer;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.TagRegistry;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.conditions.CraftCondition;
import me.dueris.genesismc.factory.conditions.biEntity.BiEntityCondition;
import me.dueris.genesismc.factory.conditions.biome.BiomeCondition;
import me.dueris.genesismc.factory.conditions.block.BlockCondition;
import me.dueris.genesismc.factory.conditions.damage.DamageCondition;
import me.dueris.genesismc.factory.conditions.entity.EntityCondition;
import me.dueris.genesismc.factory.conditions.fluid.FluidCondition;
import me.dueris.genesismc.factory.conditions.item.ItemCondition;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.block.RecipePower;
import me.dueris.genesismc.factory.powers.block.WaterBreathe;
import me.dueris.genesismc.factory.powers.player.ModelColor;
import me.dueris.genesismc.factory.powers.simple.origins.BounceSlimeBlock;
import me.dueris.genesismc.factory.powers.simple.origins.MimicWarden;
import me.dueris.genesismc.factory.powers.world.EntityGroupManager;
import me.dueris.genesismc.files.GenesisDataFiles;
import me.dueris.genesismc.files.nbt.FixerUpper;
import me.dueris.genesismc.hooks.papi.PlaceholderApiExtension;
import me.dueris.genesismc.items.GenesisItems;
import me.dueris.genesismc.items.WaterProtItem;
import me.dueris.genesismc.utils.KeybindUtils;
import me.dueris.genesismc.utils.LogoutBugWorkaround;
import me.dueris.genesismc.utils.Metrics;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.translation.LangConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.NamedThreadFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static me.dueris.genesismc.PlayerHandler.ReapplyEntityReachPowers;
import static me.dueris.genesismc.factory.powers.simple.origins.MimicWarden.getParticleTasks;
import static me.dueris.genesismc.utils.text.BukkitColour.AQUA;
import static me.dueris.genesismc.utils.text.BukkitColour.RED;

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
    public static File playerDataFolder = MinecraftServer.getServer().playerDataStorage.getPlayerDir();
    public static boolean debugOrigins = false;
    public static boolean forceUseCurrentVersion = false;
    public static boolean forceWatchdogStop = true;
    public static boolean fixPaperExploits = true;
    public static OriginScheduler.OriginSchedulerTree scheduler = null;
    public static String version = Bukkit.getVersion().split("\\(MC: ")[1].replace(")", "");
    public static boolean isCompatible = false;
    public static String pluginVersion = "v0.2.7";
    public static String world_container = MinecraftServer.getServer().options.asMap().toString().split(", \\[W, universe, world-container, world-dir]=\\[")[1].split("], ")[0];
    public static ExecutorService loaderThreadPool;
    public static ArrayList<String> versions = new ArrayList<>();
    private static GenesisMC plugin;

    static {
        tool = EnumSet.of(Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_SWORD, Material.GOLDEN_AXE, Material.GOLDEN_HOE, Material.GOLDEN_PICKAXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_SWORD, Material.NETHERITE_AXE, Material.NETHERITE_HOE, Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_SWORD, Material.IRON_AXE, Material.IRON_HOE, Material.IRON_PICKAXE, Material.IRON_SHOVEL, Material.IRON_SWORD, Material.WOODEN_AXE, Material.WOODEN_HOE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_SWORD, Material.SHEARS);
    }

    static {
        versions.add("1.20.4");
        versions.add("1.20.3");
    }

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
                    ReapplyEntityReachPowers(p);
                }
            }.runTaskLater(GenesisMC.getPlugin(), 5L);
            OriginDataContainer.loadData();
            OriginPlayerUtils.setupPowers(p);
            PlayerHandler.originValidCheck(p);
            OriginPlayerUtils.assignPowers(p);
            if (p.isOp())
                p.sendMessage(Component.text(LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "reloadMessage")).color(TextColor.fromHexString(AQUA)));
        }
    }

    public static GenesisMC getPlugin() {
        return plugin;
    }

//    public static void registerEnchantment(Enchantment enchantment) {
//        if (Enchantment.getByKey(enchantment.getKey()) != null) return;
//        try {
//            Field f = Enchantment.class.getDeclaredField("acceptingNew");
//            f.setAccessible(true);
//            f.set(null, true);
//            CraftEnchantment
//            Enchantment.registerEnchantment(enchantment);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        // It's been registered!
//    }

    public static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean getOrDefault(boolean arg1, boolean arg2) {
        boolean finaL = arg2;
        if (arg1) {
            finaL = arg1;
        }
        return finaL;
    }

    public static void sendDebug(Object string) {
        if (debugOrigins) {
            System.out.println(string);
        }
    }

    @Override
    public void onEnable() {
        plugin = this;
        metrics = new Metrics(this, 18536);
        GenesisDataFiles.loadLangConfig();
        GenesisDataFiles.loadMainConfig();
        GenesisDataFiles.loadOrbConfig();
        forceWatchdogStop = GenesisDataFiles.getMainConfig().getBoolean("disable-watchdog");
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
//        if (forceWatchdogStop) {
//            WatchdogThread.doStop();
//        }
        CraftApoli.setupDynamicThreadCount();
        ThreadFactory threadFactory = new NamedThreadFactory("OriginParsingPool");
        loaderThreadPool = Executors.newFixedThreadPool(CraftApoli.getDynamicThreadCount(), threadFactory);
        debugOrigins = getOrDefault(GenesisDataFiles.getMainConfig().getBoolean("console-startup-debug") /* add arg compat in future version */, false);
        if (LangConfig.getLangFile() == null) {
            Bukkit.getLogger().severe("Unable to start GenesisMC due to lang not being loaded properly");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
        placeholderapi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        if (placeholderapi) {
            new PlaceholderApiExtension(this).register();
        }

        if (!getTmpFolder().exists()) {
            getTmpFolder().mkdirs();
        }

        GenesisMC.disableRender = GenesisDataFiles.getMainConfig().getBoolean("disable-render-power");
        GenesisMC.fixPaperExploits = GenesisDataFiles.getMainConfig().getBoolean("modify-configs-to-fix-bugs");
        if (fixPaperExploits) {
            File globalDefault = Paths.get("config" + File.separator + "paper-world-defaults.yml").toFile();
            YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(globalDefault);
            yamlConfig.set("fixes.disable-unloaded-chunk-enderpearl-exploit", false);
        }
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        server.paperConfigurations.reloadConfigs(server);

        me.dueris.genesismc.OriginDataContainer.loadData();
        // Pre-load condition types to prevent constant calling
        CraftCondition.bientity = new BiEntityCondition();
        CraftCondition.biome = new BiomeCondition();
        CraftCondition.blockCon = new BlockCondition();
        CraftCondition.damage = new DamageCondition();
        CraftCondition.entity = new EntityCondition();
        CraftCondition.fluidCon = new FluidCondition();
        CraftCondition.item = new ItemCondition();
        // Pre-load end
        conditionExecutor = new ConditionExecutor();
        try {
            CraftApoli.loadOrigins();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // Register builtin powers
        Method registerMethod;
        try {
            registerMethod = CraftPower.class.getDeclaredMethod("registerBuiltinPowers");
            registerMethod.setAccessible(true);
            registerMethod.invoke(null);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }

        if(Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer")){
            try {
                CraftPower.registerNewPower(ModelColor.ModelTransformer.class);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }

        OriginScheduler.OriginSchedulerTree scheduler = new OriginScheduler.OriginSchedulerTree();
        GenesisMC.scheduler = scheduler;
        scheduler.runTaskTimer(this, 0, 1);

        WaterProtItem.init();
        start();
        patchPowers();
        TagRegistry.runParse();
        try {
            FixerUpper.runFixerUpper();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bukkit.getCommandMap().register("origin", new OriginCommand());
        Bukkit.getCommandMap().register("resource", new ResourceCommand());
        Bukkit.getCommandMap().register("power", new PowerCommand());
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
            Bukkit.getServer().getConsoleSender().sendMessage("  - Loaded @1 powers".replace("@1", String.valueOf(CraftPower.getRegistry().toArray().length)));
            Bukkit.getServer().getConsoleSender().sendMessage("  - Loaded @4 layers".replace("@4", String.valueOf(CraftApoli.getLayers().toArray().length)));
            Bukkit.getServer().getConsoleSender().sendMessage("  - Loaded @2 origins = [".replace("@2", String.valueOf(CraftApoli.getOrigins().toArray().length)));
            for (OriginContainer originContainer : CraftApoli.getOrigins()) {
                Bukkit.getServer().getConsoleSender().sendMessage("     () -> {@3}".replace("@3", originContainer.getTag()));
            }
            Bukkit.getServer().getConsoleSender().sendMessage("  ]");
            Bukkit.getServer().getConsoleSender().sendMessage("  - Power thread starting with {originScheduler}".replace("originScheduler", GenesisMC.scheduler.toString()));
            Bukkit.getServer().getConsoleSender().sendMessage("  - Lang testing = {true}");
            Bukkit.getServer().getConsoleSender().sendMessage("}");
        }
        Bukkit.getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        // Shutdown executor, we dont need it anymore
        loaderThreadPool.shutdown();
        try {
            Bootstrap.deleteDirectory(GenesisMC.getTmpFolder().toPath(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void start() {
        getServer().getPluginManager().registerEvents(new InventorySerializer(), this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new CooldownManager(), this);
        getServer().getPluginManager().registerEvents(new PlayerHandler(), this);
        getServer().getPluginManager().registerEvents(new EnchantTable(), this);
        getServer().getPluginManager().registerEvents(new Anvil(), this);
        getServer().getPluginManager().registerEvents(new KeybindUtils(), this);
        getServer().getPluginManager().registerEvents(new ChoosingMain(), this);
        getServer().getPluginManager().registerEvents(new ChoosingCustomOrigins(), this);
        getServer().getPluginManager().registerEvents(new Recipe(), this);
        getServer().getPluginManager().registerEvents(new Info(), this);
        getServer().getPluginManager().registerEvents(new InventorySerializer(), this);
        getServer().getPluginManager().registerEvents(new GenesisItems(), this);
        getServer().getPluginManager().registerEvents(new MimicWarden(), this);
        getServer().getPluginManager().registerEvents(new BounceSlimeBlock(), this);
        getServer().getPluginManager().registerEvents(new BiEntityCondition(), this);
        getServer().getPluginManager().registerEvents(new LogoutBugWorkaround(), this);
        getServer().getPluginManager().registerEvents(new VillagerTradeHook(), this);
        getServer().getPluginManager().registerEvents(new OriginScheduler.OriginSchedulerTree(), this);
        getServer().getPluginManager().registerEvents(new KeybindUtils(), this);
        getServer().getPluginManager().registerEvents(new StructureGeneration(), this);
//        if (getServer().getPluginManager().isPluginEnabled("SkinsRestorer")) {
//            try {
//                getServer().getPluginManager().registerEvents(new PlayerRender.ModelColor(), this);
//                getServer().getConsoleSender().sendMessage(Component.text(LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.skinRestorer.present")).color(TextColor.fromHexString(AQUA)));
//            } catch (Exception ignored) {
//                // ignored
//            }
//        } else {
//            getServer().getConsoleSender().sendMessage(Component.text(LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.skinRestorer.absent")).color(TextColor.fromHexString(AQUA)));
//        }
        GuiTicker forced = new GuiTicker();
        forced.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
        GenesisItems items = new GenesisItems();
        items.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
        InInfoCheck info = new InInfoCheck();
        info.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
        WaterBreathe waterBreathe = new WaterBreathe();
        new BukkitRunnable() {
            @Override
            public void run() {
                waterBreathe.run();
            }
        }.runTaskTimer(GenesisMC.getPlugin(), 0, 20);

        EntityGroupManager.INSTANCE.startTick();
    }

    @Override
    public void onDisable() {
        me.dueris.genesismc.OriginDataContainer.unloadAllData();
        CraftApoli.unloadData();
        OriginPlayerUtils.playerPowerMapping.clear();
        OriginPlayerUtils.powersAppliedList.clear();
        RecipePower.recipeMapping.clear();
        RecipePower.tags.clear();
        CraftPower.getRegistry().clear();
        CraftPower.getKeyedRegistry().clear();
        scheduler.cancel();
        EntityGroupManager.stop();

        for (int taskId : getParticleTasks().values()) {
            getServer().getScheduler().cancelTask(taskId);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            Team team = p.getScoreboard().getTeam("origin-players");
            if (team != null) team.removeEntity(p);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "skin clear " + p.getName());

            //closes all open menus, they would cause errors if not closed
            if (p.getOpenInventory().getTitle().startsWith("Choosing Menu") && p.getOpenInventory().getTitle().startsWith("Custom Origins") && p.getOpenInventory().getTitle().startsWith("Expanded Origins") && p.getOpenInventory().getTitle().startsWith("Custom Origin") && p.getOpenInventory().getTitle().startsWith("Origin")) {
                p.closeInventory();
            }
        }

        getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] " + LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "disable")).color(TextColor.fromHexString(RED)));
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
    public void reload(ServerResourcesReloadedEvent e){
        if(!(e.getCause().equals(ServerResourcesReloadedEvent.Cause.COMMAND) || e.getCause().equals(ServerResourcesReloadedEvent.Cause.PLUGIN))) return;
        Bukkit.broadcast(Component.text("GENESIS IS CONDUCTING A RESOURCE RELOAD, DO NOT REPORT BUGS OR CRASHES TO THE AUTHOR, THIS ACTION IS UNSUPPORTED").color(TextColor.color(230, 37, 23)));
        me.dueris.genesismc.OriginDataContainer.unloadAllData();
        onDisable();

        GenesisDataFiles.loadLangConfig();
        GenesisDataFiles.loadMainConfig();
        GenesisDataFiles.loadOrbConfig();
        CraftApoli.setupDynamicThreadCount();
        ThreadFactory threadFactory = new NamedThreadFactory("OriginParsingPool");
        loaderThreadPool = Executors.newFixedThreadPool(CraftApoli.getDynamicThreadCount(), threadFactory);
        debugOrigins = getOrDefault(GenesisDataFiles.getMainConfig().getBoolean("console-startup-debug") /* add arg compat in future version */, false);
        if (LangConfig.getLangFile() == null) {
            Bukkit.getLogger().severe("Unable to start GenesisMC due to lang not being loaded properly");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }

        me.dueris.genesismc.OriginDataContainer.loadData();
        // Pre-load condition types to prevent constant calling
        CraftCondition.bientity = new BiEntityCondition();
        CraftCondition.biome = new BiomeCondition();
        CraftCondition.blockCon = new BlockCondition();
        CraftCondition.damage = new DamageCondition();
        CraftCondition.entity = new EntityCondition();
        CraftCondition.fluidCon = new FluidCondition();
        CraftCondition.item = new ItemCondition();
        // Pre-load end
        conditionExecutor = new ConditionExecutor();
        try {
            CraftApoli.loadOrigins();
        } catch (InterruptedException | ExecutionException ee) {
            ee.printStackTrace();
        }

        // Register builtin powers
        Method registerMethod;
        try {
            registerMethod = CraftPower.class.getDeclaredMethod("registerBuiltinPowers");
            registerMethod.setAccessible(true);
            registerMethod.invoke(null);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException ee) {
            ee.printStackTrace();
        }

        if(Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer")){
            try {
                CraftPower.registerNewPower(ModelColor.ModelTransformer.class);
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }

        RecipePower.parseRecipes();

        OriginScheduler.OriginSchedulerTree scheduler = new OriginScheduler.OriginSchedulerTree();
        GenesisMC.scheduler = scheduler;
        scheduler.runTaskTimer(this, 0, 1);

        WaterProtItem.init();

        EntityGroupManager.INSTANCE.startTick();

        patchPowers();
        TagRegistry.runParse();
        try {
            FixerUpper.runFixerUpper();
        } catch (Exception ee) {
            ee.printStackTrace();
        }

        if (debugOrigins) {
            Bukkit.getServer().getConsoleSender().sendMessage("* (-debugOrigins={true}) || BEGINNING DEBUG {");
            Bukkit.getServer().getConsoleSender().sendMessage("  - Loaded @1 powers".replace("@1", String.valueOf(CraftPower.getRegistry().toArray().length)));
            Bukkit.getServer().getConsoleSender().sendMessage("  - Loaded @4 layers".replace("@4", String.valueOf(CraftApoli.getLayers().toArray().length)));
            Bukkit.getServer().getConsoleSender().sendMessage("  - Loaded @2 origins = [".replace("@2", String.valueOf(CraftApoli.getOrigins().toArray().length)));
            for (OriginContainer originContainer : CraftApoli.getOrigins()) {
                Bukkit.getServer().getConsoleSender().sendMessage("     () -> {@3}".replace("@3", originContainer.getTag()));
            }
            Bukkit.getServer().getConsoleSender().sendMessage("  ]");
            Bukkit.getServer().getConsoleSender().sendMessage("  - Power thread starting with {originScheduler}".replace("originScheduler", GenesisMC.scheduler.toString()));
            Bukkit.getServer().getConsoleSender().sendMessage("  - Lang testing = {true}");
            Bukkit.getServer().getConsoleSender().sendMessage("}");
        }

        loaderThreadPool.shutdown();
    }
}
