package me.dueris.calio.registry;

import com.google.common.base.Preconditions;
import me.dueris.calio.registry.exceptions.UnmodifiableRegistryException;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class Registrar<T extends Registerable> {
	public HashMap<NamespacedKey, T> rawRegistry = new HashMap();
	private boolean frozen = false;

	/**
	 * Registers an item in the registry.
	 *
	 * @param  item  the item to register
	 * @return       void
	 */
	public void register(T item) {
		checkFrozen();
		try {
			registerOrThrow(item);
		} catch (Exception e) {
			// silent fail
		}
	}

	/**
	 * Register an object to the registry. Throw if null
	 *
	 * @param  item  the item to register
	 * @return       void
	 */
	public void registerOrThrow(T item) {
		checkFrozen();
		Preconditions.checkArgument(item.getKey() != null, "Registerable key cannot be null");
		this.rawRegistry.put(item.getKey(), item);
	}

	/**
	 * Replaces an entry in the registry with a new value.
	 *
	 * @param  currentKey  the namespaced key of the entry to be replaced
	 * @param  newValue    the new value to replace the entry with
	 */
	public void replaceEntry(NamespacedKey currentKey, T newValue) {
		if (this.containsRegisterable(newValue)) return;
		if (this.rawRegistry.containsKey(currentKey)) {
			this.rawRegistry.remove(currentKey);
			this.rawRegistry.put(currentKey, newValue);
		}
	}

	/**
	 * Check if the registry is frozen, and throw an exception if it is.
	 */
	public void checkFrozen() {
		if (frozen) throw new UnmodifiableRegistryException("Registry already frozen!");
	}

	/**
	 * Retrieves the value associated with the given key from the registry.
	 *
	 * @param  key  the key to retrieve the value for
	 * @return      the value associated with the given key, or null if the key is not found
	 */
	public T get(NamespacedKey key) {
		return this.rawRegistry.get(key);
	}

	/**
	 * Check if the registry contains a registerable item.
	 *
	 * @param  item  the item to be checked in the registry
	 * @return      true if the registry contains the item, false otherwise
	 */
	public boolean containsRegisterable(T item) {
		return this.rawRegistry.containsValue(item);
	}

	/**
	 * Removes the specified key from the registry.
	 *
	 * @param  key  the key to be removed from the registry
	 */
	public void removeFromRegistry(NamespacedKey key) {
		this.rawRegistry.remove(key);
	}

	/**
	 * Returns an Optional containing the value associated with the given key in the registry,
	 * or an empty Optional if the key is not present in the registry.
	 *
	 * @param  key  the key for which the value is to be retrieved
	 * @return      an Optional containing the value associated with the given key, or an empty Optional if the key is not present
	 */
	public Optional<T> getOptional(NamespacedKey key) {
		return this.rawRegistry.containsKey(key) ? Optional.of(this.get(key)) : Optional.empty();
	}

	/**
	 * Returns a collection of all the values in the raw registry.
	 *
	 * @return a collection of all the values in the raw registry
	 */
	public Collection<T> values() {
		return this.rawRegistry.values();
	}

	/**
	 * Retrieves an array of elements that satisfy a given predicate.
	 *
	 * @param  predicate  the predicate used to filter the elements
	 * @return            an array of elements that satisfy the predicate
	 */
	@SuppressWarnings("unchecked")
	public T[] getFromPredicate(Predicate<T> predicate) {
		ArrayList<T> tL = new ArrayList<>();
		this.rawRegistry.values().forEach((regI) -> {
			if (predicate.test(regI)) {
				tL.add(regI);
			}
		});

		T[] array = (T[]) new Object[tL.size()];
		for (int i = 0; i < tL.size(); i++) {
			array[i] = tL.get(i);
		}
		return array;
	}

	/**
	 * Executes the given consumer function on each entry of the registry.
	 *
	 * @param  consumer  the function to be executed on each entry,
	 *                   which takes a namespaced key and the corresponding value as input
	 * @return           void
	 */
	public void forEach(BiConsumer<? super NamespacedKey, ? super T> consumer) {
		this.rawRegistry.forEach(consumer);
	}

	/**
	 * Sets the 'frozen' flag to true, freezing the registry.
	 */
	public void freeze() {
		frozen = true;
	}

	/**
	 * Clears all entries in the registry.
	 */
	public void clearEntries() {
		this.rawRegistry.clear();
	}

	/**
	 * A method to check if there are entries in the rawRegistry.
	 *
	 * @return         true if the rawRegistry is not empty, false otherwise
	 */
	public boolean hasEntries() {
		return !this.rawRegistry.isEmpty();
	}

	/**
	 * Returns the size of the registry.
	 *
	 * @return the size of the registry
	 */
	public int registrySize() {
		return this.rawRegistry.size();
	}
}
