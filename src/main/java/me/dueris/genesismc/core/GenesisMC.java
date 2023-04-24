package me.dueris.genesismc.core;

import me.dueris.genesismc.core.bukkitrunnables.*;
import me.dueris.genesismc.core.commands.BetaCommands;
import me.dueris.genesismc.core.commands.GenesisCommandManager;
import me.dueris.genesismc.core.commands.TabAutoComplete;
import me.dueris.genesismc.core.commands.subcommands.origin.Info;
import me.dueris.genesismc.core.commands.subcommands.origin.Purge;
import me.dueris.genesismc.core.enchantments.WaterProtAnvil;
import me.dueris.genesismc.core.generation.WaterProtBookGen;
import me.dueris.genesismc.core.origins.avian.AvianMain;
import me.dueris.genesismc.core.origins.creep.CreepExplode;
import me.dueris.genesismc.core.origins.creep.CreepMain;
import me.dueris.genesismc.core.origins.enderian.*;
import me.dueris.genesismc.core.origins.phantom.PhantomForm;
import me.dueris.genesismc.core.origins.phantom.PhantomFormRunnable;
import me.dueris.genesismc.core.origins.phantom.PhantomMain;
import me.dueris.genesismc.core.origins.rabbit.RabbitLeap;
import me.dueris.genesismc.core.origins.rabbit.RabbitMain;
import me.dueris.genesismc.core.utils.ParticleHandler;
import me.dueris.genesismc.custom_origins.CustomOrigins;
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
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

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
        CustomOrigins.onEnableCusotmOrigins();
       try {
                Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
           getServer().getConsoleSender().sendMessage(ChatColor.RED + "WARNING: FOLIA IS NOT SUPPORTED ON THIS VERSION TYPE. PLEASE USE THE FOLIA BUILD OF THIS VERSION.");
       } catch (ClassNotFoundException e) {
           //not folia
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
        if (getServer().getPluginManager().getPlugins().toString().contains("PurpleWolfAPI") || getServer().getPluginManager().isPluginEnabled("PurpleWolfAPI") || getServer().getPluginManager().getPlugin("PurpleWolfAPI") != null) {
            getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[GenesisMC - PurpleWolfAPI] Successfully injected PurpleWolfAPI into plugin");
        } else {
            //PurpleWolfAPI not avalible, inject built-in
            getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[GenesisMC] PurpleWolfAPI not detected. Injecting built-in API");
        }

        getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        getServer().getPluginManager().registerEvents(this, this);
//Commands
        getCommand("origin").setExecutor(new GenesisCommandManager());
        getCommand("origin").setTabCompleter(new TabAutoComplete());
        getCommand("shulker").setTabCompleter(new TabAutoComplete());
        getCommand("beta").setTabCompleter(new TabAutoComplete());
        getCommand("shulker").setExecutor(new ShulkInv());
//Event Handler Register
        getServer().getPluginManager().registerEvents(new GenesisChooseListener(), this);
        getServer().getPluginManager().registerEvents(new EnderSilkTouch(), this);
        getServer().getPluginManager().registerEvents(new EnderTeleport(), this);
        getServer().getPluginManager().registerEvents(new EnderWater(), this);
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
        getServer().getPluginManager().registerEvents(new WaterProtBookGen(), this);
        getServer().getPluginManager().registerEvents(new KeybindHandler(), this);
        getServer().getPluginManager().registerEvents(new Info(), this);
        plugin = this;
        getServer().getPluginManager().registerEvents(new DataContainer(), this);
        if (GenesisDataFiles.getPlugCon().getString("beta-enabled").equalsIgnoreCase("true")) {
            getCommand("beta").setExecutor(new BetaCommands());
        }
        OrbOfOrigins.init();
        InfinPearl.init();
        WaterProtItem.init();
        //runnables main
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
        enderdamagerun.runTaskTimer(this, 0, 10);
//shulk
        ShulkRunnable shulkrun = new ShulkRunnable();
        shulkrun.runTaskTimer(this, 0, 5);
//arachnid
        ArachnidRunnable arachnidrun = new ArachnidRunnable();
        arachnidrun.runTaskTimer(this, 0, 15);
        ArachnidClimb arachnidrunclimb = new ArachnidClimb();
        arachnidrunclimb.runTaskTimer(this, 0, 5);
//forcechoose
        ChooseRunnable forcechoose = new ChooseRunnable();
        forcechoose.runTaskTimer(this, 0, 5);
//creep
        CreepRunnable creeprun = new CreepRunnable();
        creeprun.runTaskTimer(this, 0, 15);
//phantom
        PhantomRunnable phantomrun = new PhantomRunnable();
        phantomrun.runTaskTimer(this, 0, 5);
        PhantomFormRunnable phantomformrun = new PhantomFormRunnable();
        phantomformrun.runTaskTimer(this, 0, 2);
        PhantomMain phantommainrun = new PhantomMain();
        phantommainrun.runTaskTimer(this, 0, 13);
//rabbit
        RabbitRunnable rabbitrun = new RabbitRunnable();
        rabbitrun.runTaskTimer(this, 0, 5);
//avian
        AvianRunnable avianrun = new AvianRunnable();
        avianrun.runTaskTimer(this, 0, 5);
//enchantments
        waterProtectionEnchant = new WaterProtection("waterprot");
        custom_enchants.add(waterProtectionEnchant);
        registerEnchantment(waterProtectionEnchant);

    }

    static {
        tool = EnumSet.of(Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_SWORD, Material.GOLDEN_AXE, Material.GOLDEN_HOE, Material.GOLDEN_PICKAXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_SWORD, Material.NETHERITE_AXE, Material.NETHERITE_HOE, Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_SWORD, Material.IRON_AXE, Material.IRON_HOE, Material.IRON_PICKAXE, Material.IRON_SHOVEL, Material.IRON_SWORD, Material.WOODEN_AXE, Material.WOODEN_HOE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_SWORD, Material.SHEARS);
    }


    @Override
    public void onDisable() {
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
