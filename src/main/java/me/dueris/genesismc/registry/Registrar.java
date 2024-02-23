package me.dueris.genesismc.registry;

import com.google.common.base.Preconditions;
import me.dueris.genesismc.registry.exceptions.UnmodifiableRegistryException;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class Registrar<T extends Registerable> {
    private boolean frozen = false;
    public HashMap<NamespacedKey, T> rawRegistry = new HashMap();

    public void register(T item) {
        checkFrozen();
        try {
            registerOrThrow(item);
        } catch (Exception e) {
            // silent fail
        }
    }

    public void registerOrThrow(T item) {
        checkFrozen();
        Preconditions.checkArgument(item.getKey() != null, "Registerable key cannot be null");
        this.rawRegistry.put(item.getKey(), item);
    }

    public void replaceEntry(NamespacedKey currentKey, T newValue){
        if(this.containsRegisterable(newValue)) return;
        if(this.rawRegistry.containsKey(currentKey)){
            this.rawRegistry.remove(currentKey);
            this.rawRegistry.put(currentKey, newValue);
        }
    }

    public void checkFrozen(){
        if (frozen) throw new UnmodifiableRegistryException("Registry already frozen!");
    }

    public T get(NamespacedKey key){
        return this.rawRegistry.get(key);
    }

    public boolean containsRegisterable(T item){
        return this.rawRegistry.containsValue(item);
    }

    public void removeFromRegistry(NamespacedKey key){
        this.rawRegistry.remove(key);
    }

    public Optional<T> getOptional(NamespacedKey key){
        return this.rawRegistry.containsKey(key) ? Optional.of(this.get(key)) : Optional.empty();
    }

    public Collection<T> values(){
        return this.rawRegistry.values();
    }

    @SuppressWarnings("unchecked")
    public T[] getFromPredicate(Predicate<T> predicate){
        ArrayList<T> tL = new ArrayList<>();
        this.rawRegistry.values().forEach((regI) -> {
            if(predicate.test(regI)){
                tL.add(regI);
            }
        });

        T[] array = (T[]) new Object[tL.size()];
        for (int i = 0; i < tL.size(); i++) {
            array[i] = tL.get(i);
        }
        return array;
    }

    public void forEach(BiConsumer<? super NamespacedKey, ? super T> consumer){
        this.rawRegistry.forEach(consumer);
    }

    public void freeze(){
        frozen = true;
    }

    public void clearEntries() {
        this.rawRegistry.clear();
    }

    public boolean hasEntries() {
        return !this.rawRegistry.isEmpty();
    }

    public int registrySize(){
        return this.rawRegistry.size();
    }
}
