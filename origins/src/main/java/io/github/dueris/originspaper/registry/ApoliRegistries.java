package io.github.dueris.originspaper.registry;

import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.types.modifier.IModifierOperation;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.registry.fabric.FabricRegistryBuilder;
import io.github.dueris.originspaper.screen.ChoosingPage;
import io.github.dueris.originspaper.util.LangFile;
import io.netty.util.internal.UnstableApi;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.FluidState;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ApoliRegistries {

	private static final int STORAGE_CAPACITY = 100000;

	public static final Registry<ConditionTypeFactory<Entity>> ENTITY_CONDITION = create(ApoliRegistryKeys.ENTITY_CONDITION);
	public static final Registry<ConditionTypeFactory<Tuple<Entity, Entity>>> BIENTITY_CONDITION = create(ApoliRegistryKeys.BIENTITY_CONDITION);
	public static final Registry<ConditionTypeFactory<Tuple<Level, ItemStack>>> ITEM_CONDITION = create(ApoliRegistryKeys.ITEM_CONDITION);
	public static final Registry<ConditionTypeFactory<BlockInWorld>> BLOCK_CONDITION = create(ApoliRegistryKeys.BLOCK_CONDITION);
	public static final Registry<ConditionTypeFactory<Tuple<DamageSource, Float>>> DAMAGE_CONDITION = create(ApoliRegistryKeys.DAMAGE_CONDITION);
	public static final Registry<ConditionTypeFactory<FluidState>> FLUID_CONDITION = create(ApoliRegistryKeys.FLUID_CONDITION);
	public static final Registry<ConditionTypeFactory<Tuple<BlockPos, Holder<Biome>>>> BIOME_CONDITION = create(ApoliRegistryKeys.BIOME_CONDITION);

	public static final Registry<ActionTypeFactory<Entity>> ENTITY_ACTION = create(ApoliRegistryKeys.ENTITY_ACTION);
	public static final Registry<ActionTypeFactory<Tuple<Level, SlotAccess>>> ITEM_ACTION = create(ApoliRegistryKeys.ITEM_ACTION);
	public static final Registry<ActionTypeFactory<Triple<Level, BlockPos, Direction>>> BLOCK_ACTION = create(ApoliRegistryKeys.BLOCK_ACTION);
	public static final Registry<ActionTypeFactory<Tuple<Entity, Entity>>> BIENTITY_ACTION = create(ApoliRegistryKeys.BIENTITY_ACTION);

	public static final Registry<IModifierOperation> MODIFIER_OPERATION = create(ApoliRegistryKeys.MODIFIER_OPERATION);
	public static final Registry<ChoosingPage> CHOOSING_PAGE = create(ApoliRegistryKeys.CHOOSING_PAGE);
	public static final Registry<LangFile> LANG = create(ApoliRegistryKeys.LANG);

	public static final Registry<Origin> ORIGIN = create(ApoliRegistryKeys.ORIGIN);
	public static final Registry<OriginLayer> ORIGIN_LAYER = create(ApoliRegistryKeys.ORIGIN_LAYER);
	public static final Registry<PowerType> POWER = create(ApoliRegistryKeys.POWER);

	private static <T> Registry<T> create(ResourceKey<Registry<T>> registryKey) {
		MappedRegistry<T> registry = FabricRegistryBuilder.createSimple(registryKey).buildAndRegister();
		try {
			increaseRegistryCapacity(registry, STORAGE_CAPACITY);
		} catch (Exception e) {
			throw new RuntimeException("Unable to increase registry capacity!", e);
		}
		return registry;
	}

	@UnstableApi
	public static void clearRegistries() throws Exception {
		increaseRegistryCapacity(ENTITY_CONDITION, STORAGE_CAPACITY);
		increaseRegistryCapacity(BIENTITY_CONDITION, STORAGE_CAPACITY);
		increaseRegistryCapacity(ITEM_CONDITION, STORAGE_CAPACITY);
		increaseRegistryCapacity(BLOCK_CONDITION, STORAGE_CAPACITY);
		increaseRegistryCapacity(DAMAGE_CONDITION, STORAGE_CAPACITY);
		increaseRegistryCapacity(FLUID_CONDITION, STORAGE_CAPACITY);
		increaseRegistryCapacity(BIOME_CONDITION, STORAGE_CAPACITY);
		increaseRegistryCapacity(ENTITY_ACTION, STORAGE_CAPACITY);
		increaseRegistryCapacity(ITEM_ACTION, STORAGE_CAPACITY);
		increaseRegistryCapacity(BLOCK_ACTION, STORAGE_CAPACITY);
		increaseRegistryCapacity(BIENTITY_ACTION, STORAGE_CAPACITY);
		increaseRegistryCapacity(MODIFIER_OPERATION, STORAGE_CAPACITY);
		increaseRegistryCapacity(ORIGIN, STORAGE_CAPACITY);
		increaseRegistryCapacity(ORIGIN_LAYER, STORAGE_CAPACITY);
		increaseRegistryCapacity(POWER, STORAGE_CAPACITY);
	}

	private static void increaseRegistryCapacity(Registry<?> registry, int storageCapacity) {
		if (registry instanceof MappedRegistry<?> mappedRegistry) {
			try {
				increaseRegistryCapacity(mappedRegistry, storageCapacity);
			} catch (Exception e) {
				throw new RuntimeException("Unable to increase registry capacity!", e);
			}
		}
	}

	public static <T> void increaseRegistryCapacity(MappedRegistry<T> registry, int newCapacity) throws Exception {
		setField(registry, "toId", Util.make(new Reference2IntOpenHashMap<>(newCapacity), map -> map.defaultReturnValue(-1)));
		setField(registry, "byLocation", new ConcurrentHashMap<ResourceKey<T>, Holder.Reference<T>>(newCapacity));
		setField(registry, "byKey", new ConcurrentHashMap<ResourceKey<T>, Holder.Reference<T>>(newCapacity));
		setField(registry, "byValue", new IdentityHashMap<T, Holder.Reference<T>>(newCapacity));
		setField(registry, "registrationInfos", new IdentityHashMap<ResourceKey<T>, RegistrationInfo>(newCapacity));
	}

	private static void setField(@NotNull Object target, String fieldName, Object newValue) throws Exception {
		Field field = MappedRegistry.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, Objects.requireNonNull(newValue));
	}

	public static void unfreeze(Registry<?> registry) {
		if (registry instanceof MappedRegistry<?> mappedRegistry) {
			try {
				Field field = MappedRegistry.class.getDeclaredField("frozen");
				field.setAccessible(true);
				field.set(mappedRegistry, false);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException("Unable to unfreeze registry!", e);
			}
		}
	}
}
