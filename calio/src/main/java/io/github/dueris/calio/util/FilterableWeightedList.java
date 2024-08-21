package io.github.dueris.calio.util;

import net.minecraft.world.entity.ai.behavior.ShufflingList;

import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FilterableWeightedList<U> extends ShufflingList<U> {

	private Predicate<U> filter;

	public int size() {
		return entries.size();
	}

	public void addFilter(Predicate<U> filter) {
		if (hasFilter()) {
			this.filter = this.filter.and(filter);
		} else {
			setFilter(filter);
		}
	}

	public void setFilter(Predicate<U> filter) {
		this.filter = filter;
	}

	public void removeFilter() {
		this.filter = null;
	}

	public boolean hasFilter() {
		return this.filter != null;
	}

	public Stream<U> stream() {
		if (filter != null) {
			return this.entries.stream().map(ShufflingList.WeightedEntry::getData).filter(filter);
		}
		return super.stream();
	}

	public Stream<WeightedEntry<U>> entryStream() {
		return this.entries.stream().filter(entry -> filter == null || filter.test(entry.getData()));
	}

	public void addAll(FilterableWeightedList<U> other) {
		other.entryStream().forEach(entry -> add(entry.getData(), entry.getWeight()));
	}

	public U pickRandom(Random random) {
		return pickRandom();
	}

	public U pickRandom() {
		return this.shuffle().stream().findFirst().orElseThrow(RuntimeException::new);
	}

	public FilterableWeightedList<U> copy() {
		FilterableWeightedList<U> copied = new FilterableWeightedList<>();
		copied.addAll(this);
		return copied;
	}
}
