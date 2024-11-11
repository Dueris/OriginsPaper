package io.github.dueris.originspaper.condition;

import io.github.dueris.calio.util.Validatable;
import io.github.dueris.originspaper.condition.type.AbstractConditionType;
import io.github.dueris.originspaper.util.context.TypeConditionContext;

import java.util.function.Predicate;

public abstract class AbstractCondition<T extends TypeConditionContext, CT extends AbstractConditionType<T, ?>> implements Predicate<T>, Validatable {

	private final CT conditionType;
	private final boolean inverted;

	public AbstractCondition(CT conditionType, boolean inverted) {

		this.conditionType = conditionType;
		this.inverted = inverted;

		//noinspection unchecked
		((AbstractConditionType<T, AbstractCondition<T, CT>>) this.conditionType).init(this);

	}

	@Override
	public boolean test(T context) {
		return isInverted() != getConditionType().test(context);
	}

	@Override
	public void validate() throws Exception {
		getConditionType().validate();
	}

	public final CT getConditionType() {
		return conditionType;
	}

	public final boolean isInverted() {
		return inverted;
	}

}
