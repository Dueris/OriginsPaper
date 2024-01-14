package me.dueris.genesismc.factory.powers.player;

import com.mojang.brigadier.StringReader;
import io.papermc.paper.util.MCUtil;
import me.dueris.genesismc.CooldownManager;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.events.KeybindTriggerEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.KeybindUtils;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.Utils;
import me.dueris.genesismc.utils.console.OriginConsoleSender;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
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
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.dueris.genesismc.utils.KeybindUtils.isKeyBeingPressed;

public class FireProjectile extends CraftPower implements Listener {

    public static ArrayList<Player> in_continuous = new ArrayList<>();
    public static ArrayList<Player> enderian_pearl = new ArrayList<>();
    public static ArrayList<Player> in_cooldown_patch = new ArrayList<>();
    private static final ArrayList<Player> doubleFirePatch = new ArrayList<>();

    public static void addCooldownPatch(Player p) {
        in_cooldown_patch.add(p);
        new BukkitRunnable() {
            @Override
            public void run() {
                in_cooldown_patch.remove(p);
            }
        }.runTaskLater(GenesisMC.getPlugin(), 5);
    }

    @EventHandler
    public void keybindCONTINUOUSDF(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
            if (fire_projectile.contains(p)) {
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (isKeyBeingPressed(e.getPlayer(), power.get("key").getOrDefault("key", "key.origins.primary_active").toString(), true)) {
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
            p.teleportAsync(e.getTo());
        }

    }

    @EventHandler
    public void keybindPress(KeybindTriggerEvent e) {
        Player p = e.getPlayer();
        ArrayList<Player> peopladf = new ArrayList<>();
        if (doubleFirePatch.contains(p)) return;
        if (!peopladf.contains(p)) {
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (power != null) {
                        if (fire_projectile.contains(p)) {
                            ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                            if (conditionExecutor.check("condition", "conditions", p, power, "origins:fire_projectile", p, null, null, null, p.getItemInHand(), null)) {
                                if (!CooldownManager.isPlayerInCooldown(p, power.get("key").getOrDefault("key", "key.origins.primary_active").toString())) {
                                    if (isKeyBeingPressed(e.getPlayer(), power.get("key").getOrDefault("key", "key.origins.primary_active").toString(), true)) {
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                Sound sound;
                                                int cooldown = power.getIntOrDefault("cooldown", 2);
                                                String tag = power.getStringOrDefault("tag", "{}");
                                                float divergence = power.getFloatOrDefault("divergence", 1.0f);
                                                float speed = power.getFloatOrDefault("speed", 1);
                                                int amt = power.getIntOrDefault("count", 1);
                                                int start_delay = power.getIntOrDefault("start_delay", 0);
                                                int interval = power.getIntOrDefault("interval", 1);

                                                // Introduce a slight random divergence
                                                divergence += (float) ((Math.random() - 0.5) * 0.05); // Adjust the 0.05 value to control the randomness level

                                                EntityType type;
                                                if (power.getStringOrDefault("entity_type", null).equalsIgnoreCase("origins:enderian_pearl")) {
                                                    type = EntityType.ENDER_PEARL;
                                                    enderian_pearl.add(p);
                                                } else {
                                                    type = EntityType.valueOf(power.getStringOrDefault("entity_type", null).split(":")[1].toUpperCase());
                                                    enderian_pearl.remove(p);
                                                }

                                                String key = (String) power.get("key").getOrDefault("key", "key.origins.primary_active");
                                                if (!CooldownManager.isPlayerInCooldown(p, key)) {
                                                    KeybindUtils.runKeyChangeTrigger(KeybindUtils.getTriggerFromOriginKey(p, key));

                                                    float finalDivergence1 = divergence;
                                                    final boolean[] thing = new boolean[1];
                                                    new BukkitRunnable() {
                                                        int shotsLeft = -amt;

                                                        @Override
                                                        public void run() {
                                                            if (!CooldownManager.isPlayerInCooldown(p, key)) {
                                                                if (shotsLeft >= 0) {
                                                                    if (power.get("key").getOrDefault("continuous", "false").toString().equalsIgnoreCase("false")) {
                                                                        KeybindUtils.runKeyChangeTriggerReturn(KeybindUtils.getTriggerFromOriginKey(p, key), p, key);
                                                                        CooldownManager.addCooldown(p, Utils.getNameOrTag(power), power.getType(), cooldown * 2, key);
                                                                        addCooldownPatch(p);
                                                                        peopladf.remove(p);
                                                                        if (KeybindUtils.getKeybindItem(key, p.getInventory()) != null) {
                                                                            ItemMeta met = KeybindUtils.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                                            met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                                            KeybindUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                                        }
                                                                        shotsLeft = 0;
                                                                        thing[0] = true;
                                                                        setActive(p, power.getTag(), false);
                                                                        this.cancel();
                                                                    } else {
                                                                        if (!in_continuous.contains(p)) {
                                                                            KeybindUtils.runKeyChangeTriggerReturn(KeybindUtils.getTriggerFromOriginKey(p, key), p, key);
                                                                            CooldownManager.addCooldown(p, Utils.getNameOrTag(power), power.getType(), cooldown * 2, key);
                                                                            addCooldownPatch(p);
                                                                            peopladf.remove(p);
                                                                            ItemMeta met = KeybindUtils.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                                            met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                                            KeybindUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                                            shotsLeft = 0;
                                                                            thing[0] = true;
                                                                            setActive(p, power.getTag(), false);
                                                                            this.cancel();
                                                                        } else {
                                                                            ItemMeta met = KeybindUtils.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                                            met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, true);
                                                                            setActive(p, power.getTag(), true);
                                                                            KeybindUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                                            shotsLeft = -amt;
                                                                        }
                                                                    }
                                                                    return;
                                                                }

                                                                addCooldownPatch(p);

                                                                if (type.getEntityClass() != null) {
                                                                    if (doubleFirePatch.contains(p)) return;
                                                                    Entity entityToSpawn = ((CraftEntity) p.getWorld().spawnEntity(p.getEyeLocation(), type)).getHandle();
                                                                    if (entityToSpawn.getBukkitEntity() instanceof Projectile proj) {
                                                                        proj.getScoreboardTags().add("fired_from_fp_power_by_" + p.getUniqueId());
                                                                        proj.setShooter(p);
                                                                        proj.setHasBeenShot(true);
                                                                        ((CraftEntity) proj).getHandle().saveWithoutId(new CompoundTag());
                                                                        Vector direction = p.getEyeLocation().getDirection();
                                                                        Vector dir = direction.clone().normalize().multiply(1);
                                                                        Vec3 startPos = MCUtil.toVec3(p.getEyeLocation());
                                                                        Vec3 endPos = startPos.add(dir.getX(), dir.getY(), dir.getZ());
                                                                        entityToSpawn.getBukkitEntity().getLocation().setDirection(direction);
                                                                        Location spawnLoc = CraftLocation.toBukkit(endPos);

                                                                        entityToSpawn.getBukkitEntity().teleport(spawnLoc);

                                                                        double yawRadians = Math.toRadians(p.getEyeLocation().getYaw() + finalDivergence1);

                                                                        double x = -Math.sin(yawRadians) * Math.cos(Math.toRadians(p.getEyeLocation().getPitch()));
                                                                        double y = -Math.sin(Math.toRadians(p.getEyeLocation().getPitch()));
                                                                        double z = Math.cos(yawRadians) * Math.cos(Math.toRadians(p.getEyeLocation().getPitch()));

                                                                        direction.setX(x);
                                                                        direction.setY(y);
                                                                        direction.setZ(z);
                                                                        Vector finalVeloc = direction.normalize().multiply(speed);

                                                                        entityToSpawn.getBukkitEntity().setVelocity(finalVeloc);
                                                                        CompoundTag mergedTag = entityToSpawn.saveWithoutId(new CompoundTag());
                                                                        String[] finalNbtTag = {""};
                                                                        if (power.get("tag") != null) {
                                                                            finalNbtTag[0] = mergedTag.merge(Utils.ParserUtils.parseJson(new StringReader(tag), CompoundTag.CODEC)).getAsString();
                                                                        } else {
                                                                            finalNbtTag[0] = mergedTag.getAsString();
                                                                        }

                                                                        Pattern pattern = Pattern.compile("b,Motion:\\[(.*?)\\],OnGround");
                                                                        Matcher matcher = pattern.matcher(finalNbtTag[0]);

                                                                        String previousSetMotion = "";
                                                                        if (matcher.find()) {
                                                                            previousSetMotion = matcher.group(1);
                                                                        }
                                                                        finalNbtTag[0].replace(previousSetMotion, finalVeloc.getX() + "," + finalVeloc.getY() + "," + finalVeloc.getZ());
                                                                        entityToSpawn.remove(RemovalReason.DISCARDED);
                                                                        final boolean returnToNormal = p.getWorld().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK);
                                                                        p.getWorld().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
                                                                        new BukkitRunnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                String cmd = "execute at {player} run summon {type} {loc} {nbt}"
                                                                                        .replace("{player}", p.getName())
                                                                                        .replace("{type}", type.key().asString())
                                                                                        .replace("{loc}", "^ ^1 ^")
                                                                                        .replace("{nbt}", finalNbtTag[0]);
                                                                                Bukkit.dispatchCommand(new OriginConsoleSender(), cmd);
                                                                                setActive(p, power.getTag(), true);
                                                                                new BukkitRunnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        p.getWorld().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, returnToNormal);
                                                                                    }
                                                                                }.runTaskLater(GenesisMC.getPlugin(), 1);
                                                                                doubleFirePatch.add(p);
                                                                                new BukkitRunnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        doubleFirePatch.remove(p);
                                                                                    }
                                                                                }.runTaskLater(GenesisMC.getPlugin(), 5);

                                                                                org.bukkit.entity.Entity gottenEntity = null;
                                                                                for (org.bukkit.entity.Entity nearbyEntity : p.getNearbyEntities(1, 2, 1)) {
                                                                                    if (nearbyEntity.getScoreboardTags().contains("fired_from_fp_power_by_" + p.getUniqueId())) {
                                                                                        gottenEntity = nearbyEntity;
                                                                                        break;
                                                                                    }
                                                                                }

                                                                                if (gottenEntity == null) return;

                                                                                org.bukkit.entity.Entity finalEntity = p.getWorld().spawnEntity(spawnLoc, type);
                                                                                if (finalEntity instanceof Projectile proj) {
                                                                                    proj.setShooter(p);
                                                                                }
                                                                                finalEntity.setCustomName(gottenEntity.getCustomName());
                                                                                finalEntity.setCustomNameVisible(gottenEntity.isCustomNameVisible());
                                                                                finalEntity.setFallDistance(gottenEntity.getFallDistance());
                                                                                finalEntity.setFireTicks(gottenEntity.getFireTicks());
                                                                                finalEntity.setFreezeTicks(gottenEntity.getFreezeTicks());
                                                                                finalEntity.setGlowing(gottenEntity.isGlowing());
                                                                                finalEntity.setGravity(gottenEntity.hasGravity());
                                                                                finalEntity.setInvulnerable(gottenEntity.isInvulnerable());
                                                                                finalEntity.setPassenger(gottenEntity.getPassenger());
                                                                                finalEntity.setOp(gottenEntity.isOp());
                                                                                finalEntity.setVelocity(gottenEntity.getVelocity());
                                                                                finalEntity.setSilent(gottenEntity.isSilent());
                                                                                if (((CraftEntity) gottenEntity).getHandle().getFirstPassenger() != null)
                                                                                    ((CraftEntity) gottenEntity).getHandle().getFirstPassenger().remove(RemovalReason.DISCARDED);
                                                                                ((CraftEntity) gottenEntity).getHandle().remove(RemovalReason.DISCARDED);

                                                                                peopladf.add(p);
                                                                            }
                                                                        }.runTaskLater(GenesisMC.getPlugin(), 1);
                                                                    }

                                                                    shotsLeft++; // Decrement the remaining shots
                                                                }
                                                            }
                                                        }
                                                    }.runTaskTimer(GenesisMC.getPlugin(), start_delay, interval);

                                                    if (thing[0]) {
                                                        thing[0] = false;
                                                        this.cancel();
                                                    }

                                                    if (power.get("key").getOrDefault("continuous", "false").toString().equalsIgnoreCase("false")) {
                                                        ItemMeta met = KeybindUtils.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                        met.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "contin"), PersistentDataType.BOOLEAN, false);
                                                        setActive(p, power.getTag(), false);
                                                        KeybindUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                        this.cancel();
                                                    } else {
                                                        if (isKeyBeingPressed(e.getPlayer(), power.get("key").getOrDefault("key", "key.origins.primary_active").toString(), true)) {
                                                            ItemMeta met = KeybindUtils.getKeybindItem(key, p.getInventory()).getItemMeta();
                                                            setActive(p, power.getTag(), false);
                                                            KeybindUtils.getKeybindItem(key, p.getInventory()).setItemMeta(met);
                                                            this.cancel();
                                                        }
                                                    }
                                                }
                                            }
                                        }.runTaskLater(GenesisMC.getPlugin(), 1);
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
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
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
}
