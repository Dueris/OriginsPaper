package me.dueris.originspaper.factory.actions.types;

import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.data.types.DestructionType;
import me.dueris.originspaper.factory.data.types.ExplosionMask;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftWorld;

import java.util.Optional;
import java.util.function.BiConsumer;

public class BlockActions {

	private static <T extends Comparable<T>> void modifyEnumState(ServerLevel world, BlockPos pos, BlockState originalState, Property<T> property, String value) {
		Optional<T> enumValue = property.getValue(value);
		enumValue.ifPresent(v -> world.setBlockAndUpdate(pos, originalState.setValue(property, v)));
	}

	public void register() {
		register(new ActionFactory(OriginsPaper.apoliIdentifier("explode"), (action, location) -> {
			float explosionPower = action.getNumber("power").getFloat();
			String destruction_type = "break";
			boolean create_fire = false;
			ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();

			if (action.isPresent("destruction_type"))
				destruction_type = action.getString("destruction_type");
			if (action.isPresent("create_fire"))
				create_fire = action.getBoolean("create_fire");

			Explosion explosion = new Explosion(
				level,
				null,
				level.damageSources().generic(),
				new ExplosionDamageCalculator(),
				location.getX(),
				location.getY(),
				location.getZ(),
				explosionPower,
				create_fire,
				DestructionType.parse(destruction_type).getNMS(),
				ParticleTypes.EXPLOSION,
				ParticleTypes.EXPLOSION_EMITTER,
				SoundEvents.GENERIC_EXPLODE
			);
			ExplosionMask.getExplosionMask(explosion, level).apply(action, true);
		}));
	}

	public void register(BlockActions.ActionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BLOCK_ACTION).register(factory);
	}

	public static class ActionFactory implements Registrable {
		NamespacedKey key;
		BiConsumer<FactoryJsonObject, Location> test;

		public ActionFactory(NamespacedKey key, BiConsumer<FactoryJsonObject, Location> test) {
			this.key = key;
			this.test = test;
		}

		public void test(FactoryJsonObject action, Location tester) {
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
