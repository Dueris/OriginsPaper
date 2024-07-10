package me.dueris.originspaper.factory.actions.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.dueris.calio.data.CalioDataTypes;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.ParticleEffect;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.actions.types.entity.RaycastAction;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.data.types.*;
import me.dueris.originspaper.factory.powers.apoli.Cooldown;
import me.dueris.originspaper.factory.powers.apoli.CooldownPower;
import me.dueris.originspaper.factory.powers.apoli.EntitySetPower;
import me.dueris.originspaper.factory.powers.apoli.Resource;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.registry.registries.Layer;
import me.dueris.originspaper.util.Util;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import me.dueris.originspaper.util.entity.PowerUtils;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.potion.CraftPotionUtil;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class EntityActions {

	public void register() {
		register(new ActionFactory(OriginsPaper.apoliIdentifier("change_resource"), (data, entity) -> {
			Optional<Resource.Bar> resourceBar = Resource.getDisplayedBar(entity, data.getString("resource"));
			resourceBar.ifPresent((bar) -> {
				int change = data.getNumber("change").getInt();
				String operation = data.getStringOrDefault("operation", "add");
				bar.change(change, operation);
			});
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("set_resource"), (data, entity) -> {
			Optional<Resource.Bar> resourceBar = Resource.getDisplayedBar(entity, data.getString("resource"));
			resourceBar.ifPresent((bar) -> {
				int val = data.getNumber("value").getInt();
				bar.change(val, "set");
			});
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("modify_resource"), (data, entity) -> {
			Optional<Resource.Bar> resourceBar = Resource.getDisplayedBar(entity, data.getString("resource"));
			resourceBar.ifPresent((bar) -> {
				FactoryElement obj = data.isPresent("modifier") ? data.getElement("modifier") : data.getElement("modifiers");
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
		register(new ActionFactory(OriginsPaper.apoliIdentifier("remove_power"), (data, entity) -> {
			if (entity instanceof Player p) {
				PowerType powerContainer = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(data.getNamespacedKey("power"));
				if (powerContainer == null) {
					OriginsPaper.getPlugin().getLogger().severe("Searched PowerType was null when attempting to revoke a power: {}".replace("{}", data.getString("power")));
					return;
				}
				Layer layer = CraftApoli.getLayerFromTag(data.getString("source"));
				try {
					PowerUtils.removePower(Bukkit.getConsoleSender(), powerContainer, p, layer, false);
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("grant_power"), (data, entity) -> {
			if (entity instanceof Player p) {
				PowerType powerContainer = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(data.getNamespacedKey("power"));
				if (powerContainer == null) {
					OriginsPaper.getPlugin().getLogger().severe("Searched PowerType was null when attempting to apply a new power: {}".replace("{}", data.getString("power")));
					return;
				}
				Layer layer = CraftApoli.getLayerFromTag(data.getString("source"));
				try {
					PowerUtils.grantPower(Bukkit.getConsoleSender(), powerContainer, p, layer, false);
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("revoke_power"), (data, entity) -> {
			if (entity instanceof Player p) {
				PowerType powerContainer = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(data.getNamespacedKey("power"));
				if (powerContainer == null) {
					OriginsPaper.getPlugin().getLogger().severe("Searched PowerType was null when attempting to revoke a power: {}".replace("{}", data.getString("power")));
					return;
				}
				Layer layer = CraftApoli.getLayerFromTag(data.getString("source"));
				try {
					PowerUtils.removePower(Bukkit.getConsoleSender(), powerContainer, p, layer, false);
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("revoke_all_powers"), (data, entity) -> {
			if (entity instanceof Player p) {
				for (NamespacedKey powerKey : PowerHolderComponent.getPowers(p).stream().map(PowerType::key).toList()) {
					PowerType powerContainer = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(powerKey);
					if (powerContainer == null) {
						OriginsPaper.getPlugin().getLogger().severe("Searched PowerType was null when attempting to revoke a power: {}".replace("{}", powerKey.asString()));
						return;
					}
					Layer layer = CraftApoli.getLayerFromTag(data.getString("source"));
					try {
						PowerUtils.removePower(Bukkit.getConsoleSender(), powerContainer, p, layer, false);
					} catch (InstantiationException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("explode"), (data, entity) -> {
			float explosionPower = data.getNumber("power").getFloat();
			String destruction_type = "break";
			boolean create_fire = false;
			ServerLevel level = ((CraftWorld) entity.getWorld()).getHandle();

			if (data.isPresent("destruction_type"))
				destruction_type = data.getString("destruction_type");
			if (data.isPresent("create_fire"))
				create_fire = data.getBoolean("create_fire");

			Explosion explosion = new Explosion(
				level,
				entity.getHandle(),
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
			ExplosionMask.getExplosionMask(explosion, level).apply(data, true);
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("show_toast"), (data, entity) -> {
			String title = data.getString("title");
			String description = data.getString("description");
			@Nullable ItemStack icon = data.isPresent("icon") ? data.getItemStack("icon") : new ItemStack(Material.PLAYER_HEAD);

			if (entity instanceof CraftPlayer player) {
				String advancement = "{\n" +
					"    \"criteria\": {\n" +
					"      \"trigger\": {\n" +
					"        \"trigger\": \"minecraft:impossible\"\n" +
					"      }\n" +
					"    },\n" +
					"    \"display\": {\n" +
					"      \"icon\": {\n" +
					"        \"id\": \"" + icon.getType().getKey().asString() + "\"\n" +
					"      },\n" +
					"      \"title\": {\n" +
					"        \"text\": \"" + title + "\"\n" +
					"      },\n" +
					"      \"description\": {\n" +
					"        \"text\": \"" + description + "\"\n" +
					"      },\n" +
					"      \"background\": \"minecraft:textures/gui/advancements/backgrounds/adventure.png\",\n" +
					"      \"frame\": \"task\",\n" +
					"      \"announce_to_chat\": false,\n" +
					"      \"show_toast\": true,\n" +
					"      \"hidden\": true\n" +
					"    }\n" +
					"  }";
				Advancement possible = Bukkit.getAdvancement(OriginsPaper.apoliIdentifier(title.replace(" ", "_").toLowerCase()));
				Advancement a = possible == null ?
					Bukkit.getUnsafe().loadAdvancement(OriginsPaper.apoliIdentifier(title.replace(" ", "_").toLowerCase()), advancement) : possible;
				// advancement loaded now
				player.getAdvancementProgress(a).awardCriteria("trigger");
				new BukkitRunnable() {
					@Override
					public void run() {
						player.getAdvancementProgress(a).revokeCriteria("trigger");
					}
				}.runTaskLater(OriginsPaper.getPlugin(), 5);
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("trigger_cooldown"), (data, entity) -> {
			if (entity instanceof Player player) {
				Arrays.stream(new String[]{"apoli:action_on_hit", "apoli:action_when_damage_taken", "apoli:action_when_hit",
					"apoli:action_self", "apoli:attacker_action_when_hit", "apoli:self_action_on_hit",
					"apoli:self_action_on_kill", "apoli:self_action_when_hit", "apoli:target_action_on_hit", "apoli:cooldown"}).forEach(type -> {
					for (PowerType powerContainer : PowerHolderComponent.getPowers(player, type)) {
						if (powerContainer instanceof CooldownPower cooldownPower) {
							Cooldown.addCooldown(player, cooldownPower.getCooldown(), cooldownPower);
						}
					}
				});
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("damage"), (data, entity) -> {
			Float damageAmount = data.getNumber("amount").getFloat();
			List<Modifier> modifiers = List.of(Modifier.getModifiers(data.getJsonObject("modifier"), data.getJsonArray("moifiers")));

			if (!modifiers.isEmpty() && entity.getHandle() instanceof LivingEntity livingEntity) {
				for (Modifier modifier : modifiers) {
					if (damageAmount > livingEntity.getMaxHealth()) break;
					damageAmount = Util.getOperationMappingsFloat().get(modifier.operation()).apply(damageAmount, modifier.value());
				}
			}

			if (damageAmount == null) {
				return;
			}

			try {
				DamageSource source;
				if (data.isPresent("damage_type")) {
					source = Util.getDamageSource(Util.DAMAGE_REGISTRY.get(data.getResourceLocation("damage_type")));
				} else {
					source = entity.getHandle().level().damageSources().generic();
				}
				if (data.isPresent("source") && !data.isPresent("damage_type")) {
					OriginsPaper.getPlugin().getLogger().warning("A \"source\" field was provided in the entity_action \"apoli:damage\", please use the \"damage_type\" field instead.");
				}
				entity.getHandle().hurt(source, damageAmount);
			} catch (Throwable t) {
				OriginsPaper.getPlugin().getLogger().severe("Error trying to deal damage via the `damage` entity action: " + t.getMessage());
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("heal"), (data, entity) -> {
			if (entity.getHandle() instanceof LivingEntity) {
				((LivingEntity) entity.getHandle()).heal(data.getNumber("amount").getFloat());
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("play_sound"), (data, entity) -> {
			SoundSource category = data.isPresent("category") ? data.getEnumValue("category", SoundSource.class) : entity.getHandle().getSoundSource();
			entity.getHandle().level().playSound(null, entity.getHandle().blockPosition(), CraftSound.bukkitToMinecraft(data.transformWithCalio("sound", CalioDataTypes::sound)), category, data.getNumberOrDefault("volume", 1.0F).getFloat(), data.getNumberOrDefault("pitch", 1.0F).getFloat());
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("exhaust"), (data, entity) -> {
			if (entity.getHandle() instanceof net.minecraft.world.entity.player.Player)
				((net.minecraft.world.entity.player.Player) entity.getHandle()).getFoodData().addExhaustion(data.getNumber("amount").getFloat());
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("apply_effect"), (data, entity) -> {
			if (entity.getHandle() instanceof LivingEntity le && !entity.getHandle().level().isClientSide) {
				if (data.isPresent("effect")) {
					MobEffectInstance effect = CraftPotionUtil.fromBukkit(Util.parsePotionEffect(data.getJsonObject("effect")));
					le.addEffect(new MobEffectInstance(effect));
				}
				if (data.isPresent("effects")) {
					(data.getJsonArray("effects").asJsonObjectList().stream().map(Util::parsePotionEffect)).forEach(e -> le.addEffect(CraftPotionUtil.fromBukkit(e)));
				}
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("clear_effect"), (data, entity) -> {
			if (entity.getHandle() instanceof LivingEntity le) {
				if (data.isPresent("effect")) {
					le.removeEffect(CraftPotionUtil.fromBukkit((data.isJsonObject("effect") ? Util.parsePotionEffect(data.getJsonObject("effect")) :
						new PotionEffect(PotionEffectType.getByKey(data.getNamespacedKey("effect")), 100, 0))).getEffect());
				} else {
					le.removeAllEffects();
				}
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("set_on_fire"), (data, entity) -> {
			entity.getHandle().igniteForSeconds(data.getNumber("duration").getInt());
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("add_velocity"), (data, entity) -> {
			if (entity.getHandle() instanceof net.minecraft.world.entity.player.Player
				&& (entity.getHandle().level().isClientSide ?
				!data.getBooleanOrDefault("client", true) : !data.getBooleanOrDefault("server", true)))
				return;
			Space space = data.getEnumValueOrDefault("space", Space.class, Space.WORLD);
			Vector3f vec = new Vector3f(
				data.getNumberOrDefault("x", 0F).getFloat(),
				data.getNumberOrDefault("y", 0F).getFloat(),
				data.getNumberOrDefault("z", 0F).getFloat());
			TriConsumer<Float, Float, Float> method = entity.getHandle()::push;
			if (data.getBooleanOrDefault("set", false)) {
				method = entity.getHandle()::setDeltaMovement;
			}
			space.toGlobal(vec, entity.getHandle());
			method.accept(vec.x, vec.y, vec.z);
			entity.getHandle().hurtMarked = true;
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("spawn_entity"), (data, entity) -> {
			if (entity.getHandle().level().isClientSide) return;

			ServerLevel serverWorld = (ServerLevel) entity.getHandle().level();
			EntityType<?> entityType = CraftRegistry.getMinecraftRegistry().registry(net.minecraft.core.registries.Registries.ENTITY_TYPE).get().get(data.getResourceLocation("entity_type"));
			CompoundTag entityNbt = data.transformWithCalio("tag", CalioDataTypes::compoundTag, new CompoundTag());

			Optional<Entity> opt$entityToSpawn = Util.getEntityWithPassengers(
				serverWorld,
				entityType,
				entityNbt,
				entity.getHandle().position(),
				entity.getHandle().getYRot(),
				entity.getHandle().getXRot()
			);

			if (opt$entityToSpawn.isEmpty()) return;
			Entity entityToSpawn = opt$entityToSpawn.get();

			serverWorld.tryAddFreshEntityWithPassengers(entityToSpawn);
			if (data.isPresent("entity_action")) {
				Actions.executeEntity(entityToSpawn.getBukkitEntity(), data.getJsonObject("entity_action"));
			}
			if (data.isPresent("bientity_action")) {
				Actions.executeBiEntity(entity, entityToSpawn.getBukkitEntity(), data.getJsonObject("bientity_action"));
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("gain_air"), (data, entity) -> {
			if (entity.getHandle() instanceof LivingEntity le) {
				le.setAirSupply(Math.min(le.getAirSupply() + data.getNumber("value").getInt(), le.getMaxAirSupply()));
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("block_action_at"), (data, entity) -> {
			Actions.executeBlock(entity.getLocation(), data.getJsonObject("block_action"));
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("spawn_effect_cloud"), (data, entity) -> {
			AreaEffectCloud areaEffectCloudEntity = new AreaEffectCloud(entity.getHandle().level(), entity.getX(), entity.getY(), entity.getZ());
			if (entity.getHandle() instanceof LivingEntity) {
				areaEffectCloudEntity.setOwner((LivingEntity) entity.getHandle());
			}
			areaEffectCloudEntity.setRadius(data.getNumberOrDefault("radius", 3.0F).getFloat());
			areaEffectCloudEntity.setRadiusOnUse(data.getNumberOrDefault("radius_on_use", -0.5F).getFloat());
			areaEffectCloudEntity.setWaitTime(data.getNumberOrDefault("wait_time", 10).getInt());
			areaEffectCloudEntity.setRadiusPerTick(-areaEffectCloudEntity.getRadius() / (float) areaEffectCloudEntity.getDuration());
			List<MobEffectInstance> effects = new LinkedList<>();
			if (data.isPresent("effect")) {
				effects.add(CraftPotionUtil.fromBukkit(Util.parsePotionEffect(data.getJsonObject("effect"))));
			}
			if (data.isPresent("effects")) {
				effects.addAll(data.getJsonArray("effects").asJsonObjectList().stream().map(Util::parsePotionEffect).map(CraftPotionUtil::fromBukkit).toList());
			}
			effects.forEach(areaEffectCloudEntity::addEffect);

			entity.getHandle().level().addFreshEntity(areaEffectCloudEntity);
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("extinguish"), (data, entity) -> {
			entity.getHandle().clearFire();
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("execute_command"), (data, entity) -> {
			MinecraftServer server = entity.getHandle().level().getServer();
			if (server != null) {
				boolean validOutput = !(entity.getHandle() instanceof ServerPlayer) || ((ServerPlayer) entity.getHandle()).connection != null;
				CommandSourceStack source = new CommandSourceStack(
					CommandSource.NULL,
					entity.getHandle().position(),
					entity.getHandle().getRotationVector(),
					entity.getHandle().level() instanceof ServerLevel ? (ServerLevel) entity.getHandle().level() : null,
					4,
					entity.getHandle().getName().getString(),
					entity.getHandle().getDisplayName(),
					entity.getHandle().level().getServer(),
					entity.getHandle());
				server.getCommands().performPrefixedCommand(source, data.getString("command"));
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("feed"), (data, entity) -> {
			if (entity.getHandle() instanceof net.minecraft.world.entity.player.Player) {
				((net.minecraft.world.entity.player.Player) entity.getHandle()).getFoodData().eat(data.getNumber("food").getInt(), data.getNumber("saturation").getFloat());
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("add_xp"), (data, entity) -> {
			if (entity.getHandle() instanceof net.minecraft.world.entity.player.Player) {
				int points = data.getNumberOrDefault("points", 0).getInt();
				int levels = data.getNumberOrDefault("levels", 0).getInt();
				if (points > 0) {
					((net.minecraft.world.entity.player.Player) entity.getHandle()).giveExperiencePoints(points);
				}
				((net.minecraft.world.entity.player.Player) entity.getHandle()).giveExperienceLevels(levels);
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("set_fall_distance"), (data, entity) -> {
			entity.getHandle().fallDistance = data.getNumber("fall_distance").getFloat();
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("give"), (data, entity) -> {
			if (entity.getHandle().level().isClientSide) {
				return;
			}

			net.minecraft.world.item.ItemStack stack = CraftItemStack.asNMSCopy(data.getItemStack("stack"));
			if (stack.isEmpty()) {
				return;
			}

			SlotAccess stackReference = Util.createStackReference(stack);
			if (data.isPresent("item_action")) {
				FactoryJsonObject itemAction = data.getJsonObject("item_action");
				Actions.executeItem(stackReference.get().getBukkitStack(), entity.getWorld(), itemAction);
			}

			stack = stackReference.get();

			tryPreferredSlot:
			if (data.isPresent("preferred_slot") && entity.getHandle() instanceof LivingEntity livingEntity) {

				EquipmentSlot preferredSlot = data.getEnumValueOrDefault("preferred_slot", EquipmentSlot.class, null);
				net.minecraft.world.item.ItemStack stackInSlot = livingEntity.getItemBySlot(preferredSlot);

				if (stackInSlot.isEmpty()) {
					livingEntity.setItemSlot(preferredSlot, stack);
					return;
				}

				if (!net.minecraft.world.item.ItemStack.matches(stackInSlot, stack) || stackInSlot.getCount() >= stackInSlot.getMaxStackSize()) {
					break tryPreferredSlot;
				}

				int itemsToGive = Math.min(stackInSlot.getMaxStackSize() - stackInSlot.getCount(), stack.getCount());

				stackInSlot.grow(itemsToGive);
				stack.shrink(itemsToGive);

				if (stack.isEmpty()) {
					return;
				}

			}

			if (entity.getHandle() instanceof net.minecraft.world.entity.player.Player playerEntity) {
				playerEntity.getInventory().placeItemBackInInventory(stack);
			} else {
				Util.throwItem(entity.getHandle(), stack, false, false);
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("equipped_item_action"), (data, entity) -> {
			if (!(entity.getHandle() instanceof LivingEntity livingEntity)) {
				return;
			}

			EquipmentSlot slot = data.getEnumValue("equipment_slot", EquipmentSlot.class);
			FactoryJsonObject itemAction = data.getJsonObject("action");

			SlotAccess stackReference = SlotAccess.forEquipmentSlot(livingEntity, slot);
			Actions.executeItem(stackReference.get().getBukkitStack(), livingEntity.getBukkitEntity().getWorld(), itemAction);
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("emit_game_event"), (data, entity) -> {
			entity.getHandle().gameEvent(
				entity.getHandle().level().registryAccess().registry(net.minecraft.core.registries.Registries.GAME_EVENT)
					.get().getHolder(data.getResourceLocation("event")).orElseThrow()
			);
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("dismount"), (data, entity) -> {
			entity.getHandle().stopRiding();
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("passenger_action"), (data, entity) -> {
			FactoryJsonObject entityAction = data.getJsonObject("action");
			FactoryJsonObject bientityAction = data.getJsonObject("bientity_action");
			FactoryJsonObject cond = data.getJsonObject("bientity_condition");
			if (!entity.getHandle().isVehicle() || (entityAction == null && bientityAction == null)) {
				return;
			}
			Iterable<Entity> passengers = data.getBoolean("recursive") ? entity.getHandle().getIndirectPassengers() : entity.getHandle().getPassengers();
			for (Entity passenger : passengers) {
				if (cond == null || ConditionExecutor.testBiEntity(cond, passenger.getBukkitEntity(), entity)) {
					if (entityAction != null) {
						Actions.executeEntity(passenger.getBukkitEntity(), entityAction);
					}
					if (bientityAction != null) {
						Actions.executeBiEntity(passenger.getBukkitEntity(), entity, bientityAction);
					}
				}
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("riding_action"), (data, entity) -> {
			FactoryJsonObject entityAction = data.getJsonObject("action");
			FactoryJsonObject bientityAction = data.getJsonObject("bientity_action");
			FactoryJsonObject cond = data.getJsonObject("bientity_condition");
			if (!entity.getHandle().isPassenger() || (entityAction == null && bientityAction == null)) {
				return;
			}
			if (data.getBoolean("recursive")) {
				Entity vehicle = entity.getHandle().getVehicle();
				while (vehicle != null) {
					if (cond == null || ConditionExecutor.testBiEntity(cond, entity, vehicle.getBukkitEntity())) {
						if (entityAction != null) {
							Actions.executeEntity(vehicle.getBukkitEntity(), entityAction);
						}
						if (bientityAction != null) {
							Actions.executeBiEntity(entity, vehicle.getBukkitEntity(), bientityAction);
						}
					}
					vehicle = vehicle.getVehicle();
				}
			} else {
				Entity vehicle = entity.getHandle().getVehicle();
				if (cond == null || ConditionExecutor.testBiEntity(cond, entity, vehicle.getBukkitEntity())) {
					if (entityAction != null) {
						Actions.executeEntity(vehicle.getBukkitEntity(), entityAction);
					}
					if (bientityAction != null) {
						Actions.executeBiEntity(entity, vehicle.getBukkitEntity(), bientityAction);
					}
				}
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("area_of_effect"), (data, entity) -> {
			FactoryJsonObject biEntityAction = data.getJsonObject("bientity_action");
			FactoryJsonObject biEntityCondition = data.getJsonObject("bientity_condition");
			Shape shape = data.getEnumValueOrDefault("shape", Shape.class, Shape.CUBE);

			boolean includeActor = data.getBooleanOrDefault("include_actor", false);
			double radius = data.getNumberOrDefault("radius", 16D).getDouble();

			for (Entity target : Shape.getEntities(shape, entity.getHandle().level(), entity.getHandle().getPosition(1.0f), radius)) {
				if (target == entity.getHandle() && !includeActor) {
					continue;
				}

				if ((biEntityCondition == null || biEntityCondition.isEmpty()) || ConditionExecutor.testBiEntity(biEntityAction, entity, target.getBukkitEntity())) {
					Actions.executeBiEntity(entity, target.getBukkitEntity(), biEntityAction);
				}

			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("crafting_table"), (data, entity) -> {
			if (entity instanceof Player player) {
				player.openWorkbench(null, true);
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("ender_chest"), (data, entity) -> {
			if (entity instanceof Player player) {
				player.openInventory(player.getEnderChest());
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("swing_hand"), (data, entity) -> {
			if (entity.getHandle() instanceof LivingEntity living) {
				living.swing(data.getEnumValue("hand", InteractionHand.class), true);
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("raycast"), (data, entity) -> {
			RaycastAction.action(data, entity.getHandle());
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("spawn_particles"), (data, entity) -> {
			if (!(entity.getHandle().level() instanceof ServerLevel serverWorld)) {
				return;
			}

			Vec3 delta = VectorGetter.getNMSVector(data.getJsonObject("spread"))
				.multiply(entity.getHandle().getBbWidth(), entity.getHandle().getEyeHeight(entity.getHandle().getPose()), entity.getHandle().getBbWidth());

			FactoryJsonObject biEntityCondition = data.getJsonObject("bientity_condition");
			ParticleEffect particle = data.transformWithCalio("particle", CalioDataTypes::particleEffect);

			boolean force = data.getBooleanOrDefault("force", false);
			int count = Math.max(0, data.getNumberOrDefault("count", 1).getInt());

			for (ServerPlayer player : serverWorld.players()) {
				if (biEntityCondition == null || ConditionExecutor.testBiEntity(biEntityCondition, entity, player.getBukkitEntity())) {
					player.getBukkitEntity().getWorld().spawnParticle(
						particle.particle().builder().source(player.getBukkitEntity()).force(force).location(player.getBukkitEntity().getLocation()).count(count).particle(),
						new Location(player.getBukkitEntity().getWorld(), player.getBukkitEntity().getEyeLocation().getX(), player.getBukkitEntity().getEyeLocation().getY() - 0.7, player.getBukkitEntity().getEyeLocation().getZ()),
						count, delta.x, delta.y, delta.z, 0, particle.blockData().isEmpty() ? particle.dustOptions().orElse(null) : particle.blockData().orElse(null)
					);
				}
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("modify_inventory"), (data, entity) -> {
			Util.ProcessMode processMode = data.getEnumValueOrDefault("process_mode", Util.ProcessMode.class, Util.ProcessMode.STACKS);
			int limit = data.getNumberOrDefault("limit", 0).getInt();

			Util.modifyInventory(data, entity.getHandle(), null, processMode.getProcessor(), limit);
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("replace_inventory"), (data, entity) -> {
			Util.replaceInventory(data, entity.getHandle(), null);
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("drop_inventory"), (data, entity) -> {
			Util.dropInventory(data, entity.getHandle(), null);
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("modify_death_ticks"), (data, entity) -> {
			if (entity.getHandle() instanceof LivingEntity living) {
				for (Modifier modifier : Modifier.getModifiers(data.getJsonObject("modifier"), data.getJsonArray("modifiers"))) {
					Util.getOperationMappingsInteger().get(modifier.operation()).apply(living.deathTime, Math.round(modifier.value()));
				}
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("modify_stat"), (data, entity) -> {
			if (!(entity.getHandle() instanceof ServerPlayer serverPlayerEntity)) return;

			Stat<?> stat = data.transformWithCalio("stat", CalioDataTypes::stat);
			ServerStatsCounter serverStatHandler = serverPlayerEntity.getStats();

			int newValue;
			int originalValue = serverStatHandler.getValue(stat);

			serverPlayerEntity.resetStat(stat);

			Modifier modifier = new Modifier(data.getJsonObject("modifier"));
			newValue = Util.getOperationMappingsInteger().get(modifier.operation()).apply(originalValue, Math.round(modifier.value()));

			serverPlayerEntity.awardStat(stat, newValue);
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("fire_projectile"), (data, entity) -> {
			if (!(entity.getHandle().level() instanceof ServerLevel serverWorld)) {
				return;
			}

			EntityType<?> entityType = CraftRegistry.getMinecraftRegistry().registry(net.minecraft.core.registries.Registries.ENTITY_TYPE).get().get(data.getResourceLocation("entity_type"));
			CompoundTag entityNbt = data.transformWithCalio("tag", CalioDataTypes::compoundTag, new CompoundTag());
			RandomSource random = serverWorld.getRandom();

			Vec3 rotationVector = entity.getHandle().getLookAngle();
			Vec3 velocity = entity.getHandle().getDeltaMovement();
			Vec3 verticalOffset = entity.getHandle()
				.position()
				.add(0, entity.getHandle().getEyeHeight(entity.getHandle().getPose()), 0);

			float divergence = data.getNumberOrDefault("divergence", 1F).getFloat();
			float speed = data.getNumberOrDefault("speed", 1.5F).getFloat();
			float pitch = entity.getHandle().getXRot();
			float yaw = entity.getHandle().getYRot();

			int count = data.getNumberOrDefault("count", 1).getInt();
			for (int i = 0; i < count; i++) {

				Entity entityToSpawn = Util
					.getEntityWithPassengers(serverWorld, entityType, entityNbt, verticalOffset, yaw, pitch)
					.orElse(null);

				if (entityToSpawn == null) {
					return;
				}

				if (entityToSpawn instanceof Projectile projectileToSpawn) {

					if (projectileToSpawn instanceof AbstractHurtingProjectile explosiveProjectileToSpawn) {
						explosiveProjectileToSpawn.accelerationPower = speed;
					}

					projectileToSpawn.setOwner(entity.getHandle());
					projectileToSpawn.shootFromRotation(entity.getHandle(), pitch, yaw, 0F, speed, divergence);

				} else {

					float j = 0.017453292F;
					double k = 0.007499999832361937D;

					float l = -Mth.sin(yaw * j) * Mth.cos(pitch * j);
					float m = -Mth.sin(pitch * j);
					float n = Mth.cos(yaw * j) * Mth.cos(pitch * j);

					Vec3 entityToSpawnVelocity = new Vec3(l, m, n)
						.normalize()
						.add(random.nextGaussian() * k * divergence, random.nextGaussian() * k * divergence, random.nextGaussian() * k * divergence)
						.scale(speed);

					entityToSpawn.setDeltaMovement(entityToSpawnVelocity);
					entityToSpawn.push(velocity.x, entity.getHandle().onGround() ? 0.0D : velocity.y, velocity.z);

				}

				if (!entityNbt.isEmpty()) {

					CompoundTag mergedNbt = entityToSpawn.saveWithoutId(new CompoundTag());
					mergedNbt.merge(entityNbt);

					entityToSpawn.load(mergedNbt);

				}

				serverWorld.tryAddFreshEntityWithPassengers(entityToSpawn);
				if (data.isPresent("projectile_action")) {
					Actions.executeEntity(entityToSpawn.getBukkitEntity(), data.getJsonObject("projectile_action"));
				}

			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("selector_action"), (data, entity) -> {
			MinecraftServer server = entity.getHandle().level().getServer();
			if (server == null) return;

			EntitySelector selector = null;
			String pattern = data.getString("selector");

			try {
				EntitySelectorParser entitySelectorParser = new EntitySelectorParser(new StringReader(pattern));
				selector = entitySelectorParser.parse();
			} catch (CommandSyntaxException var3) {
				OriginsPaper.getPlugin().getLog4JLogger().warn("Invalid selector component: {}: {}", pattern, var3.getMessage());
			}

			FactoryJsonObject biEntityCondition = data.getJsonObject("bientity_condition");
			FactoryJsonObject biEntityAction = data.getJsonObject("bientity_action");

			CommandSourceStack source = new CommandSourceStack(
				CommandSource.NULL,
				entity.getHandle().position(),
				entity.getHandle().getRotationVector(),
				(ServerLevel) entity.getHandle().level(),
				2,
				entity.getHandle().getScoreboardName(),
				entity.getHandle().getName(),
				server,
				entity.getHandle()
			);

			try {
				selector.findEntities(source)
					.stream()
					.filter(e -> (biEntityCondition == null || biEntityCondition.isEmpty()) || ConditionExecutor.testBiEntity(biEntityCondition, entity, e.getBukkitEntity()))
					.forEach(e -> Actions.executeBiEntity(entity, e.getBukkitEntity(), biEntityAction));
			} catch (CommandSyntaxException ignored) {}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("grant_advancement"), (data, entity) -> {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement grant $1 only $2"
				.replace("$1", entity.getName())
				.replace("$2", data.getString("advancement")));
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("revoke_advancement"), (data, entity) -> {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement revoke $1 only $2"
				.replace("$1", entity.getName())
				.replace("$2", data.getString("advancement")));
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("action_on_entity_set"), (data, entity) -> {
			if (EntitySetPower.isInEntitySet(entity) && EntitySetPower.isInEntitySet(entity, data.getString("set"))) {
				int limit = data.getNumberOrDefault("limit", 0).getInt();
				boolean isLimit = limit != 0;
				List<org.bukkit.entity.Entity> entities = EntitySetPower.entity_sets.get(data.getString("set"));
				if (data.getBooleanOrDefault("reverse", false)) Collections.reverse(entities);
				FactoryJsonObject bientityCondition = data.getJsonObject("bientity_condition");
				FactoryJsonObject bientityAction = data.getJsonObject("bientity_action");

				for (org.bukkit.entity.Entity entity1 : entities) {
					if (isLimit) {
						if (limit == 0) break;
						limit--;
					}

					if (ConditionExecutor.testBiEntity(bientityCondition, entity, entity1)) {
						Actions.executeBiEntity(entity, entity1, bientityAction);
					}
				}
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("random_teleport"), (data, entity) -> {
			if (!(entity.getHandle().level() instanceof ServerLevel serverWorld)) {
				return;
			}

			Predicate<BlockInWorld> landingBlockCondition = data.isPresent("landing_block_condition") ? (blockInWorld) -> ConditionExecutor.testBlock(data.getJsonObject("landing_block_condition"), ((ServerLevel) blockInWorld.getLevel()).getWorld().getBlockAt(CraftLocation.toBukkit(blockInWorld.getPos())))
				: cachedBlockPosition -> cachedBlockPosition.getState().blocksMotion();
			Predicate<Entity> landingCondition = data.isPresent("landing_condition") ? (entity1) -> ConditionExecutor.testEntity(data.getJsonObject("landing_condition"), entity1.getBukkitEntity())
				: _entity -> serverWorld.noCollision(_entity) && !serverWorld.containsAnyLiquid(_entity.getBoundingBox());

			Heightmap.Types heightmap = data.getEnumValueOrDefault("heightmap", Heightmap.Types.class, null);
			RandomSource random = RandomSource.create();
			Vec3 landingOffset = VectorGetter.getNMSVector(data.getJsonObject("landing_offset"));

			boolean loadedChunksOnly = data.getBooleanOrDefault("loaded_chunks_only", true);
			boolean succeeded = false;

			int attempts = data.getNumberOrDefault("attempts", (int) ((data.getNumberOrDefault("area_width", 8D).getDouble() * 2) + (data.getNumberOrDefault("area_height", 8D).getDouble() * 2))).getInt();

			double areaWidth = data.getNumberOrDefault("area_width", 8D).getDouble() * 2;
			double areaHeight = data.getNumberOrDefault("area_height", 8D).getDouble() * 2;
			double x, y, z;

			for (int i = 0; i < attempts; i++) {

				x = entity.getX() + (random.nextDouble() - 0.5) * areaWidth;
				y = Mth.clamp(entity.getY() + (random.nextInt(Math.max((int) areaHeight, 1)) - (areaHeight / 2)), serverWorld.getMinBuildHeight(), serverWorld.getMinBuildHeight() + (serverWorld.getLogicalHeight() - 1));
				z = entity.getZ() + (random.nextDouble() - 0.5) * areaWidth;

				if (Util.attemptToTeleport(entity.getHandle(), serverWorld, x, y, z, landingOffset.x(), landingOffset.y(), landingOffset.z(), areaHeight, loadedChunksOnly, heightmap, landingBlockCondition, landingCondition)) {
					if (data.isPresent("success_action")) {
						Actions.executeEntity(entity, data.getJsonObject("success_action"));
					}
					entity.getHandle().resetFallDistance();

					succeeded = true;
					break;

				}

			}

			if (!succeeded) {
				if (data.isPresent("fail_action")) {
					Actions.executeEntity(entity, data.getJsonObject("fail_action"));
				}
			}
		}));
	}

	public void register(EntityActions.ActionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.ENTITY_ACTION).register(factory);
	}

	public static class ActionFactory implements Registrable {
		NamespacedKey key;
		BiConsumer<FactoryJsonObject, CraftEntity> test;

		public ActionFactory(NamespacedKey key, BiConsumer<FactoryJsonObject, CraftEntity> test) {
			this.key = key;
			this.test = test;
		}

		public void test(FactoryJsonObject action, CraftEntity tester) {
			if (action == null || action.isEmpty()) return; // Dont execute empty actions
			try {
				test.accept(action, tester);
			} catch (Exception e) {
				OriginsPaper.getPlugin().getLogger().severe("An Error occurred while running an action: " + e.getMessage());
				e.printStackTrace();
			}
		}

		@Override
		public NamespacedKey key() {
			return key;
		}
	}
}
