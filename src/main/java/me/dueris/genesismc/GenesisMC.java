package me.dueris.genesismc;

import io.papermc.paper.event.player.PlayerFailMoveEvent;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import javassist.bytecode.Descriptor.Iterator;
import me.dueris.genesismc.choosing.ChoosingCORE;
import me.dueris.genesismc.choosing.ChoosingCUSTOM;
import me.dueris.genesismc.choosing.ChoosingGUI;
import me.dueris.genesismc.commands.OriginCommand;
import me.dueris.genesismc.commands.PlayerSelector;
import me.dueris.genesismc.commands.ResourceCommand;
import me.dueris.genesismc.commands.TabAutoComplete;
import me.dueris.genesismc.commands.subcommands.origin.Info.InInfoCheck;
import me.dueris.genesismc.commands.subcommands.origin.Info.Info;
import me.dueris.genesismc.commands.subcommands.origin.Recipe;
import me.dueris.genesismc.enchantments.Anvil;
import me.dueris.genesismc.enchantments.EnchantTable;
import me.dueris.genesismc.enchantments.WaterProtection;
import me.dueris.genesismc.enchantments.WaterProtectionNMSImpl;
import me.dueris.genesismc.entity.InventorySerializer;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.events.EventListeners;
import me.dueris.genesismc.events.RegisterPowersEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.TagRegistry;
import me.dueris.genesismc.factory.conditions.Condition;
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
import me.dueris.genesismc.factory.powers.block.WaterBreathe;
import me.dueris.genesismc.factory.powers.player.PlayerRender;
import me.dueris.genesismc.factory.powers.simple.BounceSlimeBlock;
import me.dueris.genesismc.factory.powers.simple.MimicWarden;
import me.dueris.genesismc.factory.powers.world.EntityGroupManager;
import me.dueris.genesismc.files.GenesisDataFiles;
import me.dueris.genesismc.files.TempStorageContainer;
import me.dueris.genesismc.generation.WaterProtBookGen;
import me.dueris.genesismc.hooks.papi.PlaceholderApiExtension;
import me.dueris.genesismc.items.GenesisItems;
import me.dueris.genesismc.items.InfinPearl;
import me.dueris.genesismc.items.OrbOfOrigins;
import me.dueris.genesismc.items.WaterProtItem;
import me.dueris.genesismc.utils.*;
import me.dueris.genesismc.utils.translation.LangConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.NamedThreadFactory;
import net.minecraft.world.damagesource.DamageType;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.v1_20_R3.CraftRegistry;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.WatchdogThread;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static me.dueris.genesismc.PlayerHandler.ReapplyEntityReachPowers;
import static me.dueris.genesismc.factory.powers.simple.BounceSlimeBlock.bouncePlayers;
import static me.dueris.genesismc.factory.powers.simple.MimicWarden.getParticleTasks;
import static me.dueris.genesismc.factory.powers.simple.MimicWarden.mimicWardenPlayers;
import static me.dueris.genesismc.factory.powers.simple.PiglinNoAttack.piglinPlayers;
import static me.dueris.genesismc.factory.powers.simple.ScareCreepers.scaryPlayers;
import static me.dueris.genesismc.utils.BukkitColour.*;

public final class GenesisMC extends JavaPlugin implements Listener {
    public static EnumSet<Material> tool;
    public static Metrics metrics;
    public static boolean disableRender = true;
    public static ArrayList<Enchantment> custom_enchants = new ArrayList<>();
    @Deprecated(forRemoval = true)
    public static WaterProtection waterProtectionEnchant;
    private static GenesisMC plugin;
    public static ConditionExecutor conditionExecutor;
    public static String apoliVersion = "1.11.3";
    public static boolean placeholderapi = false;

    static {
        tool = EnumSet.of(Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_SWORD, Material.GOLDEN_AXE, Material.GOLDEN_HOE, Material.GOLDEN_PICKAXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_SWORD, Material.NETHERITE_AXE, Material.NETHERITE_HOE, Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_SWORD, Material.IRON_AXE, Material.IRON_HOE, Material.IRON_PICKAXE, Material.IRON_SHOVEL, Material.IRON_SWORD, Material.WOODEN_AXE, Material.WOODEN_HOE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_SWORD, Material.SHEARS);
    }

