package me.dueris.originspaper.factory.actions.types;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.AddToSetEvent;
import me.dueris.originspaper.event.RemoveFromSetEvent;
import me.dueris.originspaper.factory.data.types.Space;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.util.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriConsumer;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.joml.Vector3f;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

// Left is the actor, right is the target.
public class BiEntityActions {

	public void register() {
		register(new ActionFactory(OriginsPaper.apoliIdentifier("remove_from_entity_set"), (data, entityPair) -> {
			RemoveFromSetEvent ev = new RemoveFromSetEvent(entityPair.right(), data.getString("set"));
			ev.callEvent();
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("add_to_entity_set"), (data, entityPair) -> {
			AddToSetEvent ev = new AddToSetEvent(entityPair.right(), data.getString("set"));
			ev.callEvent();
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("damage"), (data, entityPair) -> {
			if (entityPair.right().isDead() || !(entityPair.right() instanceof LivingEntity)) return;
			float amount = 0.0f;

			if (data.isPresent("amount"))
				amount = data.getNumber("amount").getFloat();

			NamespacedKey key = NamespacedKey.fromString(data.getStringOrDefault("damage_type", "generic"));
			DamageType dmgType = Util.DAMAGE_REGISTRY.get(CraftNamespacedKey.toMinecraft(key));
			net.minecraft.world.entity.LivingEntity serverEn = ((CraftLivingEntity) entityPair.right()).getHandle();
			serverEn.hurt(Util.getDamageSource(dmgType), amount);
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("add_velocity"), (data, entityPair) -> {
			net.minecraft.world.entity.Entity actor = entityPair.left().getHandle();
			net.minecraft.world.entity.Entity target = entityPair.right().getHandle();

			if ((actor == null || target == null)) {
				return;
			}

			Vector3f vec = new Vector3f(data.getNumberOrDefault("x", 0F).getFloat(), data.getNumberOrDefault("y", 0F).getFloat(), data.getNumberOrDefault("z", 0F).getFloat());
			TriConsumer<Float, Float, Float> method = data.getBooleanOrDefault("set", false) ? target::setDeltaMovement : target::push;

			Reference reference = data.getEnumValueOrDefault("reference", Reference.class, Reference.POSITION);
			Vec3 refVec = reference.apply(actor, target);

			Space.transformVectorToBase(refVec, vec, actor.getYRot(), true);
			method.accept(vec.x, vec.y, vec.z);

			target.hurtMarked = true;
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("mount"), (data, entityPair) -> {
			entityPair.right().addPassenger(entityPair.left());
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("set_in_love"), (data, entityPair) -> {
			if (entityPair.right().getHandle() instanceof Animal targetAnimal && entityPair.left().getHandle() instanceof Player actorPlayer) {
				targetAnimal.setInLove(actorPlayer);
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("tame"), (data, entityPair) -> {
			if (!(entityPair.right().getHandle() instanceof TamableAnimal tameableTarget) || !(entityPair.left().getHandle() instanceof Player actorPlayer)) {
				return;
			}

			if (!tameableTarget.isTame()) {
				tameableTarget.tame(actorPlayer);
			}
		}));
	}

	public void register(BiEntityActions.ActionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BIENTITY_ACTION).register(factory);
	}

	public enum Reference {

		POSITION((actor, target) -> target.position().subtract(actor.position())),
		ROTATION((actor, target) -> {

			float pitch = actor.getXRot();
			float yaw = actor.getYRot();

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
