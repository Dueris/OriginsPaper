package me.dueris.genesismc.events;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.CraftPower;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class RegisterPowersEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    ArrayList<Class<? extends CraftPower>> powers;

    public RegisterPowersEvent(ArrayList<Class<? extends CraftPower>> powers) {
        this.powers = powers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public ArrayList<Class<? extends CraftPower>> getPowers() {
        return powers;
    }

    public void registerNewPower(Class<? extends CraftPower> power) {
        if (CraftPower.class.isAssignableFrom(power)) {
            CraftPower instance = null;
            try {
                instance = power.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            CraftPower.getRegistered().add(instance.getClass());
            Bukkit.getLogger().info("new CraftPower registered with POWER_TYPE " + instance.getPowerFile() + " with POWER_ARRAY of " + instance.getPowerArray().toString());

            if (instance instanceof Listener || Listener.class.isAssignableFrom(instance.getClass())) {
                Bukkit.getServer().getPluginManager().registerEvents((Listener) instance, GenesisMC.getPlugin());
            }
        }
    }

    @Override
    public String toString() {
        return "RegisterPowersEvent{" +
                "powers=" + "powers" +
                '}';
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
