package me.dueris.genesismc.core;

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
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.factory.PowerStartHandler;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import me.dueris.genesismc.core.generation.WaterProtBookGen;
import me.dueris.genesismc.core.items.InfinPearl;
import me.dueris.genesismc.core.items.Items;
import me.dueris.genesismc.core.items.OrbOfOrigins;
import me.dueris.genesismc.core.items.WaterProtItem;
import me.dueris.genesismc.core.utils.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import static me.dueris.genesismc.core.utils.BukkitColour.*;

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

        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC]    ____                               _         __  __    ____ ").color(TextColor.fromHexString("#b9362f")));
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC]   / ___|   ___   _ __     ___   ___  (_)  ___  |  \\/  |  / ___|").color(TextColor.fromHexString("#bebe42")));
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC]  | |  _   / _ \\ | '_ \\   / _ \\ / __| | | / __| | |\\/| | | |    ").color(TextColor.fromHexString("#4fec4f")));
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC]  | |_| | |  __/ | | | | |  __/ \\__ \\ | | \\__ \\ | |  | | | |___ ").color(TextColor.fromHexString("#4de4e4")));
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC]   \\____|  \\___| |_| |_|  \\___| |___/ |_| |___/ |_|  |_|  \\____|").color(TextColor.fromHexString("#333fb7")));
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC]  GenesisMC -- Created by The Genesis Team").color(TextColor.fromHexString("#dd50ff")));
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"));
        //lang check
        if (Lang.lang_test == null) {
            getLogger().warning("[GenesisMC] Lang could not be loaded! Disabling plugin.");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] " + Lang.lang_test).color(TextColor.fromHexString(GREEN)));

        //version check
        if (GenesisDataFiles.getMainConfig().getString("version-check") == null || GenesisDataFiles.getMainConfig().getString("version-check").equalsIgnoreCase("true")) {
            VersionControl.pluginVersionCheck();
        } else {
            getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Skipping version check").color(TextColor.fromHexString(YELLOW)));
        }

        //debug check
        if (GenesisDataFiles.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
            Debug.executeGenesisDebug();
            Debug.testIncompatiblePlugins();
            Debug.versionTest();
            getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Successfully loaded version mc1.20-v0.1.7").color(TextColor.fromHexString(GREEN)));
            getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Successfully loaded API version 1.0.15").color(TextColor.fromHexString(GREEN)));
        } else {
            getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Successfully loaded version mc1.20-v0.1.7").color(TextColor.fromHexString(GREEN)));
        }

        //origin load
        BukkitUtils.CopyOriginDatapack();
        CraftApoli.loadOrigins();
        for (OriginContainer origins : CraftApoli.getOrigins()) {
            if (GenesisDataFiles.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
                getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Loaded \"" + origins.getName() + "\"").color(TextColor.fromHexString(GREEN)));
            }
        }
        if (CraftApoli.getOrigins().size() > 0) {
            getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] Loaded (" + CraftApoli.getOrigins().size() + ") Origins").color(TextColor.fromHexString(GREEN)));
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
        getServer().getPluginManager().registerEvents(new Items(), this);
        plugin = this;

//origin start begin

        OrbOfOrigins.init();
        InfinPearl.init();
        WaterProtItem.init();

        ChoosingForced forced = new ChoosingForced();
        forced.runTaskTimer(this, 0, 2);
        Items items = new Items();
        items.runTaskTimer(this, 0, 5);

        PowerStartHandler.StartRunnables();
        PowerStartHandler.StartListeners();

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

        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"));


        for (Player p : Bukkit.getOnlinePlayers()) {
            JoiningHandler.customOriginExistCheck(p);
            OriginPlayer.assignPowers(p);
            if (p.isOp()) p.sendMessage(Component.text("Origins Reloaded.").color(TextColor.fromHexString(AQUA)));
        }
    }

    @Override
    public void onDisable() {
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
        getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] GenesisMC disabled.").color(TextColor.fromHexString(RED)));
    }
}
