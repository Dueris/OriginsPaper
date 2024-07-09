package me.dueris.originspaper.factory.actions.types;

import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.CraftApoli;
import me.dueris.originspaper.factory.data.types.DestructionType;
import me.dueris.originspaper.factory.data.types.ExplosionMask;
import me.dueris.originspaper.factory.data.types.Modifier;
import me.dueris.originspaper.factory.powers.apoli.Cooldown;
import me.dueris.originspaper.factory.powers.apoli.CooldownPower;
import me.dueris.originspaper.factory.powers.apoli.Resource;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.registry.registries.Layer;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import me.dueris.originspaper.util.entity.PowerUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class EntityActions {

	public void register() {
		register(new ActionFactory(OriginsPaper.apoliIdentifier("change_resource"), (action, entity) -> {
			Optional<Resource.Bar> resourceBar = Resource.getDisplayedBar(entity, action.getString("resource"));
			resourceBar.ifPresent((bar) -> {
				int change = action.getNumber("change").getInt();
				String operation = action.getStringOrDefault("operation", "add");
				bar.change(change, operation);
			});
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("set_resource"), (action, entity) -> {
			Optional<Resource.Bar> resourceBar = Resource.getDisplayedBar(entity, action.getString("resource"));
			resourceBar.ifPresent((bar) -> {
				int val = action.getNumber("value").getInt();
				bar.change(val, "set");
			});
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("modify_resource"), (action, entity) -> {
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
		register(new ActionFactory(OriginsPaper.apoliIdentifier("remove_power"), (action, entity) -> {
			if (entity instanceof Player p) {
				PowerType powerContainer = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(action.getNamespacedKey("power"));
				if (powerContainer == null) {
					OriginsPaper.getPlugin().getLogger().severe("Searched PowerType was null when attempting to revoke a power: {}".replace("{}", action.getString("power")));
					return;
				}
				Layer layer = CraftApoli.getLayerFromTag(action.getString("source"));
				try {
					PowerUtils.removePower(Bukkit.getConsoleSender(), powerContainer, p, layer, false);
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("grant_power"), (action, entity) -> {
			if (entity instanceof Player p) {
				PowerType powerContainer = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(action.getNamespacedKey("power"));
				if (powerContainer == null) {
					OriginsPaper.getPlugin().getLogger().severe("Searched PowerType was null when attempting to apply a new power: {}".replace("{}", action.getString("power")));
					return;
				}
				Layer layer = CraftApoli.getLayerFromTag(action.getString("source"));
				try {
					PowerUtils.grantPower(Bukkit.getConsoleSender(), powerContainer, p, layer, false);
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("revoke_power"), (action, entity) -> {
			if (entity instanceof Player p) {
				PowerType powerContainer = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(action.getNamespacedKey("power"));
				if (powerContainer == null) {
					OriginsPaper.getPlugin().getLogger().severe("Searched PowerType was null when attempting to revoke a power: {}".replace("{}", action.getString("power")));
					return;
				}
				Layer layer = CraftApoli.getLayerFromTag(action.getString("source"));
				try {
					PowerUtils.removePower(Bukkit.getConsoleSender(), powerContainer, p, layer, false);
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("explode"), (action, entity) -> {
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
		register(new ActionFactory(OriginsPaper.apoliIdentifier("show_toast"), (action, entity) -> {
			String title = action.getString("title");
			String description = action.getString("description");
			@Nullable ItemStack icon = action.isPresent("icon") ? action.getItemStack("icon") : new ItemStack(Material.PLAYER_HEAD);

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
		register(new ActionFactory(OriginsPaper.apoliIdentifier("trigger_cooldown"), (action, entity) -> {
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
	}

	public void register(EntityActions.ActionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.ENTITY_ACTION).register(factory);
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
