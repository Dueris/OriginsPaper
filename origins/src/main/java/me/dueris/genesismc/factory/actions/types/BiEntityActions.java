package me.dueris.genesismc.factory.actions.types;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.AddToSetEvent;
import me.dueris.genesismc.event.RemoveFromSetEvent;
import me.dueris.genesismc.factory.data.types.Space;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.util.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriConsumer;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class BiEntityActions {

	public void register() {
		register(new ActionFactory(GenesisMC.apoliIdentifier("add_velocity"), (action, entityPair) -> {
			net.minecraft.world.entity.Entity actor = entityPair.left().getHandle();
			net.minecraft.world.entity.Entity target = entityPair.right().getHandle();

			if ((actor == null || target == null) || (target instanceof Player && (!action.getBooleanOrDefault("server", true)))) {
				return;
			}
			float xVal = action.getNumberOrDefault("x", 0.0F).getFloat();
			float yVal = action.getNumberOrDefault("y", 0.0F).getFloat();
			float zVal = action.getNumberOrDefault("z", 0.0F).getFloat();

			Vector3f vec = new Vector3f(xVal, yVal, zVal);
			TriConsumer<Float, Float, Float> method = action.getBooleanOrDefault("set", false) ? (x, y, z) -> {
				target.getBukkitEntity().setVelocity(new Vector(x, y, z));
			} : (x, y, z) -> {
				target.getBukkitEntity().setVelocity(target.getBukkitEntity().getVelocity().add(new Vector(x, y, z)));
			};

			Reference reference = action.getEnumValueOrDefault("reference", Reference.class, Reference.POSITION);
			Vec3 refVec = reference.apply(actor, target);

			Space.transformVectorToBase(refVec, vec, actor.getBukkitEntity().getYaw(), true); // vector normalized by method
			method.accept(vec.x, vec.y, vec.z);

			target.hurtMarked = true;
		}));
		register(new ActionFactory(GenesisMC.apoliIdentifier("remove_from_entity_set"), (action, entityPair) -> {
			RemoveFromSetEvent ev = new RemoveFromSetEvent(entityPair.right(), action.getString("set"));
			ev.callEvent();
		}));
		register(new ActionFactory(GenesisMC.apoliIdentifier("add_to_entity_set"), (action, entityPair) -> {
			AddToSetEvent ev = new AddToSetEvent(entityPair.right(), action.getString("set"));
			ev.callEvent();
		}));
		register(new ActionFactory(GenesisMC.apoliIdentifier("damage"), (action, entityPair) -> {
			if (entityPair.right().isDead() || !(entityPair.right() instanceof LivingEntity)) return;
			float amount = 0.0f;

			if (action.isPresent("amount"))
				amount = action.getNumber("amount").getFloat();

			NamespacedKey key = NamespacedKey.fromString(action.getStringOrDefault("damage_type", "generic"));
			DamageType dmgType = Util.DAMAGE_REGISTRY.get(CraftNamespacedKey.toMinecraft(key));
			net.minecraft.world.entity.LivingEntity serverEn = ((CraftLivingEntity) entityPair.right()).getHandle();
			serverEn.hurt(Util.getDamageSource(dmgType), amount);
		}));
		register(new ActionFactory(GenesisMC.apoliIdentifier("set_in_love"), (action, entityPair) -> {
			if (entityPair.right() instanceof Animals targetAnimal) {
				targetAnimal.setLoveModeTicks(600);
			}
		}));
		register(new ActionFactory(GenesisMC.apoliIdentifier("mount"), (action, entityPair) -> entityPair.right().addPassenger(entityPair.left())));
		register(new ActionFactory(GenesisMC.apoliIdentifier("tame"), (action, entityPair) -> {
			if (entityPair.right() instanceof Tameable targetTameable && entityPair.left() instanceof AnimalTamer actorTamer) {
				targetTameable.setOwner(actorTamer);
			}
		}));
	}

	private void register(BiEntityActions.ActionFactory factory) {
		GenesisMC.getPlugin().registry.retrieve(Registries.BIENTITY_ACTION).register(factory);
	}

	public enum Reference {

		POSITION((actor, target) -> target.position().subtract(actor.position())),
		ROTATION((actor, target) -> {

			float pitch = actor.getBukkitEntity().getPitch();
			float yaw = actor.getBukkitEntity().getYaw();

			float i = 0.017453292F;

			float j = -Mth.sin(yaw * i) * Mth.cos(pitch * i);
			float k = -Mth.sin(pitch * i);
			float l = Mth.cos(yaw * i) * Mth.cos(pitch * i);

			return new Vec3(j, k, l);

		});

		final BiFunction<net.minecraft.world.entity.Entity, net.minecraft.world.entity.Entity, Vec3> refFunction;

		Reference(BiFunction<net.minecraft.world.entity.Entity, net.minecraft.world.entity.Entity, Vec3> refFunction) {
			this.refFunction = refFunction;
		}

		public Vec3 apply(net.minecraft.world.entity.Entity actor, net.minecraft.world.entity.Entity target) {
			return refFunction.apply(actor, target);
		}

	}

	public static class ActionFactory implements Registrable {
		NamespacedKey key;
		BiConsumer<FactoryJsonObject, Pair<CraftEntity, CraftEntity>> test;

		public ActionFactory(NamespacedKey key, BiConsumer<FactoryJsonObject, Pair<CraftEntity, CraftEntity>> test) {
			this.key = key;
			this.test = test;
		}

		public void test(FactoryJsonObject action, Pair<Entity, Entity> tester) {
			if (action == null || action.isEmpty()) return; // Dont execute empty actions
			try {
				Pair<CraftEntity, CraftEntity> newTester = new Pair<>() {
					@Override
					public CraftEntity left() {
						return (CraftEntity) tester.left();
					}

					@Override
					public CraftEntity right() {
						return (CraftEntity) tester.right();
					}
				};
				test.accept(action, newTester);
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
