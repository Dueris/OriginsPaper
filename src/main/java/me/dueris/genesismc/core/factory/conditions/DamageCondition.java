package me.dueris.genesismc.core.factory.conditions;

import me.dueris.genesismc.core.factory.powers.armour.RestrictArmor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class DamageCondition {
    public static boolean checkDamageCondition(Player p, OriginContainer origin, String powerfile, EntityDamageEvent e){

                String type = origin.getPowerFileFromType(powerfile).getDamageCondition().get("type").toString();

                if (type.equalsIgnoreCase("origins:amount")) {
                    String comparison = origin.getPowerFileFromType(powerfile).getDamageCondition().get("comparison").toString();
                    Long compare_to = (Long) origin.getPowerFileFromType(powerfile).getDamageCondition().get("compare_to");

                    if (RestrictArmor.compareValues(e.getDamage(), comparison, compare_to)) {
                        return true;
                        
                    }

                }

                if (type.equalsIgnoreCase("origins:attacker")) {
                    String entity_con_type = origin.getPowerFileFromType(powerfile).getEntityConditionFromDamageCondition().get("type").toString();
                    String entity_type = origin.getPowerFileFromType(powerfile).getEntityConditionFromDamageCondition().get("entity_type").toString();
                    //tell dueris to make entity conditions
                }

                if (type.equalsIgnoreCase("origins:bypasses_armor")) {
                    if (e.getCause().equals(DamageCause.CUSTOM)) {
                        return true;
                        
                    }
                    if (e.getCause().equals(DamageCause.FIRE)) {
                        return true;
                        
                    }
                    if (e.getCause().equals(DamageCause.CRAMMING)) {
                        return true;
                        
                    }
                    if (e.getCause().equals(DamageCause.KILL)) {
                        return true;
                        
                    }
                    if (e.getCause().equals(DamageCause.MAGIC)) {
                        return true;
                        
                    }
                    if (e.getCause().equals(DamageCause.POISON)) {
                        return true;
                        
                    }
                    if (e.getCause().equals(DamageCause.VOID)) {
                        return true;
                        
                    }
                }

                if (type.equalsIgnoreCase("origins:explosive")) {
                    if (e.getCause().equals(DamageCause.BLOCK_EXPLOSION) || e.getCause().equals(DamageCause.ENTITY_EXPLOSION)) {
                        return true;
                        
                    }
                }

                if (type.equalsIgnoreCase("origins:fire")) {
                    if (e.getCause().equals(DamageCause.FIRE)) {
                        return true;
                        
                    }
                }

                if (type.equalsIgnoreCase("origins:from_falling")) {
                    if (e.getCause().equals(DamageCause.FALL)) {
                        return true;
                        
                    }
                }

                if (type.equalsIgnoreCase("origins:in_tag")) {
                    String tag = origin.getPowerFileFromType(powerfile).getDamageCondition().get("tag").toString();
                    //need to parse tag folder
                }

                if (type.equalsIgnoreCase("origins:name")) {
                    String name = origin.getPowerFileFromType(powerfile).getDamageCondition().get("name").toString();

                    if(name.equalsIgnoreCase("anvil")){
                        if(e.getCause().equals(DamageCause.FALLING_BLOCK)){
                            FallingBlock fallingBlock = getFallingBlockDamager(e);
                            if(fallingBlock != null){
                                if(fallingBlock.getMaterial().toString().contains("ANVIL")){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("anvil.player")){
                        if(e.getCause().equals(DamageCause.FALLING_BLOCK)){
                            FallingBlock fallingBlock = getFallingBlockDamager(e);
                            if(fallingBlock != null){
                                if(fallingBlock.getMaterial().toString().contains("ANVIL")){
                                    if(e instanceof EntityDamageByEntityEvent){
                                        EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                        if(ev.getDamager() instanceof Entity){
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("arrow")){
                        if (e.getEntity() instanceof Player && e.getCause() == DamageCause.PROJECTILE) {
                            Player player = (Player) e.getEntity();
                            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                            if (ev.getDamager() instanceof Projectile) {
                                Projectile projectile = (Projectile) ev.getDamager();
                                if (projectile.getShooter() instanceof Player || projectile.getShooter() instanceof Mob) {
                                    return true;
                                    
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("arrow.item")){
                        if (e.getEntity() instanceof Player && e.getCause() == DamageCause.PROJECTILE) {
                            Player player = (Player) e.getEntity();
                            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                            if (ev.getDamager() instanceof Projectile) {
                                Projectile projectile = (Projectile) ev.getDamager();
                                if (projectile.getShooter() instanceof Player || projectile.getShooter() instanceof Mob) {
                                    if(!((LivingEntity) projectile.getShooter()).getActiveItem().getItemMeta().getDisplayName().equalsIgnoreCase("Bow")){
                                        return true;
                                        
                                    }
                                    if(!((LivingEntity) projectile.getShooter()).getActiveItem().getItemMeta().getDisplayName().equalsIgnoreCase("Crossbow")){
                                        return true;
                                        
                                    }
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("bedRespawnPoint")){
                        if(e.getCause().equals(DamageCause.BLOCK_EXPLOSION)){
                            if (isBedExplosionRestrictedDimension(p)) {
                                return true;
                                
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("cactus")){
                        if(e instanceof EntityDamageByBlockEvent){
                            EntityDamageByBlockEvent eb = (EntityDamageByBlockEvent) e;
                            if(eb.getDamager().getType().equals(Material.CACTUS)){
                                return true;
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("cactus.player")){
                        if(e instanceof EntityDamageByBlockEvent){
                            EntityDamageByBlockEvent eb = (EntityDamageByBlockEvent) e;
                            if(eb.getDamager().getType().equals(Material.CACTUS)){
                                if(e instanceof EntityDamageByEntityEvent){
                                    EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                    if(ev.getDamager() instanceof Entity){
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("cramming")){
                        if(e.getCause().equals(DamageCause.CRAMMING)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("cramming.player")){
                        if(e.getCause().equals(DamageCause.CRAMMING)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof Entity){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("dragonBreath")){
                        if(e.getCause().equals(DamageCause.DRAGON_BREATH)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("dragonBreath.player")){
                        if(e.getCause().equals(DamageCause.DRAGON_BREATH)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof Entity){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("drown")){
                        if(e.getCause().equals(DamageCause.DROWNING)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("drown.player")){
                        if(e.getCause().equals(DamageCause.DROWNING)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof Entity){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("dryout")){
                        if(e.getCause().equals(DamageCause.DRYOUT)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("dryout.player")){
                        if(e.getCause().equals(DamageCause.DRYOUT)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof Entity){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("explosion")){
                        if(e.getCause().equals(DamageCause.ENTITY_EXPLOSION) || e.getCause().equals(DamageCause.BLOCK_EXPLOSION)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("explosion.player")){
                        if(e.getCause().equals(DamageCause.ENTITY_EXPLOSION) || e.getCause().equals(DamageCause.BLOCK_EXPLOSION)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof Entity){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("fall")){
                        if(e.getCause().equals(DamageCause.FALL)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("fall.player")){
                        if(e.getCause().equals(DamageCause.FALL)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof Entity){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("fallingBlock")){
                        if(e.getCause().equals(DamageCause.FALLING_BLOCK)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("fallingBlock.player")){
                        if(e.getCause().equals(DamageCause.FALLING_BLOCK)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof Entity){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("fallingStalactite")){
                        if(e instanceof EntityDamageByBlockEvent){
                            EntityDamageByBlockEvent eb = (EntityDamageByBlockEvent) e;
                            if(eb.getDamager().getType().equals(Material.POINTED_DRIPSTONE)){
                                return true;
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("fallingStalactite.player")){
                            if(e instanceof EntityDamageByBlockEvent){
                                EntityDamageByBlockEvent eb = (EntityDamageByBlockEvent) e;
                                if(eb.getDamager().getType().equals(Material.POINTED_DRIPSTONE)){
                                    if(e instanceof EntityDamageByEntityEvent){
                                        EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                        if(ev.getDamager() instanceof Entity){
                                            return true;
                                        }
                                    }
                                }
                            }
                    }
                    if(name.equalsIgnoreCase("fireball")){
                        if(e instanceof EntityDamageByEntityEvent){
                            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                            if(ev.getDamager() instanceof Fireball){
                                return true;
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("fireball.player")){
                        if(e instanceof EntityDamageByEntityEvent){
                            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                            if(ev.getDamager() instanceof Fireball){
                                if(e instanceof EntityDamageByEntityEvent){
                                    if(ev.getDamager() instanceof Entity){
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    if(name.contains("fireworks")){
                        if(e instanceof EntityDamageByEntityEvent){
                            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                            if(ev.getDamager() instanceof Firework){
                                return true;
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("flyIntoWall")){
                        if(e.getCause().equals(DamageCause.FLY_INTO_WALL)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("flyIntoWall.player")){
                        if(e.getCause().equals(DamageCause.FLY_INTO_WALL)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof Entity){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("freeze")){
                        if(e.getCause().equals(DamageCause.FREEZE)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("freeze.player")){
                        if(e.getCause().equals(DamageCause.FREEZE)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof Entity){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("generic")){
                        if(e.getCause().equals(DamageCause.CUSTOM)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("generic.player")){
                        if(e.getCause().equals(DamageCause.CUSTOM)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof Entity){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("hotFloor")){
                        if(e.getCause().equals(DamageCause.FIRE)){
                            if(p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.MAGMA_BLOCK)){
                                return true;
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("hotFloor.player")){
                        if(e.getCause().equals(DamageCause.FIRE)){
                            if(p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.MAGMA_BLOCK)){
                                if(e instanceof EntityDamageByEntityEvent){
                                    EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                    if(ev.getDamager() instanceof Entity){
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    if(name.contains("indirectMagic")){
                        if(e.getCause().equals(DamageCause.MAGIC)){
                            return true;
                        }
                    }
                    if(name.contains("inFire") || name.equalsIgnoreCase("onFire")){
                        if(e.getCause().equals(DamageCause.FIRE)){
                            return true;
                        }
                        if(e.getCause().equals(DamageCause.FIRE_TICK)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("inWall")){
                        if(e.getCause().equals(DamageCause.SUFFOCATION)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("inWall.player")){
                        if(e.getCause().equals(DamageCause.SUFFOCATION)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof Entity){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("lava")){
                        if(e.getCause().equals(DamageCause.LAVA)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("lava.player")){
                        if(e.getCause().equals(DamageCause.SUFFOCATION)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof Entity){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("lightningBolt")){
                        if(e.getCause().equals(DamageCause.LIGHTNING)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("lightningBolt.player")){
                        if(e.getCause().equals(DamageCause.LIGHTNING)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof Entity){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("magic")){
                        if(e.getCause().equals(DamageCause.MAGIC)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("magic.player")){
                        if (e.getCause().equals(DamageCause.MAGIC)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof Entity){
                                    return true;
                                }
                            }
                        }
                    }
                    if (name.equalsIgnoreCase("mob")){
                        if(e.getCause().equals(DamageCause.ENTITY_ATTACK)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("outOfWorld")){
                        if(e.getCause().equals(DamageCause.WORLD_BORDER)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("outOfWorld.player")){
                        if(name.equalsIgnoreCase("outOfWorld")){
                            if(e.getCause().equals(DamageCause.WORLD_BORDER)){
                                if(e instanceof EntityDamageByEntityEvent){
                                    EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                    if(ev.getDamager() instanceof Entity){
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("player")){
                        if(e instanceof EntityDamageByEntityEvent){
                            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                            if(ev.getDamager() instanceof Player){
                                return true;
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("player.item")){
                        if(e instanceof EntityDamageByEntityEvent){
                            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                            if(ev.getDamager() instanceof Player){
                                return true;
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("sonic_boom")){
                        if(e.getCause().equals(DamageCause.SONIC_BOOM)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("sonic_boom.player")){
                        if(e.getCause().equals(DamageCause.SONIC_BOOM)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof Entity){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("stalagmite")){
                        if(e instanceof EntityDamageByBlockEvent){
                            EntityDamageByBlockEvent eb = (EntityDamageByBlockEvent) e;
                            if(eb.getDamager().getType().equals(Material.POINTED_DRIPSTONE)){
                                if(e.getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.POINTED_DRIPSTONE)){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("stalagmite.player")){
                        if(e instanceof EntityDamageByBlockEvent){
                            EntityDamageByBlockEvent eb = (EntityDamageByBlockEvent) e;
                            if(eb.getDamager().getType().equals(Material.POINTED_DRIPSTONE)){
                                if(e.getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.POINTED_DRIPSTONE)){
                                    if(e instanceof EntityDamageByEntityEvent){
                                        EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                        if(ev.getDamager() instanceof Entity){
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("starve")){
                        if(e.getCause().equals(DamageCause.STARVATION)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("starve.player")){
                        if(e.getCause().equals(DamageCause.STARVATION)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof Entity){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("sting")){
                        if(e instanceof EntityDamageByEntityEvent){
                            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                            if(ev.getDamager() instanceof Bee){
                                return true;
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("sting.player")){
                        if(e instanceof EntityDamageByEntityEvent){
                            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                            if(ev.getDamager() instanceof Bee){
                                if(e instanceof EntityDamageByEntityEvent){
                                    if(ev.getDamager() instanceof Entity){
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("sweetBerryBush")){
                        if(e instanceof EntityDamageByBlockEvent){
                            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                            if(ev.getDamager().getType().equals(Material.SWEET_BERRY_BUSH)){
                                return true;
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("sweetBerryBush.player")){
                        if(e instanceof EntityDamageByBlockEvent){
                            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                            if(ev.getDamager().getType().equals(Material.SWEET_BERRY_BUSH)){
                                if(e instanceof EntityDamageByEntityEvent){
                                    if(ev.getDamager() instanceof Entity){
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("thorns")){
                        if(e.getCause().equals(DamageCause.THORNS)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("thorns.player")){
                        if(e.getCause().equals(DamageCause.THORNS)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof Entity){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("trident")){
                        if(e.getCause().equals(DamageCause.PROJECTILE)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof Trident){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("wither")){
                        if(e.getCause().equals(DamageCause.WITHER)){
                            return true;
                        }
                    }
                    if(name.equalsIgnoreCase("wither.player")){
                        if(e.getCause().equals(DamageCause.WITHER)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof Entity){
                                    return true;
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("witherSkull")){
                        if(e.getCause().equals(DamageCause.ENTITY_EXPLOSION)){
                            if(e instanceof EntityDamageByEntityEvent){
                                EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                                if(ev.getDamager() instanceof WitherSkull){
                                    return true;
                                }
                            }
                        }
                    }
                }

                if (type.equalsIgnoreCase("origins:out_of_world")) {
                    if(e.getCause().equals(DamageCause.VOID)){
                        return true;
                    }
                }

                if (type.equalsIgnoreCase("origins:projectile")) {
                    if(e.getCause().equals(DamageCause.PROJECTILE)){
                        return true;
                    }
                }

                if (type.equalsIgnoreCase("origins:unblockable")) {
                    if(e.getCause().equals(DamageCause.FALL)){
                        return true;
                    }
                    if(e.getCause().equals(DamageCause.FALLING_BLOCK)){

                    }
                    if(e.getCause().equals(DamageCause.MAGIC)){
                        return true;
                    }
                    if(e.getCause().equals(DamageCause.LAVA)){
                        return true;
                    }
                    if(e.getCause().equals(DamageCause.DROWNING)){
                        return true;
                    }
                    if(e.getCause().equals(DamageCause.SUFFOCATION)){
                        return true;
                    }
                    if(e.getCause().equals(DamageCause.VOID)){
                        return true;
                    }
                    if(e.getCause().equals(DamageCause.FIRE)){
                        return true;
                    }
                    if(e.getCause().equals(DamageCause.POISON)){
                        return true;
                    }
                    if(e.getCause().equals(DamageCause.WITHER)){
                        return true;
                    }
                }
        return false;
    }

    private static FallingBlock getFallingBlockDamager(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) event;
            if (damageByEntityEvent.getDamager() instanceof FallingBlock) {
                return (FallingBlock) damageByEntityEvent.getDamager();
            }
        }
        return null;
    }

    private static boolean isBedExplosionRestrictedDimension(Player player) {
        String worldName = player.getWorld().getName().toLowerCase();
        return worldName.contains("end") || worldName.contains("nether");
    }

}