    public static boolean forceMixinOrigins = classExists("org.spongepowered.asm.launch.MixinBootstrap");
    public static boolean debugOrigins = false;
    public static boolean forceUseCurrentVersion = false;
    public static boolean forceWatchdogStop = true;

    public static FoliaOriginScheduler.OriginSchedulerTree getScheduler(){
        return scheduler;
    }

    public static FoliaOriginScheduler.OriginSchedulerTree scheduler = null;
    public static String version = Bukkit.getVersion().split("\\(MC: ")[1].replace(")", "");
    public static final boolean isFolia = classExists("io.papermc.paper.threadedregions.RegionizedServer");
    public static final boolean isExpandedScheduler = classExists("io.papermc.paper.threadedregions.scheduler.ScheduledTask");
    public static boolean isCompatible = false;
    public static String pluginVersion = "v0.2.5";
    public static String world_container = MinecraftServer.getServer().options.asMap().toString().split(", \\[W, universe, world-container, world-dir]=\\[")[1].split("], ")[0];
    public static ExecutorService loaderThreadPool;

    public static ArrayList<String> versions = new ArrayList<>();
    static {
        versions.add("1.20.4");
        versions.add("1.20.3");
    }

    /**
     * For some reason, this works for fixing the bug where you cant interact or hit entities?
     * @param e
     */
    @EventHandler
    public void test(PlayerInteractAtEntityEvent e){
//        e.getPlayer().sendMessage(String.valueOf(e.isCancelled()));
    }

    public static ConditionExecutor getConditionExecutor(){
        return conditionExecutor;
    }

    // This is just a test for the ScareCreepers ai goal
    // public void testAiGoal(Creeper cr){
    //     CraftCreeper craftCreep = (CraftCreeper) cr;
    //     Predicate pred = new Predicate<LivingEntity>() {
    //         @Override
    //         public boolean test(LivingEntity t) {
    //             return true;
    //         }
    //     };
    //     craftCreep.getHandle().goalSelector.addGoal(3, new AvoidEntityGoal(craftCreep.getHandle(), net.minecraft.world.entity.player.Player.class, pred, 6.0f, 1.0d, 1.2d, pred));
    // }

