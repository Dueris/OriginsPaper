package me.dueris.genesismc;

import io.papermc.paper.event.player.PlayerFailMoveEvent;
import me.dueris.genesismc.choosing.ChoosingCORE;
import me.dueris.genesismc.choosing.ChoosingCUSTOM;
import me.dueris.genesismc.choosing.ChoosingForced;
import me.dueris.genesismc.commands.GenesisCommandManager;
import me.dueris.genesismc.commands.TabAutoComplete;
import me.dueris.genesismc.commands.ToggleCommand;
import me.dueris.genesismc.commands.subcommands.origin.Info.InInfoCheck;
import me.dueris.genesismc.commands.subcommands.origin.Info.Info;
import me.dueris.genesismc.commands.subcommands.origin.Recipe;
import me.dueris.genesismc.enchantments.EnchantProtEvent;
import me.dueris.genesismc.enchantments.WaterProtAnvil;
import me.dueris.genesismc.enchantments.WaterProtection;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.PowerStartHandler;
import me.dueris.genesismc.factory.powers.player.PlayerRender;
import me.dueris.genesismc.factory.powers.player.inventory.Inventory;
import me.dueris.genesismc.files.GenesisDataFiles;
import me.dueris.genesismc.generation.WaterProtBookGen;
import me.dueris.genesismc.items.GenesisItems;
import me.dueris.genesismc.items.InfinPearl;
import me.dueris.genesismc.items.OrbOfOrigins;
import me.dueris.genesismc.items.WaterProtItem;
import me.dueris.genesismc.utils.*;
import me.dueris.genesismc.utils.translation.LangConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import static me.dueris.genesismc.PlayerHandler.ReapplyEntityReachPowers;
import static me.dueris.genesismc.utils.BukkitColour.*;

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

    //OMG HIIII

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
        plugin = this;
        //bstats
        metrics = new Metrics(this, 18536);

        getServer().getPluginManager().registerEvents(new DataContainer(), this);
        getServer().getPluginManager().registerEvents(this, this);

        //configs

        GenesisDataFiles.loadOrbConfig();
        GenesisDataFiles.loadMainConfig();
        GenesisDataFiles.loadLangConfig();
        GenesisDataFiles.setup();
        Bukkit.getServer().getConsoleSender().sendMessage("[GenesisMC] origin-thread starting asynchronously");
        BukkitUtils.CopyOriginDatapack();
        if (LangConfig.lang_test == null) {
            getLogger().warning("[GenesisMC] Lang could not be loaded! Disabling plugin.");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        try {
            CraftApoli.loadOrigins();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Bukkit.getServer().getConsoleSender().sendMessage("[Origins] power-thread starting asynchronously");
        try {
            PowerStartHandler.startPowers();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //start
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC]    ____                               _         __  __    ____ ").color(TextColor.fromHexString("#b9362f")));
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC]   / ___|   ___   _ __     ___   ___  (_)  ___  |  \\/  |  / ___|").color(TextColor.fromHexString("#bebe42")));
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC]  | |  _   / _ \\ | '_ \\   / _ \\ / __| | | / __| | |\\/| | | |    ").color(TextColor.fromHexString("#4fec4f")));
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC]  | |_| | |  __/ | | | | |  __/ \\__ \\ | | \\__ \\ | |  | | | |___ ").color(TextColor.fromHexString("#4de4e4")));
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC]   \\____|  \\___| |_| |_|  \\___| |___/ |_| |___/ |_|  |_|  \\____|").color(TextColor.fromHexString("#333fb7")));
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] " + LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.credit")).color(TextColor.fromHexString("#dd50ff")));
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"));

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setGravity(true);
        }
        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] " + LangConfig.lang_test).color(TextColor.fromHexString(GREEN)));

        //version check
        if (GenesisDataFiles.getMainConfig().getString("version-check") == null || GenesisDataFiles.getMainConfig().getString("version-check").equalsIgnoreCase("true")) {
            VersionControl.pluginVersionCheck();
        } else {
            getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] " + LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.versionCheck.skip")).color(TextColor.fromHexString(YELLOW)));
        }

        //debug check
        if (GenesisDataFiles.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
            Debug.executeGenesisDebug();
            Debug.versionTest();
            getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] " + LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.debug.pluginVersion").replace("%pluginVersion%", "mc1.20-v0.2.1")).color(TextColor.fromHexString(GREEN)));
            getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] " + LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.debug.APIVersion").replace("%APIVersion%", "1.0.15")).color(TextColor.fromHexString(GREEN)));
        } else {
            getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] " + LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.debug.pluginVersion").replace("%pluginVersion%", "mc1.20-v0.2.1")).color(TextColor.fromHexString(GREEN)));
        }

        //origin load
        Bukkit.getServer().getConsoleSender().sendMessage("[Origins] origin-thread starting");
        for (OriginContainer origins : CraftApoli.getOrigins()) {
            if (GenesisDataFiles.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")) {
                getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] " + LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.debug.allOrigins").replace("%originName%", origins.getName())).color(TextColor.fromHexString(GREEN)));
            }
        }
        if (CraftApoli.getOrigins().size() > 0) {
            getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] " + LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.originAmount").replace("%originAmount%", String.valueOf(CraftApoli.getOrigins().size()))).color(TextColor.fromHexString(GREEN)));
        }

        getServer().getPluginManager().registerEvents(this, this);
        //Commands
        getCommand("origin").setExecutor(new GenesisCommandManager());
        getCommand("origin").setTabCompleter(new TabAutoComplete());
        getCommand("shulker").setTabCompleter(new TabAutoComplete());
        getCommand("shulker").setExecutor(new Inventory());
        getCommand("toggle").setExecutor(new ToggleCommand());
        //Event Handler Register
        getServer().getPluginManager().registerEvents(new PlayerHandler(), this);
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
        getServer().getPluginManager().registerEvents(new GenesisItems(), this);
        if (getServer().getPluginManager().isPluginEnabled("SkinsRestorer")) {
            getServer().getPluginManager().registerEvents(new PlayerRender.ModelColor(), GenesisMC.getPlugin());
            getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] " + LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.skinRestorer.present")).color(TextColor.fromHexString(AQUA)));
        } else {
            getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] " + LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.skinRestorer.absent")).color(TextColor.fromHexString(AQUA)));
        }
        plugin = this;

        OrbOfOrigins.init();
        InfinPearl.init();
        WaterProtItem.init();

        //runnables
        ChoosingForced forced = new ChoosingForced();
        forced.runTaskTimer(this, 0, 2);

        GenesisItems items = new GenesisItems();
        items.runTaskTimer(this, 0, 5);


        Bukkit.getServer().getPluginManager().registerEvents(new KeybindHandler(), GenesisMC.getPlugin());

        ScoreboardRunnable scorebo = new ScoreboardRunnable();
        scorebo.runTaskTimer(this, 0, 5);

        InInfoCheck info = new InInfoCheck();
        info.runTaskTimer(this, 0, 5);


        //enchantments
        waterProtectionEnchant = new WaterProtection("waterprot");
        custom_enchants.add(waterProtectionEnchant);
        registerEnchantment(waterProtectionEnchant);

        Bukkit.getServer().getConsoleSender().sendMessage(Component.text("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"));

        for (Player p : Bukkit.getOnlinePlayers()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    ReapplyEntityReachPowers(p);
                }
            }.runTaskTimer(GenesisMC.getPlugin(), 3, 0);
            PlayerHandler.originValidCheck(p);
            OriginPlayer.assignPowers(p);
            if (p.isOp())
                p.sendMessage(Component.text(LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "reloadMessage")).color(TextColor.fromHexString(AQUA)));
        }
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
        CraftApoli.unloadData();
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.getScoreboard().getTeam("origin-players").removeEntity(p);
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
