package io.github.dueris.originspaper.condition.type;

import io.github.dueris.calio.util.Validatable;
import io.github.dueris.originspaper.condition.AbstractCondition;
import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.util.context.TypeConditionContext;
import io.github.dueris.calio.data.SerializableData;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

public abstract class AbstractConditionType<T extends TypeConditionContext, C extends AbstractCondition<T, ?>> implements Predicate<T>, Validatable {

	private C condition = null;
	private boolean initialized = false;

	@ApiStatus.Internal
	public void init(@NotNull C condition) {

		if (condition.getConditionType() != this) {
			throw new IllegalArgumentException("Cannot initialize condition type \"" + getConfig().id() + "\" with mismatched condition!");
		}

		this.condition = condition;
		this.initialized = true;

	}

	@Override
	public abstract boolean test(T context);

	@SuppressWarnings("unchecked")
	@Override
	public void validate() throws Exception {

		TypedDataObjectFactory<AbstractConditionType<T, C>> dataFactory = (TypedDataObjectFactory<AbstractConditionType<T,C>>) getConfig().dataFactory();
		SerializableData.Instance data = dataFactory.toData(this);

		data.validate();

	}

	@NotNull
	public abstract ConditionConfiguration<?> getConfig();

	public final C getCondition() {

		if (initialized) {
			return Objects.requireNonNull(condition, "Condition of initialized condition type \"" + getConfig().id() + "\" was null!");
		}

		else {
			throw new IllegalStateException("Condition type \"" + getConfig().id() + "\" wasn't initialized yet!");
		}

	}

	public abstract C createCondition(boolean inverted);

	public C createCondition() {
		return createCondition(false);
	}

}
