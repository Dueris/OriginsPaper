package me.dueris.originspaper.factory.actions.types;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.AddToSetEvent;
import me.dueris.originspaper.event.RemoveFromSetEvent;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class BiEntityActions {

	public void register() {
		register(new ActionFactory(OriginsPaper.apoliIdentifier("remove_from_entity_set"), (action, entityPair) -> {
			RemoveFromSetEvent ev = new RemoveFromSetEvent(entityPair.right(), action.getString("set"));
			ev.callEvent();
		}));
		register(new ActionFactory(OriginsPaper.apoliIdentifier("add_to_entity_set"), (action, entityPair) -> {
			AddToSetEvent ev = new AddToSetEvent(entityPair.right(), action.getString("set"));
			ev.callEvent();
		}));
	}

	public void register(BiEntityActions.ActionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BIENTITY_ACTION).register(factory);
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
