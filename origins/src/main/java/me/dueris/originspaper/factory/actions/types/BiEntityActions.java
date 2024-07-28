package me.dueris.originspaper.factory.actions.types;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.util.holder.Pair;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.ActionFactory;
import me.dueris.originspaper.factory.actions.meta.*;
import me.dueris.originspaper.factory.data.ApoliDataTypes;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.world.entity.Entity;

// Left is the actor, right is the target.
public class BiEntityActions {

	public static void register(ActionFactory<Pair<Entity, Entity>> factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BIENTITY_ACTION).register(factory, factory.getSerializerId());
	}

	public static void registerAll() {
		register(AndAction.getFactory(SerializableDataTypes.list(ApoliDataTypes.BIENTITY_ACTION)));
		register(ChanceAction.getFactory(ApoliDataTypes.BIENTITY_ACTION));
		register(IfElseAction.getFactory(ApoliDataTypes.BIENTITY_ACTION, ApoliDataTypes.BIENTITY_CONDITION));
		register(ChoiceAction.getFactory(ApoliDataTypes.BIENTITY_ACTION));
		register(IfElseListAction.getFactory(ApoliDataTypes.BIENTITY_ACTION, ApoliDataTypes.BIENTITY_CONDITION));
		register(DelayAction.getFactory(ApoliDataTypes.BIENTITY_ACTION));
		register(NothingAction.getFactory());
		register(SideAction.getFactory(ApoliDataTypes.BIENTITY_ACTION, entities -> !entities.getLeft().level().isClientSide));
		/*register(new ActionFactory(OriginsPaper.apoliIdentifier("remove_from_entity_set"), (data, entityPair) -> {
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
		}));*/
	}

}
