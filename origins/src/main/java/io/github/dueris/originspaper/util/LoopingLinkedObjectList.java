package io.github.dueris.originspaper.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class LoopingLinkedObjectList<T> extends LinkedList<T> {
	private int currentIndex = 0;

	public T getCurrent() {
		if (isEmpty()) {
			throw new NoSuchElementException("The list is empty.");
		}
		return getByIndex(currentIndex);
	}

	public T next() {
		if (isEmpty()) {
			throw new NoSuchElementException("The list is empty.");
		}
		currentIndex = (currentIndex + 1) % size();
		return getByIndex(currentIndex);
	}

	public T previous() {
		if (isEmpty()) {
			throw new NoSuchElementException("The list is empty.");
		}
		currentIndex = (currentIndex - 1 + size()) % size();
		return getByIndex(currentIndex);
	}

	public T moveTo(int index) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException("Index out of range: " + index);
		}
		currentIndex = index;
		return getByIndex(currentIndex);
	}

	private T getByIndex(int index) {
		Iterator<T> iterator = iterator();
		for (int i = 0; i < index; i++) {
			iterator.next();
		}
		return iterator.next();
	}

	@Override
	public boolean add(T element) {
		boolean added = super.add(element);
		if (added && currentIndex >= size()) {
			currentIndex = size() - 1;
		}
		return added;
	}

	@Override
	public boolean remove(Object element) {
		boolean removed = super.remove(element);
		if (removed && currentIndex >= size()) {
			currentIndex = Math.max(0, size() - 1);
		}
		return removed;
	}
}
