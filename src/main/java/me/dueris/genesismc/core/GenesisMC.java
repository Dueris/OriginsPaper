package me.dueris.genesismc.core;

import me.dueris.genesismc.core.bukkitrunnables.*;
import me.dueris.genesismc.core.choosing.*;
import me.dueris.genesismc.core.commands.BetaCommands;
import me.dueris.genesismc.core.commands.GenesisCommandManager;
import me.dueris.genesismc.core.commands.TabAutoComplete;
import me.dueris.genesismc.core.commands.ToggleCommand;
import me.dueris.genesismc.core.commands.subcommands.origin.Info;
import me.dueris.genesismc.core.commands.subcommands.origin.Purge;
import me.dueris.genesismc.core.enchantments.WaterProtAnvil;
import me.dueris.genesismc.core.generation.WaterProtBookGen;
import me.dueris.genesismc.core.items.Items;
import me.dueris.genesismc.core.origins.avian.AvianMain;
import me.dueris.genesismc.core.origins.creep.CreepExplode;
import me.dueris.genesismc.core.origins.creep.CreepMain;
import me.dueris.genesismc.core.origins.enderian.*;
import me.dueris.genesismc.core.origins.phantom.PhantomForm;
import me.dueris.genesismc.core.origins.phantom.PhantomFormRunnable;
import me.dueris.genesismc.core.origins.phantom.PhantomMain;
import me.dueris.genesismc.core.origins.piglin.PiglinMain;
import me.dueris.genesismc.core.origins.rabbit.RabbitLeap;
import me.dueris.genesismc.core.origins.rabbit.RabbitMain;
import me.dueris.genesismc.core.utils.ParticleHandler;
import me.dueris.genesismc.custom_origins.CustomOrigins;
import me.dueris.genesismc.custom_origins.CustomOriginsMethods;
import me.dueris.genesismc.custom_origins.handlers.CustomMenuHandler;
import me.dueris.genesismc.core.enchantments.EnchantProtEvent;
import me.dueris.genesismc.core.enchantments.WaterProtection;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import me.dueris.genesismc.core.items.InfinPearl;
import me.dueris.genesismc.core.items.OrbOfOrigins;
import me.dueris.genesismc.core.items.WaterProtItem;
import me.dueris.genesismc.core.origins.arachnid.ArachnidClimb;
import me.dueris.genesismc.core.origins.arachnid.ArachnidMain;
import me.dueris.genesismc.core.origins.human.HumanMain;
import me.dueris.genesismc.core.origins.shulk.ShulkInv;
import me.dueris.genesismc.core.origins.shulk.ShulkMain;
import me.dueris.genesismc.custom_origins.powers.WorldSpawnHandler;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

public final class GenesisMC extends JavaPlugin implements Listener {
    private static GenesisMC plugin;
    public static EnumSet<Material> tool;

    public GenesisMC() {
    }

    public static ArrayList<Enchantment> custom_enchants = new ArrayList<>();
    public static WaterProtection waterProtectionEnchant;

