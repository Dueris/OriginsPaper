/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.dueris.originspaper.registry.fabric;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * OriginsPaper - Ported from the fabric registry api<br><br>
 * Used to create custom registries, with specified registry attributes.
 *
 * <p>See the following example for creating a {@link Registry} of String objects.
 *
 * <pre>
 * {@code
 *  RegistryKey<Registry<String>> registryKey = RegistryKey.ofRegistry(Identifier.of("modid", "registry_name"));
 *  Registry<String> registry = FabricRegistryBuilder.createSimple(registryKey)
 * 													.attribute(RegistryAttribute.SYNCED)
 * 													.buildAndRegister();
 *    }
 * </pre>
 *
 * <p>Tags for the entries of a custom registry must be placed in
 * {@code /tags/<registry namespace>/<registry path>/}. For example, the tags for the example
 * registry above would be placed in {@code /tags/modid/registry_name/}.
 *
 * @param <T> The type stored in the Registry
 * @param <R> The registry type
 */
public final class FabricRegistryBuilder<T, R extends WritableRegistry<T>> {
	private final R registry;

	private FabricRegistryBuilder(R registry) {
		this.registry = registry;
	}

	/**
	 * Create a new {@link FabricRegistryBuilder}.
	 *
	 * @param registry The base registry type such as {@link MappedRegistry} or {@link DefaultedRegistry}
	 * @param <T>      The type stored in the Registry
	 * @param <R>      The registry type
	 * @return An instance of FabricRegistryBuilder
	 */
	public static <T, R extends WritableRegistry<T>> @NotNull FabricRegistryBuilder<T, R> from(R registry) {
		return new FabricRegistryBuilder<>(registry);
	}

	/**
	 * Create a new {@link FabricRegistryBuilder} using a {@link MappedRegistry}.
	 *
	 * @param registryKey The registry {@link ResourceKey}
	 * @param <T>         The type stored in the Registry
	 * @return An instance of FabricRegistryBuilder
	 */
	public static <T> @NotNull FabricRegistryBuilder<T, MappedRegistry<T>> createSimple(ResourceKey<Registry<T>> registryKey) {
		return from(new MappedRegistry<>(registryKey, Lifecycle.stable(), false));
	}

	/**
	 * Create a new {@link FabricRegistryBuilder} using a {@link DefaultedRegistry}.
	 *
	 * @param registryKey The registry {@link ResourceKey}
	 * @param defaultId   The default registry id
	 * @param <T>         The type stored in the Registry
	 * @return An instance of FabricRegistryBuilder
	 */
	public static <T> @NotNull FabricRegistryBuilder<T, DefaultedMappedRegistry<T>> createDefaulted(ResourceKey<Registry<T>> registryKey, @NotNull ResourceLocation defaultId) {
		return from(new DefaultedMappedRegistry<T>(defaultId.toString(), registryKey, Lifecycle.stable(), false));
	}

	/**
	 * Create a new {@link FabricRegistryBuilder} using a {@link MappedRegistry}.
	 *
	 * @param registryId The registry {@link ResourceLocation} used as the registry id
	 * @param <T>        The type stored in the Registry
	 * @return An instance of FabricRegistryBuilder
	 * @deprecated Please migrate to {@link FabricRegistryBuilder#createSimple(ResourceKey)}
	 */
	@Deprecated
	public static <T> @NotNull FabricRegistryBuilder<T, MappedRegistry<T>> createSimple(Class<T> type, ResourceLocation registryId) {
		return createSimple(ResourceKey.createRegistryKey(registryId));
	}

	/**
	 * Create a new {@link FabricRegistryBuilder} using a {@link DefaultedRegistry}.
	 *
	 * @param registryId The registry {@link ResourceLocation} used as the registry id
	 * @param defaultId  The default registry id
	 * @param <T>        The type stored in the Registry
	 * @return An instance of FabricRegistryBuilder
	 * @deprecated Please migrate to {@link FabricRegistryBuilder#createDefaulted(ResourceKey, ResourceLocation)}
	 */
	@Deprecated
	public static <T> @NotNull FabricRegistryBuilder<T, DefaultedMappedRegistry<T>> createDefaulted(Class<T> type, ResourceLocation registryId, ResourceLocation defaultId) {
		return createDefaulted(ResourceKey.createRegistryKey(registryId), defaultId);
	}

	/**
	 * Applies the attributes to the registry and registers it.
	 *
	 * @return the registry instance with the attributes applied
	 */
	public R buildAndRegister() {
		final ResourceKey<?> key = registry.key();

		//noinspection unchecked
		((WritableRegistry<WritableRegistry<?>>) BuiltInRegistries.REGISTRY).register((ResourceKey<WritableRegistry<?>>) key, registry, RegistrationInfo.BUILT_IN);

		return registry;
	}
}
