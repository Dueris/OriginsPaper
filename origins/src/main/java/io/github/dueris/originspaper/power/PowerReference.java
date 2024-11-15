package io.github.dueris.originspaper.power;

import com.mojang.serialization.DataResult;
import io.github.dueris.calio.util.Validatable;
import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.util.PowerUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public record PowerReference(ResourceLocation id,
							 Function<PowerType, DataResult<PowerType>> condition) implements Validatable {

	public static PowerReference of(ResourceLocation id) {
		return new PowerReference(id, DataResult::success);
	}

	public static PowerReference resource(ResourceLocation id) {
		return new PowerReference(id, PowerUtil::validateResource);
	}

	@Override
	public void validate() throws Exception {
		getResultReference()
			.map(Power::getPowerType)
			.flatMap(condition())
			.getOrThrow();
	}

	@Nullable
	public PowerType getPowerTypeFrom(Entity entity) {
		return getOptionalReference()
			.flatMap(power -> Optional.ofNullable(power.getPowerTypeFrom(entity)))
			.orElse(null);
	}

	public DataResult<Power> getResultReference() {
		return PowerManager.getResult(id());
	}

	public Optional<Power> getOptionalReference() {
		return PowerManager.getOptional(id());
	}

	public Power getStrictReference() {
		return PowerManager.get(id());
	}

	@Nullable
	public Power getReference() {
		return PowerManager.getNullable(id());
	}

	public boolean isActive(Entity entity) {
		return getOptionalReference()
			.map(power -> power.isActive(entity))
			.orElse(false);
	}

}