    @Override
    public void onEnable() {
        // Plugin startup logic


        plugin = this;

        getServer().getPluginManager().registerEvents(new DataContainer(), this);

        //configs + folders

        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        GenesisDataFiles.setup();
        GenesisDataFiles.getPlugCon().options().copyDefaults(true);
        GenesisDataFiles.getOrbCon().options().copyDefaults(true);
        GenesisDataFiles.setDefaults();
        GenesisDataFiles.save();

        //start

        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC]    ____                               _         __  __    ____ ");
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[GenesisMC]   / ___|   ___   _ __     ___   ___  (_)  ___  |  \\/  |  / ___|");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[GenesisMC]  | |  _   / _ \\ | '_ \\   / _ \\ / __| | | / __| | |\\/| | | |    ");
        getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[GenesisMC]  | |_| | |  __/ | | | | |  __/ \\__ \\ | | \\__ \\ | |  | | | |___ ");
        getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "[GenesisMC]   \\____|  \\___| |_| |_|  \\___| |___/ |_| |___/ |_|  |_|  \\____|");
        getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[GenesisMC]  GenesisMC -- Created by Dueris");
        getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        //custom origins loader
        CustomOrigins.onEnableCustomOrigins();
       try {
                Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
           getServer().getConsoleSender().sendMessage(ChatColor.RED + "WARNING: FOLIA IS NOT SUPPORTED ON THIS VERSION TYPE. PLEASE USE THE FOLIA BUILD OF THIS VERSION.");
       } catch (ClassNotFoundException e) {
           //not folia
       }
       if(GenesisDataFiles.getPlugCon().getString("use-builtin-api").equalsIgnoreCase("false")){
           getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] OriginsAPI disabled!! This will cause errors if you do not use the OriginAPI that is built in, or external.");

       }
       if(Bukkit.getServer().getPluginManager().isPluginEnabled("Origins-Bukkit")){
           getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] Unable to start plugin due to Origins Bukkit being present. Using both will cause errors.");
           getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
           Bukkit.getServer().getPluginManager().disablePlugin(this);
       }

        if (GenesisDataFiles.getPlugCon().getString("console-dump-onstartup").equalsIgnoreCase("true")) {
            getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[GenesisMC] Loading API version 0.1.1");
            getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[GenesisMC] Loading Subcommands");
            getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[GenesisMC] Loading OriginsChoosingCommands");
            //method
            dumpCon();
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[GenesisMC] Successfully loaded version 0.1.6-ALPHA_SNAPSHOT (1.19.4)");
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[GenesisMC] Successfully loaded API version 0.1.1-BETA (1.19.4)");
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[GenesisMC] Successfully loaded CONFIG version 1016788 (1.19.4)");
        } else {
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[GenesisMC] Successfully loaded version 0.1.6-ALPHA_SNAPSHOT (1.19.4)");
        }
        if (getServer().getPluginManager().getPlugins().toString().contains("PurpleWolfAPI") || getServer().getPluginManager().isPluginEnabled("OriginsAPI") || getServer().getPluginManager().getPlugin("PurpleWolfAPI") != null) {
            getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[GenesisMC - OriginsAPI] Successfully injected OriginsAPI into plugin");
        } else {
            //PurpleWolfAPI not avalible, inject built-in
            getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[GenesisMC] OriginsAPI not detected. Injecting built-in API");
        }

        //Custom origins loading
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[GenesisMC] Loading custom origins");
        //CustomOriginsMethods.removeUnzippedOriginDatapacks();
        CustomOriginsMethods.unzipCustomOriginDatapacks();
        CustomOriginsMethods.loadCustomOriginDatapacks();
        for (String originTag : CustomOriginsMethods.getCustomOriginTags()) {
            getServer().getConsoleSender().sendMessage("[GenesisMC] Loaded \""+CustomOriginsMethods.getCustomOriginName(originTag)+"\"");
        }

        getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        getServer().getPluginManager().registerEvents(this, this);
//Commands
        getCommand("origin").setExecutor(new GenesisCommandManager());
        getCommand("origin").setTabCompleter(new TabAutoComplete());
        getCommand("shulker").setTabCompleter(new TabAutoComplete());
        getCommand("beta").setTabCompleter(new TabAutoComplete());
        getCommand("shulker").setExecutor(new ShulkInv());
        getCommand("toggle").setExecutor(new ToggleCommand());
