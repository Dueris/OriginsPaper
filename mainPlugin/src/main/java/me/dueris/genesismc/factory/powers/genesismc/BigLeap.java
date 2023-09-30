package me.dueris.genesismc.factory.powers.genesismc;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class BigLeap extends CraftPower implements Listener {

    private static final HashMap<UUID, Integer> cooldownBefore = new HashMap<>();
    private static final HashMap<UUID, Long> cooldownAfter = new HashMap<>();
    private static final HashMap<UUID, Boolean> playSound = new HashMap<>();
    private static final ArrayList<UUID> inAir = new ArrayList<>();

    public static boolean leapToggle(Player p) {
        PersistentDataContainer data = p.getPersistentDataContainer();
        int toggleState = data.get(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER);
        if (toggleState == 1) {
            p.sendMessage(ChatColor.GREEN + "Leap enabled.");
        } else {
            p.sendMessage(ChatColor.RED + "Leap disabled.");
        }
        p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
        return true;
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    @EventHandler
    public void onRabbitLeap(PlayerToggleSneakEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        if (getPowerArray().contains(e.getPlayer())) {
            for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (power != null) {
                        Player p = e.getPlayer();
                        ConditionExecutor executor = new ConditionExecutor();
                        if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {
                            setActive(power.getTag(), true);
                            for (HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifiers")) {
                                int cooldownTicks = Integer.valueOf(modifier.get("cooldown").toString());
                                int tickCharge = Integer.valueOf(modifier.get("tick_charge").toString());
                                int toggleState = data.get(new NamespacedKey(GenesisMC.getPlugin(), "toggle"), PersistentDataType.INTEGER);
                                if (p.isSneaking()) return;
                                if (!p.isOnGround()) return;
                                if (cooldownAfter.containsKey(p.getUniqueId())) return;
                                if (toggleState == 2) return;

                                cooldownBefore.put(p.getUniqueId(), 0);
                                playSound.put(p.getUniqueId(), Boolean.TRUE);

                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (p.isSneaking()) {
                                            if (cooldownBefore.get(p.getUniqueId()) == tickCharge / 5) {
                                                p.sendActionBar(ChatColor.RED + "|||");
                                            } else if (cooldownBefore.get(p.getUniqueId()) == tickCharge * 2 / 5) {
                                                p.sendActionBar(ChatColor.RED + "|||||");
                                            } else if (cooldownBefore.get(p.getUniqueId()) == tickCharge * 3 / 5) {
                                                p.sendActionBar(ChatColor.YELLOW + "|||||||");
                                            } else if (cooldownBefore.get(p.getUniqueId()) == tickCharge * 4 / 5) {
                                                p.sendActionBar(ChatColor.YELLOW + "|||||||||");
                                            } else if (cooldownBefore.get(p.getUniqueId()) >= tickCharge) {
                                                p.sendActionBar(ChatColor.GREEN + "|||||||||||");
                                                cooldownBefore.replace(p.getUniqueId(), 9);
                                                if (playSound.get(p.getUniqueId())) {
                                                    p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
                                                    playSound.replace(p.getUniqueId(), Boolean.FALSE);
                                                }
                                            }
                                            cooldownBefore.replace(p.getUniqueId(), cooldownBefore.get(p.getUniqueId()) + 1);
                                        } else {
                                            cooldownAfter.put(p.getUniqueId(), System.currentTimeMillis());
                                            inAir.add(p.getUniqueId());
                                            p.setVelocity(p.getLocation().getDirection().multiply(1.5 + cooldownBefore.get(p.getUniqueId()) / 10));
                                            cooldownBefore.remove(p.getUniqueId());
                                            playSound.remove(p.getUniqueId());
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    if (cooldownAfter.containsKey(p.getUniqueId())) {
                                                        if (System.currentTimeMillis() - cooldownAfter.get(p.getUniqueId()) >= cooldownTicks / 5) {
                                                            p.sendActionBar(ChatColor.RED + "|||||||||");
                                                        }
                                                        if (System.currentTimeMillis() - cooldownAfter.get(p.getUniqueId()) >= cooldownTicks * 2L / 5) {
                                                            p.sendActionBar(ChatColor.RED + "|||||||");
                                                        }
                                                        if (System.currentTimeMillis() - cooldownAfter.get(p.getUniqueId()) >= cooldownTicks * 3L / 5) {
                                                            p.sendActionBar(ChatColor.YELLOW + "|||||");
                                                        }
                                                        if (System.currentTimeMillis() - cooldownAfter.get(p.getUniqueId()) >= cooldownTicks * 4L / 5) {
                                                            p.sendActionBar(ChatColor.YELLOW + "|||");
                                                        }
                                                        if (System.currentTimeMillis() - cooldownAfter.get(p.getUniqueId()) >= cooldownTicks) {
                                                            cooldownAfter.remove(p.getUniqueId());
                                                            p.sendActionBar(ChatColor.GREEN + "-");
                                                            inAir.remove(p.getUniqueId());
                                                            p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);

                                                        }
                                                    } else {
                                                        this.cancel();
                                                    }
                                                }
                                            }.runTaskTimer(GenesisMC.getPlugin(), 0L, 10L);

                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    if (cooldownAfter.containsKey(p.getUniqueId())) {
                                                        p.playSound(p.getLocation(), Sound.BLOCK_SCAFFOLDING_HIT, 1, 2);
                                                    } else {
                                                        this.cancel();
                                                    }
                                                }
                                            }.runTaskTimer(GenesisMC.getPlugin(), 0L, 50L);
                                            this.cancel();
                                        }
                                    }
                                }.runTaskTimer(GenesisMC.getPlugin(), 0L, 2L);
                            }
                        } else {
                            if (power == null) {
                                getPowerArray().remove(p);
                                return;
                            }
                            if (!getPowerArray().contains(p)) return;
                            setActive(power.getTag(), false);
                        }
                    }
                }

            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;

        if (big_leap_tick.contains(p)) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                if (inAir.contains(p.getUniqueId())) {
                    e.setCancelled(true);
                    inAir.remove(p.getUniqueId());
                }
                e.setDamage(e.getDamage() - 4);
                if (e.getDamage() <= 0) {
                    e.setCancelled(true);
                }
            }
        }
    }

    Player p;

    public BigLeap() {
        this.p = p;
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "genesis:leap";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return big_leap_tick;
    }
}
