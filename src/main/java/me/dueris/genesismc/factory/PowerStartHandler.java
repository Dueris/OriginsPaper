package me.dueris.genesismc.factory;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.OriginsMod.effects.ApplyEffect;
import me.dueris.genesismc.factory.powers.OriginsMod.effects.NightVision;
import me.dueris.genesismc.factory.powers.OriginsMod.player.attributes.AttributeHandler;
import me.dueris.genesismc.files.GenesisDataFiles;
import org.bukkit.Bukkit;

import java.util.List;

import static me.dueris.genesismc.GenesisMC.getPlugin;
import static me.dueris.genesismc.factory.powers.CraftPower.findCraftPowerClasses;

public class PowerStartHandler {

    public static void startPowers() throws Exception {
        Bukkit.getPluginManager().registerEvents(new AttributeHandler.Reach(), GenesisMC.getPlugin());
        List<Class<? extends CraftPower>> craftPowerClasses = findCraftPowerClasses();
        for (Class<? extends CraftPower> c : craftPowerClasses) {
            CraftPower.register(c);
            if(GenesisDataFiles.getMainConfig().getString("console-startup-debug").equalsIgnoreCase("true")){
                Bukkit.getConsoleSender().sendMessage(c.getSimpleName() + " has been registered with power of " + c.newInstance().getPowerFile());
            }
        }
    }
}
