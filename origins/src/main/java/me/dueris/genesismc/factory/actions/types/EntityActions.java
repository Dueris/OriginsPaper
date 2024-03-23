package me.dueris.genesismc.factory.actions.types;

import me.dueris.calio.registry.Registrar;
import me.dueris.calio.util.MiscUtils;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.DestructionType;
import me.dueris.genesismc.factory.data.types.ExplosionMask;
import me.dueris.genesismc.factory.data.types.Space;
import me.dueris.genesismc.factory.powers.apoli.AttributeHandler;
import me.dueris.genesismc.factory.powers.apoli.Resource;
import me.dueris.genesismc.factory.powers.apoli.StackingStatusEffect;
import me.dueris.genesismc.factory.powers.apoli.Toggle;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.apoli.RaycastUtils;
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

import static me.dueris.genesismc.factory.actions.Actions.*;
import static me.dueris.genesismc.util.KeybindingUtils.addItems;

public class EntityActions {

	public static void runEntity(Entity entity, JSONObject action) {
		if (action == null || action.isEmpty()) return;
		String type = action.get("type").toString();
		if (entity == null) return;

		if (type.equals("apoli:modify_inventory")) {
			if (entity instanceof Player player) {
				if (action.containsKey("slot")) {
					try {
						if (player.getInventory().getItem(getSlotFromString(action.get("slot").toString())) == null)
							return;
						ItemActionType(player.getInventory().getItem(getSlotFromString(action.get("slot").toString())), action);
					} catch (Exception e) {
						//silently fail bc idk whats going on and yeah it wokrs lol
					}
				}
			}
		}
		if (type.equals("apoli:change_resource")) {
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
			if(bossBar.getProgress() == 1.0){
				Actions.EntityActionType(entity, CraftApoli.getPowerFromTag(resource).getAction("max_action"));
			} else if(bossBar.getProgress() == 0.0){
				Actions.EntityActionType(entity, CraftApoli.getPowerFromTag(resource).getAction("min_action"));
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
		}
		if (type.equals("apoli:modify_resource")) {
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
			if(bossBar.getProgress() == 1.0){
				Actions.EntityActionType(entity, CraftApoli.getPowerFromTag(resource).getAction("max_action"));
			} else if(bossBar.getProgress() == 0.0){
				Actions.EntityActionType(entity, CraftApoli.getPowerFromTag(resource).getAction("min_action"));
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
		}
		if (type.equals("apoli:set_on_fire")) {
			entity.setFireTicks(Integer.parseInt(action.get("duration").toString()));
		}
		if (type.equals("apoli:spawn_entity")) {
			OriginConsoleSender originConsoleSender = new OriginConsoleSender();
			originConsoleSender.setOp(true);
			RaycastUtils.executeCommandAtHit(((CraftEntity) entity).getHandle(), CraftLocation.toVec3D(entity.getLocation()), "summon $1 %1 %2 %3 $2"
				.replace("$1", action.get("entity_type").toString())
				.replace("$2", action.getOrDefault("tag", "{}").toString())
				.replace("%1", String.valueOf(entity.getLocation().getX()))
				.replace("%2", String.valueOf(entity.getLocation().getY()))
				.replace("%3", String.valueOf(entity.getLocation().getZ()))
			);
		}
		if (type.equals("apoli:spawn_particles")) {
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
		}
		if (type.equals("apoli:random_teleport")) {
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
		}
		if (type.equals("apoli:remove_power")) {
			if (entity instanceof Player p) {
				Power powerContainer = ((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(NamespacedKey.fromString(action.get("power").toString()));
				if (powerContainer != null) {
					RaycastUtils.executeCommandAtHit(((CraftEntity) p).getHandle(), CraftLocation.toVec3D(p.getLocation()), "power remove {name} {identifier}".replace("{name}", p.getName()).replace("{identifier}", action.get("action").toString()));
				}
			}
		}
		if (type.equals("apoli:spawn_effect_cloud")) {
			spawnEffectCloud(entity, Float.valueOf(action.getOrDefault("radius", 3.0).toString()), Integer.valueOf(action.getOrDefault("wait_time", 10).toString()), new PotionEffect(StackingStatusEffect.getPotionEffectType(action.get("effect").toString()), 1, 1));
		}
		if (type.equals("apoli:replace_inventory")) {
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
		}
		if (type.equals("apoli:heal")) {
			if (entity instanceof LivingEntity li) {
				double healthFinal = li.getHealth() + Double.parseDouble(action.get("amount").toString());
				if (li.getHealth() >= 20) return;
				if (healthFinal > 20) {
					li.setHealth(20);
				} else {
					li.setHealth(healthFinal);
				}
			}
		}
		if (type.equals("apoli:clear_effect")) {
			PotionEffectType potionEffectType = StackingStatusEffect.getPotionEffectType(action.get("effect").toString());
			if (entity instanceof Player player) {
				if (player.hasPotionEffect(potionEffectType)) {
					player.removePotionEffect(potionEffectType);
				}
			}
		}
		if (type.equals("apoli:exhaust")) {
			if (entity instanceof Player player) {
				player.setFoodLevel(player.getFoodLevel() - Math.round(Float.valueOf(action.get("amount").toString())));
			}
		}
		if (type.equals("apoli:explode")) {
			long explosionPower = 1l;
			if(action.get("power") instanceof Long lep){
				explosionPower = lep;
			} else if (action.get("power") instanceof Double dep) {
				explosionPower = Math.round(dep);
			}
			String destruction_type = "break";
			boolean create_fire = false;
			ServerLevel level = ((CraftWorld)entity.getWorld()).getHandle();

			if (action.containsKey("destruction_type"))
				destruction_type = action.get("destruction_type").toString();
			if (action.containsKey("create_fire"))
				create_fire = Boolean.parseBoolean(action.get("create_fire").toString());

			Explosion explosion = new Explosion(
				level,
				((CraftEntity)entity).getHandle(),
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
		}
		if (type.equals("apoli:crafting_table")) {
			if (entity instanceof Player player) {
				Inventory inventory = Bukkit.createInventory(player, InventoryType.CRAFTING);
				player.openInventory(inventory);
			}
		}
		if (type.equals("apoli:ender_chest")) {
			if (entity instanceof Player player) {
				Inventory inventory = Bukkit.createInventory(player, InventoryType.ENDER_CHEST);
				player.openInventory(inventory);
			}
		}
		if (type.equals("apoli:equipped_item_action")) {
			if (entity instanceof Player player) {
				if (action.containsKey("equipment_slot")) {
					try {
						if (player.getInventory().getItem(getSlotFromString(action.get("equipment_slot").toString())) == null)
							return;
						ItemActionType(player.getInventory().getItem(getSlotFromString(action.get("equipment_slot").toString())), action);
					} catch (Exception e) {
						//silently fail
					}
				}
			}
		}
		if (type.equals("apoli:dismount")) {
			entity.getVehicle().removePassenger(entity);
		}
		if (type.equals("apoli:feed")) {
			if (entity instanceof Player player) {
				if(player.getFoodLevel() == 20 || player.getFoodLevel() + Integer.parseInt(action.get("food").toString()) >= 20){
					player.setFoodLevel(20);
				}else{
					player.setFoodLevel(player.getFoodLevel() + Integer.parseInt(action.get("food").toString()));
				}

				if(player.getSaturation() == 20 || player.getSaturation() + Float.parseFloat(action.get("saturation").toString()) >= 20){
					player.setSaturation(20);
				}else{
					player.setSaturation(player.getSaturation() + Float.parseFloat(action.get("saturation").toString()));
				}
			}
		}
		if (type.equals("apoli:fire_projectile")) {
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
		}
		if (type.equals("apoli:passenger_action")) {
			if (entity.getPassengers() == null || entity.getPassengers().isEmpty()) return;
			EntityActionType(entity.getPassenger(), (JSONObject) action.get("action"));
			BiEntityActionType(entity.getPassenger(), entity, (JSONObject) action.get("bientity_action"));
		}
		if (type.equals("apoli:riding_action")) {
			if (entity.getVehicle() == null) return;
			if (action.containsKey("action")) {
				EntityActionType(entity.getVehicle(), (JSONObject) action.get("action"));
			}
			if (action.containsKey("bientity_action")) {
				BiEntityActionType(entity.getVehicle(), entity, (JSONObject) action.get("bientity_action"));
			}
		}
		if (type.equals("apoli:raycast")) {
			RaycastUtils.action(action, ((CraftEntity) entity).getHandle());
		}
		if (type.equals("apoli:extinguish")) {
			entity.setFireTicks(0);
		}
		if (type.equals("apoli:play_sound")) {
			Sound sound = MiscUtils.parseSound(action.get("sound").toString());
			Float volume = Float.parseFloat(action.getOrDefault("volume", 1.0).toString());
			Float pitch = Float.parseFloat(action.getOrDefault("pitch", 1.0).toString());
			entity.getWorld().playSound(entity, sound, volume, pitch);
		}
		if (type.equals("apoli:gain_air")) {
			long amt = (long) action.get("value");
			if (entity instanceof Player p) {
				p.setRemainingAir(p.getRemainingAir() + Math.toIntExact(amt));
			}
		}
		if (type.equals("apoli:drop_inventory")) {
			if (entity instanceof Player player) {
				if (action.containsKey("slot")) {
					try {
						if (player.getInventory().getItem(getSlotFromString(action.get("slot").toString())) == null)
							return;
						ItemActionType(player.getInventory().getItem(getSlotFromString(action.get("slot").toString())), action);
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
		}
		if (type.equals("apoli:grant_advancement")) {
			RaycastUtils.executeCommandAtHit(((CraftEntity) entity).getHandle(), CraftLocation.toVec3D(entity.getLocation()), "advancement grant $1 $2".replace("$1", entity.getName()).replace("$2", action.get("advacnement").toString()));
		}
		if (type.equals("apoli:revoke_advancement")) {
			RaycastUtils.executeCommandAtHit(((CraftEntity) entity).getHandle(), CraftLocation.toVec3D(entity.getLocation()), "advancement revoke $1 $2".replace("$1", entity.getName()).replace("$2", action.get("advacnement").toString()));
		}
		if (type.equals("apoli:selector_action")) {
			if (action.get("bientity_condition") != null) {
				if (entity instanceof Player player) {
					BiEntityActionType(entity, player.getTargetEntity(AttributeHandler.Reach.getDefaultReach(player), false), (JSONObject) action.get("bientity_condition"));
				}
			}
		}
		if (type.equals("apoli:give")) {
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
					ItemActionType(itemStack, action);
				}
				if (entity instanceof Player player) {
					player.getInventory().addItem(itemStack);
				}
			}

		}
		if (type.equals("apoli:damage")) {
			if (entity instanceof Player P) {
				P.damage(Double.valueOf(action.get("amount").toString()));
			}
		}
		if (type.equals("apoli:add_velocity")) {
			float y = 0f;
			float x = 0f;
			float z = 0f;
			Space space = Space.getSpace(action.getOrDefault("space", "world").toString());
			if (action.containsKey("y")) y = Float.parseFloat(action.get("y").toString());
			if (action.containsKey("x")) x = Float.parseFloat(action.get("x").toString());
			if (action.containsKey("z")) z = Float.parseFloat(action.get("z").toString());

			Vector3f vec = new Vector3f(x, y, z);
			net.minecraft.world.entity.Entity en = ((CraftLivingEntity) entity).getHandle();
			space.toGlobal(vec, en);
			if (Boolean.parseBoolean(action.getOrDefault("set", "false").toString())) {
				en.getBukkitEntity().getVelocity().add(new Vector(vec.x, vec.y, vec.z));
			} else {
				en.getBukkitEntity().setVelocity(new Vector(vec.x, vec.y, vec.z));
			}
		}
		if (type.equals("apoli:execute_command")) {
			String cmd = null;
			if (action.get("command").toString().startsWith("action") || action.get("command").toString().startsWith("/action"))
				return;
			if (action.get("command").toString().startsWith("/")) {
				cmd = action.get("command").toString().split("/")[1];
			} else {
				cmd = action.get("command").toString();
			}
			RaycastUtils.executeCommandAtHit(((CraftEntity) entity).getHandle(), CraftLocation.toVec3D(entity.getLocation()), cmd);
		}
		if (type.equals("apoli:add_xp")) {
			int points = 0;
			int levels = 0;

			if (action.containsKey("points")) points = Integer.parseInt(action.get("points").toString());
			if (action.containsKey("levels")) levels = Integer.parseInt(action.get("levels").toString());

			if (entity instanceof Player player) {
				player.giveExp(points);
				player.setLevel(player.getLevel() + levels);
			}
		}
		if (type.equals("apoli:apply_effect")) {
			if (entity instanceof LivingEntity le) {
				le.addPotionEffect(MiscUtils.parseAndApplyStatusEffectInstance(action));
			}
		}
		if (type.equals("apoli:area_of_effect")) {
			float radius = 15f;
			JSONObject bientity_action = new JSONObject();
			boolean include_target = false;

			if (action.containsKey("radius")) radius = Float.parseFloat(action.get("radius").toString());
			if (action.containsKey("bientity_action"))
				bientity_action = (JSONObject) action.get("bientity_action");
			if (action.containsKey("include_target"))
				include_target = Boolean.parseBoolean(action.get("include_target").toString());

			for (Entity nearbyEntity : entity.getNearbyEntities(radius, radius, radius)) {
				boolean run = ConditionExecutor.testBiEntity((JSONObject) action.get("bientity_condition"), (CraftEntity) entity, (CraftEntity) nearbyEntity);
				if (run) {
					BiEntityActionType(entity, nearbyEntity, bientity_action);
				}
			}
			if (include_target) BiEntityActionType(entity, entity, bientity_action);
		}
		if (type.equals("apoli:block_action_at")) {
			BlockActionType(entity.getLocation(), (JSONObject) action.get("block_action"));
		}
		if (type.equals("apoli:toggle")) {
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
		}
		if (type.equals("apoli:set_fall_distance")) {
			entity.setFallDistance(Float.parseFloat(action.get("fall_distance").toString()));
		}
		if (type.equals("apoli:trigger_cooldown")) {
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
		}
	}
}
