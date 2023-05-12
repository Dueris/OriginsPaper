package me.dueris.genesismc.core.factory.powers;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.bukkitrunnables.EnderianDamageRunnable;
import me.dueris.genesismc.core.factory.handlers.CustomOriginExistCheck;
import me.dueris.genesismc.core.factory.powers.block.MasterWebs;
import me.dueris.genesismc.core.factory.powers.block.fluid.WaterDamage;
import me.dueris.genesismc.core.factory.powers.entity.Arthropod;
import me.dueris.genesismc.core.factory.powers.entity.FreshAir;
import me.dueris.genesismc.core.factory.powers.entity.LayEggs;
import me.dueris.genesismc.core.factory.powers.entity.Reach;
import me.dueris.genesismc.core.factory.powers.food.Carnivore;
import me.dueris.genesismc.core.factory.powers.food.Vegitarian;
import me.dueris.genesismc.core.factory.powers.runnables.BurningWrath;
import me.dueris.genesismc.core.factory.powers.runnables.Climbing;

import static org.bukkit.Bukkit.getServer;

public class OriginStartHandler {
    public static void StartRunnables(){

        Climbing climb = new Climbing();
        climb.runTaskTimer(GenesisMC.getPlugin(), 0, 5);

        WaterDamage waterdamage = new WaterDamage();
        waterdamage.runTaskTimer(GenesisMC.getPlugin(), 0, 20);

        BurningWrath burningWrath = new BurningWrath();
        burningWrath.runTaskTimer(GenesisMC.getPlugin(), 0, 5);

    }

    public static void StartListeners(){
        getServer().getPluginManager().registerEvents(new CustomOriginExistCheck(), GenesisMC.getPlugin());
        getServer().getPluginManager().registerEvents(new Powers(), GenesisMC.getPlugin());
        getServer().getPluginManager().registerEvents(new MasterWebs(), GenesisMC.getPlugin());
        getServer().getPluginManager().registerEvents(new Arthropod(), GenesisMC.getPlugin());
        getServer().getPluginManager().registerEvents(new Carnivore(), GenesisMC.getPlugin());
        getServer().getPluginManager().registerEvents(new LayEggs(), GenesisMC.getPlugin());
        getServer().getPluginManager().registerEvents(new Vegitarian(), GenesisMC.getPlugin());
        getServer().getPluginManager().registerEvents(new FreshAir(), GenesisMC.getPlugin());
        getServer().getPluginManager().registerEvents(new Reach(), GenesisMC.getPlugin());
    }
}
