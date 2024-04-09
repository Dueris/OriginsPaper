package me.dueris.genesismc.factory.actions.types;

import me.dueris.calio.registry.Registerable;
import me.dueris.calio.registry.Registrar;
import me.dueris.calio.util.MiscUtils;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.*;
import me.dueris.genesismc.factory.powers.apoli.AttributeHandler;
import me.dueris.genesismc.factory.powers.apoli.Resource;
import me.dueris.genesismc.factory.powers.apoli.StackingStatusEffect;
import me.dueris.genesismc.factory.powers.apoli.Toggle;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.RaycastUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.console.OriginConsoleSender;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import org.bukkit.*;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftLocation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.joml.Vector3f;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import static me.dueris.genesismc.factory.actions.Actions.*;
import static me.dueris.genesismc.util.KeybindingUtils.addItems;

public class EntityActions {

    public void register() {
        register(new ActionFactory(GenesisMC.apoliIdentifier("modify_inventory"), (action, entity) -> {
            if (entity instanceof Player player) {
                if (action.containsKey("slot")) {
                    try {
                        if (player.getInventory().getItem(getSlotFromString(action.get("slot").toString())) == null)
                            return;
                        executeItem(player.getInventory().getItem(getSlotFromString(action.get("slot").toString())), action);
                    } catch (Exception e) {
                        // Ignore Exception
                    }
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("change_resource"), (action, entity) -> {
            if (resourceChangeTimeout.containsKey(entity)) return;
            String resource = action.get("resource").toString();
            if (Resource.getResource(entity, resource) == null) return;
            if (Resource.getResource(entity, resource).right() == null) return;
            if (Resource.getResource(entity, resource).left() == null) return;
            int change = Integer.parseInt(action.get("change").toString());
            double finalChange = 1.0 / Resource.getResource(entity, resource).right();
            BossBar bossBar = Resource.getResource(entity, resource).left();
            double toRemove = finalChange * change;
            double newP = Utils.getOperationMappingsDouble().get(action.getOrDefault("operation", "add").toString()).apply(bossBar.getProgress(), toRemove);
            if (newP > 1.0) {
                newP = 1.0;
            } else if (newP < 0) {
                newP = 0.0;
            }
            bossBar.setProgress(newP);
            if (bossBar.getProgress() == 1.0) {
                Actions.executeEntity(entity, CraftApoli.getPowerFromTag(resource).getAction("max_action"));
            } else if (bossBar.getProgress() == 0.0) {
                Actions.executeEntity(entity, CraftApoli.getPowerFromTag(resource).getAction("min_action"));
            }
            bossBar.addPlayer((Player) entity);
            bossBar.setVisible(true);
            resourceChangeTimeout.put(entity, true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    resourceChangeTimeout.remove(entity);
                }
            }.runTaskLater(GenesisMC.getPlugin(), 1);
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("modify_resource"), (action, entity) -> {
            if (resourceChangeTimeout.containsKey(entity)) return;
            String resource = action.get("resource").toString();
            if (Resource.getResource(entity, resource) == null) return;
            if (Resource.getResource(entity, resource).right() == null) return;
            if (Resource.getResource(entity, resource).left() == null) return;
            JSONObject modifier = (JSONObject) action.get("modifier");
            int change = Integer.parseInt(modifier.get("value").toString());
            double finalChange = 1.0 / Resource.getResource(entity, resource).right();
            BossBar bossBar = Resource.getResource(entity, resource).left();
            double toRemove = finalChange * change;
            double newP = Utils.getOperationMappingsDouble().get(modifier.getOrDefault("operation", "add").toString()).apply(bossBar.getProgress(), toRemove);
            if (newP > 1.0) {
                newP = 1.0;
            } else if (newP < 0) {
                newP = 0.0;
            }
            bossBar.setProgress(newP);
            if (bossBar.getProgress() == 1.0) {
                Actions.executeEntity(entity, CraftApoli.getPowerFromTag(resource).getAction("max_action"));
            } else if (bossBar.getProgress() == 0.0) {
                Actions.executeEntity(entity, CraftApoli.getPowerFromTag(resource).getAction("min_action"));
            }
            bossBar.addPlayer((Player) entity);
            bossBar.setVisible(true);
            resourceChangeTimeout.put(entity, true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    resourceChangeTimeout.remove(entity);
                }
            }.runTaskLater(GenesisMC.getPlugin(), 1);
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("set_on_fire"), (action, entity) -> entity.setFireTicks(Integer.parseInt(action.get("duration").toString()) * 20)));
        register(new ActionFactory(GenesisMC.apoliIdentifier("spawn_entity"), (action, entity) -> {
            OriginConsoleSender originConsoleSender = new OriginConsoleSender();
            originConsoleSender.setOp(true);
            RaycastUtils.executeCommandAtHit(((CraftEntity) entity).getHandle(), CraftLocation.toVec3D(entity.getLocation()), "summon $1 %1 %2 %3 $2"
                .replace("$1", action.get("entity_type").toString())
                .replace("$2", action.getOrDefault("tag", "{}").toString())
                .replace("%1", String.valueOf(entity.getLocation().getX()))
                .replace("%2", String.valueOf(entity.getLocation().getY()))
                .replace("%3", String.valueOf(entity.getLocation().getZ()))
            );
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("spawn_particles"), (action, entity) -> {
            Particle particle = Particle.valueOf(((JSONObject) action.get("particle")).getOrDefault("type", null).toString().split(":")[1].toUpperCase());
            int count = Integer.parseInt(String.valueOf(action.getOrDefault("count", 1)));
            float offset_y_no_vector = Float.parseFloat(String.valueOf(action.getOrDefault("offset_y", 1.0)));
            float offset_x = 0.25f;
            float offset_y = 0.50f;
            float offset_z = 0.25f;
            if (action.get("spread") != null) {
                JSONObject spread = (JSONObject) action.get("spread");
                if (spread.get("y") != null) {
                    offset_y = Float.parseFloat(String.valueOf(spread.get("y")));
                }

                if (spread.get("x") != null) {
                    offset_x = Float.parseFloat(String.valueOf(spread.get("x")));
                }

                if (spread.get("z") != null) {
                    offset_z = Float.parseFloat(String.valueOf(spread.get("z")));
                }
            }
            entity.getWorld().spawnParticle(particle, new Location(entity.getWorld(), entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ()), count, offset_x, offset_y, offset_z, 0);
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("random_teleport"), (action, entity) -> {
            int spreadDistance = Math.round(Float.valueOf(action.getOrDefault("max_width", "8.0").toString()));
            int attempts = Integer.valueOf(action.getOrDefault("attempts", "1").toString());
            for (int i = 0; i < attempts; i++) {
                String cmd = "spreadplayers {xloc} {zloc} 1 {spreadDist} false {name}"
                    .replace("{xloc}", String.valueOf(entity.getLocation().getX()))
                    .replace("{zloc}", String.valueOf(entity.getLocation().getZ()))
                    .replace("{spreadDist}", String.valueOf(spreadDistance))
                    .replace("{name}", "@e[{data}]"
                        .replace("{data}", "x=" + entity.getLocation().getX() + ",y=" + entity.getLocation().getY() + ",z=" + entity.getLocation().getZ() + ",type=" + entity.getType().toString().toLowerCase() + ",x_rotation=" + entity.getLocation().getDirection().getX() + ",y_rotation=" + entity.getLocation().getDirection().getY())
                    );
                RaycastUtils.executeCommandAtHit(((CraftEntity) entity).getHandle(), CraftLocation.toVec3D(entity.getLocation()), cmd);
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("remove_power"), (action, entity) -> {
            if (entity instanceof Player p) {
                Power powerContainer = ((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(NamespacedKey.fromString(action.get("power").toString()));
                if (powerContainer != null) {
                    RaycastUtils.executeCommandAtHit(((CraftEntity) p).getHandle(), CraftLocation.toVec3D(p.getLocation()), "power remove {name} {identifier}".replace("{name}", p.getName()).replace("{identifier}", action.get("action").toString()));
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("spawn_effect_cloud"), (action, entity) -> spawnEffectCloud(entity, Float.valueOf(action.getOrDefault("radius", 3.0).toString()), Integer.valueOf(action.getOrDefault("wait_time", 10).toString()), new PotionEffect(StackingStatusEffect.getPotionEffectType(action.get("effect").toString()), 1, 1))));
        register(new ActionFactory(GenesisMC.apoliIdentifier("replace_inventory"), (action, entity) -> {
            if (entity instanceof Player player) {
                if (action.containsKey("slot")) {
                    try {
                        if (player.getInventory().getItem(getSlotFromString(action.get("slot").toString())) == null)
                            return;
                        JSONObject jsonObject = (JSONObject) action.get("stack");
                        player.getInventory().getItem(getSlotFromString(action.get("slot").toString())).setType(Material.valueOf(jsonObject.get("item").toString().split(":")[1].toUpperCase()));
                    } catch (Exception e) {
                        //silently fail
                    }
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("heal"), (action, entity) -> {
            if (entity instanceof LivingEntity li) {
                double healthFinal = li.getHealth() + Double.parseDouble(action.get("amount").toString());
                if (li.getHealth() >= 20) return;
                if (healthFinal > 20) {
                    li.setHealth(20);
                } else {
                    li.setHealth(healthFinal);
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("clear_effect"), (action, entity) -> {
            PotionEffectType potionEffectType = StackingStatusEffect.getPotionEffectType(action.get("effect").toString());
            if (entity instanceof Player player) {
                if (player.hasPotionEffect(potionEffectType)) {
                    player.removePotionEffect(potionEffectType);
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("exhaust"), (action, entity) -> {
            if (entity instanceof Player player) {
                player.setFoodLevel(player.getFoodLevel() - Math.round(Float.valueOf(action.get("amount").toString())));
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("explode"), (action, entity) -> {
            long explosionPower = 1L;
            if (action.get("power") instanceof Long lep) {
                explosionPower = lep;
            } else if (action.get("power") instanceof Double dep) {
                explosionPower = Math.round(dep);
            }
            String destruction_type = "break";
            boolean create_fire = false;
            ServerLevel level = ((CraftWorld) entity.getWorld()).getHandle();

            if (action.containsKey("destruction_type"))
                destruction_type = action.get("destruction_type").toString();
            if (action.containsKey("create_fire"))
                create_fire = Boolean.parseBoolean(action.get("create_fire").toString());

            Explosion explosion = new Explosion(
                level,
                ((CraftEntity) entity).getHandle(),
                level.damageSources().generic(),
                new ExplosionDamageCalculator(),
                entity.getLocation().getX(),
                entity.getLocation().getY(),
                entity.getLocation().getZ(),
                explosionPower,
                create_fire,
                DestructionType.parse(destruction_type).getNMS(),
                ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER,
                SoundEvents.GENERIC_EXPLODE
            );
            ExplosionMask.getExplosionMask(explosion, level).apply(action, true);
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("crafting_table"), (action, entity) -> {
            if (entity instanceof Player player) {
                Inventory inventory = Bukkit.createInventory(player, InventoryType.CRAFTING);
                player.openInventory(inventory);
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("ender_chest"), (action, entity) -> {
            if (entity instanceof Player player) {
                player.openInventory(player.getEnderChest());
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("equipped_item_action"), (action, entity) -> {
            if (entity instanceof Player player) {
                if (action.containsKey("equipment_slot")) {
                    try {
                        if (player.getInventory().getItem(getSlotFromString(action.get("equipment_slot").toString())) == null)
                            return;
                        executeItem(player.getInventory().getItem(getSlotFromString(action.get("equipment_slot").toString())), action);
                    } catch (Exception e) {
                        // Ignore Exception
                    }
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("dismount"), (action, entity) -> entity.getVehicle().removePassenger(entity)));
        register(new ActionFactory(GenesisMC.apoliIdentifier("feed"), (action, entity) -> {
            if (entity instanceof Player player) {
                if (player.getFoodLevel() == 20 || player.getFoodLevel() + Integer.parseInt(action.get("food").toString()) >= 20) {
                    player.setFoodLevel(20);
                } else {
                    player.setFoodLevel(player.getFoodLevel() + Integer.parseInt(action.get("food").toString()));
                }

                if (player.getSaturation() == 20 || player.getSaturation() + Float.parseFloat(action.get("saturation").toString()) >= 20) {
                    player.setSaturation(20);
                } else {
                    player.setSaturation(player.getSaturation() + Float.parseFloat(action.get("saturation").toString()));
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("fire_projectile"), (action, entity) -> {
            if (entity instanceof ProjectileSource) {
                float finalDivergence1 = Float.parseFloat(action.getOrDefault("divergence", 1.0).toString());
                float speed = Float.parseFloat(action.getOrDefault("speed", 1).toString());
                EntityType typeE;
                if (action.getOrDefault("entity_type", null).toString().equalsIgnoreCase("origins:enderian_pearl")) {
                    typeE = EntityType.ENDER_PEARL;
                } else {
                    typeE = EntityType.valueOf(action.getOrDefault("entity_type", null).toString().split(":")[1].toUpperCase());
                }
                Projectile projectile = (Projectile) entity.getWorld().spawnEntity(entity.getLocation(), typeE);
                projectile.setShooter((ProjectileSource) entity);

                Vector direction = entity.getLocation().getDirection();

                double yawRadians = Math.toRadians(entity.getLocation().getYaw() + finalDivergence1);

                double x = -Math.sin(yawRadians) * Math.cos(Math.toRadians(entity.getLocation().getPitch()));
                double y = -Math.sin(Math.toRadians(entity.getLocation().getPitch()));
                double z = Math.cos(yawRadians) * Math.cos(Math.toRadians(entity.getLocation().getPitch()));

                direction.setX(x);
                direction.setY(y);
                direction.setZ(z);

                projectile.setVelocity(direction.normalize().multiply(speed));
                projectile.setGlowing(true);
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("passanger_action"), (action, entity) -> {
            if (entity.getPassengers() == null || entity.getPassengers().isEmpty()) return;
            executeEntity(entity.getPassenger(), (JSONObject) action.get("action"));
            executeBiEntity(entity.getPassenger(), entity, (JSONObject) action.get("bientity_action"));
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("riding_action"), (action, entity) -> {
            if (entity.getVehicle() == null) return;
            if (action.containsKey("action")) {
                executeEntity(entity.getVehicle(), (JSONObject) action.get("action"));
            }
            if (action.containsKey("bientity_action")) {
                executeBiEntity(entity.getVehicle(), entity, (JSONObject) action.get("bientity_action"));
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("raycast"), (action, entity) -> RaycastUtils.action(action, ((CraftEntity) entity).getHandle())));
        register(new ActionFactory(GenesisMC.apoliIdentifier("extinguish"), (action, entity) -> entity.setFireTicks(0)));
        register(new ActionFactory(GenesisMC.apoliIdentifier("play_sound"), (action, entity) -> {
            Sound sound = MiscUtils.parseSound(action.get("sound").toString());
            Float volume = Float.parseFloat(action.getOrDefault("volume", 1.0).toString());
            Float pitch = Float.parseFloat(action.getOrDefault("pitch", 1.0).toString());
            entity.getWorld().playSound(entity, sound, volume, pitch);
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("gain_air"), (action, entity) -> {
            long amt = (long) action.get("value");
            if (entity instanceof Player p) {
                p.setRemainingAir(p.getRemainingAir() + Math.toIntExact(amt));
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("drop_inventory"), (action, entity) -> {
            if (entity instanceof Player player) {
                if (action.containsKey("slot")) {
                    try {
                        if (player.getInventory().getItem(getSlotFromString(action.get("slot").toString())) == null)
                            return;
                        executeItem(player.getInventory().getItem(getSlotFromString(action.get("slot").toString())), action);
                    } catch (Exception e) {
                        //fail noononooo
                    }
                } else {
                    ArrayList<String> ke = new ArrayList<>();
                    ke.add("key.origins.primary_active");
                    ke.add("key.origins.secondary_active");
                    for (ItemStack item : player.getInventory().getContents()) {
                        if (item == null) continue;
                        if (!item.containsEnchantment(Enchantment.ARROW_INFINITE) && !item.getType().equals(Material.GRAY_DYE)) {
                            player.getWorld().dropItemNaturally(player.getLocation(), item);
                        }
                    }
                    player.getInventory().clear();
                    addItems(player);
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("grant_advancement"), (action, entity) -> RaycastUtils.executeCommandAtHit(((CraftEntity) entity).getHandle(), CraftLocation.toVec3D(entity.getLocation()), "advancement grant $1 $2".replace("$1", entity.getName()).replace("$2", action.get("advacnement").toString()))));
        register(new ActionFactory(GenesisMC.apoliIdentifier("revoke_advancement"), (action, entity) -> RaycastUtils.executeCommandAtHit(((CraftEntity) entity).getHandle(), CraftLocation.toVec3D(entity.getLocation()), "advancement revoke $1 $2".replace("$1", entity.getName()).replace("$2", action.get("advacnement").toString()))));
        register(new ActionFactory(GenesisMC.apoliIdentifier("selector_action"), (action, entity) -> {
            if (action.get("bientity_condition") != null) {
                if (entity instanceof Player player) {
                    executeBiEntity(entity, player.getTargetEntity(AttributeHandler.Reach.getDefaultReach(player), false), (JSONObject) action.get("bientity_condition"));
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("give"), (action, entity) -> {
            int amt = 1;
            if (action.containsKey("amount")) {
                amt = Integer.parseInt(action.get("amount").toString());
            }

            if (action.containsKey("stack")) {
                JSONObject stackObject = (JSONObject) action.get("stack");
                String item = stackObject.get("item").toString();
                int amount = Integer.parseInt(String.valueOf(stackObject.getOrDefault("amount", 1)));

                ItemStack itemStack = new ItemStack(Material.valueOf(item.toUpperCase().split(":")[1]), amount);

                if (action.containsKey("item_action")) {
                    executeItem(itemStack, action);
                }
                if (entity instanceof Player player) {
                    player.getInventory().addItem(itemStack);
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("damage"), (action, entity) -> {
            if (entity instanceof Player P) {
                P.damage(Double.valueOf(action.get("amount").toString()));
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("add_velocity"), (action, entity) -> {
            Space space = Space.getSpace(action.getOrDefault("space", "world").toString());

            Vector3f vec = VectorGetter.getAsVector3f(action);
            net.minecraft.world.entity.Entity en = ((CraftLivingEntity) entity).getHandle();
            space.toGlobal(vec, en);
            if (Boolean.parseBoolean(action.getOrDefault("set", "false").toString())) {
                en.getBukkitEntity().getVelocity().add(new Vector(vec.x, vec.y, vec.z));
            } else {
                en.getBukkitEntity().setVelocity(new Vector(vec.x, vec.y, vec.z));
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("execute_command"), (action, entity) -> {
            String cmd = null;
            if (action.get("command").toString().startsWith("action") || action.get("command").toString().startsWith("/action"))
                return;
            if (action.get("command").toString().startsWith("/")) {
                cmd = action.get("command").toString().split("/")[1];
            } else {
                cmd = action.get("command").toString();
            }
            RaycastUtils.executeCommandAtHit(((CraftEntity) entity).getHandle(), CraftLocation.toVec3D(entity.getLocation()), cmd);
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("add_xp"), (action, entity) -> {
            int points = 0;
            int levels = 0;

            if (action.containsKey("points")) points = Integer.parseInt(action.get("points").toString());
            if (action.containsKey("levels")) levels = Integer.parseInt(action.get("levels").toString());

            if (entity instanceof Player player) {
                player.giveExp(points);
                player.setLevel(player.getLevel() + levels);
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("apply_effect"), (action, entity) -> {
            if (entity instanceof LivingEntity le) {
                le.addPotionEffect(MiscUtils.parseAndApplyStatusEffectInstance(action));
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("area_of_effect"), (action, entity) -> {
            float radius = Float.parseFloat(action.getOrDefault("radius", 15F).toString());
            JSONObject bientity_action = action.containsKey("bientity_action") ? (JSONObject) action.get("bientity_action") : new JSONObject();
            boolean include_actor = action.containsKey("include_actor") && Boolean.parseBoolean(action.getOrDefault("include_actor", false).toString());

            boolean hasCondition = action.containsKey("bientity_condition");

            for (net.minecraft.world.entity.Entity target : Shape.getEntities(Shape.getShape(action.getOrDefault("shape", "cube").toString()), ((CraftWorld) entity.getWorld()).getHandle(), ((CraftEntity) entity).getHandle().getPosition(1.0f), radius)) {
                if (target == entity && !include_actor) {
                    continue;
                }

                boolean run = !hasCondition || ConditionExecutor.testBiEntity((JSONObject) action.get("bientity_condition"), (CraftEntity) entity, target.getBukkitEntity());
                if (!run) {
                    continue;
                }

                Actions.executeBiEntity(entity, target.getBukkitEntity(), bientity_action);
            }

        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("block_action_at"), (action, entity) -> executeBlock(entity.getLocation(), (JSONObject) action.get("block_action"))));
        register(new ActionFactory(GenesisMC.apoliIdentifier("toggle"), (action, entity) -> {
            if (entity instanceof Player) {
                for (Origin origin : OriginPlayerAccessor.getOrigin((Player) entity).values()) {
                    if (origin.getPowers().contains(action.get("action"))) {
                        for (Power powerContainer : origin.getPowerContainers()) {
                            if (powerContainer.getType().equals("apoli:toggle")) {
                                Toggle toggle = new Toggle();
                                toggle.execute((Player) entity, powerContainer);
                            }
                        }
                    }
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("set_fall_distance"), (action, entity) -> entity.setFallDistance(Float.parseFloat(action.get("fall_distance").toString()))));
        register(new ActionFactory(GenesisMC.apoliIdentifier("trigger_cooldown"), (action, entity) -> {
            if (entity instanceof Player player) {
                for (Origin origin : OriginPlayerAccessor.getOrigin((Player) entity).values()) {
                    if (origin.getPowers().contains(action.get("action"))) {
                        for (Power powerContainer : origin.getPowerContainers()) {
                            if (powerContainer.get("cooldown") != null) {
                                String key = "*";
                                if (powerContainer.get("key").getOrDefault("key", "key.origins.primary_active") != null) {
                                    key = powerContainer.get("key").getOrDefault("key", "key.origins.primary_active").toString();
                                    if (powerContainer.getType().equals("apoli:action_on_hit")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("apoli:action_when_damage_taken")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("apoli:action_when_hit")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("apoli:action_self")) {
                                        key = "key.use";
                                    } else if (powerContainer.getType().equals("apoli:attacker_action_when_hit")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("apoli:self_action_on_hit")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("apoli:self_action_on_kill")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("apoli:self_action_when_hit")) {
                                        key = "key.attack";
                                    } else if (powerContainer.getType().equals("apoli:target_action_on_hit")) {
                                        key = "key.attack";
                                    }
                                }
                                CooldownUtils.addCooldown(player, Utils.getNameOrTag(powerContainer), powerContainer.getType(), powerContainer.getInt("cooldown"), powerContainer.get("key"));
                            }
                        }
                    }
                }
            }
        }));
    }

    private void register(EntityActions.ActionFactory factory) {
        GenesisMC.getPlugin().registry.retrieve(Registries.ENTITY_ACTION).register(factory);
    }

    public static class ActionFactory implements Registerable {
        NamespacedKey key;
        BiConsumer<JSONObject, Entity> test;

        public ActionFactory(NamespacedKey key, BiConsumer<JSONObject, Entity> test) {
            this.key = key;
            this.test = test;
        }

        public void test(JSONObject action, Entity tester) {
            if (action == null || action.isEmpty()) return; // Dont execute empty actions
            try {
                test.accept(action, tester);
            } catch (Exception e) {
                GenesisMC.getPlugin().getLogger().severe("An Error occurred while running an action: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public NamespacedKey getKey() {
            return key;
        }
    }
}