//Event Handler Register
        getServer().getPluginManager().registerEvents(new EnderSilkTouch(), this);
        getServer().getPluginManager().registerEvents(new EnderTeleport(), this);
        getServer().getPluginManager().registerEvents(new HumanMain(), this);
        getServer().getPluginManager().registerEvents(new EnderMain(), this);
        getServer().getPluginManager().registerEvents(new Purge(), this);
        getServer().getPluginManager().registerEvents(new ShulkMain(), this);
        getServer().getPluginManager().registerEvents(new JoiningHandler(), this);
        getServer().getPluginManager().registerEvents(new EnchantProtEvent(), this);
        getServer().getPluginManager().registerEvents(new BrethrenOfEnd(), this);
        getServer().getPluginManager().registerEvents(new ArachnidMain(), this);
        getServer().getPluginManager().registerEvents(new CustomMenuHandler(), this);
        getServer().getPluginManager().registerEvents(new WaterProtAnvil(), this);
        getServer().getPluginManager().registerEvents(new CreepMain(), this);
        getServer().getPluginManager().registerEvents(new CreepExplode(), this);
        getServer().getPluginManager().registerEvents(new PlayerAddScoreboard(), this);
        getServer().getPluginManager().registerEvents(new PhantomForm(), this);
        getServer().getPluginManager().registerEvents(new PhantomMain(), this);
        getServer().getPluginManager().registerEvents(new RabbitMain(), this);
        getServer().getPluginManager().registerEvents(new RabbitLeap(), this);
        getServer().getPluginManager().registerEvents(new AvianMain(), this);
        getServer().getPluginManager().registerEvents(new PiglinMain(), this);
        getServer().getPluginManager().registerEvents(new WaterProtBookGen(), this);
        getServer().getPluginManager().registerEvents(new KeybindHandler(), this);
        getServer().getPluginManager().registerEvents(new Info(), this);
        getServer().getPluginManager().registerEvents(new ChoosingCORE(), this);
        getServer().getPluginManager().registerEvents(new ChoosingCUSTOM(), this);
        getServer().getPluginManager().registerEvents(new ChoosingEXP(), this);
        plugin = this;
        getServer().getPluginManager().registerEvents(new DataContainer(), this);
        if (GenesisDataFiles.getPlugCon().getString("beta-enabled").equalsIgnoreCase("true")) {
            getCommand("beta").setExecutor(new BetaCommands());
        }
        OrbOfOrigins.init();
        InfinPearl.init();
        WaterProtItem.init();
        //runnables main
        ChoosingForced forced = new ChoosingForced();
        forced.runTaskTimer(this, 0, 5);
        Items items = new Items();
        items.runTaskTimer(this, 0, 5);
//particle handler
        ParticleHandler handler = new ParticleHandler();
        handler.runTaskTimer(this, 0, 5);
//scoreboard
        ScoreboardRunnable scorebo = new ScoreboardRunnable();
        scorebo.runTaskTimer(this, 0, 5);
//enderian
        EnderianRunnable enderrun = new EnderianRunnable();
        enderrun.runTaskTimer(this, 0, 5);
        EnderianDamageRunnable enderdamagerun = new EnderianDamageRunnable();
        enderdamagerun.runTaskTimer(this, 0, 20);
//shulk
        ShulkRunnable shulkrun = new ShulkRunnable();
        shulkrun.runTaskTimer(this, 0, 5);
//arachnid
        ArachnidRunnable arachnidrun = new ArachnidRunnable();
        arachnidrun.runTaskTimer(this, 0, 15);
        ArachnidClimb arachnidrunclimb = new ArachnidClimb();
        arachnidrunclimb.runTaskTimer(this, 0, 5);
//creep
        CreepRunnable creeprun = new CreepRunnable();
        creeprun.runTaskTimer(this, 0, 15);
//phantom
        PhantomRunnable phantomrun = new PhantomRunnable();
        phantomrun.runTaskTimer(this, 0, 5);
        PhantomFormRunnable phantomformrun = new PhantomFormRunnable();
        phantomformrun.runTaskTimer(this, 0, 2);
        PhantomMain phantommainrun = new PhantomMain();
        phantommainrun.runTaskTimer(this, 0, 20);
//slimeling
        SlimelingRunnable slimelingrun = new SlimelingRunnable();
        slimelingrun.runTaskTimer(this, 0, 5);
//vexian
        VexianRunnable vexianrun = new VexianRunnable();
        vexianrun.runTaskTimer(this, 0, 5);
//blazeborn
        BlazebornRunnable blazebornrun = new BlazebornRunnable();
        blazebornrun.runTaskTimer(this, 0, 5);
//starborn
        StarborneRunnable starbornerun = new StarborneRunnable();
        starbornerun.runTaskTimer(this, 0, 5);
//merling
        MerlingRunnable merlingrun = new MerlingRunnable();
        merlingrun.runTaskTimer(this, 0, 5);
//allay
        AllayRunnable allayrun = new AllayRunnable();
        allayrun.runTaskTimer(this, 0, 5);
//bumblebee
        BumblebeeRunnable bumblebeerun = new BumblebeeRunnable();
        bumblebeerun.runTaskTimer(this, 0, 5);
//rabbit
        RabbitRunnable rabbitrun = new RabbitRunnable();
        rabbitrun.runTaskTimer(this, 0, 5);
//elytrian
        ElytrainRunnable elytrainrun = new ElytrainRunnable();
        elytrainrun.runTaskTimer(this, 0, 5);
//avian
        AvianRunnable avianrun = new AvianRunnable();
        avianrun.runTaskTimer(this, 0, 5);
