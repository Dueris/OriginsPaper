package io.github.dueris.originspaper.action.type.bientity.meta;

import io.github.dueris.originspaper.action.ActionConfiguration;
import io.github.dueris.originspaper.action.BiEntityAction;
import io.github.dueris.originspaper.action.context.BiEntityActionContext;
import io.github.dueris.originspaper.action.type.BiEntityActionType;
import io.github.dueris.originspaper.action.type.BiEntityActionTypes;
import io.github.dueris.originspaper.action.type.meta.ChanceMetaActionType;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ChanceBiEntityActionType extends BiEntityActionType implements ChanceMetaActionType<BiEntityActionContext, BiEntityAction> {

	private final BiEntityAction successAction;
	private final Optional<BiEntityAction> failAction;

	private final float chance;

	public ChanceBiEntityActionType(BiEntityAction successAction, Optional<BiEntityAction> failAction, float chance) {
		this.successAction = successAction;
		this.failAction = failAction;
		this.chance = chance;
	}

	@Override
	protected void execute(Entity actor, Entity target) {
		executeAction(new BiEntityActionContext(actor, target));
	}

	@Override
	public @NotNull ActionConfiguration<?> getConfig() {
		return BiEntityActionTypes.CHANCE;
	}

	@Override
	public BiEntityAction successAction() {
		return successAction;
	}

	@Override
	public Optional<BiEntityAction> failAction() {
		return failAction;
	}

	@Override
	public float chance() {
		return chance;
	}

}
