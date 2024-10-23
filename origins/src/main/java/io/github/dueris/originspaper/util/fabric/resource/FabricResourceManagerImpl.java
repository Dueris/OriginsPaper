package io.github.dueris.originspaper.util.fabric.resource;

import com.google.common.collect.Lists;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.util.fabric.IdentifiableResourceReloadListener;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FabricResourceManagerImpl {

	private static final Map<PackType, FabricResourceManagerImpl> registryMap = new HashMap<>();
	private static final List<IdentifiableResourceReloadListener> addedListeners = new LinkedList<>();

	public static FabricResourceManagerImpl get(PackType type) {
		return registryMap.computeIfAbsent(type, (t) -> new FabricResourceManagerImpl());
	}

	public static List<PreparableReloadListener> sort(PackType type, List<PreparableReloadListener> listeners) {
		FabricResourceManagerImpl instance = get(type == null ? PackType.SERVER_DATA : type);

		if (instance != null) {
			OriginsPaper.LOGGER.info("Loaded mutable resource manager successfully!");
			List<PreparableReloadListener> mutable = new ArrayList<>(listeners);
			instance.sort(mutable);
			return Collections.unmodifiableList(mutable);
		}

		return listeners;
	}

	public static void registerResourceReload(IdentifiableResourceReloadListener listener) {
		addedListeners.add(listener);
	}

	protected void sort(@NotNull List<PreparableReloadListener> listeners) {
		listeners.removeAll(addedListeners);

		List<IdentifiableResourceReloadListener> listenersToAdd = Lists.newArrayList(addedListeners);

		listeners.addAll(listenersToAdd);
	}
}
