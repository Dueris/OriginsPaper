package io.github.dueris.originspaper.action;

import io.github.dueris.calio.util.Validatable;
import io.github.dueris.originspaper.action.type.AbstractActionType;
import io.github.dueris.originspaper.util.context.TypeActionContext;

import java.util.function.Consumer;

public abstract class AbstractAction<T extends TypeActionContext<?>, AT extends AbstractActionType<T, ?>> implements Consumer<T>, Validatable {

	private final AT actionType;

	public AbstractAction(AT actionType) {
		this.actionType = actionType;
		//noinspection unchecked
		((AbstractActionType<T, AbstractAction<T, AT>>) this.actionType).init(this);
	}

	@Override
	public void accept(T context) {
		getActionType().accept(context);
	}

	@Override
	public void validate() throws Exception {
		getActionType().validate();
	}

	public final AT getActionType() {
		return actionType;
	}

}
