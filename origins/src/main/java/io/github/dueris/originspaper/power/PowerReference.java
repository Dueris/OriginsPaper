package io.github.dueris.originspaper.power;

import io.github.dueris.originspaper.power.type.PowerType;
import io.github.dueris.originspaper.power.type.PowerTypes;
import io.github.dueris.originspaper.util.PowerUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PowerReference extends Power {

	public PowerReference(ResourceLocation id) {
		super(null, DATA.instance()
			.set("id", id)
			.set(TYPE_KEY, PowerTypes.SIMPLE)
			.set("name", Component.empty())
			.set("description", Component.empty())
			.set("hidden", true));
	}

	public static PowerReference resource(ResourceLocation id) {
		return new PowerReference(id) {
			@Override
			public void validate() throws Exception {
				PowerUtil.validateResource(create(null)).getOrThrow();
			}
		};
	}

	public static PowerReference of(String namespace, String path) {
		return new PowerReference(ResourceLocation.fromNamespaceAndPath(namespace, path));
	}

	public static PowerReference of(String str) {
		return new PowerReference(ResourceLocation.parse(str));
	}

	public static PowerReference of(ResourceLocation identifier) {
		return new PowerReference(identifier);
	}

	@Override
	public PowerTypeFactory<? extends PowerType>.Instance getFactoryInstance() {
		return this.getStrictReference().getFactoryInstance();
	}

	@Nullable
	@Override
	public PowerType getType(Entity entity) {
		Power power = this.getReference();
		return power != null
			? power.getType(entity)
			: null;
	}

	@Nullable
	public Power getReference() {
		return getOptionalReference().orElse(null);
	}

	public Optional<Power> getOptionalReference() {
		return PowerManager.getOptional(this.getId());
	}

	public Power getStrictReference() {
		return PowerManager.get(this.getId());
	}

	@Override
	public void validate() throws Exception {
		getStrictReference();
	}

}
