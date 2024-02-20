package me.dueris.genesismc;

import io.papermc.paper.event.player.PlayerFailMoveEvent;
import io.papermc.paper.event.server.ServerResourcesReloadedEvent;
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
import me.dueris.genesismc.factory.conditions.CraftCondition;
import me.dueris.genesismc.factory.conditions.BiEntityConditions;
import me.dueris.genesismc.factory.conditions.BiomeConditions;
import me.dueris.genesismc.factory.conditions.BlockConditions;
import me.dueris.genesismc.factory.conditions.DamageConditions;
import me.dueris.genesismc.factory.conditions.EntityConditions;
import me.dueris.genesismc.factory.conditions.FluidConditions;
import me.dueris.genesismc.factory.conditions.ItemConditions;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.*;
import me.dueris.genesismc.factory.powers.apoli.provider.origins.BounceSlimeBlock;
import me.dueris.genesismc.factory.powers.apoli.provider.origins.MimicWarden;
import me.dueris.genesismc.integration.PlaceHolderAPI;
import me.dueris.genesismc.registry.OriginContainer;
import me.dueris.genesismc.screen.GuiTicker;
import me.dueris.genesismc.screen.OriginChoosing;
import me.dueris.genesismc.screen.ScreenNavigator;
import me.dueris.genesismc.storage.GenesisConfigs;
import me.dueris.genesismc.storage.OriginDataContainer;
import me.dueris.genesismc.storage.nbt.NBTFixerUpper;
import me.dueris.genesismc.util.*;
import me.dueris.genesismc.util.entity.InventorySerializer;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.NamedThreadFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

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
    public static String pluginVersion = "v0.2.8";
    public static String world_container;
    public static ExecutorService loaderThreadPool;
    public static ArrayList<String> versions = new ArrayList<>();
    private static GenesisMC plugin;
    public static MinecraftServer server;

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
    public void onLoad(){
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

        if (!getTmpFolder().exists()) {
            getTmpFolder().mkdirs();
        }

        OriginDataContainer.loadData();
        // Pre-load condition types to prevent constant calling
        CraftCondition.bientity = new BiEntityConditions();
        CraftCondition.biome = new BiomeConditions();
        CraftCondition.blockCon = new BlockConditions();
        CraftCondition.damage = new DamageConditions();
        CraftCondition.entity = new EntityConditions();
        CraftCondition.fluidCon = new FluidConditions();
        CraftCondition.item = new ItemConditions();
        // Pre-load end
        conditionExecutor = new ConditionExecutor();
    }

    @Override
    public void onEnable() {
        try {
            CraftApoli.loadOrigins();
            if(Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer")){
                CraftPower.registerNewPower(ModelColor.ModelTransformer.class);
            }
        } catch (InterruptedException | ExecutionException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
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

        if(Bukkit.getCommandMap().getCommand("origin") == null){
            OriginCommand.register(((CraftServer)Bukkit.getServer()).getServer().vanillaCommandDispatcher.getDispatcher());
        }
        if(Bukkit.getCommandMap().getCommand("resource") == null){
            ResourceCommand.register(((CraftServer)Bukkit.getServer()).getServer().vanillaCommandDispatcher.getDispatcher());
        }
        if(Bukkit.getCommandMap().getCommand("power") == null){
            PowerCommand.register(((CraftServer)Bukkit.getServer()).getServer().vanillaCommandDispatcher.getDispatcher());
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
        OriginCommand.commandProvidedTaggedRecipies.addAll(RecipePower.taggedRegistry.keySet());
        OriginCommand.commandProvidedPowers.addAll(CraftApoli.getPowers());
        OriginCommand.commandProvidedOrigins.addAll(CraftApoli.getOrigins());
        OriginCommand.commandProvidedLayers.addAll(CraftApoli.getLayers());
        ResourceCommand.registeredBars.putAll(Resource.registeredBars);
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
        getServer().getPluginManager().registerEvents(new MimicWarden(), this);
        getServer().getPluginManager().registerEvents(new BounceSlimeBlock(), this);
        getServer().getPluginManager().registerEvents(new BiEntityConditions(), this);
        getServer().getPluginManager().registerEvents(new LogoutBugWorkaround(), this);
        getServer().getPluginManager().registerEvents(new VillagerTradeHook(), this);
        getServer().getPluginManager().registerEvents(new OriginScheduler.OriginSchedulerTree(), this);
        getServer().getPluginManager().registerEvents(new StructureGeneration(), this);
        getServer().getPluginManager().registerEvents(new KeybindingUtils(), this);

        BukkitRunnable[] independentTickers = {new GuiTicker(), new ContentTicker(), new OriginCommand()};
        WaterBreathe.start();
        for(BukkitRunnable runnable : independentTickers){
            runnable.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
        }

        EntityGroupManager.INSTANCE.startTick();
    }

    @Override
    public void onDisable() {
        OriginDataContainer.unloadAllData();
        CraftApoli.unloadData();
        OriginPlayerAccessor.playerPowerMapping.clear();
        OriginPlayerAccessor.powersAppliedList.clear();
        OriginCommand.commandProvidedLayers.clear();
        OriginCommand.commandProvidedOrigins.clear();
        OriginCommand.commandProvidedPowers.clear();
        OriginCommand.commandProvidedTaggedRecipies.clear();
        RecipePower.recipeMapping.clear();
        RecipePower.tags.clear();
        CraftPower.getRegistry().clear();
        CraftPower.getKeyedRegistry().clear();
        scheduler.cancel();
        EntityGroupManager.stop();

        for (int taskId : MimicWarden.getParticleTasks().values()) {
            getServer().getScheduler().cancelTask(taskId);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            Team team = p.getScoreboard().getTeam("origin-players");
            if (team != null) team.removeEntity(p);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "skin clear " + p.getName());
        }

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
    public void reload(ServerResourcesReloadedEvent e){
        if(!(e.getCause().equals(ServerResourcesReloadedEvent.Cause.COMMAND) || e.getCause().equals(ServerResourcesReloadedEvent.Cause.PLUGIN))) return;
        Bukkit.broadcast(Component.text("GENESIS IS CONDUCTING A RESOURCE RELOAD, DO NOT REPORT BUGS OR CRASHES TO THE AUTHOR, THIS ACTION IS UNSUPPORTED").color(TextColor.color(230, 37, 23)));

        onDisable();
        onLoad();
        onEnable();
    }
}
