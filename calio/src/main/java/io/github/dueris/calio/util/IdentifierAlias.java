package io.github.dueris.calio.util;

import io.github.dueris.calio.CraftCalio;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * <p>A utility class used for adding aliases to ResourceLocations and/or its namespace and/or path, which can be used for substituting an
 * {@link ResourceLocation} (or its namespace and/or path).</p>
 *
 * <p>This can be used in cases, such as serializing objects with {@linkplain com.mojang.serialization.Codec#dispatchMap(Function, Function) codec map dispatcher}
 * (usually queried via {@link Registry#byNameCodec()})</p>
 */
public class IdentifierAlias {

	public static final IdentifierAlias GLOBAL = new IdentifierAlias();
	protected static final Function<ResourceLocation, RuntimeException> NO_ALIAS_EXCEPTION = id -> new RuntimeException("Tried resolving non-existent alias for id \"" + id + "\"");
	protected final Map<ResourceLocation, ResourceLocation> IdentifierAliases = new HashMap<>();
	protected final Map<String, String> namespaceAliases = new HashMap<>();
	protected final Map<String, String> pathAliases = new HashMap<>();

	public void addAlias(ResourceLocation fromId, ResourceLocation toId) {

		if (IdentifierAliases.containsKey(fromId)) {
			ResourceLocation owner = IdentifierAliases.get(fromId);
			CraftCalio.LOGGER.error("Couldn't add alias \"{}\" to ResourceLocation \"{}\": {}", fromId, toId, (owner.equals(toId) ? "it's already defined!" : "it's already defined for a different ResourceLocation: \"" + owner + "\""));
		} else {
			IdentifierAliases.put(fromId, toId);
		}

	}

	public void addNamespaceAlias(String fromNamespace, String toNamespace) {

		if (namespaceAliases.containsKey(fromNamespace)) {
			String owner = namespaceAliases.get(fromNamespace);
			CraftCalio.LOGGER.error("Couldn't add alias \"{}\" to namespace \"{}\": {}", fromNamespace, toNamespace, (owner.equals(toNamespace) ? "it's already defined!" : "it's already defined for a different namespace: \"" + owner + "\""));
		} else {
			namespaceAliases.put(fromNamespace, toNamespace);
		}

	}

	public void addPathAlias(String fromPath, String toPath) {

		if (pathAliases.containsKey(fromPath)) {
			String owner = pathAliases.get(fromPath);
			CraftCalio.LOGGER.error("Couldn't add alias \"{}\" to path \"{}\": {}", fromPath, toPath, (owner.equals(toPath) ? "it's already defined!" : "it's already defined for a different path: \"" + owner + "\""));
		} else {
			pathAliases.put(fromPath, toPath);
		}

	}

	public boolean hasIdentifierAlias(ResourceLocation id) {
		return IdentifierAliases.containsKey(id)
			|| (this != GLOBAL && GLOBAL.hasIdentifierAlias(id));
	}

	public boolean hasNamespaceAlias(ResourceLocation id) {
		String namespace = id.getNamespace();
		return namespaceAliases.containsKey(namespace)
			|| (this != GLOBAL && GLOBAL.hasNamespaceAlias(id));
	}

	public boolean hasPathAlias(ResourceLocation id) {
		String path = id.getPath();
		return pathAliases.containsKey(path)
			|| (this != GLOBAL && GLOBAL.hasPathAlias(id));
	}

	public boolean hasAlias(ResourceLocation id) {
		return this.hasIdentifierAlias(id)
			|| this.hasNamespaceAlias(id)
			|| this.hasPathAlias(id);
	}

	public ResourceLocation resolveIdentifierAlias(ResourceLocation id, boolean strict) {

		if (IdentifierAliases.containsKey(id)) {
			return IdentifierAliases.get(id);
		} else if (this != GLOBAL) {
			return GLOBAL.resolveIdentifierAlias(id, strict);
		} else if (strict) {
			throw NO_ALIAS_EXCEPTION.apply(id);
		} else {
			return id;
		}

	}

	public ResourceLocation resolveNamespaceAlias(ResourceLocation id, boolean strict) {

		String namespace = id.getNamespace();
		if (namespaceAliases.containsKey(namespace)) {
			return ResourceLocation.fromNamespaceAndPath(namespaceAliases.get(namespace), id.getPath());
		} else if (this != GLOBAL) {
			return GLOBAL.resolveNamespaceAlias(id, strict);
		} else if (strict) {
			throw NO_ALIAS_EXCEPTION.apply(id);
		} else {
			return id;
		}

	}

	public ResourceLocation resolvePathAlias(ResourceLocation id, boolean strict) {

		String path = id.getPath();
		if (pathAliases.containsKey(path)) {
			return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), pathAliases.get(path));
		} else if (this != GLOBAL) {
			return GLOBAL.resolvePathAlias(id, strict);
		} else if (strict) {
			throw NO_ALIAS_EXCEPTION.apply(id);
		} else {
			return id;
		}

	}

	public ResourceLocation resolveAlias(ResourceLocation id, Predicate<ResourceLocation> untilPredicate) {

		ResourceLocation aliasedId;
		for (Resolver resolver : Resolver.values()) {

			aliasedId = resolver.apply(this, id);

			if (untilPredicate.test(aliasedId)) {
				return aliasedId;
			}

		}

		return id;

	}

	public enum Resolver implements BiFunction<IdentifierAlias, ResourceLocation, ResourceLocation> {

		NO_OP {
			@Override
			public ResourceLocation apply(IdentifierAlias aliases, ResourceLocation id) {
				return id;
			}

		},

		IDENTIFIER {
			@Override
			public ResourceLocation apply(IdentifierAlias aliases, ResourceLocation id) {
				return aliases.resolveIdentifierAlias(id, false);
			}

		},

		NAMESPACE {
			@Override
			public ResourceLocation apply(IdentifierAlias aliases, ResourceLocation id) {
				return aliases.resolveNamespaceAlias(id, false);
			}

		},

		PATH {
			@Override
			public ResourceLocation apply(IdentifierAlias aliases, ResourceLocation id) {
				return aliases.resolvePathAlias(id, false);
			}

		},

		NAMESPACE_AND_PATH {
			@Override
			public ResourceLocation apply(IdentifierAlias aliases, ResourceLocation id) {

				String aliasedNamespace = aliases.resolveNamespaceAlias(id, false).getNamespace();
				String aliasedPath = aliases.resolvePathAlias(id, false).getPath();

				return ResourceLocation.fromNamespaceAndPath(aliasedNamespace, aliasedPath);

			}

		}

	}

}

