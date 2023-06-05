package me.dueris.genesismc.core;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.choosing.ChoosingCORE;
import me.dueris.genesismc.core.choosing.ChoosingCUSTOM;
import me.dueris.genesismc.core.choosing.ChoosingForced;
import me.dueris.genesismc.core.commands.GenesisCommandManager;
import me.dueris.genesismc.core.commands.TabAutoComplete;
import me.dueris.genesismc.core.commands.ToggleCommand;
import me.dueris.genesismc.core.commands.subcommands.origin.Info;
import me.dueris.genesismc.core.commands.subcommands.origin.Recipe;
import me.dueris.genesismc.core.enchantments.EnchantProtEvent;
import me.dueris.genesismc.core.enchantments.WaterProtAnvil;
import me.dueris.genesismc.core.enchantments.WaterProtection;
import me.dueris.genesismc.core.factory.CraftApoliRewriten;
import me.dueris.genesismc.core.factory.OriginStartHandler;
import me.dueris.genesismc.core.factory.handlers.CustomOriginExistCheck;
import me.dueris.genesismc.core.factory.handlers.PlayerHandler;
import me.dueris.genesismc.core.factory.powers.Powers;
import me.dueris.genesismc.core.factory.powers.world.WorldSpawnHandler;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import me.dueris.genesismc.core.generation.WaterProtBookGen;
import me.dueris.genesismc.core.items.InfinPearl;
import me.dueris.genesismc.core.items.Items;
import me.dueris.genesismc.core.items.OrbOfOrigins;
import me.dueris.genesismc.core.items.WaterProtItem;
import me.dueris.genesismc.core.utils.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

public final class GenesisMC extends JavaPlugin implements Listener {
    public static EnumSet<Material> tool;
    public static Metrics metrics;
    public static ArrayList<Enchantment> custom_enchants = new ArrayList<>();
    public static WaterProtection waterProtectionEnchant;
    private static GenesisMC plugin;

    static {
        tool = EnumSet.of(Material.DIAMOND_AXE, Material.DIAMOND_HOE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_SWORD, Material.GOLDEN_AXE, Material.GOLDEN_HOE, Material.GOLDEN_PICKAXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_SWORD, Material.NETHERITE_AXE, Material.NETHERITE_HOE, Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_SWORD, Material.IRON_AXE, Material.IRON_HOE, Material.IRON_PICKAXE, Material.IRON_SHOVEL, Material.IRON_SWORD, Material.WOODEN_AXE, Material.WOODEN_HOE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL, Material.WOODEN_SWORD, Material.SHEARS);
    }

    public GenesisMC() {
    }

    //origin start end

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
        if (registered) {
            // It's been registered!
        }
    }

    public static GenesisMC getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        //bstats
        metrics = new Metrics(this, 18536);


        getServer().getPluginManager().registerEvents(new DataContainer(), this);

        //configs + folders

        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        GenesisDataFiles.loadOrbConfig();
        GenesisDataFiles.loadMainConfig();
        GenesisDataFiles.loadLangConfig();
        GenesisDataFiles.setup();

        //start

        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[GenesisMC]    ____                               _         __  __    ____ ");
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[GenesisMC]   / ___|   ___   _ __     ___   ___  (_)  ___  |  \\/  |  / ___|");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[GenesisMC]  | |  _   / _ \\ | '_ \\   / _ \\ / __| | | / __| | |\\/| | | |    ");
        getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[GenesisMC]  | |_| | |  __/ | | | | |  __/ \\__ \\ | | \\__ \\ | |  | | | |___ ");
        getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "[GenesisMC]   \\____|  \\___| |_| |_|  \\___| |___/ |_| |___/ |_|  |_|  \\____|");
        getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "[GenesisMC]  GenesisMC -- Created by Dueris");
        getServer().getConsoleSender().sendMessage(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~").color(TextColor.color(128, 128, 128)));

        if (GenesisDataFiles.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
            Debug.executeGenesisDebug();
            Debug.testIncompatiblePlugins();
            Debug.versionTest();
            getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Successfully loaded version 0.1.7-ALPHA_SNAPSHOT 1.19.4)").color(TextColor.color(0,200,0)));
            getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Successfully loaded API version 0.1.2-BETA (1.19.4)").color(TextColor.color(0,200,0)));
            getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Successfully loaded CONFIG version (1.19.4)").color(TextColor.color(0,200,0)));
        } else {
            getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Successfully loaded version 0.1.7-ALPHA_SNAPSHOT (1.19.4)"));
        }

        BukkitUtils.downloadFileToDirFromResource("datapacks"+File.separator+"OriginsGenesis.zip", "datapacks/OriginsGenesis.zip");
        CraftApoliRewriten.loadOrigins();
        for (OriginContainer origins : CraftApoliRewriten.getOrigins()) {
            if (GenesisDataFiles.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
                getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Loaded \"" + origins.getName() + "\""));
            }
        }
        if (CraftApoliRewriten.getOrigins().size() > 0) {
            getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Loaded (" + CraftApoliRewriten.getOrigins().size() + ") Origins"));
        }

        getServer().getPluginManager().registerEvents(this, this);
