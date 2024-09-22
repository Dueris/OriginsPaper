package io.github.dueris.calio.util.holder;

public record ObjectTiedEnumState<T>(T object, Enum<?> state) {
}
