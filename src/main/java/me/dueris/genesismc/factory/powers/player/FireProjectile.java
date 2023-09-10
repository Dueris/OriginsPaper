package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.CooldownStuff;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.KeybindHandler;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.events.KeybindTriggerEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import static me.dueris.genesismc.KeybindHandler.isKeyBeingPressed;

public class FireProjectile extends CraftPower implements Listener {

    public static ArrayList<Player> in_continuous = new ArrayList<>();
    public static ArrayList<Player> enderian_pearl = new ArrayList<>();
    public static ArrayList<Player> in_cooldown_patch = new ArrayList<>();

    public static void addCooldownPatch(Player p) {
        in_cooldown_patch.add(p);
        p.sendMessage("1");
        new BukkitRunnable() {
            @Override
            public void run() {
                in_cooldown_patch.remove(p);
                p.sendMessage("2");
                p.sendMessage(String.valueOf(in_cooldown_patch.contains(p)));
            }
        }.runTaskLater(GenesisMC.getPlugin(), 5);
    }

    public static int parseOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @EventHandler
    public void keybindCONTINUOUSDF(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
            if (fire_projectile.contains(p)) {
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (isKeyBeingPressed(e.getPlayer(), power.getKey().get("key").toString(), true)) {
                        if (in_continuous.contains(p)) {
                            in_continuous.remove(p);
                        } else {
                            in_continuous.add(p);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void teleportDamgeOff(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        if (enderian_pearl.contains(e.getPlayer()) && e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            e.setCancelled(true);
            p.teleport(e.getTo());
        }

    }


    @EventHandler
    public void keybindPress(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        ArrayList<Player> peopladf = new ArrayList<>();
        if (!peopladf.contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (power == null) {
                        getPowerArray().remove(p);
                        return;
                    } else {
                        if (fire_projectile.contains(p)) {
                            ConditionExecutor conditionExecutor = new ConditionExecutor();
                            if (conditionExecutor.check("condition", "conditions", p, power, "origins:fire_projectile", p, null, null, null, p.getItemInHand(), null)) {
                                if (!CooldownStuff.isPlayerInCooldown(p, power.getKey().get("key").toString())) {
                                    if (isKeyBeingPressed(e.getPlayer(), power.getKey().get("key").toString(), true)) {
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                Sound sound;
                                                int cooldown = Integer.parseInt(power.get("cooldown", "1"));
                                                String tag = power.get("tag", null);
                                                float divergence = Float.parseFloat(power.get("divergence", "1.0"));
                                                float speed = Float.parseFloat(power.get("speed", "1"));
                                                int amt = Integer.parseInt(power.get("count", "1"));
                                                int start_delay = Integer.parseInt(power.get("start_delay", "0"));
                                                int interval = Integer.parseInt(power.get("interval", "1"));

                                                // Introduce a slight random divergence
                                                divergence += (float) ((Math.random() - 0.5) * 0.05); // Adjust the 0.05 value to control the randomness level

                                                EntityType type;
                                                if (power.get("entity_type", null).equalsIgnoreCase("origins:enderian_pearl")) {
                                                    type = EntityType.ENDER_PEARL;
                                                    enderian_pearl.add(p);
                                                } else {
                                                    type = EntityType.valueOf(power.get("entity_type", null).split(":")[1].toUpperCase());
                                                    enderian_pearl.remove(p);
                                                }

                                                String key = (String) power.getKey().get("key");
                                                if (!CooldownStuff.isPlayerInCooldown(p, key)) {
                                                    KeybindHandler.runKeyChangeTrigger(KeybindHandler.getTriggerFromOriginKey(p, key));

                                                    float finalDivergence = divergence;
                                                    float finalDivergence1 = divergence;
                                                    final boolean[] thing = new boolean[1];
                                                    new BukkitRunnable() {
                                                        int shotsLeft = -amt;

                                                        @Override
                                                        public void run() {
                                                            if (!CooldownStuff.isPlayerInCooldown(p, key)) {
                                                                if (shotsLeft >= 0) {
                                                                    if (power.getKey().get("continuous").toString().equalsIgnoreCase("false")) {
                                                                        KeybindHandler.runKeyChangeTriggerReturn(KeybindHandler.getTriggerFromOriginKey(p, key), p, key);
                                                                        CooldownStuff.addCooldown(p, origin, power.getTag(), power.getType(), cooldown * 2, key);
                                                                        addCooldownPatch(p);
                                                                        peopladf.remove(p);
                                                                        ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                                        KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                                        shotsLeft = 0;
                                                                        thing[0] = true;
                                                                        if (power == null) {
                                                                            getPowerArray().remove(p);
                                                                            return;
                                                                        }
                                                                        if (!getPowerArray().contains(p)) return;
                                                                        setActive(power.getTag(), false);
                                                                        this.cancel();
                                                                    } else {
                                                                        if (!in_continuous.contains(p)) {
                                                                            KeybindHandler.runKeyChangeTriggerReturn(KeybindHandler.getTriggerFromOriginKey(p, key), p, key);
                                                                            CooldownStuff.addCooldown(p, origin, power.getTag(), power.getType(), cooldown * 2, key);
                                                                            addCooldownPatch(p);
                                                                            peopladf.remove(p);
                                                                            ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                                            met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                                            KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                                            shotsLeft = 0;
                                                                            thing[0] = true;
                                                                            if (power == null)
                                                                                return;
                                                                            if (!getPowerArray().contains(p)) return;
                                                                            setActive(power.getTag(), false);
                                                                            this.cancel();
                                                                        } else {
                                                                            ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                                            met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, true);
                                                                            if (power == null)
                                                                                return;
                                                                            if (!getPowerArray().contains(p)) return;
                                                                            setActive(power.getTag(), true);
                                                                            KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                                            shotsLeft = -amt;
                                                                        }
                                                                    }
                                                                    return;
                                                                }

                                                                p.setCooldown(KeybindHandler.getKeybindItem(e.getKey(), p.getInventory()).getType(), cooldown * 2);
                                                                addCooldownPatch(p);

                                                                if (type.getEntityClass() != null && Projectile.class.isAssignableFrom(type.getEntityClass())) {
                                                                    Projectile projectile = (Projectile) p.getWorld().spawnEntity(p.getEyeLocation(), type);
                                                                    projectile.setShooter(p);

                                                                    Vector direction = p.getEyeLocation().getDirection();

                                                                    double yawRadians = Math.toRadians(p.getEyeLocation().getYaw() + finalDivergence1);

                                                                    double x = -Math.sin(yawRadians) * Math.cos(Math.toRadians(p.getLocation().getPitch()));
                                                                    double y = -Math.sin(Math.toRadians(p.getLocation().getPitch()));
                                                                    double z = Math.cos(yawRadians) * Math.cos(Math.toRadians(p.getLocation().getPitch()));

                                                                    direction.setX(x);
                                                                    direction.setY(y);
                                                                    direction.setZ(z);

                                                                    projectile.setVelocity(direction.normalize().multiply(speed));
                                                                    projectile.setGlowing(true);
                                                                    if (power == null)
                                                                        return;
                                                                    if (!getPowerArray().contains(p)) return;
                                                                    setActive(power.getTag(), true);

                                                                    peopladf.add(p);

                                                                }

                                                                shotsLeft++; // Decrement the remaining shots
                                                            }
                                                        }
                                                    }.runTaskTimer(GenesisMC.getPlugin(), start_delay, interval);

                                                    if (thing[0]) {
                                                        thing[0] = false;
                                                        this.cancel();
                                                    }

                                                    if (power.getKey().get("continuous").toString().equalsIgnoreCase("false")) {
                                                        ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                        if (power == null) {
                                                            getPowerArray().remove(p);
                                                            return;
                                                        }
                                                        if (!getPowerArray().contains(p)) return;
                                                        setActive(power.getTag(), false);
                                                        KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                        this.cancel();
                                                    } else {
                                                        if (isKeyBeingPressed(e.getPlayer(), power.getKey().get("key").toString(), true)) {
                                                            ItemMeta met = KeybindHandler.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                            met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                            if (power == null) {
                                                                getPowerArray().remove(p);
                                                                return;
                                                            }
                                                            if (!getPowerArray().contains(p)) return;
                                                            setActive(power.getTag(), false);
                                                            KeybindHandler.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                            this.cancel();
                                                        }
                                                    }
                                                }
                                            }
                                        }.runTaskTimer(GenesisMC.getPlugin(), 0, 1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    Player p;

    public FireProjectile() {
        this.p = p;
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "origins:fire_projectile";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return fire_projectile;
    }

    //TODO: make the kye thinger and all the executorss
}
