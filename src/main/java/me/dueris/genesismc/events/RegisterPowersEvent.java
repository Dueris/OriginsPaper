package me.dueris.genesismc.events;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.CraftPower;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class RegisterPowersEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public RegisterPowersEvent() {}

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public void registerNewPower(Class<? extends CraftPower> c) throws InstantiationException, IllegalAccessException {
        if (CraftPower.class.isAssignableFrom(c)) {
            CraftPower instance = c.newInstance();
            CraftPower.getRegistered().add(c);
            CraftPower.getKeyedRegistry().put(instance.getPowerFile(), c);
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "New CraftPower registered for {%%}".replace("%%", instance.getPowerFile()));
            if (instance instanceof Listener || Listener.class.isAssignableFrom(c)) {
                Bukkit.getServer().getPluginManager().registerEvents((Listener) instance, GenesisMC.getPlugin());
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean callEvent() {
        return super.callEvent();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