//piglin
        PiglinRunnable piglinrun = new PiglinRunnable();
        piglinrun.runTaskTimer(this, 0, 5);
//sculk
        SculkRunnable sculkrun = new SculkRunnable();
        sculkrun.runTaskTimer(this, 0, 5);

//enchantments
        waterProtectionEnchant = new WaterProtection("waterprot");
        custom_enchants.add(waterProtectionEnchant);
        registerEnchantment(waterProtectionEnchant);

    }

    static {
        tool = EnumSet.of(Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_SWORD, Material.GOLDEN_AXE, Material.GOLDEN_HOE, Material.GOLDEN_PICKAXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_SWORD, Material.NETHERITE_AXE, Material.NETHERITE_HOE, Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_SWORD, Material.IRON_AXE, Material.IRON_HOE, Material.IRON_PICKAXE, Material.IRON_SHOVEL, Material.IRON_SWORD, Material.WOODEN_AXE, Material.WOODEN_HOE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_SWORD, Material.SHEARS);
    }

    @EventHandler
    public void NetherOriginRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-piglin") || origintag.equalsIgnoreCase("genesis:origin-blazeborn")) {
            if (!(e.isBedSpawn() || e.isAnchorSpawn())) {
                Location location = WorldSpawnHandler.NetherSpawn();
                if (location == null) return;
                e.setRespawnLocation(location);
            }

        }
    }

    @EventHandler
    public void StartupCause(ServerLoadEvent e){

        if(e.getType().equals(ServerLoadEvent.LoadType.RELOAD)){
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.isOp()){
                    p.sendMessage(ChatColor.BLUE + "Origins Reloaded");
                }
            }
        }

    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] Disabling GenesisMC Origins.");
        // Disable enchantments
        try {
            Field keyField = Enchantment.class.getDeclaredField("byKey");

            keyField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<NamespacedKey, Enchantment> byKey = (HashMap<NamespacedKey, Enchantment>) keyField.get(null);

            for (Enchantment enchantment : custom_enchants){
                if(byKey.containsKey(enchantment.getKey())) {
                    byKey.remove(enchantment.getKey());
                }
            }

            Field nameField = Enchantment.class.getDeclaredField("byName");

            nameField.setAccessible(true);
            @SuppressWarnings("unchecked")
            HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) nameField.get(null);

            for (Enchantment enchantment : custom_enchants){
                if(byName.containsKey(enchantment.getName())) {
                    byName.remove(enchantment.getName());
                }
            }
        } catch (Exception ignored) { }

        //deletes origin files unzipped by Genesis
        //CustomOriginsMethods.removeUnzippedOriginDatapacks();
    }

    //Load custom enchantments
    public static void registerEnchantment(Enchantment enchantment) {
        boolean registered = true;
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(enchantment);
        } catch (Exception e) {
            registered = false;
            e.printStackTrace();
        }
        if(registered){
            // It's been registered!
        }
    }


    public static GenesisMC getPlugin() {
        return plugin;
    }

    public static void dumpCon(){
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[GenesisMC] DUMPING PLUGIN-API FILES:");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "Loading config file:" +
                GenesisDataFiles.getOrbCon().getValues(Boolean.parseBoolean("all")) +
                ChatColor.GRAY +
                GenesisDataFiles.getPlugCon().getValues(Boolean.parseBoolean("all"))

        );
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[GenesisMC] Loading API");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[GenesisMC] DUMPING SERVER FILES:" +
                Bukkit.getServer().getVersion() +
                Bukkit.getServer().getAllowEnd() +
                Bukkit.getServer().getAllowNether() +
                Bukkit.getServer().getPluginManager() +
                Bukkit.getServer().getMaxPlayers() +
                Bukkit.getServer().getConnectionThrottle() +
                Bukkit.getServer().getLogger() +
                Bukkit.getServer().getName() +
                Bukkit.getServer().getBukkitVersion() +
                Bukkit.getServer().getDefaultGameMode() +
                Bukkit.getServer().getWorldType() +
                Bukkit.getServer().getResourcePack() +
                Bukkit.getServer().getHelpMap() +
                Bukkit.getServer().getPluginManager().getPlugins() +
                Bukkit.getServer().getBukkitVersion() +
                Bukkit.getServer().getCommandMap()

        );

    }
}
