package me.dueris.genesismc.core.factory.powers;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.factory.handlers.CustomOriginExistCheck;
import me.dueris.genesismc.core.factory.powers.block.Webbing;
import me.dueris.genesismc.core.factory.powers.block.fluid.WaterDamage;
import me.dueris.genesismc.core.factory.powers.entity.*;
import me.dueris.genesismc.core.factory.powers.food.Carnivore;
import me.dueris.genesismc.core.factory.powers.food.Vegitarian;
import me.dueris.genesismc.core.factory.powers.runnables.BurningWrath;
import me.dueris.genesismc.core.factory.powers.runnables.Climbing;
import me.dueris.genesismc.core.factory.powers.runnables.NoCobwebSlowdown;
import me.dueris.genesismc.core.factory.powers.runnables.SlowFalling;

import static org.bukkit.Bukkit.getServer;

public class OriginStartHandler {
    public static void StartRunnables(){

        Climbing climb = new Climbing();
        climb.runTaskTimer(GenesisMC.getPlugin(), 0, 5);

        WaterDamage waterdamage = new WaterDamage();
        waterdamage.runTaskTimer(GenesisMC.getPlugin(), 0, 20);

        BurningWrath burningWrath = new BurningWrath();
        burningWrath.runTaskTimer(GenesisMC.getPlugin(), 0, 5);

        SlowFalling slowFalling = new SlowFalling();
        slowFalling.runTaskTimer(GenesisMC.getPlugin(), 0, 5);

    }

    public static void StartListeners(){
        getServer().getPluginManager().registerEvents(new CustomOriginExistCheck(), GenesisMC.getPlugin());
        getServer().getPluginManager().registerEvents(new Powers(), GenesisMC.getPlugin());
        getServer().getPluginManager().registerEvents(new Webbing(), GenesisMC.getPlugin());
        getServer().getPluginManager().registerEvents(new Arthropod(), GenesisMC.getPlugin());
        getServer().getPluginManager().registerEvents(new Carnivore(), GenesisMC.getPlugin());
        getServer().getPluginManager().registerEvents(new LayEggs(), GenesisMC.getPlugin());
        getServer().getPluginManager().registerEvents(new Vegitarian(), GenesisMC.getPlugin());
        getServer().getPluginManager().registerEvents(new FreshAir(), GenesisMC.getPlugin());
        getServer().getPluginManager().registerEvents(new Reach(), GenesisMC.getPlugin());
        //getServer().getPluginManager().registerEvents(new NoCobwebSlowdown(), GenesisMC.getPlugin());
        getServer().getPluginManager().registerEvents(new HotHands(), GenesisMC.getPlugin());
    }
}
