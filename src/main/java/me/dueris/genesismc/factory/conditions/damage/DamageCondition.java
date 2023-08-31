package me.dueris.genesismc.factory.conditions.damage;

import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.conditions.entity.EntityCondition;
import me.dueris.genesismc.factory.powers.player.RestrictArmor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.Optional;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class DamageCondition extends Condition {

    public static Optional<Boolean> check(HashMap<String, Object> condition, Player p, EntityDamageEvent entityDamageEvent, String powerFile) {
        if (condition.get("type") == null) return Optional.empty();

        String type = condition.get("type").toString().toLowerCase();

        switch (type) {
            case "origins:amount" -> {
                String comparison = condition.get("comparison").toString();
                Long compare_to = (Long) condition.get("compare_to");

                if (RestrictArmor.compareValues(entityDamageEvent.getDamage(), comparison, compare_to)) {
                    return Optional.of(true);
                }
            }
            case "origins:attacker" -> {
                if (entityDamageEvent instanceof EntityDamageByEntityEvent event) {
                    return EntityCondition.check(condition, p, event.getDamager(), powerFile);
                }
            }
            case "origins:bypasses_armor" -> {
                switch (entityDamageEvent.getCause()) {
                    case CUSTOM, FIRE, CRAMMING, KILL, MAGIC, POISON, VOID -> {
                        return Optional.of(true);
                    }
                }
            }
            case "origins:explosive" -> {
                if (entityDamageEvent.getCause().equals(DamageCause.BLOCK_EXPLOSION) || entityDamageEvent.getCause().equals(DamageCause.ENTITY_EXPLOSION)) {
                    return Optional.of(true);
                }
            }
            case "origins:fire" -> {
                if (entityDamageEvent.getCause().equals(DamageCause.FIRE)) {
                    return Optional.of(true);
                }
            }
            case "origins:from_falling" -> {
                if (entityDamageEvent.getCause().equals(DamageCause.FALL)) {
                    return Optional.of(true);
                }
            }
            case "origins:in_tag" -> {
                String tag = condition.get("tag").toString();
                //TODO: need to parse tag folder
            }
            case "origins:name" -> {
                String name = condition.get("name").toString().toLowerCase();

                switch (name) {
                    case "anvil" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.FALLING_BLOCK)) {
                            FallingBlock fallingBlock = getFallingBlockDamager(entityDamageEvent);
                            if (fallingBlock != null) {
                                if (fallingBlock.getBlockData().getMaterial().equals(Material.ANVIL)
                                        || fallingBlock.getBlockData().getMaterial().equals(Material.CHIPPED_ANVIL)
                                        || fallingBlock.getBlockData().getMaterial().equals(Material.DAMAGED_ANVIL)) {
                                    return Optional.of(true);
                                }
                            }
                        }
                    }
                    case "anvil.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.FALLING_BLOCK)) {
                            FallingBlock fallingBlock = getFallingBlockDamager(entityDamageEvent);
                            if (fallingBlock != null) {
                                if (fallingBlock.getBlockData().getMaterial().equals(Material.ANVIL)
                                        || fallingBlock.getBlockData().getMaterial().equals(Material.CHIPPED_ANVIL)
                                        || fallingBlock.getBlockData().getMaterial().equals(Material.DAMAGED_ANVIL)) {
                                    if (entityDamageEvent instanceof EntityDamageByEntityEvent ev) {
                                        return Optional.of(true);
                                    }
                                    return Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent event
                                            && event.getDamager() instanceof Player);
                                }
                            }
                        }
                    }
                    case "arrow" -> {
                        if (entityDamageEvent.getEntity() instanceof Player && entityDamageEvent.getCause() == DamageCause.PROJECTILE) {
                            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) entityDamageEvent;
                            if (ev.getDamager() instanceof Projectile projectile) {
                                if (projectile.getShooter() instanceof Player || projectile.getShooter() instanceof Mob) {
                                    return Optional.of(true);

                                }
                            }
                        }
                    }
                    case "arrow.item" -> {
                        if (entityDamageEvent.getEntity() instanceof Player && entityDamageEvent.getCause() == DamageCause.PROJECTILE) {
                            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) entityDamageEvent;
                            if (ev.getDamager() instanceof Projectile projectile) {
                                if (projectile.getShooter() instanceof Player || projectile.getShooter() instanceof Mob) {
                                    if (!((LivingEntity) projectile.getShooter()).getActiveItem().getType().equals(Material.BOW)) {
                                        return Optional.of(true);

                                    }
                                    if (!((LivingEntity) projectile.getShooter()).getActiveItem().getType().equals(Material.CROSSBOW)) {
                                        return Optional.of(true);
                                    }
                                }
                            }
                        }
                    }
                    case "bedrespawnpoint" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.BLOCK_EXPLOSION)) {
                            if (isBedExplosionRestrictedDimension(p)) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "cactus" -> {
                        if (entityDamageEvent instanceof EntityDamageByBlockEvent eb) {
                            if (eb.getDamager() != null & eb.getDamager().getType().equals(Material.CACTUS)) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "cactus.player" -> {
                        if (entityDamageEvent instanceof EntityDamageByBlockEvent eb) {
                            if (eb.getDamager() != null && eb.getDamager().getType().equals(Material.CACTUS)) {
                                if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                        && damageByEntityEvent.getDamager() instanceof Player) {
                                    return Optional.of(true);
                                }
                            }
                        }
                    }
                    case "cramming" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.CRAMMING)) {
                            return Optional.of(true);
                        }
                    }
                    case "cramming.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.CRAMMING)) {
                            if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "dragonbreath" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.DRAGON_BREATH)) {
                            return Optional.of(true);
                        }
                    }
                    case "dragonbreath.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.DRAGON_BREATH)) {
                            if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "drown" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.DROWNING)) {
                            return Optional.of(true);
                        }
                    }
                    case "drown.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.DROWNING)) {
                            if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "dryout" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.DRYOUT)) {
                            return Optional.of(true);
                        }
                    }
                    case "dryout.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.DRYOUT)) {
                            if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "explosion" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.ENTITY_EXPLOSION) || entityDamageEvent.getCause().equals(DamageCause.BLOCK_EXPLOSION)) {
                            return Optional.of(true);
                        }
                    }
                    case "explosion.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.ENTITY_EXPLOSION) || entityDamageEvent.getCause().equals(DamageCause.BLOCK_EXPLOSION)) {
                            if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "fall" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.FALL)) {
                            return Optional.of(true);
                        }
                    }
                    case "fall.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.FALL)) {
                            if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "fallingblock" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.FALLING_BLOCK)) {
                            return Optional.of(true);
                        }
                    }
                    case "fallingblock.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.FALLING_BLOCK)) {
                            if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "fallingstalactite" -> {
                        if (entityDamageEvent instanceof EntityDamageByBlockEvent eb) {
                            if (eb.getDamager().getType().equals(Material.POINTED_DRIPSTONE)) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "fallingstalactite.player" -> {
                        if (entityDamageEvent instanceof EntityDamageByBlockEvent eb) {
                            if (eb.getDamager() != null && eb.getDamager().getType().equals(Material.POINTED_DRIPSTONE)) {
                                if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                        && damageByEntityEvent.getDamager() instanceof Player) {
                                    return Optional.of(true);
                                }
                            }
                        }
                    }
                    case "fireball" -> {
                        if (entityDamageEvent instanceof EntityDamageByEntityEvent ev) {
                            if (ev.getDamager() instanceof Fireball) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "fireball.player" -> {
                        if (entityDamageEvent instanceof EntityDamageByEntityEvent ev) {
                            if (ev.getDamager() instanceof Fireball) {
                                if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                        && damageByEntityEvent.getDamager() instanceof Player) {
                                    return Optional.of(true);
                                }
                            }
                        }
                    }
                    case "fireworks" -> {
                        if (entityDamageEvent instanceof EntityDamageByEntityEvent ev) {
                            if (ev.getDamager() instanceof Firework) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "flyintowall" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.FLY_INTO_WALL)) {
                            return Optional.of(true);
                        }
                    }
                    case "flyintowall.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.FLY_INTO_WALL)) {
                            if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "freeze" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.FREEZE)) {
                            return Optional.of(true);
                        }
                    }
                    case "freeze.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.FREEZE)) {
                            if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "generic" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.CUSTOM)) {
                            return Optional.of(true);
                        }
                    }
                    case "generic.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.CUSTOM)) {
                            if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "hotfloor" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.HOT_FLOOR)) {
                            return Optional.of(true);
                        }
                    }
                    case "hotfloor.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.FIRE)) {
                            if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.MAGMA_BLOCK)) {
                                if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                        && damageByEntityEvent.getDamager() instanceof Player) {
                                    return Optional.of(true);
                                }
                            }
                        }
                    }
                    case "indirectmagic", "magic" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.MAGIC)) {
                            return Optional.of(true);
                        }
                    }
                    case "infire", "onfire" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.FIRE) || entityDamageEvent.getCause().equals(DamageCause.FIRE_TICK)) {
                            return Optional.of(true);
                        }
                    }
                    case "inwall" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.SUFFOCATION)) {
                            return Optional.of(true);
                        }
                    }
                    case "inwall.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.SUFFOCATION)) {
                            if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "lava" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.LAVA)) {
                            return Optional.of(true);
                        }
                    }
                    case "lava.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.LAVA)) {
                            if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "lightningbolt" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.LIGHTNING)) {
                            return Optional.of(true);
                        }
                    }
                    case "lightningbolt.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.LIGHTNING)) {
                            if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "magic.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.MAGIC)) {
                            if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "mob" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.ENTITY_ATTACK)) {
                            return Optional.of(true);
                        }
                    }
                    case "outofworld" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.WORLD_BORDER)) {
                            return Optional.of(true);
                        }
                    }
                    case "outofworld.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.WORLD_BORDER)) {
                            if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "player", "player.item" -> {
                        if (entityDamageEvent instanceof EntityDamageByEntityEvent ev) {
                            if (ev.getDamager() instanceof Player) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "sonic_boom" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.SONIC_BOOM)) {
                            return Optional.of(true);
                        }
                    }
                    case "sonic_boom.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.SONIC_BOOM)) {
                            if (entityDamageEvent instanceof EntityDamageByEntityEvent ev) {
                                if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                        && damageByEntityEvent.getDamager() instanceof Player) {
                                    return Optional.of(true);
                                }
                            }
                        }
                    }
                    case "stalagmite" -> {
                        if (entityDamageEvent instanceof EntityDamageByBlockEvent eb) {
                            if (eb.getDamager() != null && eb.getDamager().getType().equals(Material.POINTED_DRIPSTONE)) {
                                if (entityDamageEvent.getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.POINTED_DRIPSTONE)) {
                                    return Optional.of(true);
                                }
                            }
                        }
                    }
                    case "stalagmite.player" -> {
                        if (entityDamageEvent instanceof EntityDamageByBlockEvent eb) {
                            if (eb.getDamager() != null && eb.getDamager().getType().equals(Material.POINTED_DRIPSTONE)) {
                                if (entityDamageEvent.getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.POINTED_DRIPSTONE)) {
                                    if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                            && damageByEntityEvent.getDamager() instanceof Player) {
                                        return Optional.of(true);
                                    }
                                }
                            }
                        }
                    }
                    case "starve" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.STARVATION)) {
                            return Optional.of(true);
                        }
                    }
                    case "starve.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.STARVATION)) {
                            if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "sting" -> {
                        if (entityDamageEvent instanceof EntityDamageByEntityEvent ev) {
                            if (ev.getDamager() instanceof Bee) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "sting.player" -> {
                        if (entityDamageEvent instanceof EntityDamageByEntityEvent ev) {
                            if (ev.getDamager() instanceof Bee) {
                                if (ev.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                        && damageByEntityEvent.getDamager() instanceof Player) {
                                    return Optional.of(true);
                                }
                            }
                        }
                    }
                    case "sweetberrybush" -> {
                        if (entityDamageEvent instanceof EntityDamageByBlockEvent ev) {
                            if (ev.getDamager() != null && ev.getDamager().getType().equals(Material.SWEET_BERRY_BUSH)) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "sweetberrybush.player" -> {
                        if (entityDamageEvent instanceof EntityDamageByBlockEvent ev) {
                            if (ev.getDamager() != null && ev.getDamager().getType().equals(Material.SWEET_BERRY_BUSH)) {
                                if (ev.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                        && damageByEntityEvent.getDamager() instanceof Player) {
                                    return Optional.of(true);
                                }
                            }
                        }
                    }
                    case "thorns" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.THORNS)) {
                            return Optional.of(true);
                        }
                    }
                    case "thorns.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.THORNS)) {
                            if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "trident" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.PROJECTILE)) {
                            if (entityDamageEvent instanceof EntityDamageByEntityEvent ev) {
                                if (ev.getDamager() instanceof Trident) {
                                    return Optional.of(true);
                                }
                            }
                        }
                    }
                    case "wither" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.WITHER)) {
                            return Optional.of(true);
                        }
                    }
                    case "wither.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.WITHER)) {
                            if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player) {
                                return Optional.of(true);
                            }
                        }
                    }
                    case "witherskull" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.ENTITY_EXPLOSION)) {
                            if (entityDamageEvent instanceof EntityDamageByEntityEvent ev) {
                                if (ev.getDamager() instanceof WitherSkull) {
                                    return Optional.of(true);
                                }
                            }
                        }
                    }
                }
            }
            case "origins:out_of_world" -> {
                if (entityDamageEvent.getCause().equals(DamageCause.VOID)) {
                    return Optional.of(true);
                }
            }
            case "origins:projectile" -> {
                if (entityDamageEvent.getCause().equals(DamageCause.PROJECTILE)) {
                    return Optional.of(true);
                }
            }
            case "origins:unblockable" -> {
                switch (entityDamageEvent.getCause()) {
                    case FALL, FALLING_BLOCK, MAGIC, LAVA, DROWNING, SUFFOCATION, VOID, FIRE, POISON, WITHER -> {
                        return Optional.of(true);
                    }
                }
            }
        }
        return Optional.of(false);
    }

    private static FallingBlock getFallingBlockDamager(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent damageByEntityEvent) {
            if (damageByEntityEvent.getDamager() instanceof FallingBlock) {
                return (FallingBlock) damageByEntityEvent.getDamager();
            }
        }
        return null;
    }

    private static boolean isBedExplosionRestrictedDimension(Player player) {
        return !player.getWorld().isBedWorks();
    }

}