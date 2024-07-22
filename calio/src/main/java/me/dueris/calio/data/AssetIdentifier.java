package me.dueris.calio.data;

import me.dueris.calio.registry.Registrable;
import me.dueris.calio.registry.RegistryKey;

public record AssetIdentifier<T extends Registrable>(
	String directory, int priority, String fileType, AssetType assetType, RegistryKey<T> registryKey
) {
	public enum AssetType {
		IMAGE,
		JSON
	}
}
