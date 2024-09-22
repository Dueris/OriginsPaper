package io.github.dueris.originspaper.storage;

import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class RepositoryComponent implements Iterable<AtomicReference<Origin>> {
	protected final ConcurrentHashMap<OriginLayer, AtomicReference<Origin>> originRepo = new ConcurrentHashMap<>();
	protected final ConcurrentHashMap<ResourceLocation, List<PowerType>> powerRepo = new ConcurrentHashMap<>();

	public int originSize() {
		return originRepo.size();
	}

	public boolean originIsEmpty() {
		return originRepo.isEmpty();
	}

	public @NotNull Set<OriginLayer> getOriginLayers() {
		return originRepo.keySet();
	}

	public Origin getOrigin(OriginLayer layer) {
		return originRepo.getOrDefault(layer, new AtomicReference<>(Origin.EMPTY)).get();
	}

	public @NotNull Origin getOriginOrDefault(@NotNull Origin def, OriginLayer layer) {
		return originRepo.getOrDefault(layer, new AtomicReference<>(def)).get();
	}

	public void setOrigin(Origin origin, OriginLayer layer) {
		originRepo.putIfAbsent(layer, new AtomicReference<>(Origin.EMPTY));
		originRepo.get(layer).set(origin);
	}

	public @Unmodifiable @NotNull List<Origin> getAllOrigins() {
		return originRepo.values().stream()
			.map(AtomicReference::get)
			.toList();
	}

	public int powerSize() {
		return powerRepo.size();
	}

	public boolean powerIsEmpty() {
		return powerRepo.isEmpty();
	}

	public @NotNull Set<ResourceLocation> getPowerSources() {
		return powerRepo.keySet();
	}

	public @Unmodifiable @NotNull List<PowerType> getPowers(ResourceLocation location) {
		if (!powerRepo.containsKey(location)) {
			powerRepo.put(location, new CopyOnWriteArrayList<>());
		}
		return powerRepo.get(location);
	}

	public void addPower(PowerType type, ResourceLocation location) {
		powerRepo.computeIfAbsent(location, k -> new CopyOnWriteArrayList<>()).add(type);
	}

	public void removePower(PowerType type, ResourceLocation location) {
		List<PowerType> powerList = powerRepo.get(location);
		if (powerList != null) {
			powerList.remove(type);
			if (powerList.isEmpty()) {
				powerRepo.remove(location);
			}
		}
	}

	public @Unmodifiable @NotNull List<PowerType> getAllPowers() {
		return Util.collapseCollection(powerRepo.values().stream().toList());
	}

	@Override
	public @NotNull Iterator<AtomicReference<Origin>> iterator() {
		return originRepo.values().iterator();
	}

	@Override
	public @NotNull String toString() {
		return "RepositoryComponent{" +
			"originRepo=" + originRepo +
			", powerRepo=" + powerRepo +
			'}';
	}

	public void clear() {
		this.originRepo.clear();
		this.powerRepo.clear();
	}
}
