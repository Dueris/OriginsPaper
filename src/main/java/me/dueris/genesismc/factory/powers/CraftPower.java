package me.dueris.genesismc.factory.powers;

import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import io.papermc.paper.threadedregions.scheduler.*;
import me.dueris.genesismc.FoliaOriginScheduler;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.player.PlayerRender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jline.utils.ShutdownHooks;
import org.reflections.Reflections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public abstract class CraftPower implements Power {

    public static ArrayList<Class<? extends CraftPower>> registered = new ArrayList<>();

    public static List<Class<? extends CraftPower>> findCraftPowerClasses() throws IOException {
        List<Class<? extends CraftPower>> classes = new ArrayList<>();
        Reflections reflections = new Reflections("me.dueris.genesismc.factory.powers");

        Set<Class<? extends CraftPower>> subTypes = reflections.getSubTypesOf(CraftPower.class);
        for (Class<? extends CraftPower> subType : subTypes) {
            if (!subType.isInterface() && !subType.isEnum()) {
                classes.add(subType);
            }
        }

        return classes;
    }

    public static ArrayList<Class<? extends CraftPower>> getRegistered() {
        return registered;
    }

    public static boolean isCraftPower(Class<?> c) {
        return getRegistered().contains(c);
    }
}
