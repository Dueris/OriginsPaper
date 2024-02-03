package me.dueris.genesismc.factory.conditions.damage;

import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.conditions.entity.EntityCondition;
import me.dueris.genesismc.factory.powers.player.RestrictArmor;
import org.bukkit.Fluid;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.Optional;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class DamageCondition implements Condition {

    private static FallingBlock getFallingBlockDamager(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent damageByEntityEvent) {
            if (damageByEntityEvent.getDamager() instanceof FallingBlock) {
                return (FallingBlock) damageByEntityEvent.getDamager();
            }
        }
        return null;
    }

    private static boolean isBedExplosionRestrictedDimension(Entity player) {
        return !player.getWorld().isBedWorks();
    }

    @Override
    public String condition_type() {
        return "DAMAGE_CONDITION";
    }

    @Override
    public Optional<Boolean> check(JSONObject condition, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent entityDamageEvent) {
        if (condition.isEmpty()) return Optional.empty();
        if (condition.get("type") == null) return Optional.empty();
        if (entityDamageEvent == null) return Optional.empty();
        boolean inverted = (boolean) condition.getOrDefault("inverted", false);
        String type = condition.get("type").toString().toLowerCase();
        switch (type) {
            case "apoli:amount" -> {
                String comparison = condition.get("comparison").toString();
                Long compare_to = (Long) condition.get("compare_to");

                return getResult(inverted, Optional.of(RestrictArmor.compareValues(entityDamageEvent.getDamage(), comparison, compare_to)));
            }
            case "apoli:attacker" -> {
                if (entityDamageEvent instanceof EntityDamageByEntityEvent event) {
                    EntityCondition entityCondition = ConditionExecutor.entityCondition;
                    return entityCondition.check(condition, actor, target, block, fluid, itemStack, entityDamageEvent);
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:bypasses_armor" -> {
                switch (entityDamageEvent.getCause()) {
                    case CUSTOM, FIRE, CRAMMING, KILL, MAGIC, POISON, VOID -> {
                        return getResult(inverted, Optional.of(true));
                    }
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:fire_and_lava" -> {
                return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.FIRE) || entityDamageEvent.getCause().equals(DamageCause.LAVA)));
            }
            case "apoli:explosive" -> {
                return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.BLOCK_EXPLOSION) || entityDamageEvent.getCause().equals(DamageCause.ENTITY_EXPLOSION)));
            }
            case "apoli:fire" -> {
                return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.FIRE)));
            }
            case "apoli:from_falling" -> {
                return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.FALL)));
            }
            case "apoli:in_tag" -> {
                String tag = condition.get("tag").toString();
                //TODO: need to parse tag folder
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:name" -> {
                String name = condition.get("name").toString().toLowerCase();

                switch (name) {
                    case "anvil" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.FALLING_BLOCK)) {
                            FallingBlock fallingBlock = getFallingBlockDamager(entityDamageEvent);
                            if (fallingBlock != null) {
                                return getResult(inverted, Optional.of(fallingBlock.getBlockData().getMaterial().equals(Material.ANVIL)
                                        || fallingBlock.getBlockData().getMaterial().equals(Material.CHIPPED_ANVIL)
                                        || fallingBlock.getBlockData().getMaterial().equals(Material.DAMAGED_ANVIL)));
                            }
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "anvil.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.FALLING_BLOCK)) {
                            FallingBlock fallingBlock = getFallingBlockDamager(entityDamageEvent);
                            if (fallingBlock != null) {
                                if (fallingBlock.getBlockData().getMaterial().equals(Material.ANVIL)
                                        || fallingBlock.getBlockData().getMaterial().equals(Material.CHIPPED_ANVIL)
                                        || fallingBlock.getBlockData().getMaterial().equals(Material.DAMAGED_ANVIL)) {
                                    if (entityDamageEvent instanceof EntityDamageByEntityEvent ev) {
                                        return getResult(inverted, Optional.of(true));
                                    }
                                    return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent event
                                            && event.getDamager() instanceof Player));
                                }
                            }
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "arrow" -> {
                        if (entityDamageEvent.getEntity() instanceof Player && entityDamageEvent.getCause() == DamageCause.PROJECTILE) {
                            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) entityDamageEvent;
                            if (ev.getDamager() instanceof Projectile projectile) {
                                return getResult(inverted, Optional.of(projectile.getShooter() instanceof Player || projectile.getShooter() instanceof Mob));
                            }
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "arrow.item" -> {
                        if (entityDamageEvent.getEntity() instanceof Player && entityDamageEvent.getCause() == DamageCause.PROJECTILE) {
                            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) entityDamageEvent;
                            if (ev.getDamager() instanceof Projectile projectile) {
                                if (projectile.getShooter() instanceof Player || projectile.getShooter() instanceof Mob) {
                                    if (!((LivingEntity) projectile.getShooter()).getActiveItem().getType().equals(Material.BOW)) {
                                        return getResult(inverted, Optional.of(true));

                                    } else if (!((LivingEntity) projectile.getShooter()).getActiveItem().getType().equals(Material.CROSSBOW)) {
                                        return getResult(inverted, Optional.of(true));
                                    }
                                }
                            }
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "bedrespawnpoint" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.BLOCK_EXPLOSION)) {
                            return getResult(inverted, Optional.of(isBedExplosionRestrictedDimension(actor)));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "cactus" -> {
                        if (entityDamageEvent instanceof EntityDamageByBlockEvent eb) {
                            return getResult(inverted, Optional.of(eb.getDamager() != null & eb.getDamager().getType().equals(Material.CACTUS)));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "cactus.player" -> {
                        if (entityDamageEvent instanceof EntityDamageByBlockEvent eb) {
                            if (eb.getDamager() != null && eb.getDamager().getType().equals(Material.CACTUS)) {
                                return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent && damageByEntityEvent.getDamager() instanceof Player));
                            }
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "cramming" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.CRAMMING)));
                    }
                    case "cramming.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.CRAMMING)) {
                            return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent && damageByEntityEvent.getDamager() instanceof Player));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "dragonbreath" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.DRAGON_BREATH)));
                    }
                    case "dragonbreath.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.DRAGON_BREATH)) {
                            return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent && damageByEntityEvent.getDamager() instanceof Player));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "drown" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.DROWNING)));
                    }
                    case "drown.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.DROWNING)) {
                            return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent && damageByEntityEvent.getDamager() instanceof Player));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "dryout" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.DRYOUT)));
                    }
                    case "dryout.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.DRYOUT)) {
                            return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent && damageByEntityEvent.getDamager() instanceof Player));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "explosion" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.ENTITY_EXPLOSION) || entityDamageEvent.getCause().equals(DamageCause.BLOCK_EXPLOSION)));
                    }
                    case "explosion.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.ENTITY_EXPLOSION) || entityDamageEvent.getCause().equals(DamageCause.BLOCK_EXPLOSION)) {
                            return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent && damageByEntityEvent.getDamager() instanceof Player));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "fall" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.FALL)));
                    }
                    case "fall.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.FALL)) {
                            return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "fallingblock" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.FALLING_BLOCK)));
                    }
                    case "fallingblock.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.FALLING_BLOCK)) {
                            return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "fallingstalactite" -> {
                        if (entityDamageEvent instanceof EntityDamageByBlockEvent eb) {
                            return getResult(inverted, Optional.of(eb.getDamager().getType().equals(Material.POINTED_DRIPSTONE)));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "fallingstalactite.player" -> {
                        if (entityDamageEvent instanceof EntityDamageByBlockEvent eb) {
                            if (eb.getDamager() != null && eb.getDamager().getType().equals(Material.POINTED_DRIPSTONE)) {
                                return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent && damageByEntityEvent.getDamager() instanceof Player));
                            }
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "fireball" -> {
                        if (entityDamageEvent instanceof EntityDamageByEntityEvent ev) {
                            return getResult(inverted, Optional.of(ev.getDamager() instanceof Fireball));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "fireball.player" -> {
                        if (entityDamageEvent instanceof EntityDamageByEntityEvent ev) {
                            if (ev.getDamager() instanceof Fireball) {
                                return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent && damageByEntityEvent.getDamager() instanceof Player));
                            }
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "fireworks" -> {
                        if (entityDamageEvent instanceof EntityDamageByEntityEvent ev) {
                            return getResult(inverted, Optional.of(ev.getDamager() instanceof Firework));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "flyintowall" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.FLY_INTO_WALL)));
                    }
                    case "flyintowall.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.FLY_INTO_WALL)) {
                            return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "freeze" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.FREEZE)));
                    }
                    case "freeze.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.FREEZE)) {
                            return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "generic" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.CUSTOM)));
                    }
                    case "generic.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.CUSTOM)) {
                            return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "hotfloor" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.HOT_FLOOR)));
                    }
                    case "hotfloor.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.FIRE)) {
                            if (actor.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.MAGMA_BLOCK)) {
                                return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                        && damageByEntityEvent.getDamager() instanceof Player));
                            }
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "indirectmagic", "magic" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.MAGIC)));
                    }
                    case "infire", "onfire" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.FIRE) || entityDamageEvent.getCause().equals(DamageCause.FIRE_TICK)));
                    }
                    case "inwall" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.SUFFOCATION)));
                    }
                    case "inwall.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.SUFFOCATION)) {
                            return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "lava" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.LAVA)));
                    }
                    case "lava.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.LAVA)) {
                            return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent && damageByEntityEvent.getDamager() instanceof Player));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "lightningbolt" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.LIGHTNING)));
                    }
                    case "lightningbolt.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.LIGHTNING)) {
                            return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "magic.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.MAGIC)) {
                            return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "mob" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.ENTITY_ATTACK)));
                    }
                    case "outofworld" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.WORLD_BORDER)));
                    }
                    case "outofworld.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.WORLD_BORDER)) {
                            return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent && damageByEntityEvent.getDamager() instanceof Player));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "player", "player.item" -> {
                        if (entityDamageEvent instanceof EntityDamageByEntityEvent ev) {
                            return getResult(inverted, Optional.of(ev.getDamager() instanceof Player));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "sonic_boom" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.SONIC_BOOM)));
                    }
                    case "sonic_boom.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.SONIC_BOOM)) {
                            if (entityDamageEvent instanceof EntityDamageByEntityEvent ev) {
                                return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent && damageByEntityEvent.getDamager() instanceof Player));
                            }
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "stalagmite" -> {
                        if (entityDamageEvent instanceof EntityDamageByBlockEvent eb) {
                            if (eb.getDamager() != null && eb.getDamager().getType().equals(Material.POINTED_DRIPSTONE)) {
                                return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.POINTED_DRIPSTONE)));
                            }
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "stalagmite.player" -> {
                        if (entityDamageEvent instanceof EntityDamageByBlockEvent eb) {
                            if (eb.getDamager() != null && eb.getDamager().getType().equals(Material.POINTED_DRIPSTONE)) {
                                if (entityDamageEvent.getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.POINTED_DRIPSTONE)) {
                                    if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                            && damageByEntityEvent.getDamager() instanceof Player) {
                                        return getResult(inverted, Optional.of(true));
                                    }
                                }
                            }
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "starve" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.STARVATION)) {
                            return getResult(inverted, Optional.of(true));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "starve.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.STARVATION)) {
                            if (entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player) {
                                return getResult(inverted, Optional.of(true));
                            }
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "sting" -> {
                        if (entityDamageEvent instanceof EntityDamageByEntityEvent ev) {
                            if (ev.getDamager() instanceof Bee) {
                                return getResult(inverted, Optional.of(true));
                            }
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "sting.player" -> {
                        if (entityDamageEvent instanceof EntityDamageByEntityEvent ev) {
                            if (ev.getDamager() instanceof Bee) {
                                return getResult(inverted, Optional.of(ev.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                        && damageByEntityEvent.getDamager() instanceof Player));
                            }
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "sweetberrybush" -> {
                        if (entityDamageEvent instanceof EntityDamageByBlockEvent ev) {
                            return getResult(inverted, Optional.of(ev.getDamager() != null && ev.getDamager().getType().equals(Material.SWEET_BERRY_BUSH)));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "sweetberrybush.player" -> {
                        if (entityDamageEvent instanceof EntityDamageByBlockEvent ev) {
                            if (ev.getDamager() != null && ev.getDamager().getType().equals(Material.SWEET_BERRY_BUSH)) {
                                if (ev.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                        && damageByEntityEvent.getDamager() instanceof Player) {
                                    return getResult(inverted, Optional.of(true));
                                }
                            }
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "thorns" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.THORNS)));
                    }
                    case "thorns.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.THORNS)) {
                            return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player));
                        }
                        return getResult(inverted, Optional.of(false));
                    }
                    case "trident" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.PROJECTILE) && entityDamageEvent instanceof EntityDamageByEntityEvent ev && ev.getDamager() instanceof Trident));
                    }
                    case "wither" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.WITHER)));
                    }
                    case "wither.player" -> {
                        if (entityDamageEvent.getCause().equals(DamageCause.WITHER)) {
                            return getResult(inverted, Optional.of(entityDamageEvent.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageByEntityEvent
                                    && damageByEntityEvent.getDamager() instanceof Player));
                        }
                    }
                    case "witherskull" -> {
                        return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.ENTITY_EXPLOSION) && entityDamageEvent instanceof EntityDamageByEntityEvent ev && ev.getDamager() instanceof WitherSkull));
                    }
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:type" -> {
                String s = NamespacedKey.minecraft(entityDamageEvent.getCause().toString().toLowerCase()).asString();
                return getResult(inverted, Optional.of(s.equals(condition.get("damage_type"))));
            }
            case "apoli:out_of_world" -> {
                return getResult(inverted, Optional.of(entityDamageEvent.getCause().equals(DamageCause.VOID)));
            }
            case "apoli:projectile" -> {
                if (entityDamageEvent.getCause().equals(DamageCause.PROJECTILE)) {
                    if (condition.containsKey("projectile_condition")) {
                        return getResult(inverted, ConditionExecutor.entityCondition.check((JSONObject) condition.get("projectile_condition"), ((EntityDamageByEntityEvent) entityDamageEvent).getDamager(), target, block, fluid, itemStack, entityDamageEvent));
                    } else {
                        return getResult(inverted, Optional.of(true));
                    }
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:unblockable" -> {
                switch (entityDamageEvent.getCause()) {
                    case FALL, FALLING_BLOCK, MAGIC, LAVA, DROWNING, SUFFOCATION, VOID, FIRE, POISON, WITHER -> {
                        return getResult(inverted, Optional.of(true));
                    }
                }
                return getResult(inverted, Optional.of(false));
            }
            default -> {
                return getResult(inverted, Optional.empty());
            }
        }
    }
}