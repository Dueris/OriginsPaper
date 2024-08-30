package io.github.dueris.originspaper.storage;

import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.util.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class RepositoryComponent implements Iterable<Tuple<List<PowerType>, AtomicReference<Origin>>> {
	protected final ConcurrentHashMap<OriginLayer, Tuple<List<PowerType>, AtomicReference<Origin>>> repo = new ConcurrentHashMap<>();

	public int size() {
		return repo.size();
	}

	public boolean isEmpty() {
		return repo.isEmpty();
	}

	public @NotNull Set<OriginLayer> getLayers() {
		return repo.keySet();
	}

	public @Unmodifiable @NotNull List<PowerType> getAllPowers() {
		return Util.collapseList(repo.values().stream().map(Tuple::getA).toList());
	}

	public @Unmodifiable @NotNull List<Origin> getAllOrigins() {
		return repo.values().stream().map(Tuple::getB).map(AtomicReference::get).toList();
	}

	@Override
	public @NotNull Iterator<Tuple<List<PowerType>, AtomicReference<Origin>>> iterator() {
		return repo.values().iterator();
	}

	public void put(OriginLayer layer) {
		repo.put(layer, new Tuple<>(new CopyOnWriteArrayList<>(), new AtomicReference<>(Origin.EMPTY)));
	}

	public void addPower(PowerType type, OriginLayer layer) {
		if (!repo.containsKey(layer)) {
			put(layer);
		}

		repo.get(layer).getA().add(type);
	}

	public void removePower(PowerType type, OriginLayer layer) {
		if (!repo.containsKey(layer)) {
			put(layer);
		}

		repo.get(layer).getA().remove(type);
	}

	public Origin getOrigin(OriginLayer layer) {
		if (!repo.containsKey(layer)) {
			put(layer);
		}

		return repo.get(layer).getB().get();
	}

	@NotNull
	public Origin getOriginOrDefault(@NotNull Origin def, OriginLayer layer) {
		if (!repo.containsKey(layer)) {
			put(layer);
		}

		Origin retrieved = repo.get(layer).getB().get();

		return retrieved == null ? def : retrieved;
	}

	public void setOrigin(Origin origin, OriginLayer layer) {
		if (!repo.containsKey(layer)) {
			put(layer);
		}

		repo.get(layer).getB().set(origin);
	}

	@Override
	public @NotNull String toString() {
		return "RepositoryComponent{" +
			"repo=" + repo +
			'}';
	}
}