//Commands
        getCommand("origin").setExecutor(new GenesisCommandManager());
        getCommand("origin").setTabCompleter(new TabAutoComplete());
        getCommand("shulker").setTabCompleter(new TabAutoComplete());
        getCommand("shulker").setExecutor(new ShulkInv());
        getCommand("toggle").setExecutor(new ToggleCommand());
//Event Handler Register
        getServer().getPluginManager().registerEvents(new JoiningHandler(), this);
        getServer().getPluginManager().registerEvents(new EnchantProtEvent(), this);
        getServer().getPluginManager().registerEvents(new WaterProtAnvil(), this);
        getServer().getPluginManager().registerEvents(new PlayerAddScoreboard(), this);
        getServer().getPluginManager().registerEvents(new WaterProtBookGen(), this);
        getServer().getPluginManager().registerEvents(new KeybindHandler(), this);
        getServer().getPluginManager().registerEvents(new ChoosingCORE(), this);
        getServer().getPluginManager().registerEvents(new ChoosingCUSTOM(), this);
        getServer().getPluginManager().registerEvents(new Recipe(), this);
        getServer().getPluginManager().registerEvents(new Info(), this);
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        getServer().getPluginManager().registerEvents(new DataContainer(), this);
        getServer().getPluginManager().registerEvents(new PlayerHandler(), this);
        plugin = this;

//origin start begin

        OrbOfOrigins.init();
        InfinPearl.init();
        WaterProtItem.init();

        ChoosingForced forced = new ChoosingForced();
        forced.runTaskTimer(this, 0, 2);
        Items items = new Items();
        items.runTaskTimer(this, 0, 5);

        OriginStartHandler.StartRunnables();
        OriginStartHandler.StartListeners();

        //particle handler
        ParticleHandler handler = new ParticleHandler();
        handler.runTaskTimer(this, 0, 5);
        //scoreboard
        ScoreboardRunnable scorebo = new ScoreboardRunnable();
        scorebo.runTaskTimer(this, 0, 5);

        //enchantments
        waterProtectionEnchant = new WaterProtection("waterprot");
        custom_enchants.add(waterProtectionEnchant);
        registerEnchantment(waterProtectionEnchant);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp()) {
                p.sendMessage(Component.text("Origins Reloaded.").color(TextColor.color(137, 207, 240)));
            }
            CustomOriginExistCheck.customOriginExistCheck(p);
        }

        try {
            Bukkit.getServer().getConsoleSender().sendMessage(Component.text(Lang.lang_test).color(TextColor.color(0,200,0)));
        } catch (Exception e) {
            Bukkit.getServer().getConsoleSender().sendMessage(Component.text("A fatal error has occurred, lang could not be loaded. Disabling GenesisMC....").color(TextColor.color(200,0,0)));
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }

        getServer().getConsoleSender().sendMessage(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~").color(TextColor.color(128, 128, 128)));


        //for (Player p : Bukkit.getOnlinePlayers()) OriginPlayer.assignPowers(p);
    }

    @EventHandler
    public void NetherOriginRespawn(PlayerRespawnEvent e) {
        if (OriginPlayer.getOrigin(e.getPlayer()).getTag().equalsIgnoreCase("genesis:origin-piglin") || OriginPlayer.getOrigin(e.getPlayer()).getTag().equalsIgnoreCase("genesis:origin-blazeborn")) {
            if (!(e.isBedSpawn() || e.isAnchorSpawn())) {
                Location location = WorldSpawnHandler.NetherSpawn();
                if (location == null) return;
                e.setRespawnLocation(location);
            }

        }
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Disabling GenesisMC Origins.").color(TextColor.color(200,0,0)));
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

        //deletes origin files unzipped by Genesis
        CraftApoli.removeUnzippedDatapacks();
    }
}
