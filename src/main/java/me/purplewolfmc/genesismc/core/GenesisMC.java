package me.purplewolfmc.genesismc.core;

import me.purplewolfmc.genesismc.core.bukkitrunnables.*;
import me.purplewolfmc.genesismc.core.commands.BetaCommands;
import me.purplewolfmc.genesismc.core.commands.GenesisCommandManager;
import me.purplewolfmc.genesismc.core.commands.TabAutoComplete;
import me.purplewolfmc.genesismc.core.commands.subcommands.Purge;
import me.purplewolfmc.genesismc.core.commands.subcommands.Reload;
import me.purplewolfmc.genesismc.custom_origins.CustomOrigins;
import me.purplewolfmc.genesismc.custom_origins.handlers.CustomMenuHandler;
import me.purplewolfmc.genesismc.core.enchantments.EnchantProtEvent;
import me.purplewolfmc.genesismc.core.enchantments.WaterProtection;
import me.purplewolfmc.genesismc.core.files.GenesisDataFiles;
import me.purplewolfmc.genesismc.core.items.InfinPearl;
import me.purplewolfmc.genesismc.core.items.OrbOfOrigins;
import me.purplewolfmc.genesismc.core.items.WaterProtItem;
import me.purplewolfmc.genesismc.core.origins.arachnid.ArachnidClimb;
import me.purplewolfmc.genesismc.core.origins.arachnid.ArachnidMain;
import me.purplewolfmc.genesismc.core.origins.enderian.*;
import me.purplewolfmc.genesismc.core.origins.human.HumanMain;
import me.purplewolfmc.genesismc.core.origins.shulker.ShulkInv;
import me.purplewolfmc.genesismc.core.origins.shulker.ShulkerMain;
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

        //configs

        this.saveDefaultConfig();

        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();

        GenesisDataFiles.setup();
        GenesisDataFiles.get().options().copyDefaults(true);
        GenesisDataFiles.get1().options().copyDefaults(true);
        GenesisDataFiles.get2().options().copyDefaults(true);
        GenesisDataFiles.get3().options().copyDefaults(true);
        GenesisDataFiles.get4().options().copyDefaults(true);
        GenesisDataFiles.get5().options().copyDefaults(true);
        GenesisDataFiles.get6().options().copyDefaults(true);
        GenesisDataFiles.get7().options().copyDefaults(true);
        GenesisDataFiles.get8().options().copyDefaults(true);
        GenesisDataFiles.get9().options().copyDefaults(true);
        GenesisDataFiles.get10().options().copyDefaults(true);
        GenesisDataFiles.get11().options().copyDefaults(true);
        GenesisDataFiles.get12().options().copyDefaults(true);
        GenesisDataFiles.get13().options().copyDefaults(true);
        GenesisDataFiles.get14().options().copyDefaults(true);
        GenesisDataFiles.get15().options().copyDefaults(true);
        GenesisDataFiles.get16().options().copyDefaults(true);
        GenesisDataFiles.get17().options().copyDefaults(true);
        GenesisDataFiles.getOrb().options().copyDefaults(true);
        GenesisDataFiles.getBeta().options().copyDefaults(true);
        GenesisDataFiles.getMenu().options().copyDefaults(true);
        GenesisDataFiles.getPlugCon().options().copyDefaults(true);
        GenesisDataFiles.setDefaults();
        GenesisDataFiles.save();

        //start

        getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "[GenesisMC]              ____");
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC]  ___        |    |   ___  [*]");
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[GenesisMC] |   | | | | |    |  |   |");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[GenesisMC] |___| | | | |____|  |___|  |");
        getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[GenesisMC] |     | | | |    |  |      |");
        getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "[GenesisMC] |     |_|_| |    |  |      |");
        getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[GenesisMC] |           |    |  |      | Created by PurpleWolfMC");
        getServer().getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[GenesisMC] |                   |");
        getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        if (!this.getConfig().getString("config-version").equalsIgnoreCase(String.valueOf(0141))) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] WARNING. THIS IS THE WRONG CONFIG VERSION. PLEASE RELOAD THE CONFIG OR DELETE THE CONFIG AND RESTART");
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] ATTEMPTING RELOAD OF CONFIG FILE");
            this.getConfig().set("config-version", 0141);
            getServer().getPluginManager().getPlugin("genesismc").saveConfig();
            if (!this.getConfig().getString("config-version").equalsIgnoreCase(String.valueOf(0141))) {
                getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] ERROR RELOADING CONFIG. RETRYING");
                getServer().getPluginManager().getPlugin("genesismc").getConfig().set("config-version", 0141);
                getServer().getPluginManager().getPlugin("genesismc").saveConfig();
            } else {
                getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[GenesisMC] RELOAD SUCCESSFUL.");
            }
            this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                public void run() {
                    if (getConfig().getString("config-version") == null || !getConfig().getString("config-version").equalsIgnoreCase(String.valueOf(0141))) {
                        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] ERROR RELOADING CONFIG. PLEASE DELETE CONFIG FILE AND RESTART");
                        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] ATTEMPTING RELOAD OF CONFIG FILE");
                        getServer().getPluginManager().getPlugin("genesismc").getConfig().set("config-version", 0141);
                        getServer().getPluginManager().getPlugin("genesismc").saveConfig();
                    }
                }
            }, 0, 5);
        }
        if (this.getConfig().getString("config-version") == null) {
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] ERROR LOADING CONFIG");
        }


        //custom origins loader
        CustomOrigins.onEnableCusotmOrigins();



        if (GenesisDataFiles.getPlugCon().getString("console-dump-onstartup").equalsIgnoreCase("true")) {
            getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[GenesisMC] Loading API version 0.1.1");
            getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[GenesisMC] Loading Subcommands");
            getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[GenesisMC] Loading OriginsChoosingCommands");
            //method
            dumpCon();
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[GenesisMC] Successfully loaded version 0.1.5-ALPHA_SNAPSHOT (1.19.4)");
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[GenesisMC] Successfully loaded API version 0.1.1-BETA (1.19.4)");
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[GenesisMC] Successfully loaded CONFIG version 0138` (1.19.4)");
            if (GenesisDataFiles.getPlugCon().getString("use-purplewolfapi").equalsIgnoreCase("false")) {
                getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC] WARNGING. PURPLEWOLFAPI IS DISABLED. THINGS WILL BREAK. PLEASE TURN BACK TO TRUE");
                getServer().getPluginManager().getPlugin("origins-spigotmc").reloadConfig();
            }
        } else {
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[GenesisMC] Successfully loaded version 0.1.5-ALPHA_SNAPSHOT (1.19.4)");
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
        getCommand("origins").setExecutor(new GenesisCommandManager());
        getCommand("origins").setTabCompleter(new TabAutoComplete());
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
        getServer().getPluginManager().registerEvents(new Reload(), this);
        getServer().getPluginManager().registerEvents(new ShulkerMain(), this);
        getServer().getPluginManager().registerEvents(new JoiningHandler(), this);
        getServer().getPluginManager().registerEvents(new EnchantProtEvent(), this);
        getServer().getPluginManager().registerEvents(new BrethrenOfEnd(), this);
        getServer().getPluginManager().registerEvents(new ArachnidMain(), this);
        getServer().getPluginManager().registerEvents(new CustomMenuHandler(), this);
        getServer().getPluginManager().registerEvents(new ArachnidClimb(), this);
        plugin = this;
        getServer().getPluginManager().registerEvents(new DataContainer(), this);
        if (GenesisDataFiles.getBeta().getString("update-beta").equalsIgnoreCase("true")) {
            getCommand("beta").setExecutor(new BetaCommands());
        }
        OrbOfOrigins.init();
        InfinPearl.init();
        WaterProtItem.init();
        //runnables main

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
//forcechoose
        ForceChooseRunnable forcechoose = new ForceChooseRunnable();
        forcechoose.runTaskTimer(this, 0, 4);

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

    public void dumpCon(){
        getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[GenesisMC] DUMPING PLUGIN-API FILES:");
        getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "Loading config file:" +
                this.getConfig().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.get().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.get1().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.get2().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.get3().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.get4().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.get5().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.get6().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.get7().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.get8().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.get9().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.get10().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.get11().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.get12().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.get13().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.get14().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.get15().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.get16().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.get17().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.getOrb().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.getBeta().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.getMenu().getValues(Boolean.parseBoolean("all")) +
                GenesisDataFiles.getPlugCon().getValues(Boolean.parseBoolean("all"))

        );
        getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[GenesisMC] Loading API");
        getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[GenesisMC] DUMPING SERVER FILES:" +
                getServer().getVersion() +
                getServer().getAllowEnd() +
                getServer().getAllowNether() +
                getServer().getPluginManager() +
                getServer().getMaxPlayers() +
                getServer().getConnectionThrottle() +
                getServer().getLogger() +
                getServer().getName() +
                getServer().getBukkitVersion() +
                getServer().getDefaultGameMode() +
                getServer().getWorldType() +
                getServer().getResourcePack() +
                getServer().getHelpMap() +
                getServer().getPluginManager().getPlugins()
        );

    }
}
