package me.dueris.genesismc.core.factory.conditions;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.factory.powers.armour.RestrictArmor;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageCondition {
    public static boolean checkDamageCondition(Player p, OriginContainer origin, String powerfile, EntityDamageEvent e){

                String type = origin.getPowerFileFromType(powerfile).getDamageCondition().get("type").toString();

                if (type.equalsIgnoreCase("origins:amount")) {
                    String comparison = origin.getPowerFileFromType(powerfile).getDamageCondition().get("comparison").toString();
                    Long compare_to = (Long) origin.getPowerFileFromType(powerfile).getDamageCondition().get("compare_to");

                    if (RestrictArmor.compareValues(e.getDamage(), comparison, compare_to)) {
                        e.setCancelled(true);
                        e.setDamage(0);
                    }

                }

                if (type.equalsIgnoreCase("origins:attacker")) {
                    String entity_con_type = origin.getPowerFileFromType(powerfile).getEntityConditionFromDamageCondition().get("type").toString();
                    String entity_type = origin.getPowerFileFromType(powerfile).getEntityConditionFromDamageCondition().get("entity_type").toString();
                    //tell dueris to make entity conditions
                }

                if (type.equalsIgnoreCase("origins:bypasses_armor")) {
                    if (e.getCause().equals(EntityDamageEvent.DamageCause.CUSTOM)) {
                        e.setCancelled(true);
                        e.setDamage(0);
                    }
                    if (e.getCause().equals(EntityDamageEvent.DamageCause.FIRE)) {
                        e.setCancelled(true);
                        e.setDamage(0);
                    }
                    if (e.getCause().equals(EntityDamageEvent.DamageCause.CRAMMING)) {
                        e.setCancelled(true);
                        e.setDamage(0);
                    }
                    if (e.getCause().equals(EntityDamageEvent.DamageCause.KILL)) {
                        e.setCancelled(true);
                        e.setDamage(0);
                    }
                    if (e.getCause().equals(EntityDamageEvent.DamageCause.MAGIC)) {
                        e.setCancelled(true);
                        e.setDamage(0);
                    }
                    if (e.getCause().equals(EntityDamageEvent.DamageCause.POISON)) {
                        e.setCancelled(true);
                        e.setDamage(0);
                    }
                    if (e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                        e.setCancelled(true);
                        e.setDamage(0);
                    }
                }

                if (type.equalsIgnoreCase("origins:explosive")) {
                    if (e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) || e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
                        e.setCancelled(true);
                        e.setDamage(0);
                    }
                }

                if (type.equalsIgnoreCase("origins:fire")) {
                    if (e.getCause().equals(EntityDamageEvent.DamageCause.FIRE)) {
                        e.setCancelled(true);
                        e.setDamage(0);
                    }
                }

                if (type.equalsIgnoreCase("origins:from_falling")) {
                    if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                        e.setCancelled(true);
                        e.setDamage(0);
                    }
                }

                if (type.equalsIgnoreCase("origins:in_tag")) {
                    String tag = origin.getPowerFileFromType(powerfile).getDamageCondition().get("tag").toString();
                    //need to parse tag folder
                }

                if (type.equalsIgnoreCase("origins:name")) {
                    String name = origin.getPowerFileFromType(powerfile).getDamageCondition().get("name").toString();

                    if(name.equalsIgnoreCase("anvil")){
                        if(e.getCause().equals(EntityDamageEvent.DamageCause.FALLING_BLOCK)){
                            FallingBlock fallingBlock = getFallingBlockDamager(e);
                            if(fallingBlock != null){
                                if(fallingBlock.getMaterial().toString().contains("ANVIL")){
                                    e.setCancelled(true);
                                    e.setDamage(0);
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("anvil.player")){
                        if(e.getCause().equals(EntityDamageEvent.DamageCause.FALLING_BLOCK)){
                            FallingBlock fallingBlock = getFallingBlockDamager(e);
                            if(fallingBlock != null){
                                if(fallingBlock.getMaterial().toString().contains("ANVIL")){
                                    e.setCancelled(true);
                                    e.setDamage(0);
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("arrow")){
                        if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                            Player player = (Player) e.getEntity();
                            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                            if (ev.getDamager() instanceof Projectile) {
                                Projectile projectile = (Projectile) ev.getDamager();
                                if (projectile.getShooter() instanceof Player || projectile.getShooter() instanceof Mob) {
                                    e.setCancelled(true);
                                    e.setDamage(0);
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("arrow.item")){
                        if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                            Player player = (Player) e.getEntity();
                            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
                            if (ev.getDamager() instanceof Projectile) {
                                Projectile projectile = (Projectile) ev.getDamager();
                                if (projectile.getShooter() instanceof Player || projectile.getShooter() instanceof Mob) {
                                    if(!((LivingEntity) projectile.getShooter()).getActiveItem().getItemMeta().getDisplayName().equalsIgnoreCase("Bow")){
                                        e.setCancelled(true);
                                        e.setDamage(0);
                                    }
                                    if(!((LivingEntity) projectile.getShooter()).getActiveItem().getItemMeta().getDisplayName().equalsIgnoreCase("Crossbow")){
                                        e.setCancelled(true);
                                        e.setDamage(0);
                                    }
                                }
                            }
                        }
                    }
                    if(name.equalsIgnoreCase("bedRespawnPoint")){
                        if(e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)){
                            if (isBedExplosionRestrictedDimension(p)) {
                                e.setCancelled(true);
                                e.setDamage(0);
                            }
                        }
                    }

                }

                if (type.equalsIgnoreCase("origins:out_of_world")) {

                }

                if (type.equalsIgnoreCase("origins:projectile")) {

                }

                if (type.equalsIgnoreCase("origins:unblockable")) {

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
