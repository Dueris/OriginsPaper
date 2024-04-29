package me.dueris.genesismc.factory.actions.types;

import com.google.gson.JsonObject;
import me.dueris.calio.builder.inst.factory.FactoryElement;
import me.dueris.calio.builder.inst.factory.FactoryJsonArray;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.calio.registry.Registrar;
import me.dueris.calio.util.MiscUtils;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.*;
import me.dueris.genesismc.factory.powers.apoli.Cooldown;
import me.dueris.genesismc.factory.powers.apoli.Resource;
import me.dueris.genesismc.factory.powers.apoli.StackingStatusEffect;
import me.dueris.genesismc.factory.powers.apoli.Toggle;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Power;
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
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.*;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.BiConsumer;

import static me.dueris.genesismc.factory.actions.Actions.*;
import static me.dueris.genesismc.util.KeybindingUtils.addItems;

public class EntityActions {

    public void register() {
        register(new ActionFactory(GenesisMC.apoliIdentifier("modify_inventory"), (action, entity) -> {
            if (entity instanceof Player player) {
                List<String> slots = new ArrayList<>();
                if (action.isPresent("slot")) {
                    slots.add(action.getString("slot"));
                } else if (action.isPresent("slots")) {
                    slots.addAll(action.getJsonArray("slots").asList().stream().map(FactoryElement::getString).toList());
                }
                int limit = action.getNumberOrDefault("limit", 0).getInt();
                int count = 0;

                for (String slot : slots) {
                    try {
                        if (player.getInventory().getItem(getSlotFromString(slot)) == null)
                            continue;
                        executeItem(player.getInventory().getItem(Objects.requireNonNull(getSlotFromString(slot))), action.getJsonObject("item_action"));
                        if (!(limit <= 0) && count >= limit) break;
                        count++;
                    } catch (Exception e) {
                        // Ignore Exception
                    }
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("change_resource"), (action, entity) -> {
            Optional<Resource.Bar> resourceBar = Resource.getDisplayedBar(entity, action.getString("resource"));
            resourceBar.ifPresent((bar) -> {
                int change = action.getNumber("change").getInt();
                String operation = action.getStringOrDefault("operation", "add");
                bar.change(change, operation);
            });
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("modify_resource"), (action, entity) -> {
            Optional<Resource.Bar> resourceBar = Resource.getDisplayedBar(entity, action.getString("resource"));
            resourceBar.ifPresent((bar) -> {
                FactoryElement obj = action.isPresent("modifier") ? action.getElement("modifier") : action.getElement("modifiers");
                List<FactoryElement> result = new ArrayList<>();

                if (obj.isJsonArray()) {
                    FactoryJsonArray jsonArray = obj.toJsonArray();
                    for (FactoryElement item : jsonArray.asList()) {
                        result.add(item);
                    }
                } else {
                    result.add(obj);
                }

                result.stream().filter(FactoryElement::isJsonObject).map(FactoryElement::toJsonObject).map(Modifier::new).toList().forEach(modifier -> {
                    float change = modifier.value();
                    String operation = modifier.operation();
                    bar.change(Math.round(change), operation);
                });
            });
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("set_on_fire"), (action, entity) -> entity.setFireTicks(action.getNumber("duration").getInt() * 20)));
        register(new ActionFactory(GenesisMC.apoliIdentifier("spawn_entity"), (action, entity) -> {
            OriginConsoleSender originConsoleSender = new OriginConsoleSender();
            originConsoleSender.setOp(true);
            RaycastUtils.executeNMSCommand(((CraftEntity) entity).getHandle(), CraftLocation.toVec3D(entity.getLocation()), "summon $1 %1 %2 %3 $2"
                .replace("$1", action.getString("entity_type"))
                .replace("$2", action.getStringOrDefault("tag", "{}"))
                .replace("%1", String.valueOf(entity.getLocation().getX()))
                .replace("%2", String.valueOf(entity.getLocation().getY()))
                .replace("%3", String.valueOf(entity.getLocation().getZ()))
            );
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("spawn_particles"), (action, entity) -> {
            Particle particle = Particle.valueOf(action.getJsonObject("particle").getStringOrDefault("type", null).split(":")[1].toUpperCase());
            int count = action.getNumber("count").getInt();
            float offset_x = action.getNumberOrDefault("offset_z", 0).getFloat();
            float offset_y = action.getNumberOrDefault("offset_z", 0.5F).getFloat();
            float offset_z = action.getNumberOrDefault("offset_z", 0).getFloat();
            if (action.isPresent("spread")) {
                FactoryJsonObject spread = action.getJsonObject("spread");
                if (spread.isPresent("y")) {
                    offset_y = spread.getNumber("y").getFloat();
                }

                if (spread.isPresent("x")) {
                    offset_x = spread.getNumber("x").getFloat();
                }

                if (spread.isPresent("z")) {
                    offset_z = spread.getNumber("z").getFloat();
                }
            }
            entity.getWorld().spawnParticle(particle, new Location(entity.getWorld(), entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ()), count, offset_x, offset_y, offset_z, 0);
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("random_teleport"), (action, entity) -> {
            int spreadDistance = action.getNumberOrDefault("max_width", 8).getInt();
            int attempts = action.getNumberOrDefault("attempts", 1).getInt();
            for (int i = 0; i < attempts; i++) {
                String cmd = "spreadplayers {xloc} {zloc} 1 {spreadDist} false {name}"
                    .replace("{xloc}", String.valueOf(entity.getLocation().getX()))
                    .replace("{zloc}", String.valueOf(entity.getLocation().getZ()))
                    .replace("{spreadDist}", String.valueOf(spreadDistance))
                    .replace("{name}", "@e[{data}]"
                        .replace("{data}", "x=" + entity.getLocation().getX() + ",y=" + entity.getLocation().getY() + ",z=" + entity.getLocation().getZ() + ",type=" + entity.getType().toString().toLowerCase() + ",x_rotation=" + entity.getLocation().getDirection().getX() + ",y_rotation=" + entity.getLocation().getDirection().getY())
                    );
                RaycastUtils.executeNMSCommand(((CraftEntity) entity).getHandle(), CraftLocation.toVec3D(entity.getLocation()), cmd);
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("remove_power"), (action, entity) -> {
            if (entity instanceof Player p) {
                Power powerContainer = ((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(action.getNamespacedKey("power"));
                if (powerContainer != null) {
                    RaycastUtils.executeNMSCommand(((CraftEntity) p).getHandle(), CraftLocation.toVec3D(p.getLocation()), "power remove {name} {identifier}".replace("{name}", p.getName()).replace("{identifier}", action.getString("action")));
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("spawn_effect_cloud"), (action, entity) -> {
            float radius = action.getNumberOrDefault("radius", 3.0F).getFloat();
            int waitTime = action.getNumberOrDefault("wait_time", 10).getInt();
            float radiusOnUse = action.getNumberOrDefault("radius_on_use", -0.5F).getFloat();
            List<PotionEffect> effects = MiscUtils.parseAndReturnPotionEffects(action);

            net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
            ServerLevel level = (ServerLevel) nmsEntity.level();
            net.minecraft.world.entity.AreaEffectCloud cloud = new net.minecraft.world.entity.AreaEffectCloud(level, entity.getX(), entity.getY(), entity.getZ());
            if (nmsEntity instanceof net.minecraft.world.entity.LivingEntity livingEntity) {
                cloud.setOwner(livingEntity);
            }
            cloud.setRadius(radius);
            cloud.setRadiusOnUse(radiusOnUse);
            cloud.setWaitTime(waitTime);
            cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());
            Utils.toMobEffectList(effects).forEach(cloud::addEffect);
            // Color should be set automatically when the effects are added

            level.addFreshEntity(cloud);
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("replace_inventory"), (action, entity) -> {
            if (entity instanceof Player player) {
                List<String> slots = new ArrayList<>();
                if (action.isPresent("slot")) {
                    slots.add(action.getString("slot"));
                } else if (action.isPresent("slots")) {
                    slots.addAll(action.getJsonArray("slots").asList().stream().map(FactoryElement::getString).toList());
                }

                for (String slot : slots) {
                    try {
                        if (player.getInventory().getItem(getSlotFromString(slot)) == null)
                            continue;
                        ItemStack item = player.getInventory().getItem(getSlotFromString(action.getString("slot")));
                        ItemStack replaceWith = action.getItemStack("stack");
                        item.setType(replaceWith.getType());
                        item.setAmount(replaceWith.getAmount());
                    } catch (Exception e) {
                        // Ignore Exception
                    }
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("heal"), (action, entity) -> {
            if (entity instanceof LivingEntity li) {
                double healthFinal = li.getHealth() + action.getNumber("amount").getFloat();
                if (li.getHealth() >= 20) return;
                if (healthFinal > 20) {
                    li.setHealth(20);
                } else {
                    li.setHealth(healthFinal);
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("clear_effect"), (action, entity) -> {
            PotionEffectType potionEffectType = StackingStatusEffect.getPotionEffectType(action.getString("effect"));
            if (entity instanceof Player player) {
                if (player.hasPotionEffect(potionEffectType)) {
                    player.removePotionEffect(potionEffectType);
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("exhaust"), (action, entity) -> {
            if (entity instanceof Player player) {
                player.setFoodLevel(player.getFoodLevel() - Math.round(action.getNumber("amount").getFloat()));
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("explode"), (action, entity) -> {
            float explosionPower = action.getNumber("power").getFloat();
            String destruction_type = "break";
            boolean create_fire = false;
            ServerLevel level = ((CraftWorld) entity.getWorld()).getHandle();

            if (action.isPresent("destruction_type"))
                destruction_type = action.getString("destruction_type");
            if (action.isPresent("create_fire"))
                create_fire = action.getBoolean("create_fire");

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
                if (action.isPresent("equipment_slot")) {
                    try {
                        if (player.getInventory().getItem(getSlotFromString(action.getString("equipment_slot"))) == null)
                            return;
                        executeItem(player.getInventory().getItem(getSlotFromString(action.getString("equipment_slot"))), action.getJsonObject("action"));
                    } catch (Exception e) {
                        // Ignore Exception
                    }
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("dismount"), (action, entity) -> entity.getVehicle().removePassenger(entity)));
        register(new ActionFactory(GenesisMC.apoliIdentifier("feed"), (action, entity) -> {
            if (entity instanceof Player player) {
                if (player.getFoodLevel() == 20 || player.getFoodLevel() + action.getNumber("food").getInt() >= 20) {
                    player.setFoodLevel(20);
                } else {
                    player.setFoodLevel(player.getFoodLevel() + action.getNumber("food").getInt());
                }

                if (player.getSaturation() == 20 || player.getSaturation() + action.getNumber("saturation").getFloat() >= 20) {
                    player.setSaturation(20);
                } else {
                    player.setSaturation(player.getSaturation() + action.getNumber("saturation").getFloat());
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("fire_projectile"), (action, entity) -> {
            if (entity instanceof ProjectileSource) {
                float finalDivergence1 = action.getNumberOrDefault("divergence", 1.0).getFloat();
                float speed = action.getNumberOrDefault("speed", 1).getFloat();
                EntityType typeE;
                if (action.getString("entity_type").equalsIgnoreCase("origins:enderian_pearl")) {
                    typeE = EntityType.ENDER_PEARL;
                } else {
                    typeE = EntityType.valueOf(action.getString("entity_type").split(":")[1].toUpperCase());
                }

                for (int i = 0; i < action.getNumberOrDefault("count", 1).getInt(); i++) {
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
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("passanger_action"), (action, entity) -> {
            if (entity.getPassengers() == null || entity.getPassengers().isEmpty()) return;
            executeEntity(entity.getPassenger(), action.getJsonObject("action"));
            executeBiEntity(entity.getPassenger(), entity, action.getJsonObject("bientity_action"));
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("riding_action"), (action, entity) -> {
            if (entity.getVehicle() == null) return;
            if (action.isPresent("action")) {
                executeEntity(entity.getVehicle(), action.getJsonObject("action"));
            }
            if (action.isPresent("bientity_action")) {
                executeBiEntity(entity.getVehicle(), entity, action.getJsonObject("bientity_action"));
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("raycast"), (action, entity) -> RaycastUtils.action(action, ((CraftEntity) entity).getHandle())));
        register(new ActionFactory(GenesisMC.apoliIdentifier("extinguish"), (action, entity) -> entity.setFireTicks(0)));
        register(new ActionFactory(GenesisMC.apoliIdentifier("play_sound"), (action, entity) -> {
            Sound sound = MiscUtils.parseSound(action.getString("sound"));
            Float volume = action.getNumberOrDefault("volume", 1.0).getFloat();
            Float pitch = action.getNumberOrDefault("pitch", 1.0).getFloat();
            entity.getWorld().playSound(entity, sound, volume, pitch);
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("gain_air"), (action, entity) -> {
            long amt = action.getNumber("value").getInt();
            if (entity instanceof Player p) {
                p.setRemainingAir(p.getRemainingAir() + Math.toIntExact(amt));
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("drop_inventory"), (action, entity) -> {
            if (entity instanceof HumanEntity player) {
                List<String> slots = new ArrayList<>();
                if (action.isPresent("slot")) {
                    slots.add(action.getString("slot"));
                } else if (action.isPresent("slots")) {
                    slots.addAll(action.getJsonArray("slots").asList().stream().map(FactoryElement::getString).toList());
                }
                for (String slot : slots) {
                    try {
                        if (player.getInventory().getItem(getSlotFromString(slot)) == null)
                            return;
                        executeItem(player.getInventory().getItem(getSlotFromString(slot)), action);
                    } catch (Exception e) {
                        //fail noononooo
                    }
                }
                List<ItemStack> stacks = new ArrayList<>();
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item == null) continue;
                    if (ConditionExecutor.testItem(action.getJsonObject("item_condition"), item)) {
                        player.getWorld().dropItemNaturally(player.getLocation(), item);
                        stacks.add(item);
                    }
                }

                stacks.forEach(stack -> player.getInventory().remove(stack));
                addItems(player);
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("grant_advancement"), (action, entity) -> RaycastUtils.executeNMSCommand(((CraftEntity) entity).getHandle(), CraftLocation.toVec3D(entity.getLocation()), "advancement grant $1 $2".replace("$1", entity.getName()).replace("$2", action.getString("advacnement")))));
        register(new ActionFactory(GenesisMC.apoliIdentifier("revoke_advancement"), (action, entity) -> RaycastUtils.executeNMSCommand(((CraftEntity) entity).getHandle(), CraftLocation.toVec3D(entity.getLocation()), "advancement revoke $1 $2".replace("$1", entity.getName()).replace("$2", action.getString("advacnement")))));
        register(new ActionFactory(GenesisMC.apoliIdentifier("selector_action"), (action, entity) -> {
            if (action.isPresent("bientity_condition")) {
                if (entity instanceof Player player) {
                    executeBiEntity(entity, player.getTargetEntity(4, false), action.getJsonObject("bientity_condition"));
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("give"), (action, entity) -> {
            int amt = 1;
            if (action.isPresent("amount")) {
                amt = action.getNumber("amount").getInt();
            }

            if (action.isPresent("stack")) {
                FactoryJsonObject stackObject = action.getJsonObject("stack");
                String item = stackObject.getString("item");
                int amount = stackObject.getNumberOrDefault("amount", 1).getInt();

                ItemStack itemStack = new ItemStack(Material.valueOf(item.toUpperCase().split(":")[1]), amount);

                if (action.isPresent("item_action")) {
                    executeItem(itemStack, action);
                }
                if (entity instanceof Player player) {
                    player.getInventory().addItem(itemStack);
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("damage"), (action, entity) -> {
            if (entity instanceof Player P) {
                P.damage(action.getNumber("amount").getFloat());
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("add_velocity"), (action, entity) -> {
            Space space = action.getEnumValueOrDefault("space", Space.class, Space.WORLD);

            Vector3f vec = VectorGetter.getAsVector3f(action);
            net.minecraft.world.entity.Entity en = ((CraftLivingEntity) entity).getHandle();
            space.toGlobal(vec, en);
            if (action.getBooleanOrDefault("set", false)) {
                en.getBukkitEntity().getVelocity().add(new Vector(vec.x, vec.y, vec.z));
            } else {
                en.getBukkitEntity().setVelocity(new Vector(vec.x, vec.y, vec.z));
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("execute_command"), (action, entity) -> {
            String cmd;
            if (action.getString("command").startsWith("action") || action.getString("command").startsWith("/action"))
                return;
            if (action.getString("command").startsWith("/")) {
                cmd = action.getString("command").split("/")[1];
            } else {
                cmd = action.getString("command");
            }
            RaycastUtils.executeNMSCommand(((CraftEntity) entity).getHandle(), CraftLocation.toVec3D(entity.getLocation()), cmd);
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("add_xp"), (action, entity) -> {
            int points = 0;
            int levels = 0;

            if (action.isPresent("points")) points = action.getNumber("points").getInt();
            if (action.isPresent("levels")) levels = action.getNumber("points").getInt();

            if (entity instanceof Player player) {
                player.giveExp(points);
                player.setLevel(player.getLevel() + levels);
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("apply_effect"), (action, entity) -> {
            if (entity instanceof LivingEntity le) {
                MiscUtils.parseAndReturnPotionEffects(action).forEach(potionEffect -> le.addPotionEffect(potionEffect, true));
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("area_of_effect"), (action, entity) -> {
            float radius = action.getNumberOrDefault("radius", 15F).getFloat();
            FactoryJsonObject bientity_action = action.isPresent("bientity_action") ? action.getJsonObject("bientity_action") : new FactoryJsonObject(new JsonObject());
            boolean include_actor = action.getBooleanOrDefault("include_actor", false);

            boolean hasCondition = action.isPresent("bientity_condition");

            for (net.minecraft.world.entity.Entity target : Shape.getEntities(action.getEnumValueOrDefault("shape", Shape.class, Shape.CUBE), ((CraftWorld) entity.getWorld()).getHandle(), ((CraftEntity) entity).getHandle().getPosition(1.0f), radius)) {
                if (target == entity && !include_actor) {
                    continue;
                }

                boolean run = !hasCondition || ConditionExecutor.testBiEntity(action.getJsonObject("bientity_condition"), (CraftEntity) entity, target.getBukkitEntity());
                if (!run) {
                    continue;
                }

                Actions.executeBiEntity(entity, target.getBukkitEntity(), bientity_action);
            }

        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("block_action_at"), (action, entity) -> executeBlock(entity.getLocation(), action.getJsonObject("block_action"))));
        register(new ActionFactory(GenesisMC.apoliIdentifier("toggle"), (action, entity) -> {
            if (entity instanceof Player p) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, "apoli:toggle")) {
                    if (!power.getTag().equalsIgnoreCase(action.getString("power"))) continue;
                    Toggle toggle = new Toggle();
                    toggle.execute(p, power);
                }
            }
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("set_fall_distance"), (action, entity) -> entity.setFallDistance(action.getNumber("fall_distance").getFloat())));
        register(new ActionFactory(GenesisMC.apoliIdentifier("trigger_cooldown"), (action, entity) -> {
            if (entity instanceof Player player) {
                Arrays.stream(new String[]{"apoli:action_on_hit", "apoli:action_when_damage_taken", "apoli:action_when_hit",
                    "apoli:action_self", "apoli:attacker_action_when_hit", "apoli:self_action_on_hit",
                    "apoli:self_action_on_kill", "apoli:self_action_when_hit", "apoli:target_action_on_hit"}).forEach(type -> {
                    for (Power powerContainer : OriginPlayerAccessor.getMultiPowerFileFromType(player, type)) {
                        if (powerContainer.isPresent("cooldown") && powerContainer.isPresent("key")) {
                            Cooldown.addCooldown(player, powerContainer.getNumber("cooldown").getInt(), powerContainer);
                        }
                    }
                });
            }
        }));
    }

    private void register(EntityActions.ActionFactory factory) {
        GenesisMC.getPlugin().registry.retrieve(Registries.ENTITY_ACTION).register(factory);
    }

    public static class ActionFactory implements Registrable {
        NamespacedKey key;
        BiConsumer<FactoryJsonObject, Entity> test;

        public ActionFactory(NamespacedKey key, BiConsumer<FactoryJsonObject, Entity> test) {
            this.key = key;
            this.test = test;
        }

        public void test(FactoryJsonObject action, Entity tester) {
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
