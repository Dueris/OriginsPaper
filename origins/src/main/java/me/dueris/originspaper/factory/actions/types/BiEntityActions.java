package me.dueris.originspaper.factory.actions.types;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.calio.util.holders.Pair;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.AddToSetEvent;
import me.dueris.originspaper.event.RemoveFromSetEvent;
import me.dueris.originspaper.factory.data.types.Modifier;
import me.dueris.originspaper.factory.data.types.Space;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.util.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriConsumer;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

// Left is the actor, right is the target.
public class BiEntityActions {

	public void register() {
		register(new ActionFactory(OriginsPaper.apoliIdentifier("remove_from_entity_set"), (data, entityPair) -> {
			RemoveFromSetEvent ev = new RemoveFromSetEvent(entityPair.second(), data.getString("set"));
			ev.callEvent();
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("add_to_entity_set"), (data, entityPair) -> {
			AddToSetEvent ev = new AddToSetEvent(entityPair.second(), data.getString("set"));
			ev.callEvent();
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("damage"), (data, entityPair) -> {
			net.minecraft.world.entity.Entity actor = entityPair.first().getHandle();
			net.minecraft.world.entity.Entity target = entityPair.second().getHandle();

			if (actor == null || target == null) {
				return;
			}

			Float damageAmount = data.getNumber("amount").getFloat();
			List<Modifier> modifiers = List.of(Modifier.getModifiers(data.getJsonObject("modifier"), data.getJsonArray("modifiers")));

			if (!modifiers.isEmpty() && target instanceof LivingEntity) {
				for (Modifier modifier : modifiers) {
					damageAmount = Util.getOperationMappingsFloat().get(modifier.operation()).apply(damageAmount, modifier.value());
				}
			}

			try {
				// "source" field is not supported in OriginsPaper, given it is depreciated.
				DamageSource source;
				if (data.isPresent("damage_type")) {
					source = Util.getDamageSource(Util.DAMAGE_REGISTRY.get(data.getResourceLocation("damage_type")));
				} else {
					source = actor.level().damageSources().generic();
				}
				if (data.isPresent("source") && !data.isPresent("damage_type")) {
					OriginsPaper.getPlugin().getLogger().warning("A \"source\" field was provided in the bientity_action \"apoli:damage\", please use the \"damage_type\" field instead.");
				}
				target.hurt(source, damageAmount);
			} catch (Throwable t) {
				OriginsPaper.getPlugin().getLogger().severe("Error trying to deal damage via the `damage` bi-entity action: " + t.getMessage());
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("add_velocity"), (data, entityPair) -> {
			net.minecraft.world.entity.Entity actor = entityPair.first().getHandle();
			net.minecraft.world.entity.Entity target = entityPair.second().getHandle();

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
			entityPair.second().addPassenger(entityPair.first());
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("set_in_love"), (data, entityPair) -> {
			if (entityPair.second().getHandle() instanceof Animal targetAnimal && entityPair.first().getHandle() instanceof Player actorPlayer) {
				targetAnimal.setInLove(actorPlayer);
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("tame"), (data, entityPair) -> {
			if (!(entityPair.second().getHandle() instanceof TamableAnimal tameableTarget) || !(entityPair.first().getHandle() instanceof Player actorPlayer)) {
				return;
			}

			if (!tameableTarget.isTame()) {
				tameableTarget.tame(actorPlayer);
			}
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("leash"), (data, entityPair) -> {
			net.minecraft.world.entity.Entity actor = entityPair.first().getHandle();
			net.minecraft.world.entity.Entity target = entityPair.second().getHandle();

			if (actor == null || !(target instanceof Mob mobTarget) || !(mobTarget instanceof Leashable leashable)) {
				return;
			}

			if (!mobTarget.isLeashed()) {
				mobTarget.setLeashedTo(actor, true);
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
		ResourceLocation key;
		BiConsumer<FactoryJsonObject, Pair<CraftEntity, CraftEntity>> test;

		public ActionFactory(ResourceLocation key, BiConsumer<FactoryJsonObject, Pair<CraftEntity, CraftEntity>> test) {
			this.key = key;
			this.test = test;
		}

		public void test(FactoryJsonObject action, Pair<CraftEntity, CraftEntity> tester) {
			if (action == null || action.isEmpty()) return; // Dont execute empty actions
			try {
				test.accept(action, tester);
			} catch (Exception e) {
				OriginsPaper.getPlugin().getLogger().severe("An Error occurred while running an action: " + e.getMessage());
				e.printStackTrace();
			}
		}

		@Override
		public ResourceLocation key() {
			return key;
		}
	}
}
