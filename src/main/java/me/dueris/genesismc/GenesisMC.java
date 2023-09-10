package me.dueris.genesismc;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import io.papermc.paper.event.player.PlayerFailMoveEvent;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import me.dueris.genesismc.choosing.ChoosingCORE;
import me.dueris.genesismc.choosing.ChoosingCUSTOM;
import me.dueris.genesismc.choosing.ChoosingGUI;
import me.dueris.genesismc.commands.GenesisCommandManager;
import me.dueris.genesismc.commands.TabAutoComplete;
import me.dueris.genesismc.commands.subcommands.origin.Info.InInfoCheck;
import me.dueris.genesismc.commands.subcommands.origin.Info.Info;
import me.dueris.genesismc.commands.subcommands.origin.Recipe;
import me.dueris.genesismc.enchantments.EnchantProtEvent;
import me.dueris.genesismc.enchantments.WaterProtAnvil;
import me.dueris.genesismc.enchantments.WaterProtection;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.events.RegisterPowersEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.conditions.CraftCondition;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.block.WaterBreathe;
import me.dueris.genesismc.factory.powers.player.PlayerRender;
import me.dueris.genesismc.factory.powers.simple.BounceSlimeBlock;
import me.dueris.genesismc.factory.powers.simple.MimicWarden;
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
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import static me.dueris.genesismc.PlayerHandler.ReapplyEntityReachPowers;
import static me.dueris.genesismc.factory.powers.simple.BounceSlimeBlock.bouncePlayers;
import static me.dueris.genesismc.factory.powers.simple.MimicWarden.getParticleTasks;
import static me.dueris.genesismc.factory.powers.simple.MimicWarden.mimicWardenPlayers;
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

    public static java.util.logging.@org.jetbrains.annotations.NotNull Logger getOriginLogger() {
        return GenesisMC.getPlugin().getLogger();
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

    public static BukkitScheduler pluginScheduler;

    public interface MyScheduledTask {
        void cancel();

        boolean isCancelled();

        Plugin getOwningPlugin();

        boolean isCurrentlyRunning();

        boolean isRepeatingTask();
    }

    public static class OriginScheduledTask implements MyScheduledTask {
        private final BukkitTask task;

        public OriginScheduledTask(final BukkitTask task) {
            this.task = task;
        }

        public void cancel() {
            this.task.cancel();
        }

        public boolean isCancelled() {
            return this.task.isCancelled();
        }

        @Override
        public Plugin getOwningPlugin() {
            return null;
        }

        @Override
        public boolean isCurrentlyRunning() {
            return false;
        }

        @Override
        public boolean isRepeatingTask() {
            return false;
        }
    }

    static TaskScheduler taskS;

    public static TaskScheduler getGlobalScheduler() {
        return taskS;
    }

    boolean previous_respawn_setting;

    @Override
    public void onEnable() {
        plugin = this;
        //bstats
        metrics = new Metrics(this, 18536);

        previous_respawn_setting = Bukkit.getWorlds().get(0).getGameRuleValue(GameRule.DO_IMMEDIATE_RESPAWN);

        Bukkit.getWorlds().get(0).setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);

        getServer().getPluginManager().registerEvents(new DataContainer(), this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new CooldownStuff(), this);

        //configs

        GenesisDataFiles.loadOrbConfig();
        GenesisDataFiles.loadMainConfig();
        GenesisDataFiles.loadLangConfig();
        GenesisDataFiles.setup();
        OriginCommandSender originCommandSender = new OriginCommandSender();
        originCommandSender.setOp(true);
        Bukkit.getServer().getConsoleSender().sendMessage("[GenesisMC] origin-thread starting asynchronously");
        try {
            BukkitUtils.CopyOriginDatapack();
        } catch (Exception E) {
            //throws an exception that the file already exists
        }
        if (LangConfig.lang_test == null) {
            getLogger().warning("[GenesisMC] Lang could not be loaded! Disabling plugin.");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        taskS = UniversalScheduler.getScheduler(this);

        try {
            CraftApoli.loadOrigins();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Bukkit.getServer().getConsoleSender().sendMessage("[Origins] power-thread starting asynchronously");

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
        try {
            for (Class<? extends CraftPower> c : CraftPower.findCraftPowerClasses()) {
                if (CraftPower.class.isAssignableFrom(c)) {
                    CraftPower instance = c.newInstance();
                    CraftPower.getRegistered().add(instance.getClass());
                    Bukkit.getLogger().info("new CraftPower registered with POWER_TYPE " + instance.getPowerFile() + " with POWER_ARRAY of " + instance.getPowerArray().toString());

                    if (instance instanceof Listener || Listener.class.isAssignableFrom(instance.getClass())) {
                        Bukkit.getServer().getPluginManager().registerEvents((Listener) instance, GenesisMC.getPlugin());
                    }
                }
            }
            RegisterPowersEvent registerPowersEvent = new RegisterPowersEvent(CraftPower.getRegistered());
            Bukkit.getServer().getPluginManager().callEvent(registerPowersEvent);
        } catch (IOException | ReflectiveOperationException e) {
            e.printStackTrace();
//                throw new RuntimeException(e);
        }

        try {
            for (Class<? extends Condition> c : CraftCondition.findCraftConditionClasses()) {
                CraftCondition.conditionClasses.add(c);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (OriginContainer origin : CraftApoli.getOrigins()) {
            for (PowerContainer powerContainer : origin.getPowerContainers()) {
                CraftApoli.getPowers().add(powerContainer);
            }
        }

        if (CraftApoli.getOrigins().size() > 0) {
            getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] " + LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "startup.originAmount").replace("%originAmount%", String.valueOf(CraftApoli.getOrigins().size()))).color(TextColor.fromHexString(GREEN)));
        }
        getServer().getPluginManager().registerEvents(this, this);
        //Commands
        getCommand("origin").setExecutor(new GenesisCommandManager());
        getCommand("origin").setTabCompleter(new TabAutoComplete());
        getCommand("power").setTabCompleter(new TabAutoComplete());
//        getCommand("power").setExecutor(new PowerCommand());
        //TODO: FINISH /POWER
        //Event Handler Register
        getServer().getPluginManager().registerEvents(new PlayerHandler(), this);
        getServer().getPluginManager().registerEvents(new EnchantProtEvent(), this);
        getServer().getPluginManager().registerEvents(new WaterProtAnvil(), this);
        getServer().getPluginManager().registerEvents(new WaterProtBookGen(), this);
        getServer().getPluginManager().registerEvents(new KeybindHandler(), this);
        getServer().getPluginManager().registerEvents(new ChoosingCORE(), this);
        getServer().getPluginManager().registerEvents(new ChoosingCUSTOM(), this);
        getServer().getPluginManager().registerEvents(new Recipe(), this);
        getServer().getPluginManager().registerEvents(new Info(), this);
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        getServer().getPluginManager().registerEvents(new DataContainer(), this);
        getServer().getPluginManager().registerEvents(new GenesisItems(), this);
        getServer().getPluginManager().registerEvents(new MimicWarden(), this);
        getServer().getPluginManager().registerEvents(new BounceSlimeBlock(), this);
        getServer().getPluginManager().registerEvents(new FoliaOriginScheduler.OriginSchedulerTree(), this);
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
        ChoosingGUI forced = new ChoosingGUI();
        forced.runTaskTimer(GenesisMC.getPlugin(), 0, 1);

        GenesisItems items = new GenesisItems();
        items.runTaskTimer(GenesisMC.getPlugin(), 0, 1);


        Bukkit.getServer().getPluginManager().registerEvents(new KeybindHandler(), GenesisMC.getPlugin());

        InInfoCheck info = new InInfoCheck();
        info.runTaskTimer(GenesisMC.getPlugin(), 0, 1);

        FoliaOriginScheduler.OriginSchedulerTree tree = new FoliaOriginScheduler.OriginSchedulerTree();
        tree.runTaskTimer(GenesisMC.getPlugin(), 0, 1);

        WaterBreathe waterBreathe = new WaterBreathe();
        new BukkitRunnable() {
            @Override
            public void run() {
                waterBreathe.run();
            }
        }.runTaskTimer(GenesisMC.getPlugin(), 0, 20);

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("SkinsRestorer")) {
            GlobalRegionScheduler globalRegionScheduler = Bukkit.getGlobalRegionScheduler();
            try {
                globalRegionScheduler.execute(GenesisMC.getPlugin(), PlayerRender.ModelColor.class.newInstance());
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

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
            }.runTaskLater(GenesisMC.getPlugin(), 5L);
            PlayerHandler.originValidCheck(p);
            OriginPlayer.assignPowers(p);
            if (p.isOp())
                p.sendMessage(Component.text(LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "reloadMessage")).color(TextColor.fromHexString(AQUA)));
            boolean hasMimicWardenPower = false;

            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                for (PowerContainer power : origin.getPowerContainers()) {
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

            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
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

        for (int taskId : getParticleTasks().values()) {
            getServer().getScheduler().cancelTask(taskId);
        }

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

        Bukkit.getWorlds().get(0).setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, previous_respawn_setting);

        getServer().getConsoleSender().sendMessage(Component.text("[GenesisMC] " + LangConfig.getLocalizedString(Bukkit.getConsoleSender(), "disable")).color(TextColor.fromHexString(RED)));
    }
}