    @Override
    public void onEnable(){
        plugin = this;
        metrics = new Metrics(this, 18536);
        GenesisDataFiles.loadLangConfig();
        GenesisDataFiles.loadMainConfig();
        GenesisDataFiles.loadOrbConfig();
        forceWatchdogStop = GenesisDataFiles.getMainConfig().getBoolean("disable-watchdog");
        isCompatible = (!isFolia && (isExpandedScheduler));
        if(!isCompatible){
            if(forceUseCurrentVersion) return;
            Bukkit.getLogger().severe("Unable to start GenesisMC due to it not being compatible with this server type");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
        boolean isCorrectVersion = false;
        for(String vers : versions){
            if(isCorrectVersion) break;
            if (vers.equalsIgnoreCase(String.valueOf(version))) {
                isCorrectVersion = true;
                break;
            }
        }
        
        if(!isCorrectVersion){
            if(forceUseCurrentVersion) return;
            Bukkit.getLogger().severe("Unable to start GenesisMC due to it not being compatible with this server version");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
        try{
            if(forceMixinOrigins) {
                Bukkit.getLogger().info("Loading Mixin Environment...");
            }
        } catch (Exception e){}
        if(forceWatchdogStop){
            WatchdogThread.doStop();
        }
        CraftApoli.setupDynamicThreadCount();
        ThreadFactory threadFactory = new NamedThreadFactory("OriginParsingPool");
        loaderThreadPool = Executors.newFixedThreadPool(CraftApoli.getDynamicThreadCount(), threadFactory);
        debugOrigins = getOrDefault(GenesisDataFiles.getMainConfig().getBoolean("console-startup-debug") /* add arg compat in future version */, false);
        if(LangConfig.getLangFile() == null){
            Bukkit.getLogger().severe("Unable to start GenesisMC due to lang not being loaded properly");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
        placeholderapi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        if(placeholderapi){
            new PlaceholderApiExtension(this).register();
        }

        // load tempStorageOptimizations - start
        TempStorageContainer.BiomeStorage biomeStorage = new TempStorageContainer.BiomeStorage();
        biomeStorage.addValues();
        TempStorageContainer.StructureStorage structureStorage = new TempStorageContainer.StructureStorage();
        structureStorage.addValues();
        // load tempStorageOptimizations - end
        GenesisMC.disableRender = GenesisDataFiles.getMainConfig().getBoolean("disable-render-power");

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
        CraftApoli.loadOrigins();
        try {
            for (Class<? extends CraftPower> c : CraftPower.findCraftPowerClasses()) {
                if (CraftPower.class.isAssignableFrom(c)) {
                    CraftPower instance = c.newInstance();
                    CraftPower.getRegistered().add(instance.getClass());
                    if (instance instanceof Listener || Listener.class.isAssignableFrom(instance.getClass())) {
                        Bukkit.getServer().getPluginManager().registerEvents((Listener) instance, GenesisMC.getPlugin());
                    }
                }
            }
            RegisterPowersEvent registerPowersEvent = new RegisterPowersEvent(CraftPower.getRegistered());
            Bukkit.getServer().getPluginManager().callEvent(registerPowersEvent);
            for (OriginContainer origin : CraftApoli.getOrigins()) {
                for (PowerContainer powerContainer : origin.getPowerContainers()) {
                    CraftApoli.getPowers().add(powerContainer);
                }
            }
        } catch (IOException | ReflectiveOperationException e) {
            e.printStackTrace();
        }
        FoliaOriginScheduler.OriginSchedulerTree scheduler = new FoliaOriginScheduler.OriginSchedulerTree();
        GenesisMC.scheduler = scheduler;
        scheduler.runTaskTimer(this, 0, 1);
        // waterProtectionEnchant = new WaterProtection();
        // custom_enchants.add(waterProtectionEnchant);
    
        OrbOfOrigins.init();
        InfinPearl.init();
        WaterProtItem.init();
        start();
        patchPowers();
        TagRegistry.runParse();
        Bukkit.getCommandMap().register("origin", new OriginCommand());
        Bukkit.getCommandMap().register("resource", new ResourceCommand());
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
        Bukkit.getServer().getConsoleSender().sendMessage("");
        if(debugOrigins){
            Debug.executeGenesisDebug();
        }
        Bukkit.getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        // Shutdown executor, we dont need it anymore
        loaderThreadPool.shutdown();
    }

    protected static void patchPowers(){
        for (Player p : Bukkit.getOnlinePlayers()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    ReapplyEntityReachPowers(p);
                }
            }.runTaskLater(GenesisMC.getPlugin(), 5L);
            PlayerHandler.originValidCheck(p);
            OriginPlayerUtils.assignPowers(p);
            if (p.isOp())
                p.sendMessage(Component.text(LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "reloadMessage")).color(TextColor.fromHexString(AQUA)));
            boolean hasMimicWardenPower = false;

            for (OriginContainer origin : OriginPlayerUtils.getOrigin(p).values()) {
                for (PowerContainer power : origin.getPowerContainers()) {
                    if (power == null) continue;
                    if (power.getTag().equals("origins:mimic_warden")) {
                        hasMimicWardenPower = true;
                        break;
                    }
                }
            }
            if (hasMimicWardenPower && !mimicWardenPlayers.contains(p)) {
                mimicWardenPlayers.add(p);
            } else if (!hasMimicWardenPower) {
                mimicWardenPlayers.remove(p);
            }

            boolean hasPower = false;

            for (OriginContainer origin : OriginPlayerUtils.getOrigin(p).values()) {
                for (String power : origin.getPowers()) {
                    if (power.equals("origins:slime_block_bounce")) {
                        hasPower = true;
                        break;
                    }
                }
            }

            if (hasPower && !bouncePlayers.contains(p)) {
                bouncePlayers.add(p);
            } else if (!hasPower) {
                bouncePlayers.remove(p);
            }

            boolean hasPiglinPower = false;

            for (OriginContainer origin : OriginPlayerUtils.getOrigin(p).values()) {
                for (PowerContainer power : origin.getPowerContainers()) {
                    if (power.getTag().equals("origins:piglin_brothers")) {
                        hasPiglinPower = true;
                        break;
                    }
                }
            }
            if (hasPiglinPower && !piglinPlayers.contains(p)) {
                piglinPlayers.add(p);
            } else if (!hasPiglinPower) {
                piglinPlayers.remove(p);
            }

            boolean hasScaryPower = false;

            for (OriginContainer origin : OriginPlayerUtils.getOrigin(p).values()) {
                for (PowerContainer power : origin.getPowerContainers()) {
                    if (power.getTag().equals("origins:scare_creepers")) {
                        hasPiglinPower = true;
                        break;
                    }
                }
            }
            if (hasScaryPower && !scaryPlayers.contains(p)) {
                scaryPlayers.add(p);
            } else if (!hasScaryPower) {
                scaryPlayers.remove(p);
            }
        }
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

    private static boolean getOrDefault(boolean arg1, boolean arg2){
        boolean finaL = arg2;
        if(arg1){
            finaL = arg1;
        }
        return finaL;
    }

    public static void sendDebug(Object string){
        if(debugOrigins){
            System.out.println(string);
        }
    }

    private void start(){
        getServer().getPluginManager().registerEvents(new InventorySerializer(), this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new CooldownManager(), this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new PlayerHandler(), this);
        getServer().getPluginManager().registerEvents(new EnchantTable(), this);
        getServer().getPluginManager().registerEvents(new Anvil(), this);
        getServer().getPluginManager().registerEvents(new WaterProtBookGen(), this);
        getServer().getPluginManager().registerEvents(new KeybindUtils(), this);
        getServer().getPluginManager().registerEvents(new ChoosingCORE(), this);
        getServer().getPluginManager().registerEvents(new ChoosingCUSTOM(), this);
        getServer().getPluginManager().registerEvents(new Recipe(), this);
        getServer().getPluginManager().registerEvents(new Info(), this);
        getServer().getPluginManager().registerEvents(new EventListeners(), this);
        getServer().getPluginManager().registerEvents(new InventorySerializer(), this);
        getServer().getPluginManager().registerEvents(new GenesisItems(), this);
        getServer().getPluginManager().registerEvents(new MimicWarden(), this);
        getServer().getPluginManager().registerEvents(new BounceSlimeBlock(), this);
        getServer().getPluginManager().registerEvents(new FoliaOriginScheduler.OriginSchedulerTree(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new KeybindUtils(), GenesisMC.getPlugin());
        if (getServer().getPluginManager().isPluginEnabled("SkinsRestorer")) {
            try {
               getServer().getPluginManager().registerEvents(new PlayerRender.ModelColor(), GenesisMC.getPlugin());
               getServer().getConsoleSender().sendMessage(Component.text(LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.skinRestorer.present")).color(TextColor.fromHexString(AQUA)));
            } catch (Exception ignored){
                // ignored
            }
        } else {
            getServer().getConsoleSender().sendMessage(Component.text(LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.skinRestorer.absent")).color(TextColor.fromHexString(AQUA)));
        }
        ChoosingGUI forced = new ChoosingGUI();
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

//        if (Bukkit.getServer().getPluginManager().isPluginEnabled("SkinsRestorer")) {
//            GlobalRegionScheduler globalRegionScheduler = Bukkit.getGlobalRegionScheduler();
//            try {
//                globalRegionScheduler.execute(GenesisMC.getPlugin(), PlayerRender.ModelColor.class.newInstance());
//            } catch (InstantiationException e) {
//                throw new RuntimeException(e);
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//        }
        // Why does that even exist here?
        EntityGroupManager.INSTANCE.startTick();
    }

    @EventHandler
    public void invulnerableBugPatch(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!p.isInvulnerable() && p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR)
            return;
        p.setInvulnerable(false);
    }

    @EventHandler
    public void lagBackPatch(PlayerFailMoveEvent e) {
        e.setAllowed(true);
        e.setLogWarning(false);
    }

    @Override
    public void onDisable() {
        me.dueris.genesismc.OriginDataContainer.unloadAllData();
        CraftApoli.unloadData();

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
        // Disable enchantments
        try {
            Field keyField = Enchantment.class.getDeclaredField("byKey");

            keyField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<NamespacedKey, Enchantment> byKey = (HashMap<NamespacedKey, Enchantment>) keyField.get(null);

            for (Enchantment enchantment : custom_enchants) {
                byKey.remove(enchantment.getKey());
            }

            Field nameField = Enchantment.class.getDeclaredField("byName");

            nameField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) nameField.get(null);

            for (Enchantment enchantment : custom_enchants) {
                byName.remove(enchantment.getName());
            }
        } catch (Exception ignored) {

        }

        getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] " + LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "disable")).color(TextColor.fromHexString(RED)));
    }
}
